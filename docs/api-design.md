# API Design

REST API reference synchronized with the current controller implementation.

**Base URL:** `http://localhost:8080`

**Content-Type:** `application/json` (request and response bodies)

**Controllers:** `AuthController`, `MovieController`, `RatingController`

---

## Authentication

JWT filter is registered on the security filter chain (stateless sessions).

| Rule | Endpoints |
|------|-----------|
| Public | `POST /api/auth/**`, `GET /api/movies`, `GET /api/movies/{id}` |
| Authenticated | `POST /api/movies/{movieId}/ratings` |
| `ROLE_ADMIN` | `POST` / `PUT` / `DELETE` `/api/movies` |

Missing or invalid token → **`401 Unauthorized`**.  
Authenticated but not admin on admin routes → **`403 Forbidden`**.

Roles are seeded on startup. A demo admin is created if missing:

- Email: `admin@movieplatform.local`
- Password: `Admin@12345`

`AuthResponse` fields: `token`, `userId`, `role` (`USER` or `ADMIN`).

Ready-made requests: [quickstart.http](quickstart.http)

---

## Common Error Response (`ApiErrorResponse`)

Returned by `GlobalExceptionHandler` for validation failures and movie-not-found errors:

```json
{
  "timestamp": "2026-07-09T22:00:00",
  "status": 400,
  "error": "Validation Failed",
  "errors": {
    "email": "must be a well-formed email address"
  }
}
```

| Field | Type | Description |
|-------|------|-------------|
| `timestamp` | `string` (ISO-8601) | Error time |
| `status` | `integer` | HTTP status code |
| `error` | `string` | Error summary |
| `errors` | `object` | Field or message details |

Unhandled `RuntimeException` values (e.g. auth conflicts) are **not** mapped to `ApiErrorResponse`. Spring Boot returns its default error JSON with HTTP `500`.

---

## Endpoints

### 1. Register

| | |
|---|---|
| **URL** | `/api/auth/register` |
| **Method** | `POST` |
| **Purpose** | Create a new user account and return a JWT |
| **Authentication required** | No |

#### Request Body (`RegisterRequest`)

| Field | Type | Required | Validation |
|-------|------|----------|------------|
| `username` | `string` | Yes | Not blank, max 30 characters |
| `email` | `string` | Yes | Not blank, valid email |
| `password` | `string` | Yes | Not blank, min 8 characters |
| `firstName` | `string` | Yes | Not blank |
| `lastName` | `string` | Yes | Not blank |
| `dateOfBirth` | `string` (date) | No | ISO-8601 date (e.g. `2003-07-25`) |

```json
{
  "username": "harshit",
  "email": "harshit@example.com",
  "password": "Password@123",
  "firstName": "Harshit",
  "lastName": "Srivastava",
  "dateOfBirth": "2003-07-25"
}
```

#### Response Body (`AuthResponse`)

| Field | Type | Description |
|-------|------|-------------|
| `token` | `string` | JWT access token |

```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9..."
}
```

#### Status Codes

| Code | When |
|------|------|
| `201 Created` | User registered successfully |
| `400 Bad Request` | Validation failed |
| `500 Internal Server Error` | Email already exists, username already exists, or default role not found |

#### Possible Errors

| Condition | Status | Response |
|-----------|--------|----------|
| Invalid or missing fields | `400` | `ApiErrorResponse` with field errors |
| Duplicate email | `500` | Spring default error (`"Email already exists"`) |
| Duplicate username | `500` | Spring default error (`"Username already exists"`) |
| `USER` role missing in database | `500` | Spring default error (`"Default role not found"`) |

---

### 2. Login

| | |
|---|---|
| **URL** | `/api/auth/login` |
| **Method** | `POST` |
| **Purpose** | Authenticate a user and return a JWT |
| **Authentication required** | No |

#### Request Body (`LoginRequest`)

