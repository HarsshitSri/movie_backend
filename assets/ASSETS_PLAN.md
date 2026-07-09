# Assets Plan

Recommended diagrams and screenshots for the Movie Booking Backend documentation.

**Scope:** Implemented functionality only. Do not create assets for booking, Swagger, reviews, watchlist, or enforced JWT authorization until those features exist.

**Repository status:** `assets/` exists but is empty. README and docs reference screenshots as not yet included.

---

## Priority Legend

| Priority | Meaning |
|----------|---------|
| **P1** | High value; directly supports onboarding and README |
| **P2** | Useful for architecture and API docs |
| **P3** | Nice to have; supports testing and deployment guides |

---

## Diagrams

### `diagrams/architecture-layered-flow.png`

| Field | Detail |
|-------|--------|
| **Purpose** | Illustrate the monolithic request path documented in [architecture.md](../docs/architecture.md) |
| **What should be visible** | Client → Spring Security (`permitAll`) → Controller → Service → Mapper → Repository → PostgreSQL; DTOs at the controller boundary; `GlobalExceptionHandler` on error path |
| **When to capture** | After diagram is drawn (Excalidraw, draw.io, or Mermaid export) — not a runtime screenshot |
| **Suggested location** | `assets/diagrams/architecture-layered-flow.png` |
| **Use in docs** | `docs/architecture.md`, README Architecture section |
| **Priority** | P1 |

---

### `diagrams/database-schema-erd.png`

| Field | Detail |
|-------|--------|
| **Purpose** | Visualize the four implemented tables and relationships |
| **What should be visible** | Tables: `roles`, `users`, `movies`, `ratings`; FKs: `users.role_id → roles.id`, `ratings.user_id → users.id`, `ratings.movie_id → movies.id`; unique constraint on (`user_id`, `movie_id`); denormalized `movies.average_rating` and `movies.rating_count` |
| **When to capture** | After modeling from [database-design.md](../docs/database-design.md) — design artifact, not a live DB screenshot |
| **Suggested location** | `assets/diagrams/database-schema-erd.png` |
| **Use in docs** | `docs/database-design.md`, README Database Overview |
| **Priority** | P1 |

---

### `diagrams/auth-token-issuance-flow.png`

| Field | Detail |
|-------|--------|
| **Purpose** | Document register/login JWT issuance (what is implemented today) |
| **What should be visible** | `POST /api/auth/register` and `POST /api/auth/login` → `AuthController` → `AuthServiceImpl` → `UserRepo` / `RoleRepo` / `PasswordEncoder` → `JwtService.generateToken()` → `AuthResponse { token }` |
| **When to capture** | Design-time diagram; base on code in `AuthServiceImpl` and `JwtService` |
| **Suggested location** | `assets/diagrams/auth-token-issuance-flow.png` |
| **Use in docs** | `docs/architecture.md`, `docs/api-design.md` Authentication section |
| **Priority** | P1 |

**Note:** Do not label this as full “JWT-protected API” flow. Endpoint enforcement is not implemented (`permitAll()` in `SecurityConfig`).

---

### `diagrams/rating-aggregate-update-flow.png`

| Field | Detail |
|-------|--------|
| **Purpose** | Explain how a rating write updates cached movie aggregates |
| **What should be visible** | `POST /api/movies/{movieId}/ratings` → `RatingServiceImpl` → upsert `ratings` row → recalculate average → update `movies.average_rating` and `movies.rating_count` |
| **When to capture** | Design-time diagram from `RatingServiceImpl` |
| **Suggested location** | `assets/diagrams/rating-aggregate-update-flow.png` |
| **Use in docs** | `docs/database-design.md`, `docs/architecture.md` |
| **Priority** | P2 |

---

### `diagrams/security-components-overview.png`

| Field | Detail |
|-------|--------|
| **Purpose** | Show Spring Security components that exist vs. what is active on requests |
| **What should be visible** | Beans: `JwtAuthenticationFilter`, `JwtService`, `CustomUserDetailsService`, `SecurityConfig`, `ApplicationConfig`, `PasswordConfig`; annotation that filter is **not** registered in chain and routes are `permitAll()` |
| **When to capture** | Design-time diagram reflecting current `SecurityConfig` |
| **Suggested location** | `assets/diagrams/security-components-overview.png` |
| **Use in docs** | `docs/architecture.md`, `docs/decisions.md` (JWT / Spring Security) |
| **Priority** | P2 |

