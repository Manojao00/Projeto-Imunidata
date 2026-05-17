package com.imunidata.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Entidade que representa o registro bruto importado de um arquivo CSV.
 * Mantém apenas os campos de interesse mais a linha completa para auditoria.
 */
@Entity
@Table(name = "REGISTRO_VACINA_CSV")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegistroVacinaCsv {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Campos principais do CSV
    private String nomeUfPaciente;
    private String nomeMunicipioPaciente;
    private Integer numeroIdadePaciente;
    private String siglaVacina;
    private String descricaoTipoEstabelecimento;
    private String descricaoVacina;
    private String tipoSexoPaciente;
    private String dataVacina;

    // Guarda a linha completa (opcional, para rastreamento)
    @Column(length = 4000)
    private String linhaCompleta;
}
