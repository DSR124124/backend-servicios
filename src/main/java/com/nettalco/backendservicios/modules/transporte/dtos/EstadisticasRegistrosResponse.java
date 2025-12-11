package com.nettalco.backendservicios.modules.transporte.dtos;

import java.util.List;

/**
 * DTO para estadísticas de aglomeración - Análisis de flujo de personas
 */
public record EstadisticasRegistrosResponse(
    // KPIs principales
    Long totalPersonasRegistradas,
    Long personasRegistradasHoy,
    
    // 1. MAPA DE CALOR (Heatmap): Hora vs Paradero
    List<DatoHeatmap> datosHeatmap,
    
    // 2. GRÁFICO DE BARRAS APILADAS: Ruta con segmentos por Paradero
    List<DatoStackedBar> datosStackedBar,
    
    // 3. GRÁFICO DE LÍNEAS MULTISERIE: Hora vs Cantidad por Ruta
    List<DatoLineChart> datosLineChart
) {
    /**
     * Datos para Heatmap: Hora del día vs Paradero
     */
    public record DatoHeatmap(
        Integer hora,           // 0-23
        Integer idParadero,
        String nombreParadero,
        Long cantidadUsuarios
    ) {}
    
    /**
     * Datos para Stacked Bar: Ruta con segmentos por Paradero
     */
    public record DatoStackedBar(
        Integer idRuta,
        String nombreRuta,
        List<SegmentoParadero> segmentosParaderos,
        Long totalUsuarios
    ) {
        public record SegmentoParadero(
            Integer idParadero,
            String nombreParadero,
            Long cantidadUsuarios
        ) {}
    }
    
    /**
     * Datos para Line Chart: Hora vs Cantidad por Ruta
     */
    public record DatoLineChart(
        Integer hora,           // 0-23
        List<ValorPorRuta> valoresPorRuta
    ) {
        public record ValorPorRuta(
            Integer idRuta,
            String nombreRuta,
            Long cantidadUsuarios
        ) {}
    }
}
