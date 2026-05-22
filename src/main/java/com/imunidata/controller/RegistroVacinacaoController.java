package com.imunidata.controller;

import com.imunidata.model.RegistroVacinacao;
import com.imunidata.service.RegistroVacinacaoService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/registros")
@AllArgsConstructor
@Slf4j
@CrossOrigin(origins = "*", maxAge = 3600)
public class RegistroVacinacaoController {

    private final RegistroVacinacaoService service;

    @GetMapping({ "", "/" })
    public ResponseEntity<List<RegistroVacinacao>> obterTodos() {
        return ResponseEntity.ok(service.obterTodos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<RegistroVacinacao> obterPorId(@PathVariable Long id) {
        Optional<RegistroVacinacao> r = service.obterPorId(id);
        return r.map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @PostMapping({ "", "/" })
    public ResponseEntity<RegistroVacinacao> criar(@RequestBody RegistroVacinacao registro) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.criar(registro));
    }

    @PutMapping("/{id}")
    public ResponseEntity<RegistroVacinacao> atualizar(@PathVariable Long id,
            @RequestBody RegistroVacinacao registro) {
        try {
            return ResponseEntity.ok(service.atualizar(id, registro));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        try {
            service.deletar(id);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/buscar/vacina")
    public ResponseEntity<List<RegistroVacinacao>> buscarPorVacina(@RequestParam String nome) {
        return ResponseEntity.ok(service.buscarPorVacina(nome));
    }

    @GetMapping("/buscar/estado")
    public ResponseEntity<List<RegistroVacinacao>> buscarPorEstado(@RequestParam String nome) {
        return ResponseEntity.ok(service.buscarPorEstado(nome));
    }

    @GetMapping("/buscar/municipio")
    public ResponseEntity<List<RegistroVacinacao>> buscarPorMunicipio(@RequestParam String nome) {
        return ResponseEntity.ok(service.buscarPorMunicipio(nome));
    }

    @GetMapping("/buscar/faixa-etaria")
    public ResponseEntity<List<RegistroVacinacao>> buscarPorFaixaEtaria(@RequestParam String faixa) {
        return ResponseEntity.ok(service.buscarPorIdade(faixa));
    }

    @GetMapping("/buscar/idade")
    public ResponseEntity<List<RegistroVacinacao>> buscarPorIdade(@RequestParam String idade) {
        return ResponseEntity.ok(service.buscarPorIdade(idade));
    }

    @GetMapping("/buscar/estado-vacina")
    public ResponseEntity<List<RegistroVacinacao>> buscarPorEstadoEVacina(
            @RequestParam String estado, @RequestParam String vacina) {
        return ResponseEntity.ok(service.buscarPorEstadoEVacina(estado, vacina));
    }

    @GetMapping("/buscar/data")
    public ResponseEntity<List<RegistroVacinacao>> buscarPorData(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate data) {
        return ResponseEntity.ok(service.buscarPorData(data));
    }

    @GetMapping("/buscar/periodo")
    public ResponseEntity<List<RegistroVacinacao>> buscarPorPeriodo(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate inicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fim,
            @RequestParam(required = false) String estado) {
        return ResponseEntity.ok(service.buscarPorPeriodoEEstado(inicio, fim, estado));
    }

    @PostMapping("/carregar-csv")
    public ResponseEntity<?> carregarCSV(@RequestParam("file") MultipartFile arquivo) {
        if (arquivo.isEmpty())
            return ResponseEntity.badRequest().body("Arquivo CSV vazio");
        try {
            List<RegistroVacinacao> registros = service.carregarDadosCSV(arquivo.getInputStream());
            log.info("CSV importado: {} registros", registros.size());
            return ResponseEntity.status(HttpStatus.CREATED).body(registros);
        } catch (IOException e) {
            return ResponseEntity.badRequest().body("Erro ao processar CSV: " + e.getMessage());
        }
    }

    @GetMapping("/resumos/estado")
    public ResponseEntity<List<RegistroVacinacao>> obterResumosPorEstado() {
        return ResponseEntity.ok(service.obterResumosPorEstado());
    }
}
