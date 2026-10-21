import React, { useState } from 'react';
import { ProblemCategory, ProblemUrgency } from '../types';
import { api } from '../services/api';
import { 
  ArrowLeft, 
  Upload, 
  Terminal, 
  Binary, 
  BarChart3, 
  HelpCircle, 
  Sparkles, 
  FileText, 
  X,
  AlertCircle
} from 'lucide-react';

interface Props {
  onCancel: () => void;
  onCreated: (newCaseId: string) => void;
}

export const CreateCaseView: React.FC<Props> = ({ onCancel, onCreated }) => {
  const [title, setTitle] = useState('');
  const [category, setCategory] = useState<ProblemCategory>('SOFTWARE_DEBUGGING');
  const [goal, setGoal] = useState('');
  const [constraintsText, setConstraintsText] = useState('');
  const [urgency, setUrgency] = useState<ProblemUrgency>('HIGH');
  const [description, setDescription] = useState('');
  const [files, setFiles] = useState<File[]>([]);
  const [isSubmitting, setIsSubmitting] = useState(false);
  const [error, setError] = useState<string | null>(null);

  const categories: { id: ProblemCategory; title: string; subtitle: string; icon: React.ReactNode }[] = [
    {
      id: 'SOFTWARE_DEBUGGING',
      title: 'Software Debugging',
      subtitle: 'Connection pool, deadlocks, stack traces, latency',
      icon: <Terminal className="w-5 h-5 text-emerald-400" />,
    },
    {
      id: 'ACADEMIC_MATH',
      title: 'Academic & Math',
      subtitle: 'PDE convergence, PINNs, analytical proof validation',
      icon: <Binary className="w-5 h-5 text-indigo-400" />,
    },
    {
      id: 'DATA_BUSINESS_ANALYSIS',
      title: 'Data & Business',
      subtitle: 'Churn spike, cohort economics, margin constraints',
      icon: <BarChart3 className="w-5 h-5 text-sky-400" />,
    },
    {
      id: 'GENERAL',
      title: 'General Inquiry',
      subtitle: 'General systems problem requiring structured clarification',
      icon: <HelpCircle className="w-5 h-5 text-slate-400" />,
    },
  ];

  const handlePreFill = (templateKey: 'software' | 'math' | 'business') => {
    if (templateKey === 'software') {
      setTitle('HikariCP Connection Pool Exhaustion & Spike in p99 Latency During Flash Sale');
      setCategory('SOFTWARE_DEBUGGING');
      setGoal('Eliminate connection timeouts, maintain p99 < 250ms under 5,000 RPS without vertical DB scaling.');
      setConstraintsText('No database hardware cost increase. Must preserve transactional ACID guarantees on checkout.');
      setUrgency('CRITICAL');
      setDescription('During flash sale surge, checkout API latency spiked to 8500ms with ConnectionTimeoutException after 30000ms. Database CPU is only 28%, but pool max connections reached 500.');
    } else if (templateKey === 'math') {
      setTitle('Gradient Explosion & Convergence Failure in Physics-Informed Neural Network (PINN)');
      setCategory('ACADEMIC_MATH');
      setGoal('Achieve stable convergence with relative L2 error norm < 1.5e-3 against Cole-Hopf analytical benchmark across domain [-1, 1] x [0, 1].');
      setConstraintsText('Must maintain continuous differentiable formulation without discrete spatial meshing.');
      setUrgency('HIGH');
      setDescription('Solving 1D viscous Burgers equation u_t + u*u_x = (0.01/pi)*u_xx. Training loss oscillates violently near shock formation at t=0.5. Spectral bias in tanh MLP fails to capture steep gradient.');
    } else if (templateKey === 'business') {
      setTitle('Customer Churn Spike Following Tier-2 Pricing Model & API Quota Restructuring');
      setCategory('DATA_BUSINESS_ANALYSIS');
      setGoal('Identify precise statistical driver of churn, protect gross margin >= 78%, and reverse monthly churn below 2.5% within 60 days.');
      setConstraintsText('Cannot revert base headline price for new signups. Pricing model must maintain minimum 78% gross margin.');
      setUrgency('HIGH');
      setDescription('Monthly customer churn rose from 2.1% to 6.8% in Tier-2 after introducing a 50k API cap with $0.005/call overage billing. Need to determine whether churn is driven by price sensitivity or surprise billing.');
    }
  };

  const handleFileChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    if (e.target.files) {
      const selected = Array.from(e.target.files);
      setFiles((prev) => [...prev, ...selected]);
    }
  };

  const removeFile = (index: number) => {
    setFiles((prev) => prev.filter((_, i) => i !== index));
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!title.trim() || !goal.trim() || !description.trim()) {
      setError('Please fill in all required fields (Title, Goal, Description).');
      return;
    }

    setError(null);
    setIsSubmitting(true);

    try {
      // 1. Create problem case
      const createdCase = await api.createCase({
        title: title.trim(),
        category,
        goal: goal.trim(),
        constraintsText: constraintsText.trim() || undefined,
        urgency,
        description: description.trim(),
      });

      // 2. Upload any attached evidence files
      for (const file of files) {
        try {
          await api.uploadEvidence(createdCase.id, file);
        } catch (uploadErr) {
          console.warn('Failed to upload file:', file.name, uploadErr);
        }
      }

      // 3. Automatically trigger initial investigation
      try {
        await api.triggerInvestigation(createdCase.id);
      } catch (investigateErr) {
        console.warn('Investigation trigger deferred:', investigateErr);
      }

      onCreated(createdCase.id);
    } catch (err: any) {
      setError(err.message || 'Failed to create problem case');
      setIsSubmitting(false);
    }
  };

  return (
    <div className="max-w-4xl mx-auto space-y-6 pb-16 animate-fadeIn">
      {/* Top Bar */}
      <div className="flex items-center justify-between border-b border-slate-800 pb-4">
        <button
          onClick={onCancel}
          className="flex items-center gap-2 text-sm text-slate-400 hover:text-slate-200 transition"
        >
          <ArrowLeft className="w-4 h-4" />
          <span>Back to Dashboard</span>
        </button>
        <div className="flex items-center gap-2">
          <span className="text-xs text-slate-400">Quick template:</span>
          <button
            type="button"
            onClick={() => handlePreFill('software')}
            className="px-2.5 py-1 text-xs bg-slate-800 hover:bg-slate-700 text-slate-300 rounded-md transition"
          >
            Software
          </button>
          <button
            type="button"
            onClick={() => handlePreFill('math')}
            className="px-2.5 py-1 text-xs bg-slate-800 hover:bg-slate-700 text-slate-300 rounded-md transition"
          >
            Math
          </button>
          <button
            type="button"
            onClick={() => handlePreFill('business')}
            className="px-2.5 py-1 text-xs bg-slate-800 hover:bg-slate-700 text-slate-300 rounded-md transition"
          >
            Business
          </button>
        </div>
      </div>

      {error && (
        <div className="p-4 bg-red-950/40 border border-red-800/60 rounded-xl flex items-center gap-3 text-sm text-red-300">
          <AlertCircle className="w-5 h-5 shrink-0 text-red-400" />
          <span>{error}</span>
        </div>
      )}

      {/* Main Form */}
      <form onSubmit={handleSubmit} className="space-y-6 bg-slate-900 border border-slate-800 rounded-2xl p-6 sm:p-8 shadow-xl">
        <div className="space-y-1">
          <h2 className="text-2xl font-bold text-slate-100 flex items-center gap-2">
            <span>Create Problem Investigation Case</span>
          </h2>
          <p className="text-xs sm:text-sm text-slate-400">
            Define the observable symptoms, desired resolution criteria, and operational constraints.
          </p>
        </div>

        {/* 1. Category Selection */}
        <div className="space-y-2">
          <label className="block text-xs font-semibold text-slate-300 uppercase tracking-wider">
            1. Problem Workflow Category
          </label>
          <div className="grid grid-cols-1 sm:grid-cols-2 gap-3">
            {categories.map((cat) => {
              const isSelected = category === cat.id;
              return (
                <div
                  key={cat.id}
                  onClick={() => setCategory(cat.id)}
                  className={`p-4 rounded-xl border cursor-pointer transition flex items-start gap-3.5 ${
                    isSelected
                      ? 'bg-slate-850 border-emerald-500 shadow-md ring-1 ring-emerald-500/20'
                      : 'bg-slate-950/60 border-slate-800 hover:border-slate-700 hover:bg-slate-850/40'
                  }`}
                >
                  <div className="p-2 rounded-lg bg-slate-900 border border-slate-800 mt-0.5">
                    {cat.icon}
                  </div>
                  <div>
                    <h4 className={`text-sm font-semibold ${isSelected ? 'text-emerald-400' : 'text-slate-200'}`}>
                      {cat.title}
                    </h4>
                    <p className="text-xs text-slate-400 mt-0.5">{cat.subtitle}</p>
                  </div>
                </div>
              );
            })}
          </div>
        </div>

        {/* 2. Title & Urgency */}
        <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
          <div className="md:col-span-2 space-y-1">
            <label className="block text-xs font-semibold text-slate-300 uppercase tracking-wider">
              Problem Title *
            </label>
            <input
              type="text"
              required
              value={title}
              onChange={(e) => setTitle(e.target.value)}
              placeholder="e.g. HikariCP Connection Pool Exhaustion During Peak Traffic"
              className="w-full bg-slate-950 border border-slate-800 rounded-xl px-4 py-2.5 text-slate-100 placeholder:text-slate-600 focus:outline-none focus:border-emerald-500 text-sm"
            />
          </div>

          <div className="space-y-1">
            <label className="block text-xs font-semibold text-slate-300 uppercase tracking-wider">
              Urgency Level *
            </label>
            <select
              value={urgency}
              onChange={(e) => setUrgency(e.target.value as ProblemUrgency)}
              className="w-full bg-slate-950 border border-slate-800 rounded-xl px-3 py-2.5 text-slate-100 focus:outline-none focus:border-emerald-500 text-sm"
            >
              <option value="CRITICAL">Critical (Immediate Outage / Revenue Blocker)</option>
              <option value="HIGH">High (Major Degradation / Urgent Research)</option>
              <option value="MEDIUM">Medium (Standard Investigation)</option>
              <option value="LOW">Low (Exploratory / Optimization)</option>
            </select>
          </div>
        </div>

        {/* 3. Goal & Constraints */}
        <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
          <div className="space-y-1">
            <label className="block text-xs font-semibold text-slate-300 uppercase tracking-wider">
              Measurable Goal / Success Definition *
            </label>
            <textarea
              required
              rows={3}
              value={goal}
              onChange={(e) => setGoal(e.target.value)}
              placeholder="What specific outcome or testable benchmark defines complete resolution? (e.g. p99 < 250ms, L2 error < 1.5e-3)"
              className="w-full bg-slate-950 border border-slate-800 rounded-xl px-4 py-2.5 text-slate-100 placeholder:text-slate-600 focus:outline-none focus:border-emerald-500 text-sm resize-none"
            />
          </div>

          <div className="space-y-1">
            <label className="block text-xs font-semibold text-slate-300 uppercase tracking-wider">
              Constraints & Guardrails (Optional)
            </label>
            <textarea
              rows={3}
              value={constraintsText}
              onChange={(e) => setConstraintsText(e.target.value)}
              placeholder="Hardware budget limits, required gross margin %, ACID transactional rules, forbidden trade-offs..."
              className="w-full bg-slate-950 border border-slate-800 rounded-xl px-4 py-2.5 text-slate-100 placeholder:text-slate-600 focus:outline-none focus:border-emerald-500 text-sm resize-none"
            />
          </div>
        </div>

        {/* 4. Detailed Description */}
        <div className="space-y-1">
          <label className="block text-xs font-semibold text-slate-300 uppercase tracking-wider">
            Detailed Problem Description & Observable Evidence *
          </label>
          <textarea
            required
            rows={5}
            value={description}
            onChange={(e) => setDescription(e.target.value)}
            placeholder="Describe what happened, error logs, stack traces, mathematical equations, metric drops, and previous failed attempts..."
            className="w-full bg-slate-950 border border-slate-800 rounded-xl px-4 py-3 text-slate-100 placeholder:text-slate-600 focus:outline-none focus:border-emerald-500 text-sm resize-none font-mono"
          />
        </div>

        {/* 5. Supporting Files Upload */}
        <div className="space-y-2">
          <label className="block text-xs font-semibold text-slate-300 uppercase tracking-wider">
            Attach Evidence Files (Logs, Datasets, Screenshots)
          </label>
          <div className="border-2 border-dashed border-slate-800 hover:border-slate-700 rounded-xl p-6 text-center bg-slate-950/40 transition">
            <Upload className="w-8 h-8 text-slate-500 mx-auto mb-2" />
            <p className="text-xs text-slate-300 font-medium">
              Click to select or drag & drop evidence files
            </p>
            <p className="text-[11px] text-slate-500 mt-1">
              Supports .txt, .log, .json, .csv, .pdf, .png, .jpg (Max 15MB per file)
            </p>
            <input
              type="file"
              multiple
              onChange={handleFileChange}
              className="absolute inset-0 opacity-0 cursor-pointer"
            />
          </div>

          {files.length > 0 && (
            <div className="space-y-1.5 pt-2">
              {files.map((file, idx) => (
                <div
                  key={idx}
                  className="flex items-center justify-between p-2.5 rounded-lg bg-slate-800/60 border border-slate-700/60 text-xs"
                >
                  <div className="flex items-center gap-2 truncate">
                    <FileText className="w-4 h-4 text-emerald-400 shrink-0" />
                    <span className="text-slate-200 font-mono truncate">{file.name}</span>
                    <span className="text-slate-500 text-[10px]">({Math.round(file.size / 1024)} KB)</span>
                  </div>
                  <button
                    type="button"
                    onClick={() => removeFile(idx)}
                    className="text-slate-400 hover:text-rose-400 p-1 rounded"
                  >
                    <X className="w-4 h-4" />
                  </button>
                </div>
              ))}
            </div>
          )}
        </div>

        {/* Submit Actions */}
        <div className="flex items-center justify-end gap-3 pt-4 border-t border-slate-800">
          <button
            type="button"
            onClick={onCancel}
            disabled={isSubmitting}
            className="px-5 py-2.5 text-sm font-medium text-slate-300 hover:bg-slate-800 rounded-xl transition"
          >
            Cancel
          </button>
          <button
            type="submit"
            disabled={isSubmitting}
            className="flex items-center gap-2 px-6 py-2.5 bg-emerald-600 hover:bg-emerald-500 text-white rounded-xl text-sm font-semibold transition shadow-lg shadow-emerald-950/40 disabled:opacity-50"
          >
            {isSubmitting ? (
              <>
                <div className="w-4 h-4 border-2 border-white/20 border-t-white rounded-full animate-spin"></div>
                <span>Analyzing Problem Case...</span>
              </>
            ) : (
              <>
                <Sparkles className="w-4 h-4" />
                <span>Launch Sentinel Investigation</span>
              </>
            )}
          </button>
        </div>
      </form>
    </div>
  );
};
