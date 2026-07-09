# API Design

## Authentication

POST

```
/api/auth/register
```

POST

```
/api/auth/login
```

---

## Movies

GET

```
/api/movies
```

GET

```
/api/movies/{id}
```

POST

```
/api/movies
```

PUT

```
/api/movies/{id}
```

DELETE

```
/api/movies/{id}
```

---

## Authentication

Protected endpoints require

```
Authorization: Bearer <JWT>
```
