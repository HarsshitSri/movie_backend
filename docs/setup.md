# Project Setup

## Requirements

- Java 21
- Maven 3.9+
- PostgreSQL 16+
- IntelliJ IDEA
- Git
- Postman

---

## Clone Repository

```bash
git clone https://github.com/<your-username>/movie-booking-backend.git
cd movie-booking-backend
```

---

## Configure Database

Create a PostgreSQL database.

```sql
CREATE DATABASE movie_booking;
```

Update:

```
src/main/resources/application.properties
```

```
spring.datasource.url=jdbc:postgresql://localhost:5432/movie_booking
spring.datasource.username=postgres
spring.datasource.password=your_password
```

---

## Run

```
mvn spring-boot:run
```

or

```
./mvnw spring-boot:run
```

Application runs on

```
http://localhost:8080
```
