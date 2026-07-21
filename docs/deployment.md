# Deployment

Docker deployment guide for the application in `backend/MovieBooking/`.

## Technologies

- Docker
- Docker Compose

---

## Prerequisites

- Docker
- Docker Compose

---

## Quick start (recommended)

From `backend/MovieBooking/`:

```bash
docker compose up -d --build
```

- **UI / API:** http://localhost:8080  
- **Postgres (host):** localhost:5434 → container 5432  

Roles, demo admin, and demo movies are seeded automatically on startup.

If ports `8080` or `5434` are already in use on your machine:

```bash
APP_HOST_PORT=8081 DB_HOST_PORT=5435 docker compose up -d --build
```

Then open http://localhost:8081

### Smoke check

```bash
curl -s -o /dev/null -w "%{http_code}\n" http://localhost:8080/api/movies
```

Expect `200`. Register or use the demo admin, then call protected endpoints with a Bearer token (see [setup.md](setup.md) / [quickstart.http](quickstart.http)).

Stop services:

```bash
docker compose down
```

Data is kept in the `postgres-data-v2` volume. To wipe the database volume as well:

```bash
docker compose down -v
```

---

## Build image only

```bash
docker build -t moviebooking .
```

---

## Services

| Service | Default URL |
|---------|-------------|
| Application | `http://localhost:8080` |
| PostgreSQL (host) | `localhost:5434` → container `5432` |

| Container | Purpose |
|-----------|---------|
| `moviebooking-api` | Spring Boot application |
| `moviebooking-db` | PostgreSQL 16 database |

---

## Environment Variables

Set via shell env or a `.env` file next to `docker-compose.yml` (see `.env.example`).

| Variable | Purpose | Default |
|----------|---------|---------|
| `APP_HOST_PORT` | Host port for the API | `8080` |
| `DB_HOST_PORT` | Host port for Postgres | `5434` |
| `POSTGRES_DB` | Database name | `movie_booking` |
| `POSTGRES_USER` | Database user | `postgres` |
| `POSTGRES_PASSWORD` | Database password | `000` (change for any shared/public deploy) |
| `JWT_SECRET` | JWT signing secret | demo default (change for public deploy) |
| `JWT_EXPIRATION` | Token lifetime (ms) | `86400000` |

Inside Compose, the app connects with `jdbc:postgresql://postgres:5432/<db>` (service DNS), not the host port.

For local (non-Docker) setup, see [setup.md](setup.md).

---

## Related Documentation

- [setup.md](setup.md) — local development setup
- [README.md](../README.md) — project overview
