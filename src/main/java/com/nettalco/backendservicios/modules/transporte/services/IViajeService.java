package com.nettalco.backendservicios.modules.transporte.services;

import com.nettalco.backendservicios.modules.transporte.dtos.BusLocationResponse;
import com.nettalco.backendservicios.modules.transporte.dtos.EstadisticasConductorResponse;
import com.nettalco.backendservicios.modules.transporte.dtos.FinalizarViajeResponse;
import com.nettalco.backendservicios.modules.transporte.dtos.HistorialViajeResponse;
import com.nettalco.backendservicios.modules.transporte.dtos.IniciarViajeResponse;
import com.nettalco.backendservicios.modules.transporte.dtos.LlegadaParaderoResponse;
import com.nettalco.backendservicios.modules.transporte.dtos.ProximoParaderoResponse;
import com.nettalco.backendservicios.modules.transporte.dtos.TripDetailResponse;
import com.nettalco.backendservicios.modules.transporte.dtos.ViajeActivoConductorResponse;
import com.nettalco.backendservicios.modules.transporte.dtos.ViajeActivoResponse;
import com.nettalco.backendservicios.modules.transporte.dtos.ViajeConductorResponse;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface IViajeService {
    
    /**
     * Obtiene los detalles de un viaje incluyendo la ruta, informacion del bus y conductor
     */
    Optional<TripDetailResponse> obtenerDetalleViaje(Integer idViaje);
    
    /**
     * Obtiene la ubicacion actual del bus para un viaje
     */
    Optional<BusLocationResponse> obtenerUbicacionActual(Integer idViaje);
    
    /**
     * Obtiene los viajes activos (en_curso o programados) para una ruta especifica
     */
    List<ViajeActivoResponse> obtenerViajesActivosPorRuta(Integer idRuta);
    
    // =====================================================
    // Metodos para el modulo de conductores
    // =====================================================
    
    /**
     * Obtiene todos los viajes asignados a un conductor
     */
    List<ViajeConductorResponse> obtenerViajesConductor(Integer idConductor);
    
    /**
     * Obtiene el viaje activo (en_curso) de un conductor
     */
    Optional<ViajeActivoConductorResponse> obtenerViajeActivoConductor(Integer idConductor);
    
    /**
     * Inicia un viaje programado
     */
    IniciarViajeResponse iniciarViaje(Integer idViaje, Integer idConductor);
    
    /**
     * Finaliza un viaje en curso
     */
    FinalizarViajeResponse finalizarViaje(Integer idViaje, Integer idConductor);
    
    /**
     * Obtiene el historial de viajes finalizados de un conductor
     */
    List<HistorialViajeResponse> obtenerHistorial(Integer idConductor, int page, int size);
    
    /**
     * Obtiene las estadisticas del conductor
     */
    EstadisticasConductorResponse obtenerEstadisticas(Integer idConductor);
    
    /**
     * Marca la llegada del conductor a un paradero durante el viaje.
     * Los paraderos deben marcarse en orden secuencial.
     */
    LlegadaParaderoResponse marcarLlegadaParadero(
        Integer idViaje, 
        Integer idParadero, 
        Integer idConductor,
        BigDecimal latitud,
        BigDecimal longitud
    );
    
    /**
     * Obtiene información del próximo paradero a visitar en el viaje.
     * Útil para la navegación del conductor.
     */
    ProximoParaderoResponse obtenerProximoParadero(Integer idViaje, Integer idConductor);
}
