package com.nettalco.backendservicios.modules.transporte.dtos;

/**
 * DTO para las estadisticas del conductor
 */
public record EstadisticasConductorResponse(
    // Totales generales
    Integer totalViajes,
    Integer viajesCompletados,
    Integer viajesCancelados,
    Integer viajesEnCurso,
    Integer viajesProgramados,
    
    // Tiempo
    Long totalMinutosConducidos,
    Double promedioMinutosPorViaje,
    
    // Este mes
    Integer viajesEsteMes,
    Long minutosEsteMes,
    
    // Esta semana
    Integer viajesEstaSemana,
    Long minutosEstaSemana,
    
    // Hoy
    Integer viajesHoy,
    Long minutosHoy
) {}
