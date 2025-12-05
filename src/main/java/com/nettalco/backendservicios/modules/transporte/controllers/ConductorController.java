package com.nettalco.backendservicios.modules.transporte.controllers;

import com.nettalco.backendservicios.modules.transporte.dtos.*;
import com.nettalco.backendservicios.core.security.UserDetails;
import com.nettalco.backendservicios.modules.transporte.services.IViajeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Controller para las operaciones de conductores.
 * Todos los endpoints requieren autenticacion JWT con rol de conductor.
 */
@RestController
@RequestMapping("/api/conductor")
@CrossOrigin(origins = "*")
public class ConductorController {
    
    @Autowired
    private IViajeService viajeService;
    
    private Integer obtenerIdConductor() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getDetails() instanceof UserDetails) {
            UserDetails userDetails = (UserDetails) auth.getDetails();
            return userDetails.getIdUsuario();
        }
        throw new SecurityException("Usuario no autenticado");
    }
    
    /**
     * Obtiene todos los viajes asignados al conductor autenticado
     * Endpoint: GET /api/conductor/mis-viajes
     */
    @GetMapping("/mis-viajes")
    public ResponseEntity<?> obtenerMisViajes() {
        try {
            Integer idConductor = obtenerIdConductor();
            List<ViajeConductorResponse> viajes = viajeService.obtenerViajesConductor(idConductor);
            return ResponseEntity.ok(viajes);
        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Error al obtener los viajes: " + e.getMessage()));
        }
    }
    
    /**
     * Obtiene el viaje activo (en curso) del conductor autenticado
     * Endpoint: GET /api/conductor/viaje-activo
     */
    @GetMapping("/viaje-activo")
    public ResponseEntity<?> obtenerViajeActivo() {
        try {
            Integer idConductor = obtenerIdConductor();
            Optional<ViajeActivoConductorResponse> viajeActivo = 
                viajeService.obtenerViajeActivoConductor(idConductor);
            
            if (viajeActivo.isPresent()) {
                return ResponseEntity.ok(viajeActivo.get());
            }
            
            return ResponseEntity.ok(Map.of(
                "mensaje", "No tienes ningun viaje en curso",
                "viajeActivo", false
            ));
        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Error al obtener el viaje activo: " + e.getMessage()));
        }
    }
    
    /**
     * Inicia un viaje programado
     * Endpoint: POST /api/conductor/viaje/{id}/iniciar
     */
    @PostMapping("/viaje/{id}/iniciar")
    public ResponseEntity<?> iniciarViaje(@PathVariable("id") Integer idViaje) {
        try {
            Integer idConductor = obtenerIdConductor();
            IniciarViajeResponse response = viajeService.iniciarViaje(idViaje, idConductor);
            return ResponseEntity.ok(response);
        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(Map.of("error", e.getMessage()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                .body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Error al iniciar el viaje: " + e.getMessage()));
        }
    }
    
    /**
     * Finaliza un viaje en curso
     * Endpoint: POST /api/conductor/viaje/{id}/finalizar
     */
    @PostMapping("/viaje/{id}/finalizar")
    public ResponseEntity<?> finalizarViaje(@PathVariable("id") Integer idViaje) {
        try {
            Integer idConductor = obtenerIdConductor();
            FinalizarViajeResponse response = viajeService.finalizarViaje(idViaje, idConductor);
            return ResponseEntity.ok(response);
        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(Map.of("error", e.getMessage()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                .body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Error al finalizar el viaje: " + e.getMessage()));
        }
    }
    
    /**
     * Obtiene el historial de viajes finalizados del conductor
     * Endpoint: GET /api/conductor/historial
     */
    @GetMapping("/historial")
    public ResponseEntity<?> obtenerHistorial(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        try {
            Integer idConductor = obtenerIdConductor();
            List<HistorialViajeResponse> historial = viajeService.obtenerHistorial(idConductor, page, size);
            return ResponseEntity.ok(historial);
        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Error al obtener el historial: " + e.getMessage()));
        }
    }
    
    /**
     * Obtiene las estadisticas del conductor
     * Endpoint: GET /api/conductor/estadisticas
     */
    @GetMapping("/estadisticas")
    public ResponseEntity<?> obtenerEstadisticas() {
        try {
            Integer idConductor = obtenerIdConductor();
            EstadisticasConductorResponse estadisticas = viajeService.obtenerEstadisticas(idConductor);
            return ResponseEntity.ok(estadisticas);
        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Error al obtener las estadisticas: " + e.getMessage()));
        }
    }
    
    /**
     * Marca la llegada del conductor a un paradero durante el viaje
     * Endpoint: POST /api/conductor/viaje/{idViaje}/paradero/{idParadero}/llegada
     */
    @PostMapping("/viaje/{idViaje}/paradero/{idParadero}/llegada")
    public ResponseEntity<?> marcarLlegadaParadero(
            @PathVariable("idViaje") Integer idViaje,
            @PathVariable("idParadero") Integer idParadero,
            @RequestBody(required = false) Map<String, Object> body) {
        try {
            Integer idConductor = obtenerIdConductor();
            
            BigDecimal latitud = null;
            BigDecimal longitud = null;
            
            if (body != null) {
                if (body.containsKey("latitud") && body.get("latitud") != null) {
                    latitud = new BigDecimal(body.get("latitud").toString());
                }
                if (body.containsKey("longitud") && body.get("longitud") != null) {
                    longitud = new BigDecimal(body.get("longitud").toString());
                }
            }
            
            LlegadaParaderoResponse response = viajeService.marcarLlegadaParadero(
                idViaje, idParadero, idConductor, latitud, longitud);
            
            return ResponseEntity.ok(response);
        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(Map.of("error", e.getMessage()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                .body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Error al registrar llegada al paradero: " + e.getMessage()));
        }
    }
}
