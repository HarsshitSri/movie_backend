# Database Design

PostgreSQL schema design for the Movie Booking Backend.

**ORM:** Spring Data JPA / Hibernate

**Application package:** `com.harshit.moviebooking.entity`

---

## Database

PostgreSQL

---

## Design Goals

- Third Normal Form (3NF)
- Avoid redundant data
- Maintain referential integrity
- Scalable and maintainable schema
- Optimized for read-heavy operations

---

## Entities

### Implemented (current codebase)

| Entity | Table | JPA Class |
|--------|-------|-----------|
| User | `users` | `User` |
| Role | `roles` | `Role` |
| Movie | `movies` | `Movie` |
| Rating | `ratings` | `Rating` |
| Review | `reviews` | `Review` |
| Watchlist entry | `watchlist_entries` | `WatchlistEntry` |

### Planned (documented for future versions)

| Entity | Description |
|--------|-------------|
| Genre | Movie categories |
| Favorite | User's favorite movies |

---

## Relationships

### Implemented

- One `Role` → Many `User` (1:N); FK `users.role_id` → `roles.id`
- Many `User` → One `Role` (N:1) — each user has exactly one role
- One `User` → Many `Rating` (1:N); FK `ratings.user_id` → `users.id`
- One `Movie` → Many `Rating` (1:N); FK `ratings.movie_id` → `movies.id`
- Unique (`user_id`, `movie_id`) on `ratings` — one rating per user per movie (`uk_user_movie` in JPA)
- One `User` → Many `Review` (1:N); FK `reviews.user_id` → `users.id`
- One `Movie` → Many `Review` (1:N); FK `reviews.movie_id` → `movies.id`
- Unique (`user_id`, `movie_id`) on `reviews` — one review per user per movie
- One `User` → Many `WatchlistEntry` (1:N); FK `watchlist_entries.user_id` → `users.id`
- One `Movie` → Many `WatchlistEntry` (1:N); FK `watchlist_entries.movie_id` → `movies.id`
- Unique (`user_id`, `movie_id`) on `watchlist_entries`
- Denormalized aggregates on `movies`: `average_rating`, `rating_count` (updated by `RatingServiceImpl`, not FK relationships)

### Planned (not in database or JPA today)

- One `User` → Many `Favorite`
- Many `Movie` ↔ Many `Genre` (via `genres` / `movie_genres`)

### Not designed yet (roadmap only)

The following domains appear in [requirements.md](requirements.md) and the README roadmap but have **no tables, entities, or ERD** in this repository:

- Theatres, screens, seats
- Showtimes
- Bookings / reservations
- Payments

Do not include these in an **implemented** schema diagram. When booking is designed, add a separate planned ERD section.

---

## Schema Diagrams (documentation)

There is **no ERD image file** in the repository. Relationship detail lives in this document and in JPA entities under `com.harshit.moviebooking.entity`.

Planned asset: `assets/diagrams/database-schema-erd.png` ([ASSETS_PLAN.md](../assets/ASSETS_PLAN.md)).

### What an implemented ERD should show

| Element | Include? | Source in code |
|---------|----------|----------------|
| Table `roles` | Yes | `Role` |
| Table `users` | Yes | `User` |
| Table `movies` | Yes | `Movie` |
| Table `ratings` | Yes | `Rating` |
| `users.role_id` → `roles.id` | Yes | `@ManyToOne` on `User.role` |
| `ratings.user_id` → `users.id` | Yes | `@ManyToOne` on `Rating.user` |
| `ratings.movie_id` → `movies.id` | Yes | `@ManyToOne` on `Rating.movie` |
| Unique (`user_id`, `movie_id`) on `ratings` | Yes | `@UniqueConstraint(name = "uk_user_movie")` |
| `movies.average_rating`, `movies.rating_count` | Yes (columns, not relations) | Denormalized; maintained on rating write |
| `Genre`, `Review`, `Watchlist`, `Favorite` | **No** — planned only | Not in `entity` package |
| Theatre, showtime, seat, booking, payment tables | **No** — not designed | Roadmap only |

### Diagram accuracy review (vs. implementation)

| Issue | Status in docs | Correction |
|-------|----------------|------------|
| Missing `User` → `Role` FK direction in relationship summary | Was implicit only | Added N:1 and `users.role_id` above |
| Planned entities mixed with implemented | Was split in entity table | ERD section now states planned tables must be excluded from implemented diagram |
| Booking schema implied by project name | No booking tables documented | Explicit **not designed yet** section added |
| JPA bidirectional collections | Partially documented | `User.ratings`, `Movie.ratings`, `Role.users` exist as `@OneToMany` but are not required on a logical ERD |
| Repository naming in architecture docs | `MovieRepo` vs `RatingRepository` | Table names are `movies` / `ratings`; repository interface names do not affect schema diagrams |

### Outdated schema assumptions to avoid

- Do not draw `favorites` or `genres` / `movie_genres` as current tables (still planned).
- Do draw `reviews` and `watchlist_entries` — they are implemented.
- Do not draw booking-domain tables (`theatres`, `showtimes`, `bookings`, etc.) unless marked **planned / future**.
- Do not show JWT or API layers on a database ERD; keep persistence model only.

---

# Implemented Tables

## Roles

### Purpose

The `roles` table defines access levels in the application.

Initial roles:

- `USER`
- `ADMIN`

Each user is assigned exactly one role.

---

### Columns

