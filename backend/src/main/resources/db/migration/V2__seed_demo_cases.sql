-- ==============================================================================
-- SENTINEL V2 Seed Data Migration
-- Pre-populates realistic, structured domain cases for demonstration and testing
-- ==============================================================================

-- Case 1: Software Debugging
INSERT INTO problem_cases (id, title, description, category, goal, constraints_text, urgency, status, selected_solution_id, user_notes, created_at, updated_at)
VALUES (
    'case-sw-001',
    'PostgreSQL Connection Pool Exhaustion & Spike in p99 Latency During Flash Sale',
    'During our Friday flash sale event, the Order Checkout API response time degraded from 120ms to over 8500ms, triggering HTTP 500/504 errors. HikariCP connection pool logs reported "Connection is not available, request timed out after 30000ms". Database CPU was at only 28%, but active sessions reached the max connection cap.',
    'SOFTWARE_DEBUGGING',
    'Identify the root cause of connection starvation, eliminate timeout spikes, and maintain p99 latency < 250ms under 5000 RPS without vertical DB scaling.',
    'No increase in database server hardware cost. Must preserve strict transactional consistency on order placement and inventory reservation.',
    'CRITICAL',
    'VERIFIED',
    'sol-sw-001',
    'Investigated with HikariCP leak detection and query tracing. Verified under synthetic load testing.',
    '2026-10-10 09:00:00',
    '2026-10-10 16:30:00'
);

INSERT INTO investigation_findings (id, case_id, known_facts, assumptions, missing_information, identified_constraints, success_criteria, clarification_questions, clarification_responses, diagnostic_summary, confidence_score, is_demo_output, analyzed_at)
VALUES (
    'find-sw-001',
    'case-sw-001',
    '["Database CPU remained low (28%) during outage","HikariCP pool limit set to 50 connections across 10 service replicas","Active connections spiked to max limit (500 total) within 90 seconds of traffic surge","External payment gateway call is invoked inside an active @Transactional checkout method","Connection timeout threshold is 30,000ms"]',
    '["Database server is not CPU or I/O bottlenecked; threads are waiting for idle pool connections","Payment gateway latency increased slightly from 180ms to 650ms during peak, causing connection hold time inflation"]',
    '["Exact breakdown of time spent inside database queries vs external network I/O in checkout transaction","Whether non-transactional read queries are also using the primary write pool"]',
    '["Cannot disable ACID transactions for billing and inventory updates","Cannot horizontally scale database beyond current Aurora tier due to budget limits"]',
    '["HikariCP connection wait time p99 < 5ms","Zero connection acquisition timeout exceptions under 5,000 RPS load","Order checkout API p99 latency < 250ms"]',
    '["Is the external payment processor call synchronous or asynchronous?","Are read-only queries routed to a separate read replica?"]',
    '["The payment gateway call is currently synchronous and held inside @Transactional boundary.","Read queries currently share the write connection pool."]',
    'Classic connection starvation caused by holding relational database connections open across high-latency external network I/O calls. Payment gateway latency inflation multiplied by connection hold duration directly exhausted the pool.',
    0.96,
    true,
    '2026-10-10 09:15:00'
);

