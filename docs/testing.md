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
| `AuthAndMovieApiTest` | Register USER → 403 on create movie → ADMIN login → create movie → public GET |

Roles and demo admin are seeded by `RoleDataInitializer`.

---

## Manual API testing

**Base URL:** `http://localhost:8080`  
**Ready-made requests:** [quickstart.http](quickstart.http)

**Seeded admin:** `admin@movieplatform.local` / `Admin@12345`

### Ordered smoke flow

| Step | Request | Auth | Expect |
|------|---------|------|--------|
| 1 | `POST /api/auth/register` | No | `201` + token, `role: USER` |
| 2 | `POST /api/movies` with USER token | USER | `403` |
| 3 | `POST /api/auth/login` as admin | No | `200` + `role: ADMIN` |
| 4 | `POST /api/movies` with ADMIN token | ADMIN | `201` |
| 5 | `GET /api/movies` | No | `200` |
| 6 | `POST .../ratings` with USER token | USER | `201` |

---

## Security note

- Public: auth endpoints + `GET` movies
- Authenticated: ratings
- Admin only: movie create / update / delete

---

## Related documentation

- [api-design.md](api-design.md) — endpoint reference
- [setup.md](setup.md) — run locally
- [README.md](../README.md) — quick start
