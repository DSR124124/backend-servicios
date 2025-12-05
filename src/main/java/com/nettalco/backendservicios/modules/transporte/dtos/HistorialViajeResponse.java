package com.nettalco.backendservicios.modules.transporte.dtos;

import java.time.OffsetDateTime;

/**
 * DTO para representar un viaje en el historial del conductor
 */
public record HistorialViajeResponse(
    Integer idViaje,
    Integer idRuta,
    String nombreRuta,
    String busPlaca,
    String busModelo,
    String estado,
    OffsetDateTime fechaInicioProgramada,
    OffsetDateTime fechaFinProgramada,
    OffsetDateTime fechaInicioReal,
    OffsetDateTime fechaFinReal,
    Long duracionMinutos,
    Integer totalParaderos
) {}
