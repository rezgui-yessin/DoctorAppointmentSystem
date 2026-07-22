# Doctor Appointment System

A REST API for managing doctors, patients, and appointment bookings — built with **Spring Boot 3 + Java 17**, layered architecture, JWT authentication, and role-based access control.

## Tech Stack

- Java 17, Spring Boot 3.3
- Spring Web, Spring Data JPA, Spring Security
- PostgreSQL (production) / H2 (local dev, in-memory)
- JWT (jjwt 0.12.x)
- Lombok
- springdoc-openapi (Swagger UI)
- Maven
- Docker / docker-compose

## Architecture

```
controller  -> handles HTTP, request validation
service     -> business logic, interfaces + impl
repository  -> Spring Data JPA interfaces
entity      -> JPA entities
dto         -> request/response records (never expose entities directly)
mapper      -> entity <-> DTO conversion
security    -> JWT filter, JWT util, UserDetailsService
config      -> SecurityConfig, SwaggerConfig
exception   -> custom exceptions + @RestControllerAdvice
```

## Quick Start (no DB install needed)

Runs with an in-memory H2 database — good for trying the API immediately.

```bash
mvn clean install
mvn spring-boot:run -Dspring-boot.run.profiles=dev
```

App runs at `http://localhost:8080`
Swagger UI: `http://localhost:8080/swagger-ui.html`
H2 console: `http://localhost:8080/h2-console` (JDBC URL: `jdbc:h2:mem:doctordb`, user `sa`, no password)

## Run with PostgreSQL (production-like)

### Option A: Docker Compose (recommended)

```bash
docker-compose up --build
```

This starts Postgres + the app together. App available at `http://localhost:8080`.

### Option B: Local Postgres

1. Create a database:
```sql
CREATE DATABASE doctor_appointment_db;
```
2. Update `src/main/resources/application.properties` with your DB credentials if different from defaults.
3. Run:
```bash
mvn spring-boot:run
```

## Authentication Flow

1. **Register** a user (role: `ADMIN`, `DOCTOR`, or `PATIENT`):
```bash
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{"email":"admin@clinic.com","password":"secret123","role":"ADMIN"}'
```

2. **Login** to get a JWT:
```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"admin@clinic.com","password":"secret123"}'
```

3. Use the returned `token` in the `Authorization` header for subsequent calls:
```bash
curl http://localhost:8080/api/doctors \
  -H "Authorization: Bearer <token>"
```

## Example Endpoints

| Method | Endpoint | Auth | Description |
|---|---|---|---|
| POST | `/api/auth/register` | public | Create a login account |
| POST | `/api/auth/login` | public | Get a JWT |
| POST | `/api/doctors` | ADMIN | Create a doctor profile |
| GET | `/api/doctors?specialization=Cardiology` | any authenticated | List/filter doctors |
| POST | `/api/patients` | any authenticated | Register a patient profile |
| POST | `/api/appointments` | any authenticated | Book an appointment (conflict-checked) |
| GET | `/api/appointments/patient/{id}` | any authenticated | List a patient's appointments |
| GET | `/api/appointments/doctor/{id}?page=0&size=10` | any authenticated | Paginated doctor schedule |
| PATCH | `/api/appointments/{id}/status?status=CONFIRMED` | ADMIN, DOCTOR | Change appointment status |
| DELETE | `/api/appointments/{id}` | any authenticated | Cancel an appointment |

Note: the current authorization rules are intentionally simple (role-based only). In a real deployment you'd also want **ownership checks** — e.g. a PATIENT should only cancel their own appointment, a DOCTOR should only update their own schedule. That's a natural next step (see below).

## Running Tests

```bash
mvn test
```

## Suggested Next Steps

- Add ownership-based authorization (patient can only see/cancel their own appointments)
- Doctor availability/time-slot management instead of raw datetime booking
- Email/SMS reminders via Spring `@Scheduled` + JavaMailSender
- Pagination/filtering on doctor and patient listing endpoints
- Testcontainers-based integration tests against real Postgres
- Refresh tokens for JWT
