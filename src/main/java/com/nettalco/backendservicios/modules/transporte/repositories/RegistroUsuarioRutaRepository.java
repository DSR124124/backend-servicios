package com.nettalco.backendservicios.modules.transporte.repositories;

import com.nettalco.backendservicios.modules.transporte.entities.RegistroUsuarioRuta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.OffsetDateTime;
import java.util.List;

@Repository
public interface RegistroUsuarioRutaRepository extends JpaRepository<RegistroUsuarioRuta, Integer> {
    
    /**
     * Obtiene los registros de un usuario ordenados por fecha descendente
     */
    @Query("SELECT r FROM RegistroUsuarioRuta r WHERE r.idUsuario = :idUsuario ORDER BY r.fechaRegistro DESC")
    List<RegistroUsuarioRuta> findByUsuarioIdOrderByFechaDesc(@Param("idUsuario") Integer idUsuario);
    
    /**
     * Obtiene los registros de una ruta específica
     */
    @Query("SELECT r FROM RegistroUsuarioRuta r WHERE r.ruta.idRuta = :idRuta ORDER BY r.fechaRegistro DESC")
    List<RegistroUsuarioRuta> findByRutaIdOrderByFechaDesc(@Param("idRuta") Integer idRuta);
    
    /**
     * Obtiene los registros de un paradero específico
     */
    @Query("SELECT r FROM RegistroUsuarioRuta r WHERE r.paradero.idPunto = :idParadero ORDER BY r.fechaRegistro DESC")
    List<RegistroUsuarioRuta> findByParaderoIdOrderByFechaDesc(@Param("idParadero") Integer idParadero);
    
    /**
     * Cuenta los registros de un usuario en un rango de fechas
     */
    @Query("SELECT COUNT(r) FROM RegistroUsuarioRuta r WHERE r.idUsuario = :idUsuario " +
           "AND r.fechaRegistro >= :fechaDesde AND r.fechaRegistro <= :fechaHasta")
    Long countByUsuarioAndFechaBetween(
        @Param("idUsuario") Integer idUsuario,
        @Param("fechaDesde") OffsetDateTime fechaDesde,
        @Param("fechaHasta") OffsetDateTime fechaHasta
    );
    
    /**
     * Obtiene el último registro de un usuario (el más reciente)
     */
    @Query("SELECT r FROM RegistroUsuarioRuta r WHERE r.idUsuario = :idUsuario ORDER BY r.fechaRegistro DESC")
    java.util.List<RegistroUsuarioRuta> findUltimosRegistrosByUsuarioId(@Param("idUsuario") Integer idUsuario);
    
    /**
     * Busca un registro existente del usuario (para actualizar en lugar de crear duplicado)
     */
    @Query("SELECT r FROM RegistroUsuarioRuta r WHERE r.idUsuario = :idUsuario " +
           "AND r.ruta.idRuta = :idRuta AND r.paradero.idPunto = :idParadero " +
           "ORDER BY r.fechaRegistro DESC")
    java.util.List<RegistroUsuarioRuta> findRegistrosExistentes(
        @Param("idUsuario") Integer idUsuario,
        @Param("idRuta") Integer idRuta,
        @Param("idParadero") Integer idParadero
    );
}

