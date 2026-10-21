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
  Clock
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
            <span>AI Problem Investigation & Falsifiable Verification</span>
          </div>
          <h1 className="text-3xl font-extrabold text-slate-50 tracking-tight sm:text-4xl">
            Turn Ambiguous Problems into <span className="text-emerald-400">Verified Resolutions</span>
          </h1>
          <p className="text-sm sm:text-base text-slate-300 leading-relaxed">
            SENTINEL separates facts, assumptions, and critical unknowns, compares candidate solutions with transparent trade-offs, and enforces independently testable verification before claiming success.
          </p>
          <div className="flex flex-wrap items-center gap-3 pt-2">
            <button
              onClick={() => onNavigate('create')}
              className="flex items-center gap-2 px-5 py-2.5 bg-emerald-600 hover:bg-emerald-500 text-white rounded-xl text-sm font-semibold transition shadow-lg shadow-emerald-950/50"
            >
              <PlusCircle className="w-4 h-4" />
              <span>Submit Problem Case</span>
            </button>
            <button
              onClick={() => onNavigate('history')}
              className="flex items-center gap-2 px-4 py-2.5 bg-slate-800 hover:bg-slate-700/80 text-slate-200 border border-slate-700 rounded-xl text-sm font-medium transition"
            >
              <Search className="w-4 h-4" />
              <span>Explore All Cases</span>
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
            <span>Total Investigations</span>
            <Layers className="w-4 h-4 text-slate-400" />
          </div>
          <div className="text-2xl sm:text-3xl font-bold text-slate-50 font-mono">
            {stats?.totalCases ?? '—'}
          </div>
          <p className="text-[11px] text-slate-400">Across 3 deep workflows</p>
        </div>

        <div className="p-5 bg-slate-900/80 border border-slate-800 rounded-xl space-y-1">
          <div className="flex items-center justify-between text-blue-400 text-xs font-medium">
            <span>Active Diagnostics</span>
            <Search className="w-4 h-4 text-blue-400" />
          </div>
          <div className="text-2xl sm:text-3xl font-bold text-blue-300 font-mono">
            {(stats?.investigatingCases || 0) + (stats?.inProgressCases || 0)}
          </div>
          <p className="text-[11px] text-slate-400">Investigating or executing plan</p>
        </div>

        <div className="p-5 bg-slate-900/80 border border-slate-800 rounded-xl space-y-1">
          <div className="flex items-center justify-between text-emerald-400 text-xs font-medium">
            <span>Independently Verified</span>
            <CheckCircle2 className="w-4 h-4 text-emerald-400" />
          </div>
          <div className="text-2xl sm:text-3xl font-bold text-emerald-300 font-mono">
            {stats?.verifiedCases ?? '—'}
          </div>
          <p className="text-[11px] text-emerald-400/90 font-medium">
            {verifiedRate}% empirical pass rate
          </p>
        </div>

        <div className="p-5 bg-slate-900/80 border border-slate-800 rounded-xl space-y-1">
          <div className="flex items-center justify-between text-purple-400 text-xs font-medium">
            <span>Preserved Lessons</span>
            <FileCheck2 className="w-4 h-4 text-purple-400" />
          </div>
          <div className="text-2xl sm:text-3xl font-bold text-purple-300 font-mono">
            {stats?.knowledgeEntriesCount ?? '—'}
          </div>
          <p className="text-[11px] text-slate-400">Approved reusable articles</p>
        </div>
      </div>

      {/* Deep Workflows Banner */}
      <div className="space-y-4">
        <div className="flex items-center justify-between">
          <h2 className="text-lg font-bold text-slate-100 flex items-center gap-2">
            <span>Core Deep Investigation Workflows</span>
          </h2>
          <span className="text-xs text-slate-400">Deep domain telemetry & deterministic verification</span>
        </div>

        <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
          {/* Software Card */}
          <div 
            onClick={() => onNavigate('create')}
            className="group p-5 bg-slate-900 border border-slate-800 hover:border-emerald-500/50 rounded-xl transition cursor-pointer space-y-3"
          >
            <div className="w-10 h-10 rounded-lg bg-emerald-950/60 border border-emerald-800/60 flex items-center justify-center text-emerald-400 group-hover:scale-105 transition">
              <Terminal className="w-5 h-5" />
            </div>
            <div>
              <h3 className="font-semibold text-slate-100 group-hover:text-emerald-400 transition">
                1. Software Debugging
              </h3>
              <p className="text-xs text-slate-400 mt-1 leading-relaxed">
                Stack traces, connection starvation, thread pool deadlocks, memory leaks, and concurrency bottlenecks.
              </p>
            </div>
            <div className="pt-1 flex items-center text-xs text-emerald-400 font-medium gap-1">
              <span>Start debug case</span>
              <ArrowRight className="w-3.5 h-3.5 group-hover:translate-x-1 transition" />
            </div>
          </div>

          {/* Academic Math Card */}
          <div 
            onClick={() => onNavigate('create')}
            className="group p-5 bg-slate-900 border border-slate-800 hover:border-indigo-500/50 rounded-xl transition cursor-pointer space-y-3"
          >
            <div className="w-10 h-10 rounded-lg bg-indigo-950/60 border border-indigo-800/60 flex items-center justify-center text-indigo-400 group-hover:scale-105 transition">
              <Binary className="w-5 h-5" />
            </div>
            <div>
              <h3 className="font-semibold text-slate-100 group-hover:text-indigo-400 transition">
                2. Academic & Math Problem-Solving
              </h3>
              <p className="text-xs text-slate-400 mt-1 leading-relaxed">
                PINNs, gradient explosion, boundary condition proofs, Cole-Hopf analytical verification, and relative L2 error norms.
              </p>
            </div>
            <div className="pt-1 flex items-center text-xs text-indigo-400 font-medium gap-1">
              <span>Start math case</span>
              <ArrowRight className="w-3.5 h-3.5 group-hover:translate-x-1 transition" />
            </div>
          </div>

          {/* Data & Business Card */}
          <div 
            onClick={() => onNavigate('create')}
            className="group p-5 bg-slate-900 border border-slate-800 hover:border-sky-500/50 rounded-xl transition cursor-pointer space-y-3"
          >
            <div className="w-10 h-10 rounded-lg bg-sky-950/60 border border-sky-800/60 flex items-center justify-center text-sky-400 group-hover:scale-105 transition">
              <BarChart3 className="w-5 h-5" />
            </div>
            <div>
              <h3 className="font-semibold text-slate-100 group-hover:text-sky-400 transition">
                3. Data & Business Analysis
              </h3>
              <p className="text-xs text-slate-400 mt-1 leading-relaxed">
                Customer churn anomalies, pricing restructuring, cohort survival, CAC payback, and gross margin constraint modeling.
              </p>
            </div>
            <div className="pt-1 flex items-center text-xs text-sky-400 font-medium gap-1">
              <span>Start business case</span>
              <ArrowRight className="w-3.5 h-3.5 group-hover:translate-x-1 transition" />
            </div>
          </div>
        </div>
      </div>

      {/* Recent Investigations Table */}
      <div className="space-y-4">
        <div className="flex items-center justify-between">
          <div>
            <h2 className="text-lg font-bold text-slate-100">Recent Problem Investigations</h2>
            <p className="text-xs text-slate-400">All cases progress through explicit verification checkpoints</p>
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
            Loading investigation cases...
          </div>
        ) : cases.length === 0 ? (
          <div className="p-12 text-center bg-slate-900 border border-slate-800 rounded-xl space-y-3">
            <p className="text-slate-300 font-medium">No problem cases found</p>
            <button
              onClick={() => onNavigate('create')}
              className="px-4 py-2 bg-emerald-600 text-white rounded-lg text-xs font-semibold"
            >
              Create First Case
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
                  <th className="px-4 py-3 font-medium">Lifecycle Status</th>
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
                          {c.verificationRunsCount} run(s) logged
                        </span>
                      ) : (
                        <span className="text-slate-500 flex items-center gap-1">
                          <Clock className="w-3.5 h-3.5" />
                          <span>Unverified</span>
                        </span>
                      )}
                    </td>
                    <td className="px-4 py-4 whitespace-nowrap text-right">
                      <button 
                        onClick={(e) => { e.stopPropagation(); onNavigate('workspace', c.id); }}
                        className="text-xs text-slate-300 group-hover:text-emerald-400 font-medium inline-flex items-center gap-1 hover:underline"
                      >
                        <span>Workspace</span>
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
