package com.nettalco.backendservicios.modules.transporte.services.impl;

import com.nettalco.backendservicios.core.clients.GestionClient;
import com.nettalco.backendservicios.modules.transporte.dtos.ConductorDetalleRequest;
import com.nettalco.backendservicios.modules.transporte.dtos.ConductorDetalleResponse;
import com.nettalco.backendservicios.modules.transporte.dtos.ConductorCompletoResponse;
import com.nettalco.backendservicios.modules.transporte.entities.ConductorDetalle;
import com.nettalco.backendservicios.modules.transporte.repositories.ConductorDetalleRepository;
import com.nettalco.backendservicios.modules.transporte.services.IConductorDetalleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class ConductorDetalleService implements IConductorDetalleService {
    
    @Autowired
    private ConductorDetalleRepository conductorDetalleRepository;
    
    @Autowired
    private GestionClient gestionClient;
    
    @Override
    public ConductorDetalleResponse crearConductorDetalle(ConductorDetalleRequest request) {
        if (conductorDetalleRepository.existsById(request.getIdUsuarioGestion())) {
            throw new IllegalArgumentException("Ya existe un conductor con el ID de usuario: " + request.getIdUsuarioGestion());
        }
        
        ConductorDetalle conductor = new ConductorDetalle();
        conductor.setIdUsuarioGestion(request.getIdUsuarioGestion());
        conductor.setLicenciaNumero(request.getLicenciaNumero());
        conductor.setCategoria(request.getCategoria());
        conductor.setTelefonoContacto(request.getTelefonoContacto());
        conductor.setEstado(request.getEstado() != null ? request.getEstado() : "activo");
        
        ConductorDetalle conductorGuardado = conductorDetalleRepository.save(conductor);
        return convertirAConductorDetalleResponse(conductorGuardado);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Optional<ConductorDetalleResponse> obtenerConductorDetallePorId(Integer id) {
        return conductorDetalleRepository.findById(id)
            .map(this::convertirAConductorDetalleResponse);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<ConductorDetalleResponse> listarConductores() {
        return conductorDetalleRepository.findAll().stream()
            .map(this::convertirAConductorDetalleResponse)
            .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<ConductorDetalleResponse> listarConductoresPorEstado(String estado) {
        return conductorDetalleRepository.findAll().stream()
            .filter(conductor -> conductor.getEstado().equals(estado))
            .map(this::convertirAConductorDetalleResponse)
            .collect(Collectors.toList());
    }
    
    @Override
    public ConductorDetalleResponse actualizarConductorDetalle(Integer id, ConductorDetalleRequest request) {
        ConductorDetalle conductor = conductorDetalleRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Conductor no encontrado con ID: " + id));
        
        conductor.setLicenciaNumero(request.getLicenciaNumero());
        conductor.setCategoria(request.getCategoria());
        conductor.setTelefonoContacto(request.getTelefonoContacto());
        if (request.getEstado() != null) {
            conductor.setEstado(request.getEstado());
        }
        
        ConductorDetalle conductorActualizado = conductorDetalleRepository.save(conductor);
        return convertirAConductorDetalleResponse(conductorActualizado);
    }
    
    @Override
    public void eliminarConductorDetalle(Integer id) {
        if (!conductorDetalleRepository.existsById(id)) {
            throw new IllegalArgumentException("Conductor no encontrado con ID: " + id);
        }
        conductorDetalleRepository.deleteById(id);
    }
    
    private ConductorDetalleResponse convertirAConductorDetalleResponse(ConductorDetalle conductor) {
        return new ConductorDetalleResponse(
            conductor.getIdUsuarioGestion(),
            conductor.getLicenciaNumero(),
            conductor.getCategoria(),
            conductor.getTelefonoContacto(),
            conductor.getEstado()
        );
    }
    
    /**
     * Convierte ConductorDetalle a ConductorCompletoResponse combinando datos del usuario
     */
    public ConductorCompletoResponse convertirAConductorCompletoResponse(ConductorDetalle conductor, String token) {
        Map<String, Object> usuarioData = gestionClient.obtenerUsuario(conductor.getIdUsuarioGestion(), token);
        
        String username = null;
        String email = null;
        String nombreCompleto = null;
        Integer idRol = null;
        String nombreRol = null;
        Boolean usuarioActivo = null;
        
        if (usuarioData != null) {
            username = (String) usuarioData.get("username");
            email = (String) usuarioData.get("email");
            nombreCompleto = (String) usuarioData.get("nombreCompleto");
            idRol = usuarioData.get("idRol") != null ? ((Number) usuarioData.get("idRol")).intValue() : null;
            nombreRol = (String) usuarioData.get("nombreRol");
            usuarioActivo = usuarioData.get("activo") != null ? (Boolean) usuarioData.get("activo") : null;
        }
        
        return new ConductorCompletoResponse(
            conductor.getIdUsuarioGestion(),
            conductor.getLicenciaNumero(),
            conductor.getCategoria(),
            conductor.getTelefonoContacto(),
            conductor.getEstado(),
            username,
            email,
            nombreCompleto,
            idRol,
            nombreRol,
            usuarioActivo
        );
    }
}

