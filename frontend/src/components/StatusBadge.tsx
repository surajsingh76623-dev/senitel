import React from 'react';
import { ProblemStatus } from '../types';
import { 
  FileEdit, 
  Search, 
  Lightbulb, 
  PlayCircle, 
  Smile, 
  CheckCircle2, 
  XCircle, 
  HelpCircle 
} from 'lucide-react';

interface Props {
  status: ProblemStatus;
  size?: 'sm' | 'md' | 'lg';
}

export const StatusBadge: React.FC<Props> = ({ status, size = 'md' }) => {
  const config: Record<ProblemStatus, { label: string; bg: string; text: string; border: string; icon: React.ReactNode }> = {
    DRAFT: {
      label: 'Draft',
      bg: 'bg-slate-800/80',
      text: 'text-slate-300',
      border: 'border-slate-700',
      icon: <FileEdit className="w-3.5 h-3.5" />,
    },
    INVESTIGATING: {
      label: 'Investigating',
      bg: 'bg-blue-950/60',
      text: 'text-blue-300',
      border: 'border-blue-800/60',
      icon: <Search className="w-3.5 h-3.5" />,
    },
    PROPOSED: {
      label: 'Solutions Proposed',
      bg: 'bg-purple-950/60',
      text: 'text-purple-300',
      border: 'border-purple-800/60',
      icon: <Lightbulb className="w-3.5 h-3.5" />,
    },
    IN_PROGRESS: {
      label: 'In Progress',
      bg: 'bg-amber-950/60',
      text: 'text-amber-300',
      border: 'border-amber-800/60',
      icon: <PlayCircle className="w-3.5 h-3.5" />,
    },
    USER_REPORTED_SUCCESS: {
      label: 'User-Reported Success',
      bg: 'bg-cyan-950/60',
      text: 'text-cyan-300',
      border: 'border-cyan-800/60',
      icon: <Smile className="w-3.5 h-3.5" />,
    },
    VERIFIED: {
      label: 'Independently Verified',
      bg: 'bg-emerald-950/70',
      text: 'text-emerald-300',
      border: 'border-emerald-600/70',
      icon: <CheckCircle2 className="w-3.5 h-3.5" />,
    },
    FAILED: {
      label: 'Failed',
      bg: 'bg-rose-950/60',
      text: 'text-rose-300',
      border: 'border-rose-800/60',
      icon: <XCircle className="w-3.5 h-3.5" />,
    },
    NEEDS_MORE_EVIDENCE: {
      label: 'Needs More Evidence',
      bg: 'bg-orange-950/60',
      text: 'text-orange-300',
      border: 'border-orange-800/60',
      icon: <HelpCircle className="w-3.5 h-3.5" />,
    },
  };

  const item = config[status] || config.DRAFT;
  const padding = size === 'sm' ? 'px-2 py-0.5 text-xs' : size === 'lg' ? 'px-3.5 py-1.5 text-sm font-medium' : 'px-2.5 py-1 text-xs font-medium';

  return (
    <span className={`inline-flex items-center gap-1.5 rounded-full border ${item.bg} ${item.text} ${item.border} ${padding}`}>
      {item.icon}
      <span>{item.label}</span>
    </span>
  );
};
