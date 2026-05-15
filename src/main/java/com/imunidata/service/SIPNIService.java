package com.imunidata.service;

import com.imunidata.dto.CoberturavacinadalDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.client.RestClientException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class SIPNIService {

    private final RestTemplate restTemplate;
    
    // URLs da API SI-PNI/Datasus
    private static final String SIPNI_BASE_URL = "https://sipni.datasus.gov.br/";
    private static final String SIPNI_API_URL = "http://sipni.datasus.gov.br/api/";

    public SIPNIService() {
        this.restTemplate = new RestTemplate();
    }

    /**
     * Buscar dados de cobertura vacinal por estado na API SI-PNI
     * Com cache para reduzir requisições
     */
    @Cacheable("coberturaPorEstado")
    public List<CoberturavacinadalDTO> buscarCoberturaPorEstado(String estado) {
        log.info("Buscando cobertura vacinal para estado: {}", estado);
        return obterDadosMockSIPNI(estado);
    }

    /**
     * Buscar dados de cobertura vacinal por município
     * Com cache para reduzir requisições
     */
    @Cacheable("coberturaPorMunicipio")
    public List<CoberturavacinadalDTO> buscarCoberturaPorMunicipio(String municipio) {
        log.info("Buscando cobertura vacinal para município: {}", municipio);
        return obterDadosMockSIPNI(municipio);
    }

    /**
     * Buscar dados de cobertura vacinal por vacina
     */
    @Cacheable("coberturaPorVacina")
    public List<CoberturavacinadalDTO> buscarCoberturaPorVacina(String vacina) {
        log.info("Buscando cobertura vacinal para vacina: {}", vacina);
        return obterDadosMockSIPNI(vacina);
    }

    /**
     * Buscar cobertura em um período específico
     */
    @Cacheable("coberturaPeriodo")
    public List<CoberturavacinadalDTO> buscarCoberturaPeriodo(String ano, String mes, String estado) {
        log.info("Buscando cobertura vacinal - Ano: {}, Mês: {}, Estado: {}", ano, mes, estado);
        return obterDadosMockSIPNI(estado);
    }

    /**
     * Obter dados simulados da SI-PNI
     * Simula a estrutura real da API Datasus
     */
    private List<CoberturavacinadalDTO> obterDadosMockSIPNI(String filtro) {
        List<CoberturavacinadalDTO> dados = new ArrayList<>();

        // Dados simulados da API SI-PNI
        CoberturavacinadalDTO[] dadosSIPNI = {
            CoberturavacinadalDTO.builder()
                    .codigoMunicipio("3550308")
                    .nomeMunicipio("São Paulo")
                    .estado("SP")
                    .vacina("BCG")
                    .cobertura(98.5)
                    .quantidadeAplicada(15000)
                    .populacaoAlvo(15228)
                    .ano("2024")
                    .mes("05")
                    .build(),
            CoberturavacinadalDTO.builder()
                    .codigoMunicipio("3550308")
                    .nomeMunicipio("São Paulo")
                    .estado("SP")
                    .vacina("Poliomielite")
                    .cobertura(97.2)
                    .quantidadeAplicada(14800)
                    .populacaoAlvo(15228)
                    .ano("2024")
                    .mes("05")
                    .build(),
            CoberturavacinadalDTO.builder()
                    .codigoMunicipio("3550308")
                    .nomeMunicipio("São Paulo")
                    .estado("SP")
                    .vacina("Gripe")
                    .cobertura(95.8)
                    .quantidadeAplicada(14620)
                    .populacaoAlvo(15228)
                    .ano("2024")
                    .mes("05")
                    .build(),
            CoberturavacinadalDTO.builder()
                    .codigoMunicipio("3304557")
                    .nomeMunicipio("Rio de Janeiro")
                    .estado("RJ")
                    .vacina("BCG")
                    .cobertura(96.3)
                    .quantidadeAplicada(12500)
                    .populacaoAlvo(12980)
                    .ano("2024")
                    .mes("05")
                    .build(),
            CoberturavacinadalDTO.builder()
                    .codigoMunicipio("3304557")
                    .nomeMunicipio("Rio de Janeiro")
                    .estado("RJ")
                    .vacina("Poliomielite")
                    .cobertura(94.1)
                    .quantidadeAplicada(12220)
                    .populacaoAlvo(12980)
                    .ano("2024")
                    .mes("05")
                    .build(),
            CoberturavacinadalDTO.builder()
                    .codigoMunicipio("3304557")
                    .nomeMunicipio("Rio de Janeiro")
                    .estado("RJ")
                    .vacina("Sarampo")
                    .cobertura(92.5)
                    .quantidadeAplicada(12005)
                    .populacaoAlvo(12980)
                    .ano("2024")
                    .mes("05")
                    .build(),
            CoberturavacinadalDTO.builder()
                    .codigoMunicipio("3106200")
                    .nomeMunicipio("Belo Horizonte")
                    .estado("MG")
                    .vacina("BCG")
                    .cobertura(99.1)
                    .quantidadeAplicada(11400)
                    .populacaoAlvo(11506)
                    .ano("2024")
                    .mes("05")
                    .build(),
            CoberturavacinadalDTO.builder()
                    .codigoMunicipio("3106200")
                    .nomeMunicipio("Belo Horizonte")
                    .estado("MG")
                    .vacina("Difteria")
                    .cobertura(98.0)
                    .quantidadeAplicada(11281)
                    .populacaoAlvo(11506)
                    .ano("2024")
                    .mes("05")
                    .build(),
            CoberturavacinadalDTO.builder()
                    .codigoMunicipio("5300108")
                    .nomeMunicipio("Brasília")
                    .estado("DF")
                    .vacina("BCG")
                    .cobertura(97.8)
                    .quantidadeAplicada(9800)
                    .populacaoAlvo(10020)
                    .ano("2024")
                    .mes("05")
                    .build(),
            CoberturavacinadalDTO.builder()
                    .codigoMunicipio("5300108")
                    .nomeMunicipio("Brasília")
                    .estado("DF")
                    .vacina("HPV")
                    .cobertura(91.3)
                    .quantidadeAplicada(9148)
                    .populacaoAlvo(10020)
                    .ano("2024")
                    .mes("05")
                    .build()
        };

        // Filtrar dados baseado no filtro fornecido
        return Arrays.stream(dadosSIPNI)
                .filter(d -> d.getEstado().equalsIgnoreCase(filtro) ||
                             d.getNomeMunicipio().equalsIgnoreCase(filtro) ||
                             d.getVacina().equalsIgnoreCase(filtro) ||
                             d.getCodigoMunicipio().equals(filtro))
                .collect(Collectors.toList());
    }

    /**
     * Obter resumo de cobertura por estado
     */
    public List<CoberturavacinadalDTO> obterResumoPorEstado() {
        log.info("Obtendo resumo de cobertura por estado");
        String[] estados = {"SP", "RJ", "MG", "DF"};
        List<CoberturavacinadalDTO> resultado = new ArrayList<>();
        
        for (String estado : estados) {
            resultado.addAll(buscarCoberturaPorEstado(estado));
        }
        
        return resultado;
    }

    /**
     * Obter estatísticas de cobertura vacinal
     */
    public String obterEstatisticas() {
        log.info("Calculando estatísticas de cobertura");
        List<CoberturavacinadalDTO> todosOsDados = obterResumoPorEstado();
        
        if (todosOsDados.isEmpty()) {
            return "Nenhum dado disponível";
        }

        double coberturaMedia = todosOsDados.stream()
                .mapToDouble(CoberturavacinadalDTO::getCobertura)
                .average()
                .orElse(0.0);

        double coberturaMinima = todosOsDados.stream()
                .mapToDouble(CoberturavacinadalDTO::getCobertura)
                .min()
                .orElse(0.0);

        double coberturaMaxima = todosOsDados.stream()
                .mapToDouble(CoberturavacinadalDTO::getCobertura)
                .max()
                .orElse(0.0);

        return String.format(
                "Estatísticas SI-PNI - Cobertura Média: %.2f%%, Mínima: %.2f%%, Máxima: %.2f%%",
                coberturaMedia, coberturaMinima, coberturaMaxima
        );
    }
}
