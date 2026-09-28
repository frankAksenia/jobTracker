package com.frankAksenia.jobTracker.mapper;

import com.frankAksenia.jobTracker.dto.application.ApplicationResponse;
import com.frankAksenia.jobTracker.model.JobApplication;

public final class ApplicationResponseMapper {

    private ApplicationResponseMapper() {}

    public static ApplicationResponse mapToResponse(JobApplication jobApplication) {
        return  new ApplicationResponse(
                jobApplication.getCompanyName(),
                jobApplication.getPosition(),
                jobApplication.getLocation(),
                jobApplication.getApplicationDate(),
                jobApplication.getApplicationStatus()
        );
    }
}