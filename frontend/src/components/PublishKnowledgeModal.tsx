import React, { useState } from 'react';
import { ProblemCaseDetail } from '../types';
import { BookOpen, ShieldCheck, X } from 'lucide-react';

interface Props {
  isOpen: boolean;
  problemCase: ProblemCaseDetail;
  onClose: () => void;
  onPublish: (data: {
    title: string;
    category: string;
    problemSummary: string;
    solutionSummary: string;
    verificationSummary: string;
    anonymizedLessons: string;
    tags: string;
    approvedBy: string;
  }) => Promise<void>;
}

export const PublishKnowledgeModal: React.FC<Props> = ({
  isOpen,
  problemCase,
  onClose,
  onPublish,
}) => {
  const selectedSolution = problemCase.candidateSolutions.find(s => s.id === problemCase.selectedSolutionId)
    || problemCase.candidateSolutions[0];

  const lastVerification = problemCase.verificationRuns[0];

  const [title, setTitle] = useState(problemCase.title);
  const [problemSummary, setProblemSummary] = useState(problemCase.description);
  const [solutionSummary, setSolutionSummary] = useState(selectedSolution ? selectedSolution.summary : '');
  const [verificationSummary, setVerificationSummary] = useState(lastVerification ? `${lastVerification.testProcedure} -> Result: ${lastVerification.actualResult || 'Passed'}` : 'Empirically tested and confirmed.');
  const [anonymizedLessons, setAnonymizedLessons] = useState('Key lesson: Avoid holding transactional database connections across network I/O boundaries.');
  const [tags, setTags] = useState(problemCase.category.toLowerCase().replace(/_/g, ', '));
  const [approvedBy, setApprovedBy] = useState('Senior Investigator');
  const [confirmedConsent, setConfirmedConsent] = useState(false);
  const [isSubmitting, setIsSubmitting] = useState(false);
  const [error, setError] = useState<string | null>(null);

  if (!isOpen) return null;

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!confirmedConsent) {
      setError('Explicit user approval is required to publish into the reusable knowledge library.');
      return;
    }
    setError(null);
    setIsSubmitting(true);
    try {
      await onPublish({
        title,
        category: problemCase.category,
        problemSummary,
        solutionSummary,
        verificationSummary,
        anonymizedLessons,
        tags,
        approvedBy,
      });
      onClose();
    } catch (err: any) {
      setError(err.message || 'Failed to publish knowledge entry');
    } finally {
      setIsSubmitting(false);
    }
  };

  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center p-4 bg-black/75 backdrop-blur-sm overflow-y-auto">
      <div className="bg-slate-900 border border-slate-800 rounded-2xl max-w-2xl w-full p-6 shadow-2xl space-y-5 my-8">
        <div className="flex items-start justify-between border-b border-slate-800 pb-4">
          <div className="flex items-center gap-3">
            <div className="p-2.5 rounded-xl bg-emerald-950/60 text-emerald-400 border border-emerald-800/60">
              <BookOpen className="w-6 h-6" />
            </div>
            <div>
              <h3 className="text-xl font-semibold text-slate-100">Publish to Knowledge Library</h3>
              <p className="text-xs text-slate-400">Explicitly approve and preserve verified lessons for team reference</p>
            </div>
          </div>
          <button
            onClick={onClose}
            className="text-slate-400 hover:text-slate-200 p-1.5 rounded-lg hover:bg-slate-800 transition"
          >
            <X className="w-5 h-5" />
          </button>
        </div>

        {error && (
          <div className="p-3 bg-red-950/40 border border-red-800/60 rounded-lg text-sm text-red-300">
            {error}
          </div>
        )}

        <div className="p-3 bg-amber-950/30 border border-amber-800/40 rounded-lg flex items-start gap-2.5 text-xs text-amber-200">
          <ShieldCheck className="w-4 h-4 mt-0.5 shrink-0 text-amber-400" />
          <span>
            <strong>Privacy & Anonymization Policy:</strong> Please ensure all customer names, confidential tokens, internal IPs, and sensitive credentials have been scrubbed before publishing.
          </span>
        </div>

        <form onSubmit={handleSubmit} className="space-y-4 text-sm">
          <div>
            <label className="block text-xs font-medium text-slate-300 mb-1">Article Title</label>
            <input
              type="text"
              value={title}
              onChange={(e) => setTitle(e.target.value)}
              required
              className="w-full bg-slate-950 border border-slate-800 rounded-lg px-3 py-2 text-slate-100 focus:outline-none focus:border-emerald-500"
            />
          </div>

          <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
            <div>
              <label className="block text-xs font-medium text-slate-300 mb-1">Problem Summary</label>
              <textarea
                rows={3}
                value={problemSummary}
                onChange={(e) => setProblemSummary(e.target.value)}
                required
                className="w-full bg-slate-950 border border-slate-800 rounded-lg px-3 py-2 text-slate-100 focus:outline-none focus:border-emerald-500 resize-none text-xs"
              />
            </div>
            <div>
              <label className="block text-xs font-medium text-slate-300 mb-1">Solution Summary</label>
              <textarea
                rows={3}
                value={solutionSummary}
                onChange={(e) => setSolutionSummary(e.target.value)}
                required
                className="w-full bg-slate-950 border border-slate-800 rounded-lg px-3 py-2 text-slate-100 focus:outline-none focus:border-emerald-500 resize-none text-xs"
              />
            </div>
          </div>

          <div>
            <label className="block text-xs font-medium text-slate-300 mb-1">Verification Summary</label>
            <input
              type="text"
              value={verificationSummary}
              onChange={(e) => setVerificationSummary(e.target.value)}
              required
              className="w-full bg-slate-950 border border-slate-800 rounded-lg px-3 py-2 text-slate-100 focus:outline-none focus:border-emerald-500 text-xs"
            />
          </div>

          <div>
            <label className="block text-xs font-medium text-slate-300 mb-1">Anonymized Lessons Learned</label>
            <textarea
              rows={2}
              value={anonymizedLessons}
              onChange={(e) => setAnonymizedLessons(e.target.value)}
              required
              placeholder="e.g. In high concurrency payment flows, always decouple third party calls..."
              className="w-full bg-slate-950 border border-slate-800 rounded-lg px-3 py-2 text-slate-100 focus:outline-none focus:border-emerald-500 resize-none text-xs"
            />
          </div>

          <div className="grid grid-cols-2 gap-4">
            <div>
              <label className="block text-xs font-medium text-slate-300 mb-1">Search Tags (comma-separated)</label>
              <input
                type="text"
                value={tags}
                onChange={(e) => setTags(e.target.value)}
                className="w-full bg-slate-950 border border-slate-800 rounded-lg px-3 py-2 text-slate-100 focus:outline-none focus:border-emerald-500 text-xs"
              />
            </div>
            <div>
              <label className="block text-xs font-medium text-slate-300 mb-1">Reviewer / Approver</label>
              <input
                type="text"
                value={approvedBy}
                onChange={(e) => setApprovedBy(e.target.value)}
                required
                className="w-full bg-slate-950 border border-slate-800 rounded-lg px-3 py-2 text-slate-100 focus:outline-none focus:border-emerald-500 text-xs"
              />
            </div>
          </div>

          <div className="pt-2">
            <label className="flex items-center gap-2 cursor-pointer select-none">
              <input
                type="checkbox"
                checked={confirmedConsent}
                onChange={(e) => setConfirmedConsent(e.target.checked)}
                className="w-4 h-4 rounded border-slate-700 bg-slate-950 text-emerald-600 focus:ring-emerald-500"
              />
              <span className="text-xs text-slate-300">
                I explicitly approve adding this anonymized investigation outcome to the team knowledge library.
              </span>
            </label>
          </div>

          <div className="flex items-center justify-end gap-3 pt-3 border-t border-slate-800">
            <button
              type="button"
              onClick={onClose}
              disabled={isSubmitting}
              className="px-4 py-2 text-xs font-medium text-slate-300 hover:bg-slate-800 rounded-lg transition"
            >
              Cancel
            </button>
            <button
              type="submit"
              disabled={isSubmitting || !confirmedConsent}
              className="px-4 py-2 text-xs font-medium text-white bg-emerald-600 hover:bg-emerald-700 disabled:opacity-50 disabled:cursor-not-allowed rounded-lg transition shadow-sm"
            >
              {isSubmitting ? 'Publishing...' : 'Approve & Publish to Library'}
            </button>
          </div>
        </form>
      </div>
    </div>
  );
};
