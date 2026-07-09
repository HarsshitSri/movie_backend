# Architecture

This document describes the **current** architecture of the Movie Booking Backend as implemented in code. It does not describe planned features.

**Application:** `backend/MovieBooking/`  
**Base package:** `com.harshit.moviebooking`  
**Style:** Layered monolith  
**Database:** PostgreSQL (H2 for tests)

---

## Package Structure

```
com.harshit.moviebooking
├── MovieBookingApplication.java      # Spring Boot entry point
├── config                            # Spring configuration beans
│   ├── ApplicationConfig             # AuthenticationManager, DaoAuthenticationProvider
│   ├── PasswordConfig                # BCrypt PasswordEncoder bean
│   └── SecurityConfig                # SecurityFilterChain (permitAll)
├── controller                        # REST API layer
│   ├── AuthController
│   ├── MovieController
│   └── RatingController
├── dto                               # API request/response models
│   ├── auth                          # RegisterRequest, LoginRequest, AuthResponse
│   ├── movie                         # MovieRequestDto, MovieResponseDto
│   ├── rating                        # RatingRequestDto, RatingResponseDto
│   ├── role                          # RoleRequestDto, RoleResponseDto (empty placeholders)
│   └── user                          # UserRequestDto, UserResponseDto (empty placeholders)
├── entity                            # JPA entities
│   ├── User
│   ├── Role
│   ├── Movie
│   └── Rating
├── enums                             # AccountStatus, RoleName, ContentRating
├── exception                         # ApiErrorResponse, MovieNotFoundException, GlobalExceptionHandler
├── mapper                            # Static DTO ↔ entity mapping
│   ├── MovieMapper
│   └── RatingMapper
├── repository                        # Spring Data JPA repositories
│   ├── UserRepo
│   ├── RoleRepo
│   ├── MovieRepo
│   └── RatingRepository
├── security                          # JWT and Spring Security adapters
│   ├── JwtService
│   ├── JwtAuthenticationFilter
│   ├── CustomUserDetailsService
│   └── CustomUserDetails
└── service                           # Business logic interfaces
    ├── AuthService
    ├── MovieService
    ├── RatingService
    ├── UserService                   # Empty placeholder (not used)
    ├── RoleService                   # Empty placeholder (not used)
    └── impl
        ├── AuthServiceImpl
        ├── MovieServiceImpl
        ├── RatingServiceImpl
        ├── UserServiceImpl           # Empty placeholder (not used)
        └── RoleServiceImpl           # Empty placeholder (not used)
```

---

## High-Level Request Flow

### What the diagram should show

The previous diagram showed only:

```
Client → Controller → Service → Repository → PostgreSQL
```

That is directionally correct but **incomplete**. The implementation also includes:

- Spring Security filter chain (currently `permitAll()`)
- Jakarta Bean Validation on controller inputs
- DTOs at the API boundary (entities are not returned)
- Mappers inside services (not a separate Spring bean layer)
- `GlobalExceptionHandler` on the response path for some exceptions

### Actual request flow (happy path)

```
Client (HTTP/JSON)
    │
    ▼
Spring Security FilterChain
    │   SecurityConfig: csrf disabled, anyRequest().permitAll()
    │   JwtAuthenticationFilter exists but is NOT registered in the chain
    ▼
@RestController
    │   Accepts request DTOs (@RequestBody)
    │   Validates input (@Valid where applied)
    ▼
Service interface → ServiceImpl
    │   Business logic
    │   Entity construction / updates
    │   Mapper utilities (MovieMapper, RatingMapper)
    ▼
Repository (Spring Data JPA)
    ▼
PostgreSQL
    │
    ▼
Service maps Entity → Response DTO
    │
    ▼
Controller returns ResponseEntity<DTO>
    │
    ▼
Client (HTTP/JSON)
```

### Response / error flow

```
Controller or Service throws exception
    │
    ├─ MethodArgumentNotValidException
    │      → GlobalExceptionHandler → 400 ApiErrorResponse
    │
    ├─ MovieNotFoundException
    │      → GlobalExceptionHandler → 404 ApiErrorResponse
    │
    └─ RuntimeException (e.g. auth failures)
           → Not handled by GlobalExceptionHandler
           → Spring default error response (typically 500)
```

---

## Dependency Flow

Dependencies point inward: outer layers depend on inner abstractions, not the reverse.

