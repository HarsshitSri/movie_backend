# Changelog

## Version 1.0 (In Progress)

### Implemented

- Spring Boot 3.5 project initialized (`backend/MovieBooking/`)
- PostgreSQL integration with environment-based datasource configuration
- User registration and login
- JWT token generation and filter on the security chain
- Role-based access: movie create/update/delete require `ADMIN`; ratings require authentication
- Seeded roles + demo admin user (`admin@movieplatform.local`)
- Movie CRUD
- Movie pagination and sorting
- Movie ratings (`POST /api/movies/{movieId}/ratings`)
- Request validation (Jakarta Bean Validation)
- Global exception handling for validation errors and movie-not-found
- Docker and Docker Compose support
- Basic HTML/CSS/JS UI
- Context load test + API flow test (H2)

### Partial

- Movie title search exists in `MovieService` only (no API endpoint)

---

## Planned

- Swagger / OpenAPI
- Broader exception handling coverage
- Booking module
- Theatre module
- Payment integration
- Refresh tokens
- Expanded unit and integration tests
- CI/CD
