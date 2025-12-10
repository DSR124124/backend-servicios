package com.nettalco.backendservicios.modules.transporte.dtos;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.time.OffsetDateTime;

public class ViajeRequest {
    
    @NotNull(message = "El ID de la ruta es obligatorio")
    @Positive(message = "El ID de la ruta debe ser positivo")
    private Integer idRuta;
    
    @NotNull(message = "El ID del bus es obligatorio")
    @Positive(message = "El ID del bus debe ser positivo")
    private Integer idBus;
    
    @NotNull(message = "El ID del conductor es obligatorio")
    @Positive(message = "El ID del conductor debe ser positivo")
    private Integer idConductor;
    
    private OffsetDateTime fechaInicioProgramada;
    
    private OffsetDateTime fechaFinProgramada;
    
    private String estado;

    public ViajeRequest() {}

    public ViajeRequest(Integer idRuta, Integer idBus, Integer idConductor, 
                       OffsetDateTime fechaInicioProgramada, OffsetDateTime fechaFinProgramada, 
                       String estado) {
        this.idRuta = idRuta;
        this.idBus = idBus;
        this.idConductor = idConductor;
        this.fechaInicioProgramada = fechaInicioProgramada;
        this.fechaFinProgramada = fechaFinProgramada;
        this.estado = estado;
    }

    public Integer getIdRuta() {
        return idRuta;
    }

    public void setIdRuta(Integer idRuta) {
        this.idRuta = idRuta;
    }

    public Integer getIdBus() {
        return idBus;
    }

    public void setIdBus(Integer idBus) {
        this.idBus = idBus;
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

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }
}

