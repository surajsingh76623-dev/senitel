-- ==============================================================================
-- SENTINEL V1 Database Schema Migration
-- Compatible with both PostgreSQL 16 and H2 (PostgreSQL compatibility mode)
-- ==============================================================================

CREATE TABLE problem_cases (
    id VARCHAR(36) PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    description TEXT NOT NULL,
    category VARCHAR(50) NOT NULL,
    goal TEXT NOT NULL,
    constraints_text TEXT,
    urgency VARCHAR(20) NOT NULL,
    status VARCHAR(30) NOT NULL,
    selected_solution_id VARCHAR(36),
    user_notes TEXT,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL
);

CREATE INDEX idx_cases_category ON problem_cases(category);
CREATE INDEX idx_cases_status ON problem_cases(status);
CREATE INDEX idx_cases_created ON problem_cases(created_at);

CREATE TABLE evidence_items (
    id VARCHAR(36) PRIMARY KEY,
    case_id VARCHAR(36) NOT NULL REFERENCES problem_cases(id) ON DELETE CASCADE,
    original_filename VARCHAR(255) NOT NULL,
    storage_filename VARCHAR(255) NOT NULL,
    mime_type VARCHAR(100) NOT NULL,
    file_size BIGINT NOT NULL,
    checksum_sha256 VARCHAR(64),
    file_summary TEXT,
    uploaded_at TIMESTAMP NOT NULL
);

CREATE INDEX idx_evidence_case ON evidence_items(case_id);

CREATE TABLE investigation_findings (
    id VARCHAR(36) PRIMARY KEY,
    case_id VARCHAR(36) NOT NULL UNIQUE REFERENCES problem_cases(id) ON DELETE CASCADE,
    known_facts TEXT NOT NULL,
    assumptions TEXT NOT NULL,
    missing_information TEXT NOT NULL,
    identified_constraints TEXT NOT NULL,
    success_criteria TEXT NOT NULL,
    clarification_questions TEXT,
    clarification_responses TEXT,
    diagnostic_summary TEXT,
    confidence_score DOUBLE PRECISION,
    is_demo_output BOOLEAN NOT NULL DEFAULT FALSE,
    analyzed_at TIMESTAMP NOT NULL
);

CREATE TABLE candidate_solutions (
    id VARCHAR(36) PRIMARY KEY,
    case_id VARCHAR(36) NOT NULL REFERENCES problem_cases(id) ON DELETE CASCADE,
    solution_index INT NOT NULL,
    title VARCHAR(255) NOT NULL,
    summary TEXT NOT NULL,
    reasoning TEXT NOT NULL,
    supporting_evidence TEXT,
    benefits TEXT,
    tradeoffs TEXT,
    risks TEXT,
    effort_level VARCHAR(20) NOT NULL,
    confidence_limitations TEXT NOT NULL,
    is_selected BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP NOT NULL
);

CREATE INDEX idx_solutions_case ON candidate_solutions(case_id);

CREATE TABLE action_items (
    id VARCHAR(36) PRIMARY KEY,
    case_id VARCHAR(36) NOT NULL REFERENCES problem_cases(id) ON DELETE CASCADE,
    solution_id VARCHAR(36) REFERENCES candidate_solutions(id) ON DELETE CASCADE,
    step_number INT NOT NULL,
    title VARCHAR(255) NOT NULL,
    description TEXT NOT NULL,
    is_completed BOOLEAN NOT NULL DEFAULT FALSE,
    completed_at TIMESTAMP,
    created_at TIMESTAMP NOT NULL
);

CREATE INDEX idx_actions_case ON action_items(case_id);

CREATE TABLE verification_runs (
    id VARCHAR(36) PRIMARY KEY,
    case_id VARCHAR(36) NOT NULL REFERENCES problem_cases(id) ON DELETE CASCADE,
    solution_id VARCHAR(36) REFERENCES candidate_solutions(id) ON DELETE SET NULL,
    test_procedure TEXT NOT NULL,
    expected_result TEXT NOT NULL,
    actual_result TEXT,
    status VARCHAR(20) NOT NULL,
    verification_type VARCHAR(30) NOT NULL,
    evidence_notes TEXT,
    recorded_by VARCHAR(100) NOT NULL,
    recorded_at TIMESTAMP NOT NULL,
    is_reproducible BOOLEAN NOT NULL DEFAULT TRUE
);

CREATE INDEX idx_verifications_case ON verification_runs(case_id);

CREATE TABLE audit_events (
    id VARCHAR(36) PRIMARY KEY,
    case_id VARCHAR(36) NOT NULL REFERENCES problem_cases(id) ON DELETE CASCADE,
    event_type VARCHAR(50) NOT NULL,
    actor VARCHAR(100) NOT NULL,
    description TEXT NOT NULL,
    payload_json TEXT,
    timestamp TIMESTAMP NOT NULL
);

CREATE INDEX idx_audit_case ON audit_events(case_id);
CREATE INDEX idx_audit_time ON audit_events(timestamp);

CREATE TABLE knowledge_entries (
    id VARCHAR(36) PRIMARY KEY,
    source_case_id VARCHAR(36) REFERENCES problem_cases(id) ON DELETE SET NULL,
    title VARCHAR(255) NOT NULL,
    category VARCHAR(50) NOT NULL,
    problem_summary TEXT NOT NULL,
    solution_summary TEXT NOT NULL,
    verification_summary TEXT NOT NULL,
    anonymized_lessons TEXT NOT NULL,
    tags VARCHAR(255),
    is_approved BOOLEAN NOT NULL DEFAULT FALSE,
    approved_by VARCHAR(100) NOT NULL,
    approved_at TIMESTAMP NOT NULL
);

CREATE INDEX idx_knowledge_category ON knowledge_entries(category);
CREATE INDEX idx_knowledge_approved ON knowledge_entries(is_approved);
