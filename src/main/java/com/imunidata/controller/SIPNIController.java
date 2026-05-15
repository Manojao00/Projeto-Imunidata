package com.imunidata.controller;

import com.imunidata.dto.CoberturavacinadalDTO;
import com.imunidata.service.SIPNIService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/sipni")
@AllArgsConstructor
@Slf4j
@CrossOrigin(origins = "*", maxAge = 3600)
public class SIPNIController {

    private final SIPNIService sipniService;

    /**
     * Buscar cobertura vacinal por estado
     * GET /api/sipni/cobertura/estado?estado=SP
     */
    @GetMapping("/cobertura/estado")
    public ResponseEntity<List<CoberturavacinadalDTO>> buscarCoberturaPorEstado(
            @RequestParam String estado) {
        log.info("Requisição para buscar cobertura por estado: {}", estado);
        List<CoberturavacinadalDTO> cobertura = sipniService.buscarCoberturaPorEstado(estado);
        
        if (cobertura.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        
        return ResponseEntity.ok(cobertura);
    }

    /**
     * Buscar cobertura vacinal por município
     * GET /api/sipni/cobertura/municipio?municipio=São Paulo
     */
    @GetMapping("/cobertura/municipio")
    public ResponseEntity<List<CoberturavacinadalDTO>> buscarCoberturaPorMunicipio(
            @RequestParam String municipio) {
        log.info("Requisição para buscar cobertura por município: {}", municipio);
        List<CoberturavacinadalDTO> cobertura = sipniService.buscarCoberturaPorMunicipio(municipio);
        
        if (cobertura.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        
        return ResponseEntity.ok(cobertura);
    }

    /**
     * Buscar cobertura vacinal por tipo de vacina
     * GET /api/sipni/cobertura/vacina?vacina=BCG
     */
    @GetMapping("/cobertura/vacina")
    public ResponseEntity<List<CoberturavacinadalDTO>> buscarCoberturaPorVacina(
            @RequestParam String vacina) {
        log.info("Requisição para buscar cobertura por vacina: {}", vacina);
        List<CoberturavacinadalDTO> cobertura = sipniService.buscarCoberturaPorVacina(vacina);
        
        if (cobertura.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        
        return ResponseEntity.ok(cobertura);
    }

    /**
     * Buscar cobertura em um período específico
     * GET /api/sipni/cobertura/periodo?ano=2024&mes=05&estado=SP
     */
    @GetMapping("/cobertura/periodo")
    public ResponseEntity<List<CoberturavacinadalDTO>> buscarCoberturaPeriodo(
            @RequestParam String ano,
            @RequestParam String mes,
            @RequestParam String estado) {
        log.info("Requisição para buscar cobertura - Ano: {}, Mês: {}, Estado: {}", ano, mes, estado);
        List<CoberturavacinadalDTO> cobertura = sipniService.buscarCoberturaPeriodo(ano, mes, estado);
        
        if (cobertura.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        
        return ResponseEntity.ok(cobertura);
    }

    /**
     * Obter resumo de cobertura por estado
     * GET /api/sipni/resumo/estados
     */
    @GetMapping("/resumo/estados")
    public ResponseEntity<List<CoberturavacinadalDTO>> obterResumoPorEstado() {
        log.info("Requisição para obter resumo de cobertura por estado");
        List<CoberturavacinadalDTO> resumo = sipniService.obterResumoPorEstado();
        return ResponseEntity.ok(resumo);
    }

    /**
     * Obter estatísticas gerais de cobertura
     * GET /api/sipni/estatisticas
     */
    @GetMapping("/estatisticas")
    public ResponseEntity<String> obterEstatisticas() {
        log.info("Requisição para obter estatísticas de cobertura");
        String estatisticas = sipniService.obterEstatisticas();
        return ResponseEntity.ok(estatisticas);
    }

    /**
     * Health check da integração SI-PNI
     * GET /api/sipni/health
     */
    @GetMapping("/health")
    public ResponseEntity<String> healthCheck() {
        log.info("Health check SI-PNI");
        return ResponseEntity.ok("SI-PNI Service está funcionando normalmente");
    }
}