```
Controller
    └── depends on → Service interface (AuthService, MovieService, RatingService)

ServiceImpl
    └── depends on → Repository interfaces
    └── depends on → Mapper utilities (static)
    └── depends on → other collaborators where needed
                     (e.g. AuthServiceImpl → PasswordEncoder, JwtService)

Repository
    └── depends on → Entity (via Spring Data JPA)

Security components
    └── JwtAuthenticationFilter → JwtService, CustomUserDetailsService
    └── CustomUserDetailsService → UserRepo
    └── ApplicationConfig → CustomUserDetailsService, PasswordEncoder

Config
    └── SecurityConfig injects JwtAuthenticationFilter and AuthenticationProvider
        (both unused in the current SecurityFilterChain definition)
```

**Constructor injection** is used throughout. Controllers depend on service interfaces, not implementations.

---

## Controller → Service → Repository Flow

### Authentication (`AuthController`)

| Step | Component | Responsibility |
|------|-----------|----------------|
| 1 | `AuthController` | Receives `RegisterRequest` / `LoginRequest`, returns `AuthResponse` |
| 2 | `AuthServiceImpl` | Registration, credential check, JWT creation |
| 3 | `UserRepo`, `RoleRepo` | Persist and load users/roles |
| 4 | `PasswordEncoder` | Hash and verify passwords |
| 5 | `JwtService` | Generate JWT on success |

`AuthServiceImpl` maps `RegisterRequest` to `User` manually (no dedicated mapper class).

### Movies (`MovieController`)

| Step | Component | Responsibility |
|------|-----------|----------------|
| 1 | `MovieController` | Receives `MovieRequestDto`, returns `MovieResponseDto` or `Page<MovieResponseDto>` |
| 2 | `MovieServiceImpl` | CRUD and title search logic |
| 3 | `MovieMapper` | `MovieRequestDto` → `Movie`, `Movie` → `MovieResponseDto` |
| 4 | `MovieRepo` | Persistence and pagination queries |

`MovieServiceImpl.searchMoviesByTitle()` exists but has **no controller endpoint**.

### Ratings (`RatingController`)

| Step | Component | Responsibility |
|------|-----------|----------------|
| 1 | `RatingController` | Receives `RatingRequestDto`, returns `RatingResponseDto` |
| 2 | `RatingServiceImpl` | Upsert rating, recalculate movie aggregates |
| 3 | `RatingRepository`, `MovieRepo`, `UserRepo` | Load and persist ratings, movies, users |
| 4 | `RatingMapper` | `Rating` → `RatingResponseDto` |

`RatingServiceImpl` updates `Movie.averageRating` and `Movie.ratingCount` after each rating write.

---

## DTO Usage

DTOs isolate the REST API from JPA entities.

| Package | Used in API | Purpose |
|---------|-------------|---------|
| `dto.auth` | Yes | Registration, login, token response |
| `dto.movie` | Yes | Movie create/update/read |
| `dto.rating` | Yes | Rating submission and response |
| `dto.user` | No | Empty placeholders |
| `dto.role` | No | Empty placeholders |

### Validation

`@Valid` is applied on:

- `AuthController` — register, login
- `MovieController` — create movie
- `RatingController` — rate movie

`MovieController.updateMovie()` does **not** currently use `@Valid`.

### Boundary rule

Controllers return DTOs (`AuthResponse`, `MovieResponseDto`, `RatingResponseDto`). Entities are not exposed directly in API responses.

---

## Mapper Usage

Mappers are **static utility classes**, not Spring beans.

| Mapper | Used by | Operations |
|--------|---------|------------|
| `MovieMapper` | `MovieServiceImpl` | `toEntity(MovieRequestDto)`, `toResponseDto(Movie)` |
| `RatingMapper` | `RatingServiceImpl` | `toResponseDto(Rating)` |

`MovieServiceImpl.updateMovie()` updates entity fields manually instead of using `MovieMapper`.

`AuthServiceImpl` builds `User` entities manually from `RegisterRequest`.

---

## Security Flow

### Registration

```
POST /api/auth/register
    → AuthController
    → AuthServiceImpl.register()
        → check UserRepo.existsByEmail / existsByUsername
        → RoleRepo.findByName(USER)
        → PasswordEncoder.encode(password)
        → UserRepo.save(user)
        → JwtService.generateToken(email)
    → AuthResponse { token }
```

### Login

```
POST /api/auth/login
    → AuthController
    → AuthServiceImpl.login()
        → UserRepo.findByEmail
        → PasswordEncoder.matches
        → JwtService.generateToken(email)
    → AuthResponse { token }
```

Login does **not** use `AuthenticationManager.authenticate()`. It validates credentials directly in the service.

### JWT filter (present but not active in the request chain)

`JwtAuthenticationFilter` is implemented to:

