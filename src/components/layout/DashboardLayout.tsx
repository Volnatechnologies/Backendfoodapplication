import React from 'react';
import { Sidebar } from './Sidebar';
import { TopHeader } from './TopHeader';

interface DashboardLayoutProps {
  children: React.ReactNode;
  activeNav: string;
  setActiveNav: (id: string) => void;
  mobileOpen: boolean;
  setMobileOpen: (open: boolean) => void;
  onOpenNotifications: () => void;
  onOpenSettings: () => void;
  onToggleProfile: () => void;
  onOpenSupport: () => void;
  onUpgradeClick: () => void;
  unreadNotificationsCount: number;
  searchQuery: string;
  setSearchQuery: (query: string) => void;
}

export const DashboardLayout: React.FC<DashboardLayoutProps> = ({
  children,
  activeNav,
  setActiveNav,
  mobileOpen,
  setMobileOpen,
  onOpenNotifications,
  onOpenSettings,
  onToggleProfile,
  onOpenSupport,
  onUpgradeClick,
  unreadNotificationsCount,
  searchQuery,
  setSearchQuery,
}) => {
  return (
    <div className="min-h-screen bg-[#DDF3FF] font-sans antialiased text-slate-900 selection:bg-sky-200">
      {/* Sidebar Navigation */}
      <Sidebar
        activeNav={activeNav}
        setActiveNav={setActiveNav}
        mobileOpen={mobileOpen}
        setMobileOpen={setMobileOpen}
        onUpgradeClick={onUpgradeClick}
      />

      {/* Main Right Content Area */}
      <div className="lg:pl-64 flex flex-col min-h-screen transition-all duration-300">
        <div className="w-full max-w-6xl mx-auto px-4 sm:px-6 lg:px-8 py-4 flex-1">
          {/* Top Header */}
          <TopHeader
            onOpenMobileMenu={() => setMobileOpen(true)}
            onOpenNotifications={onOpenNotifications}
            onOpenSettings={onOpenSettings}
            onToggleProfile={onToggleProfile}
            onOpenSupport={onOpenSupport}
            unreadNotificationsCount={unreadNotificationsCount}
            searchQuery={searchQuery}
            setSearchQuery={setSearchQuery}
          />

          {/* Main Dashboard Content */}
          <main className="w-full mt-2">
            {children}
          </main>
        </div>
      </div>
    </div>
  );
};
