import React from 'react';
import {
  LayoutGrid,
  Calendar,
  LineChart,
  SlidersHorizontal,
  Activity,
  MessageSquare,
  CreditCard,
  User,
  HelpCircle,
  LogOut,
  X,
  UtensilsCrossed
} from 'lucide-react';
import { navigationItems } from '../../data/mockData';

interface SidebarProps {
  activeNav: string;
  setActiveNav: (id: string) => void;
  mobileOpen: boolean;
  setMobileOpen: (open: boolean) => void;
  onUpgradeClick?: () => void;
}

const iconMap: Record<string, React.ElementType> = {
  LayoutGrid,
  Calendar,
  LineChart,
  SlidersHorizontal,
  Activity,
  MessageSquare,
  CreditCard,
  User,
  UtensilsCrossed
};

export const Sidebar: React.FC<SidebarProps> = ({
  activeNav,
  setActiveNav,
  mobileOpen,
  setMobileOpen,
  onUpgradeClick: _onUpgradeClick,
}) => {
  return (
    <>
      {/* Mobile Overlay */}
      {mobileOpen && (
        <div
          className="fixed inset-0 z-40 bg-slate-900/60 backdrop-blur-sm lg:hidden transition-opacity"
          onClick={() => setMobileOpen(false)}
        />
      )}

      {/* Sidebar Container */}
      <aside
        className={`fixed top-0 left-0 bottom-0 z-50 w-64 p-3 transition-transform duration-300 ease-in-out lg:translate-x-0 ${
          mobileOpen ? 'translate-x-0' : '-translate-x-full lg:translate-x-0'
        }`}
      >
        <div className="h-full bg-gradient-to-b from-[#8B0000] via-[#A00000] to-[#700000] text-white rounded-3xl p-4 flex flex-col justify-between shadow-xl border border-white/10 overflow-y-auto">
          {/* Top Header & Logo */}
          <div>
            <div className="flex items-center justify-between px-2 pt-1 pb-4 mb-2">
              <div className="flex items-center space-x-2.5">
                <div className="w-9 h-9 rounded-xl bg-white/15 backdrop-blur-md flex items-center justify-center border border-white/20 shadow-inner">
                  <span className="font-extrabold text-lg text-white tracking-tighter">OH</span>
                </div>
                <div>
                  <h1 className="font-bold text-sm leading-tight tracking-wide text-white">Business</h1>
                  <p className="text-[10px] text-rose-200/80 uppercase font-medium tracking-widest">Account</p>
                </div>
              </div>

              {/* Close Mobile */}
              <button
                onClick={() => setMobileOpen(false)}
                className="lg:hidden p-1.5 rounded-lg text-rose-200 hover:text-white hover:bg-white/10 transition-colors"
                aria-label="Close Sidebar"
              >
                <X className="w-5 h-5" />
              </button>
            </div>

            {/* Navigation List */}
            <nav className="space-y-1">
              {navigationItems.map((item) => {
                const IconComponent = iconMap[item.iconName] || LayoutGrid;
                const isActive = activeNav === item.id;

                return (
                  <button
                    key={item.id}
                    onClick={() => {
                      setActiveNav(item.id);
                      setMobileOpen(false);
                    }}
                    className={`w-full flex items-center space-x-3 px-3.5 py-2.5 rounded-xl text-xs font-semibold transition-all duration-200 group ${
                      isActive
                        ? 'bg-white text-[#8B0000] shadow-lg shadow-black/10 scale-[1.02]'
                        : 'text-rose-100/90 hover:bg-white/10 hover:text-white'
                    }`}
                  >
                    <IconComponent
                      className={`w-4 h-4 transition-transform duration-200 ${
                        isActive ? 'text-[#8B0000]' : 'text-rose-200 group-hover:scale-110'
                      }`}
                    />
                    <span className="tracking-wide">{item.label}</span>
                  </button>
                );
              })}
            </nav>
          </div>

          {/* Bottom Section */}
          <div className="pt-4 space-y-4">
            {/* Quick Action Links */}
            <div className="px-1 space-y-1 text-rose-100/80 text-xs">
              <a
                href="#help"
                className="flex items-center space-x-2.5 py-1.5 px-2 rounded-lg hover:text-white hover:bg-white/10 transition-colors"
              >
                <HelpCircle className="w-4 h-4 text-rose-200" />
                <span>Help</span>
              </a>
              <a
                href="#logout"
                className="flex items-center space-x-2.5 py-1.5 px-2 rounded-lg hover:text-white hover:bg-white/10 transition-colors"
              >
                <LogOut className="w-4 h-4 text-rose-200" />
                <span>Logout</span>
              </a>
            </div>
          </div>
        </div>
      </aside>
    </>
  );
};