| Field | Type | Required | Validation |
|-------|------|----------|------------|
| `email` | `string` | Yes | Not blank, valid email |
| `password` | `string` | Yes | Not blank |

```json
{
  "email": "harshit@example.com",
  "password": "Password@123"
}
```

#### Response Body (`AuthResponse`)

```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9..."
}
```

#### Status Codes

| Code | When |
|------|------|
| `200 OK` | Login successful |
| `400 Bad Request` | Validation failed |
| `500 Internal Server Error` | Invalid email or password |

#### Possible Errors

| Condition | Status | Response |
|-----------|--------|----------|
| Invalid or missing fields | `400` | `ApiErrorResponse` with field errors |
| Unknown email or wrong password | `500` | Spring default error (`"Invalid email or password"`) |

---

### 3. List Movies

| | |
|---|---|
| **URL** | `/api/movies` |
| **Method** | `GET` |
| **Purpose** | Return a paginated, sortable list of movies |
| **Authentication required** | No |

#### Query Parameters

| Parameter | Type | Default | Description |
|-----------|------|---------|-------------|
| `page` | `integer` | `0` | Page index (zero-based) |
| `size` | `integer` | `10` | Number of items per page |
| `sort` | `string` | `id` | Entity field to sort by |
| `direction` | `string` | `asc` | `asc` or `desc` |

#### Request Body

None

#### Response Body (`Page<MovieResponseDto>`)

Spring Data `Page` JSON structure:

```json
{
  "content": [
    {
      "id": 1,
      "title": "Interstellar",
      "synopsis": "A team travels through a wormhole in space.",
      "releaseDate": "2014-11-07",
      "runtimeMinutes": 169,
      "language": "English",
      "countryOfOrigin": "USA",
      "contentRating": "PG_13",
      "posterUrl": "https://example.com/poster.jpg",
      "averageRating": 8.70,
      "ratingCount": 1543
    }
  ],
  "pageable": { "pageNumber": 0, "pageSize": 10 },
  "totalElements": 1,
  "totalPages": 1,
  "size": 10,
  "number": 0,
  "first": true,
  "last": true,
  "empty": false
}
```

#### `MovieResponseDto` fields

| Field | Type |
|-------|------|
| `id` | `long` |
| `title` | `string` |
| `synopsis` | `string` |
| `releaseDate` | `string` (date) |
| `runtimeMinutes` | `integer` |
| `language` | `string` |
| `countryOfOrigin` | `string` |
| `contentRating` | `string` enum: `G`, `PG`, `PG_13`, `R`, `NC_17`, `ADULT_18` |
| `posterUrl` | `string` |
| `averageRating` | `number` |
| `ratingCount` | `integer` |

#### Status Codes

| Code | When |
|------|------|
| `200 OK` | Movies retrieved successfully |
| `500 Internal Server Error` | Invalid `direction` value (not `asc` or `desc`) |

#### Possible Errors

| Condition | Status | Response |
|-----------|--------|----------|
| Invalid sort direction | `500` | Spring default error |

---

### 4. Get Movie by ID

| | |
|---|---|
| **URL** | `/api/movies/{id}` |
| **Method** | `GET` |
| **Purpose** | Retrieve a single movie by its ID |
| **Authentication required** | No |

#### Path Parameters

| Parameter | Type | Description |
|-----------|------|-------------|
| `id` | `long` | Movie ID |

#### Request Body

None

#### Response Body (`MovieResponseDto`)

```json
{
  "id": 1,
  "title": "Interstellar",
  "synopsis": "A team travels through a wormhole in space.",
  "releaseDate": "2014-11-07",
  "runtimeMinutes": 169,
  "language": "English",
  "countryOfOrigin": "USA",
  "contentRating": "PG_13",
  "posterUrl": "https://example.com/poster.jpg",
  "averageRating": 8.70,
  "ratingCount": 1543
}
```

#### Status Codes

