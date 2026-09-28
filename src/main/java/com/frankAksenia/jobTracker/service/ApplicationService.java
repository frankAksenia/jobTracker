package com.frankAksenia.jobTracker.service;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.frankAksenia.jobTracker.dto.application.ApplicationCreateRequest;
import com.frankAksenia.jobTracker.dto.application.ApplicationResponse;
import com.frankAksenia.jobTracker.exception.ResourceNotFoundException;
import com.frankAksenia.jobTracker.mapper.ApplicationResponseMapper;
import com.frankAksenia.jobTracker.model.EApplicationStatus;
import com.frankAksenia.jobTracker.model.JobApplication;
import com.frankAksenia.jobTracker.model.User;
import com.frankAksenia.jobTracker.repository.ApplicationRepository;
import com.frankAksenia.jobTracker.repository.UserRepository;


@Service 
public class ApplicationService {

    private final ApplicationRepository applicationRepository;

    private final UserRepository userRepository;

    public ApplicationService(ApplicationRepository applicationRepository, UserRepository userRepository) {
        this.applicationRepository = applicationRepository;
        this.userRepository = userRepository;
    }

    public List<ApplicationResponse> getAllApplications() {
        return this.applicationRepository.findAll()
                .stream()
                .map(ApplicationResponseMapper::mapToResponse)
                .toList();
    }

    public ApplicationResponse getApplicationById(UUID applicationID) {
        return ApplicationResponseMapper.mapToResponse(this.applicationRepository.findById(applicationID)
                .orElseThrow(() -> new ResourceNotFoundException("Resource not found", "Application with the given ID does not exist.")));
    }

    public ApplicationResponse createApplication(ApplicationCreateRequest request) {

        User user = this.userRepository.findById(request.user().getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("Resource not found", "User ID does not exist"));
         
        JobApplication newApplication = new JobApplication();

        newApplication.setApplicationId(request.applicationId());
        newApplication.setCompanyName(request.companyName());
        newApplication.setPosition(request.position());
        newApplication.setLocation(request.location());
        newApplication.setJobUrl(request.jobUrl());
        newApplication.setSource(request.source());
        newApplication.setApplicationStatus(EApplicationStatus.APPLIED);
        newApplication.setApplicationDate(LocalDate.now());
        newApplication.setUser(user);

        JobApplication savedApplication = this.applicationRepository.save(newApplication);

        return ApplicationResponseMapper.mapToResponse(savedApplication);
    }

    
}
