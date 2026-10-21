import React, { useState, useEffect } from 'react';
import { SystemSettings, SystemStats } from '../types';
import { api } from '../services/api';
import { 
  Settings, 
  Cpu, 
  Database, 
  HardDrive, 
  ShieldCheck, 
  Key, 
  Info,
  Server,
  Layers,
  FileCode2
} from 'lucide-react';

export const SettingsView: React.FC = () => {
  const [settings, setSettings] = useState<SystemSettings | null>(null);
  const [stats, setStats] = useState<SystemStats | null>(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const load = async () => {
      setLoading(true);
      try {
        const [settData, statsData] = await Promise.all([
          api.getSystemSettings(),
          api.getSystemStats(),
        ]);
        setSettings(settData);
        setStats(statsData);
      } catch (err) {
        console.error('Failed to load system diagnostics:', err);
      } finally {
        setLoading(false);
      }
    };
    load();
  }, []);

  return (
    <div className="max-w-4xl mx-auto space-y-6 pb-20 animate-fadeIn">
      {/* Title */}
      <div className="space-y-1">
        <h1 className="text-2xl font-bold text-slate-100 flex items-center gap-2">
          <Settings className="w-6 h-6 text-emerald-400" />
          <span>System Diagnostics & AI Provider Settings</span>
        </h1>
        <p className="text-xs sm:text-sm text-slate-400">
          Inspect backend AI configuration, application operating mode, storage guards, and persistence status.
        </p>
      </div>

      {loading ? (
        <div className="p-12 text-center text-slate-400 bg-slate-900 border border-slate-800 rounded-xl animate-pulse">
          Loading system diagnostics...
        </div>
      ) : (
        <div className="space-y-6">
          {/* AI Provider Card */}
          <div className="bg-slate-900 border border-slate-800 rounded-2xl p-6 shadow-md space-y-4">
            <div className="flex items-center justify-between border-b border-slate-800 pb-3">
              <h3 className="text-base font-semibold text-slate-100 flex items-center gap-2">
                <Cpu className="w-5 h-5 text-emerald-400" />
                <span>AI Investigation Engine Configuration</span>
              </h3>
              <span className={`px-2.5 py-0.5 rounded-full text-xs font-mono font-semibold border ${
                settings?.isDemoMode
                  ? 'bg-amber-950 text-amber-300 border-amber-800'
                  : 'bg-emerald-950 text-emerald-300 border-emerald-800'
              }`}>
                {settings?.isDemoMode ? 'Demo Mode Active' : 'Live LLM Mode'}
              </span>
            </div>

            <div className="grid grid-cols-1 md:grid-cols-2 gap-4 text-xs font-mono">
              <div className="p-3 bg-slate-950 rounded-xl border border-slate-800 space-y-1">
                <span className="text-slate-400 text-[11px] block">Active Provider Name</span>
                <span className="text-slate-100 font-semibold">{settings?.aiProvider}</span>
              </div>

              <div className="p-3 bg-slate-950 rounded-xl border border-slate-800 space-y-1">
                <span className="text-slate-400 text-[11px] block">Model Identifier</span>
                <span className="text-slate-100 font-semibold">{settings?.aiModel}</span>
              </div>

              <div className="p-3 bg-slate-950 rounded-xl border border-slate-800 space-y-1">
                <span className="text-slate-400 text-[11px] block">API Key Security</span>
                <span className="text-slate-300">{settings?.maskedApiKey}</span>
              </div>

              <div className="p-3 bg-slate-950 rounded-xl border border-slate-800 space-y-1">
                <span className="text-slate-400 text-[11px] block">Execution Guarantee</span>
                <span className="text-emerald-400 font-semibold">Strict JSON Schema Validation</span>
              </div>
            </div>

            {settings?.isDemoMode && (
              <div className="p-3.5 bg-amber-950/30 border border-amber-800/40 rounded-xl flex items-start gap-2.5 text-xs text-amber-200">
                <Info className="w-4 h-4 text-amber-400 shrink-0 mt-0.5" />
                <div>
                  <strong>Demo Mode Operating Notice:</strong> When no API key is specified in backend environment variables, Sentinel activates the high-fidelity deterministic investigation engine. It produces authentic, domain-specialized problem breakdowns for software debugging, physics-informed math convergence, and business cohort economics without external API calls.
                </div>
              </div>
            )}
          </div>

          {/* Database & Storage Card */}
          <div className="bg-slate-900 border border-slate-800 rounded-2xl p-6 shadow-md space-y-4">
            <h3 className="text-base font-semibold text-slate-100 flex items-center gap-2 border-b border-slate-800 pb-3">
              <Database className="w-5 h-5 text-indigo-400" />
              <span>Persistence & Evidence Storage</span>
            </h3>

            <div className="grid grid-cols-1 md:grid-cols-3 gap-4 text-xs font-mono">
              <div className="p-3 bg-slate-950 rounded-xl border border-slate-800 space-y-1">
                <span className="text-slate-400 text-[11px] block">Database Profiles</span>
                <span className="text-indigo-300 font-semibold">{settings?.databaseProfile}</span>
              </div>

              <div className="p-3 bg-slate-950 rounded-xl border border-slate-800 space-y-1">
                <span className="text-slate-400 text-[11px] block">Migration Engine</span>
                <span className="text-emerald-400 font-semibold">Flyway (Schema V1 & V2)</span>
              </div>

              <div className="p-3 bg-slate-950 rounded-xl border border-slate-800 space-y-1">
                <span className="text-slate-400 text-[11px] block">Max Evidence File Size</span>
                <span className="text-slate-200 font-semibold">{settings?.maxFileSizeMb} MB</span>
              </div>
            </div>

            <div className="p-3 bg-slate-950 rounded-xl border border-slate-800 text-xs text-slate-300 space-y-1">
              <span className="text-slate-400 font-mono text-[11px] block">Storage Root Directory:</span>
              <p className="font-mono text-slate-200">{settings?.storageLocation}</p>
              <p className="text-[11px] text-slate-500 pt-1">
                Protected by path-traversal sanitization, unique UUID prefixing, SHA-256 verification, and strict extension whitelisting.
              </p>
            </div>
          </div>

          {/* Architecture Monolith Overview */}
          <div className="bg-slate-900 border border-slate-800 rounded-2xl p-6 shadow-md space-y-3">
            <h3 className="text-base font-semibold text-slate-100 flex items-center gap-2 border-b border-slate-800 pb-3">
              <Server className="w-5 h-5 text-emerald-400" />
              <span>Modular Monolith Architecture</span>
            </h3>

            <div className="grid grid-cols-1 sm:grid-cols-2 gap-3 text-xs text-slate-300">
              <div className="p-3 rounded-xl bg-slate-950 border border-slate-800 space-y-1">
                <strong className="text-emerald-400 block font-mono">Backend Stack:</strong>
                <p>Java 21 / 25 LTS, Spring Boot 3.3, Spring Data JPA, Hibernate, Flyway Migrations, Spring Validation, OpenAPI 3.0 / Swagger UI.</p>
              </div>

              <div className="p-3 rounded-xl bg-slate-950 border border-slate-800 space-y-1">
                <strong className="text-cyan-400 block font-mono">Frontend Stack:</strong>
                <p>React 18, TypeScript, Vite, Tailwind CSS, Lucide React, Vitest & React Testing Library.</p>
              </div>
            </div>
          </div>
        </div>
      )}
    </div>
  );
};
