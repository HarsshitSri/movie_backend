# Project Setup

Local setup for the Spring Boot app in `backend/MovieBooking/`.

## Requirements

| Tool | Version |
|------|---------|
| Java | 21 |
| Maven | 3.9+ (or use `./mvnw`) |
| PostgreSQL | 16+ |
| Git | Any recent version |

Optional: Docker, Docker Compose, Postman, IntelliJ IDEA

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

### Seed roles (required before first register)

Registration assigns the `USER` role. Insert roles once on a fresh database:

```sql
INSERT INTO roles (name, description) VALUES
  ('USER', 'Default user'),
  ('ADMIN', 'Administrator');
```

Run this after the app has created tables (`ddl-auto=update` on first start), or create the `roles` table manually to match [database-design.md](database-design.md).

---

## Environment variables

```bash
cd backend/MovieBooking
cp .env.example .env
```

Example `.env`:

```env
DB_URL=jdbc:postgresql://localhost:5432/movie_booking
DB_USERNAME=postgres
DB_PASSWORD=your_password
JWT_SECRET=change-me-to-a-long-random-secret
JWT_EXPIRATION=86400000
```

Load before running:

```bash
set -a && source .env && set +a
```

`application.properties` maps:

```properties
spring.datasource.url=${DB_URL}
spring.datasource.username=${DB_USERNAME}
spring.datasource.password=${DB_PASSWORD}
```

JWT defaults also exist in `application.properties`; prefer env overrides for secrets.

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

Expect a JSON body with a `token` field (`201`). If you see `Default role not found`, seed roles (SQL above).

---

## Docker

PostgreSQL + API together:

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
| `Default role not found` on register | `roles` table empty — run seed SQL |
| Port `8080` or `5432` in use | Stop the other process or change ports |
| Invalid email or password → `500` | Auth errors are not mapped to `401` yet |

---

## Related documentation

- [README.md](../README.md) — overview and quick start
- [api-design.md](api-design.md) — endpoints
- [testing.md](testing.md) — manual testing
- [deployment.md](deployment.md) — Docker
