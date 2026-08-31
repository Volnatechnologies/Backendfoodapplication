import React from 'react';
import { Search, X } from 'lucide-react';

interface HeroSearchProps {
  searchQuery: string;
  setSearchQuery: (query: string) => void;
  onSearchSubmit: (e: React.FormEvent) => void;
  filteredCount?: number;
}

export const HeroSearch: React.FC<HeroSearchProps> = ({
  searchQuery,
  setSearchQuery,
  onSearchSubmit,
  filteredCount,
}) => {
  return (
    <section className="relative w-full rounded-3xl overflow-hidden mb-8 shadow-2xl">
      {/* Background Image & Ambient Lighting Overlay */}
      <div 
        className="absolute inset-0 bg-cover bg-center transition-transform duration-700 hover:scale-105"
        style={{
          backgroundImage: `url('https://images.unsplash.com/photo-1517248135467-4c7edcad34c4?auto=format&fit=crop&w=1600&q=80')`,
        }}
      />
      <div className="absolute inset-0 bg-gradient-to-r from-black/85 via-black/75 to-black/60 backdrop-blur-[2px]" />

      {/* Content Container */}
      <div className="relative z-10 py-12 px-6 sm:px-12 md:py-16 text-center max-w-3xl mx-auto flex flex-col items-center">
        <h2 className="text-2xl sm:text-3xl md:text-4xl font-extrabold text-white tracking-tight mb-3 leading-tight drop-shadow-md">
          How can we help your business thrive?
        </h2>
        <p className="text-xs sm:text-sm text-slate-200 font-normal mb-8 max-w-xl leading-relaxed drop-shadow">
          Search our knowledge base or browse categories below for immediate assistance.
        </p>

        {/* Large White Floating Search Bar */}
        <form
          onSubmit={onSearchSubmit}
          className="w-full max-w-xl bg-white rounded-full p-1.5 pl-5 shadow-2xl flex items-center border border-white/40 focus-within:ring-4 focus-within:ring-sky-400/40 transition-all duration-300"
        >
          <Search className="w-5 h-5 text-slate-400 mr-3 shrink-0" />
          <input
            type="text"
            value={searchQuery}
            onChange={(e) => setSearchQuery(e.target.value)}
            placeholder="Search for answers, guides, and more..."
            className="w-full text-slate-800 placeholder-slate-400 text-xs sm:text-sm font-medium bg-transparent border-none focus:outline-none pr-2"
          />

          {searchQuery && (
            <button
              type="button"
              onClick={() => setSearchQuery('')}
              className="p-1 rounded-full text-slate-400 hover:text-slate-600 hover:bg-slate-100 mr-1 transition-colors"
              aria-label="Clear Search"
            >
              <X className="w-4 h-4" />
            </button>
          )}

          <button
            type="submit"
            className="bg-slate-950 hover:bg-black text-white font-semibold text-xs sm:text-sm px-6 py-2.5 rounded-full transition-all duration-200 shrink-0 hover:shadow-lg active:scale-95"
          >
            Search
          </button>
        </form>

        {searchQuery && (
          <div className="mt-3 inline-flex items-center space-x-2 bg-white/20 backdrop-blur-md px-3 py-1 rounded-full border border-white/20 text-white text-xs">
            <span>Filtering by: &quot;{searchQuery}&quot;</span>
            <span className="bg-sky-500 text-white px-2 py-0.5 rounded-full text-[10px] font-bold">
              {filteredCount} results
            </span>
          </div>
        )}
      </div>
    </section>
  );
};
