# Sanos y Salvos - Backend

## Descripción del proyecto

**Sanos y Salvos** es un backend académico para registrar mascotas perdidas y encontradas, administrar usuarios y calcular coincidencias entre animales usando atributos visuales y geolocalización.

El sistema está dividido en microservicios Spring Boot independientes, con un punto único de entrada a nivel de despliegue mediante API Gateway. El objetivo es centralizar el acceso, mantener el aislamiento por dominio y permitir que cada componente evolucione de forma separada.

## Arquitectura

La arquitectura implementada se basa en microservicios con una capa de entrada centralizada.

### Componentes principales

- **API Gateway como punto único de entrada**: `gateway-sanosysalvos` expone las rutas públicas del backend en el puerto `8080` y reescribe las solicitudes hacia los microservicios internos.
- **Microservicios independientes**: cada dominio funcional está separado en su propio módulo Spring Boot.
- **Comunicación mediante OpenFeign**: `ms-coincidencias` consume los microservicios de Mascotas y Geolocalización mediante clientes Feign para calcular coincidencias entre mascotas perdidas y encontradas.
- **Bases de datos independientes por dominio**: `ms-usuarios`, `ms-mascotas` y `ms-geolocalizacion` usan MySQL con esquemas separados; `ms-coincidencias` no persiste datos propios.
- **Docker y Docker Compose**: el entorno local se define con contenedores para los microservicios, Gateway, MySQL y SonarQube.
- **JWT para autenticación**: los servicios protegidos generan y validan tokens JWT.
- **Spring Security**: protege los endpoints internos mediante filtros JWT y sesiones stateless.
- **Resilience4J Circuit Breaker**: implementado en `ms-coincidencias` para proteger las llamadas hacia `ms-geolocalizacion`.
- **Swagger/OpenAPI**: cada microservicio MVC expone documentación con springdoc-openapi.
- **Separación por capas**: controlador, servicio, repositorio, DTO, modelo y configuración.

### Patrones de diseño observables

- **API Gateway**: unifica la entrada al backend.
- **Service Layer**: la lógica de negocio se concentra en servicios.
- **Repository**: el acceso a datos se abstrae mediante repositorios JPA.
- **DTO**: los datos se intercambian entre capas usando objetos de transferencia.
- **Feign Client**: comunicación declarativa entre servicios.
- **Circuit Breaker**: tolerancia a fallos en integración remota.
- **Security stateless con JWT**: autenticación sin sesión de servidor.

## Diagrama textual de arquitectura

```txt
                Frontend React
                      │
                      ▼
          API Gateway (Puerto 8080)
                      │
      ┌───────────────┼────────────────┐
      ▼               ▼                ▼
ms-usuarios      ms-mascotas    ms-coincidencias
   (8081)           (8082)            (8083)
                                          │
                         ┌────────────────┴─────────────┐
                         ▼                              ▼
                  ms-mascotas (Feign)      ms-geolocalizacion (Feign)
                                               (8084)
```
El cliente consume el Gateway y este redirige a los microservicios según la ruta solicitada.

## Microservicios

### ms-usuarios

- **Responsabilidad**: registro, autenticación y administración de usuarios.
- **Puerto**: `8081`.
- **Endpoints principales**:
  - `POST /api/usuarios`
  - `POST /api/usuarios/login`
  - `GET /api/usuarios`
  - `GET /api/usuarios/{id}`
  - `DELETE /api/usuarios/{id}`
  - `POST /api/auth/login`
- **Tecnologías utilizadas**: Spring Boot, Spring Web, Spring Security, Spring Data JPA, Hibernate, JWT, MySQL, springdoc-openapi, Lombok.

### ms-mascotas

- **Responsabilidad**: administración de mascotas perdidas y encontradas.
- **Puerto**: `8082`.
- **Endpoints principales**:
  - `POST /api/mascotas`
  - `GET /api/mascotas`
  - `GET /api/mascotas/{id}`
  - `GET /api/mascotas/estado/{estado}`
  - `GET /api/mascotas/usuario/{usuarioId}`
  - `PUT /api/mascotas/{id}`
  - `DELETE /api/mascotas/{id}`
- **Tecnologías utilizadas**: Spring Boot, Spring Web, Spring Security, Spring Data JPA, Hibernate, MySQL, springdoc-openapi, Lombok.

### ms-coincidencias

- **Responsabilidad**: calcular coincidencias entre mascotas perdidas y encontradas.
- **Puerto**: `8083`.
- **Endpoints principales**:
  - `GET /api/coincidencias`
  - `GET /api/coincidencias/usuario/{usuarioId}`
- **Tecnologías utilizadas**: Spring Boot, Spring Web, Spring Security, OpenFeign, Resilience4J, JWT, springdoc-openapi, Lombok.

### ms-geolocalizacion

