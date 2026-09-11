# Catering Management Backend

This project provides a production-ready Spring Boot backend for the catering management module, built with Java 17, Spring Boot 3, PostgreSQL, JWT authentication, Flyway migrations, and OpenAPI documentation.

## Features

- JWT authentication with BCrypt password hashing
- Role-based authorization for owner/manager/staff/admin
- Catering event types, menu packages, custom options, and bookings
- Pricing engine with BigDecimal calculations
- Booking confirmation flow with deposit and status history
- Inquiry inbox and notifications
- Dashboard metrics from database
- PostgreSQL migrations with seed data
- Swagger UI and API docs

## Requirements

- Java 17+
- Maven 3.9+
- PostgreSQL 14+

## PostgreSQL Setup

1. Create the database:
   ```bash
   createdb catering_management
   ```
2. Update your environment variables in a `.env` file or shell.

## Environment Variables

Example:

```bash
DB_HOST=localhost
DB_PORT=5432
DB_NAME=catering_management
DB_USERNAME=postgres
DB_PASSWORD=postgres
JWT_SECRET=change-me-to-long-secret
APP_FRONTEND_URL=http://localhost:3000
SPRING_PROFILES_ACTIVE=dev
```

## Run Locally

```bash
mvn clean spring-boot:run
```

## API docs

Swagger UI is available at:

```text
http://localhost:8080/swagger-ui.html
```

## Test

```bash
mvn clean test
```

## Build

```bash
mvn clean package
```

## Important notes

- The frontend should call the backend from `http://localhost:8080`.
- Use the `Authorization: Bearer <token>` header for protected endpoints.
- Money values are returned as decimal values using BigDecimal serialization.
