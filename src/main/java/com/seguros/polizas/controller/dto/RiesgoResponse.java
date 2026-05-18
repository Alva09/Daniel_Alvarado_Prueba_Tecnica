package com.seguros.polizas.controller.dto;

import com.seguros.polizas.domain.entity.Riesgo;
import com.seguros.polizas.domain.enums.EstadoRiesgo;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class RiesgoResponse {

    private Long id;
    private Long polizaId;
    private EstadoRiesgo estado;
    private String nombreAsegurado;
    private String documentoAsegurado;
    private String direccionInmueble;
    private BigDecimal valorAsegurado;

    public static RiesgoResponse fromEntity(Riesgo riesgo) {
        return RiesgoResponse.builder()
                .id(riesgo.getId())
                .polizaId(riesgo.getPoliza().getId())
                .estado(riesgo.getEstado())
                .nombreAsegurado(riesgo.getNombreAsegurado())
                .documentoAsegurado(riesgo.getDocumentoAsegurado())
                .direccionInmueble(riesgo.getDireccionInmueble())
                .valorAsegurado(riesgo.getValorAsegurado())
                .build();
    }
}
