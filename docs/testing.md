# Testing

Testing guide for the current implementation.

---

## Automated tests

From `backend/MovieBooking/`:

```bash
./mvnw test
```

Tests use H2 via `src/test/resources/application.properties`.

There is no broad unit or integration suite yet (context load test only).

---

## Manual API testing

**Tool:** curl, Postman, or any HTTP client  
**Base URL:** `http://localhost:8080`

**Prerequisites**

1. App running ([setup.md](setup.md) or Docker)
2. Roles seeded (`USER`, `ADMIN`) — see [setup.md](setup.md)

### Ordered smoke flow

| Step | Request | Expect |
|------|---------|--------|
| 1 | `POST /api/auth/register` | `201` + `{ "token": "..." }` |
| 2 | `POST /api/auth/login` | `200` + JWT |
| 3 | `POST /api/movies` | `201` + movie with `id` |
| 4 | `GET /api/movies?page=0&size=10` | `200` + `Page` JSON |
| 5 | `POST /api/movies/{id}/ratings` with `userId` + `rating` | `201` |
| 6 | `GET /api/movies/{id}` | Updated `averageRating` / `ratingCount` |

Example register:

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

Full payloads and status codes: [api-design.md](api-design.md)

---

## Security note

JWT components exist, but endpoint authorization is **not enforced** (`permitAll()`). Token-required / 401 scenarios are planned for when the filter is wired into the security chain.

---

## Related documentation

- [api-design.md](api-design.md) — endpoint reference
- [setup.md](setup.md) — run locally
- [README.md](../README.md) — quick start