INSERT INTO candidate_solutions (id, case_id, solution_index, title, summary, reasoning, supporting_evidence, benefits, tradeoffs, risks, effort_level, confidence_limitations, is_selected, created_at)
VALUES (
    'sol-sw-001',
    'case-sw-001',
    1,
    'Decompose Transactional Boundary: Out-of-Transaction Payment Orchestration',
    'Refactor OrderCheckoutService to isolate DB writes into short-lived transactions before and after payment authorization, releasing the DB connection while waiting on the HTTP payment gateway.',
    'A database connection should never be held while waiting for network I/O. By reserving inventory in a 10ms transaction, issuing payment outside transactional context, and committing final order status in a second 8ms transaction, connection hold time drops by 95%.',
    '["HikariCP metrics showing 720ms average transaction duration with only 18ms actual query execution time","Payment gateway logs showing 650ms roundtrip"]',
    '["Releases connection pool pressure immediately","Maintains zero connection timeouts even at 10x traffic","Preserves existing hardware sizing"]',
    '["Requires compensating action (inventory release) if payment fails or crashes before second step","Adds slight application workflow complexity"]',
    '["Distributed state edge case if application crashes between payment success and order record commit"]',
    'MEDIUM',
    'Assumes payment gateway idempotent key mechanism is available and reliable.',
    true,
    '2026-10-10 09:30:00'
),
(
    'sol-sw-002',
    'case-sw-001',
    2,
    'Increase HikariCP Connection Pool Max Size to 150 per Pod',
    'Increase maximum pool size from 50 to 150 connections per pod to absorb burst volume.',
    'Provides quick buffer without immediate code restructuring.',
    '["Current max connections at 50 per replica"]',
    '["Instant deployment with zero code refactoring"]',
    '["Will overwhelm PostgreSQL maximum backend processes (150 * 10 = 1500 processes), causing context switching and DB thrashing","Masks root problem rather than solving it"]',
    '["High risk of complete database engine crash due to thread exhaustion and memory pressure"]',
    'LOW',
    'Dangerous anti-pattern for connection pooling under load.',
    false,
    '2026-10-10 09:30:00'
),
(
    'sol-sw-003',
    'case-sw-001',
    3,
    'Introduce PgBouncer / RDS Proxy Connection Multiplexing',
    'Deploy an intermediate connection pool proxy between microservices and PostgreSQL instance.',
    'Multiplexes thousands of client connections down to a fixed pool of server connections.',
    '["Database connection count limit is 600 total"]',
    '["Buffers incoming connection spikes safely","Protects DB server from process explosion"]',
    '["Does not solve transaction hold time starvation because transaction-level pooling cannot release server connection during open transaction"]',
    '["Additional infrastructure component to maintain and monitor"]',
    'HIGH',
    'Helpful for overall fleet hygiene, but does not eliminate hold-during-external-call bottleneck.',
    false,
    '2026-10-10 09:30:00'
);

INSERT INTO action_items (id, case_id, solution_id, step_number, title, description, is_completed, completed_at, created_at)
VALUES (
    'act-sw-001',
    'case-sw-001',
    'sol-sw-001',
    1,
    'Audit @Transactional annotations in CheckoutWorkflow',
    'Identify all methods enclosing REST calls, third-party SDK calls, or message queue operations inside database transactions.',
    true,
    '2026-10-10 10:15:00',
    '2026-10-10 09:45:00'
),
(
    'act-sw-002',
    'case-sw-001',
    'sol-sw-001',
    2,
    'Split Checkout Flow into Two-Phase Transaction with Compensating Action',
    'Phase 1: Reserve inventory and create PENDING order (Transaction A, commit). Phase 2: Call Payment Provider via HTTP client. Phase 3: Update Order to CONFIRMED (Transaction B, commit). If Phase 2 fails, execute compensation (cancel inventory).',
    true,
    '2026-10-10 12:40:00',
    '2026-10-10 09:45:00'
),
(
    'act-sw-003',
    'case-sw-001',
    'sol-sw-001',
    3,
    'Configure HikariCP Leak Detection and Active Pool Monitoring',
    'Set leakDetectionThreshold=5000ms and expose active/idle/pending thread metrics to Prometheus.',
    true,
    '2026-10-10 13:30:00',
    '2026-10-10 09:45:00'
),
(
    'act-sw-004',
    'case-sw-001',
    'sol-sw-001',
    4,
    'Execute Synthetic Load Test at 5,000 RPS for 15 Minutes',
    'Simulate realistic flash sale traffic using Gatling / k6 test script simulating 5,000 concurrent checkout requests.',
    true,
    '2026-10-10 15:45:00',
    '2026-10-10 09:45:00'
);

INSERT INTO verification_runs (id, case_id, solution_id, test_procedure, expected_result, actual_result, status, verification_type, evidence_notes, recorded_by, recorded_at, is_reproducible)
VALUES (
    'vr-sw-001',
    'case-sw-001',
    'sol-sw-001',
    'k6 stress load test generating 5,000 RPS across 10 pods for 15 minutes with simulated 600ms payment gateway delay.',
    'HikariCP connection acquisition wait time p99 < 5ms. Zero connection timeout exceptions. Overall API p99 < 200ms + external latency.',
    'Passed with flying colors: connection acquisition p99 was 2.8ms, active connections peaked at 14 per pod (out of 50 limit). Zero timeouts across 4,500,000 requests.',
    'PASSED',
    'MANUAL',
    'Attached load test report k6-summary-report.json and Grafana dashboard snapshot showing connection pool metrics.',
    'SRE Lead (David Chen)',
    '2026-10-10 16:15:00',
    true
);

