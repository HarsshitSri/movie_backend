# Movie Backend Requirements

## 1. Project Overview

### Project Name

Movie Backend

### Objective

Build a production-ready RESTful backend for a movie platform where users can discover movies, manage personal collections, interact with reviews, and receive personalized recommendations.

The project should demonstrate backend engineering practices including authentication, authorization, database design, REST API design, validation, testing, Dockerization, and deployment readiness.

---

# 2. Stakeholders

- Guest User
- Registered User
- Administrator

---

# 3. User Roles

## Guest

Can use the application without logging in.

## Registered User

Has an account and can access personalized features.

## Administrator

Has complete control over platform content.

---

# 4. Functional Requirements

## 4.1 Authentication

The system shall allow users to:

- Register
- Login
- Logout
- Refresh access tokens
- Change password
- Reset forgotten password (future enhancement)

---

## 4.2 User Profile

Users shall be able to:

- View profile
- Update profile
- Upload profile picture (future)
- Delete account

---

## 4.3 Movie Management

Users shall be able to:

- Browse movies
- View movie details
- Search by title
- Filter by genre
- Filter by language
- Filter by release year
- Filter by rating
- Sort by popularity
- Sort by release date
- Sort alphabetically

Administrators shall be able to:

- Add movies
- Update movies
- Delete movies

---

## 4.4 Reviews

Users shall be able to:

- Create review
- Edit own review
- Delete own review

Guests:

- View reviews only

Admins:

- Delete inappropriate reviews

---

## 4.5 Ratings

Users shall be able to:

- Give rating
- Update rating
- Remove rating

Movie rating should automatically update.

---

## 4.6 Watchlist

Users shall be able to:

- Add movie
- Remove movie
- View watchlist

---

## 4.7 Favorites

Users shall be able to:

- Add favorite
- Remove favorite
- View favorites

---

## 4.8 Recommendation System (Basic)

Recommend movies based on:

- Genres
- Highest ratings
- User watch history

---

# 5. Non-Functional Requirements

- RESTful API
- Layered Architecture
- PostgreSQL
- Spring Boot
- Spring Security
- JWT Authentication
- Docker
- Validation
- Logging
- Exception Handling
- Pagination
- Sorting
- Unit Tests
- Integration Tests
- API Documentation

---

# 6. Business Rules

- Email must be unique.
- Username must be unique.
- One user can rate a movie only once.
- One user can review a movie only once.
- Ratings must be between 1 and 10.
- Guests cannot create reviews.
- Guests cannot modify data.
- Only admins may delete movies.
- Deleted users should not remove movie data.

---

# 7. Success Criteria

The project is complete when:

- All endpoints function correctly.
- Authentication works securely.
- Database is normalized.
- Docker deployment succeeds.
- Tests pass.
- API documentation is complete.
