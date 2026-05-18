package com.seguros.polizas.controller;

import com.seguros.polizas.controller.dto.PolizaRequest;
import com.seguros.polizas.controller.dto.PolizaResponse;
import com.seguros.polizas.controller.dto.RiesgoRequest;
import com.seguros.polizas.controller.dto.RiesgoResponse;
import com.seguros.polizas.domain.entity.Poliza;
import com.seguros.polizas.domain.entity.Riesgo;
import com.seguros.polizas.domain.enums.EstadoPoliza;
import com.seguros.polizas.domain.enums.TipoPoliza;
import com.seguros.polizas.service.PolizaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/polizas")
@RequiredArgsConstructor
public class PolizaController {

    private final PolizaService polizaService;

    /**
     * POST /polizas
     * Crea una nueva póliza (individual o colectiva).
     */
    @PostMapping
    public ResponseEntity<PolizaResponse> crearPoliza(@Valid @RequestBody PolizaRequest request) {
        Poliza poliza = Poliza.builder()
                .tipo(request.getTipo())
                .fechaInicioVigencia(request.getFechaInicioVigencia())
                .mesesVigencia(request.getMesesVigencia())
                .canonMensual(request.getCanonMensual())
                .build();

        Riesgo riesgoInicial = null;
        if (request.getNombreAsegurado() != null) {
            riesgoInicial = Riesgo.builder()
                    .nombreAsegurado(request.getNombreAsegurado())
                    .documentoAsegurado(request.getDocumentoAsegurado())
                    .direccionInmueble(request.getDireccionInmueble())
                    .valorAsegurado(request.getValorAsegurado())
                    .build();
        }

        Poliza polizaCreada = polizaService.crearPoliza(poliza, riesgoInicial);
        return ResponseEntity.status(HttpStatus.CREATED).body(PolizaResponse.fromEntity(polizaCreada));
    }

    /**
     * GET /polizas?tipo=INDIVIDUAL&estado=VIGENTE
     * Lista pólizas filtrando opcionalmente por tipo y estado.
     */
    @GetMapping
    public ResponseEntity<List<PolizaResponse>> listarPolizas(
            @RequestParam(required = false) TipoPoliza tipo,
            @RequestParam(required = false) EstadoPoliza estado) {

        List<PolizaResponse> polizas = polizaService.listarPolizas(tipo, estado)
                .stream()
                .map(PolizaResponse::fromEntity)
                .toList();

        return ResponseEntity.ok(polizas);
    }

    /**
     * GET /polizas/{id}/riesgos
     * Lista los riesgos de una póliza.
     */
    @GetMapping("/{id}/riesgos")
    public ResponseEntity<List<RiesgoResponse>> listarRiesgos(@PathVariable Long id) {
        List<RiesgoResponse> riesgos = polizaService.obtenerRiesgos(id)
                .stream()
                .map(RiesgoResponse::fromEntity)
                .toList();

        return ResponseEntity.ok(riesgos);
    }

    /**
     * POST /polizas/{id}/renovar
     * Renueva una póliza incrementando canon y prima según IPC.
     */
    @PostMapping("/{id}/renovar")
    public ResponseEntity<PolizaResponse> renovarPoliza(@PathVariable Long id) {
        Poliza polizaRenovada = polizaService.renovarPoliza(id);
        return ResponseEntity.ok(PolizaResponse.fromEntity(polizaRenovada));
    }

    /**
     * POST /polizas/{id}/cancelar
     * Cancela una póliza y todos sus riesgos.
     */
    @PostMapping("/{id}/cancelar")
    public ResponseEntity<PolizaResponse> cancelarPoliza(@PathVariable Long id) {
        Poliza polizaCancelada = polizaService.cancelarPoliza(id);
        return ResponseEntity.ok(PolizaResponse.fromEntity(polizaCancelada));
    }

    /**
     * POST /polizas/{id}/riesgos
     * Agrega un riesgo a una póliza colectiva.
     */
    @PostMapping("/{id}/riesgos")
    public ResponseEntity<RiesgoResponse> agregarRiesgo(
            @PathVariable Long id,
            @Valid @RequestBody RiesgoRequest request) {

        Riesgo riesgo = Riesgo.builder()
                .nombreAsegurado(request.getNombreAsegurado())
                .documentoAsegurado(request.getDocumentoAsegurado())
                .direccionInmueble(request.getDireccionInmueble())
                .valorAsegurado(request.getValorAsegurado())
                .build();

        Riesgo riesgoCreado = polizaService.agregarRiesgo(id, riesgo);
        return ResponseEntity.status(HttpStatus.CREATED).body(RiesgoResponse.fromEntity(riesgoCreado));
    }
}
