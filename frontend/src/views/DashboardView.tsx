import React from 'react';
import { 
  ProblemCaseSummary, 
  SystemStats 
} from '../types';
import { StatusBadge } from '../components/StatusBadge';
import { CategoryBadge } from '../components/CategoryBadge';
import { UrgencyBadge } from '../components/UrgencyBadge';
import { 
  ShieldCheck, 
  CheckCircle2, 
  Search, 
  Layers, 
  ArrowRight, 
  PlusCircle, 
  Terminal, 
  Binary, 
  BarChart3, 
  FileCheck2,
  Clock,
  Wrench,
  HelpCircle
} from 'lucide-react';

interface Props {
  stats: SystemStats | null;
  cases: ProblemCaseSummary[];
  loading: boolean;
  onNavigate: (view: string, caseId?: string) => void;
}

export const DashboardView: React.FC<Props> = ({ stats, cases, loading, onNavigate }) => {
  const verifiedCount = stats?.verifiedCases || 0;
  const totalCount = stats?.totalCases || 0;
  const verifiedRate = totalCount > 0 ? Math.round((verifiedCount / totalCount) * 100) : 0;

  return (
    <div className="space-y-8 animate-fadeIn pb-12">
      {/* Hero Banner */}
      <div className="relative overflow-hidden rounded-2xl bg-gradient-to-r from-slate-900 via-slate-850 to-slate-900 border border-slate-800 p-8 shadow-xl">
        <div className="relative z-10 max-w-3xl space-y-3">
          <div className="inline-flex items-center gap-2 px-3 py-1 rounded-full bg-emerald-950/60 border border-emerald-800/60 text-emerald-400 text-xs font-semibold">
            <ShieldCheck className="w-4 h-4" />
            <span>AI Problem Investigation & Falsifiable Verification Platform</span>
          </div>
          <h1 className="text-3xl font-extrabold text-slate-50 tracking-tight sm:text-4xl">
            Solve Complex Problems with <span className="text-emerald-400">Verified Solutions</span>
          </h1>
          <p className="text-sm sm:text-base text-slate-300 leading-relaxed">
            Whether you are a student solving math and science, an engineer calculating structural loads, a developer debugging software errors, or an analyst investigating business drops — SENTINEL helps you diagnose the root cause and test real fixes.
          </p>
          <div className="flex flex-wrap items-center gap-3 pt-2">
            <button
              onClick={() => onNavigate('create')}
              className="flex items-center gap-2 px-5 py-2.5 bg-emerald-600 hover:bg-emerald-500 text-white rounded-xl text-sm font-semibold transition shadow-lg shadow-emerald-950/50"
            >
              <PlusCircle className="w-4 h-4" />
              <span>Ask SENTINEL a Problem</span>
            </button>
            <button
              onClick={() => onNavigate('history')}
              className="flex items-center gap-2 px-4 py-2.5 bg-slate-800 hover:bg-slate-700/80 text-slate-200 border border-slate-700 rounded-xl text-sm font-medium transition"
            >
              <Search className="w-4 h-4" />
              <span>View Past Investigations</span>
            </button>
          </div>
        </div>

        {/* Decorative Grid Lines */}
        <div className="absolute right-0 top-0 bottom-0 w-1/3 opacity-10 pointer-events-none bg-[radial-gradient(#10b981_1px,transparent_1px)] [background-size:16px_16px]"></div>
      </div>

      {/* Primary Metrics Grid */}
      <div className="grid grid-cols-2 md:grid-cols-4 gap-4">
        <div className="p-5 bg-slate-900/80 border border-slate-800 rounded-xl space-y-1">
          <div className="flex items-center justify-between text-slate-400 text-xs font-medium">
            <span>Total Cases</span>
            <Layers className="w-4 h-4 text-slate-400" />
          </div>
          <div className="text-2xl sm:text-3xl font-bold text-slate-50 font-mono">
            {stats?.totalCases ?? '—'}
          </div>
          <p className="text-[11px] text-slate-400">Tracked investigations</p>
        </div>

        <div className="p-5 bg-slate-900/80 border border-slate-800 rounded-xl space-y-1">
          <div className="flex items-center justify-between text-blue-400 text-xs font-medium">
            <span>In Progress</span>
            <Search className="w-4 h-4 text-blue-400" />
          </div>
          <div className="text-2xl sm:text-3xl font-bold text-blue-300 font-mono">
            {(stats?.investigatingCases || 0) + (stats?.inProgressCases || 0)}
          </div>
          <p className="text-[11px] text-slate-400">Diagnosing or fixing</p>
        </div>

        <div className="p-5 bg-slate-900/80 border border-slate-800 rounded-xl space-y-1">
          <div className="flex items-center justify-between text-emerald-400 text-xs font-medium">
            <span>Verified Passed</span>
            <CheckCircle2 className="w-4 h-4 text-emerald-400" />
          </div>
          <div className="text-2xl sm:text-3xl font-bold text-emerald-300 font-mono">
            {stats?.verifiedCases ?? '—'}
          </div>
          <p className="text-[11px] text-emerald-400/90 font-medium">
            {verifiedRate}% tested successfully
          </p>
        </div>

        <div className="p-5 bg-slate-900/80 border border-slate-800 rounded-xl space-y-1">
          <div className="flex items-center justify-between text-purple-400 text-xs font-medium">
            <span>Knowledge Library</span>
            <FileCheck2 className="w-4 h-4 text-purple-400" />
          </div>
          <div className="text-2xl sm:text-3xl font-bold text-purple-300 font-mono">
            {stats?.knowledgeEntriesCount ?? '—'}
          </div>
          <p className="text-[11px] text-slate-400">Saved lessons & fixes</p>
        </div>
      </div>

      {/* Categories / Workflows */}
      <div className="space-y-4">
        <div className="flex items-center justify-between">
          <h2 className="text-lg font-bold text-slate-100 flex items-center gap-2">
            <span>What type of problem do you want to solve?</span>
          </h2>
          <span className="text-xs text-slate-400">Select any problem to investigate</span>
        </div>

        <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-5 gap-3.5">
          {/* Software Card */}
          <div 
            onClick={() => onNavigate('create')}
            className="group p-4 bg-slate-900 border border-slate-800 hover:border-emerald-500/50 rounded-xl transition cursor-pointer flex flex-col justify-between space-y-3"
          >
            <div className="space-y-2.5">
              <div className="w-9 h-9 rounded-lg bg-emerald-950/60 border border-emerald-800/60 flex items-center justify-center text-emerald-400 group-hover:scale-105 transition">
                <Terminal className="w-4 h-4" />
              </div>
              <h3 className="text-sm font-semibold text-slate-100 group-hover:text-emerald-400 transition">
                Software & Code
              </h3>
              <p className="text-xs text-slate-400 leading-relaxed">
                Stack traces, connection timeouts, memory leaks, bugs, and API errors.
              </p>
            </div>
            <div className="flex items-center text-xs text-emerald-400 font-medium gap-1 pt-1">
              <span>Start case</span>
              <ArrowRight className="w-3 h-3 group-hover:translate-x-1 transition" />
            </div>
          </div>

          {/* Academic Math Card */}
          <div 
            onClick={() => onNavigate('create')}
            className="group p-4 bg-slate-900 border border-slate-800 hover:border-indigo-500/50 rounded-xl transition cursor-pointer flex flex-col justify-between space-y-3"
          >
            <div className="space-y-2.5">
              <div className="w-9 h-9 rounded-lg bg-indigo-950/60 border border-indigo-800/60 flex items-center justify-center text-indigo-400 group-hover:scale-105 transition">
                <Binary className="w-4 h-4" />
              </div>
              <h3 className="text-sm font-semibold text-slate-100 group-hover:text-indigo-400 transition">
                Study & Student
              </h3>
              <p className="text-xs text-slate-400 leading-relaxed">
                Math equations, physics problems, homework concepts, and formulas.
              </p>
            </div>
            <div className="flex items-center text-xs text-indigo-400 font-medium gap-1 pt-1">
              <span>Start case</span>
              <ArrowRight className="w-3 h-3 group-hover:translate-x-1 transition" />
            </div>
          </div>

          {/* Engineering Card */}
          <div 
            onClick={() => onNavigate('create')}
            className="group p-4 bg-slate-900 border border-slate-800 hover:border-amber-500/50 rounded-xl transition cursor-pointer flex flex-col justify-between space-y-3"
          >
            <div className="space-y-2.5">
              <div className="w-9 h-9 rounded-lg bg-amber-950/60 border border-amber-800/60 flex items-center justify-center text-amber-400 group-hover:scale-105 transition">
                <Wrench className="w-4 h-4" />
              </div>
              <h3 className="text-sm font-semibold text-slate-100 group-hover:text-amber-400 transition">
                Engineering
              </h3>
              <p className="text-xs text-slate-400 leading-relaxed">
                Civil structural cracks, load safety factors, circuits, and machinery.
              </p>
            </div>
            <div className="flex items-center text-xs text-amber-400 font-medium gap-1 pt-1">
              <span>Start case</span>
              <ArrowRight className="w-3 h-3 group-hover:translate-x-1 transition" />
            </div>
          </div>

          {/* Data & Business Card */}
          <div 
            onClick={() => onNavigate('create')}
            className="group p-4 bg-slate-900 border border-slate-800 hover:border-sky-500/50 rounded-xl transition cursor-pointer flex flex-col justify-between space-y-3"
          >
            <div className="space-y-2.5">
              <div className="w-9 h-9 rounded-lg bg-sky-950/60 border border-sky-800/60 flex items-center justify-center text-sky-400 group-hover:scale-105 transition">
                <BarChart3 className="w-4 h-4" />
              </div>
              <h3 className="text-sm font-semibold text-slate-100 group-hover:text-sky-400 transition">
                Business & Work
              </h3>
              <p className="text-xs text-slate-400 leading-relaxed">
                Sales dips, churn spikes, billing issues, workflow bottlenecks, and costs.
              </p>
            </div>
            <div className="flex items-center text-xs text-sky-400 font-medium gap-1 pt-1">
              <span>Start case</span>
              <ArrowRight className="w-3 h-3 group-hover:translate-x-1 transition" />
            </div>
          </div>

          {/* General Problem Card */}
          <div 
            onClick={() => onNavigate('create')}
            className="group p-4 bg-slate-900 border border-slate-800 hover:border-purple-500/50 rounded-xl transition cursor-pointer flex flex-col justify-between space-y-3"
          >
            <div className="space-y-2.5">
              <div className="w-9 h-9 rounded-lg bg-purple-950/60 border border-purple-800/60 flex items-center justify-center text-purple-400 group-hover:scale-105 transition">
                <HelpCircle className="w-4 h-4" />
              </div>
              <h3 className="text-sm font-semibold text-slate-100 group-hover:text-purple-400 transition">
                Everyday & Other
              </h3>
              <p className="text-xs text-slate-400 leading-relaxed">
                Any practical question, technical inquiry, or general troubleshooting.
              </p>
            </div>
            <div className="flex items-center text-xs text-purple-400 font-medium gap-1 pt-1">
              <span>Start case</span>
              <ArrowRight className="w-3 h-3 group-hover:translate-x-1 transition" />
            </div>
          </div>
        </div>
      </div>

      {/* Recent Investigations Table */}
      <div className="space-y-4">
        <div className="flex items-center justify-between">
          <div>
            <h2 className="text-lg font-bold text-slate-100">Recent Investigations</h2>
            <p className="text-xs text-slate-400">Cases progressing through analysis and verification</p>
          </div>
          <button
            onClick={() => onNavigate('history')}
            className="text-xs text-emerald-400 hover:text-emerald-300 font-medium flex items-center gap-1 transition"
          >
            <span>View All ({cases.length})</span>
            <ArrowRight className="w-3.5 h-3.5" />
          </button>
        </div>

        {loading ? (
          <div className="p-12 text-center text-slate-400 bg-slate-900 border border-slate-800 rounded-xl animate-pulse">
            Loading problem cases...
          </div>
        ) : cases.length === 0 ? (
          <div className="p-12 text-center bg-slate-900 border border-slate-800 rounded-xl space-y-3">
            <p className="text-slate-300 font-medium">No problem cases found</p>
            <button
              onClick={() => onNavigate('create')}
              className="px-4 py-2 bg-emerald-600 text-white rounded-lg text-xs font-semibold"
            >
              Ask First Problem
            </button>
          </div>
        ) : (
          <div className="overflow-x-auto bg-slate-900 border border-slate-800 rounded-xl shadow-sm">
            <table className="w-full text-left text-sm">
              <thead className="bg-slate-950/60 text-slate-400 text-xs uppercase tracking-wider border-b border-slate-800">
                <tr>
                  <th className="px-5 py-3 font-medium">Problem Case</th>
                  <th className="px-4 py-3 font-medium">Category</th>
                  <th className="px-4 py-3 font-medium">Urgency</th>
                  <th className="px-4 py-3 font-medium">Status</th>
                  <th className="px-4 py-3 font-medium">Verification</th>
                  <th className="px-4 py-3 font-medium text-right">Action</th>
                </tr>
              </thead>
              <tbody className="divide-y divide-slate-800/80">
                {cases.slice(0, 6).map((c) => (
                  <tr 
                    key={c.id} 
                    onClick={() => onNavigate('workspace', c.id)}
                    className="hover:bg-slate-800/40 transition cursor-pointer group"
                  >
                    <td className="px-5 py-4">
                      <div className="font-medium text-slate-100 group-hover:text-emerald-400 transition line-clamp-1">
                        {c.title}
                      </div>
                      <div className="text-xs text-slate-400 line-clamp-1 mt-0.5">
                        {c.goal}
                      </div>
                    </td>
                    <td className="px-4 py-4 whitespace-nowrap">
                      <CategoryBadge category={c.category} />
                    </td>
                    <td className="px-4 py-4 whitespace-nowrap">
                      <UrgencyBadge urgency={c.urgency} />
                    </td>
                    <td className="px-4 py-4 whitespace-nowrap">
                      <StatusBadge status={c.status} />
                    </td>
                    <td className="px-4 py-4 whitespace-nowrap text-xs">
                      {c.hasVerifiedRun ? (
                        <span className="inline-flex items-center gap-1 text-emerald-400 font-medium">
                          <CheckCircle2 className="w-3.5 h-3.5" />
                          <span>Passed Test</span>
                        </span>
                      ) : c.verificationRunsCount > 0 ? (
                        <span className="text-amber-400">
                          {c.verificationRunsCount} test(s) run
                        </span>
                      ) : (
                        <span className="text-slate-500 flex items-center gap-1">
                          <Clock className="w-3.5 h-3.5" />
                          <span>Not Yet Tested</span>
                        </span>
                      )}
                    </td>
                    <td className="px-4 py-4 whitespace-nowrap text-right">
                      <button 
                        onClick={(e) => { e.stopPropagation(); onNavigate('workspace', c.id); }}
                        className="text-xs text-slate-300 group-hover:text-emerald-400 font-medium inline-flex items-center gap-1 hover:underline"
                      >
                        <span>Open</span>
                        <ArrowRight className="w-3 h-3" />
                      </button>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        )}
      </div>
    </div>
  );
};
