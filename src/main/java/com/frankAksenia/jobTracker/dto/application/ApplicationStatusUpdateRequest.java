package com.frankAksenia.jobTracker.dto.application;

import java.util.UUID;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record ApplicationStatusUpdateRequest(
        @NotNull(message = "Application ID is required")
        UUID applicationId,

        @NotBlank(message = "New status is required")
        @Size(max = 32)
        String newStatus
) {}
