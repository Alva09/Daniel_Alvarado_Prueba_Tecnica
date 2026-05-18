package com.seguros.polizas.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * Servicio de notificaciones (correo/SMS).
 * En esta implementación se simula el envío registrando en logs.
 * En producción se conectaría a un proveedor real (SES, SendGrid, Twilio).
 */
@Service
@Slf4j
public class NotificacionService {

    public void notificarCreacion(String numeroPoliza, String tipo) {
        log.info("📧 NOTIFICACIÓN - Póliza creada");
        log.info("   Número: {}", numeroPoliza);
        log.info("   Tipo: {}", tipo);
        log.info("   Canal: EMAIL + SMS");
        log.info("   Destinatario: Tomador de la póliza");
        log.info("   Estado: ENVIADA (simulado)");
    }

    public void notificarRenovacion(String numeroPoliza, String canonAnterior, String canonNuevo) {
        log.info("📧 NOTIFICACIÓN - Póliza renovada");
        log.info("   Número: {}", numeroPoliza);
        log.info("   Canon anterior: {}", canonAnterior);
        log.info("   Canon nuevo: {}", canonNuevo);
        log.info("   Canal: EMAIL + SMS");
        log.info("   Destinatario: Tomador de la póliza");
        log.info("   Estado: ENVIADA (simulado)");
    }

    public void notificarCancelacion(String numeroPoliza) {
        log.info("📧 NOTIFICACIÓN - Póliza cancelada");
        log.info("   Número: {}", numeroPoliza);
        log.info("   Canal: EMAIL");
        log.info("   Destinatario: Tomador y beneficiario");
        log.info("   Estado: ENVIADA (simulado)");
    }
}