| Code | When |
|------|------|
| `200 OK` | Movie found |
| `404 Not Found` | Movie does not exist |

#### Possible Errors

| Condition | Status | Response |
|-----------|--------|----------|
| Movie not found | `404` | `ApiErrorResponse` — `"Movie Not Found"` |

---

### 5. Create Movie

| | |
|---|---|
| **URL** | `/api/movies` |
| **Method** | `POST` |
| **Purpose** | Create a new movie |
| **Authentication required** | Yes — `ADMIN` + `Authorization: Bearer <token>` |

#### Request Body (`MovieRequestDto`)

| Field | Type | Required | Validation |
|-------|------|----------|------------|
| `title` | `string` | Yes | Not blank, max 255 characters |
| `synopsis` | `string` | Yes | Not blank, max 5000 characters |
| `releaseDate` | `string` (date) | Yes | Not null |
| `runtimeMinutes` | `integer` | Yes | Not null, min 1 |
| `language` | `string` | Yes | Not blank, max 50 characters |
| `countryOfOrigin` | `string` | Yes | Not blank, max 100 characters |
| `contentRating` | `string` | Yes | One of: `G`, `PG`, `PG_13`, `R`, `NC_17`, `ADULT_18` |
| `posterUrl` | `string` | No | Max 500 characters |

```json
{
  "title": "Interstellar",
  "synopsis": "A team travels through a wormhole in space.",
  "releaseDate": "2014-11-07",
  "runtimeMinutes": 169,
  "language": "English",
  "countryOfOrigin": "USA",
  "contentRating": "PG_13",
  "posterUrl": "https://example.com/poster.jpg"
}
```

#### Response Body (`MovieResponseDto`)

Returns the created movie. `id`, `averageRating` (`0`), and `ratingCount` (`0`) are set by the server.

#### Status Codes

| Code | When |
|------|------|
| `201 Created` | Movie created successfully |
| `400 Bad Request` | Validation failed |
| `401 Unauthorized` | Missing or invalid JWT |

#### Possible Errors

| Condition | Status | Response |
|-----------|--------|----------|
| Missing / invalid token | `401` | Empty body (Spring Security) |
| Invalid or missing fields | `400` | `ApiErrorResponse` with field errors |
| Invalid `contentRating` value | `400` | `ApiErrorResponse` or Spring deserialization error |

---

### 6. Update Movie

| | |
|---|---|
| **URL** | `/api/movies/{id}` |
| **Method** | `PUT` |
| **Purpose** | Update an existing movie |
| **Authentication required** | Yes — `ADMIN` + `Authorization: Bearer <token>` |

#### Path Parameters

| Parameter | Type | Description |
|-----------|------|-------------|
| `id` | `long` | Movie ID |

#### Request Body (`MovieRequestDto`)

