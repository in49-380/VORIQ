# VORIQ — Car Catalog Service

A Spring Boot (Java 17) service that exposes a protected REST API for step-by-step car selection (brand → model → year → engine → transmission → wheel drive) and a final *resolve* of a concrete `carId`.  
Authentication is performed via temporary tokens (Bearer UUID) validated against Redis. The `dev` profile supports auto-bootstrapping of a PostgreSQL database from a SQL dump.

---

## Table of Contents

- [Features](#features)
- [Architecture & Stack](#architecture--stack)
- [Requirements](#requirements)
- [Configuration](#configuration)
  - [General properties](#general-properties)
  - [Profile `dev` (auto-create DB + import dump)](#profile-dev-auto-create-db--import-dump)
  - [Profiles `!dev` (prod/test)](#profiles-dev-prodtest)
- [Running](#running)
- [Authentication](#authentication)
- [CORS](#cors)
- [REST API](#rest-api)
  - [Lookup endpoints (GET)](#lookup-endpoints-get)
  - [Resolve endpoint (POST)](#resolve-endpoint-post)
  - [Test endpoint](#test-endpoint)
- [Error Model](#error-model)
- [OpenAPI / Swagger](#openapi--swagger)
- [Testing](#testing)
- [Key Classes](#key-classes)
- [License](#license)

---

## Features

- Protected endpoints for:
  - brands, models, years, engines, transmissions, wheel drives;
  - final `POST /v1/catalog/cars/resolve` returning a `carId`.
- Authentication: Bearer UUID (temporary token) via `TmpTokenAuthFilter` and Redis.
- Unified error handling and validation (`ErrorResponse` DTO).
- `dev` profile: automatic PostgreSQL database creation and import from `db/dump-voriq_cars.sql` (DDL → COPY data → FKs).
- Swagger/OpenAPI documentation for quick exploration.

## Architecture & Stack

- **Platform:** Java 17, Spring Boot
- **Data access:** Spring JDBC (`JdbcTemplate`)
- **Database:** PostgreSQL
- **Token store:** Redis (`StringRedisTemplate`)
- **Security:** Spring Security + custom `TmpTokenAuthFilter`
- **API Docs:** springdoc-openapi (Swagger UI)
- **Validation:** Jakarta Validation

Main packages/classes:
- `controller.api.CarCatalogLookupApi` — REST interface with OpenAPI annotations
- `controller.CarCatalogLookupController` — REST implementation
- `service.CarCatalogServiceImpl` — business logic
- `repository.CarCatalogJdbcRepository` — SQL via `JdbcTemplate`
- `filter.TmpTokenAuthFilter` — Redis-based token validation
- `config.SecurityConfig` — authorization rules & CORS
- `config/db_config.*` — data sources & DB bootstrap
- `exception_handler.*` — unified error model & handlers

## Requirements

- **Java:** 17+
- **Maven**
- **PostgreSQL:** 13+ (recommended 14+)
- **Redis:** 6+
- Reachability to Redis and PostgreSQL according to your configuration.

## Configuration

### General properties

Settings are read from `application.yml` / `application-*.yml`:

```yaml
server:
  # If you use a context-path, ensure Swagger paths in SecurityConfig remain permitted
  servlet:
    context-path: /api

cors:
  allowed-origins: "http://localhost:3000,http://localhost:5173"

spring:
  data:
    redis:
      host: localhost
      port: 6379
      # password: your_password_if_any

# Prefix for Redis keys that store temporary tokens
tmp-token:
  prefix: "tmp:token:"

spring:
  datasource:
    voriq:
      url: jdbc:postgresql://localhost:5432/voriq_cars
      username: app_user
      password: app_password
      driver-class-name: org.postgresql.Driver
```

> **Note:** `SecurityConfig` permits `/swagger-ui/**`, `/v3/api-docs/**`, and `/swagger-ui.html`.  
> With `server.servlet.context-path=/api`, Swagger UI is available at `/api/swagger-ui/index.html`.

### Profile `dev` (auto-create DB + import dump)

In the `dev` profile, the app connects to an admin database in PostgreSQL to create the app DB and import the dump:

```yaml
spring:
  profiles:
    active: dev

app:
  pg:
    host: localhost
    port: 5432
    admin-db: postgres   # admin DB to connect to
    user: postgres       # user with CREATE DATABASE privilege
    password: postgres
    app-db: voriq_cars   # name of the application DB to create

spring:
  datasource:
    voriq:
      url: jdbc:postgresql://localhost:5432/voriq_cars
      username: app_user
      password: app_password
```

Files used by the bootstrap:
- `db/dump-voriq_cars.sql` — **required** for the first import (`DatabaseBootstrap`).  
- Optionally, if present, `VoriqPostInitializer` will execute additional scripts found under `db/` (e.g., `dev-fill-voriq-catalogs.sql`, `seed_cars_refs_min.sql`, `add_all_keys.sql`, `seed_cars_exact_500.sql`).

### Profiles `!dev` (prod/test)

In non-`dev` profiles there is **no** automatic database creation. The app expects the database to exist and be populated. `spring.datasource.voriq.*` must point to the ready database.

## Running

### Maven

```bash
# build
mvn clean package

# run with dev profile (auto DB bootstrap from dump)
mvn spring-boot:run -Dspring-boot.run.profiles=dev

# or use the fat JAR
java -jar target/car-catalog-service-*.jar --spring.profiles.active=dev
```

Make sure Redis and PostgreSQL are reachable before starting the service.

## Authentication

All catalog endpoints require **authorization** (see below).  
The app uses **Bearer UUID** validated in Redis:

- **Key:** `<tmp-token.prefix><UUID>` (default `tmp:token:`)
- **Value:** arbitrary principal (e.g., username); TTL is up to you

Create a token (example):

```bash
# generate a UUID and use it below
redis-cli SETEX tmp:token:3f1c2a0d-1c2b-4c0d-8a11-9c0f1b2a3d4e 3600 demoUser
```

Use it in requests:

```
Authorization: Bearer 3f1c2a0d-1c2b-4c0d-8a11-9c0f1b2a3d4e
```

With a valid token, `TmpTokenAuthFilter` sets an authenticated principal into the security context.

## CORS

CORS is driven by the `cors.allowed-origins` property (comma-separated list).  
**Allowed methods by default:** `GET`. If you need cross-origin `POST`, extend the list in `SecurityConfig#corsConfigurationSource()`:

```java
config.setAllowedMethods(List.of("GET", "POST"));
```

## REST API

Base mapping: `@RequestMapping("/v1/catalog")`.

> Endpoints below are shown **without** the `server.servlet.context-path`. If it is set to `/api`, the final URLs will start with `/api/...`.

### Lookup endpoints (GET)

| Endpoint                                                                                           | Description                         | Auth |
|----------------------------------------------------------------------------------------------------|-------------------------------------|------|
| `GET /v1/catalog/brands`                                                                           | List brands                         | ✅   |
| `GET /v1/catalog/brands/{brandId}/models`                                                          | List models by brand                | ✅   |
| `GET /v1/catalog/brands/{brandId}/models/{modelId}/years`                                          | List available production years     | ✅   |
| `GET /v1/catalog/brands/{brandId}/models/{modelId}/years/{yearId}/engines`                         | List engines                        | ✅   |
| `GET /v1/catalog/brands/{brandId}/models/{modelId}/years/{yearId}/engines/{engineId}/transmissions`| List transmissions                   | ✅   |
| `GET /v1/catalog/brands/{brandId}/models/{modelId}/years/{yearId}/engines/{engineId}/transmissions/{transmissionsId}/wheel_drive` | List wheel drives | ✅   |

**Sample response (lookup):**

```json
[
  { "id": 3, "value": "BMW" },
  { "id": 5, "value": "Volkswagen" }
]
```

### Resolve endpoint (POST)

`POST /v1/catalog/cars/resolve` — resolve a concrete car ID by the full selection chain.

**Request body:**

```json
{
  "brandId": 32,
  "modelId": 12,
  "yearId": 5,
  "engineId": 9,
  "transmissionId": 2,
  "wheelDriveId": 1
}
```

**Success response:**

```json
{ "carId": 12345 }
```

**Errors:**
- `404 Not Found` — no car matches the provided combination;
- `503 Service Unavailable` — repository/data-access failure (e.g., SQL/connection issues).

### Test endpoint

`GET /v1/test/delay-ms?delay={millis}` — simulates a delay (no auth).  
`delay` is validated (min 1000, max 60000). Response: `204 No Content`.

## Error Model

Unified DTO `ErrorResponse`:

```json
{
  "timestamp": "2025-09-05T15:25:50.503899900",
  "status": 400,
  "error": "Bad Request",
  "message": [
    "The error of validation of the request"
  ],
  "path": "/v1/catalog/cars/resolve",
  "validationErrors": [
    { "field": "brandId", "message": "Id must be great of 0" }
  ]
}
```

Handlers include (non-exhaustive):
- `MethodArgumentNotValidException` and `HandlerMethodValidationException` → `400` with `validationErrors`.
- `TypeMismatchException` → `400` with a descriptive message.
- Domain exceptions (e.g., `NotFoundException`) mapped to appropriate status codes.
- Repository/infrastructure failures wrapped into `ServiceUnavailableException` → `503`.

## OpenAPI / Swagger

OpenAPI annotations live in `SwaggerConfig` and/or controller interfaces.  

- **Swagger UI:**  
  - with `server.servlet.context-path=/api`: `/api/swagger-ui/index.html`  
  - without a context path: `/swagger-ui/index.html`
- **OpenAPI JSON:** `/v3/api-docs`

Swagger endpoints and `/error` are permitted anonymously (`SecurityConfig`).

## Testing

Integration tests are located under `src/test/java/.../controller/CarCatalogLookupControllerIT.java`.

- The `test` profile may use Redis token initialization helpers (e.g., `RedisTmpTokenInitializer`) or Mockito-based repository stubs, depending on your setup.
- Ensure Redis is reachable according to `spring.data.redis.*` in `application-test.yml` or environment variables.

## Key Classes

- **Security:** `config.SecurityConfig`, `filter.TmpTokenAuthFilter`, `config.configs_components.CustomAuthenticationEntryPoint`, `CustomAccessDeniedHandler`
- **DB & Bootstrap:** `config/db_config.DatabaseBootstrap`, `AdminDataSourceConfig`, `VoriqDataSourceConfig`
- **Redis:** `config.RedisConfig`, `config.initialaler.RedisTmpTokenInitializer`
- **SQL init:** `config.initialaler.VoriqPostInitializer` — runs classpath SQL scripts if present
- **Errors:** `exception_handler.RestExceptionHandler`, DTOs `ErrorResponse`, `ValidationError`
- **Controllers:** `controller.api.CarCatalogLookupApi`, `controller.CarCatalogLookupController`, `controller.TestController`

## License

Add your preferred license (e.g., MIT) or state that the code is proprietary/internal.

---

**Author:** Ruslan Senkin  
**Contact:** rsenkin24@gmail.com