---

## API Screenshots (Postman or HTTP client)

Capture with the application running (`./mvnw spring-boot:run` or `docker compose up`) and PostgreSQL available.

### `screenshots/api/postman-auth-register-201.png`

| Field | Detail |
|-------|--------|
| **Purpose** | Show successful user registration |
| **What should be visible** | `POST http://localhost:8080/api/auth/register`; request body with username, email, password, firstName, lastName, dateOfBirth; `201 Created`; response body `{ "token": "..." }` |
| **When to capture** | After registering a new user with valid payload |
| **Suggested location** | `assets/screenshots/api/postman-auth-register-201.png` |
| **Use in docs** | README examples, `docs/api-design.md`, `docs/testing.md` |
| **Priority** | P1 |

---

### `screenshots/api/postman-auth-login-200.png`

| Field | Detail |
|-------|--------|
| **Purpose** | Show successful login and JWT response |
| **What should be visible** | `POST http://localhost:8080/api/auth/login`; email and password; `200 OK`; `AuthResponse` with JWT |
| **When to capture** | After login with a registered user |
| **Suggested location** | `assets/screenshots/api/postman-auth-login-200.png` |
| **Use in docs** | README, `docs/api-design.md`, `docs/testing.md` |
| **Priority** | P1 |

---

### `screenshots/api/postman-movie-create-201.png`

| Field | Detail |
|-------|--------|
| **Purpose** | Document movie creation endpoint and request shape |
| **What should be visible** | `POST http://localhost:8080/api/movies`; `MovieRequestDto` fields (title, synopsis, releaseDate, runtimeMinutes, language, countryOfOrigin, contentRating, posterUrl); `201 Created`; response with `id`, `averageRating: 0`, `ratingCount: 0` |
| **When to capture** | After creating a movie with valid payload |
| **Suggested location** | `assets/screenshots/api/postman-movie-create-201.png` |
| **Use in docs** | README, `docs/api-design.md` |
| **Priority** | P1 |

---

### `screenshots/api/postman-movies-list-pagination-200.png`

| Field | Detail |
|-------|--------|
| **Purpose** | Show paginated movie listing |
| **What should be visible** | `GET http://localhost:8080/api/movies?page=0&size=10&sort=title&direction=asc`; `200 OK`; Spring `Page` JSON with `content`, `totalElements`, `totalPages`, `number`, `size` |
| **When to capture** | After at least two movies exist in the database |
| **Suggested location** | `assets/screenshots/api/postman-movies-list-pagination-200.png` |
| **Use in docs** | `docs/api-design.md`, `docs/testing.md` |
| **Priority** | P2 |

---

### `screenshots/api/postman-movie-get-by-id-200.png`

| Field | Detail |
|-------|--------|
| **Purpose** | Document single movie retrieval |
| **What should be visible** | `GET http://localhost:8080/api/movies/{id}`; `200 OK`; full `MovieResponseDto` |
| **When to capture** | After a movie exists; use a known ID |
| **Suggested location** | `assets/screenshots/api/postman-movie-get-by-id-200.png` |
| **Use in docs** | `docs/api-design.md` |
| **Priority** | P2 |

---

### `screenshots/api/postman-movie-rate-201.png`

| Field | Detail |
|-------|--------|
| **Purpose** | Show rating submission and response |
| **What should be visible** | `POST http://localhost:8080/api/movies/{movieId}/ratings`; body `{ "userId": 1, "rating": 9 }`; `201 Created`; `RatingResponseDto` with timestamps |
| **When to capture** | After a user and movie exist; optionally follow with a movie GET showing updated `averageRating` / `ratingCount` |
| **Suggested location** | `assets/screenshots/api/postman-movie-rate-201.png` |
| **Use in docs** | README, `docs/api-design.md`, `docs/testing.md` |
| **Priority** | P1 |

---

### `screenshots/api/postman-validation-error-400.png`

| Field | Detail |
|-------|--------|
| **Purpose** | Document validation error format |
| **What should be visible** | Invalid request (e.g. register with blank email or movie create without title); `400 Bad Request`; `ApiErrorResponse` with `"error": "Validation Failed"` and field-level `errors` map |
| **When to capture** | After sending a deliberately invalid `@Valid` request to register, login, create movie, or rate movie |
| **Suggested location** | `assets/screenshots/api/postman-validation-error-400.png` |
| **Use in docs** | `docs/api-design.md`, `docs/architecture.md` (exception flow) |
| **Priority** | P2 |

