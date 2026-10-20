package com.frankAksenia.jobTracker.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.frankAksenia.jobTracker.dto.interview.InterviewCreateRequest;
import com.frankAksenia.jobTracker.dto.interview.InterviewResponse;
import com.frankAksenia.jobTracker.dto.interview.InterviewUpdateRequest;
import com.frankAksenia.jobTracker.exception.ResourceNotFoundException;
import com.frankAksenia.jobTracker.mapper.InterviewResponseMapper;
import com.frankAksenia.jobTracker.model.EInterviewType;
import com.frankAksenia.jobTracker.model.Interview;
import com.frankAksenia.jobTracker.model.JobApplication;
import com.frankAksenia.jobTracker.model.User;
import com.frankAksenia.jobTracker.repository.ApplicationRepository;
import com.frankAksenia.jobTracker.repository.InterviewRepository;

import jakarta.transaction.Transactional;

@Service 
public class InterviewService {

    private final InterviewRepository interviewRepository;
    private final ApplicationRepository applicationRepository;
    private final UserService userService;

    public InterviewService(InterviewRepository interviewRepository, ApplicationRepository applicationRepository,
            UserService userService) {
        this.interviewRepository = interviewRepository;
        this.applicationRepository = applicationRepository;
        this.userService = userService;
    }

    public List<InterviewResponse> getAllInterviewsByApplicationId(UUID applicationId, String username) {
        User currentUser = userService.getUserByEmailOrThrow(username);
        JobApplication jobApplication = userService.isHrSpecialist(currentUser)
                ? applicationRepository.findById(applicationId)
                    .orElseThrow(() -> new ResourceNotFoundException("Resource not found", "Job application not found"))
                : applicationRepository.findByUserAndApplicationId(currentUser, applicationId)
                .orElseThrow(() -> new ResourceNotFoundException("Resource not found", "Job application not found"));
        return interviewRepository.findAllByJobApplication(jobApplication)
                .stream().map(InterviewResponseMapper::mapToResponse).toList();
    }

    public List<InterviewResponse> getAllInterviewsByApplicant(String applicantEmail, String username) {
        User currentUser = userService.getUserByEmailOrThrow(username);
        String normalizedApplicantEmail = applicantEmail.toLowerCase(Locale.ROOT);
        if (!userService.isHrSpecialist(currentUser) && !currentUser.getEmail().equalsIgnoreCase(normalizedApplicantEmail)) 
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Applicants can only view their own interviews");
        

        User applicant = userService.getUserByEmailOrThrow(normalizedApplicantEmail);

        List<JobApplication> applications = applicationRepository.findAllByUser(applicant);
        return applications.stream()
                .flatMap(application -> interviewRepository.findAllByJobApplication(application).stream())
                .map(InterviewResponseMapper::mapToResponse)
                .toList();
    }

    public InterviewResponse createInterview(UUID applicationId, InterviewCreateRequest request, UserDetails loggedInUser) {

        requireHrSpecialist(loggedInUser.getUsername());
        JobApplication application = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new ResourceNotFoundException("Resource not found", "Job application not found"));

        Interview newInterview = new Interview();

        this.applyEditableFields(newInterview, request.interviewType(), request.scheduledAt(), request.durationMinutes(), request.location(), request.interviewer(), request.description());

        newInterview.setJobApplication(application);

        Interview savedInterview = interviewRepository.save(newInterview);

        return InterviewResponseMapper.mapToResponse(savedInterview);
    }

    @Transactional 
    public InterviewResponse updateInterview(UUID interviewId, InterviewUpdateRequest request, UserDetails loggedInUser) {
        requireHrSpecialist(loggedInUser.getUsername());
        Interview interviewToUpdate = interviewRepository.findById(interviewId)
        .orElseThrow(() -> new ResourceNotFoundException("Resource not found", "Interview with provided ID does not exist"));

        this.applyEditableFields(interviewToUpdate, request.interviewType(), request.scheduledAt(), request.durationMinutes(), request.location(), request.interviewer(), request.description());

        Interview updatedInterview = this.interviewRepository.save(interviewToUpdate);

        return InterviewResponseMapper.mapToResponse(updatedInterview);
    }

    private void applyEditableFields(Interview interview, String interviewType, LocalDateTime scheduledAt,
            Integer durationMinutes, String location, String interviewer, String description) {
        if (interviewType == null || interviewType.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Interview type is required");
        }
        try {
            interview.setInterviewType(EInterviewType.valueOf(interviewType.trim().toUpperCase(Locale.ROOT)));
        } catch (IllegalArgumentException exception) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid interview type", exception);
        }
        interview.setScheduledAt(scheduledAt);
        interview.setDurationMinutes(durationMinutes);
        interview.setLocation(location);
        interview.setInterviewer(interviewer);
        interview.setDescription(description);
    }

    private void requireHrSpecialist(String username) {
        if (!userService.isHrSpecialist(userService.getUserByEmailOrThrow(username))) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Only HR specialists can manage interviews");
        }
    }
}
