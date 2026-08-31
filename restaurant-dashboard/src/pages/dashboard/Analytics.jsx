import RevenueChart from "../../components/dashboard/RevenueChart";

export default function Analytics() {
  return (
    <div className="simple-page">
      <span className="eyebrow">BUSINESS INSIGHTS</span>
      <h1>Analytics</h1>
      <div className="dashboard-card page-card">
        <div className="section-heading">
          <div>
            <h2>Revenue Overview</h2>
            <p>Chart placeholder ready for your analytics API.</p>
          </div>
        </div>
        <RevenueChart />
      </div>
    </div>
  );
}
