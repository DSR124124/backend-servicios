package com.nettalco.backendservicios.modules.transporte.services;

import com.nettalco.backendservicios.modules.transporte.dtos.ConductorDetalleRequest;
import com.nettalco.backendservicios.modules.transporte.dtos.ConductorDetalleResponse;

import java.util.List;
import java.util.Optional;

public interface IConductorDetalleService {
    ConductorDetalleResponse crearConductorDetalle(ConductorDetalleRequest request);
    Optional<ConductorDetalleResponse> obtenerConductorDetallePorId(Integer id);
    List<ConductorDetalleResponse> listarConductores();
    List<ConductorDetalleResponse> listarConductoresPorEstado(String estado);
    ConductorDetalleResponse actualizarConductorDetalle(Integer id, ConductorDetalleRequest request);
    void eliminarConductorDetalle(Integer id);
}

