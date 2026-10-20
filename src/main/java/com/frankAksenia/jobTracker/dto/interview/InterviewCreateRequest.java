package com.frankAksenia.jobTracker.dto.interview;


import java.time.LocalDateTime;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record InterviewCreateRequest(
    @NotBlank(message = "Interview type is required!")
    @Size(max = 255)
    String interviewType,
    
    @NotNull(message = "Scheduled at is required!")
    LocalDateTime scheduledAt,

    Integer durationMinutes,

    @NotBlank 
    String location,

    @NotBlank(message = "Interviewer is required!")
    String interviewer,
    
    String description
) {

}
