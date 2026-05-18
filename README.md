# Módulo 2 – API de Gestión de Pólizas

API REST desarrollada con Spring Boot 3.2 para la gestión de pólizas de arrendamiento (individuales y colectivas).

## Requisitos

- Java 17+
- Maven 3.8+
- Lombok plugin

## Ejecución

```bash
cd modulo2-api-polizas
mvn spring-boot:run
```

La aplicación inicia en `http://localhost:8080`.

## Seguridad

Todos los endpoints (excepto `/core-mock` y `/h2-console`) requieren el header:

```
x-api-key: 123456
```

## Endpoints

| Método | Ruta | Descripción |
|--------|------|-------------|
| POST | `/polizas` | Crear nueva póliza (individual o colectiva) |
| GET | `/polizas?tipo=INDIVIDUAL&estado=VIGENTE` | Listar pólizas (filtros opcionales) |
| GET | `/polizas/{id}/riesgos` | Listar riesgos de una póliza |
| POST | `/polizas/{id}/renovar` | Renovar póliza (+IPC) |
| POST | `/polizas/{id}/cancelar` | Cancelar póliza y sus riesgos |
| POST | `/polizas/{id}/riesgos` | Agregar riesgo (solo colectivas) |
| POST | `/riesgos/{id}/cancelar` | Cancelar un riesgo |
| POST | `/core-mock/evento` | Mock del CORE (registra en logs) |

## Ejemplos de uso (cURL)

### Crear póliza individual
```bash
curl -X POST -H "x-api-key: 123456" \
  -H "Content-Type: application/json" \
  -d '{
    "tipo": "INDIVIDUAL",
    "fechaInicioVigencia": "2025-06-01",
    "mesesVigencia": 12,
    "canonMensual": 1800000,
    "nombreAsegurado": "Laura Gómez",
    "documentoAsegurado": "1033445566",
    "direccionInmueble": "Calle 72 #10-45, Bogotá",
    "valorAsegurado": 21600000
  }' \
  http://localhost:8080/polizas
```

### Crear póliza colectiva
```bash
curl -X POST -H "x-api-key: 123456" \
  -H "Content-Type: application/json" \
  -d '{
    "tipo": "COLECTIVA",
    "fechaInicioVigencia": "2025-07-01",
    "mesesVigencia": 12,
    "canonMensual": 2500000,
    "nombreAsegurado": "Roberto Díaz",
    "documentoAsegurado": "1077889900",
    "direccionInmueble": "Avenida El Dorado #68-50, Bogotá",
    "valorAsegurado": 30000000
  }' \
  http://localhost:8080/polizas
```

### Listar pólizas vigentes
```bash
curl -H "x-api-key: 123456" http://localhost:8080/polizas?estado=VIGENTE
```

### Listar riesgos de una póliza
```bash
curl -H "x-api-key: 123456" http://localhost:8080/polizas/2/riesgos
```

### Renovar póliza
```bash
curl -X POST -H "x-api-key: 123456" http://localhost:8080/polizas/1/renovar
```

### Cancelar póliza
```bash
curl -X POST -H "x-api-key: 123456" http://localhost:8080/polizas/1/cancelar
```

### Agregar riesgo a póliza colectiva
```bash
curl -X POST -H "x-api-key: 123456" \
  -H "Content-Type: application/json" \
  -d '{
    "nombreAsegurado": "Ana García",
    "documentoAsegurado": "1055667788",
    "direccionInmueble": "Calle 50 #10-30, Medellín",
    "valorAsegurado": 20000000
  }' \
  http://localhost:8080/polizas/2/riesgos
```

### Cancelar un riesgo
```bash
curl -X POST -H "x-api-key: 123456" http://localhost:8080/riesgos/1/cancelar
```

### Mock CORE (endpoint directo)
```bash
curl -X POST -H "Content-Type: application/json" \
  -d '{"evento": "ACTUALIZACION", "polizaId": 555}' \
  http://localhost:8080/core-mock/evento
```

## Reglas de Negocio Implementadas

