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
  AlertCircle,
  Wrench,
  ChevronDown,
  ChevronUp,
  CheckCircle2
} from 'lucide-react';

interface Props {
  onCancel: () => void;
  onCreated: (newCaseId: string) => void;
}

export const CreateCaseView: React.FC<Props> = ({ onCancel, onCreated }) => {
  const [title, setTitle] = useState('');
  const [category, setCategory] = useState<ProblemCategory>('SOFTWARE_DEBUGGING');
  const [description, setDescription] = useState('');
  const [goal, setGoal] = useState('');
  const [constraintsText, setConstraintsText] = useState('');
  const [urgency, setUrgency] = useState<ProblemUrgency>('MEDIUM');
  const [showAdvanced, setShowAdvanced] = useState(false);
  const [files, setFiles] = useState<File[]>([]);
  const [isSubmitting, setIsSubmitting] = useState(false);
  const [error, setError] = useState<string | null>(null);

  const categories: { 
    id: ProblemCategory; 
    title: string; 
    subtitle: string; 
    icon: React.ReactNode;
    tag: string;
    accent: string;
  }[] = [
    {
      id: 'SOFTWARE_DEBUGGING',
      title: 'Software & Code',
      subtitle: 'Bugs, crashes, code errors, website or API failures',
      icon: <Terminal className="w-5 h-5 text-emerald-400" />,
      tag: 'Coding / Dev',
      accent: 'border-emerald-500 text-emerald-400 bg-emerald-950/20',
    },
    {
      id: 'ACADEMIC_MATH',
      title: 'Study & Student',
      subtitle: 'Math, physics, science, formulas, concept understanding',
      icon: <Binary className="w-5 h-5 text-indigo-400" />,
      tag: 'Math & Science',
      accent: 'border-indigo-500 text-indigo-400 bg-indigo-950/20',
    },
    {
      id: 'ENGINEERING_TECHNICAL',
      title: 'Engineering (Civil / Mech / Elec)',
      subtitle: 'Structures, load stress, circuits, machinery, physical builds',
      icon: <Wrench className="w-5 h-5 text-amber-400" />,
      tag: 'Engineering',
      accent: 'border-amber-500 text-amber-400 bg-amber-950/20',
    },
    {
      id: 'DATA_BUSINESS_ANALYSIS',
      title: 'Business & Work',
      subtitle: 'Sales drop, customer churn, office workflow, project costs',
      icon: <BarChart3 className="w-5 h-5 text-sky-400" />,
      tag: 'Business',
      accent: 'border-sky-500 text-sky-400 bg-sky-950/20',
    },
    {
      id: 'GENERAL',
      title: 'Everyday & Other Problem',
      subtitle: 'Any other practical problem, question, or troubleshooting',
      icon: <HelpCircle className="w-5 h-5 text-slate-400" />,
      tag: 'General',
      accent: 'border-purple-500 text-purple-400 bg-purple-950/20',
    },
  ];

  const handlePreFill = (templateKey: 'software' | 'math' | 'study' | 'engineering' | 'business') => {
    if (templateKey === 'software') {
      setCategory('SOFTWARE_DEBUGGING');
      setTitle('HikariCP Connection Pool Exhaustion & Spike in Latency During Flash Sale');
      setDescription('During a flash sale surge, our checkout API response time suddenly jumped from 150ms to 8500ms. Users started seeing ConnectionTimeoutException errors. The database server CPU is only at 28%, but all 500 connections in the pool were taken up and never released.');
      setGoal('Eliminate connection timeouts and maintain sub-250ms response times under heavy traffic.');
      setUrgency('HIGH');
    } else if (templateKey === 'math' || templateKey === 'study') {
      setCategory('ACADEMIC_MATH');
      setTitle('Gradient Explosion & Convergence Failure in Physics-Informed Neural Network (PINN)');
      setDescription('We are solving the 1D viscous Burgers equation u_t + u*u_x = (0.01/pi)*u_xx using a neural network. The training loss oscillates wildly near the shock wave formation at t=0.5 and the gradient explodes.');
      setGoal('Achieve stable convergence with relative L2 error norm < 1.5e-3 against the analytical benchmark.');
      setUrgency('MEDIUM');
    } else if (templateKey === 'engineering') {
      setCategory('ENGINEERING_TECHNICAL');
      setTitle('Diagonal Shear Cracks Forming Near Support Pier of Reinforced Concrete Bridge Beam');
      setDescription('During a routine bridge inspection, 0.45mm diagonal hairline shear cracks were found extending at 45-degree angles from the bottom edge near Pier #3. Heavy loaded dump trucks regularly cross this span, and we need to determine whether the shear capacity has been exceeded and how to reinforce it.');
      setGoal('Stop crack growth, reinforce shear capacity, and restore structural safety factor FoS >= 2.0.');
      setUrgency('HIGH');
    } else if (templateKey === 'business') {
      setCategory('DATA_BUSINESS_ANALYSIS');
      setTitle('Customer Churn Spike Following New Tier-2 Pricing Model & API Quota Limits');
      setDescription('Monthly customer cancellations jumped from 2.1% to 6.8% among our Tier-2 users right after we introduced a 50,000 API call cap with automatic $0.005/call extra charges. We need to find whether customers are leaving due to the price increase or surprise bills, and fix retention without losing profit.');
      setGoal('Identify the exact cause of churn and lower monthly cancellations back below 2.5% while maintaining >= 78% profit margin.');
      setUrgency('MEDIUM');
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
    if (!title.trim()) {
      setError('Please give your problem a title.');
      return;
    }
    if (!description.trim()) {
      setError('Please describe your problem so SENTINEL can analyze it.');
      return;
    }

    setError(null);
    setIsSubmitting(true);

    try {
      // 1. Create problem case (with user-friendly defaults for optional fields)
      const effectiveGoal = goal.trim() || `Solve and verify: ${title.trim()}`;
      const createdCase = await api.createCase({
        title: title.trim(),
        category,
        goal: effectiveGoal,
        constraintsText: constraintsText.trim() || undefined,
        urgency: urgency || 'MEDIUM',
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

        {/* Quick Example Fillers */}
        <div className="flex items-center gap-2 flex-wrap justify-end">
          <span className="text-xs text-slate-400 hidden sm:inline">Try an example:</span>
          <button
            type="button"
            onClick={() => handlePreFill('software')}
            className="px-2.5 py-1 text-xs bg-slate-800 hover:bg-slate-700 text-emerald-400 rounded-md transition border border-emerald-900/40"
          >
            💻 Coding Bug
          </button>
          <button
            type="button"
            onClick={() => handlePreFill('study')}
            className="px-2.5 py-1 text-xs bg-slate-800 hover:bg-slate-700 text-indigo-400 rounded-md transition border border-indigo-900/40"
          >
            📚 Student / Math
          </button>
          <button
            type="button"
            onClick={() => handlePreFill('engineering')}
            className="px-2.5 py-1 text-xs bg-slate-800 hover:bg-slate-700 text-amber-400 rounded-md transition border border-amber-900/40"
          >
            🏗️ Engineering
          </button>
          <button
            type="button"
            onClick={() => handlePreFill('business')}
            className="px-2.5 py-1 text-xs bg-slate-800 hover:bg-slate-700 text-sky-400 rounded-md transition border border-sky-900/40"
          >
            💼 Business
          </button>
        </div>
      </div>

      {error && (
        <div className="p-4 bg-red-950/40 border border-red-800/60 rounded-xl flex items-center gap-3 text-sm text-red-300">
          <AlertCircle className="w-5 h-5 shrink-0 text-red-400" />
          <span>{error}</span>
        </div>
      )}

      {/* Main Friendly Form */}
      <form onSubmit={handleSubmit} className="space-y-7 bg-slate-900 border border-slate-800 rounded-2xl p-6 sm:p-8 shadow-xl">
        <div className="space-y-1">
          <h2 className="text-2xl font-bold text-slate-100 flex items-center gap-2.5">
            <span>Ask SENTINEL to Investigate a Problem</span>
          </h2>
          <p className="text-sm text-slate-400">
            Tell us what happened in simple words. SENTINEL will analyze what went wrong, find the cause, and give you step-by-step solutions with test checks.
          </p>
        </div>

        {/* STEP 1: Select Category */}
        <div className="space-y-3">
          <div className="flex items-center justify-between">
            <label className="text-xs font-bold text-emerald-400 uppercase tracking-wider flex items-center gap-2">
              <span className="w-5 h-5 rounded-full bg-emerald-950 border border-emerald-500 text-emerald-400 flex items-center justify-center text-xs font-bold">1</span>
              <span>Which category does your problem belong to?</span>
            </label>
            <span className="text-xs text-slate-500">Click one to choose</span>
          </div>

          <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-3">
            {categories.map((cat) => {
              const isSelected = category === cat.id;
              return (
                <div
                  key={cat.id}
                  onClick={() => setCategory(cat.id)}
                  className={`p-4 rounded-xl border cursor-pointer transition flex flex-col justify-between gap-3 ${
                    isSelected
                      ? `bg-slate-850 ring-2 ring-emerald-500 border-transparent shadow-lg ${cat.accent}`
                      : 'bg-slate-950/60 border-slate-800 hover:border-slate-700 hover:bg-slate-850/40'
                  }`}
                >
                  <div className="flex items-start justify-between">
                    <div className="p-2.5 rounded-lg bg-slate-900 border border-slate-800">
                      {cat.icon}
                    </div>
                    {isSelected ? (
                      <span className="flex items-center gap-1 text-[11px] font-semibold text-emerald-400 bg-emerald-950/60 px-2 py-0.5 rounded-full border border-emerald-800/40">
                        <CheckCircle2 className="w-3 h-3" /> Selected
                      </span>
                    ) : (
                      <span className="text-[10px] text-slate-500 px-2 py-0.5 rounded-full bg-slate-900 border border-slate-800">
                        {cat.tag}
                      </span>
                    )}
                  </div>
                  <div>
                    <h4 className={`text-sm font-semibold ${isSelected ? 'text-slate-100' : 'text-slate-200'}`}>
                      {cat.title}
                    </h4>
                    <p className="text-xs text-slate-400 mt-1 leading-relaxed">{cat.subtitle}</p>
                  </div>
                </div>
              );
            })}
          </div>
        </div>

        {/* STEP 2: Problem Details */}
        <div className="space-y-4 pt-2">
          <label className="text-xs font-bold text-emerald-400 uppercase tracking-wider flex items-center gap-2">
            <span className="w-5 h-5 rounded-full bg-emerald-950 border border-emerald-500 text-emerald-400 flex items-center justify-center text-xs font-bold">2</span>
            <span>Describe your problem</span>
          </label>

          {/* Title input */}
          <div className="space-y-1.5">
            <label className="block text-xs font-medium text-slate-300">
              Problem Title <span className="text-rose-400">*</span>
            </label>
            <input
              type="text"
              required
              value={title}
              onChange={(e) => setTitle(e.target.value)}
              placeholder="e.g. Beam cracking under load, or Website login error, or Physics equation question..."
              className="w-full bg-slate-950 border border-slate-800 rounded-xl px-4 py-3 text-slate-100 placeholder:text-slate-600 focus:outline-none focus:border-emerald-500 text-sm"
            />
          </div>

          {/* Description input */}
          <div className="space-y-1.5">
            <div className="flex items-center justify-between">
              <label className="block text-xs font-medium text-slate-300">
                What happened? (Explain in your own words) <span className="text-rose-400">*</span>
              </label>
              <span className="text-xs text-slate-500">Describe what you see or what went wrong</span>
            </div>
            <textarea
              required
              rows={6}
              value={description}
              onChange={(e) => setDescription(e.target.value)}
              placeholder="Tell us everything you know: What were you doing? What error or issue did you notice? Any numbers, symptoms, or previous attempts you made?"
              className="w-full bg-slate-950 border border-slate-800 rounded-xl px-4 py-3 text-slate-100 placeholder:text-slate-600 focus:outline-none focus:border-emerald-500 text-sm resize-none leading-relaxed"
            />
          </div>
        </div>

        {/* STEP 3: Optional Files */}
        <div className="space-y-3 pt-2">
          <div className="flex items-center justify-between">
            <label className="text-xs font-semibold text-slate-300 uppercase tracking-wider flex items-center gap-2">
              <Upload className="w-4 h-4 text-slate-400" />
              <span>Attach Photos, Logs, or Documents (Optional)</span>
            </label>
            <span className="text-xs text-slate-500">Optional</span>
          </div>

          <label 
            htmlFor="evidence-file-input" 
            className="block relative border-2 border-dashed border-slate-800 hover:border-emerald-500/60 rounded-xl p-5 text-center bg-slate-950/40 hover:bg-slate-950/70 transition cursor-pointer"
          >
            <Upload className="w-7 h-7 text-slate-500 mx-auto mb-2 pointer-events-none" />
            <p className="text-xs text-slate-300 font-medium pointer-events-none">
              Click to select or drag & drop screenshots, logs, or files
            </p>
            <p className="text-[11px] text-slate-500 mt-1 pointer-events-none">
              Supports .png, .jpg, .pdf, .txt, .log, .json, .csv (Max 15MB each)
            </p>
            <input
              id="evidence-file-input"
              type="file"
              multiple
              onChange={handleFileChange}
              className="hidden"
            />
          </label>

          {files.length > 0 && (
            <div className="space-y-1.5 pt-1">
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

        {/* Collapsible Advanced Options */}
        <div className="border border-slate-800/70 rounded-xl overflow-hidden bg-slate-950/30">
          <button
            type="button"
            onClick={() => setShowAdvanced(!showAdvanced)}
            className="w-full flex items-center justify-between p-3.5 text-xs font-medium text-slate-400 hover:text-slate-200 hover:bg-slate-850/50 transition"
          >
            <span>Advanced Options (Urgency & Constraints)</span>
            {showAdvanced ? <ChevronUp className="w-4 h-4" /> : <ChevronDown className="w-4 h-4" />}
          </button>

          {showAdvanced && (
            <div className="p-4 border-t border-slate-800/60 space-y-4">
              <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                <div className="space-y-1">
                  <label className="block text-xs font-medium text-slate-300">
                    Urgency Level
                  </label>
                  <select
                    value={urgency}
                    onChange={(e) => setUrgency(e.target.value as ProblemUrgency)}
                    className="w-full bg-slate-900 border border-slate-800 rounded-lg px-3 py-2 text-slate-200 text-xs focus:outline-none focus:border-emerald-500"
                  >
                    <option value="LOW">Low (Not urgent / Casual research)</option>
                    <option value="MEDIUM">Medium (Normal priority)</option>
                    <option value="HIGH">High (Important / Major impact)</option>
                    <option value="CRITICAL">Critical (Immediate emergency / Outage)</option>
                  </select>
                </div>

                <div className="space-y-1">
                  <label className="block text-xs font-medium text-slate-300">
                    Target Resolution Goal (Optional)
                  </label>
                  <input
                    type="text"
                    value={goal}
                    onChange={(e) => setGoal(e.target.value)}
                    placeholder="e.g. Keep deflection below 5mm or eliminate timeout errors"
                    className="w-full bg-slate-900 border border-slate-800 rounded-lg px-3 py-2 text-slate-200 text-xs placeholder:text-slate-600 focus:outline-none focus:border-emerald-500"
                  />
                </div>
              </div>

              <div className="space-y-1">
                <label className="block text-xs font-medium text-slate-300">
                  Limitations / Guardrails (Optional)
                </label>
                <input
                  type="text"
                  value={constraintsText}
                  onChange={(e) => setConstraintsText(e.target.value)}
                  placeholder="e.g. Cannot increase hardware budget, must keep minimum safety factor FoS >= 2.0"
                  className="w-full bg-slate-900 border border-slate-800 rounded-lg px-3 py-2 text-slate-200 text-xs placeholder:text-slate-600 focus:outline-none focus:border-emerald-500"
                />
              </div>
            </div>
          )}
        </div>

        {/* Submit Actions */}
        <div className="flex items-center justify-between gap-3 pt-3 border-t border-slate-800">
          <button
            type="button"
            onClick={onCancel}
            disabled={isSubmitting}
            className="px-5 py-2.5 text-sm font-medium text-slate-400 hover:text-slate-200 hover:bg-slate-800 rounded-xl transition"
          >
            Cancel
          </button>
          <button
            type="submit"
            disabled={isSubmitting}
            className="flex items-center gap-2.5 px-7 py-3 bg-emerald-600 hover:bg-emerald-500 text-white rounded-xl text-sm font-semibold transition shadow-lg shadow-emerald-950/40 disabled:opacity-50"
          >
            {isSubmitting ? (
              <>
                <div className="w-4 h-4 border-2 border-white/20 border-t-white rounded-full animate-spin"></div>
                <span>Analyzing Problem & Diagnosing Cause...</span>
              </>
            ) : (
              <>
                <Sparkles className="w-4 h-4" />
                <span>Start Investigation & Find Solutions →</span>
              </>
            )}
          </button>
        </div>
      </form>
    </div>
  );
};
