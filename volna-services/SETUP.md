# Quick setup for the two new services

## 1. PostgreSQL

Using local PostgreSQL, create:

```sql
CREATE DATABASE inventory_db;
CREATE DATABASE profile_db;
```

If your PostgreSQL server uses different ports/passwords, set the Spring datasource environment variables accordingly.

## 2. JWT secret

Copy the exact JWT signing secret already used by the existing Auth-Service. Do not generate a different secret for these services.

PowerShell example:

```powershell
$env:JWT_SECRET="YOUR_EXISTING_AUTH_SERVICE_SECRET"
```

## 3. Restaurant access mapping

After the first Flyway migration runs, get the Auth-Service user UUID from the JWT `sub` and the restaurant UUID from Restaurant-Service. Then run:

```sql
INSERT INTO inventory_restaurant_access (restaurant_id, auth_user_id)
VALUES ('RESTAURANT_UUID', 'AUTH_USER_UUID');

INSERT INTO profile_restaurant_access (restaurant_id, auth_user_id)
VALUES ('RESTAURANT_UUID', 'AUTH_USER_UUID');
```

## 4. Start

```text
cd inventory-service
mvn spring-boot:run
```

In another terminal:

```text
cd profile-service
mvn spring-boot:run
```

## 5. Test security first

Without a token, a business endpoint should return `401`.

With the Auth-Service access token:

```text
Authorization: Bearer <accessToken>
```

The APIs should then resolve the restaurant through the local access mapping.
