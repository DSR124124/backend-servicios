package com.nettalco.backendservicios.modules.politicas.services;

import java.util.List;

public interface IVersionTerminosService {

    List<Object[]> findFirstByOrderByFechaVigenciaInicioDesc();
}
