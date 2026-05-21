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

    List<RegistroVacinacao> findByVacina(String vacina);
    List<RegistroVacinacao> findByEstado(String estado);
    List<RegistroVacinacao> findByMunicipio(String municipio);
    List<RegistroVacinacao> findByIdade(String idade);
    List<RegistroVacinacao> findByEstadoAndVacina(String estado, String vacina);

    // Busca por data exata
    List<RegistroVacinacao> findByDataRegistro(LocalDate data);

    // Busca por intervalo de datas
    List<RegistroVacinacao> findByDataRegistroBetween(LocalDate inicio, LocalDate fim);

    // Busca por data e estado
    List<RegistroVacinacao> findByDataRegistroBetweenAndEstado(
            LocalDate inicio, LocalDate fim, String estado);

    @Query("SELECT r FROM RegistroVacinacao r ORDER BY r.estado, r.vacina")
    List<RegistroVacinacao> findAllOrderByEstadoAndVacina();
}
