package com.nettalco.backendservicios.modules.transporte.dtos;

/**
 * DTO que combina información del conductor con datos del usuario del sistema de gestión
 */
public class ConductorCompletoResponse {
    private Integer idUsuarioGestion;
    private String licenciaNumero;
    private String categoria;
    private String telefonoContacto;
    private String estado;
    
    // Datos del usuario desde backend-gestion
    private String username;
    private String email;
    private String nombreCompleto;
    private Integer idRol;
    private String nombreRol;
    private Boolean usuarioActivo;

    public ConductorCompletoResponse() {}

    public ConductorCompletoResponse(Integer idUsuarioGestion, String licenciaNumero, String categoria,
                                    String telefonoContacto, String estado, String username, String email,
                                    String nombreCompleto, Integer idRol, String nombreRol, Boolean usuarioActivo) {
        this.idUsuarioGestion = idUsuarioGestion;
        this.licenciaNumero = licenciaNumero;
        this.categoria = categoria;
        this.telefonoContacto = telefonoContacto;
        this.estado = estado;
        this.username = username;
        this.email = email;
        this.nombreCompleto = nombreCompleto;
        this.idRol = idRol;
        this.nombreRol = nombreRol;
        this.usuarioActivo = usuarioActivo;
    }

    // Getters and Setters
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

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getNombreCompleto() {
        return nombreCompleto;
    }

    public void setNombreCompleto(String nombreCompleto) {
        this.nombreCompleto = nombreCompleto;
    }

    public Integer getIdRol() {
        return idRol;
    }

    public void setIdRol(Integer idRol) {
        this.idRol = idRol;
    }

    public String getNombreRol() {
        return nombreRol;
    }

    public void setNombreRol(String nombreRol) {
        this.nombreRol = nombreRol;
    }

    public Boolean getUsuarioActivo() {
        return usuarioActivo;
    }

    public void setUsuarioActivo(Boolean usuarioActivo) {
        this.usuarioActivo = usuarioActivo;
    }
}

