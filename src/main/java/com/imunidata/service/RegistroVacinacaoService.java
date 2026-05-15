package com.imunidata.service;

import com.imunidata.model.RegistroVacinacao;
import com.imunidata.repository.RegistroVacinacaoRepository;
import com.opencsv.CSVReader;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
@Slf4j
public class RegistroVacinacaoService {

    private final RegistroVacinacaoRepository repository;
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    /**
     * Obter todos os registros de vacinação
     */
    public List<RegistroVacinacao> obterTodos() {
        return repository.findAll();
    }

    /**
     * Obter um registro pelo ID
     */
    public Optional<RegistroVacinacao> obterPorId(Long id) {
        return repository.findById(id);
    }

    /**
     * Criar um novo registro de vacinação
     */
    public RegistroVacinacao criar(RegistroVacinacao registro) {
        log.info("Criando novo registro de vacinação: {}", registro);
        return repository.save(registro);
    }

    /**
     * Atualizar um registro de vacinação existente
     */
    public RegistroVacinacao atualizar(Long id, RegistroVacinacao registro) {
        if (repository.existsById(id)) {
            registro.setId(id);
            log.info("Atualizando registro de vacinação com ID: {}", id);
            return repository.save(registro);
        }
        throw new IllegalArgumentException("Registro não encontrado com ID: " + id);
    }

    /**
     * Deletar um registro de vacinação
     */
    public void deletar(Long id) {
        if (repository.existsById(id)) {
            log.info("Deletando registro de vacinação com ID: {}", id);
            repository.deleteById(id);
        } else {
            throw new IllegalArgumentException("Registro não encontrado com ID: " + id);
        }
    }

    /**
     * Buscar registros por tipo de vacina
     */
    public List<RegistroVacinacao> buscarPorVacina(String vacina) {
        log.info("Buscando registros para vacina: {}", vacina);
        return repository.findByVacina(vacina);
    }

    /**
     * Buscar registros por estado
     */
    public List<RegistroVacinacao> buscarPorEstado(String estado) {
        log.info("Buscando registros para estado: {}", estado);
        return repository.findByEstado(estado);
    }

    /**
     * Buscar registros por município
     */
    public List<RegistroVacinacao> buscarPorMunicipio(String municipio) {
        log.info("Buscando registros para município: {}", municipio);
        return repository.findByMunicipio(municipio);
    }

    /**
     * Buscar registros por faixa etária
     */
    public List<RegistroVacinacao> buscarPorFaixaEtaria(String faixaEtaria) {
        log.info("Buscando registros para faixa etária: {}", faixaEtaria);
        return repository.findByFaixaEtaria(faixaEtaria);
    }

    /**
     * Buscar registros por estado e vacina
     */
    public List<RegistroVacinacao> buscarPorEstadoEVacina(String estado, String vacina) {
        log.info("Buscando registros para estado: {} e vacina: {}", estado, vacina);
        return repository.findByEstadoAndVacina(estado, vacina);
    }

    /**
     * Desafio técnico: Popular banco de dados a partir de arquivo CSV
     * Formato esperado: municipio,estado,vacina,dose,quantidadeAplicada,dataRegistro,faixaEtaria
     */
    public List<RegistroVacinacao> carregarDadosCSV(InputStream csvFile) {
        List<RegistroVacinacao> registros = new ArrayList<>();

        try (CSVReader reader = new CSVReader(new InputStreamReader(csvFile))) {
            String[] linhas;
            int contador = 0;

            // Pular cabeçalho
            reader.readNext();

            while ((linhas = reader.readNext()) != null) {
                if (linhas.length < 7) {
                    log.warn("Linha com dados incompletos ignorada na linha {}", contador);
                    continue;
                }

                try {
                    RegistroVacinacao registro = RegistroVacinacao.builder()
                            .municipio(linhas[0].trim())
                            .estado(linhas[1].trim())
                            .vacina(linhas[2].trim())
                            .dose(linhas[3].trim())
                            .quantidadeAplicada(Integer.parseInt(linhas[4].trim()))
                            .dataRegistro(LocalDate.parse(linhas[5].trim(), DATE_FORMATTER))
                            .faixaEtaria(linhas[6].trim())
                            .build();

                    registros.add(registro);
                    contador++;
                } catch (Exception e) {
                    log.error("Erro ao processar linha {}: {}", contador, e.getMessage());
                }
            }

            // Salvar todos os registros no banco
            List<RegistroVacinacao> registrosSalvos = repository.saveAll(registros);
            log.info("Foram carregados e salvos {} registros do CSV", registrosSalvos.size());

            return registrosSalvos;
        } catch (IOException e) {
            log.error("Erro ao ler arquivo CSV: {}", e.getMessage());
            throw new RuntimeException("Erro ao processar arquivo CSV", e);
        }
    }

    /**
     * Obter resumo de vacinações por estado
     */
    public List<RegistroVacinacao> obterResumosPorEstado() {
        return repository.findAllOrderByEstadoAndVacina();
    }
}
