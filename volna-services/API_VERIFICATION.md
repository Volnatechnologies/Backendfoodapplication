# Volna Inventory + Profile API Verification

## Ports
- Auth-Service: `8081`
- Inventory-Service: `8086`
- Profile-Service: `8087`
- Inventory PostgreSQL host port: `5437`
- Profile PostgreSQL host port: `5438`

## Before testing
1. Start Auth-Service and obtain a valid JWT.
2. Use the **same JWT secret** in Inventory/Profile.
3. Insert the authenticated user's restaurant mapping:

```sql
INSERT INTO inventory_restaurant_access (restaurant_id, auth_user_id)
VALUES ('RESTAURANT_UUID', 'AUTH_USER_UUID');

INSERT INTO profile_restaurant_access (restaurant_id, auth_user_id)
VALUES ('RESTAURANT_UUID', 'AUTH_USER_UUID');
```

Use your real UUIDs.

## Header
Every business API requires:

`Authorization: Bearer <accessToken>`

## Inventory
- `GET /api/v1/inventory/dashboard`
- `GET /api/v1/inventory/dashboard/insights`
- `GET /api/v1/inventory/dashboard/expiring-soon?days=7`
- `GET /api/v1/inventory/items?page=0&size=20`
- `GET /api/v1/inventory/items?search=flour`
- `GET /api/v1/inventory/items/{id}`
- `POST /api/v1/inventory/items`
- `PUT /api/v1/inventory/items/{id}`
- `DELETE /api/v1/inventory/items/{id}`
- `PATCH /api/v1/inventory/items/{id}/stock`
- `GET /api/v1/inventory/items/low-stock`
- `GET /api/v1/inventory/items/out-of-stock`
- `GET /api/v1/inventory/vendors?page=0&size=20`
- `GET /api/v1/inventory/vendors/{id}`
- `POST /api/v1/inventory/vendors`
- `PUT /api/v1/inventory/vendors/{id}`
- `DELETE /api/v1/inventory/vendors/{id}`
- `POST /api/v1/inventory/purchase-orders`

Pagination is limited to `page >= 0` and `1 <= size <= 100`.
Expiring-soon `days` is limited to `0..365`.

## Profile
- `GET /api/v1/profile`
- `PUT /api/v1/profile`

## Public health checks
- `GET http://localhost:8086/actuator/health`
- `GET http://localhost:8087/actuator/health`

## Expected status codes
- `200` successful GET/PUT/PATCH
- `201` successful POST
- `204` successful DELETE
- `400` validation/bad request
- `401` missing/invalid JWT
- `404` resource/access mapping not found
