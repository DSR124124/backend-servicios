package com.nettalco.backendservicios.modules.transporte.dtos;

import java.time.OffsetDateTime;

/**
 * DTO de respuesta para el registro de ruta y paradero
 */
public record RegistroUsuarioRutaResponse(
    Integer idRegistro,
    Integer idUsuario,
    Integer idRuta,
    String nombreRuta,
    Integer idParadero,
    String nombreParadero,
    OffsetDateTime fechaRegistro,
    String mensaje
) {
    public RegistroUsuarioRutaResponse {
        if (mensaje == null) {
            mensaje = "Registro de ruta y paradero guardado exitosamente";
        }
    }
}