INSERT INTO audit_events (id, case_id, event_type, actor, description, payload_json, timestamp)
VALUES 
    ('aud-001', 'case-sw-001', 'CASE_CREATED', 'David Chen (SRE)', 'Created problem case for connection pool exhaustion during flash sale', '{"category":"SOFTWARE_DEBUGGING","urgency":"CRITICAL"}', '2026-10-10 09:00:00'),
    ('aud-002', 'case-sw-001', 'INVESTIGATION_PERFORMED', 'SENTINEL Engine', 'Analyzed submitted stack trace and telemetry; identified network I/O inside transaction boundary', '{"confidence":0.96}', '2026-10-10 09:15:00'),
    ('aud-003', 'case-sw-001', 'SOLUTION_SELECTED', 'David Chen (SRE)', 'Selected Solution 1 (Decompose Transactional Boundary) over pool size increase', '{"solutionId":"sol-sw-001"}', '2026-10-10 09:35:00'),
    ('aud-004', 'case-sw-001', 'VERIFICATION_EXECUTED', 'David Chen (SRE)', 'Executed k6 load test verification run vr-sw-001. Status: PASSED', '{"status":"PASSED","verificationType":"MANUAL"}', '2026-10-10 16:15:00'),
    ('aud-005', 'case-sw-001', 'STATUS_CHANGED', 'David Chen (SRE)', 'Transitioned case status to VERIFIED following reproducible test passage', '{"oldStatus":"IN_PROGRESS","newStatus":"VERIFIED"}', '2026-10-10 16:20:00');

INSERT INTO knowledge_entries (id, source_case_id, title, category, problem_summary, solution_summary, verification_summary, anonymized_lessons, tags, is_approved, approved_by, approved_at)
VALUES (
    'kn-001',
    'case-sw-001',
    'Eliminating Database Connection Starvation by Decoupling External I/O from Transactions',
    'SOFTWARE_DEBUGGING',
    'High concurrency API experienced cascading 504 timeouts due to HikariCP pool exhaustion despite low database CPU utilization.',
    'Segregated synchronous external payment gateway network calls outside the @Transactional database demarcation into a two-phase commit with compensation pattern.',
    'Verified via synthetic stress load test at 5,000 RPS. Pool acquisition latency dropped from 30,000ms timeout to 2.8ms p99.',
    'Rule: Never hold open a relational database connection across third-party HTTP requests, gRPC calls, or disk I/O. Always scope transactions to pure database operations.',
    'postgresql,hikaricp,connection-pool,transactions,latency',
    true,
    'Architecture Review Board',
    '2026-10-10 16:30:00'
);

-- Case 2: Academic & Mathematical Problem Solving
INSERT INTO problem_cases (id, title, description, category, goal, constraints_text, urgency, status, selected_solution_id, user_notes, created_at, updated_at)
VALUES (
    'case-math-001',
    'Loss Function Instability and Gradient Explosion in Physics-Informed Neural Network (PINN)',
    'We are solving the 1D viscous Burgers equation u_t + u*u_x = nu*u_xx on domain x in [-1, 1], t in [0, 1] with kinematic viscosity nu = 0.01/pi. The residual loss function exhibits severe gradient oscillation and explodes around steep shock front formation at t > 0.45.',
    'ACADEMIC_MATH',
    'Achieve stable convergence with L2 relative error < 1.5e-3 against the analytical Cole-Hopf benchmark solution across the entire spatio-temporal domain.',
    'Must retain fully differentiable continuous architecture without coarse spatial discretization.',
    'HIGH',
    'PROPOSED',
    'sol-math-001',
    'Formulation validated against Cole-Hopf analytical transformation.',
    '2026-10-11 10:00:00',
    '2026-10-11 11:20:00'
);

