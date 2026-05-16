package com.imunidata.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CoberturavacinadalDTO {
    private String codigoMunicipio;
    private String nomeUfPaciente;
    private String nomeMunicipioPaciente;
    private String numeroIdadePaciente;
    private String siglaVacina;
    private String descricaoTipoEstabelecimento;
    private String descricaoVacina;
    private String tipoSexoPaciente;
    private String dataVacina;
    private String nomeMunicipio;
    private String estado;
    private String vacina;
    private Double cobertura;
    private Integer quantidadeAplicada;
    private Integer populacaoAlvo;
    private String ano;
    private String mes;
}
