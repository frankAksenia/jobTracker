package com.frankAksenia.jobTracker.dto.application;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ApplicationStatusUpdateRequest(
        @NotBlank(message = "New status is required")
        @Size(max = 32)
        String newStatus
) {}
