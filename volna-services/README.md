# Volna Inventory + Profile Backend Services

Separate Spring Boot 3.5.6 / Java 21 services designed to fit the existing Volna Food Delivery backend history.

## Services

| Service | Port | Database | Responsibility |
|---|---:|---|---|
| Auth-Service (existing) | 8081 | auth_db | Login/JWT/roles |
| User-Service (existing) | 8082 | user_db | Customer profile/address |
| Restaurant-Service (existing) | 8083 | restaurant_db | Restaurant onboarding/status |
| Meal-Service (existing) | 8084 | meal_db | Meal management |
| Catering-Service (existing) | 8085 | catering_db | Catering management |
| Inventory-Service (new) | 8086 | inventory_db | Stock, vendors, purchase orders, dashboard |
| Profile-Service (new) | 8087 | profile_db | Restaurant profile section |

**Important:** Profile-Service is intentionally separate. Nothing is added to Restaurant-Service.

## Architecture decisions

- Java 21 and Spring Boot 3.5.6.
- PostgreSQL database-per-service.
- Flyway migrations.
- JWT validation uses the same `app.jwt.secret` convention as the existing Auth/Restaurant services.
- APIs require authentication; only Actuator health/info are public.
- JWT `sub` is treated as the Auth-Service user UUID.
- Because the existing JWT does not carry `restaurantId`, each new service has its own `*_restaurant_access` mapping table. This avoids changing the already-built Auth, Restaurant, Meal and Catering services.
- Controllers are thin; business logic lives in services.
- Redis, Kafka, Spring Cloud and Kubernetes are not required for these two local MVP services. They can be introduced later when the wider platform needs caching, events/service discovery or production orchestration.
- Images are represented by `logoUrl`; large binary files should later be stored in object storage, not PostgreSQL.

## Inventory section mapped from the dashboard/UI work

### Dashboard
- Total inventory value
- Total items
- Low-stock count
- Out-of-stock count
- Expiring-soon count
- Rule-based replenishment insights

### Items
- Search + pagination
- Create/update/delete
- Stock adjustment
- Low-stock and out-of-stock views
- SKU, category, unit, minimum stock, unit cost, expiry date and supplier

### Vendors
- CRUD
- Contact details
- Active/inactive state

### Purchase orders
- Create order against a restaurant-owned vendor
- Add restaurant-owned inventory items
- Calculate estimated total on the server

## Inventory API

Base URL: `http://localhost:8086`

- GET `/api/v1/inventory/dashboard`
- GET `/api/v1/inventory/dashboard/insights`
- GET `/api/v1/inventory/dashboard/expiring-soon?days=7`
- GET `/api/v1/inventory/items?page=0&size=20`
- GET `/api/v1/inventory/items?search=flour`
- GET `/api/v1/inventory/items/{id}`
- POST `/api/v1/inventory/items`
- PUT `/api/v1/inventory/items/{id}`
- DELETE `/api/v1/inventory/items/{id}`
- PATCH `/api/v1/inventory/items/{id}/stock`
- GET `/api/v1/inventory/items/low-stock`
- GET `/api/v1/inventory/items/out-of-stock`
- GET `/api/v1/inventory/vendors?page=0&size=20`
- GET `/api/v1/inventory/vendors/{id}`
- POST `/api/v1/inventory/vendors`
- PUT `/api/v1/inventory/vendors/{id}`
- DELETE `/api/v1/inventory/vendors/{id}`
- POST `/api/v1/inventory/purchase-orders`

## Profile section

Base URL: `http://localhost:8087`

- GET `/api/v1/profile`
- PUT `/api/v1/profile`

Profile fields cover the restaurant profile UI concept: restaurant name, cuisine type, description, phone, email, website, address, city, state, postal code, country, logo URL, opening/closing time and active status.

## First-time local setup

1. Create PostgreSQL databases `inventory_db` and `profile_db`.
2. Inventory defaults to PostgreSQL `localhost:5437`; Profile defaults to `localhost:5438`.
3. Put the **exact same JWT signing secret used by Auth-Service** in `JWT_SECRET`.
4. Run each service with Maven from its own folder.
5. Before testing business endpoints, insert the authenticated user's restaurant mapping in each service database.

Example mapping SQL (replace both UUID values):

```sql
INSERT INTO inventory_restaurant_access (restaurant_id, auth_user_id)
VALUES ('RESTAURANT_UUID', 'AUTH_USER_UUID');

INSERT INTO profile_restaurant_access (restaurant_id, auth_user_id)
VALUES ('RESTAURANT_UUID', 'AUTH_USER_UUID');
```

The `auth_user_id` must equal the JWT `sub`. The `restaurant_id` must be the UUID of the restaurant belonging to that owner. This mapping is deliberately local to each service so there is no direct cross-service database access.

## Build

```text
cd inventory-service
mvn clean package -DskipTests

cd ../profile-service
mvn clean package -DskipTests
```

Then, from the bundle root:

```text
docker compose up --build
```

The Dockerfiles expect the Maven JARs in each service's `target` directory.

## Security

Do not commit the real JWT secret. Do not make inventory/profile APIs `permitAll()`. The frontend must send:

```text
Authorization: Bearer <accessToken>
```

The access token should be obtained from the existing Auth-Service.

## UI screenshots/history used as design input

The inventory work follows the previously discussed restaurant dashboard Inventory Management concepts and the Profile dashboard section. The implementation keeps the Profile section independent from Restaurant-Service, as requested.

## Existing services remain untouched

This bundle does not replace or modify the existing Auth-Service, User-Service, Restaurant-Service, Meal-Service, Catering-Service or restaurant dashboard. It is intended to be copied into the wider backend as two new top-level service folders.
