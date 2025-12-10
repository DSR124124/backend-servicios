package com.nettalco.backendservicios.modules.transporte.dtos;

import jakarta.validation.constraints.NotNull;

/**
 * DTO para registrar la selección de ruta y paradero por un usuario
 */
public class RegistroUsuarioRutaRequest {
    
    @NotNull(message = "El ID de la ruta es requerido")
    private Integer idRuta;
    
    @NotNull(message = "El ID del paradero es requerido")
    private Integer idParadero;
    
    private String observacion;
    
    // Constructors
    public RegistroUsuarioRutaRequest() {}
    
    public RegistroUsuarioRutaRequest(Integer idRuta, Integer idParadero, String observacion) {
        this.idRuta = idRuta;
        this.idParadero = idParadero;
        this.observacion = observacion;
    }
    
    // Getters and Setters
    public Integer getIdRuta() {
        return idRuta;
    }
    
    public void setIdRuta(Integer idRuta) {
        this.idRuta = idRuta;
    }
    
    public Integer getIdParadero() {
        return idParadero;
    }
    
    public void setIdParadero(Integer idParadero) {
        this.idParadero = idParadero;
    }
    
    public String getObservacion() {
        return observacion;
    }
    
    public void setObservacion(String observacion) {
        this.observacion = observacion;
    }
}

