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
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(Collections.emptyList());
        }
        
        // PASO 1: Obtener usuarios con rol "Conductor" del backend-gestion
        List<Map<String, Object>> usuariosConductores;
        try {
            usuariosConductores = gestionClient.obtenerUsuariosPorNombreRol("Conductor", token);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(Collections.emptyList());
        } catch (Exception e) {
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
        
        // PASO 3: Combinar datos del backend-gestion con datos locales
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
                
                // Si hay filtro por estado, verificar
                if (estado != null && !estado.isEmpty()) {
                    if (conductorDetalle == null || !estado.equals(conductorDetalle.getEstado())) {
                        return null; // Filtrar este conductor
                    }
                }
                
                // Si existe conductor_detalle, usar esos datos, sino crear respuesta solo con datos de gestión
                if (conductorDetalle != null) {
                    return conductorDetalleServiceImpl.convertirAConductorCompletoResponse(conductorDetalle, token);
                } else {
                    // Conductor existe en gestión pero no tiene detalles en servicios
                    // Crear respuesta solo con datos del backend-gestion
                    return new ConductorCompletoResponse(
                        idUsuario,
                        null, // licenciaNumero
                        null, // categoria
                        null, // telefonoContacto
                        null, // estado
                        (String) usuarioData.get("username"),
                        (String) usuarioData.get("email"),
                        (String) usuarioData.get("nombreCompleto"),
                        usuarioData.get("idRol") != null ? ((Number) usuarioData.get("idRol")).intValue() : null,
                        (String) usuarioData.get("nombreRol"),
                        usuarioData.get("activo") != null ? (Boolean) usuarioData.get("activo") : null
                    );
                }
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
}

