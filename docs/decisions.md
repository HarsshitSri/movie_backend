# Technical Decisions

This document records major engineering decisions evidenced in the repository: source code, `pom.xml`, configuration files, Git history, and design documentation.

Where alternatives are listed without explicit discussion in the repo, they are noted as **typical alternatives** — not confirmed evaluation outcomes.

---

## 1. Spring Boot

### What was chosen
- **Spring Boot 3.5.4** as the application framework
- **Java 21** as the language runtime
- Starters in use: `spring-boot-starter-web`, `spring-boot-starter-data-jpa`, `spring-boot-starter-security`, `spring-boot-starter-validation`

### Why it was chosen
- Listed as a non-functional requirement in [requirements.md](requirements.md)
- Provides integrated support for REST APIs, JPA, security, and validation in a single monolithic application
- Maven Wrapper (`mvnw`) was added in the initial project setup commit (`8fa97a4`)

### Alternatives considered
- **Not explicitly documented** in commit messages or design notes
- **Typical alternatives:** Quarkus, Micronaut, plain Spring Framework without Boot, or a non-JVM stack (Node.js, Go)

### Tradeoffs
| Benefit | Cost |
|---------|------|
| Fast project bootstrap and convention-based configuration | Framework coupling and Spring-specific patterns |
| Large ecosystem and documentation | Heavier runtime than minimal micro-frameworks |
| Aligns with portfolio and enterprise Java goals | Upgrade path must be managed explicitly |

### Revision noted in Git history
The initial scaffold (`8fa97a4`) used **Spring Boot 4.1.0** and **Java 26**. These were changed to **Spring Boot 3.5.4** and **Java 21** in commit `1d04639` (*Add project documentation and deployment files*). The repository does not record the reason for this change.

---

## 2. PostgreSQL

### What was chosen
- **PostgreSQL** as the production relational database
- JDBC driver: `org.postgresql:postgresql` (runtime scope)
- Connection via environment variables: `DB_URL`, `DB_USERNAME`, `DB_PASSWORD`

### Why it was chosen
- Specified in [requirements.md](requirements.md) non-functional requirements
- Documented in [database-design.md](database-design.md) with a normalized relational schema (3NF)
- Docker Compose uses `postgres:16` for local and containerized deployment

### Alternatives considered
- **Not explicitly documented** in the repository
- **Typical alternatives:** MySQL/MariaDB, H2 (production), SQL Server, Oracle, or a document store (MongoDB)

### Tradeoffs
| Benefit | Cost |
|---------|------|
| Strong relational integrity for users, roles, movies, and ratings | Requires running a separate database service |
| Mature JSON/advanced SQL features if needed later | Schema migrations must be managed deliberately |
| Good fit for normalized booking-domain data planned in requirements | Operational overhead compared to embedded databases |

---

## 3. Spring Data JPA (Hibernate)

### What was chosen
- **Spring Data JPA** with **Hibernate** as the ORM
- JPA entities in `com.harshit.moviebooking.entity`
- Spring Data repository interfaces (`JpaRepository`)
- Schema management: `spring.jpa.hibernate.ddl-auto=update`

### Why it was chosen
- Included from the initial project setup (`spring-boot-starter-data-jpa` in `8fa97a4`)
- Supports the entity-relationship model documented before implementation ([database-design.md](database-design.md) preceded application code in Git history)
- Reduces boilerplate for CRUD and derived query methods (`findByEmail`, `findByTitleContainingIgnoreCase`, etc.)

### Alternatives considered
- **Not explicitly documented**
- **Typical alternatives:** JDBC templates, jOOQ, MyBatis, or raw SQL without an ORM

### Tradeoffs
| Benefit | Cost |
|---------|------|
| Rapid persistence layer development | ORM abstraction can hide SQL and lazy-loading behavior |
| Repository pattern fits layered architecture | `ddl-auto=update` is convenient for development but risky for production |
| Aligns with Spring Boot defaults | No Flyway/Liquibase migration tool is present in `pom.xml` |

---

## 4. JWT Authentication (JJWT)

### What was chosen
- **JWT** tokens issued on register and login (`AuthResponse.token`)
- **JJWT 0.12.7** (`jjwt-api`, `jjwt-impl`, `jjwt-jackson`)
- `JwtService` for token generation and validation
- `JwtAuthenticationFilter` as a `OncePerRequestFilter`
- Configuration: `jwt.secret`, `jwt.expiration` in `application.properties`

### Why it was chosen
- Listed under authentication non-functional requirements in [requirements.md](requirements.md): *JWT Authentication*
- Added in auth commits (`156405b`, `fa1f118`) alongside `AuthController` and `AuthServiceImpl`
- Stateless tokens suit a REST API without server-side sessions

