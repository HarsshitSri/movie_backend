# Changelog

## Version 1.0 (In Progress)

### Implemented

- Spring Boot 3.5 project initialized (`backend/MovieBooking/`)
- PostgreSQL integration with environment-based datasource configuration
- User registration and login
- JWT token generation and filter on the security chain
- JWT required for movie writes and ratings (`GET` movies remain public)
- BCrypt password encryption
- Role seeding on startup (`USER`, `ADMIN`)
- Movie CRUD
- Movie pagination and sorting
- Movie ratings (`POST /api/movies/{movieId}/ratings`)
- Request validation (Jakarta Bean Validation)
- Global exception handling for validation errors and movie-not-found
- Docker and Docker Compose support
- Context load test + API flow test (H2)

### Partial

- Role storage exists (`USER`, `ADMIN`), but role-based endpoint rules are not active (any authenticated user can write)
- Movie title search exists in `MovieService` only (no API endpoint)

---

## Planned

- Role-based access control (`ADMIN` vs `USER`)
- Swagger / OpenAPI
- Broader exception handling coverage
- Booking module
- Theatre module
- Payment integration
- Refresh tokens
- Expanded unit and integration tests
- CI/CD
