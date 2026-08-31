import { Bell, Headphones, Menu, Search, Settings, UserCircle } from "lucide-react";

export default function Topbar({ onMenuClick }) {
  return (
    <header className="topbar">
      <button className="mobile-menu-button" onClick={onMenuClick}>
        <Menu size={22} />
      </button>

      <div className="search-box">
        <Search size={15} />
        <input placeholder="Search activities..." />
      </div>

      <div className="topbar-actions">
        <button title="Support"><Headphones size={18} /></button>
        <button title="Notifications"><Bell size={18} /></button>
        <button title="Settings"><Settings size={18} /></button>
        <button title="Profile"><UserCircle size={19} /></button>
      </div>
    </header>
  );
}
