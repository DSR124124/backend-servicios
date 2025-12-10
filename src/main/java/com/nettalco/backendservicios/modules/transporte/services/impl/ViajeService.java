package com.nettalco.backendservicios.modules.transporte.services.impl;

import com.nettalco.backendservicios.modules.transporte.dtos.*;
import com.nettalco.backendservicios.modules.transporte.dtos.TripDetailResponse.RoutePointResponse;
import com.nettalco.backendservicios.modules.transporte.entities.*;
import com.nettalco.backendservicios.modules.transporte.repositories.*;
import com.nettalco.backendservicios.modules.transporte.services.IViajeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.OffsetDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class ViajeService implements IViajeService {
    
    @Autowired
    private ViajeRepository viajeRepository;
    
    @Autowired
    private UbicacionTiempoRealRepository ubicacionRepository;
    
    @Autowired
    private ConductorDetalleRepository conductorDetalleRepository;
    
    @Autowired
    private LlegadaParaderoRepository llegadaParaderoRepository;
    
    @Override
    @Transactional(readOnly = true)
    public Optional<TripDetailResponse> obtenerDetalleViaje(Integer idViaje) {
        return viajeRepository.findByIdWithRelations(idViaje)
            .map(this::convertirATripDetailResponse);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Optional<BusLocationResponse> obtenerUbicacionActual(Integer idViaje) {
        if (!viajeRepository.existsById(idViaje)) {
            return Optional.empty();
        }
        
        List<UbicacionTiempoReal> ubicaciones = ubicacionRepository
            .findByViajeIdOrderByFechaRegistroDesc(idViaje);
        
        if (ubicaciones.isEmpty()) {
            return Optional.empty();
        }
        
        UbicacionTiempoReal ultimaUbicacion = ubicaciones.get(0);
        
        return Optional.of(new BusLocationResponse(
            ultimaUbicacion.getLatitud().doubleValue(),
            ultimaUbicacion.getLongitud().doubleValue(),
            ultimaUbicacion.getRumbo() != null ? ultimaUbicacion.getRumbo().doubleValue() : null,
            ultimaUbicacion.getVelocidadKmh() != null ? ultimaUbicacion.getVelocidadKmh().doubleValue() : null,
            ultimaUbicacion.getFechaRegistro()
        ));
    }
    
    private TripDetailResponse convertirATripDetailResponse(Viaje viaje) {
        String busPlate = viaje.getBus() != null ? viaje.getBus().getPlaca() : "";
        String busModel = viaje.getBus() != null ? viaje.getBus().getModelo() : null;
        
        String driverName = "Conductor " + viaje.getIdConductor();
        if (viaje.getIdConductor() != null) {
            Optional<ConductorDetalle> conductorOpt = conductorDetalleRepository
                .findByIdUsuarioGestion(viaje.getIdConductor());
            if (conductorOpt.isPresent()) {
                ConductorDetalle conductor = conductorOpt.get();
                if (conductor.getLicenciaNumero() != null && !conductor.getLicenciaNumero().isEmpty()) {
                    driverName = "Conductor " + conductor.getLicenciaNumero();
                }
            }
        }
        
        List<RoutePointResponse> routePoints = viaje.getRuta().getPuntos().stream()
            .map(p -> new RoutePointResponse(
                p.getLatitud().doubleValue(),
                p.getLongitud().doubleValue(),
                p.getNombreParadero(),
                p.getOrden()
            ))
            .collect(Collectors.toList());
        
        return new TripDetailResponse(
            viaje.getIdViaje(),
            busPlate,
            busModel,
            driverName,
            null,
            routePoints,
            viaje.getEstado() != null ? viaje.getEstado() : "desconocido",
            null
        );
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<ViajeActivoResponse> obtenerViajesActivosPorRuta(Integer idRuta) {
        List<Viaje> viajes = viajeRepository.findByRutaIdAndActivos(idRuta);
        
        return viajes.stream()
            .map(this::convertirAViajeActivoResponse)
            .collect(Collectors.toList());
    }
    
    private ViajeActivoResponse convertirAViajeActivoResponse(Viaje viaje) {
        String busPlate = viaje.getBus() != null ? viaje.getBus().getPlaca() : "";
        String busModel = viaje.getBus() != null ? viaje.getBus().getModelo() : null;
        
        String driverName = "Conductor " + viaje.getIdConductor();
        if (viaje.getIdConductor() != null) {
            Optional<ConductorDetalle> conductorOpt = conductorDetalleRepository
                .findByIdUsuarioGestion(viaje.getIdConductor());
            if (conductorOpt.isPresent()) {
                ConductorDetalle conductor = conductorOpt.get();
                if (conductor.getLicenciaNumero() != null && !conductor.getLicenciaNumero().isEmpty()) {
                    driverName = "Conductor " + conductor.getLicenciaNumero();
                }
            }
        }
        
        return new ViajeActivoResponse(
            viaje.getIdViaje(),
            viaje.getRuta().getIdRuta(),
            busPlate,
            busModel,
            driverName,
            viaje.getEstado() != null ? viaje.getEstado() : "desconocido",
            viaje.getFechaInicioProgramada(),
            viaje.getFechaFinProgramada()
        );
    }
    
    // =====================================================
    // Metodos para conductores
    // =====================================================
    
    @Override
    @Transactional(readOnly = true)
    public List<ViajeConductorResponse> obtenerViajesConductor(Integer idConductor) {
        List<Viaje> viajes = viajeRepository.findByConductorIdWithRelations(idConductor);
        
        return viajes.stream()
            .map(this::convertirAViajeConductorResponse)
            .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public Optional<ViajeActivoConductorResponse> obtenerViajeActivoConductor(Integer idConductor) {
        return viajeRepository.findViajeActivoByConductor(idConductor)
            .map(this::convertirAViajeActivoConductorResponse);
    }
    
    @Override
    @Transactional
    public IniciarViajeResponse iniciarViaje(Integer idViaje, Integer idConductor) {
        if (viajeRepository.existsViajeActivoByConductor(idConductor)) {
            throw new IllegalArgumentException(
                "Ya tienes un viaje en curso. Finalizalo antes de iniciar otro.");
        }
        
        Viaje viaje = viajeRepository.findByIdAndConductor(idViaje, idConductor)
            .orElseThrow(() -> new IllegalArgumentException(
                "Viaje no encontrado o no tienes permiso para iniciarlo."));
        
        if (!"programado".equals(viaje.getEstado())) {
            throw new IllegalArgumentException(
                "Solo se pueden iniciar viajes programados. Estado actual: " + viaje.getEstado());
        }
        
        OffsetDateTime ahora = OffsetDateTime.now();
        viaje.setEstado("en_curso");
        viaje.setFechaInicioReal(ahora);
        
        viajeRepository.save(viaje);
        
        return new IniciarViajeResponse(
            viaje.getIdViaje(),
            viaje.getEstado(),
            viaje.getFechaInicioReal(),
            "Viaje iniciado exitosamente"
        );
    }
    
    @Override
    @Transactional
    public FinalizarViajeResponse finalizarViaje(Integer idViaje, Integer idConductor) {
        Viaje viaje = viajeRepository.findByIdAndConductor(idViaje, idConductor)
            .orElseThrow(() -> new IllegalArgumentException(
                "Viaje no encontrado o no tienes permiso para finalizarlo."));
        
        if (!"en_curso".equals(viaje.getEstado())) {
            throw new IllegalArgumentException(
                "Solo se pueden finalizar viajes en curso. Estado actual: " + viaje.getEstado());
        }
        
        OffsetDateTime ahora = OffsetDateTime.now();
        viaje.setEstado("finalizado");
        viaje.setFechaFinReal(ahora);
        
        viajeRepository.save(viaje);
        
        Long duracionMinutos = null;
        if (viaje.getFechaInicioReal() != null) {
            Duration duracion = Duration.between(viaje.getFechaInicioReal(), ahora);
            duracionMinutos = duracion.toMinutes();
        }
        
        return new FinalizarViajeResponse(
            viaje.getIdViaje(),
            viaje.getEstado(),
            viaje.getFechaInicioReal(),
            viaje.getFechaFinReal(),
            duracionMinutos,
            "Viaje finalizado exitosamente"
        );
    }
    
    private ViajeConductorResponse convertirAViajeConductorResponse(Viaje viaje) {
        String busPlaca = viaje.getBus() != null ? viaje.getBus().getPlaca() : "";
        String busModelo = viaje.getBus() != null ? viaje.getBus().getModelo() : null;
        Integer busCapacidad = viaje.getBus() != null ? viaje.getBus().getCapacidad() : null;
        
        String nombreRuta = viaje.getRuta() != null ? viaje.getRuta().getNombre() : "";
        String descripcionRuta = viaje.getRuta() != null ? viaje.getRuta().getDescripcion() : "";
        Integer totalParaderos = viaje.getRuta() != null && viaje.getRuta().getPuntos() != null 
            ? viaje.getRuta().getPuntos().size() 
            : 0;
        
        return new ViajeConductorResponse(
            viaje.getIdViaje(),
            viaje.getRuta() != null ? viaje.getRuta().getIdRuta() : null,
            nombreRuta,
            descripcionRuta,
            busPlaca,
            busModelo,
            busCapacidad,
            viaje.getEstado() != null ? viaje.getEstado() : "desconocido",
            viaje.getFechaInicioProgramada(),
            viaje.getFechaFinProgramada(),
            viaje.getFechaInicioReal(),
            viaje.getFechaFinReal(),
            totalParaderos
        );
    }
    
    private ViajeActivoConductorResponse convertirAViajeActivoConductorResponse(Viaje viaje) {
        String busPlaca = viaje.getBus() != null ? viaje.getBus().getPlaca() : "";
        String busModelo = viaje.getBus() != null ? viaje.getBus().getModelo() : null;
        Integer busCapacidad = viaje.getBus() != null ? viaje.getBus().getCapacidad() : null;
        
        String nombreRuta = viaje.getRuta() != null ? viaje.getRuta().getNombre() : "";
        String descripcionRuta = viaje.getRuta() != null ? viaje.getRuta().getDescripcion() : "";
        
        // Obtener el máximo orden visitado para determinar el estado de cada paradero
        Integer maxOrdenVisitado = llegadaParaderoRepository.findMaxOrdenVisitadoByViajeId(viaje.getIdViaje());
        int ultimoOrdenVisitado = (maxOrdenVisitado == null) ? 0 : maxOrdenVisitado;
        
        List<ViajeActivoConductorResponse.ParaderoInfo> paraderos = List.of();
        if (viaje.getRuta() != null && viaje.getRuta().getPuntos() != null) {
            paraderos = viaje.getRuta().getPuntos().stream()
                .sorted(Comparator.comparing(RutaPunto::getOrden))
                .map(punto -> {
                    // Determinar el estado del paradero
                    String estadoParadero;
                    if (punto.getOrden() <= ultimoOrdenVisitado) {
                        estadoParadero = "visitado";
                    } else if (punto.getOrden() == ultimoOrdenVisitado + 1) {
                        estadoParadero = "siguiente"; // Es el próximo a visitar
                    } else {
                        estadoParadero = "pendiente";
                    }
                    
                    return new ViajeActivoConductorResponse.ParaderoInfo(
                        punto.getIdPunto(),
                    punto.getOrden(),
                    punto.getNombreParadero(),
                    punto.getLatitud() != null ? punto.getLatitud().doubleValue() : null,
                        punto.getLongitud() != null ? punto.getLongitud().doubleValue() : null,
                        estadoParadero
                    );
                })
                .collect(Collectors.toList());
        }
        
        return new ViajeActivoConductorResponse(
            viaje.getIdViaje(),
            viaje.getRuta() != null ? viaje.getRuta().getIdRuta() : null,
            nombreRuta,
            descripcionRuta,
            busPlaca,
            busModelo,
            busCapacidad,
            viaje.getEstado() != null ? viaje.getEstado() : "desconocido",
            viaje.getFechaInicioProgramada(),
            viaje.getFechaFinProgramada(),
            viaje.getFechaInicioReal(),
            paraderos,
            ultimoOrdenVisitado,
            viaje.getRuta() != null && viaje.getRuta().getPuntos() != null ? viaje.getRuta().getPuntos().size() : 0
        );
    }
    
    // =====================================================
    // Historial y estadisticas
    // =====================================================
    
    @Override
    @Transactional(readOnly = true)
    public List<HistorialViajeResponse> obtenerHistorial(Integer idConductor, int page, int size) {
        List<Viaje> historial = viajeRepository.findHistorialByConductor(idConductor);
        
        int start = page * size;
        int end = Math.min(start + size, historial.size());
        
        if (start >= historial.size()) {
            return List.of();
        }
        
        return historial.subList(start, end).stream()
            .map(this::convertirAHistorialViajeResponse)
            .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public EstadisticasConductorResponse obtenerEstadisticas(Integer idConductor) {
        List<Object[]> viajesPorEstado = viajeRepository.countViajesByEstado(idConductor);
        
        int totalViajes = 0;
        int viajesCompletados = 0;
        int viajesCancelados = 0;
        int viajesEnCurso = 0;
        int viajesProgramados = 0;
        
        for (Object[] row : viajesPorEstado) {
            String estado = (String) row[0];
            Long count = (Long) row[1];
            totalViajes += count.intValue();
            
            switch (estado) {
                case "finalizado" -> viajesCompletados = count.intValue();
                case "cancelado" -> viajesCancelados = count.intValue();
                case "en_curso" -> viajesEnCurso = count.intValue();
                case "programado" -> viajesProgramados = count.intValue();
            }
        }
        
        List<Viaje> viajesFinalizados = viajeRepository.findHistorialByConductor(idConductor);
        
        long totalMinutos = calcularTotalMinutos(viajesFinalizados);
        double promedioMinutos = viajesCompletados > 0 ? (double) totalMinutos / viajesCompletados : 0;
        
        OffsetDateTime ahora = OffsetDateTime.now();
        
        OffsetDateTime inicioHoy = ahora.toLocalDate().atStartOfDay(ahora.getOffset()).toOffsetDateTime();
        OffsetDateTime finHoy = inicioHoy.plusDays(1);
        Long viajesHoy = viajeRepository.countViajesFinalizadosEnRango(idConductor, inicioHoy, finHoy);
        Long minutosHoy = calcularMinutosEnRango(idConductor, inicioHoy, finHoy);
        
        OffsetDateTime inicioSemana = ahora.toLocalDate()
            .minusDays(ahora.getDayOfWeek().getValue() - 1)
            .atStartOfDay(ahora.getOffset())
            .toOffsetDateTime();
        OffsetDateTime finSemana = inicioSemana.plusWeeks(1);
        Long viajesEstaSemana = viajeRepository.countViajesFinalizadosEnRango(idConductor, inicioSemana, finSemana);
        Long minutosEstaSemana = calcularMinutosEnRango(idConductor, inicioSemana, finSemana);
        
        OffsetDateTime inicioMes = ahora.toLocalDate()
            .withDayOfMonth(1)
            .atStartOfDay(ahora.getOffset())
            .toOffsetDateTime();
        OffsetDateTime finMes = inicioMes.plusMonths(1);
        Long viajesEsteMes = viajeRepository.countViajesFinalizadosEnRango(idConductor, inicioMes, finMes);
        Long minutosEsteMes = calcularMinutosEnRango(idConductor, inicioMes, finMes);
        
        return new EstadisticasConductorResponse(
            totalViajes,
            viajesCompletados,
            viajesCancelados,
            viajesEnCurso,
            viajesProgramados,
            totalMinutos,
            promedioMinutos,
            viajesEsteMes.intValue(),
            minutosEsteMes,
            viajesEstaSemana.intValue(),
            minutosEstaSemana,
            viajesHoy.intValue(),
            minutosHoy
        );
    }
    
    private long calcularTotalMinutos(List<Viaje> viajes) {
        return viajes.stream()
            .filter(v -> v.getFechaInicioReal() != null && v.getFechaFinReal() != null)
            .mapToLong(v -> Duration.between(v.getFechaInicioReal(), v.getFechaFinReal()).toMinutes())
            .sum();
    }
    
    private Long calcularMinutosEnRango(Integer idConductor, OffsetDateTime desde, OffsetDateTime hasta) {
        List<Viaje> viajes = viajeRepository.findViajesFinalizadosEnRango(idConductor, desde, hasta);
        return calcularTotalMinutos(viajes);
    }
    
    private HistorialViajeResponse convertirAHistorialViajeResponse(Viaje viaje) {
        String busPlaca = viaje.getBus() != null ? viaje.getBus().getPlaca() : "";
        String busModelo = viaje.getBus() != null ? viaje.getBus().getModelo() : null;
        String nombreRuta = viaje.getRuta() != null ? viaje.getRuta().getNombre() : "";
        Integer totalParaderos = viaje.getRuta() != null && viaje.getRuta().getPuntos() != null 
            ? viaje.getRuta().getPuntos().size() 
            : 0;
        
        Long duracionMinutos = null;
        if (viaje.getFechaInicioReal() != null && viaje.getFechaFinReal() != null) {
            duracionMinutos = Duration.between(viaje.getFechaInicioReal(), viaje.getFechaFinReal()).toMinutes();
        }
        
        return new HistorialViajeResponse(
            viaje.getIdViaje(),
            viaje.getRuta() != null ? viaje.getRuta().getIdRuta() : null,
            nombreRuta,
            busPlaca,
            busModelo,
            viaje.getEstado() != null ? viaje.getEstado() : "desconocido",
            viaje.getFechaInicioProgramada(),
            viaje.getFechaFinProgramada(),
            viaje.getFechaInicioReal(),
            viaje.getFechaFinReal(),
            duracionMinutos,
            totalParaderos
        );
    }
    
    // =====================================================
    // Llegada a paraderos
    // =====================================================
    
    @Override
    @Transactional
    public LlegadaParaderoResponse marcarLlegadaParadero(
            Integer idViaje, 
            Integer idParadero, 
            Integer idConductor,
            BigDecimal latitud,
            BigDecimal longitud) {
        
        Viaje viaje = viajeRepository.findByIdAndConductor(idViaje, idConductor)
            .orElseThrow(() -> new IllegalArgumentException(
                "Viaje no encontrado o no tienes permiso para actualizarlo."));
        
        if (!"en_curso".equals(viaje.getEstado())) {
            throw new IllegalArgumentException(
                "Solo se puede marcar llegada en viajes en curso. Estado actual: " + viaje.getEstado());
        }
        
        RutaPunto paradero = viaje.getRuta().getPuntos().stream()
            .filter(p -> p.getIdPunto().equals(idParadero))
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException(
                "El paradero no pertenece a la ruta de este viaje."));
        
        if (llegadaParaderoRepository.existsByViajeIdAndParaderoId(idViaje, idParadero)) {
            throw new IllegalArgumentException(
                "Ya se registro la llegada a este paradero.");
        }
        
        // =====================================================
        // Validación de orden secuencial - NUEVO
        // =====================================================
        Integer maxOrdenVisitado = llegadaParaderoRepository.findMaxOrdenVisitadoByViajeId(idViaje);
        int siguienteOrdenEsperado = (maxOrdenVisitado == null) ? 1 : maxOrdenVisitado + 1;
        
        if (!paradero.getOrden().equals(siguienteOrdenEsperado)) {
            // Buscar el nombre del siguiente paradero esperado
            String nombreParaderoEsperado = viaje.getRuta().getPuntos().stream()
                .filter(p -> p.getOrden().equals(siguienteOrdenEsperado))
                .findFirst()
                .map(RutaPunto::getNombreParadero)
                .orElse("Paradero #" + siguienteOrdenEsperado);
            
            throw new IllegalArgumentException(
                "Debes marcar los paraderos en orden. El siguiente paradero es: " + nombreParaderoEsperado + 
                " (orden " + siguienteOrdenEsperado + ")");
        }
        
        OffsetDateTime ahora = OffsetDateTime.now();
        LlegadaParadero llegada = new LlegadaParadero();
        llegada.setViaje(viaje);
        llegada.setParadero(paradero);
        llegada.setFechaLlegada(ahora);
        llegada.setLatitudLlegada(latitud);
        llegada.setLongitudLlegada(longitud);
        
        if (latitud != null && longitud != null && 
            paradero.getLatitud() != null && paradero.getLongitud() != null) {
            double distancia = calcularDistanciaMetros(
                latitud.doubleValue(), longitud.doubleValue(),
                paradero.getLatitud().doubleValue(), paradero.getLongitud().doubleValue()
            );
            llegada.setDistanciaParadero(distancia);
        }
        
        llegadaParaderoRepository.save(llegada);
        
        int totalParaderos = viaje.getRuta().getPuntos().size();
        int paraderosVisitados = llegadaParaderoRepository.countByViajeId(idViaje).intValue();
        
        boolean esUltimoParadero = paradero.getOrden().equals(totalParaderos);
        
        String mensaje = esUltimoParadero 
            ? "Llegada al ultimo paradero registrada! Puedes finalizar el viaje."
            : "Llegada registrada exitosamente.";
        
        return new LlegadaParaderoResponse(
            llegada.getIdLlegada(),
            idViaje,
            idParadero,
            paradero.getOrden(),
            paradero.getNombreParadero(),
            ahora,
            latitud != null ? latitud.doubleValue() : null,
            longitud != null ? longitud.doubleValue() : null,
            paraderosVisitados,
            totalParaderos,
            esUltimoParadero,
            mensaje
        );
    }
    
    @Override
    @Transactional(readOnly = true)
    public ProximoParaderoResponse obtenerProximoParadero(Integer idViaje, Integer idConductor) {
        Viaje viaje = viajeRepository.findByIdAndConductor(idViaje, idConductor)
            .orElseThrow(() -> new IllegalArgumentException(
                "Viaje no encontrado o no tienes permiso para verlo."));
        
        if (!"en_curso".equals(viaje.getEstado())) {
            throw new IllegalArgumentException(
                "El viaje no esta en curso. Estado actual: " + viaje.getEstado());
        }
        
        int totalParaderos = viaje.getRuta().getPuntos().size();
        Integer maxOrdenVisitado = llegadaParaderoRepository.findMaxOrdenVisitadoByViajeId(idViaje);
        int paraderosVisitados = (maxOrdenVisitado == null) ? 0 : maxOrdenVisitado;
        int siguienteOrden = paraderosVisitados + 1;
        
        // Verificar si ya se visitaron todos los paraderos
        if (paraderosVisitados >= totalParaderos) {
            return new ProximoParaderoResponse(
                idViaje,
                null,
                null,
                null,
                null,
                null,
                paraderosVisitados,
                totalParaderos,
                true,
                "Todos los paraderos han sido visitados. Puedes finalizar el viaje."
            );
        }
        
        // Buscar el siguiente paradero
        RutaPunto proximoParadero = viaje.getRuta().getPuntos().stream()
            .filter(p -> p.getOrden().equals(siguienteOrden))
            .findFirst()
            .orElseThrow(() -> new IllegalStateException(
                "No se encontro el paradero con orden " + siguienteOrden));
        
        String mensaje = paraderosVisitados == 0 
            ? "Primer paradero de la ruta. ¡Inicia el recorrido!"
            : "Siguiente paradero en la ruta.";
        
        return new ProximoParaderoResponse(
            idViaje,
            proximoParadero.getIdPunto(),
            proximoParadero.getOrden(),
            proximoParadero.getNombreParadero(),
            proximoParadero.getLatitud() != null ? proximoParadero.getLatitud().doubleValue() : null,
            proximoParadero.getLongitud() != null ? proximoParadero.getLongitud().doubleValue() : null,
            paraderosVisitados,
            totalParaderos,
            false,
            mensaje
        );
    }
    
    /**
     * Calcula la distancia en metros entre dos puntos geograficos usando la formula de Haversine
     */
    private double calcularDistanciaMetros(double lat1, double lon1, double lat2, double lon2) {
        final int R = 6371000;
        
        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);
        
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        
        return R * c;
    }
}
