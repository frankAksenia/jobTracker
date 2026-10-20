package com.frankAksenia.jobTracker.dto.application;

import java.time.LocalDate;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ApplicationUpdateRequest(
        @NotBlank(message = "Company name is required!")
        @Size(max = 255)
        String companyName,

        @NotBlank(message = "Position is required!")
        @Size(max = 255)
        String position,

        @Size(max = 255)
        String location,

        @Size(max = 2048)
        String jobUrl,

        @Size(max = 255)
        String source,

        LocalDate applicationDate
) {}
