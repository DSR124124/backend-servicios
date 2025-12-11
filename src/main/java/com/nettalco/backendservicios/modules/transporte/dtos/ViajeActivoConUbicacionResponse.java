package com.nettalco.backendservicios.modules.transporte.dtos;

import java.time.OffsetDateTime;

/**
 * DTO para representar un viaje activo con su ubicación en tiempo real
 */
public record ViajeActivoConUbicacionResponse(
    Integer idViaje,
    Integer idRuta,
    String nombreRuta,
    String busPlate,
    String busModel,
    String driverName,
    String estado,
    OffsetDateTime fechaInicioProgramada,
    OffsetDateTime fechaFinProgramada,
    // Ubicación en tiempo real
    Double latitude,
    Double longitude,
    Double heading,
    Double speed,
    OffsetDateTime lastUpdate
) {}

