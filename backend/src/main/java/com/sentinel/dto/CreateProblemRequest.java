package com.sentinel.dto;

import com.sentinel.domain.enums.ProblemCategory;
import com.sentinel.domain.enums.ProblemUrgency;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class CreateProblemRequest {

    @NotBlank(message = "Title is required")
    @Size(max = 255, message = "Title cannot exceed 255 characters")
    private String title;

    @NotBlank(message = "Description is required")
    private String description;

    @NotNull(message = "Category is required")
    private ProblemCategory category;

    // Optional for user convenience: defaults automatically if not provided
    private String goal;

    private String constraintsText;

    // Optional for user convenience: defaults to MEDIUM if not provided
    private ProblemUrgency urgency;

    public CreateProblemRequest() {}

    public CreateProblemRequest(String title, String description, ProblemCategory category) {
        this.title = title;
        this.description = description;
        this.category = category;
        this.goal = "Analyze problem and provide clear step-by-step resolution";
        this.urgency = ProblemUrgency.MEDIUM;
    }

    public CreateProblemRequest(String title, String description, ProblemCategory category, String goal, String constraintsText, ProblemUrgency urgency) {
        this.title = title;
        this.description = description;
        this.category = category;
        this.goal = (goal != null && !goal.isBlank()) ? goal : "Analyze problem and provide clear step-by-step resolution";
        this.constraintsText = constraintsText;
        this.urgency = urgency != null ? urgency : ProblemUrgency.MEDIUM;
    }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public ProblemCategory getCategory() { return category; }
    public void setCategory(ProblemCategory category) { this.category = category; }

    public String getGoal() {
        if (goal == null || goal.isBlank()) {
            return title != null ? "Resolve: " + title : "Find and verify complete solution";
        }
        return goal;
    }
    public void setGoal(String goal) { this.goal = goal; }

    public String getConstraintsText() { return constraintsText; }
    public void setConstraintsText(String constraintsText) { this.constraintsText = constraintsText; }

    public ProblemUrgency getUrgency() {
        return urgency != null ? urgency : ProblemUrgency.MEDIUM;
    }
    public void setUrgency(ProblemUrgency urgency) { this.urgency = urgency; }
}
