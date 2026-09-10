# Volna Catering Service

Independent Catering Management microservice for the Volna food delivery platform.

## Stack

- Java 21
- Spring Boot 3.5.6
- Spring Web
- Spring Data JPA
- Spring Security
- JWT (JJWT 0.12.6)
- PostgreSQL
- Flyway
- Lombok
- Actuator
- Docker / Docker Compose

## Port

```text
Catering Service: 8085
PostgreSQL host port: 5436
Database: catering_db
```

## Architecture

```text
React :5173
    |
    | JWT
    v
Catering Service :8085
    |
    v
PostgreSQL :5436
catering_db
```

This service is intentionally independent. It does not connect directly to restaurant_db, meal_db, or user_db.

## Important

The JWT must be issued by the same Auth Service and signed with the same secret. The JWT subject is treated as the `ownerId`, and every query is scoped to that owner.

## Run locally

1. Create the database:

```sql
CREATE DATABASE catering_db;
```

2. Start PostgreSQL on port 5436, or use the included Docker Compose.

3. Run:

```bash
mvn clean spring-boot:run
```

4. Health:

```text
GET http://localhost:8085/actuator/health
```

## Run with Docker

Build first:

```bash
mvn clean package -DskipTests
```

Then:

```bash
docker compose up --build
```

## API

### Dashboard

```text
GET /api/v1/catering/dashboard
```

### Packages

```text
GET    /api/v1/catering/packages
POST   /api/v1/catering/packages
GET    /api/v1/catering/packages/{id}
PUT    /api/v1/catering/packages/{id}
DELETE /api/v1/catering/packages/{id}
```

### Events / bookings

```text
GET    /api/v1/catering/events
POST   /api/v1/catering/events
GET    /api/v1/catering/events/{id}
PUT    /api/v1/catering/events/{id}
DELETE /api/v1/catering/events/{id}
POST   /api/v1/catering/events/{id}/confirm
```

### Inquiries

```text
GET   /api/v1/catering/inquiries
POST  /api/v1/catering/inquiries
GET   /api/v1/catering/inquiries/{id}
PATCH /api/v1/catering/inquiries/{id}/status
```

## Pricing

The backend calculates the official booking total.

Default rates:

```text
Service fee = 10%
Tax         = 8.25%
Deposit     = 30%
```

These are configurable through environment variables.

The frontend should never be trusted to provide the final total.

## Example package

```json
{
  "name": "Corporate Bites",
  "description": "Assorted canapes, light sandwiches, and beverage station.",
  "pricePerGuest": 45,
  "maxGuests": 500,
  "active": true
}
```

## Example booking

```json
{
  "eventName": "TechCorp Annual Retreat",
  "eventType": "Corporate Buffet",
  "eventDate": "2026-10-24",
  "eventTime": "10:00:00",
  "guestCount": 150,
  "venueAddress": "123 Business Way, Suite 500",
  "specialInstructions": "Loading dock access required.",
  "packageId": 1,
  "customOptions": [
    {
      "name": "Add Vegan Dessert Platter",
      "amount": 250
    },
    {
      "name": "Premium Beverage Upgrade",
      "amount": 400
    }
  ]
}
```

## Future integrations

Redis, Kafka, Spring Cloud and Kubernetes are intentionally not required for V1. They can be added later when the platform needs caching, event-driven integration, service discovery/gateway, and production orchestration.
