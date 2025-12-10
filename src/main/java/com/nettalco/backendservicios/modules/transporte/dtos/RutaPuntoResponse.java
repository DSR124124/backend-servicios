package com.nettalco.backendservicios.modules.transporte.dtos;

import java.math.BigDecimal;

public class RutaPuntoResponse {
    private Integer idPunto;
    private Integer idRuta;
    private String nombreRuta;
    private Integer orden;
    private BigDecimal latitud;
    private BigDecimal longitud;
    private String nombreParadero;
    private Boolean esParaderoOficial;

    public RutaPuntoResponse() {}

    public RutaPuntoResponse(Integer idPunto, Integer idRuta, String nombreRuta, Integer orden, 
                            BigDecimal latitud, BigDecimal longitud, String nombreParadero, 
                            Boolean esParaderoOficial) {
        this.idPunto = idPunto;
        this.idRuta = idRuta;
        this.nombreRuta = nombreRuta;
        this.orden = orden;
        this.latitud = latitud;
        this.longitud = longitud;
        this.nombreParadero = nombreParadero;
        this.esParaderoOficial = esParaderoOficial;
    }

    public Integer getIdPunto() {
        return idPunto;
    }

    public void setIdPunto(Integer idPunto) {
        this.idPunto = idPunto;
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

