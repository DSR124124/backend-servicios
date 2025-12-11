package com.nettalco.backendservicios.modules.transporte.repositories;

import com.nettalco.backendservicios.modules.transporte.entities.RegistroUsuarioRuta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RegistroUsuarioRutaRepository extends JpaRepository<RegistroUsuarioRuta, Integer> {
    
    @Query("SELECT r FROM RegistroUsuarioRuta r WHERE r.idUsuario = :idUsuario ORDER BY r.fechaRegistro DESC")
    java.util.List<RegistroUsuarioRuta> findAllByUsuarioId(@Param("idUsuario") Integer idUsuario);
    
    @Query("SELECT r FROM RegistroUsuarioRuta r WHERE r.paradero.idPunto = :idParadero ORDER BY r.fechaRegistro DESC")
    List<RegistroUsuarioRuta> findByParaderoIdOrderByFechaDesc(@Param("idParadero") Integer idParadero);
    
    // Consultas para estadísticas
    @Query(value = "SELECT COUNT(*) FROM registros_usuarios_rutas WHERE DATE(fecha_registro) = CURRENT_DATE", nativeQuery = true)
    Long countRegistrosHoy();
    
    // 1. HEATMAP: Hora del día vs Paradero
    @Query(value = "SELECT EXTRACT(HOUR FROM fecha_registro) as hora, " +
           "r.id_punto as id_paradero, " +
           "COALESCE(r.nombre_paradero, 'Paradero ' || r.orden) as nombre_paradero, " +
           "COUNT(DISTINCT rur.id_usuario) as cantidad_usuarios " +
           "FROM registros_usuarios_rutas rur " +
           "JOIN ruta_puntos r ON rur.id_paradero = r.id_punto " +
           "GROUP BY EXTRACT(HOUR FROM fecha_registro), r.id_punto, r.nombre_paradero, r.orden " +
           "ORDER BY hora, id_paradero", nativeQuery = true)
    List<Object[]> obtenerDatosHeatmap();
    
    // 2. STACKED BAR: Ruta con segmentos por Paradero
    @Query(value = "SELECT rt.id_ruta, " +
           "rt.nombre as nombre_ruta, " +
           "r.id_punto as id_paradero, " +
           "COALESCE(r.nombre_paradero, 'Paradero ' || r.orden) as nombre_paradero, " +
           "COUNT(DISTINCT rur.id_usuario) as cantidad_usuarios " +
           "FROM registros_usuarios_rutas rur " +
           "JOIN rutas rt ON rur.id_ruta = rt.id_ruta " +
           "JOIN ruta_puntos r ON rur.id_paradero = r.id_punto " +
           "GROUP BY rt.id_ruta, rt.nombre, r.id_punto, r.nombre_paradero, r.orden " +
           "ORDER BY rt.id_ruta, r.id_punto", nativeQuery = true)
    List<Object[]> obtenerDatosStackedBar();
    
    // 3. LINE CHART: Hora vs Cantidad por Ruta
    @Query(value = "SELECT EXTRACT(HOUR FROM fecha_registro) as hora, " +
           "rt.id_ruta, " +
           "rt.nombre as nombre_ruta, " +
           "COUNT(DISTINCT rur.id_usuario) as cantidad_usuarios " +
           "FROM registros_usuarios_rutas rur " +
           "JOIN rutas rt ON rur.id_ruta = rt.id_ruta " +
           "GROUP BY EXTRACT(HOUR FROM fecha_registro), rt.id_ruta, rt.nombre " +
           "ORDER BY hora, rt.id_ruta", nativeQuery = true)
    List<Object[]> obtenerDatosLineChart();
}

