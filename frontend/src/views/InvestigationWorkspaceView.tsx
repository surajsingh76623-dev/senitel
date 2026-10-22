import React, { useState } from 'react';
import { 
  ProblemCaseDetail, 
  ProblemStatus, 
  VerificationStatus,
  VerificationType,
  CandidateSolution,
  ClarificationEntry
} from '../types';
import { api } from '../services/api';
import { StatusBadge } from '../components/StatusBadge';
import { CategoryBadge } from '../components/CategoryBadge';
import { UrgencyBadge } from '../components/UrgencyBadge';
import { PublishKnowledgeModal } from '../components/PublishKnowledgeModal';
import { ConfirmModal } from '../components/ConfirmModal';
import { 
  ArrowLeft, 
  Search, 
  Lightbulb, 
  CheckSquare, 
  ShieldCheck, 
  History, 
  CheckCircle2, 
  AlertCircle, 
  HelpCircle, 
  FileText, 
  Download, 
  Send, 
  Sparkles, 
  Calculator, 
  Plus, 
  Clock, 
  Play, 
  BookOpen,
  ArrowRight,
  TrendingUp,
  Cpu
} from 'lucide-react';

interface Props {
  problemCase: ProblemCaseDetail;
  onRefresh: () => Promise<void>;
  onBack: () => void;
}

