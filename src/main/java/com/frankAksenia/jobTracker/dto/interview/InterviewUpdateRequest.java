package com.frankAksenia.jobTracker.dto.interview;

import java.time.LocalDateTime;
import java.util.UUID;

import jakarta.validation.constraints.NotNull;

public record InterviewUpdateRequest(
    @NotNull 
    UUID interviewId,
    String interviewType,
    LocalDateTime scheduledAt,
    Integer durationMinutes,
    String location,
    String interviewer,
    String description
) {
}
