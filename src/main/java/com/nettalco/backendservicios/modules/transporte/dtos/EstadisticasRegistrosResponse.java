package com.nettalco.backendservicios.modules.transporte.dtos;

import java.util.List;

/**
 * DTO para estadísticas de registros de usuarios rutas
 */
public record EstadisticasRegistrosResponse(
    // KPIs principales
    Long totalRegistros,
    Long registrosHoy,
    Long registrosEstaSemana,
    Long registrosEsteMes,
    
    // Estadísticas por ruta
    List<RegistroPorRuta> registrosPorRuta,
    
    // Estadísticas por paradero
    List<RegistroPorParadero> registrosPorParadero,
    
    // Estadísticas temporales
    List<RegistroPorFecha> registrosPorDia,
    List<RegistroPorFecha> registrosPorSemana,
    List<RegistroPorFecha> registrosPorMes,
    
    // Top usuarios
    List<RegistroPorUsuario> topUsuarios,
    
    // Estadísticas generales
    Double promedioRegistrosPorDia,
    Integer rutasMasUsadas,
    Integer paraderosMasUsados
) {
    public record RegistroPorRuta(
        Integer idRuta,
        String nombreRuta,
        Long cantidadRegistros,
        Double porcentaje
    ) {}
    
    public record RegistroPorParadero(
        Integer idParadero,
        String nombreParadero,
        Integer idRuta,
        String nombreRuta,
        Long cantidadRegistros,
        Double porcentaje
    ) {}
    
    public record RegistroPorFecha(
        String fecha,
        String periodo, // "dia", "semana", "mes"
        Long cantidadRegistros
    ) {}
    
    public record RegistroPorUsuario(
        Integer idUsuario,
        Long cantidadRegistros,
        Double porcentaje
    ) {}
}

