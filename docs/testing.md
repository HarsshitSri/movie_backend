# Testing

Testing guide for the current implementation.

---

## Automated tests

From `backend/MovieBooking/`:

```bash
./mvnw test
```

| Test | What it covers |
|------|----------------|
| `MovieBookingApplicationTests` | Context loads (H2) |
| `AuthAndMovieApiTest` | Register → login → create movie (JWT) → public GET list |

Tests use H2 via `src/test/resources/application.properties`. Roles are seeded by `RoleDataInitializer`.

---

## Manual API testing

**Base URL:** `http://localhost:8080`  
**Ready-made requests:** [quickstart.http](quickstart.http)

### Ordered smoke flow

| Step | Request | Auth | Expect |
|------|---------|------|--------|
| 1 | `POST /api/auth/register` | No | `201` + token |
| 2 | `POST /api/auth/login` | No | `200` + token |
| 3 | `POST /api/movies` without token | — | `401` |
| 4 | `POST /api/movies` with Bearer token | Yes | `201` |
| 5 | `GET /api/movies` | No | `200` |
| 6 | `POST /api/movies/{id}/ratings` with token | Yes | `201` |
| 7 | `GET /api/movies/{id}` | No | Updated averages |

Full payloads: [api-design.md](api-design.md)

---

## Security note

- Public: auth endpoints + `GET` movies
- Authenticated: movie writes + ratings
- Role-based rules (`ADMIN`-only deletes, etc.) are **not** implemented yet

---

## Related documentation

- [api-design.md](api-design.md) — endpoint reference
- [setup.md](setup.md) — run locally
- [README.md](../README.md) — quick start
