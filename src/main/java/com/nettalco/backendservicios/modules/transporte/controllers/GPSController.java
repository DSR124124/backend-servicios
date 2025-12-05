package com.nettalco.backendservicios.modules.transporte.controllers;

import com.nettalco.backendservicios.modules.transporte.dtos.GPSIngestaRequest;
import com.nettalco.backendservicios.modules.transporte.dtos.GPSIngestaResponse;
import com.nettalco.backendservicios.modules.transporte.services.IUbicacionTiempoRealService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * Controller optimizado para ingesta de GPS en tiempo real.
 * Disenado para alto throughput con procesamiento asincrono.
 */
@RestController
@RequestMapping("/api/gps")
@CrossOrigin(origins = "*")
public class GPSController {
    
    @Autowired
    private IUbicacionTiempoRealService ubicacionService;
    
    /**
     * Endpoint sincrono para registro de ubicacion GPS.
     */
    @PostMapping("/ubicacion")
    public ResponseEntity<?> registrarUbicacion(@Valid @RequestBody GPSIngestaRequest request) {
        try {
            GPSIngestaResponse response = ubicacionService.registrarUbicacion(request);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                .body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Error al registrar ubicacion: " + e.getMessage()));
        }
    }
    
    /**
     * Endpoint asincrono para registro de ubicacion GPS.
     * Retorna inmediatamente mientras procesa en background.
     */
    @PostMapping("/ubicacion/async")
    public ResponseEntity<?> registrarUbicacionAsync(@Valid @RequestBody GPSIngestaRequest request) {
        try {
            ubicacionService.registrarUbicacionAsync(request)
                .thenAccept(response -> {
                    // Log opcional
                })
                .exceptionally(ex -> {
                    return null;
                });
            
            return ResponseEntity.accepted()
                .body(Map.of(
                    "mensaje", "Ubicacion en proceso de registro",
                    "idViaje", request.idViaje()
                ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Error al iniciar registro de ubicacion: " + e.getMessage()));
        }
    }
}
