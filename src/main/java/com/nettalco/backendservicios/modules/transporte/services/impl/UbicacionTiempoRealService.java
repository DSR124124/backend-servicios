package com.nettalco.backendservicios.modules.transporte.services.impl;

import com.nettalco.backendservicios.modules.transporte.dtos.GPSIngestaRequest;
import com.nettalco.backendservicios.modules.transporte.dtos.GPSIngestaResponse;
import com.nettalco.backendservicios.modules.transporte.entities.UbicacionTiempoReal;
import com.nettalco.backendservicios.modules.transporte.entities.Viaje;
import com.nettalco.backendservicios.modules.transporte.repositories.UbicacionTiempoRealRepository;
import com.nettalco.backendservicios.modules.transporte.repositories.ViajeRepository;
import com.nettalco.backendservicios.modules.transporte.services.IUbicacionTiempoRealService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.concurrent.CompletableFuture;

@Service
public class UbicacionTiempoRealService implements IUbicacionTiempoRealService {
    
    @Autowired
    private UbicacionTiempoRealRepository ubicacionRepository;
    
    @Autowired
    private ViajeRepository viajeRepository;
    
    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = Exception.class)
    public GPSIngestaResponse registrarUbicacion(GPSIngestaRequest request) {
        // Validar que el viaje existe y esta activo
        Viaje viaje = viajeRepository.findByIdAndActivo(request.idViaje())
            .orElseThrow(() -> new IllegalArgumentException(
                "El viaje con ID " + request.idViaje() + " no existe o no esta en curso"));
        
        // Crear la entidad de ubicacion
        UbicacionTiempoReal ubicacion = new UbicacionTiempoReal();
        ubicacion.setViaje(viaje);
        ubicacion.setLatitud(request.latitud());
        ubicacion.setLongitud(request.longitud());
        ubicacion.setVelocidadKmh(request.velocidadKmh());
        ubicacion.setRumbo(request.rumbo());
        ubicacion.setFechaRegistro(OffsetDateTime.now());
        
        // Guardar usando REQUIRES_NEW para minimizar el tiempo de bloqueo de transaccion
        UbicacionTiempoReal ubicacionGuardada = ubicacionRepository.save(ubicacion);
        
        return new GPSIngestaResponse(
            ubicacionGuardada.getIdTracking(),
            request.idViaje(),
            ubicacionGuardada.getFechaRegistro(),
            "Ubicacion registrada exitosamente"
        );
    }
    
    @Override
    @Async("virtualThreadExecutor")
    @Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = Exception.class)
    public CompletableFuture<GPSIngestaResponse> registrarUbicacionAsync(GPSIngestaRequest request) {
        GPSIngestaResponse response = registrarUbicacion(request);
        return CompletableFuture.completedFuture(response);
    }
}
