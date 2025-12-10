package com.nettalco.backendservicios.modules.transporte.services.impl;

import com.nettalco.backendservicios.modules.transporte.dtos.BusRequest;
import com.nettalco.backendservicios.modules.transporte.dtos.BusResponse;
import com.nettalco.backendservicios.modules.transporte.entities.Bus;
import com.nettalco.backendservicios.modules.transporte.repositories.BusRepository;
import com.nettalco.backendservicios.modules.transporte.services.IBusService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class BusService implements IBusService {
    
    @Autowired
    private BusRepository busRepository;
    
    @Override
    public BusResponse crearBus(BusRequest request) {
        if (busRepository.existsByPlaca(request.getPlaca())) {
            throw new IllegalArgumentException("Ya existe un bus con la placa: " + request.getPlaca());
        }
        
        Bus bus = new Bus();
        bus.setPlaca(request.getPlaca());
        bus.setModelo(request.getModelo());
        bus.setCapacidad(request.getCapacidad());
        bus.setImeiGps(request.getImeiGps());
        bus.setEstado(request.getEstado() != null ? request.getEstado() : "operativo");
        
        Bus busGuardado = busRepository.save(bus);
        return convertirABusResponse(busGuardado);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Optional<BusResponse> obtenerBusPorId(Integer id) {
        return busRepository.findById(id)
            .map(this::convertirABusResponse);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<BusResponse> listarBuses() {
        return busRepository.findAll().stream()
            .map(this::convertirABusResponse)
            .collect(Collectors.toList());
    }
    
    @Override
    public BusResponse actualizarBus(Integer id, BusRequest request) {
        Bus bus = busRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Bus no encontrado con ID: " + id));
        
        if (!bus.getPlaca().equals(request.getPlaca()) && 
            busRepository.existsByPlaca(request.getPlaca())) {
            throw new IllegalArgumentException("Ya existe un bus con la placa: " + request.getPlaca());
        }
        
        bus.setPlaca(request.getPlaca());
        bus.setModelo(request.getModelo());
        bus.setCapacidad(request.getCapacidad());
        bus.setImeiGps(request.getImeiGps());
        if (request.getEstado() != null) {
            bus.setEstado(request.getEstado());
        }
        
        Bus busActualizado = busRepository.save(bus);
        return convertirABusResponse(busActualizado);
    }
    
    @Override
    public void eliminarBus(Integer id) {
        if (!busRepository.existsById(id)) {
            throw new IllegalArgumentException("Bus no encontrado con ID: " + id);
        }
        busRepository.deleteById(id);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<BusResponse> listarBusesPorEstado(String estado) {
        return busRepository.findAll().stream()
            .filter(bus -> bus.getEstado().equals(estado))
            .map(this::convertirABusResponse)
            .collect(Collectors.toList());
    }
    
    private BusResponse convertirABusResponse(Bus bus) {
        return new BusResponse(
            bus.getIdBus(),
            bus.getPlaca(),
            bus.getModelo(),
            bus.getCapacidad(),
            bus.getImeiGps(),
            bus.getEstado()
        );
    }
}

