# UserService Backend Application

A Spring Boot microservice for the **Volna Food Delivery Backend**, responsible for authenticated user profile management and delivery address management.

This service facilitates RESTful API communication, providing robust support for address CRUD operations, default-address management, DTO validation, JWT-based authentication, PostgreSQL persistence, and Docker-supported development.

> **Scope Note:** This repository currently focuses on the `User_Service` layer of the food delivery backend. User profile and address APIs are implemented using Spring Boot controllers, services, repositories, DTOs, entities, Spring Security, and PostgreSQL. The APIs have been validated using Postman.

---

## 🛠 Tech Stack

* **Backend Framework:** Java 17+ · Spring Boot · Spring MVC
* **Security & Auth:** Spring Security · JWT (`@AuthenticationPrincipal`)
* **Data & Persistence:** Spring Data JPA · Hibernate · PostgreSQL
* **Validation & Utilities:** Jakarta Validation · Lombok · Maven
* **Containerization:** Docker · Docker Compose · Docker Desktop
* **Testing:** Postman

---

## 📁 Project Structure

```text
User-Service/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/
│   │   │       └── Volna/
│   │   │           └── User_Service/
│   │   │               ├── config/
│   │   │               ├── controller/
│   │   │               │   ├── AddressController.java
│   │   │               │   └── UserController.java
│   │   │               ├── dto/
│   │   │               │   ├── AddressRequestDto.java
│   │   │               │   ├── AddressResponseDto.java
│   │   │               │   ├── UserProfileRequestDto.java
│   │   │               │   └── UserProfileResponseDto.java
│   │   │               ├── entity/
│   │   │               ├── exception/
│   │   │               ├── repository/
│   │   │               └── service/
│   │   └── resources/
│   │       └── application.properties
│   └── test/
├── Dockerfile
├── docker-compose.yml
├── pom.xml
└── README.md

```

---

## 🏗 Architecture & Request Flow

```text
Client / Postman
      │
      ▼
REST Controller ──► [Spring Security / JWT] ──► Extracted @AuthenticationPrincipal (authUserId)
      │                                                                  │
      ▼                                                                  ▼
Request DTO + Validation                                           UUID userId
      │                                                                  │
      └──────────────────────────┬───────────────────────────────────────┘
                                 ▼
                           Service Layer
                                 │
                                 ▼
                          Repository Layer
                                 │
                                 ▼
                       JPA Entity ──► PostgreSQL

```

---

## 🚀 Getting Started

### Prerequisites

* Java 17 or higher
* Maven 3.8+
* PostgreSQL running locally or via Docker
* Docker Desktop (optional, for containerized run)

### Installation & Local Setup

1. **Clone the repository:**
```bash
git clone <YOUR_GITHUB_REPOSITORY_URL>
cd User-Service

```


2. **Configure Database & Credentials:**
Update `src/main/resources/application.properties` or set environment variables:
```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/volna_user_db
spring.datasource.username=postgres
spring.datasource.password=your_password

```


3. **Build the Application:**
```bash
mvn clean install

```


4. **Run the Service:**
```bash
mvn spring-boot:run

```


The service will start locally on `http://localhost:8082`.

### Docker Setup

To spin up the service and its dependencies (PostgreSQL) using Docker Compose:

```bash
# Start containers in detached mode
docker compose up -d

# Check running services
docker compose ps

# Stop containers
docker compose down

```

#### Default Container Ports

* **UserService:** `8082`
* **PostgreSQL:** `5432`
* **Keycloak / RabbitMQ:** Configured via `docker-compose.yml`

---

## 📌 API Reference

* **Base URL:** `http://localhost:8082`
* **Common Headers:**
* `Authorization: Bearer <JWT_TOKEN>`
* `Content-Type: application/json`



### 👤 User Profile APIs

#### 1. Get Current User Profile

* **Endpoint:** `GET /api/v1/users/me`
* **Response (`200 OK`):**
```json
{
  "id": "f5445e16-7f3f-49c9-96ad-e78d8de00b9f",
  "fullName": "Test User",
  "email": "testuser@example.com",
  "phoneNumber": "+919876543211",
  "avatarUrl": "[https://example.com/avatar.jpg](https://example.com/avatar.jpg)"
}

```



#### 2. Update Current User Profile

* **Endpoint:** `PUT /api/v1/users/me`
* **Request Body:**
```json
{
  "fullName": "Test User Updated",
  "phoneNumber": "+919876543211",
  "avatarUrl": "[https://example.com/new-avatar.jpg](https://example.com/new-avatar.jpg)"
}

```


