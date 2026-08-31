import React from 'react';
import { Bell, CheckCheck, Info, AlertTriangle, CheckCircle, X } from 'lucide-react';
import type { NotificationItem } from '../../types/dashboard';

interface NotificationPanelProps {
  isOpen: boolean;
  onClose: () => void;
  notifications: NotificationItem[];
  onClearAll: () => void;
}

export const NotificationPanel: React.FC<NotificationPanelProps> = ({
  isOpen,
  onClose,
  notifications,
  onClearAll,
}) => {
  if (!isOpen) return null;

  return (
    <div className="absolute top-16 right-4 sm:right-12 z-50 w-80 sm:w-96 bg-white rounded-3xl shadow-2xl border border-slate-100 p-4 text-left animate-in fade-in slide-in-from-top-2 duration-200">
      {/* Panel Header */}
      <div className="flex items-center justify-between pb-3 border-b border-slate-100 mb-3">
        <div className="flex items-center space-x-2">
          <Bell className="w-4 h-4 text-[#0B6799]" />
          <h4 className="font-bold text-sm text-slate-900">Notifications</h4>
          <span className="bg-sky-100 text-[#0B6799] text-[10px] font-bold px-2 py-0.5 rounded-full">
            {notifications.filter((n) => n.unread).length} New
          </span>
        </div>

        <div className="flex items-center space-x-2">
          <button
            onClick={onClearAll}
            className="text-[11px] font-semibold text-slate-500 hover:text-sky-700 transition-colors flex items-center space-x-1"
          >
            <CheckCheck className="w-3.5 h-3.5" />
            <span>Mark all read</span>
          </button>
          <button
            onClick={onClose}
            className="p-1 rounded-full text-slate-400 hover:text-slate-600 transition-colors"
          >
            <X className="w-4 h-4" />
          </button>
        </div>
      </div>

      {/* Notifications List */}
      <div className="max-h-72 overflow-y-auto space-y-2.5">
        {notifications.length === 0 ? (
          <p className="text-xs text-slate-400 text-center py-6">No new notifications</p>
        ) : (
          notifications.map((n) => (
            <div
              key={n.id}
              className={`p-3 rounded-2xl border transition-colors ${
                n.unread ? 'bg-sky-50/60 border-sky-100' : 'bg-slate-50/50 border-slate-100'
              }`}
            >
              <div className="flex items-start space-x-2.5">
                <div className="mt-0.5">
                  {n.type === 'success' && <CheckCircle className="w-4 h-4 text-emerald-500" />}
                  {n.type === 'alert' && <AlertTriangle className="w-4 h-4 text-amber-500" />}
                  {n.type === 'info' && <Info className="w-4 h-4 text-sky-500" />}
                </div>
                <div className="flex-1">
                  <div className="flex items-center justify-between">
                    <h5 className="text-xs font-bold text-slate-900">{n.title}</h5>
                    <span className="text-[9px] text-slate-400 font-medium">{n.time}</span>
                  </div>
                  <p className="text-[11px] text-slate-600 leading-snug mt-1">{n.message}</p>
                </div>
              </div>
            </div>
          ))
        )}
      </div>
    </div>
  );
};
