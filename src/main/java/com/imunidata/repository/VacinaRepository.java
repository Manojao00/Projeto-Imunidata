package com.imunidata.repository;

import com.imunidata.model.Vacina;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface VacinaRepository extends JpaRepository<Vacina, Long> {

    /**
     * Buscar vacina por nome
     */
    Optional<Vacina> findByNome(String nome);

    /**
     * Buscar vacinas por faixa etária recomendada
     */
    @Query("SELECT v FROM Vacina v WHERE v.faixaEtariaRecomendada LIKE %:faixa%")
    List<Vacina> findByFaixaEtaria(@Param("faixa") String faixa);

    /**
     * Buscar vacinas com efetividade acima de um percentual
     */
    @Query("SELECT v FROM Vacina v WHERE v.efetividade >= :efetividadeMinima ORDER BY v.efetividade DESC")
    List<Vacina> findByEfetividadeMinima(@Param("efetividadeMinima") Double efetividadeMinima);

    /**
     * Buscar vacinas por fabricante
     */
    List<Vacina> findByFabricante(String fabricante);

    /**
     * Buscar todas as vacinas ordenadas por nome
     */
    @Query("SELECT v FROM Vacina v ORDER BY v.nome ASC")
    List<Vacina> findAllOrderByNome();

    /**
     * Buscar vacinas por número de doses recomendadas
     */
    List<Vacina> findByDosesRecomendadas(Integer doses);
}
