package com.nettalco.backendservicios.modules.transporte.dtos;

/**
 * DTO para la respuesta con información del próximo paradero a visitar.
 * Se usa para guiar la navegación del conductor en orden.
 */
public record ProximoParaderoResponse(
    Integer idViaje,
    Integer idParadero,
    Integer ordenParadero,
    String nombreParadero,
    Double latitud,
    Double longitud,
    Integer paraderosVisitados,
    Integer totalParaderos,
    Boolean todosVisitados,
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
