# Security Service

Production‑ready Spring Boot 3 service for authentication and token management (JDK 17, Maven). Ships with PostgreSQL (JPA/Hibernate), Redis (for caching/blacklist), rate‑limiting, CORS, and OpenAPI.

> **Base URL**: all endpoints are served under the application context path `"/api"` and the HTTP port defined by `PORT` (default mapping in container: `8081`).

---

## Table of Contents

* [What’s new in this version](#whats-new-in-this-version)
* [Features](#features)
* [Requirements](#requirements)
* [Quick start (Docker)](#quick-start-docker)
* [Configuration](#configuration)
* [Logging](#logging)
* [API endpoints](#api-endpoints)

    * [Issue token — ](#issue-token--post-apitokenissue)[`POST /api/v1/tokens/issue`](#issue-token--post-apitokenissue)
    * [Validate token — ](#validate-token--get-apitokenvalidate)[`GET /api/v1/tokens/validate`](#validate-token--get-apitokenvalidate)
    * [Docs & health](#docs--health)
* [New endpoint](#new-endpoint)
* [Health checks](#health-checks)
* [Troubleshooting](#troubleshooting)
* [Security notes](#security-notes)
* [License](#license)

---

## What’s new in this version

* Endpoint docs corrected: `validate` is **GET**; removed `/api/v1/tokens/block-prefix`; clarified that the project uses **Maven**.

* **Docker Compose** flow documented (build, run, env). No YAML embedded here, just the steps.

* **File logging option** documented (log to host disk without keeping logs inside the container).

* **API endpoints** section added with descriptions and examples.

* **Table of Contents** added for quicker navigation.

* **New endpoint** placeholder kept (see below) — send the exact path/body/response and we’ll finalize it.

---

## Features

* Token **issue**/**validate** flows with configurable TTL.
* Redis‑backed token blacklist; configurable prefix blocking via `BLOCKED_PREFIX`.
* **Rate limits** per endpoint (`ISSUE_RATE_LIMIT`, `VALIDATE_RATE_LIMIT`).
* OpenAPI/Swagger UI available under `/swagger-ui.html` with docs at `/v3/api-docs`.
* Health checks and graceful startup ordering (DB/Redis first, then the app).

---

## Requirements

* Docker / Docker Compose
* Maven 3.9+ (the project uses Maven)
* (Optional) JDK 17 if you plan to run locally without containers

---

## Quick start (Docker)

1. Copy the example environment file and adjust values:

    * `cp .env.example .env` (or create `.env` with values from the **Configuration** section below).
2. Build and start the stack:

    * `docker compose up -d --build`
3. Open the API:

    * Swagger UI → `http://localhost:<PORT>/api/swagger-ui.html`

> **Note on host DB access from containers**: if you run Postgres outside of Compose, you may need to map `host.docker.internal` to the host gateway so the container can reach services on your machine. Inside Compose (single network) use the service name (e.g., `postgres`) instead of `localhost`.

---

## Configuration

Set via `.env` or your CI/CD environment. The app reads them at startup.

| Variable                                                      | Description                                                                                                              |
| ------------------------------------------------------------- | ------------------------------------------------------------------------------------------------------------------------ |
| `SPRING_PROFILES_ACTIVE`                                      | Active Spring profile, e.g. `dev` / `prod`                                                                               |
| `PORT`                                                        | HTTP port the app binds to inside the container (exposed to host by Compose)                                             |
| `DB_HOST`, `DB_PORT`, `DB_NAME`, `DB_USERNAME`, `DB_PASSWORD` | PostgreSQL connection details                                                                                            |
| `REDIS_HOST`, `REDIS_PORT`                                    | Redis connection details                                                                                                 |
| `ALLOWED_ORIGINS`                                             | CORS allowed origins (comma‑separated)                                                                                   |
| `ACCESS_TOKEN_EXPIRATION_MS`                                  | Access token lifetime in milliseconds                                                                                    |
| `MAX_TOKEN`                                                   | Max number of tokens per principal (enforced by the service)                                                             |
| `ISSUE_RATE_LIMIT`, `VALIDATE_RATE_LIMIT`                     | Rate limit window (ms) for issue/validate endpoints                                                                      |
| `BLOCKED_PREFIX`                                              | Token prefix to block (useful for emergency revocation)                                                                  |
| `LOG_PATH`                                                    | **Optional**: absolute path inside the container for file logging (see **Logging**). Leave unset to disable file logging |
| `JAVA_OPTS`                                                   | Custom JVM flags (e.g., memory settings)                                                                                 |

> **Important:** When running everything with Compose, set `DB_HOST=postgres` and `REDIS_HOST=redis` (service names on the Compose network), **not** `localhost`.

---

## Logging

You have two options:

1. **Console (default)**
   When `LOG_PATH` is **unset**, the app logs to stdout/stderr only. This is the simplest, container‑native setup (use `docker logs`, or forward to your log stack).

2. **File logging to host disk**
   If your Logback config points at `${LOG_PATH}`, you can enable file logging **without keeping logs inside the container**:

    * Pick a directory on the **host**, e.g. `/var/log/security-service` (or `C:\voriq\logs` on Windows).
    * Mount it into the container at `/opt/app/logs` (Compose volume binding).
    * Set `LOG_PATH=/opt/app/logs/app.log` in your environment.

With this setup, all log files are created on the host. If you remove the bind mount but keep `LOG_PATH` set, the app will create files **inside** the container — avoid this by either keeping the mount or unsetting `LOG_PATH`.

> Tip: rotate/ship logs using your platform’s log driver or an external agent on the host.

---

## API endpoints

> **The canonical schemas are published via OpenAPI** at `/api/v3/api-docs` (Swagger UI at `/api/swagger-ui.html`). Below are concise descriptions only — no examples or code.

### Issue token — POST /api/v1/token/issue

* **Purpose:** Issue a short‑lived access token for a principal.
* **Method & Path:** `POST /api/v1/tokens/issue`
* **Auth:** Not required (credentials are provided in the request body).
* **Request:** JSON payload with principal credentials (e.g., login identifier and secret). Optional context fields (such as device or client metadata) may be included if supported by the service configuration.
* **Processing:** Enforces per‑endpoint rate limit defined by `ISSUE_RATE_LIMIT` and validates the principal.
* **Success (200 OK):** Returns an access token (JSON).
* **

### Validate token — GET /api/v1/token/validate

* **Purpose:** Validate a bearer token and return its status and key claims.
* **Method & Path:** `GET /api/v1/tokens/validate`
* **Auth:** `Authorization: Bearer <token>` header is required.
* **Request:** No body; token is supplied via the `Authorization` header.
* **Processing:** Verifies signature/format, expiry, blacklist/prefix rules, and enforces `VALIDATE_RATE_LIMIT`.
* **Success (204 NO CONTENT):** 
### Docs & health

* **Swagger UI:** `GET api/swagger-ui/index.html`
* **OpenAPI JSON:** `GET /api/v3/api-docs`
* **Health:** `GET /actuator/health` (for orchestrator probes)

## New endpoint

Document your new API here in the same style as the existing ones.

**Summary**
*Brief one‑liner of what the endpoint does.*

**Method & Path**
`<METHOD> /api/<resource>`

**Headers**

* `Authorization: Bearer <token>` *(if required)*
* `Content-Type: application/json`

**Request body**

```jsonc
{
  // TODO: add fields
}
```

**Success response** `200` (example)

```jsonc
{
  "success": true,
  "data": {
    // TODO: add fields
  }
}
```

**Error responses**

* `400 Bad Request` – validation error (payload details in body)
* `401 Unauthorized` – missing/invalid credentials
* `429 Too Many Requests` – rate limit exceeded (respect `Retry-After` header)
* `5xx` – server‑side issues

**Example (curl)**

```bash
curl -X <METHOD> \
  "http://localhost:<PORT>/api/<resource>" \
  -H "Authorization: Bearer <token>" \
  -H "Content-Type: application/json" \
  -d '{ /* request body */ }'
```

> **Rate limiting**: If applicable, configure the relevant `*_RATE_LIMIT` variables.

---

## Health checks

* Liveness/Readiness are enabled by default (via Spring Boot Actuator). Use them in your orchestrator.
* The application starts only after DB and Redis are reachable (Compose uses health‑checks to order startup).

---

## Troubleshooting

* **Connection to** `localhost` **refused**: inside containers, `localhost` means the container itself. Use the Compose service name (`postgres`) or expose an external port and point to the host.
* **Hibernate dialect/datasource errors**: confirm all DB env vars are set; the JDBC URL is assembled from `DB_*` vars.
* **Logs appear inside the container**: either `LOG_PATH` points to a non‑mounted path or the volume mount is missing. Mount a host directory to `/opt/app/logs` or unset `LOG_PATH`.
* **CORS blocked in browser**: set `ALLOWED_ORIGINS` with the exact scheme/host/port of your frontend.

---

## Security notes

* Keep secrets (`DB_PASSWORD`, tokens) out of Git. Provide them through `.env` in dev and a secret manager in prod.
* Limit token lifetimes and enable prefix blocking for incident response.

---