1. **Póliza individual = 1 riesgo:** No se permite agregar riesgos a pólizas individuales.
2. **No renovar canceladas:** Una póliza con estado CANCELADA no puede renovarse.
3. **Cancelación en cascada:** Al cancelar una póliza, todos sus riesgos pasan a CANCELADO.
4. **Validación de tipo:** Solo pólizas COLECTIVAS aceptan nuevos riesgos.
5. **Notificación al CORE:** Toda modificación de estado realiza un POST HTTP real al endpoint `/core-mock/evento`, simulando la comunicación con el CORE vía WebLogic. Si el CORE no responde, el error se captura sin afectar la operación principal.
6. **Riesgo obligatorio para individuales:** Al crear una póliza individual, los datos del asegurado son obligatorios.

## Notificaciones

El sistema incluye un servicio de notificaciones (`NotificacionService`) que se dispara automáticamente en:
- **Creación** de póliza → Notifica al tomador (email + SMS)
- **Renovación** de póliza → Notifica al tomador con canon anterior y nuevo
- **Cancelación** de póliza → Notifica al tomador y beneficiario (email)

Las notificaciones se registran en los logs de la aplicación (simulado). En producción se conectaría a un proveedor real (SES, SendGrid, Twilio).

## Estructura del Proyecto

```
src/main/java/com/seguros/polizas/
├── GestionPolizasApplication.java       # Clase principal
├── config/
│   ├── AppConfig.java                  # Bean RestTemplate
│   ├── DataInitializer.java             # Datos de prueba
│   └── SecurityFilter.java             # Validación x-api-key
├── controller/
│   ├── PolizaController.java           # Endpoints de pólizas
│   ├── RiesgoController.java           # Endpoints de riesgos
│   ├── CoreMockController.java         # Mock del CORE
│   └── dto/
│       ├── PolizaRequest.java          # DTO de entrada póliza (crear)
│       ├── PolizaResponse.java         # DTO de respuesta póliza
│       ├── RiesgoRequest.java          # DTO de entrada riesgo
│       └── RiesgoResponse.java         # DTO de respuesta riesgo
├── domain/
│   ├── entity/
│   │   ├── Poliza.java                 # Entidad Póliza (tabla: poliza)
│   │   └── Riesgo.java                 # Entidad Riesgo (tabla: riesgo)
│   └── enums/
│       ├── EstadoPoliza.java           # VIGENTE, RENOVADA, CANCELADA
│       ├── EstadoRiesgo.java           # ACTIVO, CANCELADO
│       └── TipoPoliza.java            # INDIVIDUAL, COLECTIVA
├── exception/
│   ├── BusinessException.java          # Excepciones de negocio
│   ├── GlobalExceptionHandler.java     # Manejo global de errores
│   └── ResourceNotFoundException.java  # Recurso no encontrado
├── repository/
│   ├── PolizaRepository.java           # Repositorio de pólizas
│   └── RiesgoRepository.java          # Repositorio de riesgos
└── service/
    ├── CoreMockService.java            # Servicio de integración CORE
    ├── NotificacionService.java        # Servicio de notificaciones (email/SMS)
    ├── PolizaService.java              # Lógica de negocio pólizas
    └── RiesgoService.java             # Lógica de negocio riesgos
```

## Base de Datos

Se utiliza H2 en memoria para desarrollo. Consola disponible en:
- URL: `http://localhost:8080/h2-console`
- JDBC URL: `jdbc:h2:mem:polizasdb`
- User: `sa`
- Password: (vacío)

## Datos de Prueba Precargados

| ID | Número | Tipo | Estado | Canon Mensual |
|----|--------|------|--------|---------------|
| 1 | POL-IND-001 | INDIVIDUAL | VIGENTE | $1,500,000 |
| 2 | POL-COL-001 | COLECTIVA | VIGENTE | $2,000,000 |
| 3 | POL-COL-002 | COLECTIVA | CANCELADA | $1,800,000 |

## IPC Configurado

El valor del IPC se configura en `application.yml` (actualmente 5.2%).
Al renovar, el nuevo canon = canon_actual × (1 + 5.2/100).
