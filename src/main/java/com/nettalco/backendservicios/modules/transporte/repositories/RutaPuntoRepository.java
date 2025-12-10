package com.nettalco.backendservicios.modules.transporte.repositories;

import com.nettalco.backendservicios.modules.transporte.entities.RutaPunto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RutaPuntoRepository extends JpaRepository<RutaPunto, Integer> {
    
    /**
     * Obtiene todos los puntos de una ruta ordenados por orden
     */
    @Query("SELECT p FROM RutaPunto p WHERE p.ruta.idRuta = :idRuta ORDER BY p.orden ASC")
    List<RutaPunto> findByRutaIdOrderByOrden(@Param("idRuta") Integer idRuta);
    
    /**
     * Verifica si un paradero pertenece a una ruta
     */
    @Query("SELECT COUNT(p) > 0 FROM RutaPunto p WHERE p.idPunto = :idPunto AND p.ruta.idRuta = :idRuta")
    boolean existsByParaderoIdAndRutaId(@Param("idPunto") Integer idPunto, @Param("idRuta") Integer idRuta);
}

