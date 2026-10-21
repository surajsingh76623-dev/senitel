package com.sentinel.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sentinel.domain.enums.ProblemCategory;
import com.sentinel.domain.enums.ProblemStatus;
import com.sentinel.domain.enums.ProblemUrgency;
import com.sentinel.dto.CreateProblemRequest;
import com.sentinel.dto.StatusTransitionRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("dev")
class ProblemCaseControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("POST /api/cases: Successfully creates problem case with valid inputs")
    void createCase_withValidInput_returnsCreated() throws Exception {
        CreateProblemRequest request = new CreateProblemRequest(
                "Integration Test Memory Leak",
                "Application crashes with OOM after 4 hours of constant traffic.",
                ProblemCategory.SOFTWARE_DEBUGGING,
                "Eliminate memory leak and maintain stable 2GB heap footprint.",
                "Cannot increase container RAM beyond 4GB.",
                ProblemUrgency.HIGH
        );

        mockMvc.perform(post("/api/cases")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", notNullValue()))
                .andExpect(jsonPath("$.title", is("Integration Test Memory Leak")))
                .andExpect(jsonPath("$.status", is("DRAFT")))
                .andExpect(jsonPath("$.category", is("SOFTWARE_DEBUGGING")));
    }

    @Test
    @DisplayName("POST /api/cases: Returns 400 Validation Error when required fields are missing")
    void createCase_withMissingFields_returnsBadRequest() throws Exception {
        CreateProblemRequest invalid = new CreateProblemRequest();
        invalid.setTitle(""); // Blank title

        mockMvc.perform(post("/api/cases")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalid)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error", is("Validation Error")))
                .andExpect(jsonPath("$.validationErrors", not(empty())));
    }

    @Test
    @DisplayName("GET /api/cases: Returns seeded demo cases in list view")
    void getAllCases_returnsSeededCases() throws Exception {
        mockMvc.perform(get("/api/cases"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", not(empty())))
                .andExpect(jsonPath("$[?(@.id == 'case-sw-001')]", not(empty())));
    }

    @Test
    @DisplayName("POST /api/cases/{id}/status: Rejects invalid transition with 400 Bad Request")
    void transitionStatus_withDisallowedJump_returnsBadRequest() throws Exception {
        StatusTransitionRequest invalidJump = new StatusTransitionRequest(
                ProblemStatus.VERIFIED,
                "Premature transition",
                "User"
        );

        // case-math-001 is in PROPOSED status, cannot transition directly to VERIFIED without IN_PROGRESS and passing test
        mockMvc.perform(post("/api/cases/case-math-001/status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidJump)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error", is("Invalid State Transition")));
    }
}
