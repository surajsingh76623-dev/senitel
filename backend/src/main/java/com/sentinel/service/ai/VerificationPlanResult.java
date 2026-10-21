package com.sentinel.service.ai;

import com.sentinel.domain.enums.VerificationType;

public record VerificationPlanResult(
        String testProcedure,
        String expectedResult,
        VerificationType recommendedType,
        String verificationRationale
) {}
