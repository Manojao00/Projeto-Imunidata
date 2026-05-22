package com.imunidata.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.imunidata.dto.CoberturavacinadalDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Serviço de integração com a API pública de vacinação do Datasus (SI-PNI
 * 2026).
 *
 * ESTRUTURA REAL DA RESPOSTA (verificada):
 * { "doses_aplicadas_pni": [ { ...campos... }, ... ] }
 *
 * CAMPOS REAIS RELEVANTES:
 * sigla_uf_paciente → ex: "SP"
 * nome_uf_paciente → ex: "SAO PAULO"
 * nome_municipio_paciente → ex: "SAO PAULO"
 * numero_idade_paciente → ex: "74"
 * sigla_vacina → ex: "INF3"
 * descricao_vacina → ex: "Vacina influenza trivalente"
 * descricao_tipo_estabelecimento
 * tipo_sexo_paciente → ex: "F"
 * data_vacina → ex: "2026-04-16 00:00:00-03"
 * descricao_dose_vacina → ex: "Única"
 */
@Service
@Slf4j
public class SIPNIService {

    private final RestTemplate restTemplate;
    private final ObjectMapper mapper;

    private static final String API_BASE_URL = "https://apidadosabertos.saude.gov.br/vacinacao/doses-aplicadas-pni-2026";
    private static final int LIMITE_POR_MES = 100;

    // chave real do JSON que envolve o array
    private static final String WRAPPER_KEY = "doses_aplicadas_pni";

    public SIPNIService() {
        this.restTemplate = new RestTemplate();
        this.mapper = new ObjectMapper();
    }

    // ---------------------------------------------------------------
    // Métodos públicos
    // ---------------------------------------------------------------

    @Cacheable("coberturaPorEstado")
    public List<CoberturavacinadalDTO> buscarCoberturaPorEstado(String estado) {
        log.info("Buscando cobertura por estado: {}", estado);
        List<CoberturavacinadalDTO> todos = obterDadosDaApi();
        if (estado == null || estado.isBlank())
            return todos;
        String uf = estado.trim().toUpperCase();
        return todos.stream()
                .filter(d -> uf.equals(d.getSigla_uf_paciente())
                        || uf.equalsIgnoreCase(d.getNome_uf_paciente()))
                .collect(Collectors.toList());
    }

    @Cacheable("coberturaPorMunicipio")
    public List<CoberturavacinadalDTO> buscarCoberturaPorMunicipio(String municipio) {
        log.info("Buscando cobertura por município: {}", municipio);
        List<CoberturavacinadalDTO> todos = obterDadosDaApi();
        if (municipio == null || municipio.isBlank())
            return todos;
        return todos.stream()
                .filter(d -> municipio.equalsIgnoreCase(d.getNome_municipio_paciente()))
                .collect(Collectors.toList());
    }

    @Cacheable("coberturaPorVacina")
    public List<CoberturavacinadalDTO> buscarCoberturaPorVacina(String vacina) {
        log.info("Buscando cobertura por vacina: {}", vacina);
        List<CoberturavacinadalDTO> todos = obterDadosDaApi();
        if (vacina == null || vacina.isBlank())
            return todos;
        return todos.stream()
                .filter(d -> (d.getDescricao_vacina() != null &&
                        d.getDescricao_vacina().toLowerCase().contains(vacina.toLowerCase()))
                        || vacina.equalsIgnoreCase(d.getSigla_vacina()))
                .collect(Collectors.toList());
    }

    public List<CoberturavacinadalDTO> buscarCoberturaPeriodo(String ano, String mes, String estado) {
        log.info("Buscando cobertura período: ano={}, mes={}, estado={}", ano, mes, estado);
        List<CoberturavacinadalDTO> todos = obterDadosDaApi();
        return todos.stream()
                .filter(d -> (ano == null || ano.isBlank() || ano.equals(d.getAno()))
                        && (mes == null || mes.isBlank() || mes.equals(d.getMes()))
                        && (estado == null || estado.isBlank()
                                || estado.equalsIgnoreCase(d.getSigla_uf_paciente())
                                || estado.equalsIgnoreCase(d.getNome_uf_paciente())))
                .collect(Collectors.toList());
    }

    public List<CoberturavacinadalDTO> obterResumoPorEstado() {
        return obterDadosDaApi();
    }

