package com.nettalco.backendservicios.modules.transporte.dtos;

import java.time.OffsetDateTime;
import java.util.List;

/**
 * DTO para representar el viaje activo del conductor con informacion completa.
 * Incluye el estado de navegación por paraderos.
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
    List<ParaderoInfo> paraderos,
    Integer paraderosVisitados,
    Integer totalParaderos
) {
    
    /**
     * Calcula el progreso del viaje en porcentaje
     */
    public Double getProgresoViaje() {
        if (totalParaderos == null || totalParaderos == 0) {
            return 0.0;
        }
        return (paraderosVisitados.doubleValue() / totalParaderos.doubleValue()) * 100;
    }
    
    /**
     * Informacion de un paradero de la ruta con estado de visita.
     * Estados posibles:
     * - "visitado": El conductor ya marcó llegada a este paradero
     * - "siguiente": Es el próximo paradero a visitar (para navegación)
     * - "pendiente": Aún no se ha llegado a este paradero
     */
    public record ParaderoInfo(
        Integer idPunto,
        Integer orden,
        String nombre,
        Double latitud,
        Double longitud,
        String estadoParadero
    ) {}
}
