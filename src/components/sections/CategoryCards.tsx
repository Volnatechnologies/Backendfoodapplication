import React from 'react';
import { LayoutGrid, DollarSign, MonitorSmartphone } from 'lucide-react';
import { categoryCards } from '../../data/mockData';

interface CategoryCardsProps {
  onCategoryClick?: (title: string) => void;
}

export const CategoryCards: React.FC<CategoryCardsProps> = ({ onCategoryClick }) => {
  const getIcon = (type: string) => {
    switch (type) {
      case 'services':
        return <LayoutGrid className="w-5 h-5 text-white" />;
      case 'earnings':
        return <DollarSign className="w-5 h-5 text-white" />;
      case 'troubleshooting':
        return <MonitorSmartphone className="w-5 h-5 text-white" />;
      default:
        return <LayoutGrid className="w-5 h-5 text-white" />;
    }
  };

  return (
    <section className="mb-12">
      <div className="grid grid-cols-1 md:grid-cols-3 gap-5">
        {categoryCards.map((card) => (
          <div
            key={card.id}
            onClick={() => onCategoryClick?.(card.title)}
            className="bg-white rounded-3xl p-6 border border-slate-100 shadow-soft-card flex flex-col justify-between text-left hover:shadow-xl hover:-translate-y-1 transition-all duration-300 cursor-pointer group"
          >
            <div>
              {/* Icon Container */}
              <div
                className={`w-11 h-11 rounded-2xl flex items-center justify-center mb-5 shadow-md group-hover:scale-110 transition-transform ${card.bgColor}`}
              >
                {getIcon(card.iconType)}
              </div>

              {/* Title */}
              <h4 className="text-base font-extrabold text-slate-900 mb-2 group-hover:text-[#0B6799] transition-colors">
                {card.title}
              </h4>

              {/* Description */}
              <p className="text-xs text-slate-600 font-normal leading-relaxed mb-6">
                {card.description}
              </p>
            </div>

            {/* CTA Link */}
            <div>
              <span className="inline-flex items-center text-xs font-bold text-slate-900 group-hover:text-[#0B6799] transition-colors">
                {card.ctaText}
              </span>
            </div>
          </div>
        ))}
      </div>
    </section>
  );
};
