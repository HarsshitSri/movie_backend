# Movie Booking Backend

A REST API backend for a movie platform, built with Java and Spring Boot. The project currently covers user authentication, movie catalog management, and user ratings. It is structured as a layered monolith and is intended to grow toward full ticket booking functionality.

**Author:** Harshit Srivastava

---

## Project Overview

This repository contains a Spring Boot application that exposes HTTP APIs for registering users, authenticating with JWT, managing movies, and submitting ratings. Data is stored in PostgreSQL using Spring Data JPA and Hibernate.

The codebase is organized for learning and portfolio use, with supporting design documents under `docs/`.

---

## Motivation

The project was started as a backend engineering exercise focused on:

- Designing a normalized PostgreSQL schema
- Building REST APIs with validation and DTOs
- Implementing authentication with Spring Security and JWT
- Applying a layered architecture that can scale to booking and payments later

---

## Features

### Implemented

| Area | Capability |
|------|------------|
| Authentication | User registration and login |
| Security | BCrypt password hashing, JWT token generation, JWT filter component |
| Movies | Create, read, update, delete movies |
| Movies | Paginated listing with sorting |
| Ratings | Submit or update a rating for a movie (`1`–`10`) |
| Validation | Jakarta Bean Validation on request DTOs |
| Errors | Global handling for validation errors and movie-not-found cases |
| Persistence | JPA entities for `User`, `Role`, `Movie`, and `Rating` |
| Tooling | Maven Wrapper, Docker, Docker Compose |

### Planned

