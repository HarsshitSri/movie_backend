# Deployment

## Technologies

- Docker
- Docker Compose

---

## Build

```
docker build -t moviebooking .
```

---

## Run

```
docker compose up --build
```

---

## Services

Application

```
localhost:8080
```

Database

```
localhost:5432
```

---

Environment variables

```
DB_URL

DB_USERNAME

DB_PASSWORD

JWT_SECRET

JWT_EXPIRATION
```
