package com.nettalco.backendservicios.core.config;

import com.nettalco.backendservicios.core.security.JwtAuthFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

/**
 * Configuracion de seguridad para el backend de servicios
 * Define que endpoints requieren autenticacion y configura CORS
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {
    
    @Autowired
    private JwtAuthFilter jwtAuthFilter;
    
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            // Configurar CORS
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            
            // Deshabilitar CSRF (no necesario para API REST con JWT)
            .csrf(csrf -> csrf.disable())
            
            // Politica de sesion: STATELESS (sin sesiones)
            .sessionManagement(session -> 
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            
            // Configurar autorizacion de requests
            .authorizeHttpRequests(auth -> auth
                // Permitir peticiones OPTIONS (preflight CORS) sin autenticacion
                .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                
                // Endpoints publicos (sin autenticacion)
                .requestMatchers("/api/public/**").permitAll()
                .requestMatchers("/api/health").permitAll()
                .requestMatchers("/api/clientes/health").permitAll()
                .requestMatchers("/api/versiones-terminos/actual").permitAll()
                .requestMatchers("/api/versiones-terminos/**").permitAll()
                .requestMatchers("/api/versiones-privacidad/actual").permitAll()
                .requestMatchers("/api/versiones-privacidad/**").permitAll()
                .requestMatchers("/actuator/health").permitAll()
                
                // Endpoints de transporte (publicos para acceso sin restricciones)
                .requestMatchers("/api/gps/**").permitAll()
                .requestMatchers("/api/rutas/**").permitAll()
                .requestMatchers("/api/trips/**").permitAll()
                
                // Todos los demas endpoints requieren autenticacion
                .anyRequest().authenticated()
            )
            
            // Agregar el filtro JWT antes del filtro de autenticacion estandar
            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);
        
        return http.build();
    }
    
    /**
     * Configuracion de CORS para permitir peticiones desde Flutter y web
     * Usa patterns para permitir todos los orígenes (incluyendo localhost para desarrollo)
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        
        // Permitir todos los orígenes usando patterns (compatible con allowCredentials false)
        // Esto permite tanto localhost como dominios de producción sin necesidad de actualizar la lista
        configuration.setAllowedOriginPatterns(Arrays.asList("*"));
        
        // Métodos HTTP permitidos
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH", "HEAD"));
        
        // Headers permitidos
        configuration.setAllowedHeaders(Arrays.asList(
            "Authorization",
            "Content-Type",
            "X-Requested-With",
            "Accept",
            "Origin",
            "Access-Control-Request-Method",
            "Access-Control-Request-Headers",
            "Cache-Control"
        ));
        
        // Headers expuestos al cliente
        configuration.setExposedHeaders(Arrays.asList(
            "Authorization",
            "Content-Type",
            "Access-Control-Allow-Origin",
            "Access-Control-Allow-Credentials"
        ));
        
        // No permitir credenciales cuando usas * en origins (pero el token se envía en header)
        configuration.setAllowCredentials(false);
        
        // Cache de preflight requests
        configuration.setMaxAge(3600L);
        
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        
        return source;
    }
}
