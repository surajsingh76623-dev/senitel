import {
  ProblemCaseSummary,
  ProblemCaseDetail,
  ProblemCategory,
  ProblemStatus,
  ProblemUrgency,
  ActionItem,
  VerificationRun,
  KnowledgeEntry,
  SystemStats,
  SystemSettings,
  DeterministicCheckResponse,
  VerificationPlanResult,
  ClarificationEntry,
  EvidenceItem,
} from '../types';

const API_BASE = (import.meta as any).env?.VITE_API_BASE_URL || '/api';

async function handleResponse<T>(res: Response): Promise<T> {
  if (!res.ok) {
    let errorMsg = `Server error ${res.status}`;
    try {
      const errJson = await res.json();
      if (errJson.validationErrors && errJson.validationErrors.length > 0) {
        errorMsg = errJson.validationErrors.join(', ');
      } else if (errJson.message) {
        errorMsg = errJson.message;
      }
    } catch {
      // Ignore JSON parse error
    }
    throw new Error(errorMsg);
  }
  if (res.status === 204) {
    return {} as T;
  }
  return res.json();
}

export const api = {
  // Problem Cases
  async getCases(query?: string, category?: ProblemCategory, status?: ProblemStatus): Promise<ProblemCaseSummary[]> {
    const params = new URLSearchParams();
    if (query) params.append('query', query);
    if (category) params.append('category', category);
    if (status) params.append('status', status);
    const qs = params.toString() ? `?${params.toString()}` : '';
    const res = await fetch(`${API_BASE}/cases${qs}`);
    return handleResponse<ProblemCaseSummary[]>(res);
  },

  async getCase(id: string): Promise<ProblemCaseDetail> {
    const res = await fetch(`${API_BASE}/cases/${id}`);
    return handleResponse<ProblemCaseDetail>(res);
  },

  async createCase(payload: {
    title: string;
    description: string;
    category: ProblemCategory;
    goal: string;
    constraintsText?: string;
    urgency: ProblemUrgency;
  }): Promise<ProblemCaseDetail> {
    const res = await fetch(`${API_BASE}/cases`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(payload),
    });
    return handleResponse<ProblemCaseDetail>(res);
  },

  async updateCase(id: string, payload: Partial<ProblemCaseDetail>): Promise<ProblemCaseDetail> {
    const res = await fetch(`${API_BASE}/cases/${id}`, {
      method: 'PUT',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(payload),
    });
    return handleResponse<ProblemCaseDetail>(res);
  },

  async transitionStatus(id: string, targetStatus: ProblemStatus, reason?: string, actor?: string): Promise<ProblemCaseDetail> {
    const res = await fetch(`${API_BASE}/cases/${id}/status`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ targetStatus, reason, actor }),
    });
    return handleResponse<ProblemCaseDetail>(res);
  },

  async deleteCase(id: string): Promise<void> {
    const res = await fetch(`${API_BASE}/cases/${id}`, { method: 'DELETE' });
    return handleResponse<void>(res);
  },

  // Investigation Workflow
  async triggerInvestigation(caseId: string): Promise<ProblemCaseDetail> {
    const res = await fetch(`${API_BASE}/cases/${caseId}/investigate`, {
      method: 'POST',
    });
    return handleResponse<ProblemCaseDetail>(res);
  },

  async submitClarification(caseId: string, responses: ClarificationEntry[]): Promise<ProblemCaseDetail> {
    const res = await fetch(`${API_BASE}/cases/${caseId}/clarify`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ responses }),
    });
    return handleResponse<ProblemCaseDetail>(res);
  },

  async selectSolution(caseId: string, solutionId: string): Promise<ProblemCaseDetail> {
    const res = await fetch(`${API_BASE}/cases/${caseId}/select-solution`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ solutionId }),
    });
    return handleResponse<ProblemCaseDetail>(res);
  },

  async toggleActionItem(caseId: string, actionId: string, isCompleted: boolean): Promise<ActionItem> {
    const res = await fetch(`${API_BASE}/cases/${caseId}/actions/${actionId}`, {
      method: 'PATCH',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ isCompleted }),
    });
    return handleResponse<ActionItem>(res);
  },

  // Verification
  async recordVerification(caseId: string, payload: {
    solutionId?: string;
    testProcedure: string;
    expectedResult: string;
    actualResult?: string;
    status: string;
    verificationType: string;
    evidenceNotes?: string;
    recordedBy?: string;
    isReproducible?: boolean;
  }): Promise<VerificationRun> {
    const res = await fetch(`${API_BASE}/cases/${caseId}/verifications`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(payload),
    });
    return handleResponse<VerificationRun>(res);
  },

  async runDeterministicCheck(caseId: string, payload: {
    checkType: string;
    solutionId?: string;
    parameters?: Record<string, any>;
  }): Promise<DeterministicCheckResponse> {
    const res = await fetch(`${API_BASE}/cases/${caseId}/verifications/deterministic-check`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(payload),
    });
    return handleResponse<DeterministicCheckResponse>(res);
  },

  async getVerificationPlan(caseId: string, solutionId: string): Promise<VerificationPlanResult> {
    const res = await fetch(`${API_BASE}/cases/${caseId}/verifications/plan?solutionId=${encodeURIComponent(solutionId)}`);
    return handleResponse<VerificationPlanResult>(res);
  },

  // Evidence Files
  async uploadEvidence(caseId: string, file: File, summary?: string): Promise<EvidenceItem> {
    const formData = new FormData();
    formData.append('file', file);
    if (summary) formData.append('summary', summary);

    const res = await fetch(`${API_BASE}/cases/${caseId}/evidence`, {
      method: 'POST',
      body: formData,
    });
    return handleResponse<EvidenceItem>(res);
  },

  getEvidenceDownloadUrl(evidenceId: string): string {
    return `${API_BASE}/evidence/${evidenceId}/download`;
  },

  // Knowledge Library
  async getKnowledge(query?: string, category?: string): Promise<KnowledgeEntry[]> {
    const params = new URLSearchParams();
    if (query) params.append('query', query);
    if (category) params.append('category', category);
    const qs = params.toString() ? `?${params.toString()}` : '';
    const res = await fetch(`${API_BASE}/knowledge${qs}`);
    return handleResponse<KnowledgeEntry[]>(res);
  },

  async getKnowledgeEntry(id: string): Promise<KnowledgeEntry> {
    const res = await fetch(`${API_BASE}/knowledge/${id}`);
    return handleResponse<KnowledgeEntry>(res);
  },

  async publishKnowledge(caseId: string, payload: {
    title: string;
    category: string;
    problemSummary: string;
    solutionSummary: string;
    verificationSummary: string;
    anonymizedLessons: string;
    tags?: string;
    approvedBy: string;
  }): Promise<KnowledgeEntry> {
    const res = await fetch(`${API_BASE}/cases/${caseId}/publish-knowledge`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(payload),
    });
    return handleResponse<KnowledgeEntry>(res);
  },

  // System Diagnostics
  async getSystemStats(): Promise<SystemStats> {
    const res = await fetch(`${API_BASE}/system/stats`);
    return handleResponse<SystemStats>(res);
  },

  async getSystemSettings(): Promise<SystemSettings> {
    const res = await fetch(`${API_BASE}/system/settings`);
    return handleResponse<SystemSettings>(res);
  },
};