1. Read `Authorization: Bearer <token>`
2. Extract email via `JwtService`
3. Load user via `CustomUserDetailsService` → `UserRepo.findByEmail`
4. Validate token and set `SecurityContextHolder` authentication

However, `SecurityConfig` does **not** add this filter to `SecurityFilterChain`, and all endpoints are `permitAll()`. **Incoming requests do not require a JWT today.**

### Security configuration beans

| Class | Role |
|-------|------|
| `PasswordConfig` | Provides `BCryptPasswordEncoder` |
| `ApplicationConfig` | Provides `DaoAuthenticationProvider`, `AuthenticationManager` |
| `SecurityConfig` | Defines `SecurityFilterChain` with `permitAll()` |
| `CustomUserDetailsService` | Loads `User` for Spring Security |
| `CustomUserDetails` | Adapts `User` to `UserDetails` |

`DaoAuthenticationProvider` and `AuthenticationManager` are created but not used by the current login flow.

---

## Exception Flow

| Exception | Thrown from | Handled by | HTTP status |
|-----------|-------------|------------|-------------|
| `MethodArgumentNotValidException` | Controller validation | `GlobalExceptionHandler` | 400 |
| `MovieNotFoundException` | `MovieServiceImpl`, `RatingServiceImpl` | `GlobalExceptionHandler` | 404 |
| `RuntimeException` | `AuthServiceImpl`, `RatingServiceImpl` | Not handled centrally | 500 (Spring default) |

Error responses use `ApiErrorResponse` with timestamp, status, error message, and a field-level `errors` map.

---

## Architecture Diagrams

This repository contains **ASCII diagrams in markdown only**. There are no image files under `assets/` yet. See [assets/ASSETS_PLAN.md](../assets/ASSETS_PLAN.md) for planned PNG exports.

Each diagram below includes an **implementation alignment** note comparing the drawing to the current codebase.

### Diagram inventory

