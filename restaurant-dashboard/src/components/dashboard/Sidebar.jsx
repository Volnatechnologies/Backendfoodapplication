import { NavLink } from "react-router-dom";
import {
  BarChart3,
  ClipboardList,
  Gift,
  Home,
  LogOut,
  Megaphone,
  Menu as MenuIcon,
  MessageSquare,
  Package,
  Settings,
  ShoppingBag,
  Star,
  Store,
  UserRound,
  UsersRound,
  WalletCards
} from "lucide-react";
import useAuth from "../../hooks/useAuth";

const links = [
  { label: "Dashboard", to: "/dashboard", icon: Home },
  { label: "Orders", to: "/orders", icon: ClipboardList },
  { label: "Deliveries", to: "/orders", icon: Package },
  { label: "Menu", to: "/menu", icon: MenuIcon },
  { label: "Analytics", to: "/analytics", icon: BarChart3 },
  { label: "Loyalty", to: "/dashboard", icon: Gift },
  { label: "Catering", to: "/dashboard", icon: Store },
  { label: "Marketing", to: "/dashboard", icon: Megaphone },
  { label: "Offers", to: "/dashboard", icon: Gift },
  { label: "Staff", to: "/dashboard", icon: UsersRound },
  { label: "Shift", to: "/dashboard", icon: WalletCards },
  { label: "Reviews", to: "/dashboard", icon: Star },
  { label: "Transactions", to: "/dashboard", icon: WalletCards },
  { label: "Inventory", to: "/dashboard", icon: ShoppingBag },
  { label: "Profile", to: "/profile", icon: UserRound }
];

export default function Sidebar({ mobileOpen, onClose }) {
  const { logout } = useAuth();

  return (
    <aside className={`sidebar ${mobileOpen ? "sidebar-open" : ""}`}>
      <div className="sidebar-brand">
        <div className="brand-mark">2H</div>
        <div>
          <strong>Business</strong>
          <span>Account</span>
        </div>
      </div>

      <div className="active-now">● ACTIVE NOW</div>

      <nav className="sidebar-nav">
        {links.map(({ label, to, icon: Icon }) => (
          <NavLink
            key={label}
            to={to}
            onClick={onClose}
            className={({ isActive }) =>
              `sidebar-link ${isActive ? "active" : ""}`
            }
          >
            <Icon size={15} />
            <span>{label}</span>
          </NavLink>
        ))}
      </nav>

      <div className="sidebar-bottom">
        <button className="withdraw-button" onClick={() => alert("Withdraw Funds flow will be connected later.")}>
          <WalletCards size={15} />
          Withdraw Funds
        </button>

        <button className="sidebar-bottom-link">
          <MessageSquare size={15} />
          Help
        </button>

        <button className="sidebar-bottom-link" onClick={logout}>
          <LogOut size={15} />
          Logout
        </button>
      </div>
    </aside>
  );
}
