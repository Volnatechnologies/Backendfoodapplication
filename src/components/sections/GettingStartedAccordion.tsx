import React, { useState } from 'react';
import { ChevronDown, ChevronUp, ThumbsUp, ThumbsDown, Check } from 'lucide-react';
import type { FAQItem } from '../../types/dashboard';

interface GettingStartedAccordionProps {
  items: FAQItem[];
}

export const GettingStartedAccordion: React.FC<GettingStartedAccordionProps> = ({ items }) => {
  const [openId, setOpenId] = useState<string | null>('gs-1');
  const [feedbackState, setFeedbackState] = useState<Record<string, 'yes' | 'no' | null>>({});

  const toggleItem = (id: string) => {
    setOpenId(openId === id ? null : id);
  };

  const handleFeedback = (id: string, choice: 'yes' | 'no') => {
    setFeedbackState((prev) => ({
      ...prev,
      [id]: prev[id] === choice ? null : choice,
    }));
  };

  return (
    <section className="mb-10">
      {/* Section Header */}
      <div className="mb-4 text-left">
        <h3 className="text-xl sm:text-2xl font-extrabold text-slate-900 tracking-tight">Getting Started</h3>
        <p className="text-xs sm:text-sm text-slate-600 font-medium">
          Essential information for your first 24 hours with Calorye Business
        </p>
      </div>

      {/* Accordion List */}
      <div className="space-y-3">
        {items.map((item) => {
          const isOpen = openId === item.id;
          const userFeedback = feedbackState[item.id];

          return (
            <div
              key={item.id}
              className="bg-white rounded-2xl border border-slate-100 shadow-soft-card overflow-hidden transition-all duration-300"
            >
              {/* Header Button */}
              <button
                onClick={() => toggleItem(item.id)}
                className="w-full px-6 py-4 flex items-center justify-between text-left hover:bg-slate-50/80 transition-colors"
                aria-expanded={isOpen}
              >
                <span className="font-bold text-sm sm:text-base text-slate-800 pr-4">
                  {item.question}
                </span>
                <span className="text-slate-400 p-1 rounded-full bg-slate-100/70">
                  {isOpen ? <ChevronUp className="w-4 h-4" /> : <ChevronDown className="w-4 h-4" />}
                </span>
              </button>

              {/* Collapsible Content */}
              {isOpen && (
                <div className="px-6 pb-6 pt-2 text-xs sm:text-sm text-slate-600 border-t border-slate-100/80 leading-relaxed space-y-4">
                  {item.answer.split('\n\n').map((paragraph, idx) => (
                    <p key={idx}>{paragraph}</p>
                  ))}

                  {/* Feedback Footer */}
                  <div className="pt-4 border-t border-slate-100 flex flex-wrap items-center justify-between text-xs text-slate-500 gap-3">
                    <span className="font-medium text-slate-600">Was this article helpful?</span>
                    
                    <div className="flex items-center space-x-3">
                      <button
                        onClick={() => handleFeedback(item.id, 'yes')}
                        className={`inline-flex items-center space-x-1.5 px-3 py-1.5 rounded-full border text-xs font-semibold transition-all ${
                          userFeedback === 'yes'
                            ? 'bg-emerald-50 border-emerald-300 text-emerald-700 shadow-sm'
                            : 'border-slate-200 text-slate-600 hover:bg-slate-50 hover:border-slate-300'
                        }`}
                      >
                        {userFeedback === 'yes' ? <Check className="w-3.5 h-3.5 text-emerald-600" /> : <ThumbsUp className="w-3.5 h-3.5" />}
                        <span>Yes</span>
                      </button>

                      <button
                        onClick={() => handleFeedback(item.id, 'no')}
                        className={`inline-flex items-center space-x-1.5 px-3 py-1.5 rounded-full border text-xs font-semibold transition-all ${
                          userFeedback === 'no'
                            ? 'bg-rose-50 border-rose-300 text-rose-700 shadow-sm'
                            : 'border-slate-200 text-slate-600 hover:bg-slate-50 hover:border-slate-300'
                        }`}
                      >
                        <ThumbsDown className="w-3.5 h-3.5" />
                        <span>No</span>
                      </button>
                    </div>
                  </div>
                </div>
              )}
            </div>
          );
        })}
      </div>
    </section>
  );
};
