import React, { useState } from 'react';
import { 
  ProblemCaseSummary, 
  ProblemCategory, 
  ProblemStatus 
} from '../types';
import { StatusBadge } from '../components/StatusBadge';
import { CategoryBadge } from '../components/CategoryBadge';
import { UrgencyBadge } from '../components/UrgencyBadge';
import { 
  Search, 
  Filter, 
  Columns, 
  CheckCircle2, 
  Clock, 
  X, 
  ArrowRight,
  PlusCircle
} from 'lucide-react';

interface Props {
  cases: ProblemCaseSummary[];
  loading: boolean;
  onSelectCase: (caseId: string) => void;
  onNewCase: () => void;
}

export const CaseHistoryView: React.FC<Props> = ({ cases, loading, onSelectCase, onNewCase }) => {
  const [searchQuery, setSearchQuery] = useState('');
  const [selectedCategory, setSelectedCategory] = useState<string>('ALL');
  const [selectedStatus, setSelectedStatus] = useState<string>('ALL');

  // Multi-case compare state
  const [selectedForCompare, setSelectedForCompare] = useState<string[]>([]);
  const [showCompareModal, setShowCompareModal] = useState(false);

  const filteredCases = cases.filter((c) => {
    const matchesQuery = !searchQuery || 
      c.title.toLowerCase().includes(searchQuery.toLowerCase()) || 
      c.description.toLowerCase().includes(searchQuery.toLowerCase()) ||
      c.goal.toLowerCase().includes(searchQuery.toLowerCase());
    const matchesCategory = selectedCategory === 'ALL' || c.category === selectedCategory;
    const matchesStatus = selectedStatus === 'ALL' || c.status === selectedStatus;
    return matchesQuery && matchesCategory && matchesStatus;
  });

  const toggleCompare = (id: string) => {
    if (selectedForCompare.includes(id)) {
      setSelectedForCompare(selectedForCompare.filter((item) => item !== id));
    } else {
      if (selectedForCompare.length >= 3) {
        alert('You can compare up to 3 cases at a time.');
        return;
      }
      setSelectedForCompare([...selectedForCompare, id]);
    }
  };

  const compareCasesData = cases.filter((c) => selectedForCompare.includes(c.id));

  return (
    <div className="space-y-6 pb-16 animate-fadeIn">
      {/* Header */}
      <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-4">
        <div>
          <h1 className="text-2xl font-bold text-slate-100">Investigation Case History</h1>
          <p className="text-xs sm:text-sm text-slate-400">
            Search, filter, and compare prior technical investigations and verified outcomes.
          </p>
        </div>

        <div className="flex items-center gap-2">
          {selectedForCompare.length >= 2 && (
            <button
              onClick={() => setShowCompareModal(true)}
              className="flex items-center gap-1.5 px-3.5 py-2 bg-indigo-600 hover:bg-indigo-500 text-white rounded-xl text-xs font-semibold transition shadow-md"
            >
              <Columns className="w-4 h-4" />
              <span>Compare Selected ({selectedForCompare.length})</span>
            </button>
          )}

          <button
            onClick={onNewCase}
            className="flex items-center gap-1.5 px-4 py-2 bg-emerald-600 hover:bg-emerald-500 text-white rounded-xl text-xs font-semibold transition shadow-md"
          >
            <PlusCircle className="w-4 h-4" />
            <span>New Case</span>
          </button>
        </div>
      </div>

      {/* Filter Toolbar */}
      <div className="grid grid-cols-1 sm:grid-cols-3 gap-3 bg-slate-900 border border-slate-800 p-4 rounded-xl shadow-sm">
        {/* Search */}
        <div className="relative">
          <Search className="w-4 h-4 absolute left-3 top-3 text-slate-500" />
          <input
            type="text"
            value={searchQuery}
            onChange={(e) => setSearchQuery(e.target.value)}
            placeholder="Search keywords, stack traces, metrics..."
            className="w-full bg-slate-950 border border-slate-800 rounded-lg pl-9 pr-3 py-2 text-xs text-slate-100 placeholder:text-slate-600 focus:outline-none focus:border-emerald-500"
          />
        </div>

        {/* Category Filter */}
        <div className="flex items-center gap-2">
          <Filter className="w-3.5 h-3.5 text-slate-500 shrink-0" />
          <select
            value={selectedCategory}
            onChange={(e) => setSelectedCategory(e.target.value)}
            className="w-full bg-slate-950 border border-slate-800 rounded-lg px-3 py-2 text-xs text-slate-100 focus:outline-none focus:border-emerald-500"
          >
            <option value="ALL">All Categories</option>
            <option value="SOFTWARE_DEBUGGING">Software Debugging</option>
            <option value="ACADEMIC_MATH">Academic & Math</option>
            <option value="DATA_BUSINESS_ANALYSIS">Data & Business</option>
            <option value="GENERAL">General Inquiry</option>
          </select>
        </div>

        {/* Status Filter */}
        <div>
          <select
            value={selectedStatus}
            onChange={(e) => setSelectedStatus(e.target.value)}
            className="w-full bg-slate-950 border border-slate-800 rounded-lg px-3 py-2 text-xs text-slate-100 focus:outline-none focus:border-emerald-500"
          >
            <option value="ALL">All Lifecycle Statuses</option>
            <option value="DRAFT">Draft</option>
            <option value="INVESTIGATING">Investigating</option>
            <option value="PROPOSED">Proposed</option>
            <option value="IN_PROGRESS">In Progress</option>
            <option value="USER_REPORTED_SUCCESS">User-Reported Success</option>
            <option value="VERIFIED">Verified</option>
            <option value="FAILED">Failed</option>
            <option value="NEEDS_MORE_EVIDENCE">Needs More Evidence</option>
          </select>
        </div>
      </div>

      {/* Case List Table */}
      {loading ? (
        <div className="p-12 text-center text-slate-400 bg-slate-900 border border-slate-800 rounded-xl animate-pulse">
          Loading investigation history...
        </div>
      ) : filteredCases.length === 0 ? (
        <div className="p-12 text-center bg-slate-900 border border-slate-800 rounded-xl space-y-3">
          <p className="text-slate-300 font-medium">No problem cases match your filter criteria.</p>
          <button
            onClick={() => { setSearchQuery(''); setSelectedCategory('ALL'); setSelectedStatus('ALL'); }}
            className="px-3 py-1.5 bg-slate-800 text-slate-300 rounded-lg text-xs"
          >
            Clear Filters
          </button>
        </div>
      ) : (
        <div className="overflow-x-auto bg-slate-900 border border-slate-800 rounded-xl shadow-sm">
          <table className="w-full text-left text-sm">
            <thead className="bg-slate-950/70 text-slate-400 text-xs uppercase tracking-wider border-b border-slate-800">
              <tr>
                <th className="w-10 px-4 py-3 text-center">
                  <span className="sr-only">Compare Checkbox</span>
                </th>
                <th className="px-4 py-3 font-medium">Investigation Case</th>
                <th className="px-4 py-3 font-medium">Category</th>
                <th className="px-4 py-3 font-medium">Urgency</th>
                <th className="px-4 py-3 font-medium">Lifecycle Status</th>
                <th className="px-4 py-3 font-medium">Verification</th>
                <th className="px-4 py-3 font-medium text-right">Created</th>
              </tr>
            </thead>
            <tbody className="divide-y divide-slate-800/80">
              {filteredCases.map((c) => {
                const isChecked = selectedForCompare.includes(c.id);
                return (
                  <tr
                    key={c.id}
                    onClick={() => onSelectCase(c.id)}
                    className="hover:bg-slate-800/40 transition cursor-pointer group"
                  >
                    <td className="px-4 py-4 text-center" onClick={(e) => e.stopPropagation()}>
                      <input
                        type="checkbox"
                        checked={isChecked}
                        onChange={() => toggleCompare(c.id)}
                        className="w-4 h-4 rounded border-slate-700 bg-slate-950 text-emerald-600 focus:ring-emerald-500 cursor-pointer"
                        title="Select to compare side-by-side"
                      />
                    </td>
                    <td className="px-4 py-4">
                      <div className="font-semibold text-slate-100 group-hover:text-emerald-400 transition line-clamp-1">
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
                          <span>Passed Run</span>
                        </span>
                      ) : (
                        <span className="text-slate-500 flex items-center gap-1">
                          <Clock className="w-3.5 h-3.5" />
                          <span>{c.verificationRunsCount > 0 ? `${c.verificationRunsCount} logged` : 'Unverified'}</span>
                        </span>
                      )}
                    </td>
                    <td className="px-4 py-4 whitespace-nowrap text-right text-xs text-slate-400 font-mono">
                      {new Date(c.createdAt).toLocaleDateString()}
                    </td>
                  </tr>
                );
              })}
            </tbody>
          </table>
        </div>
      )}

      {/* Multi-Case Side-by-Side Comparison Modal */}
      {showCompareModal && (
        <div className="fixed inset-0 z-50 flex items-center justify-center p-4 bg-black/80 backdrop-blur-md overflow-y-auto">
          <div className="bg-slate-900 border border-slate-800 rounded-2xl max-w-5xl w-full p-6 shadow-2xl space-y-6 my-8">
            <div className="flex items-center justify-between border-b border-slate-800 pb-4">
              <div>
                <h3 className="text-xl font-bold text-slate-100 flex items-center gap-2">
                  <Columns className="w-5 h-5 text-indigo-400" />
                  <span>Comparative Investigation Analysis ({compareCasesData.length} Cases)</span>
                </h3>
                <p className="text-xs text-slate-400 mt-0.5">
                  Side-by-side comparison of goals, diagnostic states, and empirical outcomes.
                </p>
              </div>
              <button
                onClick={() => setShowCompareModal(false)}
                className="text-slate-400 hover:text-slate-200 p-1.5 rounded-lg hover:bg-slate-800"
              >
                <X className="w-5 h-5" />
              </button>
            </div>

            <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
              {compareCasesData.map((c) => (
                <div key={c.id} className="p-4 rounded-xl bg-slate-950 border border-slate-800 space-y-4 text-xs">
                  <div>
                    <div className="flex items-center justify-between mb-2">
                      <CategoryBadge category={c.category} />
                      <UrgencyBadge urgency={c.urgency} />
                    </div>
                    <h4 className="font-bold text-slate-100 text-sm line-clamp-2">{c.title}</h4>
                    <div className="mt-2">
                      <StatusBadge status={c.status} />
                    </div>
                  </div>

                  <div className="space-y-1">
                    <span className="text-slate-400 font-semibold block uppercase tracking-wider text-[10px]">Goal:</span>
                    <p className="text-slate-200 leading-relaxed bg-slate-900 p-2.5 rounded border border-slate-800/80">
                      {c.goal}
                    </p>
                  </div>

                  <div className="space-y-1">
                    <span className="text-slate-400 font-semibold block uppercase tracking-wider text-[10px]">Description:</span>
                    <p className="text-slate-300 leading-relaxed line-clamp-3">
                      {c.description}
                    </p>
                  </div>

                  <div className="grid grid-cols-2 gap-2 pt-2 border-t border-slate-800/80 font-mono text-[11px]">
                    <div>
                      <span className="text-slate-500 block">Solutions:</span>
                      <span className="text-slate-300 font-semibold">{c.solutionsCount} proposed</span>
                    </div>
                    <div>
                      <span className="text-slate-500 block">Actions:</span>
                      <span className="text-slate-300 font-semibold">{c.completedActionItemsCount}/{c.actionItemsCount} done</span>
                    </div>
                  </div>

                  <div className="pt-2">
                    <button
                      onClick={() => {
                        setShowCompareModal(false);
                        onSelectCase(c.id);
                      }}
                      className="w-full py-2 bg-slate-800 hover:bg-emerald-600 text-slate-200 hover:text-white rounded-lg font-semibold transition text-center flex items-center justify-center gap-1.5"
                    >
                      <span>Open Workspace</span>
                      <ArrowRight className="w-3.5 h-3.5" />
                    </button>
                  </div>
                </div>
              ))}
            </div>
          </div>
        </div>
      )}
    </div>
  );
};
