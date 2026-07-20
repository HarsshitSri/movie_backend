# Movie Backend Requirements

Product requirements for the Movie Booking Backend project.

> **Note:** This document describes target requirements. For what is implemented today, see [README.md](../README.md) and the **Implementation Status** section below.

---

## Implementation Status (Current Codebase)

| Requirement Area | Status |
|------------------|--------|
| Register / Login | Implemented |
| JWT issuance | Implemented |
| Endpoint authorization | JWT required for writes; role rules planned |
| Movie browse / view / CRUD | Implemented (writes require JWT) |
| Movie pagination / sorting | Implemented |
| Movie ratings (1–10) | Implemented |
| Movie title search API | Not exposed |
| Genre filter / recommendations | Planned |
| Reviews | Planned |
| Watchlist / Favorites | Planned |
| User profile management | Planned |
| Logout / refresh tokens | Planned |
| Booking / theatres / payments | Planned |
| Swagger / OpenAPI | Planned |
| Broad automated test suite | Planned (context load test only) |

---

## 1. Project Overview

### Project Name

Movie Booking Backend

### Objective

Build a RESTful backend for a movie platform where users can discover movies, manage personal collections, interact with reviews, and receive personalized recommendations.

The project demonstrates backend engineering practices including authentication, authorization, database design, REST API design, validation, testing, Dockerization, and deployment readiness.

---

## 2. Stakeholders

- Guest User
- Registered User
- Administrator

---

## 3. User Roles

### Guest

Can use the application without logging in.

### Registered User

Has an account and can access personalized features.

### Administrator

Has complete control over platform content.

**Current implementation:** `USER` and `ADMIN` roles exist in the database. Endpoint-level role enforcement is not active yet.

---

## 4. Functional Requirements

### 4.1 Authentication

The system shall allow users to:

| Capability | Status |
|------------|--------|
| Register | Implemented |
| Login | Implemented |
| Logout | Planned |
| Refresh access tokens | Planned |
| Change password | Planned |
| Reset forgotten password | Planned (future enhancement) |

---

### 4.2 User Profile

Users shall be able to:

- View profile — **Planned**
- Update profile — **Planned**
- Upload profile picture — **Planned (future)**
- Delete account — **Planned**

---

### 4.3 Movie Management

Users shall be able to:

| Capability | Status |
|------------|--------|
| Browse movies | Implemented |
| View movie details | Implemented |
| Search by title | Planned (service method exists; API not exposed) |
| Filter by genre | Planned |
| Filter by language | Planned |
| Filter by release year | Planned |
| Filter by rating | Planned |
| Sort by popularity | Planned |
| Sort by release date | Implemented (via list sorting) |
| Sort alphabetically | Implemented (via list sorting) |

Administrators shall be able to:

| Capability | Status |
|------------|--------|
| Add movies | Implemented (no admin-only restriction yet) |
| Update movies | Implemented (no admin-only restriction yet) |
| Delete movies | Implemented (no admin-only restriction yet) |

---

### 4.4 Reviews

Users shall be able to:

- Create review — **Planned**
- Edit own review — **Planned**
- Delete own review — **Planned**

Guests:

- View reviews only — **Planned**

Admins:

- Delete inappropriate reviews — **Planned**

---

### 4.5 Ratings

Users shall be able to:

| Capability | Status |
|------------|--------|
| Give rating | Implemented |
| Update rating | Implemented |
| Remove rating | Planned |

Movie rating aggregates (`average_rating`, `rating_count`) shall update automatically — **Implemented**

---

### 4.6 Watchlist

Users shall be able to:

- Add movie — **Planned**
- Remove movie — **Planned**
- View watchlist — **Planned**

---

### 4.7 Favorites

Users shall be able to:

- Add favorite — **Planned**
- Remove favorite — **Planned**
- View favorites — **Planned**

---

### 4.8 Recommendation System (Basic)

Recommend movies based on:

- Genres — **Planned**
- Highest ratings — **Planned**
- User watch history — **Planned**

---

## 5. Non-Functional Requirements

| Requirement | Status |
|-------------|--------|
| RESTful API | Implemented |
| Layered architecture | Implemented |
| PostgreSQL | Implemented |
| Spring Boot | Implemented |
| Spring Security | Implemented (partial authorization) |
| JWT authentication | Implemented (issuance; enforcement planned) |
| Docker | Implemented |
| Validation | Implemented |
| Logging | Partial (SLF4J not used consistently in services) |
| Exception handling | Partial (`GlobalExceptionHandler` exists) |
| Pagination | Implemented |
| Sorting | Implemented |
| Unit tests | Planned (context load test only) |
| Integration tests | Planned |
| API documentation (Swagger/OpenAPI) | Planned |

---

## 6. Business Rules

| Rule | Status |
|------|--------|
| Email must be unique | Implemented |
| Username must be unique | Implemented |
| One user can rate a movie only once | Implemented |
| One user can review a movie only once | Planned |
| Ratings must be between 1 and 10 | Implemented |
| Guests cannot create reviews | Planned |
| Guests cannot modify data | Implemented for movie writes / ratings (JWT required); guests can still GET movies |
| Only admins may delete movies | Planned (not enforced yet — any authenticated user can delete) |
| Deleted users should not remove movie data | Planned |

---

## 7. Success Criteria

The project target is complete when:

- All planned endpoints function correctly
- Authentication and authorization work securely
- Database is normalized
- Docker deployment succeeds
- Tests pass with meaningful coverage
- API documentation is complete

**Current state:** Core auth, movie, and rating flows are implemented. Booking, reviews, profile features, enforced security, and full test/documentation coverage remain planned.

---

## Related Documentation

- [api-design.md](api-design.md) — implemented endpoints
- [database-design.md](database-design.md) — schema design
- [changelog.md](changelog.md) — version history
