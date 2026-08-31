import React from 'react';
import { User, Shield, CreditCard, LogOut, CheckCircle } from 'lucide-react';

interface ProfileDropdownProps {
  isOpen: boolean;
  onClose: () => void;
  onOpenSettings: () => void;
}

export const ProfileDropdown: React.FC<ProfileDropdownProps> = ({
  isOpen,
  onClose,
  onOpenSettings,
}) => {
  if (!isOpen) return null;

  return (
    <div className="absolute top-16 right-2 sm:right-6 z-50 w-72 bg-white rounded-3xl shadow-2xl border border-slate-100 p-4 text-left animate-in fade-in slide-in-from-top-2 duration-200">
      {/* Profile Card Header */}
      <div className="flex items-center space-x-3 pb-3 border-b border-slate-100 mb-2">
        <div className="w-10 h-10 rounded-full bg-gradient-to-tr from-[#0B6799] to-sky-400 text-white font-extrabold flex items-center justify-center text-sm shadow-md">
          OM
        </div>
        <div>
          <h4 className="font-bold text-xs text-slate-900 leading-tight">Calorye Business Account</h4>
          <p className="text-[10px] text-slate-500 font-medium">admin@caloryehive.com</p>
          <span className="inline-flex items-center space-x-1 text-[9px] font-bold text-emerald-600 bg-emerald-50 px-1.5 py-0.2 rounded mt-0.5">
            <CheckCircle className="w-2.5 h-2.5" />
            <span>Verified Merchant</span>
          </span>
        </div>
      </div>

      {/* Menu Options */}
      <div className="space-y-1 text-xs text-slate-700 font-medium">
        <button
          onClick={() => {
            onClose();
            onOpenSettings();
          }}
          className="w-full flex items-center space-x-2.5 px-3 py-2 rounded-xl hover:bg-slate-50 text-left transition-colors"
        >
          <User className="w-4 h-4 text-slate-500" />
          <span>My Profile</span>
        </button>

        <button
          onClick={() => {
            onClose();
            onOpenSettings();
          }}
          className="w-full flex items-center space-x-2.5 px-3 py-2 rounded-xl hover:bg-slate-50 text-left transition-colors"
        >
          <Shield className="w-4 h-4 text-slate-500" />
          <span>Account Security</span>
        </button>

        <button
          onClick={onClose}
          className="w-full flex items-center space-x-2.5 px-3 py-2 rounded-xl hover:bg-slate-50 text-left transition-colors"
        >
          <CreditCard className="w-4 h-4 text-slate-500" />
          <span>Billing & Plan</span>
        </button>
      </div>

      {/* Logout Divider */}
      <div className="pt-2 mt-2 border-t border-slate-100">
        <button
          onClick={onClose}
          className="w-full flex items-center space-x-2.5 px-3 py-2 rounded-xl text-rose-600 hover:bg-rose-50 text-left transition-colors font-semibold text-xs"
        >
          <LogOut className="w-4 h-4 text-rose-500" />
          <span>Log Out</span>
        </button>
      </div>
    </div>
  );
};
