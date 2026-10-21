import React, { useState, useEffect } from 'react';
import { ProblemCaseSummary, ProblemCaseDetail, SystemStats, SystemSettings } from './types';
import { api } from './services/api';
import { Navbar } from './components/Navbar';
import { DashboardView } from './views/DashboardView';
import { CreateCaseView } from './views/CreateCaseView';
import { InvestigationWorkspaceView } from './views/InvestigationWorkspaceView';
import { CaseHistoryView } from './views/CaseHistoryView';
import { KnowledgeLibraryView } from './views/KnowledgeLibraryView';
import { SettingsView } from './views/SettingsView';

export function App() {
  const [currentView, setCurrentView] = useState<string>('dashboard');
  const [selectedCaseId, setSelectedCaseId] = useState<string | null>(null);
  const [currentCase, setCurrentCase] = useState<ProblemCaseDetail | null>(null);
  const [cases, setCases] = useState<ProblemCaseSummary[]>([]);
  const [stats, setStats] = useState<SystemStats | null>(null);
  const [settings, setSettings] = useState<SystemSettings | null>(null);
  const [loading, setLoading] = useState<boolean>(true);
  const [caseLoading, setCaseLoading] = useState<boolean>(false);

  const loadInitialData = async () => {
    setLoading(true);
    try {
      const [casesData, statsData, settingsData] = await Promise.all([
        api.getCases(),
        api.getSystemStats(),
        api.getSystemSettings(),
      ]);
      setCases(casesData);
      setStats(statsData);
      setSettings(settingsData);
    } catch (err) {
      console.error('Failed to load initial application data:', err);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    loadInitialData();
  }, []);

  const handleNavigate = async (view: string, caseId?: string) => {
    if (view === 'workspace' && caseId) {
      setSelectedCaseId(caseId);
      setCaseLoading(true);
      setCurrentView('workspace');
      try {
        const detail = await api.getCase(caseId);
        setCurrentCase(detail);
      } catch (err) {
        console.error('Failed to load case detail:', err);
      } finally {
        setCaseLoading(false);
      }
    } else {
      setCurrentView(view);
      if (view === 'dashboard' || view === 'history') {
        // Refresh case list & stats
        try {
          const [c, s] = await Promise.all([api.getCases(), api.getSystemStats()]);
          setCases(c);
          setStats(s);
        } catch {}
      }
    }
  };

  const handleRefreshCurrentCase = async () => {
    if (selectedCaseId) {
      try {
        const detail = await api.getCase(selectedCaseId);
        setCurrentCase(detail);
        const [c, s] = await Promise.all([api.getCases(), api.getSystemStats()]);
        setCases(c);
        setStats(s);
      } catch (err) {
        console.error('Failed to refresh case:', err);
      }
    }
  };

  return (
    <div className="min-h-screen bg-slate-950 text-slate-100 flex flex-col font-sans">
      <Navbar
        currentView={currentView}
        onNavigate={handleNavigate}
        systemSettings={settings}
      />

      <main className="flex-1 max-w-7xl w-full mx-auto px-4 sm:px-6 lg:px-8 pt-6">
        {currentView === 'dashboard' && (
          <DashboardView
            stats={stats}
            cases={cases}
            loading={loading}
            onNavigate={handleNavigate}
          />
        )}

        {currentView === 'create' && (
          <CreateCaseView
            onCancel={() => handleNavigate('dashboard')}
            onCreated={(newCaseId) => handleNavigate('workspace', newCaseId)}
          />
        )}

        {currentView === 'workspace' && (
          <>
            {caseLoading || !currentCase ? (
              <div className="p-16 text-center text-slate-400 bg-slate-900 border border-slate-800 rounded-2xl animate-pulse">
                Loading investigation workspace...
              </div>
            ) : (
              <InvestigationWorkspaceView
                problemCase={currentCase}
                onRefresh={handleRefreshCurrentCase}
                onBack={() => handleNavigate('dashboard')}
              />
            )}
          </>
        )}

        {currentView === 'history' && (
          <CaseHistoryView
            cases={cases}
            loading={loading}
            onSelectCase={(id) => handleNavigate('workspace', id)}
            onNewCase={() => handleNavigate('create')}
          />
        )}

        {currentView === 'knowledge' && (
          <KnowledgeLibraryView
            onOpenCase={(caseId) => handleNavigate('workspace', caseId)}
          />
        )}

        {currentView === 'settings' && (
          <SettingsView />
        )}
      </main>

      <footer className="border-t border-slate-900 bg-slate-950/80 py-6 text-center text-xs text-slate-500 font-mono">
        <p>SENTINEL — Advanced AI Problem Investigation & Resolution Platform</p>
        <p className="text-[11px] text-slate-600 mt-1">
          Designed with strict verification separation: model outputs are unverified hypotheses until test runs pass.
        </p>
      </footer>
    </div>
  );
}

export default App;
