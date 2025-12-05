# Backend de Servicios - Nettalco

Backend de servicios modularizado con validacion JWT para la arquitectura dual backend.

## Descripcion

Este backend es parte de una arquitectura dual donde:
- **Backend de Gestion**: Maneja autenticacion, roles y permisos (puerto 8080)
- **Backend de Servicios**: Maneja la logica de negocio, transporte y politicas (puerto 8081)

## Estructura del Proyecto

```
backend-servicios/
├── src/main/java/com/nettalco/backendservicios/
│   ├── BackendServiciosApplication.java       # Clase principal
│   │
│   ├── core/                                  # Nucleo compartido
│   │   ├── config/
│   │   │   ├── AsyncConfig.java               # Configuracion asincrona
│   │   │   └── SecurityConfig.java            # Configuracion de seguridad
│   │   ├── security/
│   │   │   ├── JwtAuthFilter.java             # Filtro JWT
│   │   │   └── UserDetails.java               # Detalles del usuario
│   │   └── util/
│   │       └── JwtUtil.java                   # Utilidades JWT
│   │
│   └── modules/
│       ├── politicas/                         # Modulo de Politicas
│       │   ├── controllers/
│       │   │   ├── VersionPrivacidadController.java
│       │   │   └── VersionTerminosController.java
│       │   ├── dtos/
│       │   ├── entities/
│       │   ├── repositories/
│       │   └── services/
│       │       ├── impl/
│       │       └── interfaces
│       │
│       └── transporte/                        # Modulo de Transporte
│           ├── controllers/
│           │   ├── ClienteController.java
│           │   ├── ConductorController.java
│           │   ├── GPSController.java
│           │   ├── HealthController.java
│           │   ├── RutaController.java
│           │   └── ViajeController.java
│           ├── dtos/
│           ├── entities/
│           ├── repositories/
│           └── services/
│               ├── impl/
│               └── interfaces
│
├── src/main/resources/
│   └── application.properties
├── Dockerfile
└── pom.xml
```

## Configuracion

### Requisitos Previos
- Java 21+
- Maven 3.9+
- PostgreSQL 14+
- Backend de Gestion en ejecucion

### Base de Datos

Configurar en `application.properties`:
```properties
spring.datasource.url=jdbc:postgresql://HOST:PORT/DATABASE
spring.datasource.username=usuario
spring.datasource.password=password
```

### JWT Secret Key

**IMPORTANTE**: La `jwt.secret` debe ser **EXACTAMENTE LA MISMA** que en el Backend de Gestion:

```properties
jwt.secret=TU_SECRET_KEY_COMPARTIDA
```

### Puerto

El backend de servicios corre en el puerto **8081**:
```properties
server.port=8081
server.servlet.context-path=/servicios
```

## Ejecutar el Proyecto

### Opcion 1: Maven
```bash
mvn clean install
mvn spring-boot:run
```

### Opcion 2: IDE
Ejecutar la clase `BackendServiciosApplication.java`

El servidor estara disponible en: `http://localhost:8081/servicios`

## Endpoints Principales

### Modulo Transporte

| Endpoint | Metodo | Descripcion | Auth |
|----------|--------|-------------|------|
| `/api/health` | GET | Health check | No |
| `/api/rutas` | GET | Listar rutas | No |
| `/api/rutas/{id}` | GET | Obtener ruta | No |
| `/api/rutas/{id}/completo` | GET | Ruta con viajes | No |
| `/api/gps/ubicacion` | POST | Registrar GPS | No |
| `/api/trips/{id}/location` | GET | Ubicacion bus | No |
| `/api/conductor/mis-viajes` | GET | Viajes conductor | Si |
| `/api/conductor/viaje-activo` | GET | Viaje en curso | Si |
| `/api/conductor/viaje/{id}/iniciar` | POST | Iniciar viaje | Si |
| `/api/conductor/viaje/{id}/finalizar` | POST | Finalizar viaje | Si |
| `/api/conductor/estadisticas` | GET | Stats conductor | Si |

### Modulo Politicas

| Endpoint | Metodo | Descripcion | Auth |
|----------|--------|-------------|------|
| `/api/versiones-privacidad/actual` | GET | Privacidad actual | No |
| `/api/versiones-terminos/actual` | GET | Terminos actuales | No |

## Docker

### Construir imagen
```bash
mvn clean package -DskipTests
docker build -t backend-servicios .
```

### Ejecutar contenedor
```bash
docker run -p 8081:8081 \
  -e SPRING_DATASOURCE_URL=jdbc:postgresql://host:5434/postgres \
  -e SPRING_DATASOURCE_USERNAME=postgres \
  -e SPRING_DATASOURCE_PASSWORD=password \
  -e JWT_SECRET=tu_secret_key \
  backend-servicios
```

### Health Check
```bash
curl http://localhost:8081/servicios/api/health
```

## Flujo de Autenticacion

```
1. Flutter App → Login → Backend Gestion (puerto 8080)
                          ↓
                      JWT Token
                          ↓
2. Flutter App → Servicios → Backend Servicios (puerto 8081)
   (con Token en header)         ↓
                          Valida Token
                          (misma secret key)
                          ↓
                     Procesa Request
                          ↓
                     Retorna Datos
```

## Seguridad

### Endpoints Publicos (Sin Token)
- `GET /api/health`
- `GET /api/rutas/**`
- `GET /api/trips/**`
- `POST /api/gps/**`
- `GET /api/versiones-privacidad/**`
- `GET /api/versiones-terminos/**`

### Endpoints Protegidos (Requieren Token)
- `GET /api/conductor/**` - Conductores autenticados
- `GET /api/clientes/mi-perfil` - Usuarios autenticados

## Errores Comunes

### 401 Unauthorized
- Token no proporcionado
- Token expirado
- Token invalido
- Secret key diferente entre backends

### 403 Forbidden
- Usuario autenticado pero sin permisos
- Rol insuficiente para el endpoint

### Solucion:
1. Verificar que `jwt.secret` sea la misma en ambos backends
2. Verificar que el token no haya expirado
3. Verificar header `Authorization: Bearer TOKEN`

---

**Listo para usar!**
