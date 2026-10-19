package com.frankAksenia.jobTracker.dto.interview;

public record InterviewResponse(
    String interviewType,
    String scheduledAt,
    Integer durationMinutes,
    String location,
    String interviewer,
    String description
) 
{}