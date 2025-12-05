package com.nettalco.backendservicios.modules.politicas.services;

import com.nettalco.backendservicios.modules.politicas.entities.VersionPrivacidad;
import java.util.List;
import java.util.Optional;

public interface IVersionPrivacidadService {
    
    List<VersionPrivacidad> listar();
    
    Optional<VersionPrivacidad> obtenerPorId(Integer id);
    
    VersionPrivacidad crear(VersionPrivacidad version);
    
    VersionPrivacidad actualizar(Integer id, VersionPrivacidad version);
    
    void eliminar(Integer id);
    
    List<Object[]> findFirstByOrderByFechaVigenciaInicioDesc();
}