INSERT INTO investigation_findings (id, case_id, known_facts, assumptions, missing_information, identified_constraints, success_criteria, clarification_questions, clarification_responses, diagnostic_summary, confidence_score, is_demo_output, analyzed_at)
VALUES (
    'find-math-001',
    'case-math-001',
    '["1D Burgers PDE: u_t + u*u_x - (0.01/pi)*u_xx = 0","Loss contains boundary loss, initial condition loss, and PDE residual loss","Equal fixed weighting (1.0, 1.0, 1.0) applied to all 3 loss components","Sharp gradient develops at x=0 for t near 0.5 where convective term u*u_x dominates diffusion"]',
    '["Fixed loss component weighting causes gradient imbalance where boundary loss dominates or PDE residual gradients explode","Uniform spatial collocation point sampling under-samples the shock region near x=0"]',
    '["Collocation point distribution (uniform grid vs Sobol / Latin hypercube sampling)","Learning rate scheduler details (Adam with Cosine Annealing or fixed learning rate)"]',
    '["Continuity requirement prevents switching to purely discrete finite volume/difference solver"]',
    '["Relative L2 norm error ||u_pred - u_exact|| / ||u_exact|| <= 1.5e-3 across [0,1]x[-1,1]","Residual loss monotonically decreases without divergence"]',
    '["Are collocation points dynamically resampled during training or static?","What activation function is used (tanh, Sin/Fourier features, Swish)?"]',
    '["Collocation points are currently static uniform grid.","Using standard tanh activations."]',
    'Gradient conflict and stiffness in the loss landscape. Near the shock front, the convective term induces high-frequency Fourier components that standard tanh multilayer perceptrons struggle to approximate (spectral bias), while static uniform collocation fails to capture the steep localized gradient.',
    0.93,
    true,
    '2026-10-11 10:25:00'
);

INSERT INTO candidate_solutions (id, case_id, solution_index, title, summary, reasoning, supporting_evidence, benefits, tradeoffs, risks, effort_level, confidence_limitations, is_selected, created_at)
VALUES (
    'sol-math-001',
    'case-math-001',
    1,
    'Adaptive Residual-Based Collocation Sampling (RAD) + Learning Rate Annealing',
    'Implement Residual-based Adaptive Distribution (RAD) to dynamically cluster collocation points around high PDE residual areas, paired with soft-adaptive loss balancing (GradNorm).',
    'Shock fronts require higher spatial point density. Rather than uniform sampling, resampling 30% of points proportional to the magnitude of |u_t + u*u_x - nu*u_xx| automatically focuses model capacity on the shock.',
    '["Literature proof: Anagnostopoulos et al. on residual adaptive sampling in stiff PDEs","Cole-Hopf exact solution shows d(u)/dx magnitude peaking 100x higher near x=0 at t=0.5"]',
    '["Dramatically accelerates shock capture","Eliminates manual tuning of loss weights"]',
    '["Slight computational overhead per resampling epoch (~5% longer epoch time)"]',
    '["Risk of over-clustering if residual variance is extreme"]',
    'MEDIUM',
    'Requires analytical gradient verification against Cole-Hopf benchmark.',
    true,
    '2026-10-11 10:45:00'
),
(
    'sol-math-002',
    'case-math-001',
    2,
    'Fourier Feature Embedding Activation Architecture (Random Fourier Projections)',
    'Replace standard input [x, t] coordinates with coordinate embeddings gamma(v) = [cos(2*pi*B*v), sin(2*pi*B*v)] to alleviate spectral bias.',
    'Enables neural networks to learn high-frequency features in low-dimensional domains.',
    '["Tancik et al. Fourier Features Let Networks Learn High Frequency Functions"]',
    '["Directly attacks spectral bias in steep gradients"]',
    '["Scale hyperparameter B must be carefully tuned to prevent spurious high-frequency oscillations"]',
    '["May cause high boundary condition error if scale is too large"]',
    'MEDIUM',
    'Sensitive to Fourier scale hyperparameter selection.',
    false,
    '2026-10-11 10:45:00'
),
(
    'sol-math-003',
    'case-math-001',
    3,
    'Space-Time Domain Decomposition (XPINNs / Sub-domain Splitting)',
    'Split the temporal domain into 3 time slabs [0, 0.3], [0.3, 0.7], [0.7, 1.0] trained sequentially.',
    'Prevents errors in later shock stages from corrupting early smooth flow fields.',
    '["Karniadakis PINN literature on sequential temporal sub-stepping"]',
    '["Very robust convergence across sharp temporal discontinuities"]',
    '["Requires training multiple individual sub-networks and stitching interface conditions"]',
    '["Substantially higher implementation complexity"]',
    'HIGH',
    'Effective but over-engineered if adaptive sampling resolves the shock.',
    false,
    '2026-10-11 10:45:00'
);

