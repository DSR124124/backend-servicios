package com.nettalco.backendservicios.modules.transporte.dtos;

import java.time.OffsetDateTime;
import java.util.List;

/**
 * DTO para representar el viaje activo del conductor con informacion completa
 */
public record ViajeActivoConductorResponse(
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
    List<ParaderoInfo> paraderos
) {
    
    /**
     * Informacion de un paradero de la ruta
     */
    public record ParaderoInfo(
        Integer orden,
        String nombre,
        Double latitud,
        Double longitud
    ) {}
}
