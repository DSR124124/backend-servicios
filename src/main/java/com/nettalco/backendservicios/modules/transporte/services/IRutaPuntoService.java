package com.nettalco.backendservicios.modules.transporte.services;

import com.nettalco.backendservicios.modules.transporte.dtos.RutaPuntoRequest;
import com.nettalco.backendservicios.modules.transporte.dtos.RutaPuntoResponse;

import java.util.List;
import java.util.Optional;

public interface IRutaPuntoService {
    RutaPuntoResponse crearRutaPunto(RutaPuntoRequest request);
    Optional<RutaPuntoResponse> obtenerRutaPuntoPorId(Integer id);
    List<RutaPuntoResponse> listarPuntosPorRuta(Integer idRuta);
    RutaPuntoResponse actualizarRutaPunto(Integer id, RutaPuntoRequest request);
    void eliminarRutaPunto(Integer id);
}

