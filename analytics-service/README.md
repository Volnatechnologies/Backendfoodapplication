# Calorye Hive - Business Analytics & Financial Backend

Production-ready Spring Boot backend for Calorye Hive Business Management Portal.

---

## 1. Modules & Capabilities

1. **Analytics & Insights (`/api/business/analytics`)**
   - Summary KPIs: Revenue ($42,500), Total Orders (1,248), AOV ($35.05), Satisfaction (4.8).
   - Dynamic Earnings Curves: 7D, 30D, and YTD aggregations.
   - Recent Transactions: Filtered and paginated customer orders with statuses.
   - Top Selling Items: Velocity, quantities, and category distribution.
   - Order Channel Split: Dine-in, Delivery, Catering breakdowns.
   - Live Operational Insights: Peak hours, peak days, kitchen turnaround times.
   - CSV Export: Streaming report export for merchant financial bookkeeping.

2. **Reviews & Feedback (`/api/business/reviews`)**
   - KPI metrics: Average Rating, Review Counts, 98% Response Rate, 92% Sentiment Index.
   - Rating Breakdown: 1-star through 5-star distribution.
   - Multi-tab Filtering: `ALL`, `WITH_PHOTOS`, `NEGATIVE_ONLY`, `UNANSWERED`, star filters.
   - Manager Response Engine: Reply creation and updates with XSS sanitization (`Jsoup`).
   - Sentiment Classification: Positive / Neutral / Negative text processing.

3. **Financial Overview (`/api/business/finance`, `/bank-accounts`, `/withdrawals`)**
   - Merchant Balances: Available for withdrawal, pending payouts, monthly target progress.
   - Revenue Trends: Daily, Weekly, and Monthly charts.
   - Verified Bank Accounts: Tokenized PCI/GLBA masked storage (`**** 4210`), default routing.
   - Idempotent Withdrawal Workflow: Pessimistic write locking on account balances, `Idempotency-Key` deduplication, audit logging.
   - Smart Capital Eligibility: Pre-approved working capital loan underwriting.

4. **Partnership Rewards & Loyalty (`/api/business/rewards`)**
   - Tier Progression: Bronze, Silver, Gold, Platinum (12,450 pts), Diamond threshold (15,000 pts).
   - Tier Perks: VIP 24/7 Support, AI Predictive Analytics, Verified Top-Tier Badges.
   - Active Objectives: Milestone progress tracking with automatic tier advancement.
   - Reward Catalog & Atomic Redemption: Pessimistic write locked redemption ledger.

---

## 2. Environment Variables

| Variable | Description | Default (Dev) | Production Example |
|---|---|---|---|
| `PORT` | HTTP server port | `8084` | `8084` |
| `SPRING_PROFILES_ACTIVE` | Active Spring profile | `dev` | `prod` |
| `DB_HOST` | PostgreSQL hostname | `localhost` | `db.production.internal` |
| `DB_PORT` | PostgreSQL port | `5432` | `5432` |
| `DB_NAME` | PostgreSQL database name | `restaurant_db` | `restaurant_prod` |
| `DB_USERNAME` | PostgreSQL username | `postgres` | `app_user` |
| `DB_PASSWORD` | PostgreSQL password | `postgres` | `StrongSecurePassword!` |
| `DB_URL` | Full JDBC connection URL | *(derived from host/port/db)* | `jdbc:postgresql://db:5432/restaurant_prod` |
| `JWT_SECRET` | 256-bit Base64 HMAC secret | *(dev secret)* | `[Secure-Random-Base64-Key]` |
| `JWT_EXPIRATION_MS` | JWT validity duration (ms) | `86400000` (24h) | `86400000` |
| `CORS_ALLOWED_ORIGINS` | Allowed frontend origins (comma-delimited) | `http://localhost:5173,http://localhost:3000` | `https://business.caloryehive.com` |

---

## 3. Build & Test Commands

### Run Automated Test Suite
```bash
mvn test
```

### Compile Production JAR
```bash
mvn clean package -DskipTests
```

---

## 4. Production Deployment

### Option A: Standalone Executable JAR
```bash
export SPRING_PROFILES_ACTIVE=prod
export DB_HOST=postgres-host
export DB_PORT=5432
export DB_NAME=restaurant_prod
export DB_USERNAME=restaurant_user
export DB_PASSWORD=your_secure_password
export JWT_SECRET=your_base64_secret_key
export CORS_ALLOWED_ORIGINS=https://business.caloryehive.com

java -jar target/analytics-financial-backend-0.0.1-SNAPSHOT.jar
```

### Option B: Docker Container
```dockerfile
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
COPY target/*.jar app.jar
EXPOSE 8084
ENTRYPOINT ["java", "-Djava.security.egd=file:/dev/./urandom", "-jar", "app.jar"]
```

---

## 5. API Documentation & Observability

- **OpenAPI UI**: `http://localhost:8084/swagger-ui.html`
- **OpenAPI JSON**: `http://localhost:8084/v3/api-docs`
- **Health Check**: `http://localhost:8084/actuator/health`
- **Application Metrics**: `http://localhost:8084/actuator/metrics`