package com.nettalco.backendservicios.modules.transporte.dtos;

import java.time.OffsetDateTime;

/**
 * DTO para la respuesta al finalizar un viaje
 */
public record FinalizarViajeResponse(
    Integer idViaje,
    String estado,
    OffsetDateTime fechaInicioReal,
    OffsetDateTime fechaFinReal,
    Long duracionMinutos,
    String mensaje
) {}
