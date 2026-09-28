package com.frankAksenia.jobTracker.dto.application;

import java.util.UUID;

import com.frankAksenia.jobTracker.model.User;

import jakarta.validation.constraints.NotNull;

public record ApplicationCreateRequest(
    @NotNull (message = "Application ID is required!")
    UUID applicationId,

    @NotNull (message = "Company name is required!")
    String companyName,

    @NotNull (message = "Position is required!")
    String position,

    String location,

    String jobUrl,

    String source,

    User user
) 
{}
