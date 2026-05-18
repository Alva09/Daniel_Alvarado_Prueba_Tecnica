package com.seguros.polizas.controller.dto;

import com.seguros.polizas.domain.entity.Poliza;
import com.seguros.polizas.domain.enums.EstadoPoliza;
import com.seguros.polizas.domain.enums.TipoPoliza;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
public class PolizaResponse {

    private Long id;
    private String numeroPoliza;
    private TipoPoliza tipo;
    private EstadoPoliza estado;
    private LocalDate fechaInicioVigencia;
    private LocalDate fechaFinVigencia;
    private Integer mesesVigencia;
    private BigDecimal canonMensual;
    private BigDecimal prima;
    private BigDecimal ipcAplicado;
    private int cantidadRiesgos;

    public static PolizaResponse fromEntity(Poliza poliza) {
        return PolizaResponse.builder()
                .id(poliza.getId())
                .numeroPoliza(poliza.getNumeroPoliza())
                .tipo(poliza.getTipo())
                .estado(poliza.getEstado())
                .fechaInicioVigencia(poliza.getFechaInicioVigencia())
                .fechaFinVigencia(poliza.getFechaFinVigencia())
                .mesesVigencia(poliza.getMesesVigencia())
                .canonMensual(poliza.getCanonMensual())
                .prima(poliza.getPrima())
                .ipcAplicado(poliza.getIpcAplicado())
                .cantidadRiesgos(poliza.getRiesgos() != null ? poliza.getRiesgos().size() : 0)
                .build();
    }
}