| Column | Data Type | Constraints | Description |
|--------|-----------|-------------|-------------|
| id | BIGSERIAL | PRIMARY KEY | Unique identifier for each role |
| name | VARCHAR(30) | NOT NULL, UNIQUE | Role name (`USER`, `ADMIN`) |
| description | VARCHAR(255) | NULL | Optional role description |

---

### Indexes

| Index | Purpose |
|-------|---------|
| Primary Key (`id`) | Fast lookup by role ID |
| Unique Index (`name`) | Prevents duplicate role names |

---

## Users

### Purpose

Stores registered users for authentication and profile data.

---

### Columns

| Column | Data Type | Constraints | Description |
|--------|-----------|-------------|-------------|
| id | BIGSERIAL | PRIMARY KEY | Unique identifier for each user |
| username | VARCHAR(30) | NOT NULL, UNIQUE | Public username |
| first_name | VARCHAR(50) | NOT NULL | User's first name |
| last_name | VARCHAR(50) | NOT NULL | User's last name |
| email | VARCHAR(255) | NOT NULL, UNIQUE | Email used for authentication |
| password_hash | VARCHAR(255) | NOT NULL | BCrypt hashed password |
| role_id | BIGINT | NOT NULL, FOREIGN KEY | References `roles(id)` |
| date_of_birth | DATE | NOT NULL | User date of birth |
| account_status | VARCHAR(20) | NOT NULL | `ACTIVE`, `SUSPENDED`, `BANNED`, `DELETED` |
| created_at | TIMESTAMP | NOT NULL | Account creation timestamp |
| updated_at | TIMESTAMP | NOT NULL | Last update timestamp |

**JPA enum:** `AccountStatus`

---

### Relationships

- One `Role` → Many `User`
- One `User` → Many `Rating`

---

### Indexes (target design)

| Index | Purpose |
|-------|---------|
| Primary Key (`id`) | Fast lookup by user ID |
| Unique Index (`username`) | Prevents duplicate usernames |
| Unique Index (`email`) | Speeds up authentication lookups |
| Index (`role_id`) | Improves joins with `roles` |
| Index (`account_status`) | Filtering by account status |

---

## Movies

### Purpose

Stores movie catalog data. Referenced by ratings, reviews, and watchlist entries.

---

### Columns

| Column | Data Type | Constraints | Description |
|--------|-----------|-------------|-------------|
| id | BIGSERIAL | PRIMARY KEY | Unique identifier |
| title | VARCHAR(255) | NOT NULL | Movie title |
| synopsis | TEXT | NOT NULL | Movie description |
| release_date | DATE | NOT NULL | Release date |
| runtime_minutes | INTEGER | NOT NULL | Duration in minutes |
| language | VARCHAR(50) | NOT NULL | Original language |
| country_of_origin | VARCHAR(100) | NOT NULL | Country of production |
| content_rating | VARCHAR(20) | NOT NULL | Age/content classification |
| poster_url | TEXT | NULL | Poster image URL |
| average_rating | DECIMAL(3,2) | DEFAULT 0 | Cached average rating |
| rating_count | INTEGER | DEFAULT 0 | Total number of ratings |
| created_at | TIMESTAMP | NOT NULL | Created timestamp |
| updated_at | TIMESTAMP | NOT NULL | Updated timestamp |

**JPA enum:** `ContentRating` — `G`, `PG`, `PG_13`, `R`, `NC_17`, `ADULT_18`

---

### Relationships

- One `Movie` → Many `Rating`

---

### Design Decisions

- `average_rating` and `rating_count` are denormalized for read performance
- Runtime is stored in minutes
- Genres are planned as a separate `genres` / `movie_genres` design in a future version

---

### Indexes (target design)

| Index | Purpose |
|-------|---------|
| Primary Key (`id`) | Fast lookup by movie ID |
| Index (`title`) | Title search |
| Index (`release_date`) | Sorting and filtering by date |
| Index (`average_rating`) | Ranking queries |

---

## Ratings

### Purpose

Stores user ratings for movies. Maintains one rating per user per movie.

Used to update cached `average_rating` and `rating_count` on `movies`.

---

### Columns

| Column | Data Type | Constraints | Description |
|--------|-----------|-------------|-------------|
| id | BIGSERIAL | PRIMARY KEY | Unique identifier |
| user_id | BIGINT | NOT NULL, FOREIGN KEY | References `users(id)` |
| movie_id | BIGINT | NOT NULL, FOREIGN KEY | References `movies(id)` |
| rating | INTEGER | NOT NULL | Rating value between 1 and 10 |
| created_at | TIMESTAMP | NOT NULL | Created timestamp |
| updated_at | TIMESTAMP | NOT NULL | Updated timestamp |

---

### Constraints

- `rating` must be between **1** and **10**
- Unique (`user_id`, `movie_id`) — enforced in JPA via `@UniqueConstraint(name = "uk_user_movie")`

---

### Indexes (target design)

| Index | Purpose |
|-------|---------|
| Primary Key (`id`) | Fast lookup by rating ID |
| Index (`user_id`) | Ratings by user |
| Index (`movie_id`) | Ratings by movie |
| Unique Composite Index (`user_id`, `movie_id`) | Prevents duplicate ratings |

---

## Schema Management

The default application configuration uses:

```properties
spring.jpa.hibernate.ddl-auto=update
```

Indexes listed as **target design** may require explicit migrations or `@Index` annotations to exist in production databases.

---

## Related Documentation

- [architecture.md](architecture.md) — application layers
- [requirements.md](requirements.md) — functional requirements
- [api-design.md](api-design.md) — API endpoints
