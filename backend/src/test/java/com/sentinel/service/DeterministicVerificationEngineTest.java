package com.sentinel.service;

import com.sentinel.domain.enums.VerificationStatus;
import com.sentinel.dto.DeterministicCheckRequest;
import com.sentinel.dto.DeterministicCheckResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class DeterministicVerificationEngineTest {

    private DeterministicVerificationEngine engine;

    @BeforeEach
    void setUp() {
        engine = new DeterministicVerificationEngine();
    }

    @Test
    @DisplayName("PINN L2 Norm: Passes when relative error is below tolerance threshold")
    void pinnL2Norm_passesWhenBelowThreshold() {
        DeterministicCheckRequest request = new DeterministicCheckRequest(
                "PINN_L2_NORM",
                "sol-math-001",
                Map.of("relativeL2Error", 0.00115, "tolerance", 0.0015)
        );

        DeterministicCheckResponse response = engine.executeCheck(request);

        assertTrue(response.isPassed());
        assertEquals(VerificationStatus.PASSED, response.getStatus());
        assertEquals(0.00115, response.getActualValue());
        assertNotNull(response.getCalculationDetails());
    }

    @Test
    @DisplayName("PINN L2 Norm: Fails when relative error exceeds tolerance threshold")
    void pinnL2Norm_failsWhenExceedsThreshold() {
        DeterministicCheckRequest request = new DeterministicCheckRequest(
                "PINN_L2_NORM",
                "sol-math-001",
                Map.of("relativeL2Error", 0.0084, "tolerance", 0.0015)
        );

        DeterministicCheckResponse response = engine.executeCheck(request);

        assertFalse(response.isPassed());
        assertEquals(VerificationStatus.FAILED, response.getStatus());
        assertEquals(0.0084, response.getActualValue());
    }

    @Test
    @DisplayName("SaaS Economics: Validates gross margin >= 78% and churn reversal")
    void saasEconomics_validatesMarginAndChurn() {
        DeterministicCheckRequest request = new DeterministicCheckRequest(
                "SAAS_CHURN_MARGIN",
                "sol-biz-001",
                Map.of(
                        "basePrice", 189.0,
                        "infrastructureCogs", 35.0,
                        "minGrossMargin", 0.78,
                        "maxAcceptableChurnRate", 0.035,
                        "historicalChurnRate", 0.021,
                        "currentSpikeChurnRate", 0.068,
                        "estimatedRecoveryRate", 0.70
                )
        );

        DeterministicCheckResponse response = engine.executeCheck(request);

        assertTrue(response.isPassed());
        assertEquals(VerificationStatus.PASSED, response.getStatus());
        assertTrue(response.getOutputMetrics().containsKey("grossMarginPercent"));
        double margin = (double) response.getOutputMetrics().get("grossMarginPercent");
        assertTrue(margin >= 78.0);
    }

    @Test
    @DisplayName("SQL Connection Pool: Passes when p99 wait <= 5ms and 0 timeouts")
    void sqlConnectionPool_passesUnderHealthyMetrics() {
        DeterministicCheckRequest request = new DeterministicCheckRequest(
                "SQL_POOL_STARVATION",
                "sol-sw-001",
                Map.of(
                        "requestsPerSecond", 5000,
                        "maxWaitThresholdMs", 5.0,
                        "p99AcquisitionWaitMs", 2.8,
                        "timeoutErrorCount", 0
                )
        );

        DeterministicCheckResponse response = engine.executeCheck(request);

        assertTrue(response.isPassed());
        assertEquals(VerificationStatus.PASSED, response.getStatus());
    }

    @Test
    @DisplayName("SQL Connection Pool: Fails if timeout errors occur")
    void sqlConnectionPool_failsOnTimeouts() {
        DeterministicCheckRequest request = new DeterministicCheckRequest(
                "SQL_POOL_STARVATION",
                "sol-sw-001",
                Map.of(
                        "requestsPerSecond", 5000,
                        "maxWaitThresholdMs", 5.0,
                        "p99AcquisitionWaitMs", 12.4,
                        "timeoutErrorCount", 42
                )
        );

        DeterministicCheckResponse response = engine.executeCheck(request);

        assertFalse(response.isPassed());
        assertEquals(VerificationStatus.FAILED, response.getStatus());
    }
}
