package com.nettalco.backendservicios.modules.transporte.controllers;

import com.nettalco.backendservicios.modules.transporte.dtos.ConductorDetalleRequest;
import com.nettalco.backendservicios.modules.transporte.dtos.ConductorDetalleResponse;
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

import java.util.List;
import java.util.Map;
import java.util.Optional;
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
    
    /**
     * Extrae el token JWT del header Authorization
     */
    private String obtenerToken(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7);
        }
        return "";
    }
    
    @PostMapping
    public ResponseEntity<?> crearConductorDetalle(
            @Valid @RequestBody ConductorDetalleRequest request,
            HttpServletRequest httpRequest) {
        try {
            String token = obtenerToken(httpRequest);
            ConductorDetalleResponse response = conductorDetalleService.crearConductorDetalle(request, token);
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
        
        Optional<ConductorDetalle> conductor = conductorDetalleRepository.findById(id);
        if (conductor.isPresent()) {
            ConductorCompletoResponse conductorCompleto = 
                conductorDetalleServiceImpl.convertirAConductorCompletoResponse(conductor.get(), token);
            return ResponseEntity.ok(conductorCompleto);
        }
        return ResponseEntity.notFound().build();
    }
    
    @GetMapping
    public ResponseEntity<List<ConductorCompletoResponse>> listarConductores(
            @RequestParam(required = false) String estado,
            HttpServletRequest request) {
        String token = obtenerToken(request);
        
        List<ConductorDetalle> conductores;
        if (estado != null && !estado.isEmpty()) {
            conductores = conductorDetalleRepository.findAll().stream()
                .filter(c -> c.getEstado().equals(estado))
                .collect(Collectors.toList());
        } else {
            conductores = conductorDetalleRepository.findAll();
        }
        
        List<ConductorCompletoResponse> conductoresCompletos = conductores.stream()
            .map(c -> conductorDetalleServiceImpl.convertirAConductorCompletoResponse(c, token))
            .collect(Collectors.toList());
        
        return ResponseEntity.ok(conductoresCompletos);
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

