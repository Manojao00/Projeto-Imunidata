package com.imunidata.controller;

import com.imunidata.model.RegistroVacinacao;
import com.imunidata.service.RegistroVacinacaoService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/registros")
@AllArgsConstructor
@Slf4j
@CrossOrigin(origins = "*", maxAge = 3600)
public class RegistroVacinacaoController {

    private final RegistroVacinacaoService service;

    /**
     * Obter todos os registros de vacinação
     * GET /api/registros
     */
    @GetMapping
    public ResponseEntity<List<RegistroVacinacao>> obterTodos() {
        log.info("Requisição para obter todos os registros");
        List<RegistroVacinacao> registros = service.obterTodos();
        return ResponseEntity.ok(registros);
    }

    /**
     * Obter um registro por ID
     * GET /api/registros/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<RegistroVacinacao> obterPorId(@PathVariable Long id) {
        log.info("Requisição para obter registro com ID: {}", id);
        Optional<RegistroVacinacao> registro = service.obterPorId(id);
        return registro.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Criar um novo registro de vacinação
     * POST /api/registros
     */
    @PostMapping
    public ResponseEntity<RegistroVacinacao> criar(@RequestBody RegistroVacinacao registro) {
        log.info("Requisição para criar novo registro");
        RegistroVacinacao novoRegistro = service.criar(registro);
        return ResponseEntity.status(HttpStatus.CREATED).body(novoRegistro);
    }

    /**
     * Atualizar um registro existente
     * PUT /api/registros/{id}
     */
    @PutMapping("/{id}")
    public ResponseEntity<RegistroVacinacao> atualizar(
            @PathVariable Long id,
            @RequestBody RegistroVacinacao registro) {
        try {
            log.info("Requisição para atualizar registro com ID: {}", id);
            RegistroVacinacao registroAtualizado = service.atualizar(id, registro);
            return ResponseEntity.ok(registroAtualizado);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Deletar um registro
     * DELETE /api/registros/{id}
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        try {
            log.info("Requisição para deletar registro com ID: {}", id);
            service.deletar(id);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Buscar registros por tipo de vacina
     * GET /api/registros/buscar/vacina?nome=BCG
     */
    @GetMapping("/buscar/vacina")
    public ResponseEntity<List<RegistroVacinacao>> buscarPorVacina(@RequestParam String nome) {
        log.info("Requisição para buscar registros por vacina: {}", nome);
        List<RegistroVacinacao> registros = service.buscarPorVacina(nome);
        return ResponseEntity.ok(registros);
    }

    /**
     * Buscar registros por estado
     * GET /api/registros/buscar/estado?nome=SP
     */
    @GetMapping("/buscar/estado")
    public ResponseEntity<List<RegistroVacinacao>> buscarPorEstado(@RequestParam String nome) {
        log.info("Requisição para buscar registros por estado: {}", nome);
        List<RegistroVacinacao> registros = service.buscarPorEstado(nome);
        return ResponseEntity.ok(registros);
    }

    /**
     * Buscar registros por município
     * GET /api/registros/buscar/municipio?nome=São Paulo
     */
    @GetMapping("/buscar/municipio")
    public ResponseEntity<List<RegistroVacinacao>> buscarPorMunicipio(@RequestParam String nome) {
        log.info("Requisição para buscar registros por município: {}", nome);
        List<RegistroVacinacao> registros = service.buscarPorMunicipio(nome);
        return ResponseEntity.ok(registros);
    }

    /**
     * Buscar registros por faixa etária
     * GET /api/registros/buscar/faixa-etaria?faixa=5-9
     */
    @GetMapping("/buscar/faixa-etaria")
    public ResponseEntity<List<RegistroVacinacao>> buscarPorFaixaEtaria(@RequestParam String faixa) {
        log.info("Requisição para buscar registros por faixa etária: {}", faixa);
        List<RegistroVacinacao> registros = service.buscarPorFaixaEtaria(faixa);
        return ResponseEntity.ok(registros);
    }

    /**
     * Buscar registros por estado e vacina
     * GET /api/registros/buscar/estado-vacina?estado=SP&vacina=BCG
     */
    @GetMapping("/buscar/estado-vacina")
    public ResponseEntity<List<RegistroVacinacao>> buscarPorEstadoEVacina(
            @RequestParam String estado,
            @RequestParam String vacina) {
        log.info("Requisição para buscar registros por estado: {} e vacina: {}", estado, vacina);
        List<RegistroVacinacao> registros = service.buscarPorEstadoEVacina(estado, vacina);
        return ResponseEntity.ok(registros);
    }

    /**
     * Carregar dados de um arquivo CSV
     * POST /api/registros/carregar-csv
     */
    @PostMapping("/carregar-csv")
    public ResponseEntity<List<RegistroVacinacao>> carregarCSV(
            @RequestParam("file") MultipartFile arquivo) {
        try {
            log.info("Requisição para carregar dados do CSV");
            List<RegistroVacinacao> registros = service.carregarDadosCSV(arquivo.getInputStream());
            return ResponseEntity.status(HttpStatus.CREATED).body(registros);
        } catch (IOException e) {
            log.error("Erro ao processar arquivo CSV: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Obter resumo de vacinações por estado
     * GET /api/registros/resumos/estado
     */
    @GetMapping("/resumos/estado")
    public ResponseEntity<List<RegistroVacinacao>> obterResumosPorEstado() {
        log.info("Requisição para obter resumos por estado");
        List<RegistroVacinacao> resumos = service.obterResumosPorEstado();
        return ResponseEntity.ok(resumos);
    }
}
