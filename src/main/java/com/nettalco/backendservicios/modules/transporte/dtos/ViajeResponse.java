package com.nettalco.backendservicios.modules.transporte.dtos;

import java.time.OffsetDateTime;

public class ViajeResponse {
    private Integer idViaje;
    private Integer idRuta;
    private String nombreRuta;
    private Integer idBus;
    private String placaBus;
    private Integer idConductor;
    private OffsetDateTime fechaInicioProgramada;
    private OffsetDateTime fechaFinProgramada;
    private OffsetDateTime fechaInicioReal;
    private OffsetDateTime fechaFinReal;
    private String estado;

    public ViajeResponse() {}

    public ViajeResponse(Integer idViaje, Integer idRuta, String nombreRuta, Integer idBus, 
                        String placaBus, Integer idConductor, OffsetDateTime fechaInicioProgramada, 
                        OffsetDateTime fechaFinProgramada, OffsetDateTime fechaInicioReal, 
                        OffsetDateTime fechaFinReal, String estado) {
        this.idViaje = idViaje;
        this.idRuta = idRuta;
        this.nombreRuta = nombreRuta;
        this.idBus = idBus;
        this.placaBus = placaBus;
        this.idConductor = idConductor;
        this.fechaInicioProgramada = fechaInicioProgramada;
        this.fechaFinProgramada = fechaFinProgramada;
        this.fechaInicioReal = fechaInicioReal;
        this.fechaFinReal = fechaFinReal;
        this.estado = estado;
    }

    public Integer getIdViaje() {
        return idViaje;
    }

    public void setIdViaje(Integer idViaje) {
        this.idViaje = idViaje;
    }

    public Integer getIdRuta() {
        return idRuta;
    }

    public void setIdRuta(Integer idRuta) {
        this.idRuta = idRuta;
    }

    public String getNombreRuta() {
        return nombreRuta;
    }

    public void setNombreRuta(String nombreRuta) {
        this.nombreRuta = nombreRuta;
    }

    public Integer getIdBus() {
        return idBus;
    }

    public void setIdBus(Integer idBus) {
        this.idBus = idBus;
    }

    public String getPlacaBus() {
        return placaBus;
    }

    public void setPlacaBus(String placaBus) {
        this.placaBus = placaBus;
    }

    public Integer getIdConductor() {
        return idConductor;
    }

    public void setIdConductor(Integer idConductor) {
        this.idConductor = idConductor;
    }

    public OffsetDateTime getFechaInicioProgramada() {
        return fechaInicioProgramada;
    }

    public void setFechaInicioProgramada(OffsetDateTime fechaInicioProgramada) {
        this.fechaInicioProgramada = fechaInicioProgramada;
    }

    public OffsetDateTime getFechaFinProgramada() {
        return fechaFinProgramada;
    }

    public void setFechaFinProgramada(OffsetDateTime fechaFinProgramada) {
        this.fechaFinProgramada = fechaFinProgramada;
    }

    public OffsetDateTime getFechaInicioReal() {
        return fechaInicioReal;
    }

    public void setFechaInicioReal(OffsetDateTime fechaInicioReal) {
        this.fechaInicioReal = fechaInicioReal;
    }

    public OffsetDateTime getFechaFinReal() {
        return fechaFinReal;
    }

    public void setFechaFinReal(OffsetDateTime fechaFinReal) {
        this.fechaFinReal = fechaFinReal;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }
}