- **Responsabilidad**: registrar ubicaciones, calcular distancias y consultar cercanías.
- **Puerto**: `8084`.
- **Endpoints principales**:
  - `POST /api/geolocalizacion`
  - `GET /api/geolocalizacion`
  - `GET /api/geolocalizacion/distancia`
  - `GET /api/geolocalizacion/cercanas`
  - `GET /api/geolocalizacion/mascota/{mascotaId}`
- **Tecnologías utilizadas**: Spring Boot, Spring Web, Spring Security, Spring Data JPA, Hibernate, MySQL, springdoc-openapi, Lombok.

### gateway-sanosysalvos

- **Responsabilidad**: centralizar el acceso al backend y enrutar solicitudes hacia los microservicios.
- **Puerto**: `8080`.
- **Endpoints principales**:
  - `/bff/usuarios/**` -> `ms-usuarios`
  - `/bff/mascotas/**` -> `ms-mascotas`
  - `/bff/coincidencias/**` -> `ms-coincidencias`
  - `/bff/geolocalizacion/**` -> `ms-geolocalizacion`
- **Tecnologías utilizadas**: Spring Boot, Spring Cloud Gateway, CORS reactivo, Maven, Lombok.

## Tecnologías

| Tecnología | Uso real en el proyecto |
|---|---|
| Java 17 | Lenguaje principal |
| Spring Boot 3.5.14 | Base de todos los módulos Spring Boot |
| Spring Web / Spring MVC | Exposición REST |
| Spring Security | Autenticación y protección de endpoints |
| Spring Cloud Gateway | Punto de entrada del backend |
| Spring Cloud OpenFeign | Comunicación entre servicios |
| Spring Data JPA | Persistencia en los microservicios con base de datos |
| Hibernate | ORM |
| MySQL 8 | Base de datos de usuarios, mascotas y geolocalización |
| PostgreSQL 15 | Base de datos de SonarQube |
| JWT (jjwt) | Generación y validación de tokens |
| Resilience4J | Circuit Breaker en `ms-coincidencias` |
| springdoc-openapi-starter-webmvc-ui | Swagger/OpenAPI |
| Jakarta Validation | Validación de requests |
| Lombok | Reducción de código repetitivo |
| Maven | Construcción y empaquetado |
| Docker | Contenerización |
| Docker Compose | Orquestación local |
| JaCoCo | Cobertura de pruebas |
| SonarQube | Calidad estática |
| JUnit 5 / Spring Boot Test | Pruebas automatizadas |

## Patrones de diseño

- **Repository**: acceso a datos encapsulado en repositorios.
- **Service Layer**: lógica de negocio centralizada en servicios.
- **DTO**: intercambio de información entre capas y servicios.
- **Feign Client**: integración declarativa entre microservicios.
- **API Gateway**: unificación del acceso externo.
- **Circuit Breaker**: protección de integraciones remotas.

## Cambios realizados para la evaluación

Esta entrega deja implementadas, de forma real en el repositorio, las siguientes mejoras:

- Reemplazo operativo del acceso directo por **API Gateway**.
- **Dockerización** de los servicios con `Dockerfile` por módulo.
- **Docker Compose** para levantar el entorno completo.
- **Swagger/OpenAPI** en los microservicios MVC.
- **Circuit Breaker** con Resilience4J en `ms-coincidencias`.
- **Administración de usuarios**.
- **Administración de mascotas**.
- **Seguridad JWT** con Spring Security.
- **Mejoras de arquitectura** mediante separación por microservicios y capas.
- **Calidad y pruebas** con JaCoCo y SonarQube.

## Docker

El proyecto se ejecuta desde la raíz del repositorio.

### Construir e iniciar

```bash
docker compose up -d --build
```

### Ver contenedores

```bash
docker compose ps
```

### Ver logs

```bash
docker compose logs -f gateway-sanosysalvos
```

### Detener el entorno

```bash
docker compose down
```

## Variables de entorno

Variables realmente utilizadas en el proyecto:

| Variable | Valor por defecto | Uso |
|---|---|---|
| `SPRING_DATASOURCE_URL` | `jdbc:mysql://localhost:3306/sanosysalvos_usuarios?createDatabaseIfNotExist=true` / `sanosysalvos_mascotas` / `sanosysalvos_geolocalizacion` | Conexión MySQL en `ms-usuarios`, `ms-mascotas` y `ms-geolocalizacion` |
| `SPRING_DATASOURCE_USERNAME` | `root` | Usuario MySQL de los microservicios con persistencia |
| `SPRING_DATASOURCE_PASSWORD` | vacío | Contraseña MySQL de los microservicios con persistencia |
| `MS_USUARIOS_URL` | `http://localhost:8081` | API Gateway |
| `MS_MASCOTAS_URL` | `http://localhost:8082` | API Gateway y `ms-coincidencias` |
| `MS_COINCIDENCIAS_URL` | `http://localhost:8083` | API Gateway |
| `MS_GEOLOCALIZACION_URL` | `http://localhost:8084` | API Gateway y `ms-coincidencias` |
| `MYSQL_ROOT_PASSWORD` | `rootpassword` | MySQL en Docker Compose |
| `MYSQL_DATABASE` | `sanosysalvos_usuarios` | MySQL en Docker Compose |
| `POSTGRES_USER` | `sonar` | SonarQube |
| `POSTGRES_PASSWORD` | `sonar` | SonarQube |
| `POSTGRES_DB` | `sonarqube` | SonarQube |
| `SONAR_JDBC_URL` | `jdbc:postgresql://sonarqube-db:5432/sonarqube` | SonarQube |
| `SONAR_JDBC_USERNAME` | `sonar` | SonarQube |
| `SONAR_JDBC_PASSWORD` | `sonar` | SonarQube |

