package com.frankAksenia.jobTracker.dto.application;

import java.time.LocalDate;

import com.frankAksenia.jobTracker.model.EApplicationStatus;

public record ApplicationResponse(
    String companyName,
    String position,
    String location,
    LocalDate applicationDate,
    EApplicationStatus applicationStatus
) {}


