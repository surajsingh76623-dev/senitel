import React from 'react';
import { 
  ShieldCheck, 
  LayoutDashboard, 
  PlusCircle, 
  FileText, 
  BookOpen, 
  Settings, 
  Cpu
} from 'lucide-react';
import { SystemSettings } from '../types';

interface Props {
  currentView: string;
  onNavigate: (view: string, caseId?: string) => void;
  systemSettings?: SystemSettings | null;
}

export const Navbar: React.FC<Props> = ({ currentView, onNavigate, systemSettings }) => {
  const isDemo = systemSettings ? systemSettings.isDemoMode : true;

  const navItems = [
    { id: 'dashboard', label: 'Dashboard', icon: <LayoutDashboard className="w-4 h-4" /> },
    { id: 'history', label: 'Case History', icon: <FileText className="w-4 h-4" /> },
    { id: 'knowledge', label: 'Knowledge Library', icon: <BookOpen className="w-4 h-4" /> },
    { id: 'settings', label: 'Settings', icon: <Settings className="w-4 h-4" /> },
  ];

  return (
    <header className="sticky top-0 z-40 bg-slate-900/90 backdrop-blur-md border-b border-slate-800">
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        <div className="flex items-center justify-between h-16">
          {/* Brand Logo */}
          <div 
            onClick={() => onNavigate('dashboard')}
            className="flex items-center gap-3 cursor-pointer group"
          >
            <div className="w-10 h-10 rounded-xl bg-emerald-500/10 border border-emerald-500/30 flex items-center justify-center text-emerald-400 group-hover:scale-105 transition-transform duration-200">
              <ShieldCheck className="w-6 h-6" />
            </div>
            <div>
              <div className="flex items-center gap-2">
                <span className="font-bold text-lg tracking-wider text-slate-100 font-mono">SENTINEL</span>
                <span className="text-[10px] uppercase tracking-widest font-semibold px-1.5 py-0.5 rounded bg-emerald-950 text-emerald-400 border border-emerald-800/80">
                  v1.0
                </span>
              </div>
              <p className="text-[11px] text-slate-400">Problem Investigation & Verification</p>
            </div>
          </div>

          {/* Navigation Links */}
          <nav className="hidden md:flex items-center gap-1">
            {navItems.map((item) => {
              const isActive = currentView === item.id;
              return (
                <button
                  key={item.id}
                  onClick={() => onNavigate(item.id)}
                  className={`flex items-center gap-2 px-3.5 py-2 rounded-lg text-sm font-medium transition ${
                    isActive
                      ? 'bg-slate-800 text-emerald-400 border border-slate-700/80 shadow-sm'
                      : 'text-slate-300 hover:text-slate-100 hover:bg-slate-800/60'
                  }`}
                >
                  {item.icon}
                  <span>{item.label}</span>
                </button>
              );
            })}
          </nav>

          {/* Mode Pill & Primary Action */}
          <div className="flex items-center gap-3">
            {/* Mode Indicator */}
            <div 
              onClick={() => onNavigate('settings')}
              title={isDemo ? "Deterministic Demo Engine active - no API key needed" : "Connected to live LLM provider"}
              className={`hidden sm:flex items-center gap-1.5 px-2.5 py-1 rounded-full text-xs font-mono border cursor-pointer transition ${
                isDemo
                  ? 'bg-amber-950/40 text-amber-300 border-amber-800/50 hover:bg-amber-950/60'
                  : 'bg-emerald-950/40 text-emerald-300 border-emerald-800/50 hover:bg-emerald-950/60'
              }`}
            >
              <Cpu className="w-3.5 h-3.5" />
              <span>{isDemo ? 'Demo Mode' : 'Live AI'}</span>
            </div>

            {/* New Investigation Button */}
            <button
              onClick={() => onNavigate('create')}
              className="flex items-center gap-2 px-4 py-2 bg-emerald-600 hover:bg-emerald-500 text-white rounded-lg text-sm font-semibold transition shadow-lg shadow-emerald-950/40 hover:shadow-emerald-950/60"
            >
              <PlusCircle className="w-4 h-4" />
              <span>New Investigation</span>
            </button>
          </div>
        </div>
      </div>
    </header>
  );
};
