package com.nettalco.backendservicios.modules.transporte.services;

import com.nettalco.backendservicios.modules.transporte.dtos.RegistroUsuarioRutaRequest;
import com.nettalco.backendservicios.modules.transporte.dtos.RegistroUsuarioRutaResponse;

public interface IRegistroUsuarioRutaService {
    
    /**
     * Registra la selección de ruta y paradero por un usuario
     * @param idUsuario ID del usuario (extraído del JWT)
     * @param request Datos de la ruta y paradero seleccionados
     * @return Respuesta con los datos del registro
     */
    RegistroUsuarioRutaResponse registrarRutaParadero(Integer idUsuario, RegistroUsuarioRutaRequest request);
}

