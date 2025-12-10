package com.nettalco.backendservicios.modules.transporte.services.impl;

import com.nettalco.backendservicios.modules.transporte.dtos.RegistroUsuarioRutaRequest;
import com.nettalco.backendservicios.modules.transporte.dtos.RegistroUsuarioRutaResponse;
import com.nettalco.backendservicios.modules.transporte.entities.RegistroUsuarioRuta;
import com.nettalco.backendservicios.modules.transporte.entities.Ruta;
import com.nettalco.backendservicios.modules.transporte.entities.RutaPunto;
import com.nettalco.backendservicios.modules.transporte.repositories.RegistroUsuarioRutaRepository;
import com.nettalco.backendservicios.modules.transporte.repositories.RutaPuntoRepository;
import com.nettalco.backendservicios.modules.transporte.repositories.RutaRepository;
import com.nettalco.backendservicios.modules.transporte.services.IRegistroUsuarioRutaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;

@Service
public class RegistroUsuarioRutaService implements IRegistroUsuarioRutaService {
    
    @Autowired
    private RegistroUsuarioRutaRepository registroRepository;
    
    @Autowired
    private RutaRepository rutaRepository;
    
    @Autowired
    private RutaPuntoRepository rutaPuntoRepository;
    
    @Override
    @Transactional
    public RegistroUsuarioRutaResponse registrarRutaParadero(
            Integer idUsuario, 
            RegistroUsuarioRutaRequest request) {
        
        Ruta ruta = rutaRepository.findById(request.getIdRuta())
            .orElseThrow(() -> new IllegalArgumentException("La ruta con ID " + request.getIdRuta() + " no existe"));
        
        RutaPunto paradero = rutaPuntoRepository.findById(request.getIdParadero())
            .orElseThrow(() -> new IllegalArgumentException("El paradero con ID " + request.getIdParadero() + " no existe"));
        
        if (!paradero.getRuta().getIdRuta().equals(ruta.getIdRuta())) {
            throw new IllegalArgumentException("El paradero no pertenece a la ruta seleccionada");
        }
        
        java.util.List<RegistroUsuarioRuta> registrosExistentes = registroRepository.findRegistrosExistentes(
            idUsuario, 
            request.getIdRuta(), 
            request.getIdParadero()
        );
        RegistroUsuarioRuta registro = registrosExistentes.isEmpty() 
            ? new RegistroUsuarioRuta() 
            : registrosExistentes.get(0);
        
        if (registro.getIdRegistro() == null) {
            registro.setIdUsuario(idUsuario);
        }
        
        registro.setRuta(ruta);
        registro.setParadero(paradero);
        registro.setFechaRegistro(OffsetDateTime.now());
        registro.setObservacion(request.getObservacion());
        
        RegistroUsuarioRuta registroGuardado = registroRepository.save(registro);
        
        String mensaje = registro.getIdRegistro() == null 
            ? "Registro de ruta y paradero guardado exitosamente"
            : "Registro de ruta y paradero actualizado exitosamente";
        
        return new RegistroUsuarioRutaResponse(
            registroGuardado.getIdRegistro(),
            registroGuardado.getIdUsuario(),
            ruta.getIdRuta(),
            ruta.getNombre(),
            paradero.getIdPunto(),
            paradero.getNombreParadero() != null ? paradero.getNombreParadero() : "Paradero " + paradero.getOrden(),
            registroGuardado.getFechaRegistro(),
            mensaje
        );
    }
    
    @Override
    @Transactional(readOnly = true)
    public java.util.Optional<RegistroUsuarioRutaResponse> obtenerUltimoRegistro(Integer idUsuario) {
        java.util.List<RegistroUsuarioRuta> registros = registroRepository.findUltimosRegistrosByUsuarioId(idUsuario);
        
        if (registros.isEmpty()) {
            return java.util.Optional.empty();
        }
        
        RegistroUsuarioRuta registro = registros.get(0);
        Ruta ruta = registro.getRuta();
        RutaPunto paradero = registro.getParadero();
        
        return java.util.Optional.of(new RegistroUsuarioRutaResponse(
            registro.getIdRegistro(),
            registro.getIdUsuario(),
            ruta.getIdRuta(),
            ruta.getNombre(),
            paradero.getIdPunto(),
            paradero.getNombreParadero() != null ? paradero.getNombreParadero() : "Paradero " + paradero.getOrden(),
            registro.getFechaRegistro(),
            "Último registro encontrado"
        ));
    }
}

