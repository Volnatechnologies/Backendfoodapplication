# API Documentation

## Authentication

### Register user

- METHOD: POST
- URL: /api/auth/register
- Auth: No
- Request JSON:

```json
{
  "firstName": "Alice",
  "lastName": "Manager",
  "email": "alice@example.com",
  "password": "StrongPass123!"
}
```

- Response JSON:

```json
{
  "token": "jwt-token",
  "tokenType": "Bearer",
  "expiresIn": 86400000,
  "user": {
    "id": "uuid",
    "email": "alice@example.com",
    "firstName": "Alice",
    "lastName": "Manager",
    "role": "BUSINESS_OWNER"
  }
}
```

### Login

- METHOD: POST
- URL: /api/auth/login
- Auth: No
- Request JSON:

```json
{
  "email": "alice@example.com",
  "password": "StrongPass123!"
}
```

- Response: same as register

### Current user

- METHOD: GET
- URL: /api/auth/me
- Auth: Bearer token
- Response JSON:

```json
{
  "id": "uuid",
  "email": "alice@example.com",
  "firstName": "Alice",
  "lastName": "Manager",
  "role": "BUSINESS_OWNER"
}
```

## Booking APIs

### Create booking

- METHOD: POST
- URL: /api/catering/bookings
- Auth: Bearer token
- Request JSON:

```json
{
  "eventName": "TechCorp Annual Retreat",
  "eventTypeId": "uuid",
  "eventDate": "2026-10-24",
  "eventTime": "10:00:00",
  "guestCount": 150,
  "venueAddress": "123 Business Way, Suite 500, Metropolis, NY 10001",
  "specialInstructions": "Loading dock access required",
  "menuPackageId": "uuid",
  "customOptionIds": ["uuid-1", "uuid-2"]
}
```

- Response: Booking object

### Get booking

- METHOD: GET
- URL: /api/catering/bookings/{id}
- Auth: Bearer token
- Response: detailed booking summary

### Confirm booking

- METHOD: POST
- URL: /api/catering/bookings/{id}/confirm
- Auth: Bearer token
- Response:

```json
{
  "bookingCode": "CH-8821",
  "eventName": "TechCorp Annual Retreat",
  "date": "2026-10-24",
  "time": "10:00:00",
  "totalValue": 6132.36,
  "status": "CONFIRMED",
  "nextSteps": [
    "Menu Preparation",
    "Logistics Planning",
    "Final Check",
    "Delivery & Setup"
  ]
}
```

## Dashboard

### Get dashboard

- METHOD: GET
- URL: /api/catering/dashboard
- Auth: Bearer token
- Response JSON:

```json
{
  "upcomingEvents": 12,
  "totalRevenue": 24500,
  "newInquiries": 5,
  "activeBookings": [],
  "recentInquiries": [],
  "menuPackages": []
}
```

## Notifications

### Get notifications

- METHOD: GET
- URL: /api/notifications
- Auth: Bearer token

### Mark notification as read

- METHOD: PATCH
- URL: /api/notifications/{id}/read
- Auth: Bearer token

## Inquiries

### Get inquiries

- METHOD: GET
- URL: /api/catering/inquiries
- Auth: Bearer token

### Mark inquiry read

- METHOD: PATCH
- URL: /api/catering/inquiries/{id}/read
- Auth: Bearer token

## Error handling

The API returns consistent error payloads:

```json
{
  "timestamp": "2026-09-08T00:00:00Z",
  "status": 400,
  "error": "VALIDATION_ERROR",
  "message": "Guest count must be greater than 0",
  "path": "/api/catering/bookings"
}
```
