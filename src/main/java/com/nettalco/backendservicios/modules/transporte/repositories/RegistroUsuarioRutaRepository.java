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
    
    @Query("SELECT r FROM RegistroUsuarioRuta r WHERE r.idUsuario = :idUsuario ORDER BY r.fechaRegistro DESC")
    List<RegistroUsuarioRuta> findByUsuarioIdOrderByFechaDesc(@Param("idUsuario") Integer idUsuario);
    
    @Query("SELECT r FROM RegistroUsuarioRuta r WHERE r.ruta.idRuta = :idRuta ORDER BY r.fechaRegistro DESC")
    List<RegistroUsuarioRuta> findByRutaIdOrderByFechaDesc(@Param("idRuta") Integer idRuta);
    
    @Query("SELECT r FROM RegistroUsuarioRuta r WHERE r.paradero.idPunto = :idParadero ORDER BY r.fechaRegistro DESC")
    List<RegistroUsuarioRuta> findByParaderoIdOrderByFechaDesc(@Param("idParadero") Integer idParadero);
    
    @Query("SELECT COUNT(r) FROM RegistroUsuarioRuta r WHERE r.idUsuario = :idUsuario " +
           "AND r.fechaRegistro >= :fechaDesde AND r.fechaRegistro <= :fechaHasta")
    Long countByUsuarioAndFechaBetween(
        @Param("idUsuario") Integer idUsuario,
        @Param("fechaDesde") OffsetDateTime fechaDesde,
        @Param("fechaHasta") OffsetDateTime fechaHasta
    );
    
    @Query("SELECT r FROM RegistroUsuarioRuta r WHERE r.idUsuario = :idUsuario ORDER BY r.fechaRegistro DESC")
    java.util.List<RegistroUsuarioRuta> findUltimosRegistrosByUsuarioId(@Param("idUsuario") Integer idUsuario);
    
    @Query("SELECT r FROM RegistroUsuarioRuta r WHERE r.idUsuario = :idUsuario " +
           "AND r.ruta.idRuta = :idRuta AND r.paradero.idPunto = :idParadero " +
           "ORDER BY r.fechaRegistro DESC")
    java.util.List<RegistroUsuarioRuta> findRegistrosExistentes(
        @Param("idUsuario") Integer idUsuario,
        @Param("idRuta") Integer idRuta,
        @Param("idParadero") Integer idParadero
    );
    
    // Consultas para estadísticas
    @Query(value = "SELECT COUNT(*) FROM registros_usuarios_rutas WHERE DATE(fecha_registro) = CURRENT_DATE", nativeQuery = true)
    Long countRegistrosHoy();
    
    @Query("SELECT COUNT(r) FROM RegistroUsuarioRuta r WHERE r.fechaRegistro >= :fechaInicio")
    Long countRegistrosDesde(@Param("fechaInicio") OffsetDateTime fechaInicio);
    
    @Query("SELECT r.ruta.idRuta, r.ruta.nombre, COUNT(r) as cantidad " +
           "FROM RegistroUsuarioRuta r GROUP BY r.ruta.idRuta, r.ruta.nombre " +
           "ORDER BY cantidad DESC")
    List<Object[]> countRegistrosPorRuta();
    
    @Query("SELECT r.paradero.idPunto, r.paradero.nombreParadero, r.ruta.idRuta, r.ruta.nombre, COUNT(r) as cantidad " +
           "FROM RegistroUsuarioRuta r GROUP BY r.paradero.idPunto, r.paradero.nombreParadero, r.ruta.idRuta, r.ruta.nombre " +
           "ORDER BY cantidad DESC")
    List<Object[]> countRegistrosPorParadero();
    
    @Query("SELECT r.idUsuario, COUNT(r) as cantidad " +
           "FROM RegistroUsuarioRuta r GROUP BY r.idUsuario " +
           "ORDER BY cantidad DESC")
    List<Object[]> countRegistrosPorUsuario();
    
    @Query(value = "SELECT DATE(fecha_registro) as fecha, COUNT(*) as cantidad " +
           "FROM registros_usuarios_rutas " +
           "WHERE fecha_registro >= :fechaInicio " +
           "GROUP BY DATE(fecha_registro) " +
           "ORDER BY fecha DESC", nativeQuery = true)
    List<Object[]> countRegistrosPorDia(@Param("fechaInicio") OffsetDateTime fechaInicio);
}

