import React from 'react';
import { ProblemCategory } from '../types';
import { Terminal, Binary, BarChart3, HelpCircle, Wrench } from 'lucide-react';

interface Props {
  category: ProblemCategory;
}

export const CategoryBadge: React.FC<Props> = ({ category }) => {
  const config = {
    SOFTWARE_DEBUGGING: {
      label: 'Software & Code',
      bg: 'bg-emerald-950/40',
      text: 'text-emerald-400',
      border: 'border-emerald-800/40',
      icon: <Terminal className="w-3.5 h-3.5" />,
    },
    ACADEMIC_MATH: {
      label: 'Study & Math',
      bg: 'bg-indigo-950/40',
      text: 'text-indigo-400',
      border: 'border-indigo-800/40',
      icon: <Binary className="w-3.5 h-3.5" />,
    },
    ENGINEERING_TECHNICAL: {
      label: 'Engineering (Civil/Mech/Elec)',
      bg: 'bg-amber-950/40',
      text: 'text-amber-400',
      border: 'border-amber-800/40',
      icon: <Wrench className="w-3.5 h-3.5" />,
    },
    DATA_BUSINESS_ANALYSIS: {
      label: 'Business & Data',
      bg: 'bg-sky-950/40',
      text: 'text-sky-400',
      border: 'border-sky-800/40',
      icon: <BarChart3 className="w-3.5 h-3.5" />,
    },
    GENERAL: {
      label: 'General Problem',
      bg: 'bg-slate-800/50',
      text: 'text-slate-300',
      border: 'border-slate-700/60',
      icon: <HelpCircle className="w-3.5 h-3.5" />,
    },
  };

  const item = config[category] || config.GENERAL;

  return (
    <span className={`inline-flex items-center gap-1.5 px-2.5 py-1 rounded-md text-xs font-medium border ${item.bg} ${item.text} ${item.border}`}>
      {item.icon}
      <span>{item.label}</span>
    </span>
  );
};
