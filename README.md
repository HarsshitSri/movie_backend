# 🎬 Movie Booking Backend API

A RESTful backend for a movie ticket booking platform built with Java Spring Boot.

The project demonstrates backend development concepts including authentication, authorization, CRUD operations, pagination, DTO mapping, validation, and relational database design.

---

## Features

- User Registration
- User Login
- JWT Authentication
- BCrypt Password Encryption
- Role-Based Authorization
- Movie CRUD Operations
- Pagination & Sorting
- Request Validation
- PostgreSQL Integration
- Layered Architecture

---

## Tech Stack

| Technology | Version |
|------------|----------|
| Java | 21 |
| Spring Boot | 3.5.x |
| Spring Security | 6 |
| Spring Data JPA | Hibernate |
| PostgreSQL | 16+ |
| Maven | 3.9+ |
| JWT | JJWT |
| Lombok | Latest |

---

## Project Structure

```
src
├── config
├── controller
├── dto
├── entity
├── enums
├── repository
├── security
├── service
│   └── impl
└── resources
```

---

## Authentication

JWT Authentication is used to secure protected endpoints.

### Public Endpoints

```
POST /api/auth/register
POST /api/auth/login
```

### Protected Endpoints

```
GET    /api/movies
GET    /api/movies/{id}
POST   /api/movies
PUT    /api/movies/{id}
DELETE /api/movies/{id}
```

Include the JWT token in the request header:

```
Authorization: Bearer <JWT_TOKEN>
```

---

## API Example

### Register

```http
POST /api/auth/register
```

```json
{
  "firstName": "Harshit",
  "lastName": "Srivastava",
  "username": "harshit",
  "email": "harshit@example.com",
  "password": "Password@123",
  "dateOfBirth": "2003-07-25"
}
```

---

### Login

```http
POST /api/auth/login
```

```json
{
  "email": "harshit@example.com",
  "password": "Password@123"
}
```

---

### Create Movie

```http
POST /api/movies
```

```json
{
  "title": "Interstellar",
  "description": "Science Fiction",
  "duration": 169,
  "language": "English",
  "genre": "Sci-Fi",
  "releaseDate": "2014-11-07"
}
```

---

## Database

PostgreSQL is used as the relational database.

The application uses Spring Data JPA with Hibernate for ORM.

---

## Security

- BCrypt Password Encoding
- Stateless Authentication
- JWT Token Generation
- JWT Request Filter
- Spring Security Filter Chain

---

## Validation

Bean Validation (Jakarta Validation) is used to validate incoming requests.

Examples:

- Email format validation
- Password validation
- Required fields
- Date validation

---

## Future Improvements

- Theatre Management
- Screens
- Seats
- Show Scheduling
- Ticket Booking
- Payment Integration
- Email Notifications
- Refresh Tokens
- Swagger/OpenAPI
- Global Exception Handling
- Unit & Integration Testing
- CI/CD Pipeline
- Docker Deployment
- Kubernetes Deployment

---

## Getting Started

### Clone

```bash
git clone https://github.com/<your-username>/movie-booking-backend.git
```

### Enter Project

```bash
cd movie-booking-backend
```

### Configure Database

Update `application.properties`

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/movie_booking
spring.datasource.username=postgres
spring.datasource.password=your_password

jwt.secret=your-secret-key
jwt.expiration=86400000
```

### Run

```bash
mvn spring-boot:run
```

or

```bash
./mvnw spring-boot:run
```

---

## Architecture

```
Client
   │
   ▼
Controller
   │
   ▼
Service
   │
   ▼
Repository
   │
   ▼
PostgreSQL
```

---

## Author

Harshit Srivastava

Backend Developer (Java | Spring Boot)

---

## License

This project is intended for educational and portfolio purposes.
