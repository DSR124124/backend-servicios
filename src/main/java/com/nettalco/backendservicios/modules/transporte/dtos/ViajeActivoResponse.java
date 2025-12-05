package com.nettalco.backendservicios.modules.transporte.dtos;

import java.time.OffsetDateTime;

/**
 * DTO para representar un viaje activo simplificado
 */
public record ViajeActivoResponse(
    Integer idViaje,
    Integer idRuta,
    String busPlate,
    String busModel,
    String driverName,
    String estado,
    OffsetDateTime fechaInicioProgramada,
    OffsetDateTime fechaFinProgramada
) {}
