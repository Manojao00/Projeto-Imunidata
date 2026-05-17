package com.imunidata.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO mapeado para a API real do Datasus SI-PNI 2026.
 *
 * Estrutura da resposta: { "doses_aplicadas_pni": [ { ...campos abaixo... } ] }
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class CoberturavacinadalDTO {

    // --- Campos REAIS da API Datasus ---
    private String sigla_uf_paciente;              // "SP"
    private String nome_uf_paciente;               // "SAO PAULO"
    private String nome_municipio_paciente;        // "SAO PAULO"
    private String numero_idade_paciente;          // "74"
    private String sigla_vacina;                   // "INF3"
    private String descricao_vacina;               // "Vacina influenza trivalente"
    private String descricao_tipo_estabelecimento; // "CENTRO DE SAUDE/UNIDADE BASICA"
    private String tipo_sexo_paciente;             // "F" ou "M"
    private String data_vacina;                    // "2026-04-16 00:00:00-03"
    private String descricao_dose_vacina;          // "Única", "1ª Dose" etc.
    private String descricao_estrategia_vacinacao; // "Rotina", "Especial" etc.

    // --- Campos derivados / aliases para facilitar uso no frontend ---
    private String estado;       // = sigla_uf_paciente  ex: "SP"
    private String municipio;    // = nome_municipio_paciente
    private String vacina;       // = descricao_vacina
    private String faixaEtaria;  // = numero_idade_paciente
    private String ano;          // extraído de data_vacina: "2026"
    private String mes;          // extraído de data_vacina: "04"

    // Campos de cobertura (não vêm da API, mantidos para compatibilidade)
    private Double cobertura;
    private Integer quantidadeAplicada;
    private Integer populacaoAlvo;
}
