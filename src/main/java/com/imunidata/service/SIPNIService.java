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

@Service
@Slf4j
public class SIPNIService {

    // New stub methods required by the controller

    private final RestTemplate restTemplate;
    private final ObjectMapper mapper;

    // API pública de doses aplicadas PNI 2026
    private static final String API_BASE_URL = "https://apidadosabertos.saude.gov.br/vacinacao/doses-aplicadas-pni-2026";

    public SIPNIService() {
        this.restTemplate = new RestTemplate();
        this.mapper = new ObjectMapper();
    }

    @Cacheable("coberturaPorEstado")
    public List<CoberturavacinadalDTO> buscarCoberturaPorEstado(String estado) {
        log.info("Buscando cobertura vacinal para estado: {}", estado);
        return obterDadosDaApiEMapper(estado);
    }

    @Cacheable("coberturaPorMunicipio")
    public List<CoberturavacinadalDTO> buscarCoberturaPorMunicipio(String municipio) {
        log.info("Buscando cobertura vacinal para município: {}", municipio);
        return obterDadosDaApiEMapper(municipio);
    }

    @Cacheable("coberturaPorVacina")
    public List<CoberturavacinadalDTO> buscarCoberturaPorVacina(String vacina) {
        log.info("Buscando cobertura vacinal para vacina: {}", vacina);
        return obterDadosDaApiEMapper(vacina);
    }

    // Existing method
    private List<CoberturavacinadalDTO> obterDadosDaApiEMapper(String filtro) {
        // existing implementation
        // Monta a URL; a API aceita param ?limit=1000 para cantidad de resultados
        String url = API_BASE_URL;
        if (filtro != null && !filtro.trim().isEmpty()) {
            url += "?limit=1000";
        }

        try {
            String response = restTemplate.getForObject(url, String.class);
            JsonNode root = mapper.readTree(response);
            List<CoberturavacinadalDTO> result = new ArrayList<>();

            if (root.isArray()) {
                for (JsonNode item : root) {
                    // Extrair os campos necessários
                    String codigoMunicipio = item.get("nome_uf_paciente").asText();
                    String nomeMunicipio = item.get("nome_municipio_paciente").asText();
                    String estado = item.get("sigla_vacina").asText(); // simplificado
                    String vacina = item.get("descricao_vacina").asText();
                    Double cobertura = item.get("cobertura") != null ? item.get("cobertura").asDouble() : 0.0;
                    Integer quantidadeAplicada = item.get("quantidadeAplicada") != null ? item.get("quantidadeAplicada").asInt() : 0;
                    Integer populacaoAlvo = item.get("populacaoAlvo") != null ? item.get("populacaoAlvo").asInt() : 0;
                    String dataVacina = item.get("data_vacina").asText();
                    String ano = dataVacina.length() >= 4 ? dataVacina.substring(0, 4) : "";
                    String mes = dataVacina.length() >= 7 ? dataVacina.substring(5, 7) : "";

                    CoberturavacinadalDTO dto = CoberturavacinadalDTO.builder()
                            .codigoMunicipio(codigoMunicipio)
                            .nomeMunicipio(nomeMunicipio)
                            .estado(estado)
                            .vacina(vacina)
                            .cobertura(cobertura)
                            .quantidadeAplicada(quantidadeAplicada)
                            .populacaoAlvo(populacaoAlvo)
                            .ano(ano)
                            .mes(mes)
                            .build();

                    result.add(dto);
                }
            }
            return result;
        } catch (RestClientException | IOException e) {
            log.error("Erro ao consultar a API de doses aplicadas: {}", e.getMessage());
            return new ArrayList<>();
        }
    }

    // Stub for period coverage
    public List<CoberturavacinadalDTO> buscarCoberturaPeriodo(String uf, String municipio, String vacina) {
        log.info("Buscar cobertura por periodo: uf={}, municipio={}, vacina={}", uf, municipio, vacina);
        // Simple filter on existing data (could be expanded)
        List<CoberturavacinadalDTO> all = obterDadosDaApiEMapper(null);
        // No real filtering implemented yet
        return all;
    }

    // Stub for state summary
    public List<CoberturavacinadalDTO> obterResumoPorEstado() {
        log.info("Obtendo resumo por estado");
        // For now, return empty list or reuse existing data
        return new ArrayList<>();
    }

    // Stub for overall statistics
    public String obterEstatisticas() {
        log.info("Obtendo estatísticas gerais");
        // Simple placeholder JSON string
        return "{}";
    }
}
