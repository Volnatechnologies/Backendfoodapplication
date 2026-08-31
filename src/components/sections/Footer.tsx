import React from 'react';
import { Globe, Share2, Layers } from 'lucide-react';

export const Footer: React.FC = () => {
  return (
    <footer className="w-full pt-12 pb-8 border-t border-sky-200/60 text-slate-700">
      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-12 gap-8 mb-12 text-left">
        {/* Column 1: Brand & Description (5 cols) */}
        <div className="lg:col-span-5 space-y-4">
          <h4 className="font-extrabold text-sm tracking-wider uppercase text-slate-900">
            CALORYE HIVE BUSINESS
          </h4>
          <p className="text-xs text-slate-600 leading-relaxed max-w-sm">
            Advancing atmospheric precision in temporal management. We create digital environments that respect the fluidity of human attention and the rigidity of time.
          </p>

          {/* Social Icons */}
          <div className="flex items-center space-x-3 pt-1">
            <a
              href="#dribbble"
              aria-label="Website"
              className="w-8 h-8 rounded-full bg-slate-900 text-white flex items-center justify-center hover:bg-[#0B6799] transition-colors shadow-sm"
            >
              <Globe className="w-4 h-4" />
            </a>
            <a
              href="#social"
              aria-label="Social Share"
              className="w-8 h-8 rounded-full bg-slate-900 text-white flex items-center justify-center hover:bg-[#0B6799] transition-colors shadow-sm"
            >
              <Share2 className="w-4 h-4" />
            </a>
            <a
              href="#platform"
              aria-label="Platform"
              className="w-8 h-8 rounded-full bg-slate-900 text-white flex items-center justify-center hover:bg-[#0B6799] transition-colors shadow-sm"
            >
              <Layers className="w-4 h-4" />
            </a>
          </div>
        </div>

        {/* Column 2: SERVICES (2 cols) */}
        <div className="lg:col-span-2 space-y-3">
          <h5 className="font-bold text-xs uppercase tracking-wider text-slate-900">
            SERVICES
          </h5>
          <ul className="space-y-2 text-xs text-slate-600 font-medium">
            <li><a href="#temporal-audits" className="hover:text-slate-900 transition-colors">Temporal Audits</a></li>
            <li><a href="#atmospheric-ui" className="hover:text-slate-900 transition-colors">Atmospheric UI</a></li>
            <li><a href="#precision-scheduling" className="hover:text-slate-900 transition-colors">Precision Scheduling</a></li>
            <li><a href="#focus-flow" className="hover:text-slate-900 transition-colors">Focus Flow</a></li>
          </ul>
        </div>

        {/* Column 3: COMPANY (2 cols) */}
        <div className="lg:col-span-2 space-y-3">
          <h5 className="font-bold text-xs uppercase tracking-wider text-slate-900">
            COMPANY
          </h5>
          <ul className="space-y-2 text-xs text-slate-600 font-medium">
            <li><a href="#philosophy" className="hover:text-slate-900 transition-colors">Our Philosophy</a></li>
            <li><a href="#offices" className="hover:text-slate-900 transition-colors">Global Offices</a></li>
            <li><a href="#lab" className="hover:text-slate-900 transition-colors">Research Lab</a></li>
            <li><a href="#careers" className="hover:text-slate-900 transition-colors">Careers</a></li>
          </ul>
        </div>

        {/* Column 4: LEGAL (3 cols) */}
        <div className="lg:col-span-3 space-y-3">
          <h5 className="font-bold text-xs uppercase tracking-wider text-slate-900">
            LEGAL
          </h5>
          <ul className="space-y-2 text-xs text-slate-600 font-medium">
            <li><a href="#privacy" className="hover:text-slate-900 transition-colors">Privacy Policy</a></li>
            <li><a href="#terms" className="hover:text-slate-900 transition-colors">Terms of Service</a></li>
            <li><a href="#cookies" className="hover:text-slate-900 transition-colors">Cookie Settings</a></li>
            <li><a href="#ethics" className="hover:text-slate-900 transition-colors">Ethics Charter</a></li>
          </ul>
        </div>
      </div>

      {/* Footer Bottom Bar */}
      <div className="pt-6 border-t border-sky-200/50 flex flex-col sm:flex-row items-center justify-between text-[11px] text-slate-500 font-medium gap-2">
        <div>
          © 2026 Calorye Hive Business. All rights reserved.
        </div>
        <div className="flex items-center space-x-4 uppercase tracking-widest text-[10px] text-slate-400 font-bold">
          <span className="flex items-center space-x-1">
            <span className="w-1.5 h-1.5 rounded-full bg-emerald-500" />
            <span>SYSTEM STATUS 100% OPERATIONAL</span>
          </span>
          <span>•</span>
          <span>GLOBAL SERVERS</span>
        </div>
      </div>
    </footer>
  );
};
