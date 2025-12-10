package com.nettalco.backendservicios.core.security;

import com.nettalco.backendservicios.core.util.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

/**
 * Filtro que intercepta todas las peticiones HTTP para validar el token JWT
 * Si el token es valido, establece la autenticacion en el contexto de seguridad
 */
@Component
public class JwtAuthFilter extends OncePerRequestFilter {
    
    @Autowired
    private JwtUtil jwtUtil;
    
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();
        return path.startsWith("/api/public/") 
            || path.startsWith("/api/versiones-terminos/actual")
            || path.startsWith("/api/versiones-terminos")
            || path.startsWith("/api/versiones-privacidad/actual")
            || path.startsWith("/api/versiones-privacidad")
            || path.startsWith("/api/gps/")
            || path.startsWith("/api/rutas/")
            || path.startsWith("/api/trips/")
            || path.startsWith("/api/health")
            || path.startsWith("/actuator/health");
    }
    
    @Override
    protected void doFilterInternal(HttpServletRequest request, 
                                  HttpServletResponse response, 
                                  FilterChain chain)
            throws ServletException, IOException {
        
        // Permitir peticiones OPTIONS (preflight) sin autenticación
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            chain.doFilter(request, response);
            return;
        }
        
        String authHeader = request.getHeader("Authorization");
        
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            
            try {
                if (jwtUtil.validateToken(token)) {
                    String username = jwtUtil.extractUsername(token);
                    Integer idUsuario = jwtUtil.extractIdUsuario(token);
                    Integer idRol = jwtUtil.extractIdRol(token);
                    String nombreRol = jwtUtil.extractNombreRol(token);
                    
                    UsernamePasswordAuthenticationToken authToken = 
                        new UsernamePasswordAuthenticationToken(
                            username,
                            null,
                            Collections.singletonList(
                                new SimpleGrantedAuthority("ROLE_" + nombreRol)
                            )
                        );
                    
                    authToken.setDetails(new UserDetails(idUsuario, username, idRol, nombreRol));
                    
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                } else {
                    logger.warn("Token JWT inválido para la petición: " + request.getRequestURI());
                }
            } catch (Exception e) {
                logger.error("Error validando token JWT: " + e.getMessage(), e);
                // Continuar sin autenticación, Spring Security manejará el 403
            }
        } else {
            logger.debug("No se encontró header Authorization en la petición: " + request.getRequestURI());
        }
        
        chain.doFilter(request, response);
    }
}
