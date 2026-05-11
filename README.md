# Sanos y Salvos - Backend

Backend del proyecto **Sanos y Salvos**, desarrollado para la asignatura Fullstack III.

La aplicación implementa una arquitectura basada en microservicios utilizando Spring Boot, JWT y un BFF (Backend for Frontend) para centralizar la comunicación entre frontend y backend.

---

# Integrantes

- Matilda Vargas
- Juan Vargas

---

# Repositorios

## Backend
https://github.com/juavargasc-del/Sanos-y-salvos-Backend-Vargas-Vargas-.git

## Frontend
https://github.com/juavargasc-del/Sanos-y-salvos-Frontend-Vargas-Vargas.git

---

# Descripción del proyecto

Sanos y Salvos es una plataforma orientada al reporte y gestión de mascotas perdidas y encontradas.

En esta primera versión se desarrolló:

- Sistema de autenticación con JWT
- Registro y login de usuarios
- CRUD de mascotas
- Microservicio de coincidencias
- Comunicación entre microservicios
- BFF/API Gateway
- Seguridad mediante Spring Security
- Persistencia en MySQL

---

# Arquitectura general

```txt
Frontend (React)
        ↓
BFF / API Gateway
        ↓
Microservicios:
- ms-usuarios
- ms-mascotas
- ms-coincidencias
        ↓
MySQL
```

---

# Tecnologías utilizadas

## Backend
- Java 17
- Spring Boot
- Spring Security
- JWT
- Maven
- MySQL
- JPA / Hibernate
- RestTemplate
- Lombok

## Frontend
- React
- Vite
- Axios

---

# Microservicios implementados

## ms-usuarios

Puerto:
```txt
8081
```

Responsabilidades:
- Registro de usuarios
- Login
- JWT Authentication
- Seguridad de endpoints
- Encriptación BCrypt

Endpoints principales:

| Método | Endpoint |
|---|---|
| POST | /api/usuarios |
| POST | /api/usuarios/login |
| GET | /api/usuarios |
| GET | /api/usuarios/{id} |

Base de datos:
```txt
jdbc:mysql://localhost:3306/sanosysalvos_usuarios
```

---

## ms-mascotas

Puerto:
```txt
8082
```

Responsabilidades:
- CRUD de mascotas
- Gestión de mascotas perdidas y encontradas

Endpoints principales:

| Método | Endpoint |
|---|---|
| POST | /api/mascotas |
| GET | /api/mascotas |
| GET | /api/mascotas/{id} |
| GET | /api/mascotas/estado/{estado} |
| PUT | /api/mascotas/{id} |
| DELETE | /api/mascotas/{id} |

Base de datos:
```txt
jdbc:mysql://localhost:3306/sanosysalvos_mascotas
```

---

## ms-coincidencias

Puerto:
```txt
8083
```

Responsabilidades:
- Detectar coincidencias entre mascotas perdidas y encontradas
- Comunicación con ms-mascotas mediante RestTemplate

Lógica utilizada:
- Comparación por:
  - tipo
  - raza
  - color

Endpoint principal:

| Método | Endpoint |
|---|---|
| GET | /api/coincidencias |

---

# Backend For Frontend (BFF)

Puerto:
```txt
8080
```

El BFF funciona como API Gateway centralizando las solicitudes provenientes del frontend.

Responsabilidades:
- Comunicación con microservicios
- Proxy de endpoints
- Centralización de acceso backend
- Simplificación de consumo frontend

Endpoints principales:

## Usuarios
- /bff/usuarios
- /bff/usuarios/login

## Mascotas
- /bff/mascotas

## Coincidencias
- /bff/coincidencias

---

# Seguridad implementada

- JWT Authentication
- Spring Security
- BCrypt
- Protected Endpoints
- Validaciones backend

---

# Patrones utilizados

- DTO Pattern
- Repository Pattern
- Service Layer Pattern
- MVC
- Microservices Architecture
- BFF / API Gateway Pattern

---

# Estructura del backend

```txt
backend/
├── ms-usuarios
├── ms-mascotas
├── ms-coincidencias
└── bff-sanosysalvos
```

---

# Configuración y ejecución

## Requisitos

- Java 17
- Maven
- MySQL
- XAMPP

---

# Ejecución de microservicios

Cada microservicio puede ejecutarse desde su clase principal utilizando Spring Boot.

Orden recomendado:

1. ms-usuarios
2. ms-mascotas
3. ms-coincidencias
4. bff-sanosysalvos

---

# Puertos utilizados

| Servicio | Puerto |
|---|---|
| BFF | 8080 |
| ms-usuarios | 8081 |
| ms-mascotas | 8082 |
| ms-coincidencias | 8083 |

---

# GitHub Flow

Durante el desarrollo se utilizó GitHub Flow mediante:

- feature/
- chore/
- fix/

Cada funcionalidad fue desarrollada en ramas independientes utilizando Pull Request y merge hacia main.

---

# Estado actual

## Implementado
- JWT Authentication
- CRUD mascotas
- Login y registro
- Seguridad backend
- BFF funcional
- Comunicación entre microservicios
- Coincidencias automáticas
- MySQL integrado

## Pendiente
- Integración frontend completa con mascotas
- Mejoras visuales futuras
- Nuevas funcionalidades del sistema

---

# Asignatura

Desarrollo Fullstack III  
Duoc UC
