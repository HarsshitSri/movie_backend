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

## Build Image

From `backend/MovieBooking/`:

```bash
docker build -t moviebooking .
```

---

## Run with Docker Compose

From `backend/MovieBooking/`:

```bash
docker compose up -d --build
```

Stop services:

```bash
docker compose down
```

---

## Services

| Service | URL |
|---------|-----|
| Application | `http://localhost:8080` |
| PostgreSQL | `localhost:5432` |

Docker Compose service names:

| Container | Purpose |
|-----------|---------|
| `moviebooking-api` | Spring Boot application |
| `moviebooking-db` | PostgreSQL 16 database |

---

## Environment Variables

Configured in `docker-compose.yml` for the `app` service:

| Variable | Purpose |
|----------|---------|
| `DB_URL` | JDBC URL (`jdbc:postgresql://postgres:5432/movie_booking`) |
| `DB_USERNAME` | Database username |
| `DB_PASSWORD` | Database password |
| `JWT_SECRET` | JWT signing secret |
| `JWT_EXPIRATION` | Token lifetime in milliseconds |

These map to Spring Boot properties via externalized configuration (`spring.datasource.*`, `jwt.*`).

For local (non-Docker) setup, see [setup.md](setup.md) and `backend/MovieBooking/.env.example`.

---

## Related Documentation

- [setup.md](setup.md) — local development setup
- [README.md](../README.md) — project overview
