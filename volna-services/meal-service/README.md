

## Delivery / fulfillment metric

Meal Service also stores delivery records. This makes the dashboard fulfillment
rate data-driven instead of hardcoded.

Create a delivery:

`POST /api/v1/meals/deliveries?planId={planId}&subscriptionId={subscriptionId}&scheduledDate=2026-09-07`

Update it:

`PATCH /api/v1/meals/deliveries/{deliveryId}/status`

Example:

```json
{
  "status": "DELIVERED",
  "failureReason": null
}
```

The dashboard calculates fulfillment over the previous 30 days as:

`DELIVERED / (DELIVERED + FAILED) * 100`

The existing Active Orders implementation remains a separate order concern.
Meal Service owns meal-plan fulfillment records and can later consume order events
from Order Service rather than querying another service's database.
