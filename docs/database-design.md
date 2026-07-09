# Database Design

## Database

PostgreSQL

## Design Goals

- Third Normal Form (3NF)
- Avoid redundant data
- Maintain referential integrity
- Scalable and maintainable schema
- Optimized for read-heavy operations

# Entities

| Entity    | Description            |
| --------- | ---------------------- |
| User      | Registered users       |
| Role      | USER / ADMIN           |
| Movie     | Movie information      |
| Genre     | Movie categories       |
| Rating    | User ratings           |
| Review    | User reviews           |
| Watchlist | Movies saved for later |
| Favorite  | User's favorite movies |

# Relationships

One Role can have many Users.

One User can write many Reviews.

One Movie can have many Reviews.

One User can rate many Movies.

One Movie can receive many Ratings.

One User can have many Favorites.

One User can have many Watchlist entries.

One Movie can belong to many Genres.

One Genre can contain many Movies.

# Tables

## Roles

### Purpose

The `roles` table defines the different levels of access available within the application. Each role determines the permissions granted to a user.

For the initial version of the project, the system supports the following roles:

- USER
- ADMIN

Each user is assigned exactly one role, while a single role can be assigned to multiple users.

---

### Columns

| Column | Data Type | Constraints | Description |
|---------|-----------|-------------|-------------|
| id | BIGSERIAL | PRIMARY KEY | Unique identifier for each role. |
| name | VARCHAR(30) | NOT NULL, UNIQUE | Name of the role (e.g., `USER`, `ADMIN`). |
| description | VARCHAR(255) | NULL | Optional description explaining the role. |

---

### Relationship

- One Role → Many Users (1:N)
- Each User → One Role

---

### Constraints

- `id` is the primary key.
- `name` must be unique.
- `name` cannot be NULL.

---

### Sample Data

| id | name | description |
|----|------|-------------|
| 1 | ADMIN | Full access to manage the application. |
| 2 | USER | Standard user with access to application features. |

---

### Design Decisions

- A separate `roles` table is used instead of storing role names directly in the `users` table.
- This keeps the database normalized and avoids repeating identical role values for every user.
- New roles can be added in the future without modifying the database schema.
- The `description` column allows additional information about each role without changing the table structure.

---

### Indexes

| Index | Purpose |
|--------|---------|
| Primary Key (`id`) | Fast lookup by role ID. |
| Unique Index (`name`) | Prevents duplicate role names and speeds up searches by role. |

## Users


### Purpose

The `users` table stores information about every registered user in the application. It is responsible for user authentication, authorization, and maintaining profile information.

This table acts as the central entity of the system. Other tables such as reviews, ratings, favorites, and watchlists reference users to associate activities with a specific account.

---

### Columns

| Column | Data Type | Constraints | Description |
|---------|-----------|-------------|-------------|
| id | BIGSERIAL | PRIMARY KEY | Unique identifier for each user. |
| username | VARCHAR(30) | NOT NULL, UNIQUE | Public username used to identify a user. |
| first_name | VARCHAR(50) | NOT NULL | User's first name. |
| last_name | VARCHAR(50) | NOT NULL | User's last name. |
| email | VARCHAR(255) | NOT NULL, UNIQUE | User's email address used for authentication. |
| password_hash | VARCHAR(255) | NOT NULL | BCrypt hashed password. Plain-text passwords are never stored. |
| role_id | BIGINT | NOT NULL, FOREIGN KEY | References the user's role in the `roles` table. |
| date_of_birth | DATE | NOT NULL | Used to enforce the minimum age requirement (13+ years). |
| account_status | VARCHAR(20) | NOT NULL | Current account state (e.g., ACTIVE, SUSPENDED, BANNED, DELETED). |
| created_at | TIMESTAMP | NOT NULL | Timestamp when the account was created. |
| updated_at | TIMESTAMP | NOT NULL | Timestamp of the latest profile update. |

---

### Relationships

- One Role → Many Users (1:N)
- One User → Many Reviews (1:N)
- One User → Many Ratings (1:N)
- One User → Many Watchlist Entries (1:N)
- One User → Many Favorites (1:N)

