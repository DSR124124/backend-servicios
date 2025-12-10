package com.nettalco.backendservicios.modules.transporte.entities;

import jakarta.persistence.*;
import java.time.OffsetDateTime;
import java.util.Objects;

@Entity
@Table(name = "registros_usuarios_rutas", indexes = {
    @Index(name = "idx_registro_usuario", columnList = "id_usuario"),
    @Index(name = "idx_registro_ruta", columnList = "id_ruta"),
    @Index(name = "idx_registro_fecha", columnList = "fecha_registro")
})
public class RegistroUsuarioRuta {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_registro")
    private Integer idRegistro;
    
    @Column(name = "id_usuario", nullable = false)
    private Integer idUsuario; // FK lógica hacia usuarios en BD Gestión
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_ruta", nullable = false)
    private Ruta ruta;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_paradero", nullable = false)
    private RutaPunto paradero;
    
    @Column(name = "fecha_registro", nullable = false)
    private OffsetDateTime fechaRegistro;
    
    @Column(name = "observacion", length = 500)
    private String observacion;
    
    @PrePersist
    protected void onCreate() {
        if (fechaRegistro == null) {
            fechaRegistro = OffsetDateTime.now();
        }
    }
    
    // Constructors
    public RegistroUsuarioRuta() {}
    
    public RegistroUsuarioRuta(Integer idUsuario, Ruta ruta, RutaPunto paradero, 
                              OffsetDateTime fechaRegistro, String observacion) {
        this.idUsuario = idUsuario;
        this.ruta = ruta;
        this.paradero = paradero;
        this.fechaRegistro = fechaRegistro;
        this.observacion = observacion;
    }
    
    // Getters and Setters
    public Integer getIdRegistro() {
        return idRegistro;
    }
    
    public void setIdRegistro(Integer idRegistro) {
        this.idRegistro = idRegistro;
    }
    
    public Integer getIdUsuario() {
        return idUsuario;
    }
    
    public void setIdUsuario(Integer idUsuario) {
        this.idUsuario = idUsuario;
    }
    
    public Ruta getRuta() {
        return ruta;
    }
    
    public void setRuta(Ruta ruta) {
        this.ruta = ruta;
    }
    
    public RutaPunto getParadero() {
        return paradero;
    }
    
    public void setParadero(RutaPunto paradero) {
        this.paradero = paradero;
    }
    
    public OffsetDateTime getFechaRegistro() {
        return fechaRegistro;
    }
    
    public void setFechaRegistro(OffsetDateTime fechaRegistro) {
        this.fechaRegistro = fechaRegistro;
    }
    
    public String getObservacion() {
        return observacion;
    }
    
    public void setObservacion(String observacion) {
        this.observacion = observacion;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RegistroUsuarioRuta that = (RegistroUsuarioRuta) o;
        return Objects.equals(idRegistro, that.idRegistro);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(idRegistro);
    }
}

