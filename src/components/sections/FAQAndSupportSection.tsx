import React, { useState } from 'react';
import { ChevronDown, ChevronUp, MessageSquare, PhoneCall, Users, ArrowRight } from 'lucide-react';
import type { FAQItem } from '../../types/dashboard';

interface FAQAndSupportSectionProps {
  faqs: FAQItem[];
  onOpenChat: () => void;
  onOpenCallback: () => void;
}

export const FAQAndSupportSection: React.FC<FAQAndSupportSectionProps> = ({
  faqs,
  onOpenChat,
  onOpenCallback,
}) => {
  const [openFaqId, setOpenFaqId] = useState<string | null>(null);

  const toggleFaq = (id: string) => {
    setOpenFaqId(openFaqId === id ? null : id);
  };

  return (
    <section className="mb-12">
      <div className="grid grid-cols-1 lg:grid-cols-12 gap-8 items-start">
        {/* LEFT COLUMN: Frequently Asked Questions (8 cols) */}
        <div className="lg:col-span-7 xl:col-span-8 text-left">
          <h3 className="text-xl font-extrabold text-slate-900 mb-4 tracking-tight">
            Frequently Asked Questions
          </h3>

          <div className="space-y-3">
            {faqs.map((faq) => {
              const isOpen = openFaqId === faq.id;

              return (
                <div
                  key={faq.id}
                  className="bg-white rounded-2xl border border-slate-100 shadow-soft-card overflow-hidden transition-all duration-200"
                >
                  <button
                    onClick={() => toggleFaq(faq.id)}
                    className="w-full px-5 py-4 flex items-center justify-between text-left hover:bg-slate-50/70 transition-colors"
                  >
                    <span className="font-bold text-sm text-slate-800 pr-3">
                      {faq.question}
                    </span>
                    <span className="text-slate-400">
                      {isOpen ? <ChevronUp className="w-4 h-4" /> : <ChevronDown className="w-4 h-4" />}
                    </span>
                  </button>

                  {isOpen && (
                    <div className="px-5 pb-4 pt-1 text-xs text-slate-600 border-t border-slate-100 leading-relaxed">
                      {faq.answer}
                    </div>
                  )}
                </div>
              );
            })}
          </div>
        </div>

        {/* RIGHT COLUMN: Need more help? (4 cols) */}
        <div className="lg:col-span-5 xl:col-span-4 text-left">
          <h3 className="text-xl font-extrabold text-slate-900 mb-4 tracking-tight">
            Need more help?
          </h3>

          <div className="space-y-4">
            {/* Live Chat Card */}
            <div className="bg-white rounded-3xl p-5 border border-slate-100 shadow-soft-card">
              <div className="flex items-start space-x-3.5 mb-3">
                <div className="w-10 h-10 rounded-2xl bg-black flex items-center justify-center shrink-0 shadow-md">
                  <MessageSquare className="w-5 h-5 text-white" />
                </div>
                <div>
                  <h4 className="text-sm font-extrabold text-slate-900 leading-tight">Start Live Chat</h4>
                  <div className="flex items-center space-x-1.5 text-[10px] text-emerald-600 font-semibold mt-0.5">
                    <span className="w-1.5 h-1.5 rounded-full bg-emerald-500 animate-pulse" />
                    <span>Usually responds in &lt; 2 mins</span>
                  </div>
                </div>
              </div>

              <p className="text-xs text-slate-600 leading-relaxed mb-4">
                Chat with our hospitality specialists for immediate operational support.
              </p>

              <button
                onClick={onOpenChat}
                className="w-full bg-slate-950 hover:bg-black text-white font-semibold text-xs py-2.5 px-4 rounded-xl transition-all duration-200 shadow-md hover:shadow-lg active:scale-95"
              >
                Chat now
              </button>
            </div>

            {/* Request a Callback Card */}
            <div className="bg-white rounded-3xl p-5 border border-slate-100 shadow-soft-card">
              <div className="flex items-start space-x-3.5 mb-3">
                <div className="w-10 h-10 rounded-2xl bg-slate-900 flex items-center justify-center shrink-0 shadow-md">
                  <PhoneCall className="w-5 h-5 text-white" />
                </div>
                <div>
                  <h4 className="text-sm font-extrabold text-slate-900 leading-tight">Request a Callback</h4>
                  <span className="text-[10px] text-slate-500 font-semibold">⏱ Wait time: ~15 mins</span>
                </div>
              </div>

              <p className="text-xs text-slate-600 leading-relaxed mb-4">
                Leave your number and a specialist will call you back shortly to assist with complex issues.
              </p>

              <button
                onClick={onOpenCallback}
                className="w-full bg-slate-100 hover:bg-slate-200 text-slate-800 font-semibold text-xs py-2.5 px-4 rounded-xl transition-all duration-200 active:scale-95"
              >
                Schedule Call
              </button>
            </div>

            {/* Partner Community Card */}
            <div className="bg-gradient-to-br from-sky-400/90 to-sky-600 rounded-3xl p-5 text-white shadow-lg relative overflow-hidden group">
              <div className="flex items-start space-x-3.5 mb-3">
                <div className="w-10 h-10 rounded-2xl bg-white/20 backdrop-blur-md flex items-center justify-center shrink-0 border border-white/20">
                  <Users className="w-5 h-5 text-white" />
                </div>
                <div>
                  <h4 className="text-sm font-extrabold text-white leading-tight">Partner Community</h4>
                </div>
              </div>

              <p className="text-xs text-sky-50 leading-relaxed mb-4">
                Connect with 5,000+ service providing owners and share industry insights.
              </p>

              <a
                href="#community"
                className="inline-flex items-center space-x-1.5 text-xs font-bold text-white hover:underline group-hover:translate-x-1 transition-transform"
              >
                <span>Join Discussion</span>
                <ArrowRight className="w-3.5 h-3.5" />
              </a>
            </div>
          </div>
        </div>
      </div>
    </section>
  );
};
