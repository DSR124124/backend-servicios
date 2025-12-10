package com.nettalco.backendservicios.modules.transporte.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public class ConductorDetalleRequest {
    
    @NotNull(message = "El ID del usuario de gestión es obligatorio")
    @Positive(message = "El ID del usuario debe ser positivo")
    private Integer idUsuarioGestion;
    
    @NotBlank(message = "El número de licencia es obligatorio")
    @Size(max = 20, message = "El número de licencia no puede exceder 20 caracteres")
    private String licenciaNumero;
    
    @Size(max = 20, message = "La categoría no puede exceder 20 caracteres")
    private String categoria;
    
    @Size(max = 20, message = "El teléfono no puede exceder 20 caracteres")
    private String telefonoContacto;
    
    @Size(max = 20, message = "El estado no puede exceder 20 caracteres")
    private String estado;

    public ConductorDetalleRequest() {}

    public ConductorDetalleRequest(Integer idUsuarioGestion, String licenciaNumero, String categoria, 
                                   String telefonoContacto, String estado) {
        this.idUsuarioGestion = idUsuarioGestion;
        this.licenciaNumero = licenciaNumero;
        this.categoria = categoria;
        this.telefonoContacto = telefonoContacto;
        this.estado = estado;
    }

    public Integer getIdUsuarioGestion() {
        return idUsuarioGestion;
    }

    public void setIdUsuarioGestion(Integer idUsuarioGestion) {
        this.idUsuarioGestion = idUsuarioGestion;
    }

    public String getLicenciaNumero() {
        return licenciaNumero;
    }

    public void setLicenciaNumero(String licenciaNumero) {
        this.licenciaNumero = licenciaNumero;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public String getTelefonoContacto() {
        return telefonoContacto;
    }

    public void setTelefonoContacto(String telefonoContacto) {
        this.telefonoContacto = telefonoContacto;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }
}

