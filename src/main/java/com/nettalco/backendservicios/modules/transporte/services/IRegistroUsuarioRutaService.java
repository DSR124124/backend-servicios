package com.nettalco.backendservicios.modules.transporte.services;

import com.nettalco.backendservicios.modules.transporte.dtos.EstadisticasRegistrosResponse;
import com.nettalco.backendservicios.modules.transporte.dtos.RegistroUsuarioRutaRequest;
import com.nettalco.backendservicios.modules.transporte.dtos.RegistroUsuarioRutaResponse;

public interface IRegistroUsuarioRutaService {
    RegistroUsuarioRutaResponse registrarRutaParadero(Integer idUsuario, RegistroUsuarioRutaRequest request);
    java.util.Optional<RegistroUsuarioRutaResponse> obtenerUltimoRegistro(Integer idUsuario);
    EstadisticasRegistrosResponse obtenerEstadisticas();
}

