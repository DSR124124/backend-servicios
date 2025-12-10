package com.nettalco.backendservicios.modules.transporte.dtos;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;

public class RutaPuntoRequest {
    
    @NotNull(message = "El ID de la ruta es obligatorio")
    @Positive(message = "El ID de la ruta debe ser positivo")
    private Integer idRuta;
    
    @NotNull(message = "El orden es obligatorio")
    @Positive(message = "El orden debe ser positivo")
    private Integer orden;
    
    @NotNull(message = "La latitud es obligatoria")
    private BigDecimal latitud;
    
    @NotNull(message = "La longitud es obligatoria")
    private BigDecimal longitud;
    
    private String nombreParadero;
    
    private Boolean esParaderoOficial;

    public RutaPuntoRequest() {}

    public RutaPuntoRequest(Integer idRuta, Integer orden, BigDecimal latitud, BigDecimal longitud, 
                            String nombreParadero, Boolean esParaderoOficial) {
        this.idRuta = idRuta;
        this.orden = orden;
        this.latitud = latitud;
        this.longitud = longitud;
        this.nombreParadero = nombreParadero;
        this.esParaderoOficial = esParaderoOficial;
    }

    public Integer getIdRuta() {
        return idRuta;
    }

    public void setIdRuta(Integer idRuta) {
        this.idRuta = idRuta;
    }

    public Integer getOrden() {
        return orden;
    }

    public void setOrden(Integer orden) {
        this.orden = orden;
    }

    public BigDecimal getLatitud() {
        return latitud;
    }

    public void setLatitud(BigDecimal latitud) {
        this.latitud = latitud;
    }

    public BigDecimal getLongitud() {
        return longitud;
    }

    public void setLongitud(BigDecimal longitud) {
        this.longitud = longitud;
    }

    public String getNombreParadero() {
        return nombreParadero;
    }

    public void setNombreParadero(String nombreParadero) {
        this.nombreParadero = nombreParadero;
    }

    public Boolean getEsParaderoOficial() {
        return esParaderoOficial;
    }

    public void setEsParaderoOficial(Boolean esParaderoOficial) {
        this.esParaderoOficial = esParaderoOficial;
    }
}