Same fields as [Create Movie](#5-create-movie).

> **Note:** This endpoint does not use `@Valid`. Validation annotations on `MovieRequestDto` are not enforced by the controller.

#### Response Body (`MovieResponseDto`)

Returns the updated movie. `averageRating` and `ratingCount` are not modified by this endpoint.

#### Status Codes

| Code | When |
|------|------|
| `200 OK` | Movie updated successfully |
| `401 Unauthorized` | Missing or invalid JWT |
| `404 Not Found` | Movie does not exist |

#### Possible Errors

| Condition | Status | Response |
|-----------|--------|----------|
| Missing / invalid token | `401` | Empty body (Spring Security) |
| Movie not found | `404` | `ApiErrorResponse` — `"Movie Not Found"` |
| Invalid JSON or `contentRating` | `400` | Spring deserialization error |

---

### 7. Delete Movie

| | |
|---|---|
| **URL** | `/api/movies/{id}` |
| **Method** | `DELETE` |
| **Purpose** | Delete a movie by ID |
| **Authentication required** | Yes — `ADMIN` + `Authorization: Bearer <token>` |

#### Path Parameters

| Parameter | Type | Description |
|-----------|------|-------------|
| `id` | `long` | Movie ID |

#### Request Body

None

#### Response Body

None (empty body)

#### Status Codes

| Code | When |
|------|------|
| `204 No Content` | Movie deleted successfully |
| `401 Unauthorized` | Missing or invalid JWT |
| `404 Not Found` | Movie does not exist |

#### Possible Errors

| Condition | Status | Response |
|-----------|--------|----------|
| Missing / invalid token | `401` | Empty body (Spring Security) |
| Movie not found | `404` | `ApiErrorResponse` — `"Movie Not Found"` |

---

### 8. Rate Movie

| | |
|---|---|
| **URL** | `/api/movies/{movieId}/ratings` |
| **Method** | `POST` |
| **Purpose** | Create or update a user's rating for a movie and recalculate movie aggregates |
| **Authentication required** | Yes — any logged-in user (`Authorization: Bearer <token>`) |

#### Path Parameters

| Parameter | Type | Description |
|-----------|------|-------------|
| `movieId` | `long` | Movie ID |

#### Request Body (`RatingRequestDto`)

| Field | Type | Required | Validation |
|-------|------|----------|------------|
| `userId` | `long` | Yes | Not null |
| `rating` | `integer` | Yes | Not null, min 1, max 10 |

```json
{
  "userId": 1,
  "rating": 9
}
```

#### Response Body (`RatingResponseDto`)

| Field | Type | Description |
|-------|------|-------------|
| `id` | `long` | Rating ID |
| `userId` | `long` | User ID |
| `movieId` | `long` | Movie ID |
| `rating` | `integer` | Rating value (1–10) |
| `createdAt` | `string` (datetime) | Created timestamp |
| `updatedAt` | `string` (datetime) | Last updated timestamp |

```json
{
  "id": 1,
  "userId": 1,
  "movieId": 1,
  "rating": 9,
  "createdAt": "2026-07-09T22:00:00",
  "updatedAt": "2026-07-09T22:00:00"
}
```

> **Note:** The controller always returns `201 Created`, including when updating an existing rating.

#### Status Codes

| Code | When |
|------|------|
| `201 Created` | Rating created or updated |
| `400 Bad Request` | Validation failed |
| `401 Unauthorized` | Missing or invalid JWT |
| `404 Not Found` | Movie does not exist |
| `500 Internal Server Error` | User not found |

#### Possible Errors

| Condition | Status | Response |
|-----------|--------|----------|
| Missing / invalid token | `401` | Empty body (Spring Security) |
| Invalid or missing fields | `400` | `ApiErrorResponse` with field errors |
| Movie not found | `404` | `ApiErrorResponse` — `"Movie Not Found"` |
| User not found | `500` | Spring default error (`"User not found"`) |

---

## Endpoint Summary

| Method | URL | Auth required |
|--------|-----|---------------|
| `POST` | `/api/auth/register` | No |
| `POST` | `/api/auth/login` | No |
| `GET` | `/api/movies` | No |
| `GET` | `/api/movies/{id}` | No |
| `POST` | `/api/movies` | ADMIN + JWT |
| `PUT` | `/api/movies/{id}` | ADMIN + JWT |
| `DELETE` | `/api/movies/{id}` | ADMIN + JWT |
| `POST` | `/api/movies/{movieId}/ratings` | JWT |

---

## Not Implemented

The following are **not** exposed as API endpoints in the current codebase:

- Movie title search (`MovieService.searchMoviesByTitle` exists without a controller)
- Reviews, watchlist, favorites
- User profile management
- Logout and refresh tokens
- Booking, theatres, and payments

See [requirements.md](requirements.md) for planned scope.

---

## Related Documentation

- [README.md](../README.md) — project overview
- [architecture.md](architecture.md) — request and security flow
- [testing.md](testing.md) — manual test scenarios
