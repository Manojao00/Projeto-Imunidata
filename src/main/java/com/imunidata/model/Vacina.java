package com.imunidata.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "vacina")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Vacina {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String nome;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String descricao;

    @Column(nullable = false)
    private Integer dosesRecomendadas;

    @Column(nullable = false)
    private String intervaloEntreD;

    @Column(nullable = false)
    private String faixaEtariaRecomendada;

    @Column(nullable = false)
    private Double efetividade;

    @Column(columnDefinition = "TEXT")
    private String possiveisEfeitosColaterais;

    @Column
    private String fabricante;

    @Override
    public String toString() {
        return "Vacina{" +
                "id=" + id +
                ", nome='" + nome + '\'' +
                ", descricao='" + descricao + '\'' +
                ", dosesRecomendadas=" + dosesRecomendadas +
                ", intervaloEntreD='" + intervaloEntreD + '\'' +
                ", faixaEtariaRecomendada='" + faixaEtariaRecomendada + '\'' +
                ", efetividade=" + efetividade +
                ", fabricante='" + fabricante + '\'' +
                '}';
    }
}