| Diagram | Location | Reflects implementation? |
|---------|----------|--------------------------|
| Simplified request flow | [README.md](../README.md) — Architecture Overview | **Partial** — omits security, DTOs, mappers, and exception handling |
| Happy-path request flow | This document — [Actual request flow](#actual-request-flow-happy-path) | **Yes** |
| Error / exception flow | This document — [Response / error flow](#response--error-flow) | **Yes** |
| Layered data flow (box) | Below | **Partial** — core layers only; see alignment note |
| Authentication component flow | Below | **Partial** — issuance path yes; JWT filter path is not active |
| Rating aggregate flow | This document — [Ratings flow](#ratings-ratingcontroller) | **Yes** (text only; no box diagram) |
| Booking / theatre / payment flow | **Not present** | **N/A** — not implemented; do not add as a current-architecture diagram |

There is **no booking-flow diagram** in this repository. Booking, theatres, showtimes, seats, and payments are roadmap items only ([README.md](../README.md), [requirements.md](requirements.md)).

---

### Layered data flow (core layers)

```
┌─────────┐
│ Client  │
└────┬────┘
     │ JSON
     ▼
┌─────────────────────┐
│ Spring Security     │  permitAll(); JWT filter not registered
└─────────┬───────────┘
          ▼
┌─────────────────────┐
│ Controller          │  Request DTO + @Valid
└─────────┬───────────┘
          ▼
┌─────────────────────┐
│ Service (impl)      │  Business logic + Mapper
└─────────┬───────────┘
          ▼
┌─────────────────────┐
│ Repository          │  Spring Data JPA
└─────────┬───────────┘
          ▼
┌─────────────────────┐
│ PostgreSQL          │
└─────────────────────┘
```

**Implementation alignment**

| Shown in diagram | Matches code? | Notes |
|------------------|---------------|-------|
| `Spring Security` with `permitAll()` | Yes | `SecurityConfig.securityFilterChain()` |
| JWT filter not registered | Yes | `JwtAuthenticationFilter` is a bean but never added via `http.addFilterBefore(...)` |
| `Controller` with DTO + `@Valid` | Yes | `AuthController`, `MovieController`, `RatingController` |
| `Service (impl)` + Mapper | Yes | `MovieMapper`, `RatingMapper`; auth maps `User` manually |
| `Repository` → PostgreSQL | Yes | `UserRepo`, `RoleRepo`, `MovieRepo`, `RatingRepository` |
| Response DTO mapping on return path | **Not shown** | Services map entities to response DTOs before `ResponseEntity` |
| `GlobalExceptionHandler` | **Not shown** | Handles validation (`400`) and `MovieNotFoundException` (`404`) only |
| `dto` as a separate package/layer | **Not shown** | DTOs live in `com.harshit.moviebooking.dto.*` |
| `entity` layer | **Not shown** | JPA entities are used inside services, not returned by controllers |
| Booking / payment modules | **Not applicable** | Not implemented |

For the full path including DTO mapping and errors, use the [happy-path](#actual-request-flow-happy-path) and [error-flow](#response--error-flow) diagrams above.

---

### Authentication component flow (issuance vs. filter)

```
AuthController
      │
      ▼
AuthServiceImpl ──────► UserRepo / RoleRepo
      │                      │
      ├──── PasswordEncoder    ▼
      │                   PostgreSQL
      └──── JwtService ──► AuthResponse (token)

JwtAuthenticationFilter (component exists, not in filter chain)
      │
      ├──── JwtService
      └──── CustomUserDetailsService ──► UserRepo
```

**Implementation alignment**

| Path | Matches code? | Notes |
|------|---------------|-------|
| Register → `AuthServiceImpl` → repos → BCrypt → JWT → `AuthResponse` | Yes | Default role `RoleName.USER` via `RoleRepo.findByName` |
| Login → `UserRepo.findByEmail` → `PasswordEncoder.matches` → JWT | Yes | Does **not** call `AuthenticationManager.authenticate()` |
| `JwtAuthenticationFilter` branch | **Not active** | Filter is injected into `SecurityConfig` but unused in `securityFilterChain()` |
| `CustomUserDetailsService` on live requests | **Not active** | Only referenced by the inactive filter and `ApplicationConfig` |
| `DaoAuthenticationProvider` / `AuthenticationManager` | **Unused** | Beans exist in `ApplicationConfig`; login bypasses them |
| JWT required on movie/rating endpoints | **No** | All routes are `permitAll()`; tokens are returned but not validated on API calls |
| `RatingController` uses authenticated principal | **No** | `RatingRequestDto.userId` is supplied in the request body |

**Outdated authentication assumptions to avoid**

- Do not depict `Client → JWT filter → authenticated endpoint` as the current flow.
- Do not show `AuthenticationManager` on the login path; credentials are checked in `AuthServiceImpl`.
- Do not imply role-based access (`@PreAuthorize`, admin-only movie CRUD) is enforced.

---

### Rating request flow (text diagram)

No box diagram exists for ratings. The implemented flow is:

```
POST /api/movies/{movieId}/ratings
    → RatingController (@Valid RatingRequestDto)
    → RatingServiceImpl.rateMovie()
        → MovieRepo.findById(movieId)     → MovieNotFoundException if missing
        → UserRepo.findById(userId)       → RuntimeException if missing
        → RatingRepository.findByUserAndMovie (upsert)
        → RatingRepository.save(rating)
        → RatingRepository.findByMovie(movie)  (reload all ratings for average)
        → update Movie.averageRating, Movie.ratingCount
        → MovieRepo.save(movie)
        → RatingMapper.toResponseDto(rating)
    → 201 RatingResponseDto
```

**Implementation alignment:** Matches `RatingController` and `RatingServiceImpl`. Rating writes are not tied to the JWT or `SecurityContext`; `userId` comes from the request body.

---

## What Changed from the Previous Documentation

| Previous statement / diagram | Issue | Correction |
|------------------------------|-------|------------|
| `Client → Controller → Service → Repository → PostgreSQL` only | Omits validation, DTOs, mappers, security, and exception handling | Expanded flow diagrams above; README diagram labeled simplified |
| Layered / auth diagrams labeled "accurate" | Box diagrams omit DTO return path, exception handler, and inactive JWT enforcement | Renamed with **implementation alignment** tables |
| Implied JWT protects endpoints | `SecurityConfig` uses `permitAll()`; filter not registered | Documented as not enforced |
| Booking / theatre flows in architecture docs | Could be read as implemented given project name | Explicit: no booking diagram; roadmap only |
| Listed only three service impls without placeholders | Empty `UserService`, `RoleService` and DTOs exist in codebase | Listed with placeholder note |
| Security layer described generically | Login bypasses `AuthenticationManager`; provider beans unused | Documented actual auth path |
| Exception layer one line | Only two exception types handled centrally | Documented full exception routing |
| No package tree | Incomplete view of `dto`, `enums`, `mapper`, `config` split | Full package structure added |
| Database ERD implied by schema doc | No image; planned entities could be confused with implemented | Added schema diagram section in database-design.md |

---

## Related Documentation

- [api-design.md](api-design.md) — REST endpoints
- [database-design.md](database-design.md) — schema and entities
- [requirements.md](requirements.md) — target product requirements
- [README.md](../README.md) — project overview
