package com.seguros.polizas.controller;

import com.seguros.polizas.controller.dto.RiesgoResponse;
import com.seguros.polizas.domain.entity.Riesgo;
import com.seguros.polizas.service.RiesgoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/riesgos")
@RequiredArgsConstructor
public class RiesgoController {

    private final RiesgoService riesgoService;

    /**
     * POST /riesgos/{id}/cancelar
     * Cancela un riesgo individual.
     */
    @PostMapping("/{id}/cancelar")
    public ResponseEntity<RiesgoResponse> cancelarRiesgo(@PathVariable Long id) {
        Riesgo riesgoCancelado = riesgoService.cancelarRiesgo(id);
        return ResponseEntity.ok(RiesgoResponse.fromEntity(riesgoCancelado));
    }
}
