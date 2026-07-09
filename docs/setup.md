# Project Setup

Local setup guide for the Spring Boot application in `backend/MovieBooking/`.

## Requirements

| Tool | Version |
|------|---------|
| Java | 21 |
| Maven | 3.9+ (or use `./mvnw`) |
| PostgreSQL | 16+ |
| Git | Any recent version |

Optional: Docker, Docker Compose, Postman, IntelliJ IDEA

---

## Clone Repository

```bash
git clone <repository-url>
cd Movie_Backend
```

---

## Configure Database

Create a PostgreSQL database:

```sql
CREATE DATABASE movie_booking;
```

Configure connection settings with environment variables (recommended):

```bash
cd backend/MovieBooking
cp .env.example .env
```

Example `.env` values:

```env
DB_URL=jdbc:postgresql://localhost:5432/movie_booking
DB_USERNAME=postgres
DB_PASSWORD=your_password
```

Load the variables before running the app:

```bash
set -a && source .env && set +a
```

The application reads these values from `backend/MovieBooking/src/main/resources/application.properties`:

```properties
spring.datasource.url=${DB_URL}
spring.datasource.username=${DB_USERNAME}
spring.datasource.password=${DB_PASSWORD}
```

---

## Run

From `backend/MovieBooking/`:

```bash
./mvnw spring-boot:run
```

Or with a system Maven installation:

```bash
mvn spring-boot:run
```

Application URL:

```
http://localhost:8080
```

---

## Docker Alternative

To run PostgreSQL and the application with Docker Compose, see [deployment.md](deployment.md).

---

## Related Documentation

- [README.md](../README.md) — project overview
- [api-design.md](api-design.md) — API endpoints
- [testing.md](testing.md) — manual testing guide
