import React from 'react';
import { featuredResources } from '../../data/mockData';
import { ArrowUpRight } from 'lucide-react';

interface FeaturedResourcesProps {
  onSelectResource?: (title: string) => void;
}

export const FeaturedResources: React.FC<FeaturedResourcesProps> = ({ onSelectResource }) => {
  return (
    <section className="mb-10">
      <div className="grid grid-cols-1 md:grid-cols-2 gap-5">
        {featuredResources.map((resource) => (
          <div
            key={resource.id}
            onClick={() => onSelectResource?.(resource.title)}
            className="group relative h-52 sm:h-60 rounded-3xl overflow-hidden shadow-lg border border-white/40 cursor-pointer transition-all duration-300 hover:shadow-2xl hover:-translate-y-1"
          >
            {/* Background Image */}
            <div
              className="absolute inset-0 bg-cover bg-center transition-transform duration-700 group-hover:scale-110"
              style={{ backgroundImage: `url(${resource.imageUrl})` }}
            />

            {/* Gradient Overlay matching screenshot */}
            <div className="absolute inset-0 bg-gradient-to-t from-slate-950 via-slate-900/65 to-sky-900/30 group-hover:opacity-90 transition-opacity" />

            {/* Content overlay */}
            <div className="relative z-10 h-full p-6 flex flex-col justify-end text-left text-white">
              <div className="flex items-center justify-between mb-2">
                <span className="text-[10px] uppercase tracking-widest font-bold text-sky-300 bg-white/10 backdrop-blur-md px-2.5 py-0.5 rounded-full border border-white/15">
                  {resource.category}
                </span>
                <span className="p-1.5 rounded-full bg-white/10 group-hover:bg-white group-hover:text-slate-900 text-white transition-all transform group-hover:translate-x-0.5 group-hover:-translate-y-0.5">
                  <ArrowUpRight className="w-4 h-4" />
                </span>
              </div>

              <h4 className="text-lg sm:text-xl font-bold tracking-tight text-white mb-1 leading-snug">
                {resource.title}
              </h4>
              <p className="text-xs text-slate-300 font-normal line-clamp-2 leading-relaxed">
                {resource.description}
              </p>
            </div>
          </div>
        ))}
      </div>
    </section>
  );
};
