package com.sentinel.dto;

import jakarta.validation.constraints.NotNull;

public class UpdateActionItemRequest {

    @NotNull(message = "isCompleted is required")
    private Boolean isCompleted;

    public UpdateActionItemRequest() {}

    public UpdateActionItemRequest(Boolean isCompleted) {
        this.isCompleted = isCompleted;
    }

    public Boolean getIsCompleted() { return isCompleted; }
    public void setIsCompleted(Boolean isCompleted) { this.isCompleted = isCompleted; }
}
