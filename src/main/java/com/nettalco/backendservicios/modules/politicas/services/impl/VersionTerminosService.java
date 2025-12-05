package com.nettalco.backendservicios.modules.politicas.services.impl;

import com.nettalco.backendservicios.modules.politicas.repositories.VersionTerminosRepository;
import com.nettalco.backendservicios.modules.politicas.services.IVersionTerminosService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class VersionTerminosService implements IVersionTerminosService {
    
    @Autowired
    private VersionTerminosRepository termRpty;

    @Override
    public List<Object[]> findFirstByOrderByFechaVigenciaInicioDesc() {
        return termRpty.findFirstByOrderByFechaVigenciaInicioDesc();
    }
}
