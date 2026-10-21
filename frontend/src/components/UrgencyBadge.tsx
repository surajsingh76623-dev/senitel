import React from 'react';
import { ProblemUrgency } from '../types';
import { Flame, AlertTriangle, Clock, Info } from 'lucide-react';

interface Props {
  urgency: ProblemUrgency;
}

export const UrgencyBadge: React.FC<Props> = ({ urgency }) => {
  const config = {
    CRITICAL: { label: 'Critical', bg: 'bg-red-950/50', text: 'text-red-400', border: 'border-red-800/60', icon: <Flame className="w-3 h-3" /> },
    HIGH: { label: 'High', bg: 'bg-orange-950/50', text: 'text-orange-400', border: 'border-orange-800/60', icon: <AlertTriangle className="w-3 h-3" /> },
    MEDIUM: { label: 'Medium', bg: 'bg-amber-950/50', text: 'text-amber-400', border: 'border-amber-800/60', icon: <Clock className="w-3 h-3" /> },
    LOW: { label: 'Low', bg: 'bg-slate-800/60', text: 'text-slate-400', border: 'border-slate-700', icon: <Info className="w-3 h-3" /> },
  };

  const item = config[urgency] || config.MEDIUM;

  return (
    <span className={`inline-flex items-center gap-1 px-2 py-0.5 rounded text-xs font-medium border ${item.bg} ${item.text} ${item.border}`}>
      {item.icon}
      <span>{item.label}</span>
    </span>
  );
};
