import { ArrowRight } from "lucide-react";
import OrderTable from "./OrderTable";

export default function RecentOrders({ orders, onViewAll, onStatusChange }) {
  return (
    <section className="dashboard-section">
      <div className="section-heading">
        <div>
          <h2>Live Orders</h2>
        </div>
        <button className="text-link" onClick={onViewAll}>
          View All Orders <ArrowRight size={13} />
        </button>
      </div>
      <OrderTable orders={orders} onStatusChange={onStatusChange} />
    </section>
  );
}
