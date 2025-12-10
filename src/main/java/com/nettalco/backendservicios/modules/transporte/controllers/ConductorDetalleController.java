package com.nettalco.backendservicios.modules.transporte.controllers;

import com.nettalco.backendservicios.core.clients.GestionClient;
import com.nettalco.backendservicios.modules.transporte.dtos.ConductorDetalleRequest;
import com.nettalco.backendservicios.modules.transporte.dtos.ConductorCompletoResponse;
import com.nettalco.backendservicios.modules.transporte.services.IConductorDetalleService;
import com.nettalco.backendservicios.modules.transporte.services.impl.ConductorDetalleService;
import com.nettalco.backendservicios.modules.transporte.entities.ConductorDetalle;
import com.nettalco.backendservicios.modules.transporte.repositories.ConductorDetalleRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin/conductores")
@CrossOrigin(origins = "*")
public class ConductorDetalleController {
    
    private static final Logger logger = LoggerFactory.getLogger(ConductorDetalleController.class);
    
    @Autowired
    private IConductorDetalleService conductorDetalleService;
    
    @Autowired
    private ConductorDetalleService conductorDetalleServiceImpl;
    
    @Autowired
    private ConductorDetalleRepository conductorDetalleRepository;
    
    @Autowired
    private GestionClient gestionClient;
    
