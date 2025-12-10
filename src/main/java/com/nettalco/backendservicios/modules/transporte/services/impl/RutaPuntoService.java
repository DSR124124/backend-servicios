package com.nettalco.backendservicios.modules.transporte.services.impl;

import com.nettalco.backendservicios.modules.transporte.dtos.RutaPuntoRequest;
import com.nettalco.backendservicios.modules.transporte.dtos.RutaPuntoResponse;
import com.nettalco.backendservicios.modules.transporte.entities.Ruta;
import com.nettalco.backendservicios.modules.transporte.entities.RutaPunto;
import com.nettalco.backendservicios.modules.transporte.repositories.RutaPuntoRepository;
import com.nettalco.backendservicios.modules.transporte.repositories.RutaRepository;
import com.nettalco.backendservicios.modules.transporte.services.IRutaPuntoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class RutaPuntoService implements IRutaPuntoService {
    
    @Autowired
    private RutaPuntoRepository rutaPuntoRepository;
    
    @Autowired
    private RutaRepository rutaRepository;
    
    @Override
    public RutaPuntoResponse crearRutaPunto(RutaPuntoRequest request) {
        Ruta ruta = rutaRepository.findById(request.getIdRuta())
            .orElseThrow(() -> new IllegalArgumentException("Ruta no encontrada con ID: " + request.getIdRuta()));
        
        RutaPunto punto = new RutaPunto();
        punto.setRuta(ruta);
        punto.setOrden(request.getOrden());
        punto.setLatitud(request.getLatitud());
        punto.setLongitud(request.getLongitud());
        punto.setNombreParadero(request.getNombreParadero());
        punto.setEsParaderoOficial(request.getEsParaderoOficial() != null ? request.getEsParaderoOficial() : false);
        
        RutaPunto puntoGuardado = rutaPuntoRepository.save(punto);
        return convertirARutaPuntoResponse(puntoGuardado);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Optional<RutaPuntoResponse> obtenerRutaPuntoPorId(Integer id) {
        return rutaPuntoRepository.findById(id)
            .map(this::convertirARutaPuntoResponse);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<RutaPuntoResponse> listarPuntosPorRuta(Integer idRuta) {
        return rutaPuntoRepository.findByRutaIdOrderByOrden(idRuta).stream()
            .map(this::convertirARutaPuntoResponse)
            .collect(Collectors.toList());
    }
    
    @Override
    public RutaPuntoResponse actualizarRutaPunto(Integer id, RutaPuntoRequest request) {
        RutaPunto punto = rutaPuntoRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Punto de ruta no encontrado con ID: " + id));
        
        Ruta ruta = rutaRepository.findById(request.getIdRuta())
            .orElseThrow(() -> new IllegalArgumentException("Ruta no encontrada con ID: " + request.getIdRuta()));
        
        punto.setRuta(ruta);
        punto.setOrden(request.getOrden());
        punto.setLatitud(request.getLatitud());
        punto.setLongitud(request.getLongitud());
        punto.setNombreParadero(request.getNombreParadero());
        if (request.getEsParaderoOficial() != null) {
            punto.setEsParaderoOficial(request.getEsParaderoOficial());
        }
        
        RutaPunto puntoActualizado = rutaPuntoRepository.save(punto);
        return convertirARutaPuntoResponse(puntoActualizado);
    }
    
    @Override
    public void eliminarRutaPunto(Integer id) {
        if (!rutaPuntoRepository.existsById(id)) {
            throw new IllegalArgumentException("Punto de ruta no encontrado con ID: " + id);
        }
        rutaPuntoRepository.deleteById(id);
    }
    
    private RutaPuntoResponse convertirARutaPuntoResponse(RutaPunto punto) {
        return new RutaPuntoResponse(
            punto.getIdPunto(),
            punto.getRuta().getIdRuta(),
            punto.getRuta().getNombre(),
            punto.getOrden(),
            punto.getLatitud(),
            punto.getLongitud(),
            punto.getNombreParadero(),
            punto.getEsParaderoOficial()
        );
    }
}

