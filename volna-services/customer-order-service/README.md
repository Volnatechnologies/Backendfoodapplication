# Customer Order Service

Port: 8088

Responsibilities:
- Customer cart in Redis
- Menu validation through Menu Service (8089)
- Checkout preview
- Order creation and history
- Payment status
- Cancellation
- Order tracking

Important integration:
`POST /api/v1/cart/items` calls:
`GET http://localhost:8089/api/v1/menu/items/{menuItemId}`

IDs are UUIDs throughout customer-order/menu because Auth and Restaurant services use UUID identifiers.

## Customer Order APIs

GET /actuator/health
GET /api/v1/cart
POST /api/v1/cart/items
PUT /api/v1/cart/items/{menuItemId}?quantity=2
DELETE /api/v1/cart/items/{menuItemId}
DELETE /api/v1/cart
POST /api/v1/checkout/preview
POST /api/v1/orders
GET /api/v1/orders
GET /api/v1/orders/{orderId}
GET /api/v1/orders/{orderId}/tracking
POST /api/v1/orders/{orderId}/payment
POST /api/v1/orders/{orderId}/cancel

Authorization:
`Authorization: Bearer <JWT from auth-service>`

The menu GET endpoint is intentionally available for catalog/cart validation, while customer order APIs require a valid JWT.
