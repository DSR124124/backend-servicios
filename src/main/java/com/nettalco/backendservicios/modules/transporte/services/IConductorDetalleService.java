package com.nettalco.backendservicios.modules.transporte.services;

import com.nettalco.backendservicios.modules.transporte.dtos.ConductorDetalleRequest;
import com.nettalco.backendservicios.modules.transporte.dtos.ConductorCompletoResponse;

public interface IConductorDetalleService {
    ConductorCompletoResponse crearConductorDetalle(ConductorDetalleRequest request, String token);
    ConductorCompletoResponse actualizarConductorDetalle(Integer id, ConductorDetalleRequest request, String token);
    void eliminarConductorDetalle(Integer id);
}

