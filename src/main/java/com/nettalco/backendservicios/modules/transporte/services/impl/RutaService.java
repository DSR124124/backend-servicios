package com.nettalco.backendservicios.modules.transporte.services.impl;

import com.nettalco.backendservicios.modules.transporte.dtos.CrearRutaRequest;
import com.nettalco.backendservicios.modules.transporte.dtos.PuntoRutaDto;
import com.nettalco.backendservicios.modules.transporte.dtos.RutaDetalleCompletoResponse;
import com.nettalco.backendservicios.modules.transporte.dtos.RutaResponse;
import com.nettalco.backendservicios.modules.transporte.dtos.ViajeActivoResponse;
import com.nettalco.backendservicios.modules.transporte.entities.Bus;
import com.nettalco.backendservicios.modules.transporte.entities.Ruta;
import com.nettalco.backendservicios.modules.transporte.entities.RutaPunto;
import com.nettalco.backendservicios.modules.transporte.repositories.BusRepository;
import com.nettalco.backendservicios.modules.transporte.repositories.RutaRepository;
import com.nettalco.backendservicios.modules.transporte.services.IRutaService;
import com.nettalco.backendservicios.modules.transporte.services.IViajeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class RutaService implements IRutaService {
    
    @Autowired
    private RutaRepository rutaRepository;
    
    @Autowired
    private BusRepository busRepository;
    
    @Autowired
    private IViajeService viajeService;
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public RutaResponse crearRuta(CrearRutaRequest request) {
        // Validar que el bus existe
        Bus bus = busRepository.findById(request.idVehiculoDefault())
            .orElseThrow(() -> new IllegalArgumentException("El vehiculo con ID " + request.idVehiculoDefault() + " no existe"));
        
        // Validar que el nombre de la ruta no este duplicado
        if (rutaRepository.existsByNombre(request.nombre())) {
            throw new IllegalArgumentException("Ya existe una ruta con el nombre: " + request.nombre());
        }
        
        // Crear la entidad Ruta
        Ruta ruta = new Ruta();
        ruta.setNombre(request.nombre());
        ruta.setDescripcion(request.descripcion());
        ruta.setColorMapa(request.colorMapa() != null ? request.colorMapa() : "#0000FF");
        ruta.setEstado(true);
        
        // Crear y asociar los puntos de la ruta
        for (PuntoRutaDto puntoDto : request.puntos()) {
            RutaPunto punto = new RutaPunto();
            punto.setRuta(ruta);
            punto.setOrden(puntoDto.orden());
            punto.setLatitud(puntoDto.latitud());
            punto.setLongitud(puntoDto.longitud());
            punto.setNombreParadero(puntoDto.nombreParadero());
            punto.setEsParaderoOficial(puntoDto.esParaderoOficial() != null ? puntoDto.esParaderoOficial() : false);
            
            ruta.addPunto(punto);
        }
        
        // Guardar la ruta y sus puntos en una sola transaccion
        Ruta rutaGuardada = rutaRepository.save(ruta);
        
        // Retornar el DTO de respuesta
        return convertirARutaResponse(rutaGuardada);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Optional<RutaResponse> obtenerRutaPorId(Integer id) {
        return rutaRepository.findByIdWithPuntos(id)
            .map(this::convertirARutaResponse);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<RutaResponse> listarRutas() {
        return rutaRepository.findAll().stream()
            .map(this::convertirARutaResponse)
            .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void eliminarRuta(Integer id) {
        if (!rutaRepository.existsById(id)) {
            throw new IllegalArgumentException("La ruta con ID " + id + " no existe");
        }
        rutaRepository.deleteById(id);
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public RutaResponse actualizarRuta(Integer id, CrearRutaRequest request) {
        Ruta ruta = rutaRepository.findByIdWithPuntos(id)
            .orElseThrow(() -> new IllegalArgumentException("La ruta con ID " + id + " no existe"));
        
        // Validar nombre unico si cambio
        if (!ruta.getNombre().equals(request.nombre()) && rutaRepository.existsByNombre(request.nombre())) {
            throw new IllegalArgumentException("Ya existe una ruta con el nombre: " + request.nombre());
        }
        
        // Validar que el bus existe
        Bus bus = busRepository.findById(request.idVehiculoDefault())
            .orElseThrow(() -> new IllegalArgumentException("El vehiculo con ID " + request.idVehiculoDefault() + " no existe"));
        
        // Actualizar datos basicos
        ruta.setNombre(request.nombre());
        ruta.setDescripcion(request.descripcion());
        if (request.colorMapa() != null) {
            ruta.setColorMapa(request.colorMapa());
        }
        
        // Eliminar puntos existentes
        ruta.getPuntos().clear();
        
        // Agregar nuevos puntos
        for (PuntoRutaDto puntoDto : request.puntos()) {
            RutaPunto punto = new RutaPunto();
            punto.setRuta(ruta);
            punto.setOrden(puntoDto.orden());
            punto.setLatitud(puntoDto.latitud());
            punto.setLongitud(puntoDto.longitud());
            punto.setNombreParadero(puntoDto.nombreParadero());
            punto.setEsParaderoOficial(puntoDto.esParaderoOficial() != null ? puntoDto.esParaderoOficial() : false);
            
            ruta.addPunto(punto);
        }
        
        Ruta rutaActualizada = rutaRepository.save(ruta);
        return convertirARutaResponse(rutaActualizada);
    }
    
    private RutaResponse convertirARutaResponse(Ruta ruta) {
        List<RutaResponse.PuntoRutaResponse> puntosResponse = ruta.getPuntos().stream()
            .map(p -> new RutaResponse.PuntoRutaResponse(
                p.getIdPunto(),
                p.getOrden(),
                p.getLatitud(),
                p.getLongitud(),
                p.getNombreParadero(),
                p.getEsParaderoOficial()
            ))
            .collect(Collectors.toList());
        
        return new RutaResponse(
            ruta.getIdRuta(),
            ruta.getNombre(),
            ruta.getDescripcion(),
            ruta.getColorMapa(),
            ruta.getEstado(),
            puntosResponse
        );
    }
    
    @Override
    @Transactional(readOnly = true)
    public Optional<RutaDetalleCompletoResponse> obtenerRutaDetalleCompleto(Integer idRuta) {
        Optional<Ruta> rutaOpt = rutaRepository.findByIdWithPuntos(idRuta);
        
        if (rutaOpt.isEmpty()) {
            return Optional.empty();
        }
        
        Ruta ruta = rutaOpt.get();
        RutaResponse rutaResponse = convertirARutaResponse(ruta);
        
        // Obtener viajes activos para esta ruta
        List<ViajeActivoResponse> viajesActivos = viajeService.obtenerViajesActivosPorRuta(idRuta);
        
        // Calcular horarios basados en los viajes programados
        List<RutaDetalleCompletoResponse.HorarioResponse> horarios = calcularHorarios(viajesActivos);
        
        return Optional.of(new RutaDetalleCompletoResponse(
            rutaResponse,
            viajesActivos,
            horarios
        ));
    }
    
    /**
     * Calcula los horarios basados en los viajes programados
     */
    private List<RutaDetalleCompletoResponse.HorarioResponse> calcularHorarios(
            List<ViajeActivoResponse> viajes) {
        return viajes.stream()
            .filter(v -> v.fechaInicioProgramada() != null && v.fechaFinProgramada() != null)
            .map(v -> {
                String horaInicio = v.fechaInicioProgramada().toLocalTime().toString();
                String horaFin = v.fechaFinProgramada().toLocalTime().toString();
                String frecuencia = "Variable";
                String diasSemana = "Lunes a Domingo";
                
                return new RutaDetalleCompletoResponse.HorarioResponse(
                    horaInicio,
                    horaFin,
                    frecuencia,
                    diasSemana
                );
            })
            .distinct()
            .collect(Collectors.toList());
    }
}
