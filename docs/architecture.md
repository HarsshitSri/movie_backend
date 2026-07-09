# Architecture

The project follows a layered architecture.

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

## Layers

### Controller

Handles HTTP Requests.

### Service

Contains business logic.

### Repository

Communicates with PostgreSQL using Spring Data JPA.

### Entity

Maps Java objects to database tables.

### DTO

Transfers data between client and server.

### Security

Handles authentication, authorization and JWT validation.
