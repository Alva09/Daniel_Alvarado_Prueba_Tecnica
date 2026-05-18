package com.seguros.polizas.domain.entity;

import com.seguros.polizas.domain.enums.EstadoPoliza;
import com.seguros.polizas.domain.enums.TipoPoliza;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "poliza")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Poliza {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String numeroPoliza;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoPoliza tipo;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoPoliza estado;

    @Column(nullable = false)
    private LocalDate fechaInicioVigencia;

    @Column(nullable = false)
    private LocalDate fechaFinVigencia;

    @Column(nullable = false)
    private Integer mesesVigencia;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal canonMensual;

    @Column(nullable = false, precision = 14, scale = 2)
    private BigDecimal prima;

    @Column(precision = 5, scale = 2)
    private BigDecimal ipcAplicado;

    @OneToMany(mappedBy = "poliza", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Riesgo> riesgos = new ArrayList<>();

    public void agregarRiesgo(Riesgo riesgo) {
        riesgos.add(riesgo);
        riesgo.setPoliza(this);
    }
}
