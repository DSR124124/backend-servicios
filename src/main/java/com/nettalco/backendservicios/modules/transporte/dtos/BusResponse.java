package com.nettalco.backendservicios.modules.transporte.dtos;

public class BusResponse {
    private Integer idBus;
    private String placa;
    private String modelo;
    private Integer capacidad;
    private String imeiGps;
    private String estado;

    public BusResponse() {}

    public BusResponse(Integer idBus, String placa, String modelo, Integer capacidad, String imeiGps, String estado) {
        this.idBus = idBus;
        this.placa = placa;
        this.modelo = modelo;
        this.capacidad = capacidad;
        this.imeiGps = imeiGps;
        this.estado = estado;
    }

    public Integer getIdBus() {
        return idBus;
    }

    public void setIdBus(Integer idBus) {
        this.idBus = idBus;
    }

    public String getPlaca() {
        return placa;
    }

    public void setPlaca(String placa) {
        this.placa = placa;
    }

    public String getModelo() {
        return modelo;
    }

    public void setModelo(String modelo) {
        this.modelo = modelo;
    }

    public Integer getCapacidad() {
        return capacidad;
    }

    public void setCapacidad(Integer capacidad) {
        this.capacidad = capacidad;
    }

    public String getImeiGps() {
        return imeiGps;
    }

    public void setImeiGps(String imeiGps) {
        this.imeiGps = imeiGps;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }
}

