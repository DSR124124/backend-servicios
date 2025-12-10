package com.nettalco.backendservicios.modules.transporte.services.impl;

import com.nettalco.backendservicios.modules.transporte.dtos.ViajeRequest;
import com.nettalco.backendservicios.modules.transporte.dtos.ViajeResponse;
import com.nettalco.backendservicios.modules.transporte.entities.Bus;
import com.nettalco.backendservicios.modules.transporte.entities.Ruta;
import com.nettalco.backendservicios.modules.transporte.entities.Viaje;
import com.nettalco.backendservicios.modules.transporte.repositories.BusRepository;
import com.nettalco.backendservicios.modules.transporte.repositories.RutaRepository;
import com.nettalco.backendservicios.modules.transporte.repositories.ViajeRepository;
import com.nettalco.backendservicios.modules.transporte.services.IViajeAdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class ViajeAdminService implements IViajeAdminService {
    
    @Autowired
    private ViajeRepository viajeRepository;
    
    @Autowired
    private RutaRepository rutaRepository;
    
    @Autowired
    private BusRepository busRepository;
    
    @Override
    public ViajeResponse crearViaje(ViajeRequest request) {
        Ruta ruta = rutaRepository.findById(request.getIdRuta())
            .orElseThrow(() -> new IllegalArgumentException("Ruta no encontrada con ID: " + request.getIdRuta()));
        
        Bus bus = busRepository.findById(request.getIdBus())
            .orElseThrow(() -> new IllegalArgumentException("Bus no encontrado con ID: " + request.getIdBus()));
        
        Viaje viaje = new Viaje();
        viaje.setRuta(ruta);
        viaje.setBus(bus);
        viaje.setIdConductor(request.getIdConductor());
        viaje.setFechaInicioProgramada(request.getFechaInicioProgramada());
        viaje.setFechaFinProgramada(request.getFechaFinProgramada());
        viaje.setEstado(request.getEstado() != null ? request.getEstado() : "programado");
        
        Viaje viajeGuardado = viajeRepository.save(viaje);
        return convertirAViajeResponse(viajeGuardado);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Optional<ViajeResponse> obtenerViajePorId(Integer id) {
        return viajeRepository.findById(id)
            .map(this::convertirAViajeResponse);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<ViajeResponse> listarViajes() {
        return viajeRepository.findAll().stream()
            .map(this::convertirAViajeResponse)
            .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<ViajeResponse> listarViajesPorEstado(String estado) {
        return viajeRepository.findByEstado(estado).stream()
            .map(this::convertirAViajeResponse)
            .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<ViajeResponse> listarViajesPorRuta(Integer idRuta) {
        return viajeRepository.findByRutaIdRuta(idRuta).stream()
            .map(this::convertirAViajeResponse)
            .collect(Collectors.toList());
    }
    
    @Override
    public ViajeResponse actualizarViaje(Integer id, ViajeRequest request) {
        Viaje viaje = viajeRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Viaje no encontrado con ID: " + id));
        
        Ruta ruta = rutaRepository.findById(request.getIdRuta())
            .orElseThrow(() -> new IllegalArgumentException("Ruta no encontrada con ID: " + request.getIdRuta()));
        
        Bus bus = busRepository.findById(request.getIdBus())
            .orElseThrow(() -> new IllegalArgumentException("Bus no encontrado con ID: " + request.getIdBus()));
        
        viaje.setRuta(ruta);
        viaje.setBus(bus);
        viaje.setIdConductor(request.getIdConductor());
        viaje.setFechaInicioProgramada(request.getFechaInicioProgramada());
        viaje.setFechaFinProgramada(request.getFechaFinProgramada());
        if (request.getEstado() != null) {
            viaje.setEstado(request.getEstado());
        }
        
        Viaje viajeActualizado = viajeRepository.save(viaje);
        return convertirAViajeResponse(viajeActualizado);
    }
    
    @Override
    public void eliminarViaje(Integer id) {
        if (!viajeRepository.existsById(id)) {
            throw new IllegalArgumentException("Viaje no encontrado con ID: " + id);
        }
        viajeRepository.deleteById(id);
    }
    
    private ViajeResponse convertirAViajeResponse(Viaje viaje) {
        return new ViajeResponse(
            viaje.getIdViaje(),
            viaje.getRuta().getIdRuta(),
            viaje.getRuta().getNombre(),
            viaje.getBus().getIdBus(),
            viaje.getBus().getPlaca(),
            viaje.getIdConductor(),
            viaje.getFechaInicioProgramada(),
            viaje.getFechaFinProgramada(),
            viaje.getFechaInicioReal(),
            viaje.getFechaFinReal(),
            viaje.getEstado()
        );
    }
}