---

### Constraints

- `id` is the primary key.
- `username` must be unique.
- `email` must be unique.
- `password_hash` stores only hashed passwords.
- `role_id` references `roles(id)`.
- `date_of_birth` must represent an age of at least 13 years.
- `account_status` must contain a valid predefined value.

---

### Sample Data

| id | username | first_name | last_name | email | role_id | account_status |
|----|----------|------------|-----------|-------|---------|----------------|
| 1 | harshit | Harshit | Srivastava | harshit@example.com | 2 | ACTIVE |
| 2 | admin | System | Admin | admin@example.com | 1 | ACTIVE |

---

### Design Decisions

- `username` is kept separate from the user's real name. It serves as the user's public identity and allows future features such as profile sharing and user search.
- `email` is used for authentication and is unique across all users.
- Passwords are never stored in plain text. Only BCrypt password hashes are stored.
- User permissions are managed through the `roles` table to maintain database normalization and simplify future role expansion.
- `date_of_birth` is stored to enforce the application's minimum age requirement (13+ years).
- `account_status` is used instead of a simple boolean (`is_active`) to support multiple account states such as `ACTIVE`, `SUSPENDED`, `BANNED`, and `DELETED`.
- Accounts are logically deleted through anonymization instead of physical deletion. Personal information is removed while preserving reviews, ratings, and other historical data to maintain referential integrity.

---

### Indexes

| Index | Purpose |
|--------|---------|
| Primary Key (`id`) | Fast lookup by user ID. |
| Unique Index (`username`) | Prevents duplicate usernames and speeds up username searches. |
| Unique Index (`email`) | Prevents duplicate email addresses and speeds up authentication lookups. |
| Index (`role_id`) | Improves joins between `users` and `roles`. |
| Index (`account_status`) | Optimizes filtering users by account status. |

## Movies

### Purpose

The `movies` table stores information about every movie available on the platform. It serves as the primary source of movie-related data and is referenced by features such as ratings, reviews, favorites, watchlists, and recommendations.

Only information that directly describes a movie is stored in this table. Related entities such as genres, reviews, ratings, and actors are managed through separate tables to maintain database normalization.

---

### Columns

| Column | Data Type | Constraints | Description |
|---------|-----------|-------------|-------------|
| id | BIGSERIAL | PRIMARY KEY | Unique identifier for each movie. |
| title | VARCHAR(255) | NOT NULL | Official movie title. |
| synopsis | TEXT | NOT NULL | Short description or overview of the movie. |
| release_date | DATE | NOT NULL | Official release date. |
| runtime_minutes | INTEGER | NOT NULL | Duration of the movie in minutes. |
| language | VARCHAR(50) | NOT NULL | Original language of the movie. |
| country_of_origin | VARCHAR(100) | NOT NULL | Country where the movie was produced. |
| content_rating | VARCHAR(20) | NOT NULL | Age/content classification (e.g., PG, PG-13, R, 18+). |
| poster_url | TEXT | NULL | URL of the movie poster image. |
| average_rating | DECIMAL(3,2) | DEFAULT 0 | Cached average rating of the movie. |
| rating_count | INTEGER | DEFAULT 0 | Total number of ratings received. |
| created_at | TIMESTAMP | NOT NULL | Timestamp when the movie was added to the system. |
| updated_at | TIMESTAMP | NOT NULL | Timestamp of the latest movie update. |

---

### Relationships

- One Movie → Many Reviews (1:N)
- One Movie → Many Ratings (1:N)
- One Movie → Many Favorites (1:N)
- One Movie → Many Watchlist Entries (1:N)
- One Movie → Many Genres (M:N)

---

### Constraints

- `id` is the primary key.
- `title` cannot be NULL.
- `release_date` cannot be NULL.
- `runtime_minutes` must be greater than 0.
- `average_rating` must be between 0.00 and 10.00.
- `rating_count` cannot be negative.
- `content_rating` must contain a valid predefined value.

---

### Sample Data