---

### `screenshots/api/postman-movie-not-found-404.png`

| Field | Detail |
|-------|--------|
| **Purpose** | Document movie-not-found handling |
| **What should be visible** | `GET` or `DELETE` on non-existent movie ID; `404 Not Found`; `ApiErrorResponse` with `"error": "Movie Not Found"` |
| **When to capture** | After requesting a movie ID that does not exist |
| **Suggested location** | `assets/screenshots/api/postman-movie-not-found-404.png` |
| **Use in docs** | `docs/api-design.md` |
| **Priority** | P2 |

---

## Deployment Screenshots

### `screenshots/docker/docker-compose-services-running.png`

| Field | Detail |
|-------|--------|
| **Purpose** | Confirm Docker Compose stack from [deployment.md](../docs/deployment.md) |
| **What should be visible** | Terminal output of `docker compose up` or `docker ps` showing `moviebooking-api` and `moviebooking-db` running; ports `8080` and `5432` |
| **When to capture** | After successful `docker compose up -d --build` from `backend/MovieBooking/` |
| **Suggested location** | `assets/screenshots/docker/docker-compose-services-running.png` |
| **Use in docs** | `docs/deployment.md`, README Docker section |
| **Priority** | P2 |

---

### `screenshots/docker/docker-api-health-check.png`

| Field | Detail |
|-------|--------|
| **Purpose** | Verify API responds when run in Docker |
| **What should be visible** | HTTP client hitting `GET http://localhost:8080/api/movies` (or register/login) with successful response while containers are running |
| **When to capture** | Immediately after Compose stack is up |
| **Suggested location** | `assets/screenshots/docker/docker-api-health-check.png` |
| **Use in docs** | `docs/deployment.md` |
| **Priority** | P3 |

---

## Suggested `assets/` Layout

```
assets/
├── ASSETS_PLAN.md                          (this file)
├── diagrams/
│   ├── architecture-layered-flow.png
│   ├── database-schema-erd.png
│   ├── auth-token-issuance-flow.png
│   ├── rating-aggregate-update-flow.png
│   └── security-components-overview.png
└── screenshots/
    ├── api/
    │   ├── postman-auth-register-201.png
    │   ├── postman-auth-login-200.png
    │   ├── postman-movie-create-201.png
    │   ├── postman-movies-list-pagination-200.png
    │   ├── postman-movie-get-by-id-200.png
    │   ├── postman-movie-rate-201.png
    │   ├── postman-validation-error-400.png
    │   └── postman-movie-not-found-404.png
    └── docker/
        ├── docker-compose-services-running.png
        └── docker-api-health-check.png
```

---

## Assets Not Recommended (not implemented)

| Asset | Reason |
|-------|--------|
| `swagger-ui.png` | Swagger/OpenAPI is planned; no UI in project |
| `booking-flow.png` | Booking module not implemented |
| `jwt-protected-endpoint-401.png` | Endpoints do not require JWT (`permitAll()`) |
| `refresh-token-flow.png` | Refresh tokens not implemented |
| `review-api.png` | Reviews not implemented |
| `watchlist-api.png` | Watchlist not implemented |
| `genre-filter-api.png` | Genre filtering not implemented |
| `ci-pipeline.png` | CI/CD not configured in repository |

---

## Recommended Capture Order

1. **P1 diagrams** — architecture, database ERD, auth token issuance  
2. **P1 API screenshots** — register, login, create movie, rate movie  
3. **P2 items** — pagination, validation 400, not-found 404, rating flow diagram  
4. **P2/P3 deployment** — Docker Compose running, API health check  

---

## Linking Assets in Documentation

After assets are added, update:

| Document | Suggested embeds |
|----------|------------------|
| `README.md` | Replace Screenshots placeholder; link register, create movie, rate movie screenshots |
| `docs/architecture.md` | `architecture-layered-flow.png`, `auth-token-issuance-flow.png` |
| `docs/database-design.md` | `database-schema-erd.png`, `rating-aggregate-update-flow.png` |
| `docs/api-design.md` | Per-endpoint Postman screenshots |
| `docs/deployment.md` | Docker Compose screenshot |
| `docs/testing.md` | Reference API screenshots as expected test results |