* **Response (`200 OK`):** Updated `UserProfileResponseDto` object.

---

### 🏠 Address APIs

* **Base Address Path:** `/api/v1/users/addresses`

#### 1. Create Address

* **Endpoint:** `POST /api/v1/users/addresses`
* **Request Body:**
```json
{
  "houseNumber": "123",
  "streetAddress": "Model Town",
  "landmark": "Near Bus Stand",
  "city": "Rewari",
  "state": "Haryana",
  "pincode": "123401",
  "addressType": "HOME",
  "latitude": 28.199,
  "longitude": 76.617,
  "isDefault": false
}

```


* **Response (`201 Created`):** Returns the created `AddressResponseDto`.

#### 2. Get All Addresses

* **Endpoint:** `GET /api/v1/users/addresses`
* **Response (`200 OK`):** Array of user addresses or an empty array `[]`.

#### 3. Get Address by ID

* **Endpoint:** `GET /api/v1/users/addresses/{addressId}`
* **Response (`200 OK`):** `AddressResponseDto`

#### 4. Update Address

* **Endpoint:** `PUT /api/v1/users/addresses/{addressId}`
* **Request Body:**
```json
{
  "houseNumber": "789",
  "streetAddress": "Model Town Updated Street",
  "landmark": "Near City Mall",
  "city": "Rewari",
  "state": "Haryana",
  "pincode": "123401",
  "addressType": "HOME",
  "latitude": 28.199,
  "longitude": 76.617,
  "isDefault": true
}

```


* **Response (`200 OK`):** Returns updated `AddressResponseDto`.

#### 5. Make Address Default

* **Endpoint:** `PATCH /api/v1/users/addresses/{addressId}/default`
* **Request Body:** *None*
* **Response (`200 OK`):** Returns updated `AddressResponseDto` with `"isDefault": true`.

#### 6. Delete Address

* **Endpoint:** `DELETE /api/v1/users/addresses/{addressId}`
* **Response (`204 No Content`):** *No response body.*

---

## 🧪 API Testing Summary

| # | Method | Endpoint | Purpose | Status |
| --- | --- | --- | --- | --- |
| 1 | `POST` | `/api/v1/users/addresses` | Create address | ✅ 201 Created |
| 2 | `GET` | `/api/v1/users/addresses` | Get all addresses | ✅ 200 OK |
| 3 | `GET` | `/api/v1/users/addresses/{id}` | Get address by ID | ✅ 200 OK |
| 4 | `PUT` | `/api/v1/users/addresses/{id}` | Update address | ✅ 200 OK |
| 5 | `PATCH` | `/api/v1/users/addresses/{id}/default` | Set default address | ✅ 200 OK |
| 6 | `DELETE` | `/api/v1/users/addresses/{id}` | Delete address | ✅ 204 No Content |
| 7 | `GET` | `/api/v1/users/me` | Get current profile | ✅ 200 OK |
| 8 | `PUT` | `/api/v1/users/me` | Update current profile | 🔄 Pending Verification |

---

## ⚠️ Error Handling & Responses

| Status Code | Reason | Example Response Payload |
| --- | --- | --- |
| **`400 Bad Request`** | Jakarta Validation Error | `json {"status": 400, "message": "Input validation failed", "validationErrors": {"isDefault": "Default status must be specified"}} ` |
| **`403 Forbidden`** | Invalid/Missing Bearer Token | Recheck security rules & header `Authorization: Bearer <JWT>` |
| **`404 Not Found`** | Resource Missing / Deleted | `"Address not found with ID: <addressId>"` |
| **`204 No Content`** | Successful Deletion | *(No body payload returned)* |

---

## 🔒 Security & Best Practices

* **Authentication Extraction:** User identity is non-spoofable. Controllers extract user identity using Spring Security's `@AuthenticationPrincipal String authUserId`, which is then safely converted into a `UUID` in business logic:
```java
UUID userId = UUID.fromString(authUserId);

```


* **Secret Protection:** Never commit JWT secrets, passwords, `.env` files, or production credentials to source control.

---

## 📊 Database Verification

Verify persisted data directly using PostgreSQL:

```sql
SELECT * FROM user_profile;
SELECT * FROM address WHERE is_default = true;

```

---

## 📜 License

This repository is maintained for internal development, learning, and platform review purposes for the **Volna Food Delivery Platform**.

```

```
