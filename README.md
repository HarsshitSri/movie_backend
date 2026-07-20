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
| Roles seeded on startup (`USER`, `ADMIN`) | Done |
| JWT required on write endpoints | Done — `GET` movies public; create/update/delete/rate need Bearer token |
| Basic HTML/CSS/JS UI for navigation | Done — served at `/` |
| Role-based access (`USER` / `ADMIN` on endpoints) | Partial — roles stored; not used for authorization rules yet |
| Theatres / booking / payments | Planned |

**Why it is not “just CRUD”:** JWT-protected writes, denormalized rating aggregates, role seeding, and design docs under `docs/`.

---

## Quick start (Docker)

```bash
git clone git@github.com:HarsshitSri/movie_backend.git
cd movie_backend/backend/MovieBooking
docker compose up -d postgres
./mvnw spring-boot:run
```

Or run API + DB together:

```bash
docker compose up -d --build
```

API / UI: [http://localhost:8080](http://localhost:8080/)

**Note:** Compose maps Postgres to host port **5434** (to avoid clashing with a local Postgres on 5432). App defaults use `jdbc:postgresql://localhost:5434/movie_booking` with user `postgres` / password `000`.

Roles (`USER`, `ADMIN`) are seeded automatically on startup. Register via the UI or `POST /api/auth/register`, then use the JWT for write endpoints.

HTTP examples: [docs/quickstart.http](docs/quickstart.http)

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
│   ├── src/main/resources/static/   # HTML / CSS / JS UI
│   ├── Dockerfile
│   ├── docker-compose.yml
│   └── pom.xml
├── docs/                     # Architecture, API, DB, decisions
├── assets/                   # Diagram / screenshot plan (images TBD)
└── README.md
```

### Frontend pages

| Path | Purpose |
|------|---------|
| `/` or `/index.html` | Home + navigation |
| `/movies.html` | Paginated movie list |
| `/movie.html?id=` | Movie detail, rate, delete |
| `/movie-form.html` | Create movie (JWT) |
| `/login.html` / `/register.html` | Auth |

Packages: `controller` → `service` → `repository` → `entity`, plus `dto`, `mapper`, `security`, `config`, `exception`.

---

## Architecture (simplified)

```
Client → Controller → Service → Repository → PostgreSQL
```

Also in play: request DTOs + `@Valid`, mappers, JWT filter on the security chain, and `GlobalExceptionHandler` for validation / movie-not-found.

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

## Authentication

**Implemented:** register, login, BCrypt hashing, JWT generation, JWT filter on the security chain, roles seeded on startup.

**Access rules today**

| Endpoints | Auth |
|-----------|------|
| `POST /api/auth/**` | Public |
| `GET /api/movies`, `GET /api/movies/{id}` | Public |
| Create / update / delete movie, rate movie | Requires `Authorization: Bearer <token>` |

Role names exist in the DB; endpoint rules do **not** yet distinguish `USER` vs `ADMIN` (any authenticated user can write).

---

## API overview

Base URL: `http://localhost:8080`

| Method | Endpoint | Auth |
|--------|----------|------|
| `POST` | `/api/auth/register` | No |
| `POST` | `/api/auth/login` | No |
| `GET` | `/api/movies` | No |
| `GET` | `/api/movies/{id}` | No |
| `POST` | `/api/movies` | Bearer JWT |
| `PUT` | `/api/movies/{id}` | Bearer JWT |
| `DELETE` | `/api/movies/{id}` | Bearer JWT |
| `POST` | `/api/movies/{movieId}/ratings` | Bearer JWT |

Full reference: [docs/api-design.md](docs/api-design.md) · Quick requests: [docs/quickstart.http](docs/quickstart.http)

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
  -H "Authorization: Bearer YOUR_JWT_HERE" \
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
  -H "Authorization: Bearer YOUR_JWT_HERE" \
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

Then hit the API. Roles are seeded automatically on startup.

More detail: [docs/setup.md](docs/setup.md)

---

## Testing

```bash
cd backend/MovieBooking
./mvnw test
```

Uses H2. Includes a context-load test and an API flow test (register → login → create movie with JWT). Manual scenarios: [docs/testing.md](docs/testing.md)

---

## Documentation

| Document | Description |
|----------|-------------|
| [docs/setup.md](docs/setup.md) | Local setup |
| [docs/quickstart.http](docs/quickstart.http) | Copy-paste HTTP requests |
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
| v1 | Auth, movies, ratings, PostgreSQL, Docker, JWT on writes | In progress |
| v2 | Role-based access (`ADMIN` vs `USER`) | Planned |
| v3 | Theatres, showtimes, booking | Planned |
| v4 | Payments, notifications | Planned |
| v5 | OpenAPI, CI/CD, broader tests | Planned |

---

## License

Educational / portfolio use. No formal open-source license file yet.
