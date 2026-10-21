// Sentinel TypeScript Domain Models and API Contracts

export type ProblemCategory = 
  | 'SOFTWARE_DEBUGGING' 
  | 'ACADEMIC_MATH' 
  | 'DATA_BUSINESS_ANALYSIS' 
  | 'GENERAL';

export type ProblemUrgency = 'LOW' | 'MEDIUM' | 'HIGH' | 'CRITICAL';

export type ProblemStatus = 
  | 'DRAFT' 
  | 'INVESTIGATING' 
  | 'PROPOSED' 
  | 'IN_PROGRESS' 
  | 'USER_REPORTED_SUCCESS' 
  | 'VERIFIED' 
  | 'FAILED' 
  | 'NEEDS_MORE_EVIDENCE';

export type VerificationStatus = 'PENDING' | 'PASSED' | 'FAILED' | 'INCONCLUSIVE';

export type VerificationType = 'MANUAL' | 'DETERMINISTIC_CALCULATOR' | 'CODE_CHECK';

export type AuditEventType = 
  | 'CASE_CREATED'
  | 'CASE_UPDATED'
  | 'EVIDENCE_UPLOADED'
  | 'INVESTIGATION_PERFORMED'
  | 'CLARIFICATION_SUBMITTED'
  | 'SOLUTION_PROPOSED'
  | 'SOLUTION_SELECTED'
  | 'ACTION_ITEM_UPDATED'
  | 'VERIFICATION_EXECUTED'
  | 'STATUS_CHANGED'
  | 'KNOWLEDGE_PUBLISHED';

export interface EvidenceItem {
  id: string;
  caseId: string;
  originalFilename: string;
  mimeType: string;
  fileSize: number;
  checksumSha256: string;
  fileSummary?: string;
  uploadedAt: string;
}

export interface ClarificationEntry {
  question: string;
  answer: string;
}

export interface InvestigationFinding {
  id: string;
  caseId: string;
  knownFacts: string[];
  assumptions: string[];
  missingInformation: string[];
  identifiedConstraints: string[];
  successCriteria: string[];
  clarificationQuestions: string[];
  clarificationResponses?: ClarificationEntry[];
  diagnosticSummary: string;
  confidenceScore: number;
  isDemoOutput: boolean;
  analyzedAt: string;
}

export interface CandidateSolution {
  id: string;
  caseId: string;
  solutionIndex: number;
  title: string;
  summary: string;
  reasoning: string;
  supportingEvidence: string[];
  benefits: string[];
  tradeoffs: string[];
  risks: string[];
  effortLevel: 'LOW' | 'MEDIUM' | 'HIGH';
  confidenceLimitations: string;
  isSelected: boolean;
  createdAt: string;
}

export interface ActionItem {
  id: string;
  caseId: string;
  solutionId?: string;
  stepNumber: number;
  title: string;
  description: string;
  isCompleted: boolean;
  completedAt?: string;
  createdAt: string;
}

export interface VerificationRun {
  id: string;
  caseId: string;
  solutionId?: string;
  testProcedure: string;
  expectedResult: string;
  actualResult?: string;
  status: VerificationStatus;
  verificationType: VerificationType;
  evidenceNotes?: string;
  recordedBy: string;
  recordedAt: string;
  isReproducible: boolean;
}

export interface AuditEvent {
  id: string;
  caseId: string;
  eventType: AuditEventType;
  eventTypeDescription?: string;
  actor: string;
  description: string;
  payloadJson?: string;
  timestamp: string;
}

export interface ProblemCaseSummary {
  id: string;
  title: string;
  description: string;
  category: ProblemCategory;
  categoryDisplayName: string;
  goal: string;
  urgency: ProblemUrgency;
  status: ProblemStatus;
  statusDisplayName: string;
  evidenceCount: number;
  solutionsCount: number;
  actionItemsCount: number;
  completedActionItemsCount: number;
  verificationRunsCount: number;
  hasVerifiedRun: boolean;
  createdAt: string;
  updatedAt: string;
}

export interface ProblemCaseDetail {
  id: string;
  title: string;
  description: string;
  category: ProblemCategory;
  categoryDisplayName: string;
  goal: string;
  constraintsText?: string;
  urgency: ProblemUrgency;
  status: ProblemStatus;
  statusDisplayName: string;
  selectedSolutionId?: string;
  userNotes?: string;
  createdAt: string;
  updatedAt: string;
  evidenceItems: EvidenceItem[];
  investigationFinding?: InvestigationFinding;
  candidateSolutions: CandidateSolution[];
  actionItems: ActionItem[];
  verificationRuns: VerificationRun[];
  auditEvents: AuditEvent[];
}

export interface KnowledgeEntry {
  id: string;
  sourceCaseId?: string;
  title: string;
  category: string;
  problemSummary: string;
  solutionSummary: string;
  verificationSummary: string;
  anonymizedLessons: string;
  tags?: string;
  isApproved: boolean;
  approvedBy: string;
  approvedAt: string;
}

export interface SystemStats {
  totalCases: number;
  draftCases: number;
  investigatingCases: number;
  proposedCases: number;
  inProgressCases: number;
  userSuccessCases: number;
  verifiedCases: number;
  failedCases: number;
  needsEvidenceCases: number;
  knowledgeEntriesCount: number;
  evidenceFilesCount: number;
  casesByCategory: Record<string, number>;
}

export interface SystemSettings {
  aiProvider: string;
  aiModel: string;
  hasApiKey: boolean;
  maskedApiKey: string;
  storageLocation: string;
  maxFileSizeMb: number;
  isDemoMode: boolean;
  databaseProfile: string;
  appVersion: string;
}

export interface DeterministicCheckResponse {
  passed: boolean;
  status: VerificationStatus;
  checkName: string;
  expectedCondition: string;
  actualValue: any;
  calculationDetails: string;
  testProcedure: string;
  evidenceNotes: string;
  outputMetrics: Record<string, any>;
}

export interface VerificationPlanResult {
  testProcedure: string;
  expectedResult: string;
  recommendedType: VerificationType;
  verificationRationale: string;
}