### Alternatives considered
- **Not explicitly documented**
- **Typical alternatives:** server-side sessions with cookies, OAuth 2.0 / OpenID Connect, API keys, or Spring Security session-based form login

### Tradeoffs
| Benefit | Cost |
|---------|------|
| Stateless authentication scales horizontally in theory | Token revocation and refresh require additional design (refresh tokens are **planned**, not implemented) |
| Common pattern for SPA/mobile clients | Secret management and expiration must be configured carefully |
| Integrates with Spring Security filter model | **Current gap:** `SecurityConfig` uses `permitAll()` and does not register `JwtAuthenticationFilter` in the chain |

---

## 5. Spring Security and BCrypt

### What was chosen
- **Spring Security** (`spring-boot-starter-security` from initial setup)
- **BCrypt** password hashing via `BCryptPasswordEncoder` (`PasswordConfig`)
- `DaoAuthenticationProvider` and `AuthenticationManager` beans (`ApplicationConfig`)
- `CustomUserDetailsService` and `CustomUserDetails` for Spring Security integration
- `SecurityConfig` with CSRF disabled and `permitAll()` for all routes

### Why it was chosen
- Spring Security is the standard security stack for Spring Boot applications
- [database-design.md](database-design.md) specifies: *Only BCrypt password hashes are stored* in `password_hash`
- `AuthServiceImpl` uses `PasswordEncoder` for registration and login

### Alternatives considered
- **Documented in database design:** plain-text passwords were rejected; only hashed passwords are stored
- **Typical alternatives:** Argon2, PBKDF2, or delegating password encoding through Spring's `DelegatingPasswordEncoder`

### Tradeoffs
| Benefit | Cost |
|---------|------|
| Industry-standard password hashing | BCrypt is CPU-intensive by design (intentional for security) |
| Rich security extension points | Current configuration does not enforce authentication on endpoints |
| `AuthenticationManager` is available for future login refactoring | `AuthServiceImpl` validates credentials manually instead of using `AuthenticationManager.authenticate()` today |

---

## 6. Layered Architecture

### What was chosen
- **Monolithic layered structure:** Controller → Service → Repository → Database (simplified summary; see [architecture.md](architecture.md) for the full request path including DTOs, security, mappers, and exception handling)
- Packages: `controller`, `service` / `service.impl`, `repository`, `entity`, `dto`, `mapper`, `security`, `config`, `exception`

### Why it was chosen
- Documented in [architecture.md](architecture.md) and [requirements.md](requirements.md) (*Layered Architecture*)
- Introduced incrementally in Git: entities and repositories (`8f64365`), DTOs and mappers (`5db1bdb`), then controllers
- Matches the learning and portfolio goals stated in the README

### Alternatives considered
- **Not explicitly documented**
- **Typical alternatives:** hexagonal (ports and adapters), clean architecture with use-case classes, or microservices split by domain

### Tradeoffs
| Benefit | Cost |
|---------|------|
| Simple to navigate for a small codebase | Can become package-heavy as domains grow (auth, booking, payments) |
| Clear separation of HTTP, business, and persistence concerns | Risk of anemic services or fat services without discipline |
| Appropriate for current monolith scope | Requires future package restructuring if many new domains are added |

---

## 7. DTO Pattern

### What was chosen
- Separate **request and response DTOs** under `com.harshit.moviebooking.dto`
- Controllers accept and return DTOs, not JPA entities
- Domain-grouped subpackages: `auth`, `movie`, `rating` (plus empty `user` and `role` placeholders)

### Why it was chosen
- Commit `5db1bdb`: *Add DTOs and manual mappers*
- Commit `f976adf`: *Add DTO layer, mapper layer and service architecture*
- Prevents exposing persistence model fields and JPA relationships directly in the API

### Alternatives considered
- **Not explicitly documented**
- **Typical alternatives:** returning entities directly (with `@JsonIgnore`), OpenAPI-generated models, or record-based API types only

### Tradeoffs
| Benefit | Cost |
|---------|------|
| Stable API contract independent of entity changes | Duplication between DTOs and entities |
| Validation annotations can live on request DTOs | Empty placeholder DTOs (`UserRequestDto`, `RoleRequestDto`) add noise until implemented |
| Clear boundary for serialization | More classes to maintain per endpoint |

---

## 8. Manual Mapping

### What was chosen
- **Static mapper utility classes:** `MovieMapper`, `RatingMapper`
- Manual field copying in `AuthServiceImpl` (register) and `MovieServiceImpl` (update)

### Why it was chosen
- Evidenced by commit `5db1bdb`: *Add DTOs and **manual mappers***
- No MapStruct, ModelMapper, or similar dependency appears in `pom.xml`
- Keeps mapping explicit and visible for a small number of types

