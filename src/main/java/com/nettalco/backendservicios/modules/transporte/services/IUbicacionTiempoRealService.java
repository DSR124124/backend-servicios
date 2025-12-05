package com.nettalco.backendservicios.modules.transporte.services;

import com.nettalco.backendservicios.modules.transporte.dtos.GPSIngestaRequest;
import com.nettalco.backendservicios.modules.transporte.dtos.GPSIngestaResponse;

import java.util.concurrent.CompletableFuture;

public interface IUbicacionTiempoRealService {
    
    GPSIngestaResponse registrarUbicacion(GPSIngestaRequest request);
    
    CompletableFuture<GPSIngestaResponse> registrarUbicacionAsync(GPSIngestaRequest request);
}
