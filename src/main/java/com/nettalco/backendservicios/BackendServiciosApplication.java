package com.nettalco.backendservicios;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.core.task.AsyncTaskExecutor;
import org.springframework.core.task.support.TaskExecutorAdapter;
import org.springframework.web.client.RestTemplate;

import java.util.concurrent.Executors;

/**
 * Aplicación principal del microservicio de transporte.
 * Configurada para usar Virtual Threads (Project Loom) en Java 21+.
 * 
 * La configuración de Virtual Threads para Tomcat se realiza a través de
 * application.properties (spring.threads.virtual.enabled=true)
 */
@SpringBootApplication
public class BackendServiciosApplication {

    public static void main(String[] args) {
        SpringApplication.run(BackendServiciosApplication.class, args);
    }
    
    /**
     * Configura un AsyncTaskExecutor usando Virtual Threads para operaciones asíncronas.
     * Esto mejora el rendimiento para operaciones I/O bound como la ingesta de GPS.
     */
    @Bean
    public AsyncTaskExecutor applicationTaskExecutor() {
        return new TaskExecutorAdapter(Executors.newVirtualThreadPerTaskExecutor());
    }
    
    /**
     * Bean de RestTemplate para consumir servicios externos (backend-gestion)
     * Configurado para manejar HTTPS y timeouts
     */
    @Bean
    public RestTemplate restTemplate() {
        org.springframework.http.client.SimpleClientHttpRequestFactory factory = 
            new org.springframework.http.client.SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(10000); // 10 segundos
        factory.setReadTimeout(30000); // 30 segundos
        
        RestTemplate restTemplate = new RestTemplate(factory);
        return restTemplate;
    }
}