INSERT INTO action_items (id, case_id, solution_id, step_number, title, description, is_completed, completed_at, created_at)
VALUES (
    'act-math-001',
    'case-math-001',
    'sol-math-001',
    1,
    'Implement Cole-Hopf Analytical Solution Validator',
    'Code exact reference evaluation function in Python/SymPy to compute benchmark values at 10,000 test coordinates.',
    true,
    '2026-10-11 11:00:00',
    '2026-10-11 10:50:00'
),
(
    'act-math-002',
    'case-math-001',
    'sol-math-001',
    2,
    'Implement Residual-based Adaptive Sampling Routine (RAD)',
    'Every 2,000 epochs, compute PDE residual over 20,000 candidate points and select 2,000 top-residual locations as new collocation set.',
    false,
    null,
    '2026-10-11 10:50:00'
),
(
    'act-math-003',
    'case-math-001',
    'sol-math-001',
    3,
    'Run Deterministic L2 Error Norm Check Against Cole-Hopf',
    'Validate that predicted solution has relative L2 error norm < 1.5e-3.',
    false,
    null,
    '2026-10-11 10:50:00'
);

-- Case 3: Data & Business Analysis
INSERT INTO problem_cases (id, title, description, category, goal, constraints_text, urgency, status, selected_solution_id, user_notes, created_at, updated_at)
VALUES (
    'case-biz-001',
    'B2B SaaS 34% Churn Spike Following Tier-2 Pricing & Usage Quota Restructuring',
    'In Q3, SaaS monthly customer churn rate rose from historical average of 2.1% to 6.8% (34% absolute customer loss in mid-tier segment). Leadership assumed price resistance to the $149 -> $189/mo change, but anecdotal reports mention unexpected overage fees and seat assignment lockouts.',
    'DATA_BUSINESS_ANALYSIS',
    'Determine the precise statistical driver of churn, quantify segment revenue risk, and design targeted mitigation plan to reverse churn to < 2.5% within 60 days.',
    'Cannot revert headline pricing on new signups. Pricing model must maintain minimum gross margin of 78%.',
    'HIGH',
    'IN_PROGRESS',
    'sol-biz-001',
    'Cohort decomposition and survival analysis underway.',
    '2026-10-12 14:00:00',
    '2026-10-13 09:15:00'
);

INSERT INTO investigation_findings (id, case_id, known_facts, assumptions, missing_information, identified_constraints, success_criteria, clarification_questions, clarification_responses, diagnostic_summary, confidence_score, is_demo_output, analyzed_at)
VALUES (
    'find-biz-001',
    'case-biz-001',
    '["Monthly churn spiked from 2.1% to 6.8% specifically in Tier-2 cohort","Tier-2 price increased from $149 to $189/mo (+26.8%)","A new API request cap of 50,000 calls/mo was instituted simultaneously with a $0.005/request overage fee","Gross margin target is 78%"]',
    '["Churn is primarily driven by unexpected overage bills rather than base subscription price increase","Accounts exceeding quota are high-utilization power users with highest LTV potential"]',
    '["Distribution of overage charges among churned accounts vs retained accounts","Customer support ticket volume categorization for billing dispute vs cancellation request"]',
    '["Cannot revert base price to $149 for new accounts","Must preserve 78% gross margin threshold"]',
    '["Identify primary churn correlation factor with Pearson/Spearman r > 0.70","Model net revenue impact of overage grace period vs customer cancellation","Return churn rate below 2.5% within 60 days"]',
    '["What percentage of Tier-2 users exceeded the 50k API cap in their first bill cycle?","Did users receive email warnings before overage charges were billed?"]',
    '["62% of churned accounts had overage fees averaging $112 above base subscription.","No proactive alerts were sent; charges appeared unexpectedly on automated invoices."]',
    'Investigation reveals the core churn catalyst was not price sensitivity to the base fee, but punitive surprise overage invoicing coupled with lack of consumption alerts. 62% of churned accounts were hit with unexpected charges >50% of their base fee without advance warning.',
    0.91,
    true,
    '2026-10-12 14:30:00'
);