### Alternatives considered
- **Implied by absence from `pom.xml`:** MapStruct and model-mapping libraries were not adopted
- **Typical alternatives:** MapStruct (compile-time), ModelMapper (runtime), or JPA DTO projections in repositories

### Tradeoffs
| Benefit | Cost |
|---------|------|
| No extra dependencies or annotation processing setup | Repetitive code; update paths can drift (movie update maps fields manually, create uses `MovieMapper`) |
| Easy to read for learners | Does not scale as cleanly past many entities |
| Full control over mapped fields | Easy to miss fields when entities evolve |

---

## 9. Jakarta Bean Validation

### What was chosen
- **`spring-boot-starter-validation`** dependency
- Validation annotations on request DTOs (`@NotBlank`, `@Email`, `@Size`, `@NotNull`, `@Min`, `@Max`)
- `@Valid` on controller methods (register, login, create movie, rate movie — **not** on movie update)

### Why it was chosen
- Listed in [requirements.md](requirements.md) non-functional requirements: *Validation*
- Added in commit `ce05198`: *Implement Validation*
- Validates input at the HTTP boundary before service logic runs

### Alternatives considered
- **Not explicitly documented**
- **Typical alternatives:** manual validation in services, custom validator classes only, or JSON Schema validation

### Tradeoffs
| Benefit | Cost |
|---------|------|
| Declarative, readable constraint definitions | Inconsistent application (`updateMovie` lacks `@Valid`) |
| Integrates with Spring MVC and `GlobalExceptionHandler` | Does not replace all business-rule validation (e.g. duplicate email checked in service) |
| Standard Jakarta API | Some rules (e.g. `dateOfBirth` required at DB level) are not fully mirrored on DTOs |

---

## 10. Global Exception Handling

### What was chosen
- `@RestControllerAdvice` class: `GlobalExceptionHandler`
- Shared error envelope: `ApiErrorResponse`
- Domain exception: `MovieNotFoundException`
- Handled cases: `MethodArgumentNotValidException` (400), `MovieNotFoundException` (404)

### Why it was chosen
- Added in commit `03749ee`: *Add request validation and global exception handling*
- Listed in [requirements.md](requirements.md): *Exception Handling* (partially implemented)
- Centralizes API error shape for validation and movie-not-found paths

### Alternatives considered
- **Not explicitly documented**
- **Typical alternatives:** per-controller try/catch, Spring Boot default error whitelabel JSON only, or RFC 7807 `ProblemDetail` (Spring Framework 6+)

### Tradeoffs
| Benefit | Cost |
|---------|------|
| Consistent error format for handled exceptions | Auth and other `RuntimeException` paths still use Spring defaults (typically 500) |
| Keeps controllers thin | Incomplete coverage across all failure types |
| Easy to extend with new `@ExceptionHandler` methods | Two error response styles coexist today |

---

## 11. Docker and Docker Compose

### What was chosen
- **Multi-stage Dockerfile:** Maven build stage + JRE runtime stage (`eclipse-temurin:21-jre`)
- **Docker Compose** with `postgres` and `app` services
- `.dockerignore` to limit build context
- Documented in [deployment.md](deployment.md) and [changelog.md](changelog.md)

### Why it was chosen
- Listed in [requirements.md](requirements.md): *Docker*
- Added in commit `1d04639`: *Add project documentation and deployment files*
- Supports reproducible builds and local full-stack startup (API + database)

### Alternatives considered
- **Not explicitly documented**
- **Typical alternatives:** running JAR directly on host, Kubernetes, Podman, or cloud PaaS without containers

### Tradeoffs
| Benefit | Cost |
|---------|------|
| Consistent runtime environment | Image build time and Docker dependency for contributors |
| Compose simplifies PostgreSQL co-deployment | `depends_on` does not wait for database readiness by default |
| Multi-stage build keeps runtime image smaller | Dockerfile runs `mvn package -DskipTests` (tests skipped in image build) |

---

## 12. Normalized Schema with Selective Denormalization

### What was chosen
- **Third Normal Form (3NF)** as a design goal ([database-design.md](database-design.md))
- Separate **`roles`** table instead of embedding role names in `users`
- Separate **`ratings`** table (user–movie relationship with a score)
- **Denormalized** `average_rating` and `rating_count` on `movies`
- **`account_status` enum** instead of a boolean `is_active`
- **BCrypt `password_hash`** instead of plain-text passwords

### Why it was chosen
Documented explicitly in [database-design.md](database-design.md):

