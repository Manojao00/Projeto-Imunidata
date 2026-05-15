package com.imunidata.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Table(name = "registro_vacinacao")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RegistroVacinacao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String municipio;

    @Column(nullable = false)
    private String estado;

    @Column(nullable = false)
    private String vacina;

    @Column(nullable = false)
    private String dose;

    @Column(nullable = false)
    private Integer quantidadeAplicada;

    @Column(nullable = false)
    private LocalDate dataRegistro;

    @Column
    private String faixaEtaria;

    @Override
    public String toString() {
        return "RegistroVacinacao{" +
                "id=" + id +
                ", municipio='" + municipio + '\'' +
                ", estado='" + estado + '\'' +
                ", vacina='" + vacina + '\'' +
                ", dose='" + dose + '\'' +
                ", quantidadeAplicada=" + quantidadeAplicada +
                ", dataRegistro=" + dataRegistro +
                ", faixaEtaria='" + faixaEtaria + '\'' +
                '}';
    }
}
