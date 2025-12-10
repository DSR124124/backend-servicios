package com.nettalco.backendservicios.modules.transporte.services;

import com.nettalco.backendservicios.modules.transporte.dtos.BusRequest;
import com.nettalco.backendservicios.modules.transporte.dtos.BusResponse;

import java.util.List;
import java.util.Optional;

public interface IBusService {
    BusResponse crearBus(BusRequest request);
    Optional<BusResponse> obtenerBusPorId(Integer id);
    List<BusResponse> listarBuses();
    BusResponse actualizarBus(Integer id, BusRequest request);
    void eliminarBus(Integer id);
    List<BusResponse> listarBusesPorEstado(String estado);
}

