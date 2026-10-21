package com.sentinel.dto;

import com.sentinel.domain.enums.ProblemCategory;
import com.sentinel.domain.enums.ProblemUrgency;
import jakarta.validation.constraints.Size;

public class UpdateProblemRequest {

    @Size(max = 255, message = "Title cannot exceed 255 characters")
    private String title;

    private String description;
    private ProblemCategory category;
    private String goal;
    private String constraintsText;
    private ProblemUrgency urgency;
    private String userNotes;

    public UpdateProblemRequest() {}

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

    public String getUserNotes() { return userNotes; }
    public void setUserNotes(String userNotes) { this.userNotes = userNotes; }
}
