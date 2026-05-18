package com.seguros.polizas.domain.entity;

import com.seguros.polizas.domain.enums.EstadoRiesgo;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "riesgo")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Riesgo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "poliza_id", nullable = false)
    private Poliza poliza;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoRiesgo estado;

    @Column(nullable = false)
    private String nombreAsegurado;

    @Column(nullable = false)
    private String documentoAsegurado;

    private String direccionInmueble;

    @Column(precision = 14, scale = 2)
    private BigDecimal valorAsegurado;

}
