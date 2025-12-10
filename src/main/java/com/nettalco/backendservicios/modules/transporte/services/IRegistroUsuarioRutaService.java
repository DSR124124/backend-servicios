package com.nettalco.backendservicios.modules.transporte.services;

import com.nettalco.backendservicios.modules.transporte.dtos.RegistroUsuarioRutaRequest;
import com.nettalco.backendservicios.modules.transporte.dtos.RegistroUsuarioRutaResponse;

import java.util.Optional;

public interface IRegistroUsuarioRutaService {
    
    /**
     * Registra o actualiza la selección de ruta y paradero por un usuario.
     * Si ya existe un registro con la misma ruta y paradero, lo actualiza.
     * @param idUsuario ID del usuario (extraído del JWT)
     * @param request Datos de la ruta y paradero seleccionados
     * @return Respuesta con los datos del registro
     */
    RegistroUsuarioRutaResponse registrarRutaParadero(Integer idUsuario, RegistroUsuarioRutaRequest request);
    
    /**
     * Obtiene el último registro de ruta y paradero de un usuario
     * @param idUsuario ID del usuario
     * @return Último registro del usuario, o vacío si no tiene registros
     */
    Optional<RegistroUsuarioRutaResponse> obtenerUltimoRegistro(Integer idUsuario);
}

