package com.nettalco.backendservicios.modules.transporte.dtos;

import java.time.OffsetDateTime;

/**
 * DTO para la respuesta de marcar llegada a un paradero
 */
public record LlegadaParaderoResponse(
    Integer idLlegada,
    Integer idViaje,
    Integer idParadero,
    Integer ordenParadero,
    String nombreParadero,
    OffsetDateTime fechaLlegada,
    Double latitudLlegada,
    Double longitudLlegada,
    Integer paraderosVisitados,
    Integer totalParaderos,
    Boolean esUltimoParadero,
    String mensaje
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
}
