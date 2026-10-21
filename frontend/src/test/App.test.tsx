import { describe, it, expect, vi, beforeEach } from 'vitest';
import { render, screen, waitFor } from '@testing-library/react';
import React from 'react';
import App from '../App';
import { StatusBadge } from '../components/StatusBadge';
import { CategoryBadge } from '../components/CategoryBadge';
import { UrgencyBadge } from '../components/UrgencyBadge';

describe('Sentinel Frontend Core Component Tests', () => {
  beforeEach(() => {
    vi.stubGlobal('fetch', vi.fn((url: string) => {
      if (url.includes('/api/cases')) {
        return Promise.resolve({
          ok: true,
          json: () => Promise.resolve([
            {
              id: 'case-sw-001',
              title: 'PostgreSQL Connection Pool Exhaustion',
              category: 'SOFTWARE_DEBUGGING',
              categoryDisplayName: 'Software Debugging',
              goal: 'Maintain p99 < 250ms',
              urgency: 'CRITICAL',
              status: 'VERIFIED',
              statusDisplayName: 'Verified',
              hasVerifiedRun: true,
              evidenceCount: 1,
              solutionsCount: 3,
              actionItemsCount: 4,
              completedActionItemsCount: 4,
              verificationRunsCount: 1,
              createdAt: new Date().toISOString(),
              updatedAt: new Date().toISOString(),
            }
          ]),
        });
      }
      if (url.includes('/api/system/stats')) {
        return Promise.resolve({
          ok: true,
          json: () => Promise.resolve({
            totalCases: 3,
            draftCases: 0,
            investigatingCases: 0,
            proposedCases: 1,
            inProgressCases: 1,
            userSuccessCases: 0,
            verifiedCases: 1,
            failedCases: 0,
            needsEvidenceCases: 0,
            knowledgeEntriesCount: 1,
            evidenceFilesCount: 2,
            casesByCategory: { SOFTWARE_DEBUGGING: 1, ACADEMIC_MATH: 1, DATA_BUSINESS_ANALYSIS: 1 },
          }),
        });
      }
      if (url.includes('/api/system/settings')) {
        return Promise.resolve({
          ok: true,
          json: () => Promise.resolve({
            aiProvider: 'Deterministic Demo Engine',
            aiModel: 'gpt-4o',
            hasApiKey: false,
            maskedApiKey: 'None (Operating in Demo Mode)',
            storageLocation: './uploads',
            maxFileSizeMb: 15,
            isDemoMode: true,
            databaseProfile: '[dev]',
            appVersion: '1.0.0-PROD-CANDIDATE',
          }),
        });
      }
      return Promise.resolve({
        ok: true,
        json: () => Promise.resolve([]),
      });
    }));
  });

  it('renders Sentinel header and branding', async () => {
    render(<App />);
    expect(screen.getByText('SENTINEL')).toBeInTheDocument();
    expect(screen.getByText('Problem Investigation & Verification')).toBeInTheDocument();
  });

  it('renders dashboard with primary metrics and deep workflows', async () => {
    render(<App />);
    await waitFor(() => {
      expect(screen.getByText(/Turn Ambiguous Problems into/i)).toBeInTheDocument();
      expect(screen.getByText(/Core Deep Investigation Workflows/i)).toBeInTheDocument();
      expect(screen.getByText(/1. Software Debugging/i)).toBeInTheDocument();
      expect(screen.getByText(/2. Academic & Math Problem-Solving/i)).toBeInTheDocument();
      expect(screen.getByText(/3. Data & Business Analysis/i)).toBeInTheDocument();
    });
  });

  it('StatusBadge correctly distinguishes User-Reported Success from Independently Verified', () => {
    const { container: verifiedContainer } = render(<StatusBadge status="VERIFIED" />);
    expect(screen.getByText('Independently Verified')).toBeInTheDocument();

    const { container: successContainer } = render(<StatusBadge status="USER_REPORTED_SUCCESS" />);
    expect(screen.getByText('User-Reported Success')).toBeInTheDocument();
  });

  it('CategoryBadge renders deep workflow labels', () => {
    render(<CategoryBadge category="SOFTWARE_DEBUGGING" />);
    expect(screen.getByText('Software Debugging')).toBeInTheDocument();

    render(<CategoryBadge category="ACADEMIC_MATH" />);
    expect(screen.getByText('Academic & Math')).toBeInTheDocument();

    render(<CategoryBadge category="DATA_BUSINESS_ANALYSIS" />);
    expect(screen.getByText('Data & Business')).toBeInTheDocument();
  });

  it('UrgencyBadge renders priority ratings', () => {
    render(<UrgencyBadge urgency="CRITICAL" />);
    expect(screen.getByText('Critical')).toBeInTheDocument();

    render(<UrgencyBadge urgency="HIGH" />);
    expect(screen.getByText('High')).toBeInTheDocument();
  });
});
