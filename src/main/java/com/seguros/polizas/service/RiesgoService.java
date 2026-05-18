package com.seguros.polizas.service;

import com.seguros.polizas.domain.entity.Riesgo;
import com.seguros.polizas.domain.enums.EstadoRiesgo;
import com.seguros.polizas.exception.BusinessException;
import com.seguros.polizas.exception.ResourceNotFoundException;
import com.seguros.polizas.repository.RiesgoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class RiesgoService {

    private final RiesgoRepository riesgoRepository;
    private final CoreMockService coreMockService;

    /**
     * Cancela un riesgo individual.
     */
    @Transactional
    public Riesgo cancelarRiesgo(Long riesgoId) {
        Riesgo riesgo = riesgoRepository.findById(riesgoId)
                .orElseThrow(() -> new ResourceNotFoundException("Riesgo no encontrado con ID: " + riesgoId));

        if (riesgo.getEstado() == EstadoRiesgo.CANCELADO) {
            throw new BusinessException(
                    "El riesgo ya se encuentra cancelado (ID: " + riesgoId + ")",
                    HttpStatus.UNPROCESSABLE_ENTITY
            );
        }

        riesgo.setEstado(EstadoRiesgo.CANCELADO);
        Riesgo riesgoCancelado = riesgoRepository.save(riesgo);

        // Notificar al CORE
        coreMockService.notificarEvento("CANCELACION_RIESGO", riesgo.getPoliza().getId());

        log.info("Riesgo {} cancelado de póliza {}", riesgoId, riesgo.getPoliza().getId());
        return riesgoCancelado;
    }
}
