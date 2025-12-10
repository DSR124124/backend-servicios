package com.nettalco.backendservicios.core.clients;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

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
            // Si no se puede obtener el usuario, retornar null
            return null;
        }
    }
    
    /**
     * Obtiene todos los usuarios desde el backend de gestión
     */
    public List<Map<String, Object>> obtenerTodosUsuarios(String token) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer " + token);
            HttpEntity<String> entity = new HttpEntity<>(headers);
            
            String url = gestionBaseUrl + "/api/usuarios";
            @SuppressWarnings("unchecked")
            ResponseEntity<List<Map<String, Object>>> response = restTemplate.exchange(
                url, 
                HttpMethod.GET, 
                entity, 
                (Class<List<Map<String, Object>>>) (Class<?>) List.class
            );
            
            return response.getBody();
        } catch (Exception e) {
            // Si no se puede obtener los usuarios, retornar lista vacía
            return List.of();
        }
    }
}

