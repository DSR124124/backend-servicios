package com.nettalco.backendservicios.modules.transporte.controllers;

import com.nettalco.backendservicios.core.security.UserDetails;
import com.nettalco.backendservicios.modules.transporte.dtos.RegistroUsuarioRutaRequest;
import com.nettalco.backendservicios.modules.transporte.dtos.RegistroUsuarioRutaResponse;
import com.nettalco.backendservicios.modules.transporte.services.IRegistroUsuarioRutaService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/registro-ruta")
@CrossOrigin(origins = "*")
public class RegistroUsuarioRutaController {
    
    @Autowired
    private IRegistroUsuarioRutaService registroService;
    
    private Integer obtenerIdUsuario() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getDetails() instanceof UserDetails) {
            UserDetails userDetails = (UserDetails) auth.getDetails();
            return userDetails.getIdUsuario();
        }
        throw new SecurityException("Usuario no autenticado");
    }
    
    @PostMapping
    public ResponseEntity<?> registrarRutaParadero(@Valid @RequestBody RegistroUsuarioRutaRequest request) {
        try {
            Integer idUsuario = obtenerIdUsuario();
            RegistroUsuarioRutaResponse response = registroService.registrarRutaParadero(idUsuario, request);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(Map.of("error", e.getMessage()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                .body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Error al registrar ruta y paradero: " + e.getMessage()));
        }
    }
    
    @GetMapping("/ultimo")
    public ResponseEntity<?> obtenerUltimoRegistro() {
        try {
            Integer idUsuario = obtenerIdUsuario();
            java.util.Optional<RegistroUsuarioRutaResponse> registro = registroService.obtenerUltimoRegistro(idUsuario);
            
            if (registro.isPresent()) {
                return ResponseEntity.ok(registro.get());
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("mensaje", "No se encontró ningún registro para este usuario"));
            }
        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Error al obtener el último registro: " + e.getMessage()));
        }
    }
    
    @GetMapping("/admin/estadisticas")
    public ResponseEntity<?> obtenerEstadisticas() {
        try {
            com.nettalco.backendservicios.modules.transporte.dtos.EstadisticasRegistrosResponse estadisticas = 
                registroService.obtenerEstadisticas();
            return ResponseEntity.ok(estadisticas);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Error al obtener las estadísticas: " + e.getMessage()));
        }
    }
}

