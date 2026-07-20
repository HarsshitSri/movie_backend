# Project Setup

Local setup for the Spring Boot app in `backend/MovieBooking/`.

## Requirements

| Tool | Version |
|------|---------|
| Java | 21 |
| Maven | 3.9+ (or use `./mvnw`) |
| PostgreSQL | 16+ |
| Git | Any recent version |

Optional: Docker, Docker Compose, Postman / IntelliJ HTTP Client

---

## Clone

```bash
git clone git@github.com:HarsshitSri/movie_backend.git
cd movie_backend
```

---

## Database

```sql
CREATE DATABASE movie_booking;
```

For the recommended Docker DB (Compose):

```bash
cd backend/MovieBooking
docker compose up -d postgres
```

Host JDBC URL defaults to port **5434**: `jdbc:postgresql://localhost:5434/movie_booking` (user `postgres`, password `000`).

Roles (`USER`, `ADMIN`) are inserted automatically on startup by `RoleDataInitializer` if missing.

---

## Environment variables

```bash
cd backend/MovieBooking
cp .env.example .env
```

Example `.env`:

```env
DB_URL=jdbc:postgresql://localhost:5434/movie_booking
DB_USERNAME=postgres
DB_PASSWORD=your_password
JWT_SECRET=change-me-to-a-long-random-secret
JWT_EXPIRATION=86400000
```

Load before running (optional if you rely on `application.properties` defaults):

```bash
set -a && source .env && set +a
```

Defaults in `application.properties` already point at Compose Postgres on port **5434**.

---

## Run

From `backend/MovieBooking/`:

```bash
./mvnw spring-boot:run
```

API: `http://localhost:8080`

### Smoke check

```bash
curl -s -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "harshit",
    "email": "harshit@example.com",
    "password": "Password@123",
    "firstName": "Harshit",
    "lastName": "Srivastava",
    "dateOfBirth": "2003-07-25"
  }'
```

Expect `201` and a `token`. Use that token for movie create/update/delete and ratings:

```bash
curl -s -X POST http://localhost:8080/api/movies \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_JWT_HERE" \
  -d '{ ... }'
```

More requests: [quickstart.http](quickstart.http)

---

## Docker

```bash
cd backend/MovieBooking
docker compose up -d --build
```

Details: [deployment.md](deployment.md)

---

## Troubleshooting

| Problem | Likely cause |
|---------|--------------|
| App fails to start / DB connection error | `DB_URL` / credentials not set or Postgres not running |
| `401` on create movie / rate | Missing or invalid `Authorization: Bearer <token>` |
| Port `8080` or `5432` in use | Stop the other process or change ports |
| Invalid email or password → `500` | Auth errors are not mapped to `401` yet |

---

## Related documentation

- [README.md](../README.md) — overview and quick start
- [quickstart.http](quickstart.http) — HTTP client requests
- [api-design.md](api-design.md) — endpoints
- [testing.md](testing.md) — testing
- [deployment.md](deployment.md) — Docker
