package com.nettalco.backendservicios.modules.transporte.dtos;

public class ConductorDetalleResponse {
    private Integer idUsuarioGestion;
    private String licenciaNumero;
    private String categoria;
    private String telefonoContacto;
    private String estado;

    public ConductorDetalleResponse() {}

    public ConductorDetalleResponse(Integer idUsuarioGestion, String licenciaNumero, String categoria, 
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

