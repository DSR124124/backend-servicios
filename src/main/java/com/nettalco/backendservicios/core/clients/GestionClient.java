package com.nettalco.backendservicios.core.clients;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;
import java.util.Map;

/**
 * Cliente para consumir servicios del backend de gestión
 */
@Component
public class GestionClient {
    
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
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer " + token);
            HttpEntity<String> entity = new HttpEntity<>(headers);
            
            String url = UriComponentsBuilder.fromUriString(gestionBaseUrl + "/api/usuarios/por-nombre-rol")
                .queryParam("nombreRol", nombreRol)
                .toUriString();
            
            ParameterizedTypeReference<List<Map<String, Object>>> responseType = 
                new ParameterizedTypeReference<List<Map<String, Object>>>() {};
            
            ResponseEntity<List<Map<String, Object>>> response = restTemplate.exchange(
                url, 
                HttpMethod.GET, 
                entity, 
                responseType
            );
            
            return response.getBody() != null ? response.getBody() : List.of();
        } catch (Exception e) {
            return List.of();
        }
    }
}

