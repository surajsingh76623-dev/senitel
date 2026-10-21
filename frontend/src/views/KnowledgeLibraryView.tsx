import React, { useState, useEffect } from 'react';
import { KnowledgeEntry } from '../types';
import { api } from '../services/api';
import { 
  BookOpen, 
  Search, 
  Tag, 
  CheckCircle2, 
  ShieldCheck, 
  Calendar, 
  User, 
  ExternalLink 
} from 'lucide-react';

interface Props {
  onOpenCase?: (caseId: string) => void;
}

export const KnowledgeLibraryView: React.FC<Props> = ({ onOpenCase }) => {
  const [entries, setEntries] = useState<KnowledgeEntry[]>([]);
  const [loading, setLoading] = useState(true);
  const [searchQuery, setSearchQuery] = useState('');
  const [selectedCategory, setSelectedCategory] = useState<string>('ALL');

  const loadEntries = async () => {
    setLoading(true);
    try {
      const data = await api.getKnowledge(
        searchQuery || undefined,
        selectedCategory !== 'ALL' ? selectedCategory : undefined
      );
      setEntries(data);
    } catch (err) {
      console.error('Failed to load knowledge entries:', err);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    loadEntries();
  }, [searchQuery, selectedCategory]);

  return (
    <div className="space-y-6 pb-16 animate-fadeIn">
      {/* Header */}
      <div className="bg-slate-900 border border-slate-800 rounded-2xl p-6 shadow-xl space-y-2">
        <div className="inline-flex items-center gap-2 px-3 py-1 rounded-full bg-purple-950/60 border border-purple-800/60 text-purple-300 text-xs font-semibold">
          <BookOpen className="w-4 h-4" />
          <span>Reusable Knowledge Library</span>
        </div>
        <h1 className="text-2xl sm:text-3xl font-extrabold text-slate-100 tracking-tight">
          Approved Lessons & Verified Patterns
        </h1>
        <p className="text-xs sm:text-sm text-slate-400 max-w-2xl">
          Only investigations with explicit user consent and anonymized sensitive variables are preserved here for cross-team pattern reuse.
        </p>
      </div>

      {/* Search & Category Filter */}
      <div className="grid grid-cols-1 sm:grid-cols-3 gap-3 bg-slate-900 border border-slate-800 p-4 rounded-xl shadow-sm">
        <div className="sm:col-span-2 relative">
          <Search className="w-4 h-4 absolute left-3 top-3 text-slate-500" />
          <input
            type="text"
            value={searchQuery}
            onChange={(e) => setSearchQuery(e.target.value)}
            placeholder="Search lessons by keyword, tags, or problem pattern..."
            className="w-full bg-slate-950 border border-slate-800 rounded-lg pl-9 pr-3 py-2 text-xs text-slate-100 placeholder:text-slate-600 focus:outline-none focus:border-purple-500"
          />
        </div>

        <div>
          <select
            value={selectedCategory}
            onChange={(e) => setSelectedCategory(e.target.value)}
            className="w-full bg-slate-950 border border-slate-800 rounded-lg px-3 py-2 text-xs text-slate-100 focus:outline-none focus:border-purple-500"
          >
            <option value="ALL">All Categories</option>
            <option value="SOFTWARE_DEBUGGING">Software Debugging</option>
            <option value="ACADEMIC_MATH">Academic & Math</option>
            <option value="DATA_BUSINESS_ANALYSIS">Data & Business</option>
          </select>
        </div>
      </div>

      {/* Knowledge Articles Grid */}
      {loading ? (
        <div className="p-12 text-center text-slate-400 bg-slate-900 border border-slate-800 rounded-xl animate-pulse">
          Loading approved knowledge entries...
        </div>
      ) : entries.length === 0 ? (
        <div className="p-12 text-center bg-slate-900 border border-slate-800 rounded-xl space-y-2">
          <BookOpen className="w-8 h-8 text-slate-500 mx-auto" />
          <p className="text-slate-300 font-medium">No knowledge entries found matching your query.</p>
          <p className="text-xs text-slate-500">
            Cases can be added here from the Workspace once a solution is tested and explicitly approved.
          </p>
        </div>
      ) : (
        <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
          {entries.map((entry) => (
            <div
              key={entry.id}
              className="bg-slate-900 border border-slate-800 hover:border-slate-700 rounded-2xl p-6 shadow-md transition space-y-4 flex flex-col justify-between"
            >
              <div className="space-y-3">
                <div className="flex items-center justify-between">
                  <span className="text-[11px] font-mono uppercase px-2.5 py-0.5 rounded-full bg-purple-950 text-purple-300 border border-purple-800 font-semibold">
                    {entry.category.replace(/_/g, ' ')}
                  </span>
                  <div className="flex items-center gap-1.5 text-xs text-emerald-400 font-medium">
                    <ShieldCheck className="w-4 h-4" />
                    <span>Approved & Anonymized</span>
                  </div>
                </div>

                <h3 className="text-base font-bold text-slate-100 leading-snug">
                  {entry.title}
                </h3>

                {/* Problem & Solution Summaries */}
                <div className="space-y-2 text-xs">
                  <div className="p-3 rounded-xl bg-slate-950/70 border border-slate-800 text-slate-300 space-y-1">
                    <span className="font-semibold text-slate-400 block uppercase tracking-wider text-[10px]">Diagnosed Problem:</span>
                    <p className="leading-relaxed">{entry.problemSummary}</p>
                  </div>

                  <div className="p-3 rounded-xl bg-slate-950/70 border border-slate-800 text-slate-300 space-y-1">
                    <span className="font-semibold text-emerald-400 block uppercase tracking-wider text-[10px]">Verified Resolution:</span>
                    <p className="leading-relaxed">{entry.solutionSummary}</p>
                  </div>

                  <div className="p-3 rounded-xl bg-slate-950/70 border border-slate-800 text-slate-300 space-y-1">
                    <span className="font-semibold text-cyan-400 block uppercase tracking-wider text-[10px]">Verification Proof:</span>
                    <p className="leading-relaxed font-mono text-[11px]">{entry.verificationSummary}</p>
                  </div>
                </div>

                {/* Lessons Learned Highlight */}
                <div className="p-3.5 rounded-xl bg-purple-950/30 border border-purple-800/40 text-xs text-purple-200 space-y-1">
                  <span className="font-bold text-purple-300 block uppercase tracking-wider text-[10px]">Architectural Takeaway:</span>
                  <p className="leading-relaxed italic">"{entry.anonymizedLessons}"</p>
                </div>

                {/* Tags */}
                {entry.tags && (
                  <div className="flex items-center gap-1.5 flex-wrap pt-1">
                    <Tag className="w-3.5 h-3.5 text-slate-500" />
                    {entry.tags.split(',').map((tag, idx) => (
                      <span key={idx} className="text-[10px] font-mono px-2 py-0.5 rounded bg-slate-800 text-slate-300 border border-slate-700">
                        {tag.trim()}
                      </span>
                    ))}
                  </div>
                )}
              </div>

              {/* Footer Meta */}
              <div className="pt-4 border-t border-slate-800/80 flex items-center justify-between text-[11px] text-slate-500 font-mono">
                <span className="flex items-center gap-1">
                  <User className="w-3.5 h-3.5 text-slate-400" />
                  <span>Approved by {entry.approvedBy}</span>
                </span>
                <span className="flex items-center gap-1">
                  <Calendar className="w-3.5 h-3.5 text-slate-400" />
                  <span>{new Date(entry.approvedAt).toLocaleDateString()}</span>
                </span>
              </div>
            </div>
          ))}
        </div>
      )}
    </div>
  );
};