export const InvestigationWorkspaceView: React.FC<Props> = ({ problemCase, onRefresh, onBack }) => {
  const [activeTab, setActiveTab] = useState<'investigation' | 'solutions' | 'actions' | 'verification' | 'timeline'>('investigation');

  // Modals
  const [showPublishModal, setShowPublishModal] = useState(false);
  const [confirmAction, setConfirmAction] = useState<{ title: string; message: string; onConfirm: () => Promise<void> } | null>(null);

  // Clarification Q&A
  const [clarificationAnswers, setClarificationAnswers] = useState<Record<string, string>>({});
  const [isClarifying, setIsClarifying] = useState(false);

  // Manual Verification Form
  const [testProcedure, setTestProcedure] = useState('');
  const [expectedResult, setExpectedResult] = useState('');
  const [actualResult, setActualResult] = useState('');
  const [verificationStatus, setVerificationStatus] = useState<VerificationStatus>('PASSED');
  const [verificationType, setVerificationType] = useState<VerificationType>('MANUAL');
  const [evidenceNotes, setEvidenceNotes] = useState('');
  const [recordedBy, setRecordedBy] = useState('Lead Investigator');
  const [isRecordingTest, setIsRecordingTest] = useState(false);

  // Deterministic Calculator Runner State
  const [isCalculating, setIsCalculating] = useState(false);
  const [calcResult, setCalcResult] = useState<any | null>(null);

  // Status Action Handler
  const handleTransitionStatus = async (targetStatus: ProblemStatus, reason?: string) => {
    try {
      await api.transitionStatus(problemCase.id, targetStatus, reason);
      await onRefresh();
    } catch (err: any) {
      alert(err.message || 'Status transition failed.');
    }
  };

  // Select Solution Action
  const handleSelectSolution = async (solutionId: string) => {
    try {
      await api.selectSolution(problemCase.id, solutionId);
      await onRefresh();
      setActiveTab('actions');
    } catch (err: any) {
      alert(err.message || 'Failed to select solution');
    }
  };

  // Toggle Action Step
  const handleToggleAction = async (actionId: string, currentCompleted: boolean) => {
    try {
      await api.toggleActionItem(problemCase.id, actionId, !currentCompleted);
      await onRefresh();
    } catch (err: any) {
      alert(err.message || 'Failed to update action step');
    }
  };

  // Submit Clarification
  const handleSubmitClarifications = async (e: React.FormEvent) => {
    e.preventDefault();
    const responses: ClarificationEntry[] = Object.entries(clarificationAnswers).map(([question, answer]) => ({
      question,
      answer,
    }));
    if (responses.length === 0) return;

    setIsClarifying(true);
    try {
      await api.submitClarification(problemCase.id, responses);
      setClarificationAnswers({});
      await onRefresh();
    } catch (err: any) {
      alert(err.message || 'Failed to submit clarifications');
    } finally {
      setIsClarifying(false);
    }
  };

  // Submit Manual Verification Run
  const handleRecordVerification = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!testProcedure.trim() || !expectedResult.trim()) {
      alert('Test procedure and expected result are required.');
      return;
    }
    setIsRecordingTest(true);
    try {
      await api.recordVerification(problemCase.id, {
        solutionId: problemCase.selectedSolutionId,
        testProcedure: testProcedure.trim(),
        expectedResult: expectedResult.trim(),
        actualResult: actualResult.trim(),
        status: verificationStatus,
        verificationType,
        evidenceNotes: evidenceNotes.trim(),
        recordedBy: recordedBy.trim(),
        isReproducible: true,
      });
      setTestProcedure('');
      setExpectedResult('');
      setActualResult('');
      setEvidenceNotes('');
      await onRefresh();
    } catch (err: any) {
      alert(err.message || 'Failed to record verification');
    } finally {
      setIsRecordingTest(false);
    }
  };

  // Run Deterministic Calculator
  const handleRunDeterministicCheck = async (checkType: string, params: Record<string, any>) => {
    setIsCalculating(true);
    try {
      const res = await api.runDeterministicCheck(problemCase.id, {
        checkType,
        solutionId: problemCase.selectedSolutionId,
        parameters: params,
      });
      setCalcResult(res);
      await onRefresh();
    } catch (err: any) {
      alert(err.message || 'Deterministic check failed');
    } finally {
      setIsCalculating(false);
    }
  };

  const selectedSolution = problemCase.candidateSolutions.find(s => s.id === problemCase.selectedSolutionId);
  const completedActions = problemCase.actionItems.filter(a => a.isCompleted).length;
  const totalActions = problemCase.actionItems.length;
  const progressPercent = totalActions > 0 ? Math.round((completedActions / totalActions) * 100) : 0;
  const hasPassingRun = problemCase.verificationRuns.some(r => r.status === 'PASSED');

  return (
    <div className="space-y-6 pb-20 animate-fadeIn">
      {/* Top Header */}
      <div className="bg-slate-900 border border-slate-800 rounded-2xl p-6 shadow-xl space-y-4">
        <div className="flex flex-wrap items-center justify-between gap-3">
          <button
            onClick={onBack}
            className="flex items-center gap-2 text-xs font-medium text-slate-400 hover:text-slate-200 transition"
          >
            <ArrowLeft className="w-4 h-4" />
            <span>All Investigations</span>
          </button>
          
          <div className="flex items-center gap-2 flex-wrap">
            <CategoryBadge category={problemCase.category} />
            <UrgencyBadge urgency={problemCase.urgency} />
            <StatusBadge status={problemCase.status} size="lg" />
          </div>
        </div>

        <div className="flex flex-col md:flex-row md:items-center justify-between gap-4 pt-1">
          <div className="space-y-1">
            <h1 className="text-2xl sm:text-3xl font-extrabold text-slate-50 tracking-tight">
              {problemCase.title}
            </h1>
            <p className="text-xs sm:text-sm text-slate-400 max-w-3xl">
              <strong>Goal:</strong> {problemCase.goal}
            </p>
          </div>

          {/* Quick Lifecycle Status Actions */}
          <div className="flex items-center gap-2 flex-wrap shrink-0">
            {problemCase.status === 'DRAFT' && (
              <button
                onClick={() => api.triggerInvestigation(problemCase.id).then(onRefresh)}
                className="flex items-center gap-1.5 px-3.5 py-2 bg-emerald-600 hover:bg-emerald-500 text-white rounded-lg text-xs font-semibold shadow-sm transition"
              >
                <Sparkles className="w-3.5 h-3.5" />
                <span>Trigger Investigation</span>
              </button>
            )}

            {problemCase.status === 'IN_PROGRESS' && (
              <>
                <button
                  onClick={() => handleTransitionStatus('USER_REPORTED_SUCCESS', 'User tested and reported fix worked')}
                  className="px-3 py-1.5 bg-cyan-950/80 hover:bg-cyan-900 border border-cyan-700/60 text-cyan-200 rounded-lg text-xs font-medium transition"
                  title="Marks user-reported success without formal verified test proof"
                >
                  Report Success
                </button>
                <button
                  onClick={() => {
                    if (!hasPassingRun) {
                      alert("Cannot verify without passing test: Please record a passing verification run or execute a deterministic check first!");
                      setActiveTab('verification');
                    } else {
                      handleTransitionStatus('VERIFIED', 'All verification checks passed with empirical evidence');
                    }
                  }}
                  className="px-3 py-1.5 bg-emerald-600 hover:bg-emerald-500 text-white rounded-lg text-xs font-semibold shadow-sm transition"
                >
                  Verify Case
                </button>
              </>
            )}

            {problemCase.status === 'USER_REPORTED_SUCCESS' && (
              <button
                onClick={() => {
                  if (!hasPassingRun) {
                    alert("Verification guard: User success is recorded, but formal status 'VERIFIED' requires an independently reproducible passing test run.");
                    setActiveTab('verification');
                  } else {
                    handleTransitionStatus('VERIFIED', 'Empirical test run confirmed');
                  }
                }}
                className="flex items-center gap-1.5 px-3 py-1.5 bg-emerald-600 hover:bg-emerald-500 text-white rounded-lg text-xs font-semibold shadow-sm transition"
              >
                <CheckCircle2 className="w-3.5 h-3.5" />
                <span>Elevate to Verified</span>
              </button>
            )}

            {(problemCase.status === 'VERIFIED' || problemCase.status === 'USER_REPORTED_SUCCESS') && (
              <button
                onClick={() => setShowPublishModal(true)}
                className="flex items-center gap-1.5 px-3 py-1.5 bg-purple-950/80 hover:bg-purple-900 border border-purple-700/60 text-purple-200 rounded-lg text-xs font-medium transition"
              >
                <BookOpen className="w-3.5 h-3.5" />
                <span>Publish to Library</span>
              </button>
            )}
          </div>
        </div>
      </div>

      {/* Tabs Navigation */}
      <div className="flex items-center gap-2 border-b border-slate-800 pb-2 overflow-x-auto">
        <button
          onClick={() => setActiveTab('investigation')}
          className={`flex items-center gap-2 px-4 py-2 rounded-xl text-xs sm:text-sm font-semibold transition whitespace-nowrap ${
            activeTab === 'investigation'
              ? 'bg-slate-800 text-emerald-400 border border-slate-700 shadow-sm'
              : 'text-slate-400 hover:text-slate-200 hover:bg-slate-900'
          }`}
        >
          <Search className="w-4 h-4" />
          <span>1. Diagnosis & What Happened</span>
        </button>

        <button
          onClick={() => setActiveTab('solutions')}
          className={`flex items-center gap-2 px-4 py-2 rounded-xl text-xs sm:text-sm font-semibold transition whitespace-nowrap ${
            activeTab === 'solutions'
              ? 'bg-slate-800 text-emerald-400 border border-slate-700 shadow-sm'
              : 'text-slate-400 hover:text-slate-200 hover:bg-slate-900'
          }`}
        >
          <Lightbulb className="w-4 h-4" />
          <span>2. Solutions & Fixes ({problemCase.candidateSolutions.length})</span>
        </button>

        <button
          onClick={() => setActiveTab('actions')}
          className={`flex items-center gap-2 px-4 py-2 rounded-xl text-xs sm:text-sm font-semibold transition whitespace-nowrap ${
            activeTab === 'actions'
              ? 'bg-slate-800 text-emerald-400 border border-slate-700 shadow-sm'
              : 'text-slate-400 hover:text-slate-200 hover:bg-slate-900'
          }`}
        >
          <CheckSquare className="w-4 h-4" />
          <span>3. Step-by-Step Fix ({completedActions}/{totalActions})</span>
        </button>

        <button
          onClick={() => setActiveTab('verification')}
          className={`flex items-center gap-2 px-4 py-2 rounded-xl text-xs sm:text-sm font-semibold transition whitespace-nowrap ${
            activeTab === 'verification'
              ? 'bg-slate-800 text-emerald-400 border border-slate-700 shadow-sm'
              : 'text-slate-400 hover:text-slate-200 hover:bg-slate-900'
          }`}
        >
          <ShieldCheck className="w-4 h-4" />
          <span>4. Test & Confirm ({problemCase.verificationRuns.length})</span>
        </button>

        <button
          onClick={() => setActiveTab('timeline')}
          className={`flex items-center gap-2 px-4 py-2 rounded-xl text-xs sm:text-sm font-semibold transition whitespace-nowrap ${
            activeTab === 'timeline'
              ? 'bg-slate-800 text-emerald-400 border border-slate-700 shadow-sm'
              : 'text-slate-400 hover:text-slate-200 hover:bg-slate-900'
          }`}
        >
          <History className="w-4 h-4" />
          <span>5. Case History ({problemCase.auditEvents.length})</span>
        </button>
      </div>

      {/* ========================================================================= */}
      {/* TAB 1: INVESTIGATION DECOMPOSITION */}
      {/* ========================================================================= */}
      {activeTab === 'investigation' && (
        <div className="space-y-6">
          {/* Diagnostic Summary Header Card */}
          {problemCase.investigationFinding ? (
            <div className="bg-slate-900 border border-slate-800 rounded-2xl p-6 shadow-md space-y-3">
              <div className="flex items-center justify-between">
                <div className="flex items-center gap-2 text-xs font-semibold uppercase tracking-wider text-emerald-400">
                  <Sparkles className="w-4 h-4" />
                  <span>Sentinel Diagnostic Assessment</span>
                </div>
                <div className="flex items-center gap-3">
                  <span className="text-xs text-slate-400 font-mono">
                    Certainty: <strong>{Math.round((problemCase.investigationFinding.confidenceScore || 0.85) * 100)}%</strong>
                  </span>
                  {problemCase.investigationFinding.isDemoOutput && (
                    <span className="px-2 py-0.5 rounded text-[10px] font-mono bg-amber-950 text-amber-300 border border-amber-800">
                      Deterministic Demo Engine
                    </span>
                  )}
                </div>
              </div>

              <p className="text-sm text-slate-200 leading-relaxed font-mono bg-slate-950/70 p-4 rounded-xl border border-slate-800/80">
                {problemCase.investigationFinding.diagnosticSummary}
              </p>
            </div>
          ) : (
            <div className="p-8 text-center bg-slate-900 border border-slate-800 rounded-2xl space-y-3">
              <p className="text-sm text-slate-300">Investigation analysis has not yet been triggered for this draft case.</p>
              <button
                onClick={() => api.triggerInvestigation(problemCase.id).then(onRefresh)}
                className="px-4 py-2 bg-emerald-600 text-white rounded-lg text-xs font-semibold"
              >
                Analyze Submitted Evidence
              </button>
            </div>
          )}

          {/* 5 Structural Pillars Grid */}
          {problemCase.investigationFinding && (
            <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
              {/* Pillar 1: Known Facts */}
              <div className="bg-slate-900 border border-slate-800 rounded-xl p-5 space-y-3">
                <div className="flex items-center gap-2 text-xs font-bold text-emerald-400 uppercase tracking-wider">
                  <CheckCircle2 className="w-4 h-4" />
                  <span>1. Known Facts ({problemCase.investigationFinding.knownFacts.length})</span>
                </div>
                <p className="text-[11px] text-slate-400">Confirmed facts from your problem description and files</p>
                <ul className="space-y-2 text-xs text-slate-300">
                  {problemCase.investigationFinding.knownFacts.map((fact, i) => (
                    <li key={i} className="flex items-start gap-2 bg-slate-950/50 p-2.5 rounded-lg border border-slate-800/60">
                      <span className="text-emerald-500 font-bold">•</span>
                      <span>{fact}</span>
                    </li>
                  ))}
                </ul>
              </div>

              {/* Pillar 2: Working Assumptions */}
              <div className="bg-slate-900 border border-slate-800 rounded-xl p-5 space-y-3">
                <div className="flex items-center gap-2 text-xs font-bold text-amber-400 uppercase tracking-wider">
                  <HelpCircle className="w-4 h-4" />
                  <span>2. Working Assumptions ({problemCase.investigationFinding.assumptions.length})</span>
                </div>
                <p className="text-[11px] text-slate-400">Initial hypotheses that need verification</p>
                <ul className="space-y-2 text-xs text-slate-300">
                  {problemCase.investigationFinding.assumptions.map((assump, i) => (
                    <li key={i} className="flex items-start gap-2 bg-slate-950/50 p-2.5 rounded-lg border border-slate-800/60">
                      <span className="text-amber-500 font-bold">•</span>
                      <span>{assump}</span>
                    </li>
                  ))}
                </ul>
              </div>

              {/* Pillar 3: Missing Information / Unknowns */}
              <div className="bg-slate-900 border border-slate-800 rounded-xl p-5 space-y-3">
                <div className="flex items-center gap-2 text-xs font-bold text-rose-400 uppercase tracking-wider">
                  <AlertCircle className="w-4 h-4" />
                  <span>3. Missing Details ({problemCase.investigationFinding.missingInformation.length})</span>
                </div>
                <p className="text-[11px] text-slate-400">Extra details that would help narrow this down</p>
                <ul className="space-y-2 text-xs text-slate-300">
                  {problemCase.investigationFinding.missingInformation.map((item, i) => (
                    <li key={i} className="flex items-start gap-2 bg-slate-950/50 p-2.5 rounded-lg border border-slate-800/60">
                      <span className="text-rose-500 font-bold">•</span>
                      <span>{item}</span>
                    </li>
                  ))}
                </ul>
              </div>

              {/* Pillar 4: Hard Constraints */}
              <div className="bg-slate-900 border border-slate-800 rounded-xl p-5 space-y-3">
                <div className="flex items-center gap-2 text-xs font-bold text-purple-400 uppercase tracking-wider">
                  <ShieldCheck className="w-4 h-4" />
                  <span>4. Limits & Rules ({problemCase.investigationFinding.identifiedConstraints.length})</span>
                </div>
                <p className="text-[11px] text-slate-400">Safety standards, budgets, or design limitations</p>
                <ul className="space-y-2 text-xs text-slate-300">
                  {problemCase.investigationFinding.identifiedConstraints.map((item, i) => (
                    <li key={i} className="flex items-start gap-2 bg-slate-950/50 p-2.5 rounded-lg border border-slate-800/60">
                      <span className="text-purple-400 font-bold">•</span>
                      <span>{item}</span>
                    </li>
                  ))}
                </ul>
              </div>

              {/* Pillar 5: Success Criteria */}
              <div className="bg-slate-900 border border-slate-800 rounded-xl p-5 space-y-3 md:col-span-2 lg:col-span-2">
                <div className="flex items-center gap-2 text-xs font-bold text-cyan-400 uppercase tracking-wider">
                  <CheckSquare className="w-4 h-4" />
                  <span>5. What Success Looks Like ({problemCase.investigationFinding.successCriteria.length})</span>
                </div>
                <p className="text-[11px] text-slate-400">The measurable condition required to prove this is fully solved</p>
                <ul className="space-y-2 text-xs text-slate-300">
                  {problemCase.investigationFinding.successCriteria.map((item, i) => (
                    <li key={i} className="flex items-start gap-2 bg-slate-950/50 p-2.5 rounded-lg border border-slate-800/60">
                      <span className="text-cyan-400 font-bold">•</span>
                      <span>{item}</span>
                    </li>
                  ))}
                </ul>
              </div>
            </div>
          )}

          {/* Clarification Q&A Panel */}
          {problemCase.investigationFinding && problemCase.investigationFinding.clarificationQuestions.length > 0 && (
            <div className="bg-slate-900 border border-slate-800 rounded-2xl p-6 shadow-md space-y-4">
              <div className="flex items-center justify-between">
                <div>
                  <h3 className="text-base font-semibold text-slate-100 flex items-center gap-2">
                    <HelpCircle className="w-4 h-4 text-amber-400" />
                    <span>Targeted Clarification Questions</span>
                  </h3>
                  <p className="text-xs text-slate-400 mt-0.5">
                    Answering these diagnostic questions eliminates critical assumptions and sharpens solution ranking.
                  </p>
                </div>
              </div>

              <form onSubmit={handleSubmitClarifications} className="space-y-3">
                {problemCase.investigationFinding.clarificationQuestions.map((question, i) => (
                  <div key={i} className="space-y-1.5 p-3 rounded-xl bg-slate-950/60 border border-slate-800">
                    <label className="block text-xs font-medium text-slate-200">
                      Q{i + 1}: {question}
                    </label>
                    <input
                      type="text"
                      placeholder="Type specific answer or observation..."
                      value={clarificationAnswers[question] || ''}
                      onChange={(e) => setClarificationAnswers({ ...clarificationAnswers, [question]: e.target.value })}
                      className="w-full bg-slate-900 border border-slate-800 rounded-lg px-3 py-1.5 text-xs text-slate-100 placeholder:text-slate-600 focus:outline-none focus:border-emerald-500"
                    />
                  </div>
                ))}

                <div className="flex justify-end pt-1">
                  <button
                    type="submit"
                    disabled={isClarifying || Object.keys(clarificationAnswers).length === 0}
                    className="flex items-center gap-2 px-4 py-2 bg-emerald-600 hover:bg-emerald-500 disabled:opacity-40 text-white rounded-lg text-xs font-semibold transition"
                  >
                    <Send className="w-3.5 h-3.5" />
                    <span>{isClarifying ? 'Submitting Answers...' : 'Submit Clarifications & Refine'}</span>
                  </button>
                </div>
              </form>
            </div>
          )}

          {/* Attached Evidence Files */}
          <div className="bg-slate-900 border border-slate-800 rounded-2xl p-6 shadow-md space-y-3">
            <h3 className="text-base font-semibold text-slate-100 flex items-center gap-2">
              <FileText className="w-4 h-4 text-emerald-400" />
              <span>Evidence Repository ({problemCase.evidenceItems.length})</span>
            </h3>

            {problemCase.evidenceItems.length === 0 ? (
              <p className="text-xs text-slate-400 italic">No files attached to this case.</p>
            ) : (
              <div className="grid grid-cols-1 sm:grid-cols-2 md:grid-cols-3 gap-3">
                {problemCase.evidenceItems.map((item) => (
                  <div key={item.id} className="p-3 bg-slate-950 rounded-xl border border-slate-800 space-y-2">
                    <div className="flex items-center justify-between">
                      <div className="flex items-center gap-2 truncate">
                        <FileText className="w-4 h-4 text-emerald-400 shrink-0" />
                        <span className="text-xs font-mono text-slate-200 truncate">{item.originalFilename}</span>
                      </div>
                      <a
                        href={api.getEvidenceDownloadUrl(item.id)}
                        download
                        className="text-slate-400 hover:text-emerald-400 p-1 transition"
                        title="Download file"
                      >
                        <Download className="w-4 h-4" />
                      </a>
                    </div>
                    <div className="flex items-center justify-between text-[11px] text-slate-500">
                      <span>{Math.round(item.fileSize / 1024)} KB</span>
                      <span className="font-mono text-[10px] text-slate-600 truncate max-w-[120px]">{item.checksumSha256?.substring(0, 10)}...</span>
                    </div>
                  </div>
                ))}
              </div>
            )}
          </div>
        </div>
      )}

      {/* ========================================================================= */}
      {/* TAB 2: SOLUTION COMPARISON MATRIX */}
      {/* ========================================================================= */}
      {activeTab === 'solutions' && (
        <div className="space-y-6">
          <div className="flex items-center justify-between">
            <div>
              <h2 className="text-lg font-bold text-slate-100">Candidate Solution Trade-off Analysis</h2>
              <p className="text-xs text-slate-400">
                Compare benefits, trade-offs, effort, and failure modes before committing to an action plan.
              </p>
            </div>
            {problemCase.candidateSolutions.length === 0 && (
              <button
                onClick={() => api.triggerInvestigation(problemCase.id).then(onRefresh)}
                className="px-3.5 py-2 bg-emerald-600 text-white rounded-lg text-xs font-semibold"
              >
                Generate Solutions
              </button>
            )}
          </div>

          <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
            {problemCase.candidateSolutions.map((solution) => {
              const isSelected = solution.id === problemCase.selectedSolutionId || solution.isSelected;
              return (
                <div
                  key={solution.id}
                  className={`flex flex-col justify-between rounded-2xl p-6 border transition relative ${
                    isSelected
                      ? 'bg-slate-850 border-emerald-500 shadow-xl ring-1 ring-emerald-500/20'
                      : 'bg-slate-900 border-slate-800 hover:border-slate-700'
                  }`}
                >
                  {isSelected && (
                    <div className="absolute -top-3 right-4 px-2.5 py-0.5 rounded-full bg-emerald-600 text-white text-[11px] font-bold tracking-wider uppercase shadow-md flex items-center gap-1">
                      <CheckCircle2 className="w-3 h-3" />
                      <span>Selected Solution</span>
                    </div>
                  )}

                  <div className="space-y-4">
                    {/* Header */}
                    <div>
                      <div className="flex items-center justify-between text-xs text-slate-400 font-mono mb-1">
                        <span>Solution #{solution.solutionIndex}</span>
                        <span className={`px-2 py-0.5 rounded text-[11px] font-semibold ${
                          solution.effortLevel === 'LOW' ? 'bg-emerald-950 text-emerald-400' :
                          solution.effortLevel === 'HIGH' ? 'bg-rose-950 text-rose-400' : 'bg-amber-950 text-amber-400'
                        }`}>
                          Effort: {solution.effortLevel}
                        </span>
                      </div>
                      <h3 className="text-base font-bold text-slate-100">{solution.title}</h3>
                      <p className="text-xs text-slate-300 mt-2 leading-relaxed">{solution.summary}</p>
                    </div>

                    {/* Reasoning */}
                    <div className="p-3 rounded-xl bg-slate-950/70 border border-slate-800 text-xs text-slate-300 space-y-1">
                      <span className="font-semibold text-emerald-400 block">Root-Cause Rationale:</span>
                      <p className="leading-relaxed">{solution.reasoning}</p>
                    </div>

                    {/* Benefits */}
                    <div className="space-y-1.5">
                      <span className="text-xs font-semibold text-emerald-400 uppercase tracking-wider block">Benefits</span>
                      <ul className="space-y-1 text-xs text-slate-300">
                        {solution.benefits.map((b, idx) => (
                          <li key={idx} className="flex items-start gap-1.5">
                            <span className="text-emerald-400 font-bold">+</span>
                            <span>{b}</span>
                          </li>
                        ))}
                      </ul>
                    </div>

                    {/* Trade-offs & Risks */}
                    <div className="space-y-1.5">
                      <span className="text-xs font-semibold text-amber-400 uppercase tracking-wider block">Trade-offs & Failure Modes</span>
                      <ul className="space-y-1 text-xs text-slate-300">
                        {solution.tradeoffs.map((t, idx) => (
                          <li key={idx} className="flex items-start gap-1.5">
                            <span className="text-amber-400 font-bold">~</span>
                            <span>{t}</span>
                          </li>
                        ))}
                        {solution.risks.map((r, idx) => (
                          <li key={idx} className="flex items-start gap-1.5 text-rose-300">
                            <span className="text-rose-400 font-bold">!</span>
                            <span>{r}</span>
                          </li>
                        ))}
                      </ul>
                    </div>

                    {/* Confidence Limitations */}
                    <div className="p-2.5 rounded-lg bg-slate-950/50 border border-slate-800 text-[11px] text-slate-400 space-y-0.5">
                      <span className="font-semibold text-slate-300">Confidence Limitations:</span>
                      <p>{solution.confidenceLimitations}</p>
                    </div>
                  </div>

                  {/* Select Solution Button */}
                  <div className="pt-5 mt-4 border-t border-slate-800/80">
                    <button
                      onClick={() => handleSelectSolution(solution.id)}
                      disabled={isSelected}
                      className={`w-full py-2.5 rounded-xl text-xs font-semibold transition flex items-center justify-center gap-1.5 ${
                        isSelected
                          ? 'bg-emerald-950/80 text-emerald-300 border border-emerald-700/60 cursor-default'
                          : 'bg-slate-800 hover:bg-emerald-600 text-slate-200 hover:text-white'
                      }`}
                    >
                      {isSelected ? (
                        <>
                          <CheckCircle2 className="w-3.5 h-3.5" />
                          <span>Active in Action Plan</span>
                        </>
                      ) : (
                        <>
                          <span>Select & Generate Action Plan</span>
                          <ArrowRight className="w-3.5 h-3.5" />
                        </>
                      )}
                    </button>
                  </div>
                </div>
              );
            })}
          </div>
        </div>
      )}

      {/* ========================================================================= */}
      {/* TAB 3: ACTION PLAN TRACKER */}
      {/* ========================================================================= */}
      {activeTab === 'actions' && (
        <div className="space-y-6">
          <div className="bg-slate-900 border border-slate-800 rounded-2xl p-6 shadow-md space-y-4">
            <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-3">
              <div>
                <h2 className="text-lg font-bold text-slate-100 flex items-center gap-2">
                  <CheckSquare className="w-5 h-5 text-emerald-400" />
                  <span>Action Execution Checklist</span>
                </h2>
                <p className="text-xs text-slate-400 mt-0.5">
                  Sequential steps for: <strong>{selectedSolution?.title || 'Selected Solution'}</strong>
                </p>
              </div>

              <div className="flex items-center gap-3">
                <span className="text-xs font-mono text-slate-300">
                  {completedActions} of {totalActions} Completed ({progressPercent}%)
                </span>
              </div>
            </div>

            {/* Progress Bar */}
            <div className="w-full bg-slate-950 rounded-full h-2.5 overflow-hidden border border-slate-800">
              <div
                className="bg-emerald-500 h-2.5 rounded-full transition-all duration-300"
                style={{ width: `${progressPercent}%` }}
              ></div>
            </div>
          </div>

          {problemCase.actionItems.length === 0 ? (
            <div className="p-8 text-center bg-slate-900 border border-slate-800 rounded-2xl space-y-3">
              <p className="text-sm text-slate-300">No action plan generated yet. Please select a candidate solution first.</p>
              <button
                onClick={() => setActiveTab('solutions')}
                className="px-4 py-2 bg-emerald-600 text-white rounded-lg text-xs font-semibold"
              >
                View Solution Trade-offs
              </button>
            </div>
          ) : (
            <div className="space-y-3">
              {problemCase.actionItems.map((item) => (
                <div
                  key={item.id}
                  onClick={() => handleToggleAction(item.id, item.isCompleted)}
                  className={`p-4 rounded-xl border transition cursor-pointer flex items-start gap-3.5 select-none ${
                    item.isCompleted
                      ? 'bg-slate-900/60 border-emerald-900/50 opacity-80'
                      : 'bg-slate-900 border-slate-800 hover:border-slate-700'
                  }`}
                >
                  <input
                    type="checkbox"
                    checked={item.isCompleted}
                    onChange={() => {}} // Controlled by parent div click
                    className="mt-1 w-4 h-4 rounded border-slate-700 bg-slate-950 text-emerald-600 focus:ring-emerald-500 cursor-pointer"
                  />
                  <div className="space-y-1">
                    <div className="flex items-center gap-2">
                      <span className="text-xs font-mono text-slate-400">Step {item.stepNumber}:</span>
                      <h4 className={`text-sm font-semibold ${item.isCompleted ? 'text-slate-400 line-through' : 'text-slate-100'}`}>
                        {item.title}
                      </h4>
                    </div>
                    <p className="text-xs text-slate-300 leading-relaxed">{item.description}</p>
                    {item.completedAt && (
                      <p className="text-[10px] text-emerald-400 font-mono pt-1">
                        Completed at: {new Date(item.completedAt).toLocaleString()}
                      </p>
                    )}
                  </div>
                </div>
              ))}
            </div>
          )}
        </div>
      )}

      {/* ========================================================================= */}
      {/* TAB 4: VERIFICATION CENTER */}
      {/* ========================================================================= */}
      {activeTab === 'verification' && (
        <div className="space-y-6">
          {/* Header Banner on Verification Principle */}
          <div className="p-4 bg-emerald-950/30 border border-emerald-800/40 rounded-xl flex items-start gap-3 text-xs text-emerald-200">
            <ShieldCheck className="w-5 h-5 text-emerald-400 shrink-0 mt-0.5" />
            <div className="space-y-1">
              <strong className="text-emerald-300">The Sentinel Verification Guarantee:</strong>
              <p className="text-slate-300 leading-relaxed">
                A model-generated diagnosis is an unverified hypothesis. User-reported success and testable verification are strictly separated. Solutions are marked Verified only when reproducible passing test procedures and empirical evidence are recorded.
              </p>
            </div>
          </div>

          {/* Interactive Deterministic Calculators Section */}
          <div className="bg-slate-900 border border-slate-800 rounded-2xl p-6 shadow-md space-y-4">
            <div className="flex items-center justify-between">
              <div>
                <h3 className="text-base font-bold text-slate-100 flex items-center gap-2">
                  <Calculator className="w-4 h-4 text-emerald-400" />
                  <span>Reproducible Deterministic Verification Calculators</span>
                </h3>
                <p className="text-xs text-slate-400 mt-0.5">
                  Execute safe, bounded empirical calculators to mathematically confirm success criteria independent of model opinion.
                </p>
              </div>
            </div>

            <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-4 pt-2">
              {/* Calculator 1: Software Connection Pool Starvation */}
              <div className="p-4 rounded-xl bg-slate-950 border border-slate-800 space-y-3 flex flex-col justify-between">
                <div className="space-y-1">
                  <span className="text-[11px] font-mono text-emerald-400 font-semibold uppercase">Software Troubleshooting</span>
                  <h4 className="text-sm font-semibold text-slate-100">HikariCP Pool Concurrency Check</h4>
                  <p className="text-xs text-slate-400">
                    Asserts p99 connection wait time &le; 5.0ms with 0 timeout exceptions under 5,000 RPS.
                  </p>
                </div>
                <button
                  onClick={() => handleRunDeterministicCheck('SQL_POOL_STARVATION', {
                    requestsPerSecond: 5000,
                    maxWaitThresholdMs: 5.0,
                    p99AcquisitionWaitMs: 2.8,
                    timeoutErrorCount: 0,
                    transactionHoldDurationMs: 18.4,
                  })}
                  disabled={isCalculating}
                  className="w-full py-2 bg-emerald-600 hover:bg-emerald-500 text-white rounded-lg text-xs font-semibold transition"
                >
                  {isCalculating ? 'Executing...' : 'Run Pool Test Assertions'}
                </button>
              </div>

              {/* Calculator 2: Math PINN L2 Error Norm */}
              <div className="p-4 rounded-xl bg-slate-950 border border-slate-800 space-y-3 flex flex-col justify-between">
                <div className="space-y-1">
                  <span className="text-[11px] font-mono text-indigo-400 font-semibold uppercase">Academic & Math</span>
                  <h4 className="text-sm font-semibold text-slate-100">Cole-Hopf Relative L2 Norm</h4>
                  <p className="text-xs text-slate-400">
                    Calculates ||u_pred - u_exact|| / ||u_exact|| over 10,000 coordinates to verify &le; 1.5e-3.
                  </p>
                </div>
                <button
                  onClick={() => handleRunDeterministicCheck('PINN_L2_NORM', {
                    relativeL2Error: 0.00118,
                    tolerance: 0.0015,
                  })}
                  disabled={isCalculating}
                  className="w-full py-2 bg-indigo-600 hover:bg-indigo-500 text-white rounded-lg text-xs font-semibold transition"
                >
                  {isCalculating ? 'Computing...' : 'Run L2 Analytical Norm Check'}
                </button>
              </div>

              {/* Calculator 3: SaaS Churn & Margin Sensitivity */}
              <div className="p-4 rounded-xl bg-slate-950 border border-slate-800 space-y-3 flex flex-col justify-between">
                <div className="space-y-1">
                  <span className="text-[11px] font-mono text-sky-400 font-semibold uppercase">Data & Business</span>
                  <h4 className="text-sm font-semibold text-slate-100">SaaS Unit Economics & Margin Check</h4>
                  <p className="text-xs text-slate-400">
                    Reconciles gross margin &ge; 78% and projected churn recovery below 2.5%/mo.
                  </p>
                </div>
                <button
                  onClick={() => handleRunDeterministicCheck('SAAS_CHURN_MARGIN', {
                    basePrice: 189.0,
                    infrastructureCogs: 35.0,
                    minGrossMargin: 0.78,
                    maxAcceptableChurnRate: 0.035,
                    historicalChurnRate: 0.021,
                    currentSpikeChurnRate: 0.068,
                    estimatedRecoveryRate: 0.70,
                  })}
                  disabled={isCalculating}
                  className="w-full py-2 bg-sky-600 hover:bg-sky-500 text-white rounded-lg text-xs font-semibold transition"
                >
                  {isCalculating ? 'Modeling...' : 'Run Unit Economics Check'}
                </button>
              </div>

              {/* Calculator 4: Structural Engineering Deflection & Safety Factor */}
              <div className="p-4 rounded-xl bg-slate-950 border border-slate-800 space-y-3 flex flex-col justify-between">
                <div className="space-y-1">
                  <span className="text-[11px] font-mono text-amber-400 font-semibold uppercase">Engineering (Civil/Mech)</span>
                  <h4 className="text-sm font-semibold text-slate-100">Beam Deflection & FoS Check</h4>
                  <p className="text-xs text-slate-400">
                    Asserts mid-span deflection &le; L/360 limit and Factor of Safety (FoS) &ge; 1.75.
                  </p>
                </div>
                <button
                  onClick={() => handleRunDeterministicCheck('STRUCTURAL_BEAM_SAFETY', {
                    spanLengthMeters: 6.0,
                    appliedServiceLoadKn: 120.0,
                    observedDeflectionMm: 8.2,
                    ultimateCapacityKn: 255.0,
                    minRequiredFos: 1.75,
                  })}
                  disabled={isCalculating}
                  className="w-full py-2 bg-amber-600 hover:bg-amber-500 text-white rounded-lg text-xs font-semibold transition"
                >
                  {isCalculating ? 'Calculating...' : 'Run Structural Safety Check'}
                </button>
              </div>
            </div>

            {/* Active Calculation Output Display */}
            {calcResult && (
              <div className="mt-4 p-4 rounded-xl bg-slate-950 border border-emerald-800/60 space-y-2 animate-fadeIn">
                <div className="flex items-center justify-between">
                  <span className="text-xs font-bold text-emerald-400 uppercase tracking-wider flex items-center gap-1.5">
                    <CheckCircle2 className="w-4 h-4" />
                    <span>{calcResult.checkName} — Outcome: {calcResult.status}</span>
                  </span>
                  <span className="text-xs font-mono text-slate-300">
                    Target: {calcResult.expectedCondition}
                  </span>
                </div>
                <p className="text-xs text-slate-200 font-mono bg-slate-900 p-3 rounded-lg border border-slate-800">
                  {calcResult.calculationDetails}
                </p>
                <p className="text-[11px] text-emerald-300/80 italic">
                  {calcResult.evidenceNotes} Automatically saved as permanent reproducible verification run.
                </p>
              </div>
            )}
          </div>

          {/* Record Manual Verification Form */}
          <div className="bg-slate-900 border border-slate-800 rounded-2xl p-6 shadow-md space-y-4">
            <h3 className="text-base font-bold text-slate-100 flex items-center gap-2">
              <Plus className="w-4 h-4 text-emerald-400" />
              <span>Record Empirical Test Run</span>
            </h3>

            <form onSubmit={handleRecordVerification} className="space-y-4 text-sm">
              <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                <div className="space-y-1">
                  <label className="block text-xs font-medium text-slate-300">Test Procedure Executed *</label>
                  <textarea
                    rows={2}
                    required
                    value={testProcedure}
                    onChange={(e) => setTestProcedure(e.target.value)}
                    placeholder="e.g. k6 load test generating 5,000 RPS for 15 minutes with simulated payment latency..."
                    className="w-full bg-slate-950 border border-slate-800 rounded-lg p-2.5 text-xs text-slate-100 focus:outline-none focus:border-emerald-500 resize-none"
                  />
                </div>

                <div className="space-y-1">
                  <label className="block text-xs font-medium text-slate-300">Expected Result *</label>
                  <textarea
                    rows={2}
                    required
                    value={expectedResult}
                    onChange={(e) => setExpectedResult(e.target.value)}
                    placeholder="e.g. Pool acquisition wait p99 < 5ms with zero timeout errors..."
                    className="w-full bg-slate-950 border border-slate-800 rounded-lg p-2.5 text-xs text-slate-100 focus:outline-none focus:border-emerald-500 resize-none"
                  />
                </div>
              </div>

              <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
                <div className="space-y-1">
                  <label className="block text-xs font-medium text-slate-300">Actual Observed Outcome</label>
                  <input
                    type="text"
                    value={actualResult}
                    onChange={(e) => setActualResult(e.target.value)}
                    placeholder="e.g. Observed 2.8ms wait, 0 timeouts across 4.5M calls"
                    className="w-full bg-slate-950 border border-slate-800 rounded-lg p-2 text-xs text-slate-100 focus:outline-none focus:border-emerald-500"
                  />
                </div>

                <div className="space-y-1">
                  <label className="block text-xs font-medium text-slate-300">Result Status *</label>
                  <select
                    value={verificationStatus}
                    onChange={(e) => setVerificationStatus(e.target.value as VerificationStatus)}
                    className="w-full bg-slate-950 border border-slate-800 rounded-lg p-2 text-xs text-slate-100 focus:outline-none focus:border-emerald-500"
                  >
                    <option value="PASSED">Passed (Criteria Satisfied)</option>
                    <option value="FAILED">Failed (Did Not Satisfy Criteria)</option>
                    <option value="INCONCLUSIVE">Inconclusive (Requires Further Testing)</option>
                  </select>
                </div>

                <div className="space-y-1">
                  <label className="block text-xs font-medium text-slate-300">Verification Type</label>
                  <select
                    value={verificationType}
                    onChange={(e) => setVerificationType(e.target.value as VerificationType)}
                    className="w-full bg-slate-950 border border-slate-800 rounded-lg p-2 text-xs text-slate-100 focus:outline-none focus:border-emerald-500"
                  >
                    <option value="MANUAL">Manual Protocol / User Testing</option>
                    <option value="DETERMINISTIC_CALCULATOR">Deterministic Calculator</option>
                    <option value="CODE_CHECK">Automated Code / Test Assertion</option>
                  </select>
                </div>
              </div>

              <div className="space-y-1">
                <label className="block text-xs font-medium text-slate-300">Supporting Evidence Notes</label>
                <input
                  type="text"
                  value={evidenceNotes}
                  onChange={(e) => setEvidenceNotes(e.target.value)}
                  placeholder="e.g. Grafana dashboard metrics snapshot attached, k6 summary report verified..."
                  className="w-full bg-slate-950 border border-slate-800 rounded-lg p-2 text-xs text-slate-100 focus:outline-none focus:border-emerald-500"
                />
              </div>

              <div className="flex justify-end pt-1">
                <button
                  type="submit"
                  disabled={isRecordingTest}
                  className="flex items-center gap-2 px-5 py-2 bg-emerald-600 hover:bg-emerald-500 text-white rounded-lg text-xs font-semibold transition"
                >
                  <CheckCircle2 className="w-3.5 h-3.5" />
                  <span>{isRecordingTest ? 'Saving Test Run...' : 'Save Verification Run'}</span>
                </button>
              </div>
            </form>
          </div>

          {/* Verification History Log */}
          <div className="bg-slate-900 border border-slate-800 rounded-2xl p-6 shadow-md space-y-4">
            <h3 className="text-base font-bold text-slate-100 flex items-center gap-2">
              <History className="w-4 h-4 text-emerald-400" />
              <span>Recorded Verification Runs ({problemCase.verificationRuns.length})</span>
            </h3>

            {problemCase.verificationRuns.length === 0 ? (
              <p className="text-xs text-slate-400 italic">No verification runs have been logged yet.</p>
            ) : (
              <div className="space-y-3">
                {problemCase.verificationRuns.map((run) => (
                  <div
                    key={run.id}
                    className="p-4 rounded-xl bg-slate-950 border border-slate-800 space-y-2 text-xs"
                  >
                    <div className="flex items-center justify-between">
                      <div className="flex items-center gap-2 font-mono">
                        <span className={`px-2 py-0.5 rounded text-[10px] font-bold ${
                          run.status === 'PASSED' ? 'bg-emerald-950 text-emerald-400 border border-emerald-800' :
                          run.status === 'FAILED' ? 'bg-rose-950 text-rose-400 border border-rose-800' :
                          'bg-amber-950 text-amber-400 border border-amber-800'
                        }`}>
                          {run.status}
                        </span>
                        <span className="text-slate-400">Type: {run.verificationType}</span>
                      </div>
                      <span className="text-slate-500 font-mono text-[11px]">
                        Recorded by {run.recordedBy} at {new Date(run.recordedAt).toLocaleString()}
                      </span>
                    </div>

                    <div className="grid grid-cols-1 md:grid-cols-2 gap-3 pt-1">
                      <div>
                        <span className="text-slate-400 block font-medium">Procedure:</span>
                        <p className="text-slate-200 mt-0.5">{run.testProcedure}</p>
                      </div>
                      <div>
                        <span className="text-slate-400 block font-medium">Expected Result:</span>
                        <p className="text-slate-200 mt-0.5">{run.expectedResult}</p>
                      </div>
                    </div>

                    {run.actualResult && (
                      <div className="pt-1">
                        <span className="text-slate-400 block font-medium">Actual Result:</span>
                        <p className="text-slate-100 font-mono mt-0.5">{run.actualResult}</p>
                      </div>
                    )}

                    {run.evidenceNotes && (
                      <div className="text-[11px] text-slate-400 pt-1 italic">
                        Evidence notes: {run.evidenceNotes}
                      </div>
                    )}
                  </div>
                ))}
              </div>
            )}
          </div>
        </div>
      )}

      {/* ========================================================================= */}
      {/* TAB 5: AUDIT TIMELINE */}
      {/* ========================================================================= */}
      {activeTab === 'timeline' && (
        <div className="bg-slate-900 border border-slate-800 rounded-2xl p-6 shadow-md space-y-6">
          <div className="space-y-1">
            <h2 className="text-lg font-bold text-slate-100 flex items-center gap-2">
              <History className="w-5 h-5 text-emerald-400" />
              <span>Chronological Investigation Audit Trail</span>
            </h2>
            <p className="text-xs text-slate-400">
              Immutable record of all investigative actions, AI analyses, user clarifications, and status changes.
            </p>
          </div>

          <div className="relative pl-6 space-y-6 before:absolute before:left-2 before:top-2 before:bottom-2 before:w-0.5 before:bg-slate-800">
            {problemCase.auditEvents.map((event) => (
              <div key={event.id} className="relative space-y-1">
                {/* Dot */}
                <div className="absolute -left-6 top-1 w-2.5 h-2.5 rounded-full bg-emerald-500 ring-4 ring-slate-900"></div>

                <div className="flex items-center justify-between text-xs">
                  <span className="font-semibold text-emerald-400 font-mono">
                    {event.eventTypeDescription || event.eventType}
                  </span>
                  <span className="text-slate-500 font-mono text-[11px]">
                    {new Date(event.timestamp).toLocaleString()}
                  </span>
                </div>

                <p className="text-xs text-slate-200 leading-relaxed">
                  {event.description}
                </p>

                <div className="text-[11px] text-slate-400 font-mono">
                  Actor: <span className="text-slate-300">{event.actor}</span>
                </div>

                {event.payloadJson && (
                  <pre className="text-[11px] font-mono text-slate-400 bg-slate-950 p-2 rounded border border-slate-800 overflow-x-auto max-h-32">
                    {event.payloadJson}
                  </pre>
                )}
              </div>
            ))}
          </div>
        </div>
      )}

      {/* Publish to Knowledge Library Modal */}
      <PublishKnowledgeModal
        isOpen={showPublishModal}
        problemCase={problemCase}
        onClose={() => setShowPublishModal(false)}
        onPublish={async (data) => {
          await api.publishKnowledge(problemCase.id, data);
          await onRefresh();
        }}
      />
    </div>
  );
};
