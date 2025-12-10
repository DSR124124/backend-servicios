package com.nettalco.backendservicios.modules.transporte.dtos;

import java.util.Map;

public class EstadisticasGeneralesResponse {
    private Long totalRutas;
    private Long totalBuses;
    private Long totalConductores;
    private Long totalViajes;
    private Long viajesActivos;
    private Long totalRegistrosUsuarios;
    private Map<String, Long> busesPorEstado;
    private Map<String, Long> viajesPorEstado;
    private Map<String, Long> registrosPorRuta;

    public EstadisticasGeneralesResponse() {}

    public EstadisticasGeneralesResponse(Long totalRutas, Long totalBuses, Long totalConductores, 
                                        Long totalViajes, Long viajesActivos, Long totalRegistrosUsuarios,
                                        Map<String, Long> busesPorEstado, Map<String, Long> viajesPorEstado,
                                        Map<String, Long> registrosPorRuta) {
        this.totalRutas = totalRutas;
        this.totalBuses = totalBuses;
        this.totalConductores = totalConductores;
        this.totalViajes = totalViajes;
        this.viajesActivos = viajesActivos;
        this.totalRegistrosUsuarios = totalRegistrosUsuarios;
        this.busesPorEstado = busesPorEstado;
        this.viajesPorEstado = viajesPorEstado;
        this.registrosPorRuta = registrosPorRuta;
    }

    public Long getTotalRutas() {
        return totalRutas;
    }

    public void setTotalRutas(Long totalRutas) {
        this.totalRutas = totalRutas;
    }

    public Long getTotalBuses() {
        return totalBuses;
    }

    public void setTotalBuses(Long totalBuses) {
        this.totalBuses = totalBuses;
    }

    public Long getTotalConductores() {
        return totalConductores;
    }

    public void setTotalConductores(Long totalConductores) {
        this.totalConductores = totalConductores;
    }

    public Long getTotalViajes() {
        return totalViajes;
    }

    public void setTotalViajes(Long totalViajes) {
        this.totalViajes = totalViajes;
    }

    public Long getViajesActivos() {
        return viajesActivos;
    }

    public void setViajesActivos(Long viajesActivos) {
        this.viajesActivos = viajesActivos;
    }

    public Long getTotalRegistrosUsuarios() {
        return totalRegistrosUsuarios;
    }

    public void setTotalRegistrosUsuarios(Long totalRegistrosUsuarios) {
        this.totalRegistrosUsuarios = totalRegistrosUsuarios;
    }

    public Map<String, Long> getBusesPorEstado() {
        return busesPorEstado;
    }

    public void setBusesPorEstado(Map<String, Long> busesPorEstado) {
        this.busesPorEstado = busesPorEstado;
    }

    public Map<String, Long> getViajesPorEstado() {
        return viajesPorEstado;
    }

    public void setViajesPorEstado(Map<String, Long> viajesPorEstado) {
        this.viajesPorEstado = viajesPorEstado;
    }

    public Map<String, Long> getRegistrosPorRuta() {
        return registrosPorRuta;
    }

    public void setRegistrosPorRuta(Map<String, Long> registrosPorRuta) {
        this.registrosPorRuta = registrosPorRuta;
    }
}

