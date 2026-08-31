import React, { useState } from 'react';
import { Settings, X, Globe, Bell, Shield, Save } from 'lucide-react';

interface SettingsPanelProps {
  isOpen: boolean;
  onClose: () => void;
}

export const SettingsPanel: React.FC<SettingsPanelProps> = ({ isOpen, onClose }) => {
  const [emailAlerts, setEmailAlerts] = useState(true);
  const [smsAlerts, setSmsAlerts] = useState(false);
  const [language, setLanguage] = useState('English (US)');
  const [saved, setSaved] = useState(false);

  if (!isOpen) return null;

  const handleSave = () => {
    setSaved(true);
    setTimeout(() => {
      setSaved(false);
      onClose();
    }, 1000);
  };

  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center p-4 bg-slate-900/60 backdrop-blur-sm transition-opacity">
      <div className="bg-white rounded-3xl w-full max-w-md shadow-2xl overflow-hidden border border-slate-100 p-6 text-left animate-in fade-in zoom-in-95 duration-200">
        <div className="flex items-center justify-between pb-4 border-b border-slate-100">
          <div className="flex items-center space-x-2.5">
            <div className="w-8 h-8 rounded-xl bg-slate-900 text-white flex items-center justify-center">
              <Settings className="w-4 h-4" />
            </div>
            <h3 className="font-extrabold text-base text-slate-900">Account Preferences</h3>
          </div>
          <button
            onClick={onClose}
            className="p-1 rounded-full text-slate-400 hover:text-slate-600 hover:bg-slate-100 transition-colors"
          >
            <X className="w-5 h-5" />
          </button>
        </div>

        <div className="py-4 space-y-4">
          {/* Notifications Setting */}
          <div>
            <span className="text-xs font-bold text-slate-800 flex items-center space-x-1.5 mb-2">
              <Bell className="w-3.5 h-3.5 text-[#0B6799]" />
              <span>Notification Preferences</span>
            </span>
            <div className="space-y-2 bg-slate-50 p-3 rounded-2xl border border-slate-100">
              <label className="flex items-center justify-between cursor-pointer">
                <span className="text-xs text-slate-700 font-medium">Email Payout Statements</span>
                <input
                  type="checkbox"
                  checked={emailAlerts}
                  onChange={(e) => setEmailAlerts(e.target.checked)}
                  className="rounded text-[#0B6799] focus:ring-[#0B6799]"
                />
              </label>
              <label className="flex items-center justify-between cursor-pointer">
                <span className="text-xs text-slate-700 font-medium">SMS Operational Dispatch Alerts</span>
                <input
                  type="checkbox"
                  checked={smsAlerts}
                  onChange={(e) => setSmsAlerts(e.target.checked)}
                  className="rounded text-[#0B6799] focus:ring-[#0B6799]"
                />
              </label>
            </div>
          </div>

          {/* Regional Settings */}
          <div>
            <span className="text-xs font-bold text-slate-800 flex items-center space-x-1.5 mb-2">
              <Globe className="w-3.5 h-3.5 text-[#0B6799]" />
              <span>Language & Locale</span>
            </span>
            <select
              value={language}
              onChange={(e) => setLanguage(e.target.value)}
              className="w-full bg-slate-50 border border-slate-200 text-xs rounded-xl p-2.5 font-medium focus:outline-none focus:ring-2 focus:ring-[#0B6799]/40"
            >
              <option>English (US)</option>
              <option>Spanish (ES)</option>
              <option>French (FR)</option>
              <option>German (DE)</option>
            </select>
          </div>

          {/* Security */}
          <div>
            <span className="text-xs font-bold text-slate-800 flex items-center space-x-1.5 mb-2">
              <Shield className="w-3.5 h-3.5 text-[#0B6799]" />
              <span>Security</span>
            </span>
            <div className="bg-slate-50 p-3 rounded-2xl border border-slate-100 flex items-center justify-between text-xs">
              <span className="text-slate-700 font-medium">Two-Factor Authentication</span>
              <span className="text-[10px] font-bold bg-emerald-100 text-emerald-700 px-2 py-0.5 rounded-full">
                ENABLED
              </span>
            </div>
          </div>
        </div>

        {/* Footer Actions */}
        <div className="pt-3 border-t border-slate-100 flex items-center justify-end space-x-2">
          <button
            onClick={onClose}
            className="px-4 py-2 text-xs font-semibold text-slate-600 hover:text-slate-900 transition-colors"
          >
            Cancel
          </button>
          <button
            onClick={handleSave}
            className="bg-[#0B6799] hover:bg-[#074D74] text-white text-xs font-bold px-5 py-2 rounded-xl transition-all shadow-md flex items-center space-x-1.5"
          >
            <Save className="w-3.5 h-3.5" />
            <span>{saved ? 'Saved!' : 'Save Settings'}</span>
          </button>
        </div>
      </div>
    </div>
  );
};
