package com.nettalco.backendservicios.modules.transporte.services;

import com.nettalco.backendservicios.modules.transporte.dtos.EstadisticasRegistrosResponse;
import com.nettalco.backendservicios.modules.transporte.dtos.RegistroUsuarioRutaRequest;
import com.nettalco.backendservicios.modules.transporte.dtos.RegistroUsuarioRutaResponse;

import java.util.List;
import java.util.Optional;

public interface IRegistroUsuarioRutaService {
    RegistroUsuarioRutaResponse registrarRutaParadero(Integer idUsuario, RegistroUsuarioRutaRequest request);
    Optional<RegistroUsuarioRutaResponse> obtenerUltimoRegistro(Integer idUsuario);
    List<RegistroUsuarioRutaResponse> listarTodosLosRegistros();
    List<RegistroUsuarioRutaResponse> listarRegistrosPorUsuario(Integer idUsuario);
    List<RegistroUsuarioRutaResponse> listarRegistrosPorRuta(Integer idRuta);
    EstadisticasRegistrosResponse obtenerEstadisticas();
}

