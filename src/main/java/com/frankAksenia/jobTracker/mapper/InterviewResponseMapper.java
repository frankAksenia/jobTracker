package com.frankAksenia.jobTracker.mapper;

import com.frankAksenia.jobTracker.dto.interview.InterviewResponse;
import com.frankAksenia.jobTracker.model.Interview;

public class InterviewResponseMapper {

    private InterviewResponseMapper() {}

        public static InterviewResponse mapToResponse(Interview interview) {
        return new InterviewResponse(
                interview.getInterviewType().toString(),
                interview.getScheduledAt().toString(),
                interview.getDurationMinutes(),
                interview.getLocation(),
                interview.getInterviewer(),
                interview.getDescription()
        );
    }
}