INSERT INTO candidate_solutions (id, case_id, solution_index, title, summary, reasoning, supporting_evidence, benefits, tradeoffs, risks, effort_level, confidence_limitations, is_selected, created_at)
VALUES (
    'sol-biz-001',
    'case-biz-001',
    1,
    'Soft Quota with 80%/100% Proactive Alerts + Tiered Overage Buffer Pack',
    'Replace hard overage billing with proactive usage thresholds (email at 80% and 100%), provide a 1-month one-time grace waiver, and offer predictable \$40/50k API add-on packs.',
    'Data proves power users churn when blindsided by unpredictable variable fees. Providing predictability and transparent alerts preserves revenue while honoring customer trust.',
    '["62% churn correlation with unexpected overage fee > $50","CSAT score on billing plummeted from 4.6 to 1.9"]',
    '["Expected to recover 70%+ of at-risk accounts","Restores customer goodwill while preserving gross margin (81.2% modeled)"]',
    '["Short-term \$18k waiver expense in Month 1 for overage adjustments"]',
    '["Some accounts may attempt to exploit grace window"]',
    'LOW',
    'Requires CRM and billing email integration within 7 days.',
    true,
    '2026-10-12 15:00:00'
),
(
    'sol-biz-002',
    'case-biz-001',
    2,
    'Immediate Full Reversal to Legacy $149 Unlimited Plan',
    'Rollback all Tier-2 accounts to old pricing structure.',
    'Removes all friction immediately.',
    '["Historical churn was 2.1% under old model"]',
    '["Instant stabilization of cancellation numbers"]',
    '["Leaves unsustainable API infrastructure cost unrecovered; gross margin drops below required 78% target to 71.4%"]',
    '["Sends signal of indecisiveness to market and investors"]',
    'LOW',
    'Directly violates gross margin business constraint.',
    false,
    '2026-10-12 15:00:00'
),
(
    'sol-biz-003',
    'case-biz-001',
    3,
    'Hard Usage Rate Limiting Without Overage Billing',
    'Cut off API traffic once 50,000 monthly quota is exhausted instead of billing overages.',
    'Ensures no surprise bills appear on invoices.',
    '["Customers complained about billing surprise"]',
    '["Zero billing surprises or invoice disputes"]',
    '["Hard traffic cutoff will break production customer integrations, causing catastrophic emergency churn and SLA breaches"]',
    '["Severe customer dissatisfaction and contract cancellations"]',
    'LOW',
    'Unacceptable for mission-critical B2B integrations.',
    false,
    '2026-10-12 15:00:00'
);

INSERT INTO action_items (id, case_id, solution_id, step_number, title, description, is_completed, completed_at, created_at)
VALUES (
    'act-biz-001',
    'case-biz-001',
    'sol-biz-001',
    1,
    'Reconcile Cohort Unit Economics & Overage Revenue Sensitivity Model',
    'Calculate revenue impact using the SaaS Unit Economics Calculator: base churn rate, CAC payback, and net margin impact.',
    true,
    '2026-10-12 16:30:00',
    '2026-10-12 15:15:00'
),
(
    'act-biz-002',
    'case-biz-001',
    'sol-biz-001',
    2,
    'Deploy Automated In-App and Email 80% & 100% Quota Threshold Alerts',
    'Configure SendGrid billing webhook to trigger alert notifications when customer reaches 40,000 and 50,000 requests.',
    true,
    '2026-10-13 09:00:00',
    '2026-10-12 15:15:00'
),
(
    'act-biz-003',
    'case-biz-001',
    'sol-biz-001',
    3,
    'Execute CS Outreach Campaign with One-Click Grace Waiver for Churned Accounts',
    'Reach out to 84 recently cancelled Tier-2 accounts offering retroactive overage credit and migration to predictable add-on pack.',
    false,
    null,
    '2026-10-12 15:15:00'
);