    public String obterEstatisticas() {
        List<CoberturavacinadalDTO> dados = obterDadosDaApi();
        long total = dados.size();
        long estados = dados.stream()
                .map(CoberturavacinadalDTO::getSigla_uf_paciente)
                .filter(e -> e != null && !e.isBlank())
                .distinct().count();
        long vacinas = dados.stream()
                .map(CoberturavacinadalDTO::getDescricao_vacina)
                .filter(v -> v != null && !v.isBlank())
                .distinct().count();
        return String.format(
                "{\"totalRegistros\":%d,\"totalEstados\":%d,\"totalVacinas\":%d}",
                total, estados, vacinas);
    }

    // ---------------------------------------------------------------
    // Busca e mapeamento da API Datasus
    // ---------------------------------------------------------------
    @Cacheable("dadosApi")
    private List<CoberturavacinadalDTO> obterDadosDaApi() {

        List<CoberturavacinadalDTO> todosOsDados = new ArrayList<>();

        // Janeiro até Maio
        String[] meses = { "01", "02", "03", "04", "05" };

        for (String mes : meses) {

            String dataInicial = "2026-" + mes + "-01";
            String dataFinal;

            switch (mes) {
                case "02":
                    dataFinal = "2026-02-28";
                    break;

                case "04":
                case "06":
                case "09":
                case "11":
                    dataFinal = "2026-" + mes + "-30";
                    break;

                default:
                    dataFinal = "2026-" + mes + "-31";
            }

            int offset = Integer.parseInt(mes) * 100;

            String url = API_BASE_URL
                    + "?limit=" + LIMITE_POR_MES
                    + "&offset=" + offset;

            log.info("Buscando mês {} -> {}", mes, url);

            try {

                String response = restTemplate.getForObject(url, String.class);

                if (response == null || response.isBlank()) {
                    continue;
                }

                JsonNode root = mapper.readTree(response);

                JsonNode items = root.path(WRAPPER_KEY);

                if (!items.isArray()) {
                    log.warn("Nenhum array encontrado para o mês {}", mes);
                    continue;
                }

                for (JsonNode item : items) {

                    try {

                        String dataVacina = texto(item, "data_vacina");

                        String ano = (dataVacina != null && dataVacina.length() >= 4)
                                ? dataVacina.substring(0, 4)
                                : "";

                        String mesRegistro = (dataVacina != null && dataVacina.length() >= 7)
                                ? dataVacina.substring(5, 7)
                                : "";

                        String siglaUf = texto(item, "sigla_uf_paciente");
                        String nomeUf = texto(item, "nome_uf_paciente");
                        String municipio = texto(item, "nome_municipio_paciente");
                        String siglaVac = texto(item, "sigla_vacina");
                        String descVac = texto(item, "descricao_vacina");
                        String idade = texto(item, "numero_idade_paciente");

                        CoberturavacinadalDTO dto = CoberturavacinadalDTO.builder()

                                .sigla_uf_paciente(siglaUf)
                                .nome_uf_paciente(nomeUf)
                                .nome_municipio_paciente(municipio)
                                .numero_idade_paciente(idade)
                                .sigla_vacina(siglaVac)
                                .descricao_vacina(descVac)
                                .descricao_tipo_estabelecimento(
                                        texto(item, "descricao_tipo_estabelecimento"))
                                .tipo_sexo_paciente(texto(item, "tipo_sexo_paciente"))
                                .descricao_dose_vacina(texto(item, "descricao_dose_vacina"))
                                .data_vacina(dataVacina)

                                // Frontend
                                .estado(siglaUf)
                                .municipio(municipio)
                                .vacina(descVac)
                                .faixaEtaria(idade)
                                .ano(ano)
                                .mes(mesRegistro)
                                .build();

                        todosOsDados.add(dto);

                    } catch (Exception e) {
                        log.warn("Erro ao processar item: {}", e.getMessage());
                    }
                }

            } catch (RestClientException e) {
                log.error("Erro ao chamar API para mês {}: {}", mes, e.getMessage());
            } catch (IOException e) {
                log.error("Erro ao parsear JSON para mês {}: {}", mes, e.getMessage());
            }
        }

        return todosOsDados;
    }

    private String texto(JsonNode node, String campo) {
        JsonNode f = node.path(campo);
        if (f.isMissingNode() || f.isNull())
            return null;
        String v = f.asText().trim();
        return v.isEmpty() ? null : v;
    }
}
