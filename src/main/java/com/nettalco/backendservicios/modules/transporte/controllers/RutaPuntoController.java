package com.nettalco.backendservicios.modules.transporte.controllers;

import com.nettalco.backendservicios.modules.transporte.dtos.RutaPuntoRequest;
import com.nettalco.backendservicios.modules.transporte.dtos.RutaPuntoResponse;
import com.nettalco.backendservicios.modules.transporte.services.IRutaPuntoService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/admin/ruta-puntos")
@CrossOrigin(origins = "*")
public class RutaPuntoController {
    
    @Autowired
    private IRutaPuntoService rutaPuntoService;
    
    @PostMapping
    public ResponseEntity<?> crearRutaPunto(@Valid @RequestBody RutaPuntoRequest request) {
        try {
            RutaPuntoResponse response = rutaPuntoService.crearRutaPunto(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                .body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Error al crear el punto de ruta: " + e.getMessage()));
        }
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<?> obtenerRutaPuntoPorId(@PathVariable Integer id) {
        Optional<RutaPuntoResponse> punto = rutaPuntoService.obtenerRutaPuntoPorId(id);
        if (punto.isPresent()) {
            return ResponseEntity.ok(punto.get());
        }
        return ResponseEntity.notFound().build();
    }
    
    @GetMapping("/ruta/{idRuta}")
    public ResponseEntity<List<RutaPuntoResponse>> listarPuntosPorRuta(@PathVariable Integer idRuta) {
        List<RutaPuntoResponse> puntos = rutaPuntoService.listarPuntosPorRuta(idRuta);
        return ResponseEntity.ok(puntos);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<?> actualizarRutaPunto(
            @PathVariable Integer id,
            @Valid @RequestBody RutaPuntoRequest request) {
        try {
            RutaPuntoResponse response = rutaPuntoService.actualizarRutaPunto(id, request);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                .body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Error al actualizar el punto de ruta: " + e.getMessage()));
        }
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminarRutaPunto(@PathVariable Integer id) {
        try {
            rutaPuntoService.eliminarRutaPunto(id);
            return ResponseEntity.ok(Map.of("mensaje", "Punto de ruta eliminado exitosamente"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                .body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Error al eliminar el punto de ruta: " + e.getMessage()));
        }
    }
}

