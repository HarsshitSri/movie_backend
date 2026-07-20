# Movie Platform Backend

REST API for **user authentication**, a **movie catalog**, and **user ratings** — built with Java 21 and Spring Boot.

Ticket booking (theatres, seats, showtimes) is on the roadmap; it is **not** implemented yet.

**Author:** [Harshit Srivastava](https://github.com/HarsshitSri)  
**Repo:** [github.com/HarsshitSri/movie_backend](https://github.com/HarsshitSri/movie_backend)

---

## What this project does

| Feature | Status |
|---------|--------|
| Register / login with BCrypt + JWT issuance | Done |
| Movie CRUD + pagination / sorting | Done |
| Rate a movie (1–10); updates average & count | Done |
| Layered architecture, DTOs, validation | Done |
| PostgreSQL + Docker Compose | Done |
| JWT required on endpoints | Partial — filter exists; routes are `permitAll()` today |
| Role-based access (`USER` / `ADMIN`) | Partial — roles stored; not enforced on endpoints |
| Theatres / booking / payments | Planned |

**Why it is not “just CRUD”:** auth token flow, denormalized rating aggregates, Spring Security wiring (documented as partial), and design docs under `docs/`.

---

## Quick start (Docker)

```bash
git clone git@github.com:HarsshitSri/movie_backend.git
cd movie_backend/backend/MovieBooking
docker compose up -d --build
```

API: [http://localhost:8080](http://localhost:8080)

### Seed roles (required before first register)

Registration looks up the `USER` role. On a fresh database, insert roles once:

```sql
INSERT INTO roles (name, description) VALUES
  ('USER', 'Default user'),
  ('ADMIN', 'Administrator');
```

Then call `POST /api/auth/register` (see [API](#api-overview)).

---

## Tech stack

| Technology | Version / notes |
|------------|-----------------|
| Java | 21 |
| Spring Boot | 3.5.4 |
| Spring Security | 6.x (JWT components present) |
| Spring Data JPA | Hibernate |
| PostgreSQL | 16+ |
| JWT | JJWT 0.12.7 |
| Maven | Wrapper included |
| Docker | Multi-stage image + Compose |

---

## Project structure

```
movie_backend/
├── backend/MovieBooking/     # Spring Boot app (run from here)
│   ├── src/
│   ├── Dockerfile
│   ├── docker-compose.yml
│   └── pom.xml
├── docs/                     # Architecture, API, DB, decisions
├── assets/                   # Diagram / screenshot plan (images TBD)
└── README.md
```

Packages: `controller` → `service` → `repository` → `entity`, plus `dto`, `mapper`, `security`, `config`, `exception`.

---

## Architecture (simplified)

```
Client → Controller → Service → Repository → PostgreSQL
```

Also in play: request DTOs + `@Valid`, mappers, Spring Security (`permitAll()` today), and `GlobalExceptionHandler` for validation / movie-not-found.

Full request and security flow: [docs/architecture.md](docs/architecture.md)

---

## Database

| Table | Purpose |
|-------|---------|
| `roles` | `USER`, `ADMIN` |
| `users` | Accounts (BCrypt `password_hash`) |
| `movies` | Catalog + cached `average_rating` / `rating_count` |
| `ratings` | One rating per user per movie |

Details: [docs/database-design.md](docs/database-design.md)

---

## Authentication (honest status)

**Implemented:** register, login, BCrypt hashing, JWT generation (`JwtService`, `JwtAuthenticationFilter`, `CustomUserDetailsService`).

**Not enforced yet:** `SecurityConfig` uses `permitAll()` and does not register the JWT filter on the chain. Movie and rating endpoints do **not** require a token today.

---

## API overview

Base URL: `http://localhost:8080`  
Auth required today: **No** (`permitAll()`)

| Method | Endpoint | Description |
|--------|----------|-------------|
| `POST` | `/api/auth/register` | Register; returns JWT |
| `POST` | `/api/auth/login` | Login; returns JWT |
| `GET` | `/api/movies` | List (`page`, `size`, `sort`, `direction`) |
| `GET` | `/api/movies/{id}` | Get by ID |
| `POST` | `/api/movies` | Create |
| `PUT` | `/api/movies/{id}` | Update |
| `DELETE` | `/api/movies/{id}` | Delete |
| `POST` | `/api/movies/{movieId}/ratings` | Create or update rating |

Full reference: [docs/api-design.md](docs/api-design.md)

### Example: register

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

### Example: create movie

```bash
curl -s -X POST http://localhost:8080/api/movies \
  -H "Content-Type: application/json" \
  -d '{
    "title": "Interstellar",
    "synopsis": "A team travels through a wormhole in space.",
    "releaseDate": "2014-11-07",
    "runtimeMinutes": 169,
    "language": "English",
    "countryOfOrigin": "USA",
    "contentRating": "PG_13",
    "posterUrl": "https://example.com/poster.jpg"
  }'
```

### Example: rate a movie

```bash
curl -s -X POST http://localhost:8080/api/movies/1/ratings \
  -H "Content-Type: application/json" \
  -d '{"userId": 1, "rating": 9}'
```

---

## Run without Docker

**Prerequisites:** Java 21, PostgreSQL 16+, Maven (or `./mvnw`).

```bash
git clone git@github.com:HarsshitSri/movie_backend.git
cd movie_backend/backend/MovieBooking
```

Create DB and env:

```sql
CREATE DATABASE movie_booking;
```

```bash
cp .env.example .env   # if present; or export vars below
set -a && source .env && set +a   # bash
./mvnw spring-boot:run
```

| Variable | Purpose |
|----------|---------|
| `DB_URL` | e.g. `jdbc:postgresql://localhost:5432/movie_booking` |
| `DB_USERNAME` | DB user |
| `DB_PASSWORD` | DB password |
| `JWT_SECRET` | Signing secret (recommended) |
| `JWT_EXPIRATION` | Lifetime in ms (optional) |

Then seed roles (SQL above) and hit the API.

More detail: [docs/setup.md](docs/setup.md)

---

## Testing

```bash
cd backend/MovieBooking
./mvnw test
```

Uses H2 (`src/test/resources/application.properties`). Suite is minimal (context load). Manual scenarios: [docs/testing.md](docs/testing.md)

---

## Documentation

| Document | Description |
|----------|-------------|
| [docs/setup.md](docs/setup.md) | Local setup |
| [docs/architecture.md](docs/architecture.md) | Layers, request & security flow |
| [docs/api-design.md](docs/api-design.md) | Endpoint reference |
| [docs/database-design.md](docs/database-design.md) | Schema |
| [docs/decisions.md](docs/decisions.md) | Technical tradeoffs |
| [docs/deployment.md](docs/deployment.md) | Docker |
| [docs/testing.md](docs/testing.md) | Manual testing |
| [docs/changelog.md](docs/changelog.md) | Version history |
| [assets/ASSETS_PLAN.md](assets/ASSETS_PLAN.md) | Planned diagrams / screenshots |

---

## Roadmap

| Phase | Scope | Status |
|-------|-------|--------|
| v1 | Auth, movies, ratings, PostgreSQL, Docker | In progress |
| v2 | Enforce JWT + role-based access | Planned |
| v3 | Theatres, showtimes, booking | Planned |
| v4 | Payments, notifications | Planned |
| v5 | OpenAPI, CI/CD, broader tests | Planned |

---

## License

Educational / portfolio use. No formal open-source license file yet.
