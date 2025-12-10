package com.nettalco.backendservicios.modules.transporte.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public class BusRequest {
    
    @NotBlank(message = "La placa es obligatoria")
    @Size(max = 10, message = "La placa no puede exceder 10 caracteres")
    private String placa;
    
    @Size(max = 50, message = "El modelo no puede exceder 50 caracteres")
    private String modelo;
    
    @Positive(message = "La capacidad debe ser un número positivo")
    private Integer capacidad;
    
    @Size(max = 50, message = "El IMEI GPS no puede exceder 50 caracteres")
    private String imeiGps;
    
    @Size(max = 20, message = "El estado no puede exceder 20 caracteres")
    private String estado;

    public BusRequest() {}

    public BusRequest(String placa, String modelo, Integer capacidad, String imeiGps, String estado) {
        this.placa = placa;
        this.modelo = modelo;
        this.capacidad = capacidad;
        this.imeiGps = imeiGps;
        this.estado = estado;
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

