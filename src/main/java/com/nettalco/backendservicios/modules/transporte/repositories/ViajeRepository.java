package com.nettalco.backendservicios.modules.transporte.repositories;

import com.nettalco.backendservicios.modules.transporte.entities.Viaje;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ViajeRepository extends JpaRepository<Viaje, Integer> {
    
    @Query("SELECT v FROM Viaje v WHERE v.idViaje = :id AND v.estado = 'en_curso'")
    Optional<Viaje> findByIdAndActivo(@Param("id") Integer id);
    
    @Query("SELECT v FROM Viaje v " +
           "LEFT JOIN FETCH v.ruta r " +
           "LEFT JOIN FETCH r.puntos " +
           "LEFT JOIN FETCH v.bus " +
           "WHERE v.idViaje = :id")
    Optional<Viaje> findByIdWithRelations(@Param("id") Integer id);
    
    @Query("SELECT v FROM Viaje v " +
           "LEFT JOIN FETCH v.bus " +
           "WHERE v.ruta.idRuta = :idRuta " +
           "AND v.estado IN ('en_curso', 'programado') " +
           "ORDER BY v.fechaInicioProgramada ASC")
    List<Viaje> findByRutaIdAndActivos(@Param("idRuta") Integer idRuta);
    
    boolean existsByIdViaje(Integer idViaje);
    
    // =====================================================
    // Queries para el modulo de conductores
    // =====================================================
    
    @Query("SELECT v FROM Viaje v " +
           "LEFT JOIN FETCH v.ruta r " +
           "LEFT JOIN FETCH v.bus " +
           "WHERE v.idConductor = :idConductor " +
           "AND v.estado IN ('programado', 'en_curso', 'finalizado') " +
           "ORDER BY v.fechaInicioProgramada DESC")
    List<Viaje> findByConductorIdWithRelations(@Param("idConductor") Integer idConductor);
    
    @Query("SELECT v FROM Viaje v " +
           "LEFT JOIN FETCH v.ruta r " +
           "LEFT JOIN FETCH v.bus " +
           "WHERE v.idConductor = :idConductor " +
           "AND v.estado = 'programado' " +
           "ORDER BY v.fechaInicioProgramada ASC")
    List<Viaje> findViajesProgramadosByConductor(@Param("idConductor") Integer idConductor);
    
    @Query("SELECT v FROM Viaje v " +
           "LEFT JOIN FETCH v.ruta r " +
           "LEFT JOIN FETCH r.puntos " +
           "LEFT JOIN FETCH v.bus " +
           "WHERE v.idConductor = :idConductor " +
           "AND v.estado = 'en_curso'")
    Optional<Viaje> findViajeActivoByConductor(@Param("idConductor") Integer idConductor);
    
    @Query("SELECT COUNT(v) > 0 FROM Viaje v " +
           "WHERE v.idConductor = :idConductor " +
           "AND v.estado = 'en_curso'")
    boolean existsViajeActivoByConductor(@Param("idConductor") Integer idConductor);
    
    @Query("SELECT v FROM Viaje v " +
           "LEFT JOIN FETCH v.ruta r " +
           "LEFT JOIN FETCH r.puntos " +
           "LEFT JOIN FETCH v.bus " +
           "WHERE v.idViaje = :idViaje " +
           "AND v.idConductor = :idConductor")
    Optional<Viaje> findByIdAndConductor(
        @Param("idViaje") Integer idViaje, 
        @Param("idConductor") Integer idConductor
    );
    
    @Query("SELECT v FROM Viaje v " +
           "LEFT JOIN FETCH v.ruta r " +
           "LEFT JOIN FETCH v.bus " +
           "WHERE v.idConductor = :idConductor " +
           "AND v.estado = 'finalizado' " +
           "ORDER BY v.fechaFinReal DESC")
    List<Viaje> findHistorialByConductor(@Param("idConductor") Integer idConductor);
    
    @Query("SELECT v.estado, COUNT(v) FROM Viaje v " +
           "WHERE v.idConductor = :idConductor " +
           "GROUP BY v.estado")
    List<Object[]> countViajesByEstado(@Param("idConductor") Integer idConductor);
    
    @Query("SELECT COUNT(v) FROM Viaje v " +
           "WHERE v.idConductor = :idConductor " +
           "AND v.estado = 'finalizado' " +
           "AND v.fechaFinReal >= :desde " +
           "AND v.fechaFinReal < :hasta")
    Long countViajesFinalizadosEnRango(
        @Param("idConductor") Integer idConductor,
        @Param("desde") java.time.OffsetDateTime desde,
        @Param("hasta") java.time.OffsetDateTime hasta
    );
    
    @Query("SELECT v FROM Viaje v " +
           "WHERE v.idConductor = :idConductor " +
           "AND v.estado = 'finalizado' " +
           "AND v.fechaFinReal >= :desde " +
           "AND v.fechaFinReal < :hasta")
    List<Viaje> findViajesFinalizadosEnRango(
        @Param("idConductor") Integer idConductor,
        @Param("desde") java.time.OffsetDateTime desde,
        @Param("hasta") java.time.OffsetDateTime hasta
    );
    
    List<Viaje> findByEstado(String estado);
    
    @Query("SELECT v FROM Viaje v WHERE v.ruta.idRuta = :idRuta")
    List<Viaje> findByRutaIdRuta(@Param("idRuta") Integer idRuta);
}
