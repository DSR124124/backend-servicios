package com.nettalco.backendservicios.modules.transporte.services.impl;

import com.nettalco.backendservicios.modules.transporte.dtos.EstadisticasRegistrosResponse;
import com.nettalco.backendservicios.modules.transporte.dtos.RegistroUsuarioRutaRequest;
import com.nettalco.backendservicios.modules.transporte.dtos.RegistroUsuarioRutaResponse;
import com.nettalco.backendservicios.modules.transporte.entities.RegistroUsuarioRuta;
import com.nettalco.backendservicios.modules.transporte.entities.Ruta;
import com.nettalco.backendservicios.modules.transporte.entities.RutaPunto;
import com.nettalco.backendservicios.modules.transporte.repositories.RegistroUsuarioRutaRepository;
import com.nettalco.backendservicios.modules.transporte.repositories.RutaPuntoRepository;
import com.nettalco.backendservicios.modules.transporte.repositories.RutaRepository;
import com.nettalco.backendservicios.modules.transporte.services.IRegistroUsuarioRutaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class RegistroUsuarioRutaService implements IRegistroUsuarioRutaService {
    
    @Autowired
    private RegistroUsuarioRutaRepository registroRepository;
    
    @Autowired
    private RutaRepository rutaRepository;
    
    @Autowired
    private RutaPuntoRepository rutaPuntoRepository;
    
    @Override
    @Transactional
    public RegistroUsuarioRutaResponse registrarRutaParadero(
            Integer idUsuario, 
            RegistroUsuarioRutaRequest request) {
        
        Ruta ruta = rutaRepository.findById(request.getIdRuta())
            .orElseThrow(() -> new IllegalArgumentException("La ruta con ID " + request.getIdRuta() + " no existe"));
        
        RutaPunto paradero = rutaPuntoRepository.findById(request.getIdParadero())
            .orElseThrow(() -> new IllegalArgumentException("El paradero con ID " + request.getIdParadero() + " no existe"));
        
        if (!paradero.getRuta().getIdRuta().equals(ruta.getIdRuta())) {
            throw new IllegalArgumentException("El paradero no pertenece a la ruta seleccionada");
        }
        
        // Obtener todos los registros del usuario
        java.util.List<RegistroUsuarioRuta> registrosUsuario = registroRepository.findAllByUsuarioId(idUsuario);
        
        RegistroUsuarioRuta registro;
        boolean esActualizacion = false;
        
        if (!registrosUsuario.isEmpty()) {
            // Usar el último registro del usuario y actualizarlo
            registro = registrosUsuario.get(0);
            esActualizacion = true;
            
            // Eliminar duplicados (todos los demás registros del usuario)
            if (registrosUsuario.size() > 1) {
                java.util.List<RegistroUsuarioRuta> duplicados = registrosUsuario.subList(1, registrosUsuario.size());
                registroRepository.deleteAll(duplicados);
            }
        } else {
            // Si no existe ningún registro, crear uno nuevo
            registro = new RegistroUsuarioRuta();
            registro.setIdUsuario(idUsuario);
        }
        
        // Actualizar los campos del registro (ruta y paradero pueden cambiar)
        registro.setRuta(ruta);
        registro.setParadero(paradero);
        registro.setFechaRegistro(OffsetDateTime.now());
        registro.setObservacion(request.getObservacion());
        
        RegistroUsuarioRuta registroGuardado = registroRepository.save(registro);
        
        String mensaje = esActualizacion
            ? "Registro de ruta y paradero actualizado exitosamente"
            : "Registro de ruta y paradero guardado exitosamente";
        
        return new RegistroUsuarioRutaResponse(
            registroGuardado.getIdRegistro(),
            registroGuardado.getIdUsuario(),
            ruta.getIdRuta(),
            ruta.getNombre(),
            paradero.getIdPunto(),
            paradero.getNombreParadero() != null ? paradero.getNombreParadero() : "Paradero " + paradero.getOrden(),
            registroGuardado.getFechaRegistro(),
            mensaje
        );
    }
    
    @Override
    @Transactional(readOnly = true)
    public EstadisticasRegistrosResponse obtenerEstadisticas() {
        // KPIs principales
        Long totalPersonasRegistradas = registroRepository.count();
        Long personasRegistradasHoy = registroRepository.countRegistrosHoy();
        
        // 1. HEATMAP: Hora vs Paradero
        List<Object[]> datosHeatmapRaw = registroRepository.obtenerDatosHeatmap();
        List<EstadisticasRegistrosResponse.DatoHeatmap> datosHeatmap = new ArrayList<>();
        for (Object[] data : datosHeatmapRaw) {
            Integer hora = ((Number) data[0]).intValue();
            Integer idParadero = ((Number) data[1]).intValue();
            String nombreParadero = (String) data[2];
            Long cantidadUsuarios = ((Number) data[3]).longValue();
            
            datosHeatmap.add(new EstadisticasRegistrosResponse.DatoHeatmap(
                hora, idParadero, nombreParadero, cantidadUsuarios
            ));
        }
        
        // 2. STACKED BAR: Ruta con segmentos por Paradero
        List<Object[]> datosStackedBarRaw = registroRepository.obtenerDatosStackedBar();
        java.util.Map<Integer, EstadisticasRegistrosResponse.DatoStackedBar> rutasMap = new java.util.HashMap<>();
        
        for (Object[] data : datosStackedBarRaw) {
            Integer idRuta = ((Number) data[0]).intValue();
            String nombreRuta = (String) data[1];
            Integer idParadero = ((Number) data[2]).intValue();
            String nombreParadero = (String) data[3];
            Long cantidadUsuarios = ((Number) data[4]).longValue();
            
            rutasMap.computeIfAbsent(idRuta, k -> {
                return new EstadisticasRegistrosResponse.DatoStackedBar(
                    idRuta, nombreRuta, new ArrayList<>(), 0L
                );
            });
            
            EstadisticasRegistrosResponse.DatoStackedBar ruta = rutasMap.get(idRuta);
            List<EstadisticasRegistrosResponse.DatoStackedBar.SegmentoParadero> segmentos = 
                new ArrayList<>(ruta.segmentosParaderos());
            segmentos.add(new EstadisticasRegistrosResponse.DatoStackedBar.SegmentoParadero(
                idParadero, nombreParadero, cantidadUsuarios
            ));
            
            Long nuevoTotal = ruta.totalUsuarios() + cantidadUsuarios;
            rutasMap.put(idRuta, new EstadisticasRegistrosResponse.DatoStackedBar(
                idRuta, nombreRuta, segmentos, nuevoTotal
            ));
        }
        
        List<EstadisticasRegistrosResponse.DatoStackedBar> datosStackedBar = 
            new ArrayList<>(rutasMap.values());
        datosStackedBar.sort((a, b) -> Long.compare(b.totalUsuarios(), a.totalUsuarios()));
        
        // 3. LINE CHART: Hora vs Cantidad por Ruta
        List<Object[]> datosLineChartRaw = registroRepository.obtenerDatosLineChart();
        java.util.Map<Integer, List<EstadisticasRegistrosResponse.DatoLineChart.ValorPorRuta>> lineChartMap = 
            new java.util.HashMap<>();
        
        for (Object[] data : datosLineChartRaw) {
            Integer hora = ((Number) data[0]).intValue();
            Integer idRuta = ((Number) data[1]).intValue();
            String nombreRuta = (String) data[2];
            Long cantidadUsuarios = ((Number) data[3]).longValue();
            
            lineChartMap.computeIfAbsent(hora, k -> new ArrayList<>())
                .add(new EstadisticasRegistrosResponse.DatoLineChart.ValorPorRuta(
                    idRuta, nombreRuta, cantidadUsuarios
                ));
        }
        
        List<EstadisticasRegistrosResponse.DatoLineChart> datosLineChart = new ArrayList<>();
        for (int hora = 0; hora < 24; hora++) {
            List<EstadisticasRegistrosResponse.DatoLineChart.ValorPorRuta> valores = 
                lineChartMap.getOrDefault(hora, new ArrayList<>());
            datosLineChart.add(new EstadisticasRegistrosResponse.DatoLineChart(hora, valores));
        }
        
        return new EstadisticasRegistrosResponse(
            totalPersonasRegistradas,
            personasRegistradasHoy,
            datosHeatmap,
            datosStackedBar,
            datosLineChart
        );
    }
}

