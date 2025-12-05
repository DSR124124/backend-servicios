package com.nettalco.backendservicios.modules.transporte.repositories;

import com.nettalco.backendservicios.modules.transporte.entities.LlegadaParadero;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LlegadaParaderoRepository extends JpaRepository<LlegadaParadero, Integer> {
    
    @Query("SELECT l FROM LlegadaParadero l " +
           "LEFT JOIN FETCH l.paradero " +
           "WHERE l.viaje.idViaje = :idViaje " +
           "ORDER BY l.paradero.orden ASC")
    List<LlegadaParadero> findByViajeIdOrderByOrden(@Param("idViaje") Integer idViaje);
    
    @Query("SELECT COUNT(l) > 0 FROM LlegadaParadero l " +
           "WHERE l.viaje.idViaje = :idViaje " +
           "AND l.paradero.idPunto = :idParadero")
    boolean existsByViajeIdAndParaderoId(
        @Param("idViaje") Integer idViaje,
        @Param("idParadero") Integer idParadero
    );
    
    @Query("SELECT l FROM LlegadaParadero l " +
           "LEFT JOIN FETCH l.paradero " +
           "WHERE l.viaje.idViaje = :idViaje " +
           "AND l.paradero.idPunto = :idParadero")
    Optional<LlegadaParadero> findByViajeIdAndParaderoId(
        @Param("idViaje") Integer idViaje,
        @Param("idParadero") Integer idParadero
    );
    
    @Query("SELECT COUNT(l) FROM LlegadaParadero l " +
           "WHERE l.viaje.idViaje = :idViaje")
    Long countByViajeId(@Param("idViaje") Integer idViaje);
    
    @Query("SELECT l FROM LlegadaParadero l " +
           "LEFT JOIN FETCH l.paradero " +
           "WHERE l.viaje.idViaje = :idViaje " +
           "ORDER BY l.fechaLlegada DESC")
    List<LlegadaParadero> findUltimaLlegadaByViajeId(@Param("idViaje") Integer idViaje);
    
    /**
     * Obtiene el máximo orden de paradero visitado para un viaje.
     * Retorna null si no hay paraderos visitados.
     */
    @Query("SELECT MAX(l.paradero.orden) FROM LlegadaParadero l " +
           "WHERE l.viaje.idViaje = :idViaje")
    Integer findMaxOrdenVisitadoByViajeId(@Param("idViaje") Integer idViaje);
    
    /**
     * Verifica si existe una llegada registrada para un paradero con un orden específico
     */
    @Query("SELECT COUNT(l) > 0 FROM LlegadaParadero l " +
           "WHERE l.viaje.idViaje = :idViaje " +
           "AND l.paradero.orden = :orden")
    boolean existsByViajeIdAndOrden(
        @Param("idViaje") Integer idViaje,
        @Param("orden") Integer orden
    );
}
