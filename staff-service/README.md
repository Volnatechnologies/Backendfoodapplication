# Staff Management Backend

Production-ready Spring Boot backend for the Staff Management, Shift Planning, Coverage Summary, Peak Hour Analysis, and Shift Request Management system.

## Features

- **Staff Management:** Full CRUD operations, dynamic metrics (`totalHeadcount`, `activeToday`, `lateAbsent`), and keyword search.
- **Shift Planning:** Shift scheduling with duration validation (>= 30 mins), start/end time validation, and overlap conflict protection.
- **Weekly Schedule:** Weekly staff schedule matrix across days of the week.
- **Coverage Summary:** Scheduled hours, budget hours, utilization %, and period-by-period coverage status (`COVERED`, `PARTIALLY_COVERED`, `UNCOVERED`).
- **Peak Hour Analysis:** Identification of peak staffing periods, max staff counts, shortage, and excess across operating hours.
- **Shift Request Management:** Time-off requests and shift-swap requests with conflict prevention, and approval/denial state machine.
- **Root Health Check:** `GET /` returns status `UP` with HTTP 200.

## Tech Stack

- Java 17
- Spring Boot 3.3.4
- Spring Data JPA / Hibernate
- PostgreSQL & H2
- Lombok
- Bean Validation

## Running Locally

```bash
mvn clean test
mvn spring-boot:run
```

Server starts on port `8085` with context path `/`.
API endpoints are under `/api`.
Health check is available at `GET /`.
