package com.nettalco.backendservicios.modules.transporte.dtos;

import java.time.OffsetDateTime;

/**
 * DTO para representar un viaje desde la perspectiva del conductor
 */
public record ViajeConductorResponse(
    Integer idViaje,
    Integer idRuta,
    String nombreRuta,
    String descripcionRuta,
    String busPlaca,
    String busModelo,
    Integer busCapacidad,
    String estado,
    OffsetDateTime fechaInicioProgramada,
    OffsetDateTime fechaFinProgramada,
    OffsetDateTime fechaInicioReal,
    OffsetDateTime fechaFinReal,
    Integer totalParaderos
) {}
