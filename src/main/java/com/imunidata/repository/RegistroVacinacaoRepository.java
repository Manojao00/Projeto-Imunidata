package com.imunidata.repository;

import com.imunidata.model.RegistroVacinacao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface RegistroVacinacaoRepository extends JpaRepository<RegistroVacinacao, Long> {

    /**
     * Busca registros de vacinação por tipo de vacina
     */
    List<RegistroVacinacao> findByVacina(String vacina);

    /**
     * Busca registros de vacinação por estado
     */
    List<RegistroVacinacao> findByEstado(String estado);

    /**
     * Busca registros de vacinação por município
     */
    List<RegistroVacinacao> findByMunicipio(String municipio);

    /**
     * Busca registros de vacinação por faixa etária
     */
    List<RegistroVacinacao> findByFaixaEtaria(String faixaEtaria);

    /**
     * Busca registros de vacinação por estado e vacina
     */
    List<RegistroVacinacao> findByEstadoAndVacina(String estado, String vacina);

    /**
     * Busca registros de vacinação por data de registro
     */
    List<RegistroVacinacao> findByDataRegistro(LocalDate dataRegistro);

    /**
     * Busca registros de vacinação dentro de um período de datas
     */
    @Query("SELECT r FROM RegistroVacinacao r WHERE r.dataRegistro BETWEEN :dataInicio AND :dataFim")
    List<RegistroVacinacao> findByDataRegistroBetween(
            @Param("dataInicio") LocalDate dataInicio,
            @Param("dataFim") LocalDate dataFim
    );

    /**
     * Busca registros agrupados por estado e vacina (para resumos)
     */
    @Query("SELECT r FROM RegistroVacinacao r ORDER BY r.estado, r.vacina")
    List<RegistroVacinacao> findAllOrderByEstadoAndVacina();
}
