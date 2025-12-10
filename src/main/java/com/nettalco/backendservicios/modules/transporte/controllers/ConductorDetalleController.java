package com.nettalco.backendservicios.modules.transporte.controllers;

import com.nettalco.backendservicios.modules.transporte.dtos.ConductorDetalleRequest;
import com.nettalco.backendservicios.modules.transporte.dtos.ConductorDetalleResponse;
import com.nettalco.backendservicios.modules.transporte.services.IConductorDetalleService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/admin/conductores")
@CrossOrigin(origins = "*")
public class ConductorDetalleController {
    
    @Autowired
    private IConductorDetalleService conductorDetalleService;
    
    @PostMapping
    public ResponseEntity<?> crearConductorDetalle(@Valid @RequestBody ConductorDetalleRequest request) {
        try {
            ConductorDetalleResponse response = conductorDetalleService.crearConductorDetalle(request);
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
    public ResponseEntity<?> obtenerConductorDetallePorId(@PathVariable Integer id) {
        Optional<ConductorDetalleResponse> conductor = conductorDetalleService.obtenerConductorDetallePorId(id);
        if (conductor.isPresent()) {
            return ResponseEntity.ok(conductor.get());
        }
        return ResponseEntity.notFound().build();
    }
    
    @GetMapping
    public ResponseEntity<List<ConductorDetalleResponse>> listarConductores(
            @RequestParam(required = false) String estado) {
        List<ConductorDetalleResponse> conductores;
        if (estado != null && !estado.isEmpty()) {
            conductores = conductorDetalleService.listarConductoresPorEstado(estado);
        } else {
            conductores = conductorDetalleService.listarConductores();
        }
        return ResponseEntity.ok(conductores);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<?> actualizarConductorDetalle(
            @PathVariable Integer id,
            @Valid @RequestBody ConductorDetalleRequest request) {
        try {
            ConductorDetalleResponse response = conductorDetalleService.actualizarConductorDetalle(id, request);
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

