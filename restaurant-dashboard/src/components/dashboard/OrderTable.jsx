import { Clock3, PackageCheck } from "lucide-react";
import { formatCurrency } from "../../utils/formatCurrency";

function statusLabel(status) {
  return status?.replaceAll("_", " ") || "NEW";
}

export default function OrderTable({ orders = [], onStatusChange }) {
  if (!orders.length) {
    return <div className="dashboard-empty">No active orders right now.</div>;
  }

  return (
    <div className="order-list">
      {orders.map((order) => (
        <div className="order-row" key={order.id}>
          <div className="order-customer">
            <div className="order-avatar">
              {order.customerName?.slice(0, 2).toUpperCase() || "CU"}
            </div>
            <div>
              <strong>{order.customerName}</strong>
              <small>Order #{order.orderNumber} • {order.itemCount} items</small>
            </div>
          </div>

          <div className="order-meta">
            <span className="order-amount">{formatCurrency(order.totalAmount)}</span>
            <span className={`order-status status-${order.status?.toLowerCase()}`}>
              {statusLabel(order.status)}
            </span>
          </div>

          {onStatusChange && (
            <button
              className="small-action"
              title="Mark ready"
              onClick={() => onStatusChange(order.id, "READY")}
            >
              <PackageCheck size={14} />
            </button>
          )}
        </div>
      ))}
    </div>
  );
}
