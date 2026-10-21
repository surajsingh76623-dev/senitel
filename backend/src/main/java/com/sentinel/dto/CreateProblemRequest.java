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

    @NotBlank(message = "Goal is required")
    private String goal;

    private String constraintsText;

    @NotNull(message = "Urgency is required")
    private ProblemUrgency urgency;

    public CreateProblemRequest() {}

    public CreateProblemRequest(String title, String description, ProblemCategory category, String goal, String constraintsText, ProblemUrgency urgency) {
        this.title = title;
        this.description = description;
        this.category = category;
        this.goal = goal;
        this.constraintsText = constraintsText;
        this.urgency = urgency;
    }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public ProblemCategory getCategory() { return category; }
    public void setCategory(ProblemCategory category) { this.category = category; }

    public String getGoal() { return goal; }
    public void setGoal(String goal) { this.goal = goal; }

    public String getConstraintsText() { return constraintsText; }
    public void setConstraintsText(String constraintsText) { this.constraintsText = constraintsText; }

    public ProblemUrgency getUrgency() { return urgency; }
    public void setUrgency(ProblemUrgency urgency) { this.urgency = urgency; }
}
