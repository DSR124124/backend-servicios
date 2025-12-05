package com.nettalco.backendservicios.modules.transporte.dtos;

import java.time.OffsetDateTime;

/**
 * DTO para la respuesta al iniciar un viaje
 */
public record IniciarViajeResponse(
    Integer idViaje,
    String estado,
    OffsetDateTime fechaInicioReal,
    String mensaje
) {}
