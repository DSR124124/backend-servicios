package com.nettalco.backendservicios.modules.transporte.controllers;

import com.nettalco.backendservicios.modules.transporte.dtos.BusRequest;
import com.nettalco.backendservicios.modules.transporte.dtos.BusResponse;
import com.nettalco.backendservicios.modules.transporte.services.IBusService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/admin/buses")
@CrossOrigin(origins = "*")
public class BusController {
    
    @Autowired
    private IBusService busService;
    
    @PostMapping
    public ResponseEntity<?> crearBus(@Valid @RequestBody BusRequest request) {
        try {
            BusResponse response = busService.crearBus(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                .body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Error al crear el bus: " + e.getMessage()));
        }
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<?> obtenerBusPorId(@PathVariable Integer id) {
        Optional<BusResponse> bus = busService.obtenerBusPorId(id);
        if (bus.isPresent()) {
            return ResponseEntity.ok(bus.get());
        }
        return ResponseEntity.notFound().build();
    }
    
    @GetMapping
    public ResponseEntity<List<BusResponse>> listarBuses(
            @RequestParam(required = false) String estado) {
        List<BusResponse> buses;
        if (estado != null && !estado.isEmpty()) {
            buses = busService.listarBusesPorEstado(estado);
        } else {
            buses = busService.listarBuses();
        }
        return ResponseEntity.ok(buses);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<?> actualizarBus(
            @PathVariable Integer id,
            @Valid @RequestBody BusRequest request) {
        try {
            BusResponse response = busService.actualizarBus(id, request);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                .body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Error al actualizar el bus: " + e.getMessage()));
        }
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminarBus(@PathVariable Integer id) {
        try {
            busService.eliminarBus(id);
            return ResponseEntity.ok(Map.of("mensaje", "Bus eliminado exitosamente"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                .body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Error al eliminar el bus: " + e.getMessage()));
        }
    }
}