    /**
     * Extrae el token JWT del header Authorization
     */
    private String obtenerToken(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7).trim();
            if (!token.isEmpty()) {
                return token;
            }
        }
        return null; // Retornar null si no hay token válido
    }
    
    @PostMapping
    public ResponseEntity<?> crearConductorDetalle(
            @Valid @RequestBody ConductorDetalleRequest request,
            HttpServletRequest httpRequest) {
        try {
            String token = obtenerToken(httpRequest);
            ConductorCompletoResponse response = conductorDetalleService.crearConductorDetalle(request, token);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                .body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Error al crear el conductor: " + e.getMessage()));
        }
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<?> obtenerConductorDetallePorId(
            @PathVariable Integer id,
            HttpServletRequest request) {
        String token = obtenerToken(request);
        
        return conductorDetalleRepository.findById(id)
            .map(conductor -> ResponseEntity.ok(
                conductorDetalleServiceImpl.convertirAConductorCompletoResponse(conductor, token)))
            .orElse(ResponseEntity.notFound().build());
    }
    
    @GetMapping
    public ResponseEntity<List<ConductorCompletoResponse>> listarConductores(
            @RequestParam(required = false) String estado,
            HttpServletRequest request) {
        String token = obtenerToken(request);
        
        if (token == null || token.trim().isEmpty()) {
            logger.warn("Petición sin token JWT para listar conductores");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(Collections.emptyList());
        }
        
        logger.debug("Token recibido para listar conductores. Longitud: {}, primeros 20 chars: {}...", 
            token.length(), token.length() > 20 ? token.substring(0, 20) : token);
        
        // PASO 1: Obtener usuarios con rol "Conductor" del backend-gestion
        List<Map<String, Object>> usuariosConductores;
        try {
            usuariosConductores = gestionClient.obtenerUsuariosPorNombreRol("Conductor", token);
        } catch (IllegalArgumentException e) {
            logger.error("Error de autenticación al obtener usuarios: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(Collections.emptyList());
        } catch (Exception e) {
            logger.error("Error inesperado al obtener usuarios del backend-gestion: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Collections.emptyList());
        }
        
        if (usuariosConductores == null || usuariosConductores.isEmpty()) {
            return ResponseEntity.ok(Collections.emptyList());
        }
        
        // PASO 2: Obtener todos los conductores_detalle del backend-servicios
        List<ConductorDetalle> conductoresDetalle = conductorDetalleRepository.findAll();
        
        // Crear un mapa para acceso rápido por idUsuarioGestion
        Map<Integer, ConductorDetalle> conductoresMap = conductoresDetalle.stream()
            .collect(Collectors.toMap(
                ConductorDetalle::getIdUsuarioGestion,
                c -> c
            ));
        
        // PASO 3: SOLO retornar conductores que YA están registrados en backend-servicios
        // Filtrar solo los que tienen un registro en conductor_detalle
        List<ConductorCompletoResponse> conductoresCompletos = usuariosConductores.stream()
            .map(usuarioData -> {
                Integer idUsuario = usuarioData.get("idUsuario") != null 
                    ? ((Number) usuarioData.get("idUsuario")).intValue() 
                    : null;
                
                if (idUsuario == null) {
                    return null;
                }
                
                // Obtener datos del conductor desde backend-servicios (si existe)
                ConductorDetalle conductorDetalle = conductoresMap.get(idUsuario);
                
                // SOLO incluir si tiene registro en conductor_detalle
                if (conductorDetalle == null) {
                    return null; // Excluir conductores no registrados
                }
                
                // Si hay filtro por estado, verificar
                if (estado != null && !estado.isEmpty()) {
                    if (!estado.equals(conductorDetalle.getEstado())) {
                        return null; // Filtrar este conductor por estado
                    }
                }
                
                // Retornar respuesta completa con datos del conductor registrado
                return conductorDetalleServiceImpl.convertirAConductorCompletoResponse(conductorDetalle, token);
            })
            .filter(Objects::nonNull) // Filtrar nulls
            .collect(Collectors.toList());
        
        return ResponseEntity.ok(conductoresCompletos);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<?> actualizarConductorDetalle(
            @PathVariable Integer id,
            @Valid @RequestBody ConductorDetalleRequest request,
            HttpServletRequest httpRequest) {
        try {
            String token = obtenerToken(httpRequest);
            ConductorCompletoResponse response = conductorDetalleService.actualizarConductorDetalle(id, request, token);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                .body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Error al actualizar el conductor: " + e.getMessage()));
        }
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminarConductorDetalle(@PathVariable Integer id) {
        try {
            conductorDetalleService.eliminarConductorDetalle(id);
            return ResponseEntity.ok(Map.of("mensaje", "Conductor eliminado exitosamente"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                .body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Error al eliminar el conductor: " + e.getMessage()));
        }
    }
    
    /**
     * Obtiene los usuarios con rol "Conductor" que NO están registrados aún en backend-servicios
     * Útil para el formulario de creación de conductores
     */
    @GetMapping("/usuarios-disponibles")
    public ResponseEntity<List<Map<String, Object>>> obtenerUsuariosConductoresDisponibles(
            HttpServletRequest request) {
        String token = obtenerToken(request);
        
        if (token == null || token.trim().isEmpty()) {
            logger.warn("Petición sin token JWT para obtener usuarios conductores disponibles");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(Collections.emptyList());
        }
        
        try {
            // Obtener todos los usuarios con rol "Conductor" del backend-gestion
            List<Map<String, Object>> usuariosConductores = gestionClient.obtenerUsuariosPorNombreRol("Conductor", token);
            
            if (usuariosConductores == null || usuariosConductores.isEmpty()) {
                return ResponseEntity.ok(Collections.emptyList());
            }
            
            // Obtener IDs de usuarios que YA están registrados como conductores
            List<Integer> idsConductoresRegistrados = conductorDetalleRepository.findAll().stream()
                .map(ConductorDetalle::getIdUsuarioGestion)
                .collect(Collectors.toList());
            
            // Filtrar solo los usuarios que NO están registrados
            List<Map<String, Object>> usuariosDisponibles = usuariosConductores.stream()
                .filter(usuarioData -> {
                    Integer idUsuario = usuarioData.get("idUsuario") != null 
                        ? ((Number) usuarioData.get("idUsuario")).intValue() 
                        : null;
                    return idUsuario != null && !idsConductoresRegistrados.contains(idUsuario);
                })
                .collect(Collectors.toList());
            
            return ResponseEntity.ok(usuariosDisponibles);
        } catch (IllegalArgumentException e) {
            logger.error("Error de autenticación al obtener usuarios disponibles: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(Collections.emptyList());
        } catch (Exception e) {
            logger.error("Error inesperado al obtener usuarios disponibles: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Collections.emptyList());
        }
    }
}

