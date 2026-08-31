import { useEffect, useState } from "react";
import Loader from "../../components/common/Loader";
import OrderTable from "../../components/dashboard/OrderTable";
import { getRestaurantDashboard } from "../../services/restaurantService";
import { updateOrderStatus } from "../../services/orderService";

export default function Orders() {
  const [orders, setOrders] = useState([]);
  const [loading, setLoading] = useState(true);

  async function load() {
    setLoading(true);
    try {
      const data = await getRestaurantDashboard();
      setOrders(data.liveOrders || []);
    } finally {
      setLoading(false);
    }
  }

  useEffect(() => { load(); }, []);

  async function update(id, status) {
    await updateOrderStatus(id, status);
    await load();
  }

  if (loading) return <Loader text="Loading orders..." />;

  return (
    <div className="simple-page">
      <div className="simple-page-header">
        <div>
          <span className="eyebrow">RESTAURANT OPERATIONS</span>
          <h1>Orders</h1>
          <p>Monitor and update active restaurant orders.</p>
        </div>
      </div>
      <div className="dashboard-card page-card">
        <OrderTable orders={orders} onStatusChange={update} />
      </div>
    </div>
  );
}
