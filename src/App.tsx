import React, { useState, useMemo } from 'react';
import { DashboardLayout } from './components/layout/DashboardLayout';
import { HeroSearch } from './components/sections/HeroSearch';
import { GettingStartedAccordion } from './components/sections/GettingStartedAccordion';
import { FeaturedResources } from './components/sections/FeaturedResources';
import { CategoryCards } from './components/sections/CategoryCards';
import { FAQAndSupportSection } from './components/sections/FAQAndSupportSection';
import { AcademyBanner } from './components/sections/AcademyBanner';
import { Footer } from './components/sections/Footer';

import { LiveChatModal } from './components/modals/LiveChatModal';
import { CallbackModal } from './components/modals/CallbackModal';
import { NotificationPanel } from './components/modals/NotificationPanel';
import { SettingsPanel } from './components/modals/SettingsPanel';
import { ProfileDropdown } from './components/modals/ProfileDropdown';

import { gettingStartedFaqs, generalFaqs, mockNotifications } from './data/mockData';
import type { NotificationItem } from './types/dashboard';
import { SearchX } from 'lucide-react';

export function App() {
  const [activeNav, setActiveNav] = useState('services');
  const [mobileOpen, setMobileOpen] = useState(false);
  const [searchQuery, setSearchQuery] = useState('');

  // Modals & Panels
  const [isChatOpen, setIsChatOpen] = useState(false);
  const [isCallbackOpen, setIsCallbackOpen] = useState(false);
  const [isNotificationsOpen, setIsNotificationsOpen] = useState(false);
  const [isSettingsOpen, setIsSettingsOpen] = useState(false);
  const [isProfileOpen, setIsProfileOpen] = useState(false);

  // Notifications State
  const [notifications, setNotifications] = useState<NotificationItem[]>(mockNotifications);
  const unreadCount = useMemo(
    () => notifications.filter((n) => n.unread).length,
    [notifications]
  );

  const handleClearNotifications = () => {
    setNotifications((prev) => prev.map((n) => ({ ...n, unread: false })));
  };

  // Real-time Search Filtering
  const filteredGettingStarted = useMemo(() => {
    if (!searchQuery.trim()) return gettingStartedFaqs;
    const q = searchQuery.toLowerCase();
    return gettingStartedFaqs.filter(
      (item) => item.question.toLowerCase().includes(q) || item.answer.toLowerCase().includes(q)
    );
  }, [searchQuery]);

  const filteredGeneralFaqs = useMemo(() => {
    if (!searchQuery.trim()) return generalFaqs;
    const q = searchQuery.toLowerCase();
    return generalFaqs.filter(
      (item) => item.question.toLowerCase().includes(q) || item.answer.toLowerCase().includes(q)
    );
  }, [searchQuery]);

  const totalFilteredCount = filteredGettingStarted.length + filteredGeneralFaqs.length;

  const handleSearchSubmit = (e: React.FormEvent) => {
    e.preventDefault();
  };

  return (
    <DashboardLayout
      activeNav={activeNav}
      setActiveNav={setActiveNav}
      mobileOpen={mobileOpen}
      setMobileOpen={setMobileOpen}
      onOpenNotifications={() => setIsNotificationsOpen(!isNotificationsOpen)}
      onOpenSettings={() => setIsSettingsOpen(true)}
      onToggleProfile={() => setIsProfileOpen(!isProfileOpen)}
      onOpenSupport={() => setIsChatOpen(true)}
      onUpgradeClick={() => setIsSettingsOpen(true)}
      unreadNotificationsCount={unreadCount}
      searchQuery={searchQuery}
      setSearchQuery={setSearchQuery}
    >
      {/* Top Header Overlay Dropdowns */}
      <div className="relative">
        <NotificationPanel
          isOpen={isNotificationsOpen}
          onClose={() => setIsNotificationsOpen(false)}
          notifications={notifications}
          onClearAll={handleClearNotifications}
        />
        <ProfileDropdown
          isOpen={isProfileOpen}
          onClose={() => setIsProfileOpen(false)}
          onOpenSettings={() => setIsSettingsOpen(true)}
        />
      </div>

      {/* 1. HERO SEARCH SECTION */}
      <HeroSearch
        searchQuery={searchQuery}
        setSearchQuery={setSearchQuery}
        onSearchSubmit={handleSearchSubmit}
        filteredCount={totalFilteredCount}
      />

      {/* SEARCH EMPTY STATE IF NOTHING MATCHES */}
      {searchQuery && totalFilteredCount === 0 ? (
        <div className="bg-white rounded-3xl p-10 text-center my-8 shadow-soft-card border border-slate-100 max-w-lg mx-auto">
          <SearchX className="w-12 h-12 text-slate-400 mx-auto mb-3" />
          <h3 className="font-extrabold text-lg text-slate-900 mb-1">No articles found</h3>
          <p className="text-xs text-slate-600 mb-5">
            We couldn&apos;t find any results matching &quot;<span className="font-semibold">{searchQuery}</span>&quot;.
          </p>
          <button
            onClick={() => setSearchQuery('')}
            className="bg-[#0B6799] text-white font-bold text-xs px-5 py-2.5 rounded-full hover:bg-[#08527C] transition-all"
          >
            Clear Search Filter
          </button>
        </div>
      ) : (
        <>
          {/* 2. GETTING STARTED SECTION */}
          <GettingStartedAccordion items={filteredGettingStarted} />

          {/* 3. FEATURED RESOURCE CARDS */}
          <FeaturedResources
            onSelectResource={(title) => {
              setSearchQuery(title);
            }}
          />

          {/* 4. CATEGORY CARDS */}
          <CategoryCards
            onCategoryClick={(title) => {
              setSearchQuery(title);
            }}
          />

          {/* 5. FAQ & NEED MORE HELP SECTION */}
          <FAQAndSupportSection
            faqs={filteredGeneralFaqs}
            onOpenChat={() => setIsChatOpen(true)}
            onOpenCallback={() => setIsCallbackOpen(true)}
          />

          {/* 6. ELITE ACADEMY BANNER */}
          <AcademyBanner onStartLearning={() => setIsChatOpen(true)} />
        </>
      )}

      {/* 7. FOOTER */}
      <Footer />

      {/* Interactive Modals */}
      <LiveChatModal isOpen={isChatOpen} onClose={() => setIsChatOpen(false)} />
      <CallbackModal isOpen={isCallbackOpen} onClose={() => setIsCallbackOpen(false)} />
      <SettingsPanel isOpen={isSettingsOpen} onClose={() => setIsSettingsOpen(false)} />
    </DashboardLayout>
  );
}

export default App;
