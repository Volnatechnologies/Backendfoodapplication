# Test Order

1. Start PostgreSQL databases.
2. Start auth-service on 8081.
3. POST /api/v1/auth/register
4. POST /api/v1/auth/login
5. Save accessToken.
6. Start restaurant-service on 8083.
7. GET /actuator/health
8. POST /api/v1/restaurants with Bearer token.
9. GET /api/v1/restaurants/me
10. GET /api/v1/restaurants/{id}
11. PUT /api/v1/restaurants/{id}
12. Test document upload/list/delete.

Negative tests:
- No JWT -> 401
- Invalid JWT -> 401
- Invalid input -> 400
- Duplicate owner restaurant -> 400
- Duplicate restaurant email -> 400
- Invalid opening/closing time -> 400
