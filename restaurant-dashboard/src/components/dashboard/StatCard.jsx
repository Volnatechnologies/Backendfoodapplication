import { ArrowDown, ArrowUp, Star, Trophy, Utensils, Wallet } from "lucide-react";
import { formatCurrency } from "../../utils/formatCurrency";

const icons = {
  revenue: Wallet,
  orders: Utensils,
  rating: Star,
  points: Trophy
};

export default function StatCard({
  type,
  title,
  value,
  subtitle,
  change,
  negative = false
}) {
  const Icon = icons[type] || Wallet;

  return (
    <div className="stat-card">
      <div className="stat-card-top">
        <span>{title}</span>
        <span className="stat-icon"><Icon size={14} /></span>
      </div>

      <strong>
        {typeof value === "number" && type === "revenue"
          ? formatCurrency(value)
          : value}
      </strong>

      <div className="stat-card-bottom">
        <span>{subtitle}</span>
        {change !== undefined && (
          <span className={`stat-change ${negative ? "negative" : ""}`}>
            {negative ? <ArrowDown size={12} /> : <ArrowUp size={12} />}
            {change}
          </span>
        )}
      </div>
    </div>
  );
}
