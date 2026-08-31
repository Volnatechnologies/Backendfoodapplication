import { useEffect, useMemo, useState } from "react";
import { ArrowRight, CheckCircle2, ChevronRight, CircleHelp, Copy, Plus, RefreshCw, XCircle } from "lucide-react";
import { useNavigate } from "react-router-dom";
import Loader from "../../components/common/Loader";
import StatCard from "../../components/dashboard/StatCard";
import RecentOrders from "../../components/dashboard/RecentOrders";
import { getRestaurantDashboard, seedDashboardDemoData } from "../../services/restaurantService";
import { updateOrderStatus } from "../../services/orderService";
import { formatCurrency } from "../../utils/formatCurrency";

export default function Dashboard() {
  const navigate = useNavigate();
  const [data, setData] = useState(null);
  const [loading, setLoading] = useState(true);
  const [refreshing, setRefreshing] = useState(false);
  const [error, setError] = useState("");
  const [seeding, setSeeding] = useState(false);

  async function loadDashboard(showSpinner = false) {
    if (showSpinner) setRefreshing(true);
    else setLoading(true);

    try {
      setError("");
      const result = await getRestaurantDashboard();
      setData(result);
    } catch (err) {
      setError(
        err.response?.data?.message ||
        err.response?.data?.error ||
        err.message ||
        "Unable to load dashboard"
      );
    } finally {
      setLoading(false);
      setRefreshing(false);
    }
  }

  useEffect(() => {
    loadDashboard();
  }, []);

  async function seedDemo() {
    try {
      setSeeding(true);
      await seedDashboardDemoData();
      await loadDashboard(true);
    } catch (err) {
      setError(err.response?.data?.message || err.message || "Demo seed failed");
    } finally {
      setSeeding(false);
    }
  }

  async function handleOrderStatus(orderId, status) {
    try {
      await updateOrderStatus(orderId, status);
      await loadDashboard(true);
    } catch (err) {
      setError(err.response?.data?.message || err.message || "Order update failed");
    }
  }

 const metrics = data?.metrics || {};
const restaurant = data?.restaurant || {};
const profile = data?.profile || {};
const documents = data?.documents || {};

const firstOrder = data?.liveOrders?.[0];
const progress = Number(profile?.completionPercentage || 0);

  const greeting = useMemo(() => {
    const hour = new Date().getHours();
    if (hour < 12) return "Good Morning";
    if (hour < 17) return "Good Afternoon";
    return "Good Evening";
  }, []);

  if (loading) return <Loader />;

  return (
    <div className="dashboard-page">
      <div className="page-header-row">
        <div className="welcome-banner">
          <span className="eyebrow">Restaurant Business Account</span>
          <h1>{greeting}, {restaurant?.name || "Restaurant Owner"}</h1>
          <p>You have {metrics?.activeOrders || 0} active orders requiring attention.</p>
        </div>

        <div className="header-actions">
          <button className="round-action" onClick={() => loadDashboard(true)} title="Refresh">
            <RefreshCw size={16} className={refreshing ? "spin" : ""} />
          </button>
          <button className="quick-add" onClick={() => alert("Promotion creation flow")}>
            <Plus size={15} /> New Promotion
          </button>
          <button className="quick-add" onClick={() => alert("Catering creation flow")}>
            <Plus size={15} /> Add Catering
          </button>
          <button className="quick-add" onClick={() => navigate("/menu")}>
            <Plus size={15} /> Add Menu
          </button>
        </div>
      </div>

      {error && (
        <div className="error-banner">
          <span>{error}</span>
          <div>
            <button onClick={seedDemo} disabled={seeding}>
              {seeding ? "Seeding..." : "Seed Demo Data"}
            </button>
            <button onClick={() => setError("")}>×</button>
          </div>
        </div>
      )}

      <section className="stats-grid">
        <StatCard
          type="revenue"
          title="Today's Revenue"
          value={Number(metrics?.todayRevenue || 0)}
          subtitle="vs yesterday"
          change={`${Number(metrics?.revenueChangePercent || 0).toFixed(1)}%`}
        />
        <StatCard
          type="orders"
          title="Active Orders"
          value={Number(metrics?.activeOrders || 0)}
          subtitle={`${Number(metrics?.todayOrders || 0)} today`}
        />
        <StatCard
          type="rating"
          title="Average Rating"
          value={Number(metrics?.averageRating || 0).toFixed(1)}
          subtitle="Based on customer reviews"
        />
        <StatCard
          type="points"
          title="Platinum Points"
          value={(metrics?.platinumPoints || 0).toLocaleString("en-IN")}
          subtitle="Loyalty tier"
        />
      </section>

      <section className="main-dashboard-grid">
        <div className="coupon-card dashboard-card">
          <div className="section-heading">
            <div>
              <h2>Coupon Tracker</h2>
              <p>Real-time usage of unique guest codes.</p>
            </div>
            <Copy size={15} />
          </div>

          <div className="coupon-head">
            <span>Code</span><span>Status</span><span>Redemptions</span>
          </div>

          {data?.coupons?.map((coupon) => (
            <div className="coupon-row" key={coupon.code}>
              <strong>{coupon.code}</strong>
              <span className={`coupon-status ${coupon.status?.toLowerCase()}`}>
                {coupon.status}
              </span>
              <span>{coupon.redemptions} / {coupon.redemptionLimit}</span>
            </div>
          ))}

          {!data?.coupons?.length && (
            <div className="dashboard-empty">No coupons available.</div>
          )}

          <button className="generate-link" onClick={() => alert("Batch code generator")}>
            Generate Batch Code <ArrowRight size={13} />
          </button>
        </div>

        <div className="order-card dashboard-card">
          <div className="order-card-header">
            <div>
              <span className="eyebrow">NEW ORDER</span>
              <h2>Order #{firstOrder?.orderNumber || "—"}</h2>
              <p>Placed at {firstOrder?.orderTime ? new Date(firstOrder.orderTime).toLocaleTimeString([], { hour: "2-digit", minute: "2-digit" }) : "—"}</p>
            </div>
            <button className="print-button" onClick={() => window.print()}>
              Print Ticket
            </button>
          </div>

          <div className="order-card-body">
            <div>
              <h3>Order Items</h3>
              {(firstOrder?.items || []).map((item, index) => (
                <div className="item-line" key={`${item.itemName}-${index}`}>
                  <span>{item.quantity}x</span>
                  <div>
                    <strong>{item.itemName}</strong>
                    {item.variantNote && <small>{item.variantNote}</small>}
                  </div>
                  <strong>{formatCurrency(item.unitPrice)}</strong>
                </div>
              ))}
              {!firstOrder?.items?.length && (
                <div className="dashboard-empty">Select a live order to see items.</div>
              )}
            </div>

            <div className="customer-box">
              <span className="eyebrow">CUSTOMER</span>
              <strong>{firstOrder?.customerName || "No customer"}</strong>
              <small>{firstOrder?.deliveryType || "DELIVERY"}</small>
            </div>
          </div>

          <div className="order-total">
            <div>
              <span>Subtotal</span>
              <strong>{formatCurrency(firstOrder?.subtotal || 0)}</strong>
            </div>
            <div>
              <span>Tax</span>
              <strong>{formatCurrency(firstOrder?.tax || 0)}</strong>
            </div>
            <div>
              <span>Delivery Fee</span>
              <strong>{formatCurrency(firstOrder?.deliveryFee || 0)}</strong>
            </div>
            <div className="grand-total">
              <span>Total</span>
              <strong>{formatCurrency(firstOrder?.totalAmount || 0)}</strong>
            </div>
          </div>

          <div className="order-actions">
            <button
              className="decline-button"
              disabled={!firstOrder}
              onClick={() => firstOrder && handleOrderStatus(firstOrder.id, "DECLINED")}
            >
              <XCircle size={15} /> Decline
            </button>
            <button
              className="accept-button"
              disabled={!firstOrder}
              onClick={() => firstOrder && handleOrderStatus(firstOrder.id, "PREPARING")}
            >
              <CheckCircle2 size={15} /> Accept Order
            </button>
          </div>
        </div>
      </section>

      <div className="goal-promo-grid">
        <div className="goal-card dashboard-card">
          <div>
            <span className="eyebrow">MONTHLY GOAL</span>
            <h2>{progress.toFixed(0)}%</h2>
            <p>{formatCurrency(metrics?.monthlyRevenue || 0)} / {formatCurrency(metrics?.monthlyGoal || 0)}</p>
          </div>
          <div className="progress-ring" style={{ "--progress": `${progress * 3.6}deg` }}>
            <span>{progress.toFixed(0)}%</span>
          </div>
        </div>

        <div className="promotion-card dashboard-card">
          <div className="promotion-icon">↗</div>
          <div>
            <span className="eyebrow">FEATURED</span>
            <h3>{metrics?.activePromotions?.[0]?.title || "Weekend Brunch Special"}</h3>
            <p>{metrics?.activePromotions?.[0]?.description || "Create a promotion to increase revenue."}</p>
          </div>
          <div className="promotion-metrics">
            <span><strong>+{metrics?.activePromotions?.[0]?.revenueImpactPercent || 0}%</strong> Revenue</span>
            <span><strong>{metrics?.activePromotions?.[0]?.views || 0}</strong> Views</span>
          </div>
        </div>
      </div>

      <div className="bottom-dashboard-grid">
        <RecentOrders
          orders={data?.liveOrders || []}
          onViewAll={() => navigate("/orders")}
          onStatusChange={handleOrderStatus}
        />

        <section className="dashboard-section">
          <div className="section-heading">
            <h2>Recent Messages</h2>
            <button className="text-link">View All <ChevronRight size={13} /></button>
          </div>
          <div className="message-list">
            {(data?.recentMessages || []).map((message) => (
              <div className="message-item" key={message.id}>
                <div className="message-avatar">{message.senderName?.slice(0, 1) || "S"}</div>
                <div>
                  <strong>{message.senderName}</strong>
                  <p>{message.message}</p>
                </div>
              </div>
            ))}
            {!data?.recentMessages?.length && (
              <div className="dashboard-empty">No recent messages.</div>
            )}
          </div>
        </section>
      </div>

      <div className="dashboard-tip">
        <CircleHelp size={15} />
        Dashboard values are loaded from your Spring Boot Restaurant Dashboard API.
      </div>
    </div>
  );
}
