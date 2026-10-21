package com.sentinel.service.ai;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sentinel.domain.enums.ProblemCategory;
import com.sentinel.domain.enums.VerificationType;
import com.sentinel.domain.model.ActionItem;
import com.sentinel.domain.model.CandidateSolution;
import com.sentinel.domain.model.EvidenceItem;
import com.sentinel.domain.model.InvestigationFinding;
import com.sentinel.domain.model.ProblemCase;
import com.sentinel.dto.ClarificationAnswerRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service("demoAiService")
public class DemoAiService implements AiInvestigationService {

    private static final Logger log = LoggerFactory.getLogger(DemoAiService.class);
    private final ObjectMapper objectMapper;

    public DemoAiService(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public String getProviderName() {
        return "Deterministic Demo Engine (Offline)";
    }

    @Override
    public boolean isDemoMode() {
        return true;
    }

    @Override
    public InvestigationAnalysisResult analyzeProblem(ProblemCase problem, List<EvidenceItem> evidence) {
        log.info("Executing Demo Mode investigation analysis for case: {} [{}]", problem.getId(), problem.getCategory());

        List<String> knownFacts = new ArrayList<>();
        List<String> assumptions = new ArrayList<>();
        List<String> missingInfo = new ArrayList<>();
        List<String> constraints = new ArrayList<>();
        List<String> successCriteria = new ArrayList<>();
        List<String> clarificationQuestions = new ArrayList<>();
        String diagnosticSummary = "Investigation underway.";
        double confidence = 0.85;

        String desc = problem.getDescription() != null ? problem.getDescription().toLowerCase() : "";
        String title = problem.getTitle() != null ? problem.getTitle().toLowerCase() : "";

        // Extract any attached evidence summaries as facts
        if (evidence != null && !evidence.isEmpty()) {
            for (EvidenceItem item : evidence) {
                knownFacts.add("Evidence attached: " + item.getOriginalFilename() + " (" + (item.getFileSize() / 1024) + " KB, " + item.getMimeType() + ")");
            }
        }

        if (problem.getConstraintsText() != null && !problem.getConstraintsText().isBlank()) {
            constraints.add("User Constraint: " + problem.getConstraintsText().trim());
        }

        switch (problem.getCategory()) {
            case SOFTWARE_DEBUGGING -> {
                knownFacts.add("Reported incident: " + problem.getTitle());
                if (desc.contains("connection") || desc.contains("hikaricp") || desc.contains("timeout") || desc.contains("pool")) {
                    knownFacts.add("Database connection pool indicates exhaustion under concurrent query/request load");
                    knownFacts.add("Service threads blocked in connection acquisition wait state");
                    assumptions.add("Underlying database server CPU/disk IO is not fully saturated; exhaustion is caused by connection hold duration inflation");
                    assumptions.add("Transactions may be wrapping external network calls or long-running computations");
                    missingInfo.add("Exact breakdown of query execution duration vs network IO held within the transaction boundary");
                    missingInfo.add("HikariCP leak detection threshold logs and thread dump stack traces");
                    constraints.add("Zero tolerance for data inconsistency or broken transactional guarantees");
                    constraints.add("Cannot horizontally scale DB tier beyond budget limits");
                    successCriteria.add("HikariCP p99 connection wait time < 5ms under peak traffic");
                    successCriteria.add("Zero 504 Gateway Timeout or ConnectionTimeoutException errors in 15-minute synthetic load run");
                    clarificationQuestions.add("Are external REST/payment/gRPC calls executed synchronously inside the @Transactional boundary?");
                    clarificationQuestions.add("Are read-only queries segregated to read replicas or sharing the primary write pool?");
                    diagnosticSummary = "Connection starvation pattern: Database connection pool exhaustion caused by holding open active relational connections across external network boundaries or unindexed slow queries.";
                    confidence = 0.94;
                } else if (desc.contains("memory") || desc.contains("heap") || desc.contains("oom")) {
                    knownFacts.add("Memory pressure leading to GC pauses or OutOfMemoryError exceptions");
                    assumptions.add("Object accumulation in retained heap without timely dereferencing (static collection, unclosed streams, or threadlocal leak)");
                    missingInfo.add("HPROF memory heap dump histogram and top retaining GC root references");
                    successCriteria.add("Stable heap utilization with memory plateau post-GC under continuous workload");
                    clarificationQuestions.add("Does memory usage steadily increase over time without returning to baseline (linear leak)?");
                    clarificationQuestions.add("Are cached objects configured with strict TTL and maximum size limits?");
                    diagnosticSummary = "Memory retention bottleneck: Heap memory growth suggests unevicted cache entries or leaked thread context references.";
                    confidence = 0.90;
                } else {
                    knownFacts.add("System defect observed during normal runtime execution");
                    assumptions.add("Mismatch between caller expectations and component state or configuration");
                    missingInfo.add("Complete stack trace with line numbers, runtime configuration, and environment differences");
                    successCriteria.add("Reproduction test passes and error condition no longer occurs under regression suite");
                    clarificationQuestions.add("Can this issue be reproduced in a local or isolated staging environment?");
                    clarificationQuestions.add("What recent deployments or configuration changes preceded the failure?");
                    diagnosticSummary = "Software runtime anomaly requiring isolated reproduction and environmental parameter verification.";
                    confidence = 0.85;
                }
            }
            case ACADEMIC_MATH -> {
                knownFacts.add("Mathematical formulation: " + problem.getTitle());
                knownFacts.add("Target objective: " + problem.getGoal());
                if (desc.contains("pinn") || desc.contains("burgers") || desc.contains("loss") || desc.contains("gradient")) {
                    knownFacts.add("Physics-Informed Neural Network exhibits training loss instability or gradient explosion");
                    knownFacts.add("Loss function comprises multiple competing terms (boundary, initial condition, PDE residual)");
                    assumptions.add("Stiffness and spectral bias in standard neural networks prevent capturing steep spatial derivatives");
                    assumptions.add("Uniform spatial sampling fails to represent localized gradient extremes (e.g. shock fronts)");
                    missingInfo.add("Exact weighting schedule for residual vs boundary loss components (dynamic GradNorm vs static)");
                    missingInfo.add("Collocation point density and spatial coordinate distribution strategy");
                    constraints.add("Must maintain continuous differentiable formulation without falling back to purely discrete grids");
                    successCriteria.add("Relative L2 norm error ||u_pred - u_exact|| / ||u_exact|| <= 1.5e-3 across entire domain");
                    successCriteria.add("Stable monotonic residual decrease without catastrophic gradient explosion");
                    clarificationQuestions.add("Are collocation points dynamically resampled during training or static across all epochs?");
                    clarificationQuestions.add("What activation function is currently used in the architecture (tanh, Sin/Fourier features, Swish)?");
                    diagnosticSummary = "Gradient imbalance and spectral bias: Multi-term loss landscape creates severe gradient disparity between high-frequency shock regions and smooth boundary regions.";
                    confidence = 0.92;
                } else {
                    knownFacts.add("Analytical or computational mathematical problem statement");
                    assumptions.add("Standard numerical or closed-form approach faces convergence or constraint satisfaction barriers");
                    missingInfo.add("Boundary conditions, domain intervals, and analytical ground truth comparisons");
                    successCriteria.add("Analytical proof or numerical error bounded within target tolerance epsilon");
                    clarificationQuestions.add("Is an exact analytical benchmark available, or must verification rely on bounded numerical error bounds?");
                    clarificationQuestions.add("What precision requirements (e.g. double precision IEEE 754) apply to intermediate steps?");
                    diagnosticSummary = "Mathematical analysis: Requires structural decomposition of constraints and verification against analytical boundary checks.";
                    confidence = 0.88;
                }
            }
            case DATA_BUSINESS_ANALYSIS -> {
                knownFacts.add("Business metric observation: " + problem.getTitle());
                knownFacts.add("Target business goal: " + problem.getGoal());
                if (desc.contains("churn") || desc.contains("pricing") || desc.contains("quota") || desc.contains("revenue")) {
                    knownFacts.add("Significant variance in customer retention following commercial or policy restructuring");
                    assumptions.add("Primary churn catalyst may be unexpected billing surprises or friction rather than headline price sensitivity alone");
                    assumptions.add("At-risk accounts represent high-utilization power accounts with substantial lifetime value");
                    missingInfo.add("Distribution of variable/overage charges among churned versus retained customer segments");
                    missingInfo.add("Categorization of customer support inquiries leading up to cancellation notices");
                    constraints.add("Pricing model must maintain minimum gross margin threshold (e.g. >= 78%)");
                    successCriteria.add("Statistically significant churn correlation factor identified (correlation r > 0.70)");
                    successCriteria.add("Projected cohort retention stabilized below historical benchmark within 60 days");
                    clarificationQuestions.add("Did customers receive proactive consumption warning alerts prior to invoice generation?");
                    clarificationQuestions.add("What proportion of churned accounts were hit with variable charges exceeding 30% of their base subscription?");
                    diagnosticSummary = "Churn driver analysis: Strong probability of pricing and billing friction mismatch between product utilization and unexpected invoice overages.";
                    confidence = 0.91;
                } else {
                    knownFacts.add("Business telemetry and metric variance reported");
                    assumptions.add("Observed anomaly is driven by segment-specific behavior or external market shift");
                    missingInfo.add("Historical cohort baseline and data collection methodology for the metric");
                    successCriteria.add("Root cause hypothesis confirmed with statistical significance (p < 0.05)");
                    clarificationQuestions.add("Has this metric been adjusted for seasonal fluctuations or cohort maturation effects?");
                    clarificationQuestions.add("Were data collection pipelines or tracking definitions modified during this timeframe?");
                    diagnosticSummary = "Business metric investigation: Disaggregating aggregate statistics into cohort segments to isolate behavioral drivers.";
                    confidence = 0.87;
                }
            }
            case GENERAL -> {
                knownFacts.add("General problem query submitted: " + problem.getTitle());
                knownFacts.add("Stated objective: " + problem.getGoal());
                assumptions.add("Problem definition requires further structural decomposition into specific measurable parameters");
                missingInfo.add("Exact operational context, platform dependencies, and empirical baseline data");
                constraints.add("Honest boundary: Generalized inquiry requires user clarification before formal resolution claims");
                successCriteria.add("Clarify specific domain constraints and establish measurable verification test procedure");
                clarificationQuestions.add("Can you provide the specific environment, input dataset, or sequence of steps that triggered this issue?");
                clarificationQuestions.add("What quantifiable metric or verifiable result constitutes definitive success for this case?");
                diagnosticSummary = "General investigation inquiry: Structured analysis initiated. To produce high-confidence candidate solutions, please answer the targeted clarification questions.";
                confidence = 0.75;
            }
        }

        return new InvestigationAnalysisResult(
                knownFacts,
                assumptions,
                missingInfo,
                constraints,
                successCriteria,
                clarificationQuestions,
                diagnosticSummary,
                confidence,
                true // Clearly labeled as Demo Mode output
        );
    }

    @Override
    public List<CandidateSolution> generateSolutions(ProblemCase problem, InvestigationFinding finding, List<EvidenceItem> evidence) {
        log.info("Generating candidate solutions in Demo Mode for case: {}", problem.getId());
        List<CandidateSolution> solutions = new ArrayList<>();

        switch (problem.getCategory()) {
            case SOFTWARE_DEBUGGING -> {
                CandidateSolution s1 = new CandidateSolution(
                        problem,
                        1,
                        "Decompose Transactional Boundary: Out-of-Transaction Network Orchestration",
                        "Refactor the service layer to isolate database write transactions into brief operations before and after external network calls, releasing connection pool handles while awaiting HTTP/RPC responses.",
                        "Relational database connections should never be held idle across network round-trips. Segregating operations into distinct atomic transactions eliminates connection hold-time inflation by over 90%.",
                        "MEDIUM",
                        "Assumes external operations support idempotent keys or compensating rollback actions."
                );
                s1.setBenefits(toJsonList(List.of(
                        "Eliminates connection pool starvation without costly hardware scale-ups",
                        "Maintains sub-5ms pool wait latency even during sudden 10x traffic bursts",
                        "Preserves strict ACID guarantees on actual database modifications"
                )));
                s1.setTradeoffs(toJsonList(List.of(
                        "Requires implementing compensating actions if the external operation fails after phase one commit",
                        "Adds slight state orchestration complexity"
                )));
                s1.setRisks(toJsonList(List.of(
                        "Edge case state inconsistency if service pod crashes precisely between transaction phases"
                )));
                s1.setSupportingEvidence(toJsonList(List.of(
                        "HikariCP connection pool metrics showing 95% of transaction duration spent idle in socketRead0",
                        "Network latency logs showing external roundtrip of 350ms-800ms"
                )));
                s1.setIsSelected(true);
                solutions.add(s1);

                CandidateSolution s2 = new CandidateSolution(
                        problem,
                        2,
                        "Deploy Connection Multiplexing Proxy (PgBouncer / RDS Proxy)",
                        "Introduce an intermediate connection pooler between application replicas and PostgreSQL instance using transaction-level pooling.",
                        "Buffers incoming client connection spikes and prevents PostgreSQL process explosion.",
                        "HIGH",
                        "Transaction-level pooling cannot release server connection during active multi-statement transactions."
                );
                s2.setBenefits(toJsonList(List.of(
                        "Shields database engine from thousands of client connections",
                        "Provides graceful connection queueing during spikes"
                )));
                s2.setTradeoffs(toJsonList(List.of(
                        "Does not resolve transaction hold-time inflation if long transactions remain in code",
                        "Additional architectural component requiring monitoring and maintenance"
                )));
                s2.setRisks(toJsonList(List.of(
                        "Session-state features (prepared statements, temporary tables) require careful proxy mode configuration"
                )));
                s2.setSupportingEvidence(toJsonList(List.of(
                        "PostgreSQL max_connections ceiling documentation"
                )));
                solutions.add(s2);

                CandidateSolution s3 = new CandidateSolution(
                        problem,
                        3,
                        "Increase HikariCP Maximum Pool Size to 150 per Pod",
                        "Triple maximum pool size from 50 to 150 connections across all application pods.",
                        "Simple configuration change that temporarily increases concurrent connection headroom.",
                        "LOW",
                        "Dangerous anti-pattern: directly multiplies backend process contention on PostgreSQL server."
                );
                s3.setBenefits(toJsonList(List.of(
                        "Requires zero code refactoring",
                        "Immediate configuration deployment"
                )));
                s3.setTradeoffs(toJsonList(List.of(
                        "Causes CPU thrashing and context-switch overhead on database host",
                        "Merely delays pool exhaustion rather than fixing hold-time root cause"
                )));
                s3.setRisks(toJsonList(List.of(
                        "Severe risk of cascading database outage due to connection limit exhaustion and memory pressure"
                )));
                s3.setSupportingEvidence(toJsonList(List.of(
                        "Current replica count (10) * 150 = 1500 concurrent connections"
                )));
                solutions.add(s3);
            }
            case ACADEMIC_MATH -> {
                CandidateSolution s1 = new CandidateSolution(
                        problem,
                        1,
                        "Residual-based Adaptive Distribution (RAD) Collocation Sampling",
                        "Dynamically resample collocation points proportional to local PDE residual magnitude ||u_t + u*u_x - nu*u_xx|| every N training epochs.",
                        "Concentrating model training density specifically where residual variance is highest resolves steep shock gradients without brute-force global grid refinement.",
                        "MEDIUM",
                        "Requires calibration of resampling frequency to avoid over-clustering on localized noise."
                );
                s1.setBenefits(toJsonList(List.of(
                        "Dramatically improves convergence in high-gradient boundary layers",
                        "Eliminates manual spatial grid heuristic guessing",
                        "Validated in scientific machine learning literature (Anagnostopoulos et al.)"
                )));
                s1.setTradeoffs(toJsonList(List.of(
                        "Adds approximately 5-8% computational overhead during point resampling epochs"
                )));
                s1.setRisks(toJsonList(List.of(
                        "Potential instability if residual estimates have extreme localized variance"
                )));
                s1.setSupportingEvidence(toJsonList(List.of(
                        "Analytical Cole-Hopf benchmark showing steep spatial derivative d(u)/dx at t=0.5"
                )));
                s1.setIsSelected(true);
                solutions.add(s1);

                CandidateSolution s2 = new CandidateSolution(
                        problem,
                        2,
                        "Random Fourier Feature Coordinate Embeddings (Addressing Spectral Bias)",
                        "Transform raw spatial-temporal coordinates [x, t] through random Fourier feature projection layers before feeding into deep layers.",
                        "Standard MLPs exhibit spectral bias, learning low-frequency components rapidly while struggling with high frequencies.",
                        "MEDIUM",
                        "Highly sensitive to the standard deviation hyperparameter B of the Fourier projection matrix."
                );
                s2.setBenefits(toJsonList(List.of(
                        "Enables deep neural networks to accurately learn high-frequency shock dynamics"
                )));
                s2.setTradeoffs(toJsonList(List.of(
                        "If Fourier scale B is too large, introduces high-frequency ringing artifacts near boundaries"
                )));
                s2.setRisks(toJsonList(List.of(
                        "Overfitting on non-physical oscillations"
                )));
                s2.setSupportingEvidence(toJsonList(List.of(
                        "Fourier Features Let Networks Learn High Frequency Functions (Tancik et al.)"
                )));
                solutions.add(s2);

                CandidateSolution s3 = new CandidateSolution(
                        problem,
                        3,
                        "Space-Time Sequential Domain Decomposition (XPINNs)",
                        "Partition the time domain [0, T] into sequential sub-intervals trained in a time-marching sequence.",
                        "Errors from downstream shock development cannot corrupt upstream early flow fields.",
                        "HIGH",
                        "Substantial implementation complexity for interface boundary condition exchange."
                );
                s3.setBenefits(toJsonList(List.of(
                        "Extremely stable numerical convergence across sharp temporal discontinuities"
                )));
                s3.setTradeoffs(toJsonList(List.of(
                        "Requires managing multiple network weights and coordinating continuity across interfaces"
                )));
                s3.setRisks(toJsonList(List.of(
                        "Increased architectural overhead compared to adaptive sampling"
                )));
                s3.setSupportingEvidence(toJsonList(List.of(
                        "XPINN domain decomposition papers (Karniadakis group)"
                )));
                solutions.add(s3);
            }
            case DATA_BUSINESS_ANALYSIS -> {
                CandidateSolution s1 = new CandidateSolution(
                        problem,
                        1,
                        "Proactive 80%/100% Usage Quota Alerts + Predictable Add-On Buffer Packs",
                        "Replace abrupt post-facto invoice penalties with automated email alerts at 80% and 100% quota consumption, a 30-day grace waiver, and pre-purchased discounted expansion packs.",
                        "Power users churn when blindsided by unexpected invoice fees. Proactive transparency preserves customer trust while maintaining healthy unit economics.",
                        "LOW",
                        "Requires SendGrid/Stripe billing webhook integration within 7 days."
                );
                s1.setBenefits(toJsonList(List.of(
                        "Recovers estimated 70%+ of at-risk power accounts",
                        "Protects target gross margin (modeled at 81.2% > 78% constraint)",
                        "Turns churn risk into an upsell path for dedicated enterprise tiers"
                )));
                s1.setTradeoffs(toJsonList(List.of(
                        "One-time $18k goodwill invoice credit adjustment in first month"
                )));
                s1.setRisks(toJsonList(List.of(
                        "Small fraction of accounts may attempt to exploit the grace period"
                )));
                s1.setSupportingEvidence(toJsonList(List.of(
                        "62% churn correlation with surprise overage fees > $50",
                        "Customer cancellation exit survey comments"
                )));
                s1.setIsSelected(true);
                solutions.add(s1);

                CandidateSolution s2 = new CandidateSolution(
                        problem,
                        2,
                        "Complete Rollback to Legacy $149 Unlimited Quota Structure",
                        "Immediately revert all Tier-2 accounts to old pricing and eliminate the usage quota entirely.",
                        "Eliminates all billing friction instantly.",
                        "LOW",
                        "Violates core business constraint: gross margin collapses below required 78% floor."
                );
                s2.setBenefits(toJsonList(List.of(
                        "Instant stabilization of cancellations"
                )));
                s2.setTradeoffs(toJsonList(List.of(
                        "Leaves infrastructure costs for high-volume users unrecovered",
                        "Gross margin drops to 71.4%, violating business constraint"
                )));
                s2.setRisks(toJsonList(List.of(
                        "Signals market and investor instability regarding commercial model"
                )));
                s2.setSupportingEvidence(toJsonList(List.of(
                        "Historical 2.1% churn rate under legacy plan"
                )));
                solutions.add(s2);

                CandidateSolution s3 = new CandidateSolution(
                        problem,
                        3,
                        "Hard Traffic Rate Limiting Without Overage Billing",
                        "Throttle or block API requests once monthly quota of 50,000 calls is reached, without billing extra fees.",
                        "Guarantees that invoices will never exceed base subscription fee.",
                        "LOW",
                        "Severe operational impact: breaking customer production integrations causes contract termination."
                );
                s3.setBenefits(toJsonList(List.of(
                        "Eliminates billing surprises with 100% certainty"
                )));
                s3.setTradeoffs(toJsonList(List.of(
                        "Causes catastrophic outages for customers using the API in critical production flows"
                )));
                s3.setRisks(toJsonList(List.of(
                        "High likelihood of immediate enterprise SLA dispute and total account cancellation"
                )));
                s3.setSupportingEvidence(toJsonList(List.of(
                        "Customer SLA terms regarding API availability"
                )));
                solutions.add(s3);
            }
            case GENERAL -> {
                CandidateSolution s1 = new CandidateSolution(
                        problem,
                        1,
                        "Diagnostic Isolation & Controlled Reproduction Protocol",
                        "Construct a minimal reproducible example (MRE) in an isolated sandbox to separate internal logic from environmental factors.",
                        "General systems issues must first be contained before attempting corrective interventions.",
                        "LOW",
                        "Requires specific test input or environment details from user clarification."
                );
                s1.setBenefits(toJsonList(List.of("Identifies root cause with high certainty", "Prevents premature changes")));
                s1.setTradeoffs(toJsonList(List.of("Requires initial setup time for isolated sandbox")));
                s1.setRisks(toJsonList(List.of("May take longer than trial-and-error guessing")));
                s1.setIsSelected(true);
                solutions.add(s1);

                CandidateSolution s2 = new CandidateSolution(
                        problem,
                        2,
                        "Targeted Incremental Parameter Verification",
                        "Vary input variables one at a time while measuring output response to locate sensitivity threshold.",
                        "Effective for multi-variable uncertainty.",
                        "MEDIUM",
                        "Assumes output metrics can be measured deterministically."
                );
                s2.setBenefits(toJsonList(List.of("Exposes hidden non-linear dependencies")));
                s2.setTradeoffs(toJsonList(List.of("Requires multiple measurement runs")));
                s2.setRisks(toJsonList(List.of("Potential interaction effects between variables")));
                solutions.add(s2);
            }
        }

        return solutions;
    }

    @Override
    public List<ActionItem> generateActionPlan(ProblemCase problem, CandidateSolution solution) {
        log.info("Generating action plan in Demo Mode for case: {} and solution: {}", problem.getId(), solution.getTitle());
        List<ActionItem> items = new ArrayList<>();

        if (problem.getCategory() == ProblemCategory.SOFTWARE_DEBUGGING) {
            items.add(new ActionItem(problem, solution, 1, "Audit @Transactional Scopes in Workflow", "Examine codebase to locate external network calls, payment APIs, or file IO executed inside active database transaction boundaries."));
            items.add(new ActionItem(problem, solution, 2, "Implement Two-Phase Execution with Compensation", "Phase 1: Reserve state in brief DB transaction. Phase 2: Invoke external network API. Phase 3: Update final state in second brief DB transaction."));
            items.add(new ActionItem(problem, solution, 3, "Configure HikariCP Connection Leak Detection", "Set leakDetectionThreshold=5000ms and monitor active/idle pool gauges via metrics endpoint."));
            items.add(new ActionItem(problem, solution, 4, "Execute Synthetic Load Test at Target Concurrency", "Run load test simulating 5,000 requests/sec with simulated external latency to verify pool availability."));
        } else if (problem.getCategory() == ProblemCategory.ACADEMIC_MATH) {
            items.add(new ActionItem(problem, solution, 1, "Implement Cole-Hopf Analytical Reference Benchmark", "Set up analytical verification function to evaluate exact ground truth across 10,000 domain coordinates."));
            items.add(new ActionItem(problem, solution, 2, "Integrate Residual-based Adaptive Sampling Routine", "Implement dynamic resampling hook to query PDE residual and reallocate collocation points every 2,000 epochs."));
            items.add(new ActionItem(problem, solution, 3, "Compute Relative L2 Error Norm Check", "Run deterministic verification script comparing PINN predictions against Cole-Hopf exact solution."));
        } else if (problem.getCategory() == ProblemCategory.DATA_BUSINESS_ANALYSIS) {
            items.add(new ActionItem(problem, solution, 1, "Reconcile Cohort Unit Economics & Margin Sensitivity", "Run SaaS Unit Economics Calculator check with CAC, churn rate, and grace waiver to verify >= 78% margin."));
            items.add(new ActionItem(problem, solution, 2, "Deploy Automated 80% & 100% In-App and Email Alerts", "Configure billing notification triggers to warn users before unexpected overage costs accumulate."));
            items.add(new ActionItem(problem, solution, 3, "Launch Proactive Grace Waiver Campaign for Churned Accounts", "Reach out to cancelled accounts with one-click credit waiver and migration to predictable expansion pack."));
        } else {
            items.add(new ActionItem(problem, solution, 1, "Isolate Minimal Reproducible Environment", "Configure clean sandbox environment with known baseline parameters."));
            items.add(new ActionItem(problem, solution, 2, "Execute Controlled Test Sequence", "Run reproducible test steps and log all intermediate states."));
            items.add(new ActionItem(problem, solution, 3, "Record Verification Results and Evidence", "Attach test evidence and determine if expected outcome was achieved."));
        }

        return items;
    }

    @Override
    public VerificationPlanResult generateVerificationPlan(ProblemCase problem, CandidateSolution solution) {
        log.info("Generating verification plan in Demo Mode for case: {}", problem.getId());

        return switch (problem.getCategory()) {
            case SOFTWARE_DEBUGGING -> new VerificationPlanResult(
                    "Simulate 5,000 RPS peak checkout traffic with 600ms simulated payment latency for 15 minutes. Check HikariCP connection pool acquisition latency and HTTP response error rate.",
                    "HikariCP connection acquisition wait time p99 < 5ms. Zero ConnectionTimeoutException or 504 errors across 1,000,000+ requests.",
                    VerificationType.MANUAL,
                    "Synthetic load testing under simulated latency directly stresses connection pool retention and validates decoupling."
            );
            case ACADEMIC_MATH -> new VerificationPlanResult(
                    "Evaluate relative L2 error norm ||u_pred - u_exact|| / ||u_exact|| across 10,000 spatial-temporal points against analytical Cole-Hopf benchmark.",
                    "Relative L2 norm error <= 1.5e-3 across domain [-1, 1] x [0, 1] with zero gradient explosion.",
                    VerificationType.DETERMINISTIC_CALCULATOR,
                    "Analytical ground truth provides exact mathematical verification independent of subjective model impressions."
            );
            case DATA_BUSINESS_ANALYSIS -> new VerificationPlanResult(
                    "Execute SaaS Unit Economics sensitivity model on cohort metrics: calculate Gross Margin %, CAC payback period, and 60-day projected churn rate post-alert rollout.",
                    "Gross Margin >= 78.0%, CAC Payback < 12 months, and projected churn rate reverses below 2.5% per month.",
                    VerificationType.DETERMINISTIC_CALCULATOR,
                    "Deterministic economic model verifies business constraint compliance before launching billing changes."
            );
            case GENERAL -> new VerificationPlanResult(
                    "Execute defined reproducible test procedure in sandbox, record exact actual output, and compare against specified success criteria.",
                    "Actual result matches expected criteria within specified tolerances with supporting log/data evidence.",
                    VerificationType.MANUAL,
                    "General domain requires empirical verification to establish validity."
            );
        };
    }

    @Override
    public ClarificationEvaluationResult evaluateClarification(ProblemCase problem, InvestigationFinding currentFinding, List<ClarificationAnswerRequest.ClarificationEntry> answers) {
        log.info("Evaluating clarification responses in Demo Mode for case: {}", problem.getId());

        List<String> newKnownFacts = new ArrayList<>();
        List<String> updatedAssumptions = new ArrayList<>();
        List<String> remainingUnknowns = new ArrayList<>();

        for (ClarificationAnswerRequest.ClarificationEntry entry : answers) {
            if (entry.getAnswer() != null && !entry.getAnswer().isBlank()) {
                newKnownFacts.add("Confirmed via Clarification: " + entry.getQuestion() + " -> " + entry.getAnswer());
            } else {
                remainingUnknowns.add("Unanswered: " + entry.getQuestion());
            }
        }

        updatedAssumptions.add("Clarified constraints incorporated into diagnostic hypotheses");
        String updatedSummary = currentFinding.getDiagnosticSummary() + " [Refined based on " + newKnownFacts.size() + " user clarification responses]";
        double updatedConfidence = Math.min(0.98, (currentFinding.getConfidenceScore() != null ? currentFinding.getConfidenceScore() : 0.80) + 0.05);

        return new ClarificationEvaluationResult(
                newKnownFacts,
                updatedAssumptions,
                remainingUnknowns,
                updatedSummary,
                updatedConfidence
        );
    }

    private String toJsonList(List<String> items) {
        try {
            return objectMapper.writeValueAsString(items);
        } catch (JsonProcessingException e) {
            return "[]";
        }
    }
}
