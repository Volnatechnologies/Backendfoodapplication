# Postman Test Flow

Base URL:

```text
http://localhost:8085
```

Every protected request requires:

```text
Authorization: Bearer <JWT_FROM_AUTH_SERVICE>
```

The JWT must contain:

```text
sub = authenticated user id
roles = ["RESTAURANT_OWNER"]
```

## 1. Health

```http
GET /actuator/health
```

## 2. Create package

```http
POST /api/v1/catering/packages
Content-Type: application/json
Authorization: Bearer <token>
```

```json
{
  "name": "Corporate Bites",
  "description": "Assorted canapes, light sandwiches, and beverage station.",
  "pricePerGuest": 45,
  "maxGuests": 500,
  "active": true
}
```

## 3. Create second package

```json
{
  "name": "Premium Plated",
  "description": "3-course plated meal with premium choices.",
  "pricePerGuest": 120,
  "maxGuests": 300,
  "active": true
}
```

## 4. List packages

```http
GET /api/v1/catering/packages
```

Save a returned package id.

## 5. Create booking

```http
POST /api/v1/catering/events
```

```json
{
  "eventName": "TechCorp Annual Retreat",
  "eventType": "Corporate Buffet",
  "eventDate": "2026-10-24",
  "eventTime": "10:00:00",
  "guestCount": 150,
  "venueAddress": "123 Business Way, Suite 500",
  "specialInstructions": "Loading dock access required for delivery vehicles.",
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

## 6. Get booking

```http
GET /api/v1/catering/events/{id}
```

## 7. Confirm booking

```http
POST /api/v1/catering/events/{id}/confirm
```

## 8. Dashboard

```http
GET /api/v1/catering/dashboard
```

## 9. Create inquiry

```http
POST /api/v1/catering/inquiries
```

```json
{
  "name": "John Smith",
  "email": "john@example.com",
  "phone": "9876543210",
  "eventType": "Wedding",
  "eventDate": "2026-11-15",
  "guestCount": 120,
  "message": "Interested in premium plated catering."
}
```

## 10. Update inquiry

```http
PATCH /api/v1/catering/inquiries/{id}/status
```

```json
{
  "status": "CONTACTED"
}
```
