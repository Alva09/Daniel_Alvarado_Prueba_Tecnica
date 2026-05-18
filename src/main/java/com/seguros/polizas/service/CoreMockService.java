package com.seguros.polizas.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

/**
 * Servicio que realiza la llamada HTTP real al mock del CORE transaccional.
 * En producción, este servicio invocaría el endpoint real del CORE vía WebLogic.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class CoreMockService {

    private final RestTemplate restTemplate;

    @Value("${core.mock.url}")
    private String coreMockUrl;

    /**
     * Notifica al CORE sobre un evento en una póliza.
     * Realiza un POST real al endpoint /core-mock/evento.
     */
    public void notificarEvento(String evento, Long polizaId) {
        try {
            Map<String, Object> payload = Map.of(
                    "evento", evento,
                    "polizaId", polizaId
            );

            log.info("→ Enviando evento al CORE: {} (póliza {})", evento, polizaId);

            var response = restTemplate.postForEntity(coreMockUrl, payload, Map.class);

            log.info("← Respuesta del CORE: {} - {}", response.getStatusCode(), response.getBody());

        } catch (Exception e) {
            log.error("✗ Error al comunicarse con el CORE: {}", e.getMessage());
            // En producción aquí iría el Circuit Breaker / Dead Letter Queue
        }
    }
}
