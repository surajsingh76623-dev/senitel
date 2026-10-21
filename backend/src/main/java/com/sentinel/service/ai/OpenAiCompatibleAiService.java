package com.sentinel.service.ai;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sentinel.config.AiProperties;
import com.sentinel.domain.enums.VerificationType;
import com.sentinel.domain.model.ActionItem;
import com.sentinel.domain.model.CandidateSolution;
import com.sentinel.domain.model.EvidenceItem;
import com.sentinel.domain.model.InvestigationFinding;
import com.sentinel.domain.model.ProblemCase;
import com.sentinel.dto.ClarificationAnswerRequest;
import com.sentinel.exception.AiServiceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service("openAiCompatibleAiService")
@Primary
public class OpenAiCompatibleAiService implements AiInvestigationService {

    private static final Logger log = LoggerFactory.getLogger(OpenAiCompatibleAiService.class);

    private final AiProperties aiProperties;
    private final DemoAiService demoAiService;
    private final ObjectMapper objectMapper;
    private final HttpClient httpClient;

    public OpenAiCompatibleAiService(AiProperties aiProperties, DemoAiService demoAiService, ObjectMapper objectMapper) {
        this.aiProperties = aiProperties;
        this.demoAiService = demoAiService;
        this.objectMapper = objectMapper;
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(15))
                .build();
    }

    @Override
    public String getProviderName() {
        if (!aiProperties.hasApiKey()) {
            return "Demo Mode (No API key configured - using deterministic engine)";
        }
        return "Live LLM Provider: " + aiProperties.getProvider() + " (" + aiProperties.getModel() + ")";
    }

    @Override
    public boolean isDemoMode() {
        return !aiProperties.hasApiKey() || "demo".equalsIgnoreCase(aiProperties.getProvider());
    }

    @Override
    public InvestigationAnalysisResult analyzeProblem(ProblemCase problem, List<EvidenceItem> evidence) {
        if (isDemoMode()) {
            return demoAiService.analyzeProblem(problem, evidence);
        }

        try {
            String systemPrompt = """
                You are SENTINEL, an advanced AI problem investigation platform.
                Analyze the submitted problem and separate:
                1. knownFacts (unambiguously established facts from the description and evidence)
                2. assumptions (working hypotheses that need verification)
                3. missingInformation (critical unknowns and diagnostic gaps)
                4. identifiedConstraints (hard boundaries, budget, hardware, compliance limits)
                5. successCriteria (measurable conditions defining complete resolution)
                6. clarificationQuestions (targeted questions to clarify unknowns)
                7. diagnosticSummary (concise technical root cause or problem diagnosis)
                8. confidenceScore (float from 0.0 to 1.0 representing diagnostic certainty)
                
                Respond ONLY with a valid JSON object matching this schema:
                {
                  "knownFacts": ["string"],
                  "assumptions": ["string"],
                  "missingInformation": ["string"],
                  "identifiedConstraints": ["string"],
                  "successCriteria": ["string"],
                  "clarificationQuestions": ["string"],
                  "diagnosticSummary": "string",
                  "confidenceScore": 0.85
                }
                Never fabricate citations or invent test results. If the problem is unsupported or vague, request clarification.
                """;

            StringBuilder userPrompt = new StringBuilder();
            userPrompt.append("Case Title: ").append(problem.getTitle()).append("\n");
            userPrompt.append("Category: ").append(problem.getCategory().getDisplayName()).append("\n");
            userPrompt.append("Goal: ").append(problem.getGoal()).append("\n");
            userPrompt.append("Urgency: ").append(problem.getUrgency()).append("\n");
            userPrompt.append("Description:\n").append(problem.getDescription()).append("\n");
            if (problem.getConstraintsText() != null && !problem.getConstraintsText().isBlank()) {
                userPrompt.append("Constraints:\n").append(problem.getConstraintsText()).append("\n");
            }
            if (evidence != null && !evidence.isEmpty()) {
                userPrompt.append("Attached Evidence Files:\n");
                for (EvidenceItem item : evidence) {
                    userPrompt.append("- ").append(item.getOriginalFilename())
                              .append(" (").append(item.getMimeType()).append(", ")
                              .append(item.getFileSize()).append(" bytes)\n");
                }
            }

            String jsonResponse = callLlm(systemPrompt, userPrompt.toString());
            JsonNode root = objectMapper.readTree(cleanJsonMarkdown(jsonResponse));

            List<String> knownFacts = jsonToList(root.get("knownFacts"));
            List<String> assumptions = jsonToList(root.get("assumptions"));
            List<String> missingInfo = jsonToList(root.get("missingInformation"));
            List<String> constraints = jsonToList(root.get("identifiedConstraints"));
            List<String> successCriteria = jsonToList(root.get("successCriteria"));
            List<String> clarificationQuestions = jsonToList(root.get("clarificationQuestions"));
            String diagnosticSummary = root.has("diagnosticSummary") ? root.get("diagnosticSummary").asText() : "Analysis completed.";
            double confidenceScore = root.has("confidenceScore") ? root.get("confidenceScore").asDouble(0.85) : 0.85;

            return new InvestigationAnalysisResult(
                    knownFacts,
                    assumptions,
                    missingInfo,
                    constraints,
                    successCriteria,
                    clarificationQuestions,
                    diagnosticSummary,
                    confidenceScore,
                    false
            );
        } catch (Exception e) {
            log.error("Live AI investigation call failed, falling back to deterministic Demo engine: {}", e.getMessage());
            return demoAiService.analyzeProblem(problem, evidence);
        }
    }

    @Override
    public List<CandidateSolution> generateSolutions(ProblemCase problem, InvestigationFinding finding, List<EvidenceItem> evidence) {
        if (isDemoMode()) {
            return demoAiService.generateSolutions(problem, finding, evidence);
        }

        try {
            String systemPrompt = """
                You are SENTINEL. Propose up to 3 candidate solutions for the investigated problem case.
                For each solution, provide:
                1. solutionIndex (1, 2, or 3)
                2. title
                3. summary
                4. reasoning
                5. supportingEvidence (array of strings citing observations or telemetry)
                6. benefits (array of strings)
                7. tradeoffs (array of strings)
                8. risks (array of strings)
                9. effortLevel ("LOW", "MEDIUM", or "HIGH")
                10. confidenceLimitations (honestly describe what could invalidate this solution)
                11. isSelected (boolean, designate the best primary solution as true, others false)
                
                Respond ONLY with a valid JSON array of solution objects:
                [
                  {
                    "solutionIndex": 1,
                    "title": "...",
                    "summary": "...",
                    "reasoning": "...",
                    "supportingEvidence": ["..."],
                    "benefits": ["..."],
                    "tradeoffs": ["..."],
                    "risks": ["..."],
                    "effortLevel": "MEDIUM",
                    "confidenceLimitations": "...",
                    "isSelected": true
                  }
                ]
                """;

            String userPrompt = "Problem: " + problem.getTitle() + "\nDescription: " + problem.getDescription() +
                    "\nDiagnosis: " + finding.getDiagnosticSummary() +
                    "\nGoal: " + problem.getGoal() +
                    "\nConstraints: " + problem.getConstraintsText();

            String jsonResponse = callLlm(systemPrompt, userPrompt);
            JsonNode root = objectMapper.readTree(cleanJsonMarkdown(jsonResponse));

            List<CandidateSolution> solutions = new ArrayList<>();
            if (root.isArray()) {
                for (JsonNode sNode : root) {
                    CandidateSolution sol = new CandidateSolution(
                            problem,
                            sNode.path("solutionIndex").asInt(solutions.size() + 1),
                            sNode.path("title").asText("Candidate Solution"),
                            sNode.path("summary").asText(),
                            sNode.path("reasoning").asText(),
                            sNode.path("effortLevel").asText("MEDIUM"),
                            sNode.path("confidenceLimitations").asText("Subject to empirical verification.")
                    );
                    sol.setSupportingEvidence(objectMapper.writeValueAsString(jsonToList(sNode.get("supportingEvidence"))));
                    sol.setBenefits(objectMapper.writeValueAsString(jsonToList(sNode.get("benefits"))));
                    sol.setTradeoffs(objectMapper.writeValueAsString(jsonToList(sNode.get("tradeoffs"))));
                    sol.setRisks(objectMapper.writeValueAsString(jsonToList(sNode.get("risks"))));
                    sol.setIsSelected(sNode.path("isSelected").asBoolean(solutions.isEmpty()));
                    solutions.add(sol);
                }
            }

            if (solutions.isEmpty()) {
                return demoAiService.generateSolutions(problem, finding, evidence);
            }
            return solutions;
        } catch (Exception e) {
            log.error("Live AI solution generation failed, falling back to deterministic Demo engine: {}", e.getMessage());
            return demoAiService.generateSolutions(problem, finding, evidence);
        }
    }

    @Override
    public List<ActionItem> generateActionPlan(ProblemCase problem, CandidateSolution solution) {
        if (isDemoMode()) {
            return demoAiService.generateActionPlan(problem, solution);
        }

        try {
            String systemPrompt = """
                You are SENTINEL. Generate an actionable, sequential execution checklist (3 to 6 steps) for the selected solution.
                Respond ONLY with a valid JSON array of objects:
                [
                  {
                    "stepNumber": 1,
                    "title": "...",
                    "description": "..."
                  }
                ]
                """;

            String userPrompt = "Problem: " + problem.getTitle() + "\nSelected Solution: " + solution.getTitle() +
                    "\nSummary: " + solution.getSummary();

            String jsonResponse = callLlm(systemPrompt, userPrompt);
            JsonNode root = objectMapper.readTree(cleanJsonMarkdown(jsonResponse));

            List<ActionItem> items = new ArrayList<>();
            if (root.isArray()) {
                for (JsonNode itemNode : root) {
                    items.add(new ActionItem(
                            problem,
                            solution,
                            itemNode.path("stepNumber").asInt(items.size() + 1),
                            itemNode.path("title").asText("Action Step"),
                            itemNode.path("description").asText()
                    ));
                }
            }

            if (items.isEmpty()) {
                return demoAiService.generateActionPlan(problem, solution);
            }
            return items;
        } catch (Exception e) {
            log.error("Live AI action plan generation failed, falling back to Demo engine: {}", e.getMessage());
            return demoAiService.generateActionPlan(problem, solution);
        }
    }

    @Override
    public VerificationPlanResult generateVerificationPlan(ProblemCase problem, CandidateSolution solution) {
        if (isDemoMode()) {
            return demoAiService.generateVerificationPlan(problem, solution);
        }

        try {
            String systemPrompt = """
                You are SENTINEL. Create a rigorous, falsifiable verification plan for this solution.
                Specify:
                1. testProcedure (concrete steps to test the solution under realistic conditions)
                2. expectedResult (unambiguous, quantifiable passing criteria)
                3. recommendedType ("MANUAL", "DETERMINISTIC_CALCULATOR", or "CODE_CHECK")
                4. verificationRationale (why this test proves the fix)
                
                Respond ONLY with a valid JSON object:
                {
                  "testProcedure": "...",
                  "expectedResult": "...",
                  "recommendedType": "MANUAL",
                  "verificationRationale": "..."
                }
                """;

            String userPrompt = "Problem: " + problem.getTitle() + "\nCategory: " + problem.getCategory() +
                    "\nSolution: " + solution.getTitle() + "\nReasoning: " + solution.getReasoning();

            String jsonResponse = callLlm(systemPrompt, userPrompt);
            JsonNode root = objectMapper.readTree(cleanJsonMarkdown(jsonResponse));

            VerificationType type = VerificationType.MANUAL;
            String typeStr = root.path("recommendedType").asText("MANUAL").toUpperCase();
            if ("DETERMINISTIC_CALCULATOR".equals(typeStr)) {
                type = VerificationType.DETERMINISTIC_CALCULATOR;
            } else if ("CODE_CHECK".equals(typeStr)) {
                type = VerificationType.CODE_CHECK;
            }

            return new VerificationPlanResult(
                    root.path("testProcedure").asText(),
                    root.path("expectedResult").asText(),
                    type,
                    root.path("verificationRationale").asText()
            );
        } catch (Exception e) {
            log.error("Live AI verification plan generation failed, falling back to Demo engine: {}", e.getMessage());
            return demoAiService.generateVerificationPlan(problem, solution);
        }
    }

    @Override
    public ClarificationEvaluationResult evaluateClarification(ProblemCase problem, InvestigationFinding currentFinding, List<ClarificationAnswerRequest.ClarificationEntry> answers) {
        if (isDemoMode()) {
            return demoAiService.evaluateClarification(problem, currentFinding, answers);
        }

        try {
            String systemPrompt = """
                You are SENTINEL. Re-evaluate the problem case based on new user answers to clarification questions.
                Update the facts, assumptions, remaining unknowns, and provide an updated diagnostic summary and confidence score.
                Respond ONLY with a valid JSON object:
                {
                  "newKnownFacts": ["..."],
                  "updatedAssumptions": ["..."],
                  "remainingUnknowns": ["..."],
                  "updatedDiagnosticSummary": "...",
                  "updatedConfidenceScore": 0.92
                }
                """;

            StringBuilder userPrompt = new StringBuilder();
            userPrompt.append("Original Diagnosis: ").append(currentFinding.getDiagnosticSummary()).append("\n");
            userPrompt.append("Clarification Responses:\n");
            for (ClarificationAnswerRequest.ClarificationEntry entry : answers) {
                userPrompt.append("Q: ").append(entry.getQuestion()).append("\n");
                userPrompt.append("A: ").append(entry.getAnswer()).append("\n\n");
            }

            String jsonResponse = callLlm(systemPrompt, userPrompt.toString());
            JsonNode root = objectMapper.readTree(cleanJsonMarkdown(jsonResponse));

            return new ClarificationEvaluationResult(
                    jsonToList(root.get("newKnownFacts")),
                    jsonToList(root.get("updatedAssumptions")),
                    jsonToList(root.get("remainingUnknowns")),
                    root.path("updatedDiagnosticSummary").asText(),
                    root.path("updatedConfidenceScore").asDouble(0.90)
            );
        } catch (Exception e) {
            log.error("Live AI clarification evaluation failed, falling back to Demo engine: {}", e.getMessage());
            return demoAiService.evaluateClarification(problem, currentFinding, answers);
        }
    }

    private String callLlm(String systemPrompt, String userPrompt) throws Exception {
        String endpoint = aiProperties.getBaseUrl().replaceAll("/+$", "") + "/chat/completions";

        Map<String, Object> messageSystem = Map.of("role", "system", "content", systemPrompt);
        Map<String, Object> messageUser = Map.of("role", "user", "content", userPrompt);

        Map<String, Object> payload = new HashMap<>();
        payload.put("model", aiProperties.getModel());
        payload.put("messages", List.of(messageSystem, messageUser));
        payload.put("temperature", 0.2);

        String requestBody = objectMapper.writeValueAsString(payload);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(endpoint))
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + aiProperties.getApiKey())
                .timeout(Duration.ofSeconds(aiProperties.getTimeoutSeconds()))
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() < 200 || response.statusCode() >= 300) {
            throw new AiServiceException("AI Provider API returned HTTP " + response.statusCode() + ": " + response.body());
        }

        JsonNode responseJson = objectMapper.readTree(response.body());
        JsonNode choices = responseJson.path("choices");
        if (choices.isArray() && !choices.isEmpty()) {
            return choices.get(0).path("message").path("content").asText();
        }

        throw new AiServiceException("AI Provider returned unexpected response structure without choices: " + response.body());
    }

    private List<String> jsonToList(JsonNode arrayNode) {
        List<String> list = new ArrayList<>();
        if (arrayNode != null && arrayNode.isArray()) {
            for (JsonNode item : arrayNode) {
                list.add(item.asText());
            }
        }
        return list;
    }

    private String cleanJsonMarkdown(String raw) {
        if (raw == null) return "{}";
        String trimmed = raw.trim();
        if (trimmed.startsWith("```json")) {
            trimmed = trimmed.substring(7);
        } else if (trimmed.startsWith("```")) {
            trimmed = trimmed.substring(3);
        }
        if (trimmed.endsWith("```")) {
            trimmed = trimmed.substring(0, trimmed.length() - 3);
        }
        return trimmed.trim();
    }
}
