package com.seguros.polizas.config;

import com.seguros.polizas.domain.entity.Poliza;
import com.seguros.polizas.domain.entity.Riesgo;
import com.seguros.polizas.domain.enums.EstadoPoliza;
import com.seguros.polizas.domain.enums.EstadoRiesgo;
import com.seguros.polizas.domain.enums.TipoPoliza;
import com.seguros.polizas.repository.PolizaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Carga datos de prueba al iniciar la aplicación.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {

    private final PolizaRepository polizaRepository;

    @Override
    public void run(String... args) {
        log.info("Cargando datos de prueba...");

        // Póliza Individual con 1 riesgo
        Poliza individual = Poliza.builder()
                .numeroPoliza("POL-IND-001")
                .tipo(TipoPoliza.INDIVIDUAL)
                .estado(EstadoPoliza.VIGENTE)
                .fechaInicioVigencia(LocalDate.of(2025, 1, 1))
                .fechaFinVigencia(LocalDate.of(2025, 12, 31))
                .mesesVigencia(12)
                .canonMensual(new BigDecimal("1500000.00"))
                .prima(new BigDecimal("18000000.00"))
                .build();

        Riesgo riesgoIndividual = Riesgo.builder()
                .estado(EstadoRiesgo.ACTIVO)
                .nombreAsegurado("Carlos Pérez")
                .documentoAsegurado("1023456789")
                .direccionInmueble("Calle 100 #15-20, Bogotá")
                .valorAsegurado(new BigDecimal("18000000.00"))
                .build();
        individual.agregarRiesgo(riesgoIndividual);

        polizaRepository.save(individual);

        // Póliza Colectiva con 2 riesgos
        Poliza colectiva = Poliza.builder()
                .numeroPoliza("POL-COL-001")
                .tipo(TipoPoliza.COLECTIVA)
                .estado(EstadoPoliza.VIGENTE)
                .fechaInicioVigencia(LocalDate.of(2025, 3, 1))
                .fechaFinVigencia(LocalDate.of(2026, 2, 28))
                .mesesVigencia(12)
                .canonMensual(new BigDecimal("2000000.00"))
                .prima(new BigDecimal("24000000.00"))
                .build();

        Riesgo riesgo1 = Riesgo.builder()
                .estado(EstadoRiesgo.ACTIVO)
                .nombreAsegurado("María López")
                .documentoAsegurado("1098765432")
                .direccionInmueble("Carrera 7 #45-10, Bogotá")
                .valorAsegurado(new BigDecimal("24000000.00"))
                .build();

        Riesgo riesgo2 = Riesgo.builder()
                .estado(EstadoRiesgo.ACTIVO)
                .nombreAsegurado("Juan Rodríguez")
                .documentoAsegurado("1012345678")
                .direccionInmueble("Avenida 68 #30-50, Bogotá")
                .valorAsegurado(new BigDecimal("24000000.00"))
                .build();

        colectiva.agregarRiesgo(riesgo1);
        colectiva.agregarRiesgo(riesgo2);

        polizaRepository.save(colectiva);

        // Póliza Colectiva cancelada (para pruebas de validación)
        Poliza cancelada = Poliza.builder()
                .numeroPoliza("POL-COL-002")
                .tipo(TipoPoliza.COLECTIVA)
                .estado(EstadoPoliza.CANCELADA)
                .fechaInicioVigencia(LocalDate.of(2024, 6, 1))
                .fechaFinVigencia(LocalDate.of(2025, 5, 31))
                .mesesVigencia(12)
                .canonMensual(new BigDecimal("1800000.00"))
                .prima(new BigDecimal("21600000.00"))
                .build();

        polizaRepository.save(cancelada);

        log.info("Datos de prueba cargados: 3 pólizas creadas");
    }
}
