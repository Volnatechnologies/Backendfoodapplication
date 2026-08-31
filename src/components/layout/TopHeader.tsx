import React from 'react';
import { Headphones, Bell, Settings, User, Search, Menu } from 'lucide-react';

interface TopHeaderProps {
  onOpenMobileMenu: () => void;
  onOpenNotifications: () => void;
  onOpenSettings: () => void;
  onToggleProfile: () => void;
  onOpenSupport: () => void;
  unreadNotificationsCount: number;
  searchQuery: string;
  setSearchQuery: (query: string) => void;
}

export const TopHeader: React.FC<TopHeaderProps> = ({
  onOpenMobileMenu,
  onOpenNotifications,
  onOpenSettings,
  onToggleProfile,
  onOpenSupport,
  unreadNotificationsCount,
  searchQuery,
  setSearchQuery,
}) => {
  return (
    <header className="w-full flex items-center justify-between py-3 px-2 sm:px-4 mb-4">
      {/* Mobile Hamburger & Brand */}
      <div className="flex items-center space-x-3 lg:hidden">
        <button
          onClick={onOpenMobileMenu}
          className="p-2 rounded-xl bg-white/80 text-slate-700 hover:bg-white shadow-sm transition-all"
          aria-label="Open Mobile Menu"
        >
          <Menu className="w-5 h-5" />
        </button>
        <span className="font-bold text-slate-800 text-sm">Calorye Hive</span>
      </div>

      {/* Center Search Pill */}
      <div className="hidden sm:flex flex-1 max-w-xs mx-auto">
        <div className="relative w-full">
          <input
            type="text"
            value={searchQuery}
            onChange={(e) => setSearchQuery(e.target.value)}
            placeholder="Search questions..."
            className="w-full bg-[#BFE5FA] text-slate-800 placeholder-slate-500 text-xs rounded-full py-1.5 pl-8 pr-3 border border-sky-300/40 focus:outline-none focus:ring-2 focus:ring-sky-500/40 transition-all shadow-inner"
          />
          <Search className="w-3.5 h-3.5 text-slate-500 absolute left-3 top-1/2 -translate-y-1/2" />
        </div>
      </div>

      {/* Right Top Action Icons with Labels */}
      <div className="flex items-center space-x-5 sm:space-x-6 ml-auto">
        {/* Support */}
        <button
          onClick={onOpenSupport}
          className="flex flex-col items-center text-slate-700 hover:text-sky-700 transition-colors group"
          aria-label="Support Help Center"
          title="Support Center"
        >
          <div className="p-1 rounded-full group-hover:bg-white/50 transition-colors">
            <Headphones className="w-5 h-5" />
          </div>
          <span className="text-[10px] font-semibold text-slate-700 group-hover:text-sky-800 mt-0.5">Support</span>
        </button>

        {/* Notifications */}
        <button
          onClick={onOpenNotifications}
          className="relative flex flex-col items-center text-slate-700 hover:text-sky-700 transition-colors group"
          aria-label="Notifications"
          title="Notifications"
        >
          <div className="p-1 rounded-full group-hover:bg-white/50 transition-colors relative">
            <Bell className="w-5 h-5" />
            {unreadNotificationsCount > 0 && (
              <span className="absolute top-0.5 right-0.5 w-2 h-2 rounded-full bg-rose-500 ring-2 ring-[#DDF3FF] animate-pulse" />
            )}
          </div>
          <span className="text-[10px] font-semibold text-slate-700 group-hover:text-sky-800 mt-0.5">Notifications</span>
        </button>

        {/* Settings */}
        <button
          onClick={onOpenSettings}
          className="flex flex-col items-center text-slate-700 hover:text-sky-700 transition-colors group"
          aria-label="Settings"
          title="Account Settings"
        >
          <div className="p-1 rounded-full group-hover:bg-white/50 transition-colors">
            <Settings className="w-5 h-5" />
          </div>
          <span className="text-[10px] font-semibold text-slate-700 group-hover:text-sky-800 mt-0.5">Settings</span>
        </button>

        {/* Profile */}
        <button
          onClick={onToggleProfile}
          className="flex flex-col items-center text-slate-700 hover:text-sky-700 transition-colors group"
          aria-label="Profile Menu"
          title="User Profile"
        >
          <div className="p-1 rounded-full group-hover:bg-white/50 transition-colors">
            <User className="w-5 h-5" />
          </div>
          <span className="text-[10px] font-semibold text-slate-700 group-hover:text-sky-800 mt-0.5">Profile</span>
        </button>
      </div>
    </header>
  );
};
