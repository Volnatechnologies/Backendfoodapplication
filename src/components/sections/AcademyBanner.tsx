import React from 'react';
import { PlayCircle, Sparkles } from 'lucide-react';

interface AcademyBannerProps {
  onStartLearning: () => void;
}

export const AcademyBanner: React.FC<AcademyBannerProps> = ({ onStartLearning }) => {
  return (
    <section className="mb-14">
      <div className="bg-white rounded-3xl p-6 sm:p-10 border border-slate-100 shadow-soft-card">
        <div className="grid grid-cols-1 lg:grid-cols-12 gap-8 items-center">
          {/* Left Side Content */}
          <div className="lg:col-span-6 text-left space-y-4">
            <h3 className="text-2xl sm:text-3xl font-extrabold text-slate-900 leading-tight tracking-tight">
              Master the platform <br />
              <span className="text-[#0B6799]">with Elite Academy</span>
            </h3>

            <p className="text-xs sm:text-sm text-slate-600 font-normal leading-relaxed max-w-md">
              Explore our curated video series designed to help you optimize delivery flow and increase customer retention.
            </p>

            <div className="pt-2">
              <button
                onClick={onStartLearning}
                className="bg-[#0B6799] hover:bg-[#08527C] text-white font-bold text-xs sm:text-sm px-6 py-3 rounded-full transition-all duration-200 shadow-lg shadow-[#0B6799]/20 hover:shadow-xl active:scale-95 flex items-center space-x-2"
              >
                <PlayCircle className="w-4 h-4" />
                <span>Start Learning</span>
              </button>
            </div>
          </div>

          {/* Right Side Dark Card Preview */}
          <div className="lg:col-span-6">
            <div className="relative bg-[#07131E] rounded-3xl p-6 sm:p-8 text-white shadow-2xl border border-slate-800 overflow-hidden group">
              {/* Subtle Ambient Radial Glow */}
              <div className="absolute -top-12 -right-12 w-48 h-48 bg-sky-500/20 rounded-full blur-3xl group-hover:bg-sky-400/30 transition-all duration-500" />
              <div className="absolute -bottom-12 -left-12 w-48 h-48 bg-blue-600/20 rounded-full blur-3xl" />

              <div className="relative z-10 flex flex-col justify-between h-48 sm:h-52 text-center">
                {/* Header Tag */}
                <div>
                  <span className="inline-flex items-center space-x-1 text-[10px] uppercase tracking-widest font-extrabold text-sky-400 bg-sky-950/80 px-3 py-1 rounded-full border border-sky-800/40">
                    <Sparkles className="w-3 h-3 mr-1" />
                    LEARNING RESOURCE
                  </span>
                  <p className="text-[11px] text-slate-400 font-medium mt-1">Interactive Tutorial</p>
                </div>

                {/* Main Heading */}
                <div className="my-auto">
                  <h4 className="text-xl sm:text-2xl font-bold tracking-tight text-white/95 drop-shadow">
                    Save time, scale faster
                  </h4>
                </div>

                {/* Footer Tag & Course Title */}
                <div className="text-left border-t border-slate-800/80 pt-3">
                  <span className="text-[9px] uppercase tracking-wider font-extrabold text-amber-400 bg-amber-950/60 px-2 py-0.5 rounded border border-amber-800/40">
                    NEW COURSE
                  </span>
                  <p className="text-xs sm:text-sm font-bold text-white mt-1">
                    Optimizing Peak Hour Efficiency
                  </p>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </section>
  );
};
