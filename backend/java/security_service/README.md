# VORIQ Security Service

A Spring Boot 3 service for issuing and validating access tokens. Active sessions are stored in **Redis**; if Redis becomes unavailable the service transparently falls back to **in‑memory** storage and migrates accumulated data back to Redis once it is reachable again. The service also enforces per‑user **rate limiting** and a **max active tokens** policy with temporary blocking.

---

## Table of Contents
- [Overview](#overview)
- [Requirements](#requirements)
- [Environment Variables](#environment-variables)
- [How to Run](#how-to-run)
  - [Run PostgreSQL (docker-compose)](#run-postgresql-docker-compose)
  - [Run Redis](#run-redis)
  - [Run the Application (dev profile)](#run-the-application-dev-profile)
  - [Build a Jar](#build-a-jar)
- [API](#api)
  - [Issue Token](#issue-token)
  - [Swagger / OpenAPI](#swagger--openapi)
- [Rate Limiting & Blocking](#rate-limiting--blocking)
- [Storage Strategies & Migration](#storage-strategies--migration)
- [Logging](#logging)
- [Profiles](#profiles)
- [Testing](#testing)
- [Troubleshooting](#troubleshooting)

---

## Overview

**Base context path:** `/api`  
**Main endpoint:** `/v1/tokens/issue`

Components:
- **Token issuing** (`TokenController` → `TokenServiceImpl`)
- **Token storage** via strategies:
  - `RedisTokenStoreStrategy` (primary)
  - `InMemoryTokenStoreStrategy` (fallback when Redis is down)
  - `DelegatingTokenStoreStrategy` orchestrates selection and triggers migration back to Redis
- **Rate limiting filter** (`TokenRateLimitFilter`) on the issue endpoint
- **Global error handling** (`RestExceptionHandler`)
- **AOP logging** (`GlobalLoggingAspect`) for successful operations and handled errors

---

## Requirements

- **JDK 17**
- **Maven 3.9+** (or use the included `mvnw` wrapper)
- **PostgreSQL 15** (see `docker-compose.yml` in the repo)
- **Redis 7+**

---

## Environment Variables

> These are referenced in `src/main/resources/application.yml` and the `*-dev.yml/test.yml` profiles.

### Core service
| Variable | Description | Example |
|---|---|---|
| `PORT` | HTTP server port | `8080` |
| `ALLOWED_ORIGINS` | Comma-separated CORS origins | `http://localhost:3000` |

### PostgreSQL
| Variable | Description | Example |
|---|---|---|
| `DB_HOST` | PostgreSQL host | `localhost` |
| `DB_PORT` | PostgreSQL port | `5432` |
| `DB_NAME` | Database name | `securitydb` |
| `DB_USERNAME` | Database user | `postgres` |
| `DB_PASSWORD` | Database password | `secret` |

> The **users** table must exist. With JPA `ddl-auto=update` it will be created automatically; otherwise create it manually:

```sql
CREATE TABLE IF NOT EXISTS users (
  id BIGSERIAL PRIMARY KEY,
  user_id UUID NOT NULL UNIQUE,
  "key"   UUID NOT NULL UNIQUE
);
```

### Redis
| Variable | Description | Example |
|---|---|---|
| `SPRING_DATA_REDIS_HOST` | Redis host | `localhost` |
| `SPRING_DATA_REDIS_PORT` | Redis port | `6379` |
| `SPRING_DATA_REDIS_PASSWORD` | Redis password (optional) | *(unset or value)* |

### Token & limits
| Variable | Description | Example |
|---|---|---|
| `ACCESS_TOKEN_EXPIRATION_MS` | Access token TTL (also used for temp blocking) | `600000` |
| `MAX_TOKEN` | Max simultaneous tokens per user | `4` |
| `BLOCKED_PREFIX` | Redis key prefix for user blocking | `blocked:` |
| `RATE_LIMIT` | Min interval between `/issue` requests per user (ms) | `5000` |

---

## How to Run

### Run PostgreSQL (docker-compose)

The repo provides a simple PostgreSQL + pgAdmin stack:

```bash
docker compose up -d postgres pgadmin
```

Environment in `docker-compose.yml`:
- DB: `securitydb`, user: `postgres`, password: <Your password>
- pgAdmin on `http://localhost:8082` (email `admin@admin.com`, password <Your password>)

### Run Redis

If you don’t have Redis locally, run it via Docker:

```bash
docker run -d --name redis -p 6379:6379 redis:7-alpine
```

### Run the Application (dev profile)

Set environment variables (see above), then:

```bash
./mvnw spring-boot:run -Dspring-boot.run.profiles=dev
```

### Build a Jar

```bash
./mvnw clean package -DskipTests
java -jar target/security_service-0.0.1-SNAPSHOT.jar --spring.profiles.active=dev
```

---

## API

**Base URL:** `http://localhost:${PORT}/api`

### Issue Token

**POST** `/v1/tokens/issue`  
Request body:
```json
{
  "userId": "11111111-1111-1111-1111-111111111111",
  "key": "aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa"
}
```

Example:
```bash
curl -X POST "http://localhost:8080/api/v1/tokens/issue"   -H "Content-Type: application/json"   -d '{"userId":"11111111-1111-1111-1111-111111111111","key":"aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa"}'
```

Success **200**:
```json
{ "accessToken": "d3cc8ac7-38d6-4f6c-83c8-ecf37c843e8a" }
```

Error codes:
- **400** – validation error (missing/invalid fields)
- **404** – user not found / not active
- **403** – user temporarily blocked (exceeded `MAX_TOKEN`)
- **429** – too many requests (rate limited by `RATE_LIMIT`)
- **503** – backend unavailable (e.g., DB connection)
- **500** – internal server error

### Swagger / OpenAPI

### How to open Swagger UI (step‑by‑step)

1. **Export required env vars** (at least `PORT`; see [Environment Variables](#environment-variables)):
   ```bash
   export PORT=8080
   # optional: others like DB_*, SPRING_DATA_REDIS_*, etc.
   ```

2. **Run the app** (dev profile):
   ```bash
   ./mvnw spring-boot:run -Dspring-boot.run.profiles=dev
   # or: java -jar target/security_service-*.jar --spring.profiles.active=dev
   ```

3. **Open Swagger UI** in your browser:
   - UI: `http://localhost:${PORT}/api/swagger-ui/index.html` (e.g., http://localhost:8080/api/swagger-ui/index.html)
   - OpenAPI JSON: `http://localhost:${PORT}/api/v3/api-docs`

4. **Verify via curl** (optional):
   ```bash
   curl -s http://localhost:8080/api/v3/api-docs | head
   ```

> Notes
> - The base context path is `/api`. If you change it (`server.servlet.context-path`), the Swagger paths will shift accordingly.
> - Springdoc is already configured in `SwaggerConfig`. Docs path is `/v3/api-docs`.
> - If you see **401/403**, ensure your security config permits:
>   `/v3/api-docs/**`, `/swagger-ui/**`, `/swagger-ui.html`.
> - If you see **404**, double‑check `PORT` and that the app is running.


- UI: `http://localhost:${PORT}/api/swagger-ui/index.html`
- Docs JSON: `http://localhost:${PORT}/api/v3/api-docs`

Springdoc is configured via `SwaggerConfig` and `application*.yml`.

---

## Rate Limiting & Blocking

- `TokenRateLimitFilter` enforces a **per-user** delay between token issuance requests. The interval is controlled by `RATE_LIMIT` (ms). Violations return **429**.
- The token store strategies enforce **max active tokens** per user (`MAX_TOKEN`). When the limit is reached, old sessions may be revoked and the user is **temporarily blocked** for `ACCESS_TOKEN_EXPIRATION_MS` (a block key is kept under `BLOCKED_PREFIX + <userId>` in Redis).

---

## Storage Strategies & Migration

- Primary storage: **Redis** (`RedisTokenStoreStrategy`)
- Fallback: **In-memory** (`InMemoryTokenStoreStrategy`) used when Redis is down
- `DelegatingTokenStoreStrategy`:
  - selects the first applicable strategy
  - detects when Redis becomes active again
  - triggers migration of accumulated in-memory tokens back to Redis via `TokenMigrationService`

The migration is idempotent and runs only when Redis transitions to the active role and the in-memory buffer is not empty.

---

## Logging

Logback is configured in `logback-spring.xml`.

- Console logs (root logger)
- Aspect logs (`com.voriq.security_service.aop`) both to console **and** a rolling file: `logs/voriq_token.YYYY-MM-DD.log`

The `GlobalLoggingAspect` logs:
- successful issuance (`INFO`, Code=200)
- handled errors (`ERROR` with an HTTP code, user id and message)

---

## Profiles

- **dev** – local development (reads env vars from your shell)
- **test** – testing profile (uses H2 for DB in tests; Redis must be available or mocked)

Run with a specific profile:
```bash
java -jar target/security_service-0.0.1-SNAPSHOT.jar --spring.profiles.active=dev
```

---

## Testing

Run all tests:
```bash
./mvnw test
```

Notes:
- Integration tests assume a reachable Redis (configure `SPRING_DATA_REDIS_*`).
- For DB‑unavailable scenarios the global exception handler maps to **503**.
- Some tests use per‑test waiting (`Awaitility` is recommended) to avoid flakiness instead of raw `Thread.sleep`.

---

## Troubleshooting

- **Cannot connect to Redis**: verify `SPRING_DATA_REDIS_HOST/PORT` and that Redis is running (Docker or local).
- **DB connection errors**: verify `DB_*` variables and that the database exists (`securitydb` by default in `docker-compose.yml`).
- **CORS errors in browser**: set `ALLOWED_ORIGINS` to your frontend origin(s).
- **429 Too Many Requests**: you’re hitting the `RATE_LIMIT` window—wait and retry.
- **User frequently blocked**: you may be exceeding `MAX_TOKEN`; adjust values or revoke stale sessions.

---
