package com.nettalco.backendservicios.modules.transporte.controllers;

import com.nettalco.backendservicios.modules.transporte.dtos.BusLocationResponse;
import com.nettalco.backendservicios.modules.transporte.dtos.TripDetailResponse;
import com.nettalco.backendservicios.modules.transporte.dtos.ViajeActivoConUbicacionResponse;
import com.nettalco.backendservicios.modules.transporte.dtos.ViajeActivoResponse;
import com.nettalco.backendservicios.modules.transporte.services.IViajeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/trips")
@CrossOrigin(origins = "*")
public class ViajeController {
    
    @Autowired
    private IViajeService viajeService;
    
    /**
     * Obtiene los detalles de un viaje incluyendo la ruta completa
     * Endpoint: GET /api/trips/{tripId}/route
     */
    @GetMapping("/{tripId}/route")
    public ResponseEntity<?> obtenerRutaViaje(@PathVariable Integer tripId) {
        try {
            Optional<TripDetailResponse> tripDetail = viajeService.obtenerDetalleViaje(tripId);
            
            if (tripDetail.isPresent()) {
                return ResponseEntity.ok(tripDetail.get());
            }
            
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Map.of("error", "Viaje no encontrado"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Error al obtener la ruta del viaje: " + e.getMessage()));
        }
    }
    
    /**
     * Obtiene la ubicacion actual del bus para un viaje
     * Endpoint: GET /api/trips/{tripId}/location
     */
    @GetMapping("/{tripId}/location")
    public ResponseEntity<?> obtenerUbicacionActual(@PathVariable Integer tripId) {
        try {
            Optional<BusLocationResponse> location = viajeService.obtenerUbicacionActual(tripId);
            
            if (location.isPresent()) {
                return ResponseEntity.ok(location.get());
            }
            
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Map.of("error", "Ubicacion no encontrada para el viaje"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Error al obtener la ubicacion: " + e.getMessage()));
        }
    }
    
    /**
     * Obtiene los viajes activos para una ruta especifica
     * Endpoint: GET /api/trips/ruta/{rutaId}/activos
     */
    @GetMapping("/ruta/{rutaId}/activos")
    public ResponseEntity<?> obtenerViajesActivosPorRuta(@PathVariable Integer rutaId) {
        try {
            List<ViajeActivoResponse> viajes = viajeService.obtenerViajesActivosPorRuta(rutaId);
            return ResponseEntity.ok(viajes);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Error al obtener viajes activos: " + e.getMessage()));
        }
    }
    
    /**
     * Obtiene todos los viajes activos con sus ubicaciones en tiempo real
     * Endpoint: GET /api/trips/activos/ubicaciones
     */
    @GetMapping("/activos/ubicaciones")
    public ResponseEntity<?> obtenerTodosLosViajesActivosConUbicacion() {
        try {
            List<ViajeActivoConUbicacionResponse> viajes = viajeService.obtenerTodosLosViajesActivosConUbicacion();
            return ResponseEntity.ok(viajes);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Error al obtener viajes activos con ubicaciones: " + e.getMessage()));
        }
    }
}
