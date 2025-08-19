# Security Service

Production-ready Spring Boot 3 service for authentication and token management (JDK 17, Maven). Uses PostgreSQL (JPA/Hibernate), Redis (for caching/blacklist), rate-limiting, CORS, and OpenAPI.

> **Base URL**: all endpoints are served under the application context path `"/api"` and the HTTP port defined by `PORT` (default mapping in container: `8081`).

---

## Table of Contents

- [What’s new in this version](#whats-new-in-this-version)
- [Features](#features)
- [Requirements](#requirements)
- [Quick start (Docker)](#quick-start-docker)
- [Database bootstrap (manual DB creation)](#database-bootstrap-manual-db-creation)
- [Configuration](#configuration)
- [Logging](#logging)
- [Schedulers (log archiving)](#schedulers-log-archiving)
- [API endpoints](#api-endpoints)
  - [Issue token — `POST /api/v1/tokens/issue`](#issue-token--post-apiv1tokensissue)
  - [Validate token — `GET /api/v1/tokens/validate`](#validate-token--get-apiv1tokensvalidate)
  - [Revoke token — `DELETE /api/v1/tokens/revoke`](#revoke-token--post-apiv1tokensrevoke)
  - [Docs & health](#docs--health)
- [Health checks](#health-checks)
- [Troubleshooting](#troubleshooting)
- [Security notes](#security-notes)
- [License](#license)

---

## What’s new in this version

- **New endpoint:** token **revoke** (`DELETE /api/v1/tokens/revoke`, returns `204 No Content`).
- **Background schedulers** documented: periodic zipping of daily logs and monthly cleanup of old archives.
- **Manual database creation** documented: you **must** create the PostgreSQL database yourself using the name from environment variables.
- Endpoint docs clarified and consolidated; quick-start and configuration polished.

---

## Features

- Token **issue** / **validate** / **revoke** flows with configurable TTL and blacklist.
- Redis-backed token blacklist; optional **prefix blocking** via `BLOCKED_PREFIX`.
- **Rate limits** per endpoint (`ISSUE_RATE_LIMIT`, `VALIDATE_RATE_LIMIT`).
- OpenAPI/Swagger UI at `/swagger-ui.html` (served under `/api` context path).
- Health checks and graceful startup ordering (DB/Redis first, then the app).

> **Developer convenience (dev profile):** when `SPRING_PROFILES_ACTIVE=dev`, the app seeds two test users if they are missing:
> - `userId=11111111-1111-1111-1111-111111111111`, `key=aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa`  
> - `userId=22222222-2222-2222-2222-222222222222`, `key=bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb`

---

## Requirements

- Docker & Docker Compose
- Maven 3.9+ (the project uses Maven)
- (Optional) JDK 17 if you plan to run locally outside of Docker
- **PostgreSQL database pre-created** with the name from your env (see [Database bootstrap](#database-bootstrap-manual-db-creation))

---

## Quick start (Docker)

1. Create your environment file:
   ```bash
   cp .env.example .env
   # or create .env using the Configuration section below
   ```

2. **Create the PostgreSQL database manually** before starting the app  
   (see [Database bootstrap](#database-bootstrap-manual-db-creation)).

3. Build and start the stack:
   ```bash
   docker compose up -d --build
   ```

4. Open Swagger UI:
   ```
   http://localhost:<PORT>/api/swagger-ui.html
   ```

> **Inside Compose**: use service names (e.g. `postgres`, `redis`) in your env instead of `localhost`.  
> **Outside Compose**: from containers, use `host.docker.internal` to reach services on your host.

---

## Database bootstrap (manual DB creation)

This service **does not** auto-create the PostgreSQL database. You **must** create it up front, using the **same name** you configure via `DB_NAME`.

### Option A — Create from your host using `psql`
```bash
# Export envs (adapt to your setup)
export DB_HOST=localhost
export DB_PORT=5432
export DB_USERNAME=postgres
export DB_NAME=security_service

# Create the database (you will be prompted for the password)
psql -h "$DB_HOST" -p "$DB_PORT" -U "$DB_USERNAME" -c "CREATE DATABASE \"$DB_NAME\";"
```

### Option B — Create inside a running Postgres container
```bash
# Replace "postgres" with your Compose service/container name
docker exec -it postgres psql -U "$DB_USERNAME" -c "CREATE DATABASE \"$DB_NAME\";"
```

> If you use your own external PostgreSQL, ensure networking and credentials in `.env` match (`DB_HOST`, `DB_PORT`, `DB_USERNAME`, `DB_PASSWORD`, `DB_NAME`).

---

## Configuration

Set via `.env`, shell env, or Spring properties. The app reads them at startup.

| Variable                                                      | Description                                                                                                    |
|--------------------------------------------------------------|----------------------------------------------------------------------------------------------------------------|
| `SPRING_PROFILES_ACTIVE`                                      | Active Spring profile, e.g. `dev` / `prod`                                                                     |
| `PORT`                                                        | HTTP port bound by the app (exposed by Compose)                                                                |
| `DB_HOST`, `DB_PORT`, `DB_NAME`, `DB_USERNAME`, `DB_PASSWORD` | PostgreSQL connection details (**DB must exist beforehand**)                                                   |
| `REDIS_HOST`, `REDIS_PORT`                                    | Redis connection details                                                                                       |
| `ALLOWED_ORIGINS`                                             | CORS allowed origins (comma-separated)                                                                         |
| `ACCESS_TOKEN_EXPIRATION_MS`                                  | Access token lifetime (ms)                                                                                     |
| `MAX_TOKEN`                                                   | Max number of tokens per principal                                                                             |
| `ISSUE_RATE_LIMIT`, `VALIDATE_RATE_LIMIT`                     | Rate-limit window (ms) for issue/validate endpoints                                                            |
| `BLOCKED_PREFIX`                                              | Token prefix to block (emergency revocation)                                                                   |
| `LOG_PATH`                                                    | **Optional** file path for Logback to write to (see [Logging](#logging))                                       |
| `JAVA_OPTS`                                                   | Custom JVM flags                                                                                                |
| `log.dir`                                                     | **Directory** with daily logs/archives for schedulers (e.g. `/opt/app/logs`)                                   |
| `archive.schedule.fixed-delay-ms`                             | Period for the zipping job; also the *age window* for files to include (default **259200000** = 3 days)        |
| `archive.schedule.initial-delay-ms`                           | Initial delay before the first zipping run (default **60000** = 60s)                                           |

> Spring’s relaxed binding lets you set `log.dir` as `LOG_DIR` in env, and `archive.schedule.*` as `ARCHIVE_SCHEDULE_*`.

---

## Logging

Two modes:

1. **Console (default)** – when `LOG_PATH` is **unset**, logs go to stdout/stderr (container-native).
2. **File logging** – set `LOG_PATH` to a path **inside the container** that’s **bind-mounted** to the host:
   - Mount a host directory (e.g. `/var/log/security-service`) to `/opt/app/logs`.
   - Set `LOG_PATH=/opt/app/logs/app.log`.

Daily service logs used by the schedulers follow the pattern:
```
voriq_token.YYYY-MM-DD.log
```
and are placed under `log.dir`.

---

## Schedulers (log archiving)

Two background jobs manage files in `log.dir`:

### 1) Zip daily logs (current & previous month)

- **Method:** `FilesZipper#zipFiles()`
- **Schedule:**
  - `fixedDelay = ${archive.schedule.fixed-delay-ms}` (default **3 days**)
  - `initialDelay = ${archive.schedule.initial-delay-ms}` (default **60s**)
  - `zone = Europe/Berlin`
- **Inputs:** `voriq_token.YYYY-MM-DD.log`
- **Rules:**
  - Include `.log` files **older than today** and **not older than** the configured *age window* (`fixed-delay-ms`).
  - Group by month relative to “today”: **current** and **previous** month.
- **Archive names:**
  - Single date → `voriq_token.<firstDate>.zip`
  - Range → `voriq_token.<firstDate>_<lastDate>.zip`

### 2) Purge old archives (monthly)

- **Method:** `FilesZipper#removeOldZipFiles()`
- **Schedule:** `cron = "0 0 0 1 * *"` (midnight, **first day of month**), `Europe/Berlin`.
- **Targets:** archives matching
  - `voriq_token.YYYY-MM-DD.zip`
  - `voriq_token.YYYY-MM-DD_YYYY-MM-DD.zip`
- **Rule:** delete archives **older than previous month** (strictly `< currentMonth - 1`).  
  Safe-delete ensures removals happen only within `log.dir`.

> To disable all scheduled tasks (e.g., in tests), set:  
> `spring.task.scheduling.enabled=false`.

---

## API endpoints

> **OpenAPI**: `/api/v3/api-docs`  
> **Swagger UI**: `/api/swagger-ui.html`

### Issue token — `POST /api/v1/tokens/issue`

- **Purpose:** Issue a short-lived access token for a principal.
- **Auth:** Not required (credentials in body).
- **Rate-limit:** `ISSUE_RATE_LIMIT`
- **Success:** `200 OK` with token JSON.

**Example**
```bash
curl -X POST "http://localhost:$PORT/api/v1/tokens/issue" \
  -H "Content-Type: application/json" \
  -d '{"userId":"11111111-1111-1111-1111-111111111111","key":"aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa"}'
```

---

### Validate token — `GET /api/v1/tokens/validate`

- **Purpose:** Validate a bearer token and return status/claims.
- **Auth:** `Authorization: Bearer <token>`
- **Rate-limit:** `VALIDATE_RATE_LIMIT`
- **Success:** `204 No Content`

**Example**
```bash
curl -i -X GET "http://localhost:$PORT/api/v1/tokens/validate" \
  -H "Authorization: Bearer <token>"
```

---

### Revoke token — `DELETE /api/v1/tokens/revoke`

- **Purpose:** Explicitly revoke a token (add to blacklist) so subsequent validations fail.
- **Auth:** `Authorization: Bearer <token>` *(the token to revoke)*
- **Body:** none
- **Success:** `204 No Content`

Controller (already implemented):
```java
@Override
public ResponseEntity<Void> revoke(String token) {
    tokenService.revokeToken(token);
    return ResponseEntity.noContent().build();
}
```

**Example**
```bash
curl -i -X DELETE "http://localhost:$PORT/api/v1/tokens/revoke" \
  -H "Authorization: Bearer <token>"
```

---

### Docs & health

- **Swagger UI:** `GET /api/swagger-ui.html`
- **OpenAPI JSON:** `GET /api/v3/api-docs`
- **Health:** `GET /actuator/health` (use in probes)

---

## Health checks

Spring Boot Actuator liveness/readiness endpoints are enabled. In container orchestration, wait for DB/Redis to become healthy before starting the app (Compose services include health checks and dependencies).

---

## Troubleshooting

- **“Connection to localhost refused” (from containers):** inside a container, `localhost` is the container. Use Compose service names (`postgres`, `redis`) or `host.docker.internal` if you point to services on your host.
- **Hibernate dialect/datasource errors:** verify all `DB_*` vars; ensure the **database exists** and is reachable (see [Database bootstrap](#database-bootstrap-manual-db-creation)).
- **Logs appear inside the container:** either `LOG_PATH` points to a non-mounted path or the volume mount is missing. Either mount a host directory to `/opt/app/logs` or unset `LOG_PATH`.
- **CORS blocked in browser:** set `ALLOWED_ORIGINS` with exact scheme/host/port of your frontend.

---

## Security notes

- Keep secrets (`DB_PASSWORD`, tokens) out of version control. Use `.env` only in dev; prefer secret managers in prod.
- Prefer short token lifetimes; enable **prefix blocking** for incident response.
- Review rate-limits for your expected traffic.

---

## License

MIT (or your chosen license). Add the full text in `LICENSE`.