## Swagger

Swagger/OpenAPI está disponible en los microservicios MVC mediante springdoc-openapi.

| Microservicio | Puerto | Swagger UI | OpenAPI JSON |
|---|---|---|---|
| `ms-usuarios` | `8081` | `http://localhost:8081/swagger-ui/index.html` | `http://localhost:8081/v3/api-docs` |
| `ms-mascotas` | `8082` | `http://localhost:8082/swagger-ui/index.html` | `http://localhost:8082/v3/api-docs` |
| `ms-coincidencias` | `8083` | `http://localhost:8083/swagger-ui/index.html` | `http://localhost:8083/v3/api-docs` |
| `ms-geolocalizacion` | `8084` | `http://localhost:8084/swagger-ui/index.html` | `http://localhost:8084/v3/api-docs` |

## Ejecución

La forma recomendada de ejecutar el backend completo es con Docker Compose:

```bash
docker compose up -d --build
```

Servicios levantados por el archivo actual:

- `sonarqube-db`
- `sonarqube`
- `mysql`
- `ms-usuarios`
- `ms-mascotas`
- `ms-coincidencias`
- `ms-geolocalizacion`
- `gateway-sanosysalvos`

## Calidad del software

### JaCoCo

Los módulos Spring Boot incluyen el plugin de JaCoCo. El reporte se genera en:

```txt
target/site/jacoco/jacoco.xml
```

### SonarQube

El entorno local incluye:

- `http://localhost:9000` para SonarQube
- PostgreSQL en `localhost:5432` para la base de datos de SonarQube

### Pruebas

El repositorio incluye pruebas automatizadas con `spring-boot-starter-test` y JUnit 5 en los módulos principales.

## Estructura del proyecto

```txt
sanos-y-salvos-backend-vargas-vargas/
├── README.md
├── docker-compose.yml
├── gateway-sanosysalvos/
│   ├── Dockerfile
│   ├── pom.xml
│   └── src/
│       ├── main/
│       │   ├── java/com/sanosysalvos/gateway/
│       │   │   ├── GatewaySanosysalvosApplication.java
│       │   │   └── config/CorsConfig.java
│       │   └── resources/application.properties
├── ms-usuarios/
│   ├── Dockerfile
│   ├── pom.xml
│   └── src/
│       ├── main/
│       │   ├── java/com/sanosysalvos/usuarios/
│       │   │   ├── MsUsuariosApplication.java
│       │   │   ├── config/
│       │   │   ├── controller/
│       │   │   ├── dto/
│       │   │   ├── model/
│       │   │   ├── repository/
│       │   │   └── service/
│       │   └── resources/application.properties
│       └── test/java/com/sanosysalvos/usuarios/
├── ms-mascotas/
│   ├── Dockerfile
│   ├── pom.xml
│   └── src/
│       ├── main/
│       │   ├── java/com/sanosysalvos/mascotas/
│       │   │   ├── MsMascotasApplication.java
│       │   │   ├── config/
│       │   │   ├── controller/
│       │   │   ├── dto/
│       │   │   ├── exception/
│       │   │   ├── model/
│       │   │   ├── repository/
│       │   │   └── service/
│       │   └── resources/application.properties
│       └── test/java/com/sanosysalvos/mascotas/
├── ms-coincidencias/
│   ├── Dockerfile
│   ├── pom.xml
│   └── src/
│       ├── main/
│       │   ├── java/com/sanosysalvos/coincidencias/
│       │   │   ├── MsCoincidenciasApplication.java
│       │   │   ├── client/
│       │   │   ├── config/
│       │   │   ├── controller/
│       │   │   ├── dto/
│       │   │   ├── model/
│       │   │   ├── repository/
│       │   │   └── service/
│       │   └── resources/application.properties
│       └── test/java/com/sanosysalvos/coincidencias/
└── ms-geolocalizacion/
    ├── Dockerfile
    ├── pom.xml
    └── src/
        ├── main/
        │   ├── java/com/sanosysalvos/geolocalizacion/
        │   │   ├── MsGeolocalizacionApplication.java
        │   │   ├── config/
        │   │   ├── controller/
        │   │   ├── dto/
        │   │   ├── model/
        │   │   ├── repository/
        │   │   ├── service/
        │   │   └── util/
        │   └── resources/application.properties
        └── test/java/com/sanosysalvos/geolocalizacion/
```

## Autores

Juan Fernando Vargas Castillo
Matilda Isabel Vargas Canseco