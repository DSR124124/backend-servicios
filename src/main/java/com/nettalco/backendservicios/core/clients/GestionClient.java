package com.nettalco.backendservicios.core.clients;

import org.springframework.beans.factory.annotation.Value;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;
import java.util.Map;

/**
 * Cliente para consumir servicios del backend de gestión
 */
@Component
public class GestionClient {
    
    private static final Logger logger = LoggerFactory.getLogger(GestionClient.class);
    
    private final RestTemplate restTemplate;
    private final String gestionBaseUrl;
    
    public GestionClient(RestTemplate restTemplate, 
                        @Value("${backend.gestion.url}") String gestionBaseUrl) {
        this.restTemplate = restTemplate;
        this.gestionBaseUrl = gestionBaseUrl;
    }
    
    /**
     * Obtiene un usuario por ID desde el backend de gestión
     */
    public Map<String, Object> obtenerUsuario(Integer idUsuario, String token) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer " + token);
            HttpEntity<String> entity = new HttpEntity<>(headers);
            
            String url = gestionBaseUrl + "/api/usuarios/" + idUsuario;
            @SuppressWarnings("unchecked")
            ResponseEntity<Map<String, Object>> response = restTemplate.exchange(
                url, 
                HttpMethod.GET, 
                entity, 
                (Class<Map<String, Object>>) (Class<?>) Map.class
            );
            
            return response.getBody();
        } catch (Exception e) {
            return null;
        }
    }
    
    /**
     * Obtiene usuarios por nombre de rol desde el backend de gestión
     * @param nombreRol Nombre del rol (ej: "Conductor")
     * @param token Token JWT para autenticación
     * @return Lista de usuarios con ese rol
     */
    public List<Map<String, Object>> obtenerUsuariosPorNombreRol(String nombreRol, String token) {
        if (token == null || token.trim().isEmpty()) {
            logger.error("Token JWT vacío o nulo al intentar obtener usuarios por rol: {}", nombreRol);
            throw new IllegalArgumentException("Token JWT requerido para obtener usuarios del backend-gestion");
        }
        
        String tokenTrimmed = token.trim();
        
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer " + tokenTrimmed);
            HttpEntity<String> entity = new HttpEntity<>(headers);
            
            String url = UriComponentsBuilder.fromUriString(gestionBaseUrl + "/api/usuarios/por-nombre-rol")
                .queryParam("nombreRol", nombreRol)
                .toUriString();
            
            logger.info("Consultando backend-gestion: {} con token de longitud: {} (primeros 20 chars: {}...)", 
                url, tokenTrimmed.length(), 
                tokenTrimmed.length() > 20 ? tokenTrimmed.substring(0, 20) : tokenTrimmed);
            
            ParameterizedTypeReference<List<Map<String, Object>>> responseType = 
                new ParameterizedTypeReference<List<Map<String, Object>>>() {};
            
            ResponseEntity<List<Map<String, Object>>> response = restTemplate.exchange(
                url, 
                HttpMethod.GET, 
                entity, 
                responseType
            );
            
            List<Map<String, Object>> usuarios = response.getBody() != null ? response.getBody() : List.of();
            logger.debug("Obtenidos {} usuarios con rol '{}' del backend-gestion", usuarios.size(), nombreRol);
            return usuarios;
        } catch (RestClientException e) {
            String errorMsg = e.getMessage() != null ? e.getMessage() : "Error desconocido";
            logger.error("Error al consultar backend-gestion para obtener usuarios con rol '{}': {}", nombreRol, errorMsg);
            
            // Verificar si es un error 403 (Forbidden) o 401 (Unauthorized)
            if (errorMsg.contains("403") || errorMsg.contains("Forbidden")) {
                logger.error("Error 403 Forbidden: El token JWT fue rechazado por el backend-gestion. Posibles causas:");
                logger.error("  - Token expirado");
                logger.error("  - Token inválido o corrupto");
                logger.error("  - Secret key diferente entre backends");
                logger.error("  - Token no incluye los claims necesarios");
                throw new IllegalArgumentException("Token JWT inválido o expirado. Por favor, inicie sesión nuevamente.");
            } else if (errorMsg.contains("401") || errorMsg.contains("Unauthorized")) {
                logger.error("Error 401 Unauthorized: Token no proporcionado o formato incorrecto");
                throw new IllegalArgumentException("Token JWT no válido. Por favor, inicie sesión nuevamente.");
            }
            throw new RuntimeException("Error al comunicarse con el backend-gestion: " + errorMsg, e);
        } catch (Exception e) {
            logger.error("Error inesperado al obtener usuarios por rol '{}': {}", nombreRol, e.getMessage(), e);
            throw new RuntimeException("Error al obtener usuarios del backend-gestion: " + e.getMessage(), e);
        }
    }
}

