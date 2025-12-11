package com.nettalco.backendservicios.modules.transporte.dtos;

import jakarta.validation.constraints.NotBlank;

public record CrearRutaRequest(
    @NotBlank(message = "El nombre de la ruta es obligatorio")
    String nombre,
    
    String descripcion,
    
    String colorMapa
) {}
