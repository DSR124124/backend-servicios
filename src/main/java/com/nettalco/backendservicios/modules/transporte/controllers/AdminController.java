package com.nettalco.backendservicios.modules.transporte.controllers;

import com.nettalco.backendservicios.modules.transporte.dtos.EstadisticasGeneralesResponse;
import com.nettalco.backendservicios.modules.transporte.services.IAdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/admin")
@CrossOrigin(origins = "*")
public class AdminController {
    
    @Autowired
    private IAdminService adminService;
    
    @GetMapping("/estadisticas")
    public ResponseEntity<?> obtenerEstadisticasGenerales() {
        try {
            EstadisticasGeneralesResponse estadisticas = adminService.obtenerEstadisticasGenerales();
            return ResponseEntity.ok(estadisticas);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Error al obtener las estadísticas: " + e.getMessage()));
        }
    }
}

