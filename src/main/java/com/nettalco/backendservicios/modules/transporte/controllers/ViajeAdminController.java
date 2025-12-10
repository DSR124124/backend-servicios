package com.nettalco.backendservicios.modules.transporte.controllers;

import com.nettalco.backendservicios.modules.transporte.dtos.ViajeRequest;
import com.nettalco.backendservicios.modules.transporte.dtos.ViajeResponse;
import com.nettalco.backendservicios.modules.transporte.services.IViajeAdminService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/admin/viajes")
@CrossOrigin(origins = "*")
public class ViajeAdminController {
    
    @Autowired
    private IViajeAdminService viajeAdminService;
    
    @PostMapping
    public ResponseEntity<?> crearViaje(@Valid @RequestBody ViajeRequest request) {
        try {
            ViajeResponse response = viajeAdminService.crearViaje(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                .body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Error al crear el viaje: " + e.getMessage()));
        }
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<?> obtenerViajePorId(@PathVariable Integer id) {
        Optional<ViajeResponse> viaje = viajeAdminService.obtenerViajePorId(id);
        if (viaje.isPresent()) {
            return ResponseEntity.ok(viaje.get());
        }
        return ResponseEntity.notFound().build();
    }
    
    @GetMapping
    public ResponseEntity<List<ViajeResponse>> listarViajes(
            @RequestParam(required = false) String estado,
            @RequestParam(required = false) Integer idRuta) {
        List<ViajeResponse> viajes;
        if (estado != null && !estado.isEmpty()) {
            viajes = viajeAdminService.listarViajesPorEstado(estado);
        } else if (idRuta != null) {
            viajes = viajeAdminService.listarViajesPorRuta(idRuta);
        } else {
            viajes = viajeAdminService.listarViajes();
        }
        return ResponseEntity.ok(viajes);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<?> actualizarViaje(
            @PathVariable Integer id,
            @Valid @RequestBody ViajeRequest request) {
        try {
            ViajeResponse response = viajeAdminService.actualizarViaje(id, request);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                .body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Error al actualizar el viaje: " + e.getMessage()));
        }
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminarViaje(@PathVariable Integer id) {
        try {
            viajeAdminService.eliminarViaje(id);
            return ResponseEntity.ok(Map.of("mensaje", "Viaje eliminado exitosamente"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                .body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Error al eliminar el viaje: " + e.getMessage()));
        }
    }
}

