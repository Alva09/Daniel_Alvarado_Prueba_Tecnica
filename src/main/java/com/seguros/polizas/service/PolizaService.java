package com.seguros.polizas.service;

import com.seguros.polizas.domain.entity.Poliza;
import com.seguros.polizas.domain.entity.Riesgo;
import com.seguros.polizas.domain.enums.EstadoPoliza;
import com.seguros.polizas.domain.enums.EstadoRiesgo;
import com.seguros.polizas.domain.enums.TipoPoliza;
import com.seguros.polizas.exception.BusinessException;
import com.seguros.polizas.exception.ResourceNotFoundException;
import com.seguros.polizas.repository.PolizaRepository;
import com.seguros.polizas.repository.RiesgoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class PolizaService {

    private final PolizaRepository polizaRepository;
    private final RiesgoRepository riesgoRepository;
    private final CoreMockService coreMockService;
    private final NotificacionService notificacionService;

    @Value("${negocio.ipc}")
    private BigDecimal ipc;

    /**
     * Crea una nueva póliza (individual o colectiva).
     * Para individuales, se debe incluir los datos del riesgo inicial.
     */
    @Transactional
    public Poliza crearPoliza(Poliza poliza, Riesgo riesgoInicial) {
        // Generar número de póliza
        String prefijo = poliza.getTipo() == TipoPoliza.INDIVIDUAL ? "POL-IND-" : "POL-COL-";
        long count = polizaRepository.count() + 1;
        poliza.setNumeroPoliza(prefijo + String.format("%03d", count));

        // Calcular prima = canon mensual × meses de vigencia
        poliza.setPrima(poliza.getCanonMensual()
                .multiply(BigDecimal.valueOf(poliza.getMesesVigencia()))
                .setScale(2, RoundingMode.HALF_UP));

        // Calcular fecha fin de vigencia
        poliza.setFechaFinVigencia(
                poliza.getFechaInicioVigencia().plusMonths(poliza.getMesesVigencia()));

        poliza.setEstado(EstadoPoliza.VIGENTE);

        // Para individuales, el riesgo inicial es obligatorio
        if (poliza.getTipo() == TipoPoliza.INDIVIDUAL) {
            if (riesgoInicial == null) {
                throw new BusinessException(
                        "Una póliza individual requiere los datos del riesgo (asegurado)",
                        HttpStatus.BAD_REQUEST
                );
            }
            riesgoInicial.setEstado(EstadoRiesgo.ACTIVO);
            poliza.agregarRiesgo(riesgoInicial);
        } else if (riesgoInicial != null) {
            // Para colectivas, si viene un riesgo inicial, se agrega
            riesgoInicial.setEstado(EstadoRiesgo.ACTIVO);
            poliza.agregarRiesgo(riesgoInicial);
        }

        Poliza polizaCreada = polizaRepository.save(poliza);

        // Notificar al CORE
        coreMockService.notificarEvento("CREACION", polizaCreada.getId());

        // Notificar al tomador (email/SMS)
        notificacionService.notificarCreacion(polizaCreada.getNumeroPoliza(), poliza.getTipo().name());

        log.info("Póliza creada: {} ({})", polizaCreada.getNumeroPoliza(), poliza.getTipo());
        return polizaCreada;
    }

    /**
     * Lista pólizas filtrando por tipo y/o estado.
     */
    public List<Poliza> listarPolizas(TipoPoliza tipo, EstadoPoliza estado) {
        if (tipo != null && estado != null) {
            return polizaRepository.findByTipoAndEstado(tipo, estado);
        } else if (tipo != null) {
            return polizaRepository.findByTipo(tipo);
        } else if (estado != null) {
            return polizaRepository.findByEstado(estado);
        }
        return polizaRepository.findAll();
    }

    /**
     * Obtiene los riesgos de una póliza.
     */
    public List<Riesgo> obtenerRiesgos(Long polizaId) {
        validarPolizaExiste(polizaId);
        return riesgoRepository.findByPolizaId(polizaId);
    }

    /**
     * Renueva una póliza: incrementa canon y prima según IPC.
     * Regla: No se puede renovar una póliza cancelada.
     */
    @Transactional
    public Poliza renovarPoliza(Long polizaId) {
        Poliza poliza = buscarPoliza(polizaId);

        if (poliza.getEstado() == EstadoPoliza.CANCELADA) {
            throw new BusinessException(
                    "No se puede renovar una póliza cancelada (ID: " + polizaId + ")",
                    HttpStatus.UNPROCESSABLE_ENTITY
            );
        }

        // Guardar canon anterior para la notificación
        String canonAnterior = poliza.getCanonMensual().toPlainString();

        // Calcular nuevo canon: canon * (1 + IPC/100)
        BigDecimal factorIpc = BigDecimal.ONE.add(ipc.divide(BigDecimal.valueOf(100), 4, RoundingMode.HALF_UP));
        BigDecimal nuevoCanon = poliza.getCanonMensual().multiply(factorIpc).setScale(2, RoundingMode.HALF_UP);

        // Calcular nueva prima: nuevo canon * meses de vigencia
        BigDecimal nuevaPrima = nuevoCanon.multiply(BigDecimal.valueOf(poliza.getMesesVigencia())).setScale(2, RoundingMode.HALF_UP);

        poliza.setCanonMensual(nuevoCanon);
        poliza.setPrima(nuevaPrima);
        poliza.setIpcAplicado(ipc);
        poliza.setEstado(EstadoPoliza.RENOVADA);

        Poliza polizaRenovada = polizaRepository.save(poliza);

        // Notificar al CORE
        coreMockService.notificarEvento("RENOVACION", polizaId);

        // Notificar al tomador (email/SMS)
        notificacionService.notificarRenovacion(poliza.getNumeroPoliza(), canonAnterior, nuevoCanon.toPlainString());

        log.info("Póliza {} renovada. Nuevo canon: {}, Nueva prima: {}", polizaId, nuevoCanon, nuevaPrima);
        return polizaRenovada;
    }

    /**
     * Cancela una póliza y todos sus riesgos asociados.
     */
    @Transactional
    public Poliza cancelarPoliza(Long polizaId) {
        Poliza poliza = buscarPoliza(polizaId);

        if (poliza.getEstado() == EstadoPoliza.CANCELADA) {
            throw new BusinessException(
                    "La póliza ya se encuentra cancelada (ID: " + polizaId + ")",
                    HttpStatus.UNPROCESSABLE_ENTITY
            );
        }

        // Cancelar todos los riesgos asociados
        poliza.getRiesgos().forEach(riesgo -> riesgo.setEstado(EstadoRiesgo.CANCELADO));
        poliza.setEstado(EstadoPoliza.CANCELADA);

        Poliza polizaCancelada = polizaRepository.save(poliza);

        // Notificar al CORE
        coreMockService.notificarEvento("CANCELACION", polizaId);

        // Notificar al tomador y beneficiario (email)
        notificacionService.notificarCancelacion(poliza.getNumeroPoliza());

        log.info("Póliza {} cancelada junto con {} riesgos", polizaId, poliza.getRiesgos().size());
        return polizaCancelada;
    }

    /**
     * Agrega un riesgo a una póliza.
     * Regla: Solo se pueden agregar riesgos a pólizas COLECTIVAS.
     * Regla: Una póliza INDIVIDUAL solo puede tener 1 riesgo.
     */
    @Transactional
    public Riesgo agregarRiesgo(Long polizaId, Riesgo riesgo) {
        Poliza poliza = buscarPoliza(polizaId);

        if (poliza.getTipo() == TipoPoliza.INDIVIDUAL) {
            throw new BusinessException(
                    "No se pueden agregar riesgos a una póliza individual (ID: " + polizaId + ")",
                    HttpStatus.UNPROCESSABLE_ENTITY
            );
        }

        if (poliza.getEstado() == EstadoPoliza.CANCELADA) {
            throw new BusinessException(
                    "No se pueden agregar riesgos a una póliza cancelada (ID: " + polizaId + ")",
                    HttpStatus.UNPROCESSABLE_ENTITY
            );
        }

        riesgo.setEstado(EstadoRiesgo.ACTIVO);
        poliza.agregarRiesgo(riesgo);
        polizaRepository.save(poliza);

        // Notificar al CORE
        coreMockService.notificarEvento("ACTUALIZACION", polizaId);

        log.info("Riesgo agregado a póliza colectiva {}", polizaId);
        return riesgo;
    }

    private Poliza buscarPoliza(Long polizaId) {
        return polizaRepository.findById(polizaId)
                .orElseThrow(() -> new ResourceNotFoundException("Póliza no encontrada con ID: " + polizaId));
    }

    private void validarPolizaExiste(Long polizaId) {
        if (!polizaRepository.existsById(polizaId)) {
            throw new ResourceNotFoundException("Póliza no encontrada con ID: " + polizaId);
        }
    }
}
