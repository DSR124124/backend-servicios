package com.nettalco.backendservicios.modules.transporte.services;

import com.nettalco.backendservicios.modules.transporte.dtos.ViajeRequest;
import com.nettalco.backendservicios.modules.transporte.dtos.ViajeResponse;

import java.util.List;
import java.util.Optional;

public interface IViajeAdminService {
    ViajeResponse crearViaje(ViajeRequest request);
    Optional<ViajeResponse> obtenerViajePorId(Integer id);
    List<ViajeResponse> listarViajes();
    List<ViajeResponse> listarViajesPorEstado(String estado);
    List<ViajeResponse> listarViajesPorRuta(Integer idRuta);
    ViajeResponse actualizarViaje(Integer id, ViajeRequest request);
    void eliminarViaje(Integer id);
}

