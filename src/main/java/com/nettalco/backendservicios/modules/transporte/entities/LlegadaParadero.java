package com.nettalco.backendservicios.modules.transporte.entities;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.Objects;

/**
 * Entidad para registrar las llegadas de un conductor a los paraderos durante un viaje.
 * Permite hacer tracking del progreso del viaje y calcular tiempos de llegada reales.
 */
@Entity
@Table(name = "llegadas_paraderos", indexes = {
    @Index(name = "idx_llegada_viaje", columnList = "id_viaje"),
    @Index(name = "idx_llegada_paradero", columnList = "id_punto"),
    @Index(name = "idx_llegada_fecha", columnList = "fecha_llegada")
})
public class LlegadaParadero {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_llegada")
    private Integer idLlegada;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_viaje", nullable = false)
    private Viaje viaje;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_punto", nullable = false)
    private RutaPunto paradero;
    
    @Column(name = "fecha_llegada", nullable = false)
    private OffsetDateTime fechaLlegada;
    
    @Column(name = "latitud_llegada", precision = 10, scale = 8)
    private BigDecimal latitudLlegada;
    
    @Column(name = "longitud_llegada", precision = 11, scale = 8)
    private BigDecimal longitudLlegada;
    
    @Column(name = "distancia_paradero")
    private Double distanciaParadero; // Distancia en metros desde la ubicacion de llegada al paradero
    
    @Column(name = "observacion", length = 500)
    private String observacion;
    
    @PrePersist
    protected void onCreate() {
        if (fechaLlegada == null) {
            fechaLlegada = OffsetDateTime.now();
        }
    }
    
    // Constructors
    public LlegadaParadero() {}
    
    public LlegadaParadero(Viaje viaje, RutaPunto paradero, OffsetDateTime fechaLlegada,
                          BigDecimal latitudLlegada, BigDecimal longitudLlegada) {
        this.viaje = viaje;
        this.paradero = paradero;
        this.fechaLlegada = fechaLlegada;
        this.latitudLlegada = latitudLlegada;
        this.longitudLlegada = longitudLlegada;
    }
    
    // Getters and Setters
    public Integer getIdLlegada() {
        return idLlegada;
    }
    
    public void setIdLlegada(Integer idLlegada) {
        this.idLlegada = idLlegada;
    }
    
    public Viaje getViaje() {
        return viaje;
    }
    
    public void setViaje(Viaje viaje) {
        this.viaje = viaje;
    }
    
    public RutaPunto getParadero() {
        return paradero;
    }
    
    public void setParadero(RutaPunto paradero) {
        this.paradero = paradero;
    }
    
    public OffsetDateTime getFechaLlegada() {
        return fechaLlegada;
    }
    
    public void setFechaLlegada(OffsetDateTime fechaLlegada) {
        this.fechaLlegada = fechaLlegada;
    }
    
    public BigDecimal getLatitudLlegada() {
        return latitudLlegada;
    }
    
    public void setLatitudLlegada(BigDecimal latitudLlegada) {
        this.latitudLlegada = latitudLlegada;
    }
    
    public BigDecimal getLongitudLlegada() {
        return longitudLlegada;
    }
    
    public void setLongitudLlegada(BigDecimal longitudLlegada) {
        this.longitudLlegada = longitudLlegada;
    }
    
    public Double getDistanciaParadero() {
        return distanciaParadero;
    }
    
    public void setDistanciaParadero(Double distanciaParadero) {
        this.distanciaParadero = distanciaParadero;
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
        LlegadaParadero that = (LlegadaParadero) o;
        return Objects.equals(idLlegada, that.idLlegada);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(idLlegada);
    }
}
