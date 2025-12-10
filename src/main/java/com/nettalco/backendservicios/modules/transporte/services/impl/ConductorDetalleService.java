package com.nettalco.backendservicios.modules.transporte.services.impl;

import com.nettalco.backendservicios.core.clients.GestionClient;
import com.nettalco.backendservicios.modules.transporte.dtos.ConductorDetalleRequest;
import com.nettalco.backendservicios.modules.transporte.dtos.ConductorCompletoResponse;
import com.nettalco.backendservicios.modules.transporte.entities.ConductorDetalle;
import com.nettalco.backendservicios.modules.transporte.repositories.ConductorDetalleRepository;
import com.nettalco.backendservicios.modules.transporte.services.IConductorDetalleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

@Service
@Transactional
public class ConductorDetalleService implements IConductorDetalleService {
    
    @Autowired
    private ConductorDetalleRepository conductorDetalleRepository;
    
    @Autowired
    private GestionClient gestionClient;
    
    /**
     * Registra un conductor en la tabla conductores_detalle del backend-servicios.
     * El idUsuarioGestion debe corresponder a un usuario existente en el backend-gestion
     * con rol de Conductor y estado activo.
     * 
     * @param request Datos del conductor incluyendo el idUsuarioGestion del backend-gestion
     * @param token Token JWT para validar con el backend-gestion
     * @return ConductorCompletoResponse con datos del conductor y del usuario de gestión
     */
    @Override
    public ConductorCompletoResponse crearConductorDetalle(ConductorDetalleRequest request, String token) {
        // Validar que no exista ya un conductor con este ID de usuario en backend-servicios
        if (conductorDetalleRepository.existsById(request.getIdUsuarioGestion())) {
            throw new IllegalArgumentException("Ya existe un conductor registrado con el ID de usuario: " + request.getIdUsuarioGestion());
        }
        
        // Validar que el usuario exista en el backend-gestion
        Map<String, Object> usuarioData = gestionClient.obtenerUsuario(request.getIdUsuarioGestion(), token);
        if (usuarioData == null) {
            throw new IllegalArgumentException("El usuario con ID " + request.getIdUsuarioGestion() + " no existe en el sistema de gestión");
        }
        
        // Validar que el usuario esté activo en el backend-gestion
        Boolean activo = usuarioData.get("activo") != null ? (Boolean) usuarioData.get("activo") : false;
        if (!activo) {
            throw new IllegalArgumentException("El usuario con ID " + request.getIdUsuarioGestion() + " no está activo en el sistema de gestión");
        }
        
        // Validar que el usuario tenga rol de Conductor en el backend-gestion
        String nombreRol = (String) usuarioData.get("nombreRol");
        if (nombreRol == null || !nombreRol.toLowerCase().contains("conductor")) {
            throw new IllegalArgumentException("El usuario con ID " + request.getIdUsuarioGestion() + " no tiene el rol de Conductor. Su rol actual es: " + (nombreRol != null ? nombreRol : "sin rol"));
        }
        
        // Crear y guardar el conductor en la tabla conductores_detalle del backend-servicios
        // El idUsuarioGestion es la clave primaria y referencia al usuario en backend-gestion
        ConductorDetalle conductor = new ConductorDetalle();
        conductor.setIdUsuarioGestion(request.getIdUsuarioGestion()); // ID del usuario del backend-gestion
        conductor.setLicenciaNumero(request.getLicenciaNumero());
        conductor.setCategoria(request.getCategoria());
        conductor.setTelefonoContacto(request.getTelefonoContacto());
        conductor.setEstado(request.getEstado() != null ? request.getEstado() : "activo");
        
        // Guardar en la base de datos del backend-servicios
        ConductorDetalle conductorGuardado = conductorDetalleRepository.save(conductor);
        
        // Retornar respuesta completa con datos del usuario de gestión
        return convertirAConductorCompletoResponse(conductorGuardado, token);
    }
    
    @Override
    public ConductorCompletoResponse actualizarConductorDetalle(Integer id, ConductorDetalleRequest request, String token) {
        ConductorDetalle conductor = conductorDetalleRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Conductor no encontrado con ID: " + id));
        
        // No permitir cambiar el idUsuarioGestion
        if (!conductor.getIdUsuarioGestion().equals(request.getIdUsuarioGestion())) {
            throw new IllegalArgumentException("No se permite cambiar el ID de usuario de gestión de un conductor existente");
        }
        
        // Validar que el usuario siga existiendo y tenga rol de Conductor
        Map<String, Object> usuarioData = gestionClient.obtenerUsuario(request.getIdUsuarioGestion(), token);
        if (usuarioData == null) {
            throw new IllegalArgumentException("El usuario con ID " + request.getIdUsuarioGestion() + " no existe en el sistema de gestión");
        }
        
        Boolean activo = usuarioData.get("activo") != null ? (Boolean) usuarioData.get("activo") : false;
        if (!activo) {
            throw new IllegalArgumentException("El usuario con ID " + request.getIdUsuarioGestion() + " no está activo en el sistema de gestión");
        }
        
        String nombreRol = (String) usuarioData.get("nombreRol");
        if (nombreRol == null || !nombreRol.toLowerCase().contains("conductor")) {
            throw new IllegalArgumentException("El usuario con ID " + request.getIdUsuarioGestion() + " no tiene el rol de Conductor. Su rol actual es: " + (nombreRol != null ? nombreRol : "sin rol"));
        }
        
        conductor.setLicenciaNumero(request.getLicenciaNumero());
        conductor.setCategoria(request.getCategoria());
        conductor.setTelefonoContacto(request.getTelefonoContacto());
        if (request.getEstado() != null) {
            conductor.setEstado(request.getEstado());
        }
        
        ConductorDetalle conductorActualizado = conductorDetalleRepository.save(conductor);
        return convertirAConductorCompletoResponse(conductorActualizado, token);
    }
    
    @Override
    public void eliminarConductorDetalle(Integer id) {
        if (!conductorDetalleRepository.existsById(id)) {
            throw new IllegalArgumentException("Conductor no encontrado con ID: " + id);
        }
        conductorDetalleRepository.deleteById(id);
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

