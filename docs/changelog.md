# Changelog

## Version 1.0 (In Progress)

### Implemented

- Spring Boot 3.5 project initialized (`backend/MovieBooking/`)
- PostgreSQL integration with environment-based datasource configuration
- User registration and login
- JWT token generation
- BCrypt password encryption
- Movie CRUD
- Movie pagination and sorting
- Movie ratings (`POST /api/movies/{movieId}/ratings`)
- Request validation (Jakarta Bean Validation)
- Global exception handling for validation errors and movie-not-found
- Docker and Docker Compose support
- Basic Spring Boot context load test (H2 for tests)

### Partial

- JWT authentication components exist, but endpoint authorization is not enforced (`permitAll()`)
- Role storage exists (`USER`, `ADMIN`), but role-based endpoint access is not active
- Movie title search exists in `MovieService` only (no API endpoint)

---

## Planned

- Enforced JWT authorization and role-based access control
- Swagger / OpenAPI
- Broader exception handling coverage
- Booking module
- Theatre module
- Payment integration
- Refresh tokens
- Expanded unit and integration tests
- CI/CD
