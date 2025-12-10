package com.nettalco.backendservicios.modules.transporte.services.impl;

import com.nettalco.backendservicios.modules.transporte.dtos.EstadisticasGeneralesResponse;
import com.nettalco.backendservicios.modules.transporte.entities.Bus;
import com.nettalco.backendservicios.modules.transporte.entities.Viaje;
import com.nettalco.backendservicios.modules.transporte.repositories.BusRepository;
import com.nettalco.backendservicios.modules.transporte.repositories.ConductorDetalleRepository;
import com.nettalco.backendservicios.modules.transporte.repositories.RegistroUsuarioRutaRepository;
import com.nettalco.backendservicios.modules.transporte.repositories.RutaRepository;
import com.nettalco.backendservicios.modules.transporte.repositories.ViajeRepository;
import com.nettalco.backendservicios.modules.transporte.services.IAdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class AdminService implements IAdminService {
    
    @Autowired
    private RutaRepository rutaRepository;
    
    @Autowired
    private BusRepository busRepository;
    
    @Autowired
    private ConductorDetalleRepository conductorDetalleRepository;
    
    @Autowired
    private ViajeRepository viajeRepository;
    
    @Autowired
    private RegistroUsuarioRutaRepository registroUsuarioRutaRepository;
    
    @Override
    public EstadisticasGeneralesResponse obtenerEstadisticasGenerales() {
        Long totalRutas = rutaRepository.count();
        Long totalBuses = busRepository.count();
        Long totalConductores = conductorDetalleRepository.count();
        Long totalViajes = viajeRepository.count();
        Long viajesActivos = viajeRepository.count() - viajeRepository.findByEstado("finalizado").size();
        
        Long totalRegistrosUsuarios = registroUsuarioRutaRepository.count();
        
        Map<String, Long> busesPorEstado = busRepository.findAll().stream()
            .collect(Collectors.groupingBy(
                Bus::getEstado,
                Collectors.counting()
            ));
        
        Map<String, Long> viajesPorEstado = viajeRepository.findAll().stream()
            .collect(Collectors.groupingBy(
                Viaje::getEstado,
                Collectors.counting()
            ));
        
        Map<String, Long> registrosPorRuta = registroUsuarioRutaRepository.findAll().stream()
            .collect(Collectors.groupingBy(
                registro -> registro.getRuta().getNombre(),
                Collectors.counting()
            ));
        
        return new EstadisticasGeneralesResponse(
            totalRutas,
            totalBuses,
            totalConductores,
            totalViajes,
            viajesActivos,
            totalRegistrosUsuarios,
            busesPorEstado,
            viajesPorEstado,
            registrosPorRuta
        );
    }
}

