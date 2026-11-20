<div align="center">

# 🛒 FastCart

**Plataforma Full-Stack de Comercio Electrónico y Gestión de Catálogo de Productos**

[![Angular](https://img.shields.io/badge/Angular-18+-DD0031?style=for-the-badge&logo=angular&logoColor=white)](https://angular.dev/)
[![Java](https://img.shields.io/badge/Java-21-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)](https://openjdk.org/)
[![Spring Boot](https://img.shields.io/badge/Spring_Boot-3+-6DB33F?style=for-the-badge&logo=spring-boot&logoColor=white)](https://spring.io/projects/spring-boot)
[![Tailwind CSS](https://img.shields.io/badge/Tailwind_CSS-38B2AC?style=for-the-badge&logo=tailwind-css&logoColor=white)](https://tailwindcss.com/)
[![PostgreSQL](https://img.shields.io/badge/PostgreSQL-16-316192?style=for-the-badge&logo=postgresql&logoColor=white)](https://www.postgresql.org/)
[![Docker](https://img.shields.io/badge/Docker-Compose-2496ED?style=for-the-badge&logo=docker&logoColor=white)](https://www.docker.com/)

</div>

---

## 📖 Descripción

**FastCart** es una solución de comercio electrónico full-stack desarrollada para ofrecer una experiencia rápida, reactiva y segura tanto para clientes como para administradores de catálogo de productos.

Combina una interfaz moderna construida con **Angular** y **Tailwind CSS** con una arquitectura de backend robusta en **Spring Boot**, aplicando principios de seguridad avanzada como rotación de **Refresh Tokens**, autenticación JWT sin estado y persistencia relacional en **PostgreSQL**.

---

## 🚀 Características Principales

- **Gestión Completa de Catálogo:** Creación, edición, consulta y eliminación de productos con asignación dinámica de categorías, precios y stock.
- **Autenticación Segura y Rotación de Tokens:**
  - Login con credenciales cifradas (BCrypt).
  - Emisión de `Access Token` JWT de corta duración y `Refresh Token` para renovación automática de sesión sin fricción.
  - Endpoint de `Logout` con invalidación de Refresh Token.
- **Frontend Reactivo con Angular:**
  - Componentes Standalone y gestión de rutas con protección mediante `AuthGuard`.
  - `JwtInterceptor` para inyección automática de encabezados `Authorization: Bearer <token>`.
  - Modales de confirmación desacoplados con Angular Material y estilos personalizados con Tailwind CSS.
- **Poblado Automático de Datos:** `DataSeeder` integrado para inicializar roles, usuarios de prueba y categorías al arrancar.
- **Control Centralizado de Excepciones:** Manejo uniforme de errores con `GlobalExceptionHandler` y respuestas de error legibles.

---

## 🏛️ Estructura del Proyecto

```text
FastCart/
├── client/                     # Frontend Angular
│   ├── src/
│   │   ├── app/
│   │   │   ├── components/     # Modales reutilizables (ConfirmDialog, etc.)
│   │   │   ├── guards/         # Auth Guards para protección de rutas
│   │   │   ├── interceptors/   # Interceptor HTTP para JWT
│   │   │   ├── models/         # Modelos e interfaces TypeScript
│   │   │   ├── pages/          # Vistas (Login, Gestión de Productos)
│   │   │   └── services/       # Servicios HTTP (Auth, Category, Product)
│   │   └── environments/       # Configuración de entornos
│   ├── package.json
│   └── angular.json
└── server/                     # Backend Spring Boot
    ├── src/main/java/com/danilodoria/server/
    │   ├── config/             # DataSeeder y configuraciones
    │   ├── controller/         # Controladores REST (Auth, Category, Product)
    │   ├── dto/                # Records / DTOs de petición y respuesta
    │   ├── entity/             # Entidades JPA (User, Role, Product, Category, RefreshToken)
    │   ├── exception/          # Manejador global y excepciones de negocio
    │   ├── mappers/            # Mapeo entre entidades y DTOs
    │   ├── repository/         # Interfaces Spring Data JPA
    │   ├── security/           # Filtros JWT, UserDetailsService y SecurityConfig
    │   └── service/            # Lógica de negocio
    ├── docker-compose.yml      # Base de datos PostgreSQL
    └── pom.xml
```

---

## 🛠️ Tecnologías Utilizadas

### Frontend
- **Angular** (Standalone Components)
- **TypeScript**
- **Tailwind CSS** & **Angular Material**
- **RxJS**

### Backend
- **Java 21**
- **Spring Boot** (Spring Web, Spring Security, Spring Data JPA)
- **JSON Web Tokens (JJWT)**
- **PostgreSQL 16**
- **Docker Compose**
- **Maven**

---

## ⚙️ Puesta en Marcha

### 1. Iniciar la Base de Datos (PostgreSQL)
```bash
cd server
docker compose up -d
```

### 2. Iniciar el Backend (Spring Boot)
```bash
cd server
./mvnw clean spring-boot:run
```
El servidor backend quedará disponible en `http://localhost:8080`.

### 3. Iniciar el Frontend (Angular)
En una nueva terminal:
```bash
cd client
npm install
npm start
```
La aplicación cliente estará accesible en `http://localhost:4200`.

---

## 👨💻 Author

- GitHub: **[Danilo-Doria](https://github.com/Danilo-Doria)**
- LinkedIn: **[Danilo Doria Diaz](https://www.linkedin.com/in/danilo-doria-diaz)**
- Mail: **danilodoria519@gmail.com**

---

## 📄 Licencia

Este proyecto está bajo la Licencia MIT. Consulta el archivo [LICENSE](LICENSE) para más detalles.
