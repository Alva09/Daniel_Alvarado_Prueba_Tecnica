package com.seguros.polizas.controller.dto;

import com.seguros.polizas.domain.enums.TipoPoliza;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class PolizaRequest {

    @NotNull(message = "El tipo de póliza es obligatorio")
    private TipoPoliza tipo;

    @NotNull(message = "La fecha de inicio de vigencia es obligatoria")
    private LocalDate fechaInicioVigencia;

    @NotNull(message = "Los meses de vigencia son obligatorios")
    @Min(value = 1, message = "La vigencia mínima es 1 mes")
    @Max(value = 60, message = "La vigencia máxima es 60 meses")
    private Integer mesesVigencia;

    @NotNull(message = "El canon mensual es obligatorio")
    @Positive(message = "El canon mensual debe ser positivo")
    private BigDecimal canonMensual;

    // Datos del riesgo inicial (obligatorio para individuales, opcional para colectivas)
    private String nombreAsegurado;
    private String documentoAsegurado;
    private String direccionInmueble;
    private BigDecimal valorAsegurado;
}
