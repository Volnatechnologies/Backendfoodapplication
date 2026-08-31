# Volna Restaurant Dashboard — React + JavaScript

This frontend is the React/Vite dashboard implementation for the Volna restaurant business account.

## Stack

- React
- JavaScript
- Vite
- React Router
- Axios
- Lucide React icons
- CSS

## Backend expected

Restaurant Service:

`http://localhost:8083/api/v1`

Auth Service:

`http://localhost:8081/api/v1`

The dashboard calls:

`GET /restaurants/dashboard`

with:

`Authorization: Bearer <JWT>`

Order status calls:

`PATCH /restaurants/dashboard/orders/{orderId}/status`

## Run

Install Node.js LTS first.

Then:

```bash
npm install
npm run dev
```

Open:

`http://localhost:5173`

## Existing authentication

The project assumes your restaurant login backend is already completed.

The frontend stores the JWT in:

`localStorage -> volna_access_token`

The Axios interceptor automatically sends:

`Authorization: Bearer <token>`

to the Restaurant Service.

## Important: adapt login response if needed

`src/context/AuthContext.jsx`

currently supports:

```json
{ "token": "..." }
```

or:

```json
{ "accessToken": "..." }
```

or:

```json
{ "jwt": "..." }
```

If your existing Spring Boot login response uses another field, change that one line.

## Dashboard API response expected

The dashboard page expects these fields:

```json
{
  "restaurantId": "uuid",
  "greetingName": "Restaurant Owner",
  "activeOrders": 12,
  "todayRevenue": 4280.50,
  "revenueChangePercent": 12.5,
  "todayOrderCount": 12,
  "averageRating": 4.9,
  "platinumPoints": 8450,
  "monthlyGoal": 150000,
  "monthlyRevenue": 120000,
  "monthlyGoalPercent": 80,
  "liveOrders": [],
  "activePromotions": [],
  "recentMessages": [],
  "coupons": []
}
```

The Java DTOs created for the backend already follow this contract.

## Figma mapping

- Left red sidebar -> `Sidebar.jsx`
- Search/top icons -> `Topbar.jsx`
- Welcome banner -> `Dashboard.jsx`
- Four statistic cards -> `StatCard.jsx`
- Coupon tracker -> `Dashboard.jsx`
- Order card -> `Dashboard.jsx`
- Monthly goal -> `Dashboard.jsx`
- Promotion card -> `Dashboard.jsx`
- Live Orders -> `RecentOrders.jsx` + `OrderTable.jsx`
- Recent Messages -> `Dashboard.jsx`

## Development seed

If your backend contains the development-only seed API:

`POST /restaurants/dashboard/dev/seed`

you can run it from the UI after the dashboard API returns an error because there is no demo data.

The button is shown inside the dashboard error banner.

Remove/disable this feature before production.

## Recommended next work

1. Connect the exact existing restaurant login response.
2. Connect dashboard API and verify in browser Network tab.
3. Connect Orders page to the real Order Service.
4. Connect Menu page to Menu Service.
5. Add Analytics API and chart data.
6. Add promotions/catering/menu quick actions.
7. Add responsive/mobile polish.
8. Remove development seed functionality before production.
