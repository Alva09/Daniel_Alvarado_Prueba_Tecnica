package com.seguros.polizas.controller.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class RiesgoRequest {

    @NotBlank(message = "El nombre del asegurado es obligatorio")
    private String nombreAsegurado;

    @NotBlank(message = "El documento del asegurado es obligatorio")
    private String documentoAsegurado;

    private String direccionInmueble;

    @NotNull(message = "El valor asegurado es obligatorio")
    @Positive(message = "El valor asegurado debe ser positivo")
    private BigDecimal valorAsegurado;
}