| Decision | Rationale (from design docs) |
|----------|------------------------------|
| Separate `roles` table | Normalization; avoid repeating role values; support new roles without schema changes |
| Cached rating aggregates on `movies` | Read-heavy workload; movies viewed more often than ratings are submitted |
| `account_status` vs boolean | Support `ACTIVE`, `SUSPENDED`, `BANNED`, `DELETED` |
| Separate `ratings` table | Ratings belong to the user–movie relationship, not the movie row alone |
| Runtime in minutes | Easier calculations and filtering |
| `release_date` not year only | More precise queries and sorting |

### Alternatives considered
| Area | Documented alternative |
|------|------------------------|
| Roles | Store role name directly on `users` — rejected for normalization |
| Movie ratings display | Calculate average on every read — rejected for read performance |
| Account state | Simple `is_active` boolean — rejected for multiple states |
| Genres | Store genre string on `movies` — rejected; separate `genres` / `movie_genres` planned |
| Actors/directors | Include in v1 — rejected to keep scope focused |

### Tradeoffs
| Benefit | Cost |
|---------|------|
| Integrity and normalization for core entities | Denormalized aggregates must be kept in sync on writes |
| Faster movie reads via cached averages | Current implementation recalculates from all ratings in memory on each write |
| Flexible role and account models | More tables and joins than a minimal schema |

---

## 13. Maven and Project Layout

### What was chosen
- **Maven** as the build tool (with Maven Wrapper)
- Application nested at **`backend/MovieBooking/`**
- Documentation at repository root in **`docs/`**

### Why it was chosen
- Maven Wrapper committed in `8fa97a4`
- [setup.md](setup.md) and README document `./mvnw` usage
- Layout separates docs from runnable application code

### Alternatives considered
- **Not explicitly documented**
- **Typical alternatives:** Gradle, single-module root layout, or multi-module Maven parent POM

### Tradeoffs
| Benefit | Cost |
|---------|------|
| Wrapper ensures consistent Maven version | Extra directory nesting for a single app |
| Familiar Java enterprise build tool | Slightly longer paths for setup commands |

---

## 14. Lombok

### What was chosen
- **Lombok** for getters, setters, and constructors on entities and DTOs
- Lombok excluded from the Spring Boot repackaged JAR
- Annotation processor configured in `maven-compiler-plugin`

### Why it was chosen
- Present from initial project setup (`8fa97a4`)
- Reduces boilerplate on JPA entities and DTOs

### Alternatives considered
- **Not explicitly documented**
- **Typical alternatives:** Java records (DTOs), explicit getters/setters, or Immutables

### Tradeoffs
| Benefit | Cost |
|---------|------|
| Less boilerplate code | IDE and tooling must support Lombok |
| Faster entity/DTO authoring | Can hide important entity behavior (e.g. `equals`/`hashCode` not defined) |

---

## 15. Externalized Configuration

### What was chosen
- Database credentials via **environment variables** (`DB_URL`, `DB_USERNAME`, `DB_PASSWORD`)
- JWT settings via `jwt.secret` and `jwt.expiration` (overridable via env in Docker Compose)
- `.env` listed in `.gitignore` (commit `b590497`)

### Why it was chosen
- Changed from hardcoded JDBC values in early `application.properties` to placeholders during deployment preparation (`b590497`, `1d04639`)
- Docker Compose injects environment variables for the `app` service
- `.env.example` documents local variable names

### Alternatives considered
- **Not explicitly documented**
- **Typical alternatives:** hardcoded properties per profile only, Spring Cloud Config, or secrets managers (Vault, AWS Secrets Manager)

### Tradeoffs
| Benefit | Cost |
|---------|------|
| Safer credential handling for deployment | Requires env setup for every environment |
| Aligns with 12-factor configuration practices | Default `jwt.secret` still exists in committed `application.properties` |

---

## Decisions Not Made (evidenced by absence)

The following are **planned or discussed** in requirements/changelog but **not implemented** in the current codebase:

| Topic | Evidence |
|-------|----------|
| Flyway / Liquibase migrations | No dependency in `pom.xml`; `ddl-auto=update` used instead |
| MapStruct / ModelMapper | No dependency; manual mappers used |
| Swagger / OpenAPI | Listed as planned in [changelog.md](changelog.md) |
| Refresh tokens | Listed as planned |
| OAuth 2.0 / social login | Not referenced in requirements or code |
| Microservices | Monolith structure only |
| Redis / caching | Not referenced |
| Enforced endpoint authorization | JWT issued but `permitAll()` in `SecurityConfig` |

---

## Related Documentation

- [architecture.md](architecture.md) — request flow and package layout
- [database-design.md](database-design.md) — schema and normalization decisions
- [requirements.md](requirements.md) — target non-functional requirements
- [changelog.md](changelog.md) — implementation timeline
