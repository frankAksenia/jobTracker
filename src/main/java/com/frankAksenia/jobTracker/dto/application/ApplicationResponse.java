package com.frankAksenia.jobTracker.dto.application;

import java.time.LocalDate;
import java.util.UUID;

import com.frankAksenia.jobTracker.model.EApplicationStatus;

public record ApplicationResponse(
    UUID applicationId,
    String companyName,
    String position,
    String location,
    LocalDate applicationDate,
    EApplicationStatus applicationStatus
) {}