| id | title | language | runtime_minutes | average_rating | rating_count |
|----|-------|----------|-----------------|----------------|--------------|
| 1 | Interstellar | English | 169 | 8.70 | 1543 |
| 2 | 3 Idiots | Hindi | 170 | 9.10 | 2084 |

---

### Design Decisions

- Only movie-specific information is stored in this table.
- Genres are managed through a separate `genres` table with a `movie_genres` junction table because a movie can belong to multiple genres.
- Actors, directors, and production studios are intentionally excluded from Version 1 to keep the project focused and the database properly normalized.
- `average_rating` and `rating_count` are stored instead of being calculated on every request. This is a deliberate denormalization to improve read performance, as movies are viewed much more frequently than ratings are submitted.
- Runtime is stored in minutes instead of hours for easier calculations and filtering.
- `release_date` is stored instead of only the release year, allowing more precise queries and sorting.

---

### Indexes

| Index | Purpose |
|--------|---------|
| Primary Key (`id`) | Fast lookup by movie ID. |
| Index (`title`) | Speeds up movie title searches. |
| Index (`release_date`) | Optimizes sorting and filtering by release date. |
| Index (`average_rating`) | Optimizes ranking and recommendation queries. |

# Ratings

## Purpose

The `ratings` table stores the ratings given by users to movies. It acts as a junction table between the `users` and `movies` tables while also storing additional information about the relationship, specifically the rating value.

Each user can rate a movie only once, while a movie can receive ratings from many users. The data stored in this table is used to calculate and maintain the cached `average_rating` and `rating_count` values in the `movies` table.

---

## Columns

| Column | Data Type | Constraints | Description |
|---------|-----------|-------------|-------------|
| id | BIGSERIAL | PRIMARY KEY | Unique identifier for each rating. |
| user_id | BIGINT | NOT NULL, FOREIGN KEY | References the user who submitted the rating. |
| movie_id | BIGINT | NOT NULL, FOREIGN KEY | References the movie being rated. |
| rating | INTEGER | NOT NULL | Rating value between 1 and 10. |
| created_at | TIMESTAMP | NOT NULL | Timestamp when the rating was created. |
| updated_at | TIMESTAMP | NOT NULL | Timestamp when the rating was last updated. |

---

## Relationships

- One User → Many Ratings (1:N)
- One Movie → Many Ratings (1:N)
- One Rating → One User (N:1)
- One Rating → One Movie (N:1)

---

## Constraints

- `id` is the primary key.
- `user_id` references `users(id)`.
- `movie_id` references `movies(id)`.
- `rating` must be between **1** and **10**.
- A user can rate the same movie only once.
- The combination of (`user_id`, `movie_id`) must be unique.

---

## Sample Data

| id | user_id | movie_id | rating |
|----|---------|----------|--------|
| 1 | 5 | 1 | 9 |
| 2 | 8 | 1 | 8 |
| 3 | 5 | 3 | 10 |

---

## Design Decisions

- A separate `ratings` table is used instead of storing ratings directly in the `movies` table because ratings belong to the relationship between a user and a movie.
- The table serves as a junction table while also storing additional attributes, making a direct Many-to-Many relationship inappropriate.
- Each user is allowed only one rating per movie through a composite unique constraint on (`user_id`, `movie_id`).
- The `average_rating` and `rating_count` values are intentionally cached in the `movies` table to improve read performance. These values are updated whenever a rating is created, updated, or deleted.
- Ratings are restricted to whole numbers between **1** and **10** in Version 1 for simplicity. Decimal ratings can be introduced in a future version if required.
- `created_at` and `updated_at` are stored to maintain an audit trail and support future features such as rating history.

---

## Indexes

| Index | Purpose |
|--------|---------|
| Primary Key (`id`) | Fast lookup by rating ID. |
| Index (`user_id`) | Improves lookups of ratings submitted by a user. |
| Index (`movie_id`) | Improves retrieval of ratings for a movie. |
| Unique Composite Index (`user_id`, `movie_id`) | Prevents duplicate ratings by the same user for the same movie and speeds up existence checks. |
