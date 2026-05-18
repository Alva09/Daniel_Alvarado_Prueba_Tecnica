package com.seguros.polizas.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * Mock del endpoint del CORE transaccional.
 * Su único propósito es registrar en logs que la operación se intentó enviar al CORE.
 */
@RestController
@RequestMapping("/core-mock")
@Slf4j
public class CoreMockController {

    @PostMapping("/evento")
    public ResponseEntity<Map<String, String>> recibirEvento(@RequestBody Map<String, Object> payload) {
        log.info("══════════════════════════════════════════════════════");
        log.info("  CORE MOCK - Evento recibido");
        log.info("  Payload: {}", payload);
        log.info("══════════════════════════════════════════════════════");

        return ResponseEntity.ok(Map.of(
                "status", "RECIBIDO",
                "mensaje", "Evento registrado en CORE mock"
        ));
    }
}
