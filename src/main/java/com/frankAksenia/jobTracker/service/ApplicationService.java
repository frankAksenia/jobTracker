package com.frankAksenia.jobTracker.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.frankAksenia.jobTracker.dto.application.ApplicationCreateOrUpdateRequest;
import com.frankAksenia.jobTracker.dto.application.ApplicationResponse;
import com.frankAksenia.jobTracker.exception.ResourceNotFoundException;
import com.frankAksenia.jobTracker.mapper.ApplicationResponseMapper;
import com.frankAksenia.jobTracker.model.EApplicationStatus;
import com.frankAksenia.jobTracker.model.JobApplication;
import com.frankAksenia.jobTracker.model.User;
import com.frankAksenia.jobTracker.model.EUserRole;
import com.frankAksenia.jobTracker.repository.ApplicationRepository;
import com.frankAksenia.jobTracker.repository.UserRepository;

import jakarta.transaction.Transactional;


@Service 
public class ApplicationService {

    private final ApplicationRepository applicationRepository;

    private final UserRepository userRepository;

    public ApplicationService(ApplicationRepository applicationRepository, UserRepository userRepository) {
        this.applicationRepository = applicationRepository;
        this.userRepository = userRepository;
    }

    public List<ApplicationResponse> getAllApplications(String userEmail) {
        User user = findUser(userEmail);
        List<JobApplication> applications = isHrSpecialist(user)
                ? this.applicationRepository.findAll()
                : this.applicationRepository.findAllByUser(user);
        return applications
                .stream()
                .map(ApplicationResponseMapper::mapToResponse)
                .toList();
    }

    public ApplicationResponse getApplicationById(UUID applicationID, String userEmail) {
        User user = findUser(userEmail);
        return ApplicationResponseMapper.mapToResponse(findAccessibleApplication(user, applicationID)
                .orElseThrow(() -> new ResourceNotFoundException("Resource not found", "Application with the given ID does not exist.")));
    }

    @Transactional
    public ApplicationResponse createApplication(ApplicationCreateOrUpdateRequest request, String userEmail) {
        if (request.applicationId() != null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Application ID must not be provided when creating an application");
        }

        User user = findUser(userEmail);
        if (isHrSpecialist(user)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "HR specialists cannot create applicant-owned applications");
        }
         
        JobApplication newApplication = new JobApplication();
        applyEditableFields(newApplication, request);

        newApplication.setStatus(EApplicationStatus.APPLIED);
        newApplication.setApplicationDate(request.applicationDate() == null ? LocalDate.now() : request.applicationDate());
        newApplication.setUser(user);

        JobApplication savedApplication = this.applicationRepository.save(newApplication);

        return ApplicationResponseMapper.mapToResponse(savedApplication);
    }

    @Transactional
    public ApplicationResponse updateApplication(ApplicationCreateOrUpdateRequest request, String userEmail) {

        User user = findUser(userEmail);

        if (request.applicationId() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Application ID is required for an update");
        }

        JobApplication application = findAccessibleApplication(user, request.applicationId())
            .orElseThrow(() -> new ResourceNotFoundException("Resource not found", "Application with the given ID does not exist for the specified user."));

        applyEditableFields(application, request);

        JobApplication updatedApplication = this.applicationRepository.save(application);

        return ApplicationResponseMapper.mapToResponse(updatedApplication);
    }


    private void applyEditableFields(JobApplication application, ApplicationCreateOrUpdateRequest request) {

        application.setCompanyName(request.companyName());
        application.setPosition(request.position());
        application.setLocation(request.location());
        application.setJobUrl(request.jobUrl());
        application.setSource(request.source());
        if (request.applicationDate() != null) {
            application.setApplicationDate(request.applicationDate());
        }
    }

    private User findUser(String userEmail) {
        return this.userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Resource not found", "User with the authenticated email does not exist."));
    }

    public ApplicationResponse updateApplicationStatus(UUID applicationId, String newStatus, String username) {
        User user = findUser(username);

        JobApplication application = findAccessibleApplication(user, applicationId)
                .orElseThrow(() -> new ResourceNotFoundException("Resource not found", "Application with the given ID does not exist for the specified user."));

        EApplicationStatus status;
        try {
            status = EApplicationStatus.valueOf(newStatus.toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid status value: " + newStatus);
        }

        application.setStatus(status);
        JobApplication updatedApplication = this.applicationRepository.save(application);

        return ApplicationResponseMapper.mapToResponse(updatedApplication);
    }

    @Transactional
    public void deleteApplication(UUID applicationId, String userEmail) {
        User user = findUser(userEmail);
        JobApplication application = findAccessibleApplication(user, applicationId)
                .orElseThrow(() -> new ResourceNotFoundException("Resource not found", "Application with the given ID does not exist for the specified user."));
        applicationRepository.delete(application);
    }

    private Optional<JobApplication> findAccessibleApplication(User user, UUID applicationId) {
        return isHrSpecialist(user)
                ? applicationRepository.findById(applicationId)
                : applicationRepository.findByUserAndApplicationId(user, applicationId);
    }

    private boolean isHrSpecialist(User user) {
        return user.getUserRole() == EUserRole.HR_SPECIALIST;
    }
}
