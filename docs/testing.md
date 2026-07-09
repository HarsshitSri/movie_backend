# Testing

Testing guide for the current implementation.

---

## Automated Tests

The project includes a Spring Boot context load test.

From `backend/MovieBooking/`:

```bash
./mvnw test
```

Tests run against an in-memory H2 database configured in:

```
backend/MovieBooking/src/test/resources/application.properties
```

There is no broad unit or integration test suite yet.

---

## Manual API Testing

Recommended tool: Postman

Base URL: `http://localhost:8080`

---

## Authentication

| Scenario | Endpoint |
|----------|----------|
| Register | `POST /api/auth/register` |
| Login | `POST /api/auth/login` |

Verify that login returns a JWT in the response body.

---

## Movies

Verify the following against the movie endpoints:

- Create (`POST /api/movies`)
- Read by ID (`GET /api/movies/{id}`)
- Update (`PUT /api/movies/{id}`)
- Delete (`DELETE /api/movies/{id}`)
- Pagination (`GET /api/movies?page=0&size=10`)
- Sorting (`GET /api/movies?sort=title&direction=asc`)

---

## Ratings

| Scenario | Endpoint |
|----------|----------|
| Create or update rating | `POST /api/movies/{movieId}/ratings` |

Verify that submitting a rating updates the movie's `average_rating` and `rating_count`.

---

## Security

JWT components exist (`JwtService`, `JwtAuthenticationFilter`), but endpoint authorization is **not enforced** yet (`permitAll()` in `SecurityConfig`).

The following scenarios are **planned** for when authorization is enabled:

- Invalid JWT
- Missing JWT on protected endpoints
- Expired JWT
- Unauthorized access to protected endpoints

---

## Related Documentation

- [api-design.md](api-design.md) — endpoint reference
- [setup.md](setup.md) — run the application locally
