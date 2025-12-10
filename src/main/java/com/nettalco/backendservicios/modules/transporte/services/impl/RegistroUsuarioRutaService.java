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
        
        // Validar que la ruta existe
        Ruta ruta = rutaRepository.findById(request.getIdRuta())
            .orElseThrow(() -> new IllegalArgumentException("La ruta con ID " + request.getIdRuta() + " no existe"));
        
        // Validar que el paradero existe
        RutaPunto paradero = rutaPuntoRepository.findById(request.getIdParadero())
            .orElseThrow(() -> new IllegalArgumentException("El paradero con ID " + request.getIdParadero() + " no existe"));
        
        // Validar que el paradero pertenece a la ruta
        if (!paradero.getRuta().getIdRuta().equals(ruta.getIdRuta())) {
            throw new IllegalArgumentException("El paradero no pertenece a la ruta seleccionada");
        }
        
        // Buscar si ya existe un registro del mismo usuario con la misma ruta y paradero
        // Si existe, lo actualizamos en lugar de crear uno nuevo
        RegistroUsuarioRuta registro = registroRepository.findRegistroExistente(
            idUsuario, 
            request.getIdRuta(), 
            request.getIdParadero()
        ).orElse(new RegistroUsuarioRuta());
        
        // Si es un registro nuevo, establecer el usuario
        if (registro.getIdRegistro() == null) {
            registro.setIdUsuario(idUsuario);
        }
        
        // Actualizar los datos (o establecer si es nuevo)
        registro.setRuta(ruta);
        registro.setParadero(paradero);
        registro.setFechaRegistro(OffsetDateTime.now()); // Actualizar fecha al momento de la actualización
        registro.setObservacion(request.getObservacion());
        
        RegistroUsuarioRuta registroGuardado = registroRepository.save(registro);
        
        // Construir respuesta
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
        return registroRepository.findUltimoRegistroByUsuarioId(idUsuario)
            .map(registro -> {
                Ruta ruta = registro.getRuta();
                RutaPunto paradero = registro.getParadero();
                
                return new RegistroUsuarioRutaResponse(
                    registro.getIdRegistro(),
                    registro.getIdUsuario(),
                    ruta.getIdRuta(),
                    ruta.getNombre(),
                    paradero.getIdPunto(),
                    paradero.getNombreParadero() != null ? paradero.getNombreParadero() : "Paradero " + paradero.getOrden(),
                    registro.getFechaRegistro(),
                    "Último registro encontrado"
                );
            });
    }
}

