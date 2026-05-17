package com.imunidata.controller;

import com.imunidata.dto.CoberturavacinadalDTO;
import com.imunidata.service.SIPNIService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

    /** GET /api/sipni/cobertura/estado?estado=SP */
    @GetMapping("/cobertura/estado")
    public ResponseEntity<List<CoberturavacinadalDTO>> buscarCoberturaPorEstado(
            @RequestParam String estado) {
        log.info("Requisição para buscar cobertura por estado: {}", estado);
        List<CoberturavacinadalDTO> cobertura = sipniService.buscarCoberturaPorEstado(estado);
        return ResponseEntity.ok(cobertura);  // retorna lista vazia em vez de 404
    }

    /** GET /api/sipni/cobertura/municipio?municipio=São Paulo */
    @GetMapping("/cobertura/municipio")
    public ResponseEntity<List<CoberturavacinadalDTO>> buscarCoberturaPorMunicipio(
            @RequestParam String municipio) {
        log.info("Requisição para buscar cobertura por município: {}", municipio);
        List<CoberturavacinadalDTO> cobertura = sipniService.buscarCoberturaPorMunicipio(municipio);
        return ResponseEntity.ok(cobertura);
    }

    /** GET /api/sipni/cobertura/vacina?vacina=BCG */
    @GetMapping("/cobertura/vacina")
    public ResponseEntity<List<CoberturavacinadalDTO>> buscarCoberturaPorVacina(
            @RequestParam String vacina) {
        log.info("Requisição para buscar cobertura por vacina: {}", vacina);
        List<CoberturavacinadalDTO> cobertura = sipniService.buscarCoberturaPorVacina(vacina);
        return ResponseEntity.ok(cobertura);
    }

    /**
     * GET /api/sipni/cobertura/periodo?ano=2026&mes=05&estado=SP
     * Parâmetros alinhados com a chamada do frontend (SIPNI.js)
     */
    @GetMapping("/cobertura/periodo")
    public ResponseEntity<List<CoberturavacinadalDTO>> buscarCoberturaPeriodo(
            @RequestParam String ano,
            @RequestParam String mes,
            @RequestParam String estado) {
        log.info("Requisição cobertura período - Ano: {}, Mês: {}, Estado: {}", ano, mes, estado);
        List<CoberturavacinadalDTO> cobertura = sipniService.buscarCoberturaPeriodo(ano, mes, estado);
        return ResponseEntity.ok(cobertura);
    }

    /** GET /api/sipni/resumo/estados */
    @GetMapping("/resumo/estados")
    public ResponseEntity<List<CoberturavacinadalDTO>> obterResumoPorEstado() {
        log.info("Requisição para obter resumo de cobertura por estado");
        return ResponseEntity.ok(sipniService.obterResumoPorEstado());
    }

    /** GET /api/sipni/estatisticas */
    @GetMapping("/estatisticas")
    public ResponseEntity<String> obterEstatisticas() {
        log.info("Requisição para obter estatísticas");
        return ResponseEntity.ok(sipniService.obterEstatisticas());
    }

    /** GET /api/sipni/health */
    @GetMapping("/health")
    public ResponseEntity<String> healthCheck() {
        return ResponseEntity.ok("SI-PNI Service está funcionando normalmente");
    }
}
