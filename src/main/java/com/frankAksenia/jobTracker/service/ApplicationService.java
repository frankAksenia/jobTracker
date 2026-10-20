package com.frankAksenia.jobTracker.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.frankAksenia.jobTracker.dto.application.ApplicationCreateRequest;
import com.frankAksenia.jobTracker.dto.application.ApplicationResponse;
import com.frankAksenia.jobTracker.dto.application.ApplicationUpdateRequest;
import com.frankAksenia.jobTracker.exception.ResourceNotFoundException;
import com.frankAksenia.jobTracker.mapper.ApplicationResponseMapper;
import com.frankAksenia.jobTracker.model.EApplicationStatus;
import com.frankAksenia.jobTracker.model.JobApplication;
import com.frankAksenia.jobTracker.model.User;
import com.frankAksenia.jobTracker.repository.ApplicationRepository;

import jakarta.transaction.Transactional;


@Service 
public class ApplicationService {

    private final ApplicationRepository applicationRepository;

    private final UserService userService;

    public ApplicationService(ApplicationRepository applicationRepository, UserService userService) {
        this.applicationRepository = applicationRepository;
        this.userService = userService;
    }

    public List<ApplicationResponse> getAllApplications(String userEmail) {
        User user = userService.getUserByEmailOrThrow(userEmail);
        List<JobApplication> applications = userService.isHrSpecialist(user)
                ? this.applicationRepository.findAll()
                : this.applicationRepository.findAllByUser(user);
        return applications.stream().map(ApplicationResponseMapper::mapToResponse).toList();
    }

    public ApplicationResponse getApplicationById(UUID applicationID, String userEmail) {
        User user = userService.getUserByEmailOrThrow(userEmail);
        return ApplicationResponseMapper.mapToResponse(findAccessibleApplication(user, applicationID)
                .orElseThrow(() -> new ResourceNotFoundException("Resource not found", "Application with the given ID does not exist.")));
    }

    @Transactional
    public ApplicationResponse createApplication(ApplicationCreateRequest request, String userEmail) {
        User user = userService.getUserByEmailOrThrow(userEmail);
        if (userService.isHrSpecialist(user))
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "HR specialists cannot create applicant-owned applications");

        JobApplication newApplication = new JobApplication();

        applyEditableFields(newApplication, request.companyName(), request.position(), request.location(),
                request.jobUrl(), request.source(), request.applicationDate());

        newApplication.setStatus(EApplicationStatus.APPLIED);
        newApplication.setApplicationDate(request.applicationDate() == null ? LocalDate.now() : request.applicationDate());
        newApplication.setUser(user);

        JobApplication savedApplication = this.applicationRepository.save(newApplication);

        return ApplicationResponseMapper.mapToResponse(savedApplication);
    }

    @Transactional
    public ApplicationResponse updateApplication(ApplicationUpdateRequest request, String userEmail) {

        User user = userService.getUserByEmailOrThrow(userEmail);

        JobApplication application = findAccessibleApplication(user, request.applicationId())
            .orElseThrow(() -> new ResourceNotFoundException("Resource not found", "Application with the given ID does not exist for the specified user."));

        applyEditableFields(application, request.companyName(), request.position(), request.location(),
                request.jobUrl(), request.source(), request.applicationDate());

        JobApplication updatedApplication = this.applicationRepository.save(application);

        return ApplicationResponseMapper.mapToResponse(updatedApplication);
    }


    private void applyEditableFields(JobApplication application, String companyName, String position,
            String location, String jobUrl, String source, LocalDate applicationDate) {
        application.setCompanyName(companyName);
        application.setPosition(position);
        application.setLocation(location);
        application.setJobUrl(jobUrl);
        application.setSource(source);
        if (applicationDate != null) {
            application.setApplicationDate(applicationDate);
        }
    }

    
    public ApplicationResponse updateApplicationStatus(UUID applicationId, String newStatus, String username) {
        User user = userService.getUserByEmailOrThrow(username);
        
        if (!userService.isHrSpecialist(user))
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Only HR specialists can change application status");
        
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
        User user = userService.getUserByEmailOrThrow(userEmail);
        JobApplication application = findAccessibleApplication(user, applicationId)
        .orElseThrow(() -> new ResourceNotFoundException("Resource not found", "Application with the given ID does not exist for the specified user."));
        applicationRepository.delete(application);
    }
    
    private Optional<JobApplication> findAccessibleApplication(User user, UUID applicationId) {
        return userService.isHrSpecialist(user)
        ? applicationRepository.findById(applicationId)
        : applicationRepository.findByUserAndApplicationId(user, applicationId);
    }
}