See [Roadmap](#roadmap) and [docs/changelog.md](docs/changelog.md).

---

## Current Development Status

| Module | Status |
|--------|--------|
| User registration / login | Implemented |
| JWT issuance | Implemented |
| Endpoint authorization | Partial — JWT components exist, but endpoints are currently `permitAll()` |
| Role-based access control | Partial — roles are stored; endpoint enforcement is not active |
| Movie CRUD | Implemented |
| Movie ratings | Implemented |
| Movie title search API | Not exposed — service method exists without controller endpoint |
| Booking / theatres / seats | Planned |
| Payments / notifications | Planned |
| Swagger / OpenAPI | Planned |
| Automated API tests | Minimal — context load test only |

---

## Tech Stack

| Technology | Version / Notes |
|------------|-----------------|
| Java | 21 |
| Spring Boot | 3.5.4 |
| Spring Web | REST controllers |
| Spring Data JPA | Hibernate |
| Spring Security | 6.x |
| Spring Validation | Jakarta Bean Validation |
| PostgreSQL | 16+ (runtime) |
| H2 | In-memory database for tests |
| JWT | JJWT 0.12.7 |
| Lombok | Compile-time boilerplate reduction |
| Maven | 3.9+ (wrapper included) |
| Docker | Multi-stage image build |

---

## Project Structure

The runnable application lives in `backend/MovieBooking/`.

```
Movie_Backend/
├── backend/
│   └── MovieBooking/          # Spring Boot application
│       ├── src/
│       │   ├── main/
│       │   │   ├── java/com/harshit/moviebooking/
│       │   │   └── resources/
│       │   └── test/
│       ├── Dockerfile
│       ├── docker-compose.yml
│       └── pom.xml
├── docs/                      # Architecture and design documents
└── README.md
```

### Application packages

```
com.harshit.moviebooking
├── config          # Security and application configuration
├── controller      # REST controllers
├── dto             # Request/response models
├── entity          # JPA entities
├── enums           # Domain enums
├── exception       # Exceptions and global handler
├── mapper          # DTO mapping utilities
├── repository      # Spring Data repositories
├── security        # JWT and user details
└── service         # Business logic
    └── impl
```

---

## Architecture Overview

The application follows a layered monolith. The diagram below is a **simplified overview** of the happy path only:

```
Client
  │
  ▼
Controller
  │
  ▼
Service
  │
  ▼
Repository
  │
  ▼
PostgreSQL
```

**What this diagram omits (but the code includes):**

- Spring Security filter chain (`permitAll()` today; JWT filter bean exists but is not registered)
- Request/response DTOs at the controller boundary (`dto.auth`, `dto.movie`, `dto.rating`)
- Jakarta Bean Validation (`@Valid`) on most write endpoints
- Static mappers (`MovieMapper`, `RatingMapper`) and manual entity mapping in auth/update flows
- `GlobalExceptionHandler` for validation (`400`) and movie-not-found (`404`) responses
- Booking, theatre, showtime, seat, and payment modules — **not implemented**

Accurate flow diagrams, diagram inventory, and implementation alignment notes: [docs/architecture.md](docs/architecture.md)

---

## Database Overview

PostgreSQL is used with Hibernate `ddl-auto=update` in the default configuration.

### Implemented tables

| Table | Purpose |
|-------|---------|
| `users` | Registered users |
| `roles` | `USER` and `ADMIN` roles |
| `movies` | Movie catalog with cached `average_rating` and `rating_count` |
| `ratings` | User-to-movie ratings (one rating per user per movie) |

Relationships include:

- `Role` → `User` (one-to-many); `users.role_id` → `roles.id`
- `User` → `Rating` and `Movie` → `Rating` (one-to-many)
- Unique constraint on (`user_id`, `movie_id`) in `ratings`
- Denormalized `average_rating` and `rating_count` on `movies` (updated when ratings are saved)

There is **no ERD image** in the repo yet. Planned entities (genres, reviews, watchlist) and booking-domain tables (theatres, showtimes, bookings) are **not** in the current schema.

Full schema notes and ERD documentation: [docs/database-design.md](docs/database-design.md)

---

## Authentication Overview

### What is implemented

- `POST /api/auth/register` — creates a user with the default `USER` role
- `POST /api/auth/login` — validates credentials and returns a JWT
- Passwords are hashed with BCrypt
- JWTs are signed using `jwt.secret` and expire based on `jwt.expiration`
- `JwtAuthenticationFilter` and `CustomUserDetailsService` are present

### Current limitation

`SecurityConfig` currently allows all requests (`permitAll()`), and the JWT filter is not registered on the security filter chain. **Movie and rating endpoints do not require a token today**, even though login returns a JWT.

This is intentional to document accurately: authentication is implemented, but API authorization is not fully enforced yet.

---

## API Overview

Full endpoint reference: [docs/api-design.md](docs/api-design.md)

Base URL (local): `http://localhost:8080`

**Authentication required:** No (all endpoints are currently `permitAll()`)

### Endpoint Summary

| Method | Endpoint | Description |
|--------|----------|-------------|
| `POST` | `/api/auth/register` | Register a new user |
| `POST` | `/api/auth/login` | Login and receive JWT |
| `GET` | `/api/movies` | List movies (pagination and sorting) |
| `GET` | `/api/movies/{id}` | Get movie by ID |
| `POST` | `/api/movies` | Create a movie |
| `PUT` | `/api/movies/{id}` | Update a movie |
| `DELETE` | `/api/movies/{id}` | Delete a movie |
| `POST` | `/api/movies/{movieId}/ratings` | Create or update a rating |

**Pagination query params** (`GET /api/movies`): `page` (default `0`), `size` (default `10`), `sort` (default `id`), `direction` (default `asc`)

### Example: Register

```http
POST /api/auth/register
Content-Type: application/json
```

```json
{
  "username": "harshit",
  "email": "harshit@example.com",
  "password": "Password@123",
  "firstName": "Harshit",
  "lastName": "Srivastava",
  "dateOfBirth": "2003-07-25"
}
```

### Example: Create movie

```http
POST /api/movies
Content-Type: application/json
```

```json
{
  "title": "Interstellar",
  "synopsis": "A team travels through a wormhole in space.",
  "releaseDate": "2014-11-07",
  "runtimeMinutes": 169,
  "language": "English",
  "countryOfOrigin": "USA",
  "contentRating": "PG_13",
  "posterUrl": "https://example.com/poster.jpg"
}
```

### Example: Rate a movie

```http
POST /api/movies/1/ratings
Content-Type: application/json
```

```json
{
  "userId": 1,
  "rating": 9
}
```

API design reference: [docs/api-design.md](docs/api-design.md)

---

## Getting Started

### Prerequisites

| Tool | Version |
|------|---------|
| Java | 21 |
| Maven | 3.9+ (or use `./mvnw`) |
| PostgreSQL | 16+ (for local non-Docker runs) |
| Docker & Docker Compose | Optional |

### Clone the repository

```bash
git clone <repository-url>
cd Movie_Backend
```

---

## Running Locally

### 1. Start PostgreSQL

Create a database:

```sql
CREATE DATABASE movie_booking;
```

### 2. Configure environment variables

```bash
cd backend/MovieBooking
cp .env.example .env
```

Edit `.env` as needed, then load it:

```bash
set -a && source .env && set +a
```

### 3. Run the application

```bash
./mvnw spring-boot:run
```

The API starts at `http://localhost:8080`.

Detailed setup: [docs/setup.md](docs/setup.md)

---

## Environment Variables

| Variable | Purpose | Required |
|----------|---------|----------|
| `DB_URL` | JDBC URL (e.g. `jdbc:postgresql://localhost:5432/movie_booking`) | Yes |
| `DB_USERNAME` | Database username | Yes |
| `DB_PASSWORD` | Database password | Yes |
| `JWT_SECRET` | JWT signing secret | Recommended |
| `JWT_EXPIRATION` | Token lifetime in milliseconds | Optional |

These map to `spring.datasource.*` and `jwt.*` properties via Spring Boot externalized configuration.

Example file: [backend/MovieBooking/.env.example](backend/MovieBooking/.env.example)

---

## Docker Instructions

From `backend/MovieBooking/`:

### Run database and application

```bash
docker compose up -d --build
```

| Service | URL |
|---------|-----|
| API | `http://localhost:8080` |
| PostgreSQL | `localhost:5432` |

### Build image only

```bash
docker build -t moviebooking .
```

Deployment notes: [docs/deployment.md](docs/deployment.md)

---

## Testing

### Automated tests

The project includes a basic Spring Boot context load test:

```bash
cd backend/MovieBooking
./mvnw test
```

Tests use an in-memory H2 database via `src/test/resources/application.properties`.

### Manual API testing

Manual test scenarios are documented in [docs/testing.md](docs/testing.md) (Postman-based).

There is no broad unit or integration test suite yet.

---

## Documentation Links

| Document | Description |
|----------|-------------|
| [docs/setup.md](docs/setup.md) | Local setup |
| [docs/architecture.md](docs/architecture.md) | Layered architecture |
| [docs/api-design.md](docs/api-design.md) | API endpoints |
| [docs/database-design.md](docs/database-design.md) | Schema and relationships |
| [docs/requirements.md](docs/requirements.md) | Product requirements (target scope with implementation status) |
| [docs/deployment.md](docs/deployment.md) | Docker deployment |
| [docs/testing.md](docs/testing.md) | Manual testing guide |
| [docs/changelog.md](docs/changelog.md) | Version history |
| [docs/decisions.md](docs/decisions.md) | Technical decisions and tradeoffs |

---

## Screenshots

<!-- Add API response screenshots, architecture diagrams, or Postman collections here -->

_Screenshots not included yet._

---

## Roadmap

| Phase | Scope | Status |
|-------|-------|--------|
| v1 | Auth, movies, ratings, PostgreSQL, Docker | In progress |
| v2 | Enforce JWT authorization and role-based access | Planned |
| v3 | Theatres, screens, showtimes, booking | Planned |
| v4 | Payments and notifications | Planned |
| v5 | OpenAPI docs, CI/CD, expanded test coverage | Planned |

---

## Future Improvements

Items documented in the codebase and changelog as planned work:

- Theatre, screen, and seat management
- Show scheduling and ticket booking
- Payment integration
- Email notifications
- Refresh tokens
- Swagger / OpenAPI
- Expanded exception handling coverage
- Unit and integration tests
- CI/CD pipeline
- Fully enforced endpoint security

---

## Learning Outcomes

This project demonstrates:

- Building REST APIs with Spring Boot 3 and Java 21
- Separating controllers, services, repositories, and DTOs
- Modeling relational data with JPA and PostgreSQL
- Password hashing and JWT-based authentication flows
- Request validation and centralized exception handling
- Pagination and sorting with Spring Data
- Packaging and running a Spring Boot app with Docker

---

## License

This project is intended for educational and portfolio purposes.
