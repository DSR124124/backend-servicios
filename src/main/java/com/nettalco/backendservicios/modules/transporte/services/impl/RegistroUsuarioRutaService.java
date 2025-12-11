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

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
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
        
        java.util.List<RegistroUsuarioRuta> registrosExistentes = registroRepository.findRegistrosExistentes(
            idUsuario, 
            request.getIdRuta(), 
            request.getIdParadero()
        );
        RegistroUsuarioRuta registro = registrosExistentes.isEmpty() 
            ? new RegistroUsuarioRuta() 
            : registrosExistentes.get(0);
        
        if (registro.getIdRegistro() == null) {
            registro.setIdUsuario(idUsuario);
        }
        
        registro.setRuta(ruta);
        registro.setParadero(paradero);
        registro.setFechaRegistro(OffsetDateTime.now());
        registro.setObservacion(request.getObservacion());
        
        RegistroUsuarioRuta registroGuardado = registroRepository.save(registro);
        
        String mensaje = registro.getIdRegistro() == null 
            ? "Registro de ruta y paradero guardado exitosamente"
            : "Registro de ruta y paradero actualizado exitosamente";
        
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
    public java.util.Optional<RegistroUsuarioRutaResponse> obtenerUltimoRegistro(Integer idUsuario) {
        java.util.List<RegistroUsuarioRuta> registros = registroRepository.findUltimosRegistrosByUsuarioId(idUsuario);
        
        if (registros.isEmpty()) {
            return java.util.Optional.empty();
        }
        
        RegistroUsuarioRuta registro = registros.get(0);
        Ruta ruta = registro.getRuta();
        RutaPunto paradero = registro.getParadero();
        
        return java.util.Optional.of(new RegistroUsuarioRutaResponse(
            registro.getIdRegistro(),
            registro.getIdUsuario(),
            ruta.getIdRuta(),
            ruta.getNombre(),
            paradero.getIdPunto(),
            paradero.getNombreParadero() != null ? paradero.getNombreParadero() : "Paradero " + paradero.getOrden(),
            registro.getFechaRegistro(),
            "Último registro encontrado"
        ));
    }
    
    @Override
    @Transactional(readOnly = true)
    public java.util.List<RegistroUsuarioRutaResponse> listarTodosLosRegistros() {
        return registroRepository.findAll().stream()
            .map(this::convertirARegistroResponse)
            .collect(java.util.stream.Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public java.util.List<RegistroUsuarioRutaResponse> listarRegistrosPorUsuario(Integer idUsuario) {
        return registroRepository.findByUsuarioIdOrderByFechaDesc(idUsuario).stream()
            .map(this::convertirARegistroResponse)
            .collect(java.util.stream.Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public java.util.List<RegistroUsuarioRutaResponse> listarRegistrosPorRuta(Integer idRuta) {
        return registroRepository.findByRutaIdOrderByFechaDesc(idRuta).stream()
            .map(this::convertirARegistroResponse)
            .collect(java.util.stream.Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public EstadisticasRegistrosResponse obtenerEstadisticas() {
        // KPIs principales
        Long totalRegistros = registroRepository.count();
        Long registrosHoy = registroRepository.countRegistrosHoy();
        
        OffsetDateTime inicioSemana = OffsetDateTime.now().minusDays(7);
        OffsetDateTime inicioMes = OffsetDateTime.now().minusDays(30);
        
        Long registrosEstaSemana = registroRepository.countRegistrosDesde(inicioSemana);
        Long registrosEsteMes = registroRepository.countRegistrosDesde(inicioMes);
        
        // Estadísticas por ruta
        List<Object[]> registrosPorRutaData = registroRepository.countRegistrosPorRuta();
        List<EstadisticasRegistrosResponse.RegistroPorRuta> registrosPorRuta = new ArrayList<>();
        if (totalRegistros > 0) {
            for (Object[] data : registrosPorRutaData) {
                Integer idRuta = (Integer) data[0];
                String nombreRuta = (String) data[1];
                Long cantidad = ((Number) data[2]).longValue();
                Double porcentaje = (cantidad.doubleValue() / totalRegistros) * 100;
                registrosPorRuta.add(new EstadisticasRegistrosResponse.RegistroPorRuta(
                    idRuta, nombreRuta, cantidad, porcentaje
                ));
            }
        }
        
        // Estadísticas por paradero
        List<Object[]> registrosPorParaderoData = registroRepository.countRegistrosPorParadero();
        List<EstadisticasRegistrosResponse.RegistroPorParadero> registrosPorParadero = new ArrayList<>();
        if (totalRegistros > 0) {
            for (Object[] data : registrosPorParaderoData) {
                Integer idParadero = (Integer) data[0];
                String nombreParadero = (String) data[1];
                Integer idRuta = (Integer) data[2];
                String nombreRuta = (String) data[3];
                Long cantidad = ((Number) data[4]).longValue();
                Double porcentaje = (cantidad.doubleValue() / totalRegistros) * 100;
                registrosPorParadero.add(new EstadisticasRegistrosResponse.RegistroPorParadero(
                    idParadero, nombreParadero != null ? nombreParadero : "Paradero " + idParadero,
                    idRuta, nombreRuta, cantidad, porcentaje
                ));
            }
        }
        
        // Estadísticas temporales - últimos 30 días
        OffsetDateTime fechaInicio30Dias = OffsetDateTime.now().minusDays(30);
        List<Object[]> registrosPorDiaData = registroRepository.countRegistrosPorDia(fechaInicio30Dias);
        List<EstadisticasRegistrosResponse.RegistroPorFecha> registrosPorDia = new ArrayList<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        for (Object[] data : registrosPorDiaData) {
            LocalDate fecha;
            if (data[0] instanceof java.sql.Date) {
                fecha = ((java.sql.Date) data[0]).toLocalDate();
            } else if (data[0] instanceof java.sql.Timestamp) {
                fecha = ((java.sql.Timestamp) data[0]).toLocalDateTime().toLocalDate();
            } else if (data[0] instanceof java.time.LocalDate) {
                fecha = (LocalDate) data[0];
            } else {
                // Intentar parsear como string
                fecha = LocalDate.parse(data[0].toString());
            }
            Long cantidad = ((Number) data[1]).longValue();
            registrosPorDia.add(new EstadisticasRegistrosResponse.RegistroPorFecha(
                fecha.format(formatter), "dia", cantidad
            ));
        }
        
        // Top usuarios (top 10)
        List<Object[]> registrosPorUsuarioData = registroRepository.countRegistrosPorUsuario();
        List<EstadisticasRegistrosResponse.RegistroPorUsuario> topUsuarios = new ArrayList<>();
        if (totalRegistros > 0) {
            int limit = Math.min(10, registrosPorUsuarioData.size());
            for (int i = 0; i < limit; i++) {
                Object[] data = registrosPorUsuarioData.get(i);
                Integer idUsuario = (Integer) data[0];
                Long cantidad = ((Number) data[1]).longValue();
                Double porcentaje = (cantidad.doubleValue() / totalRegistros) * 100;
                topUsuarios.add(new EstadisticasRegistrosResponse.RegistroPorUsuario(
                    idUsuario, cantidad, porcentaje
                ));
            }
        }
        
        // Estadísticas generales
        Double promedioRegistrosPorDia = totalRegistros > 0 && registrosPorDia.size() > 0
            ? registrosPorDia.stream().mapToLong(r -> r.cantidadRegistros()).average().orElse(0.0)
            : 0.0;
        
        Integer rutasMasUsadas = registrosPorRuta.size();
        Integer paraderosMasUsados = registrosPorParadero.size();
        
        // Para semanas y meses, agrupamos los datos por día
        List<EstadisticasRegistrosResponse.RegistroPorFecha> registrosPorSemana = new ArrayList<>();
        List<EstadisticasRegistrosResponse.RegistroPorFecha> registrosPorMes = new ArrayList<>();
        
        // Agrupar por semana (últimas 4 semanas)
        OffsetDateTime fechaInicio4Semanas = OffsetDateTime.now().minusDays(28);
        List<Object[]> registrosPorSemanaData = registroRepository.countRegistrosPorDia(fechaInicio4Semanas);
        java.util.Map<String, Long> semanaMap = new java.util.HashMap<>();
        for (Object[] data : registrosPorSemanaData) {
            LocalDate fecha;
            if (data[0] instanceof java.sql.Date) {
                fecha = ((java.sql.Date) data[0]).toLocalDate();
            } else if (data[0] instanceof java.sql.Timestamp) {
                fecha = ((java.sql.Timestamp) data[0]).toLocalDateTime().toLocalDate();
            } else if (data[0] instanceof java.time.LocalDate) {
                fecha = (LocalDate) data[0];
            } else {
                fecha = LocalDate.parse(data[0].toString());
            }
            Long cantidad = ((Number) data[1]).longValue();
            String semana = fecha.format(DateTimeFormatter.ofPattern("yyyy-'W'ww"));
            semanaMap.put(semana, semanaMap.getOrDefault(semana, 0L) + cantidad);
        }
        semanaMap.forEach((semana, cantidad) -> {
            registrosPorSemana.add(new EstadisticasRegistrosResponse.RegistroPorFecha(semana, "semana", cantidad));
        });
        registrosPorSemana.sort(Comparator.comparing(EstadisticasRegistrosResponse.RegistroPorFecha::fecha).reversed());
        
        // Agrupar por mes (últimos 12 meses)
        OffsetDateTime fechaInicio12Meses = OffsetDateTime.now().minusDays(365);
        List<Object[]> registrosPorMesData = registroRepository.countRegistrosPorDia(fechaInicio12Meses);
        java.util.Map<String, Long> mesMap = new java.util.HashMap<>();
        for (Object[] data : registrosPorMesData) {
            LocalDate fecha;
            if (data[0] instanceof java.sql.Date) {
                fecha = ((java.sql.Date) data[0]).toLocalDate();
            } else if (data[0] instanceof java.sql.Timestamp) {
                fecha = ((java.sql.Timestamp) data[0]).toLocalDateTime().toLocalDate();
            } else if (data[0] instanceof java.time.LocalDate) {
                fecha = (LocalDate) data[0];
            } else {
                fecha = LocalDate.parse(data[0].toString());
            }
            Long cantidad = ((Number) data[1]).longValue();
            String mes = fecha.format(DateTimeFormatter.ofPattern("yyyy-MM"));
            mesMap.put(mes, mesMap.getOrDefault(mes, 0L) + cantidad);
        }
        mesMap.forEach((mes, cantidad) -> {
            registrosPorMes.add(new EstadisticasRegistrosResponse.RegistroPorFecha(mes, "mes", cantidad));
        });
        registrosPorMes.sort(Comparator.comparing(EstadisticasRegistrosResponse.RegistroPorFecha::fecha).reversed());
        
        return new EstadisticasRegistrosResponse(
            totalRegistros,
            registrosHoy,
            registrosEstaSemana,
            registrosEsteMes,
            registrosPorRuta,
            registrosPorParadero,
            registrosPorDia,
            registrosPorSemana,
            registrosPorMes,
            topUsuarios,
            promedioRegistrosPorDia,
            rutasMasUsadas,
            paraderosMasUsados
        );
    }
    
    private RegistroUsuarioRutaResponse convertirARegistroResponse(RegistroUsuarioRuta registro) {
        Ruta ruta = registro.getRuta();
        RutaPunto paradero = registro.getParadero();
        return new RegistroUsuarioRutaResponse(
            registro.getIdRegistro(),
            registro.getIdUsuario(),
            ruta.getIdRuta(),
            ruta.getNombre(),
            paradero.getIdPunto(),
            paradero.getNombreParadero() != null ? paradero.getNombreParadero() : "Paradero " + paradero.getOrden(),
            registro.getFechaRegistro(),
            "Registro encontrado"
        );
    }
}

