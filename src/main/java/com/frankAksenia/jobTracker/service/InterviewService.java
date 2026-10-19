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
import com.frankAksenia.jobTracker.model.EUserRole;
import com.frankAksenia.jobTracker.model.Interview;
import com.frankAksenia.jobTracker.model.JobApplication;
import com.frankAksenia.jobTracker.model.User;
import com.frankAksenia.jobTracker.repository.ApplicationRepository;
import com.frankAksenia.jobTracker.repository.InterviewRepository;
import com.frankAksenia.jobTracker.repository.UserRepository;

import jakarta.transaction.Transactional;

@Service 
public class InterviewService {

    private final InterviewRepository interviewRepository;
    private final ApplicationRepository applicationRepository;
    private final UserRepository userRepository;

    public InterviewService(InterviewRepository interviewRepository, ApplicationRepository applicationRepository, UserRepository userRepository) {
        this.interviewRepository = interviewRepository;
        this.applicationRepository = applicationRepository;
        this.userRepository = userRepository;
    }

    public List<InterviewResponse> getAllInterviewsByApplicationId(UUID applicationId, String username) {
        User currentUser = findUser(username);
        JobApplication jobApplication = isHrSpecialist(currentUser)
                ? applicationRepository.findById(applicationId)
                    .orElseThrow(() -> new ResourceNotFoundException("Resource not found", "Job application not found"))
                : applicationRepository.findByUserAndApplicationId(currentUser, applicationId)
                .orElseThrow(() -> new ResourceNotFoundException("Resource not found", "Job application not found"));
        return interviewRepository.findAllByJobApplication(jobApplication)
                .stream().map(InterviewResponseMapper::mapToResponse).toList();
    }

    public List<InterviewResponse> getAllInterviewsByApplicant(String applicantEmail, String username) {
        User currentUser = findUser(username);
        String normalizedApplicantEmail = applicantEmail.toLowerCase(Locale.ROOT);
        if (!isHrSpecialist(currentUser) && !currentUser.getEmail().equalsIgnoreCase(normalizedApplicantEmail)) 
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Applicants can only view their own interviews");
        

        User applicant = userRepository.findByEmail(normalizedApplicantEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Resource not found", "User not found"));

        List<JobApplication> applications = applicationRepository.findAllByUser(applicant);
        return applications.stream()
                .flatMap(application -> interviewRepository.findAllByJobApplication(application).stream())
                .map(InterviewResponseMapper::mapToResponse)
                .toList();
    }

    public InterviewResponse createInterview(InterviewCreateRequest request, UserDetails loggedInUser) {

        requireHrSpecialist(loggedInUser.getUsername());

        Interview newInterview = new Interview();

        this.applyEditableFields(newInterview, request.interviewType(), request.scheduledAt(), request.durationMinutes(), request.location(), request.interviewer(), request.description());

        newInterview.setJobApplication(applicationRepository.findById(request.applicationId()).get());

        Interview savedInterview = interviewRepository.save(newInterview);

        return InterviewResponseMapper.mapToResponse(savedInterview);
    }

    @Transactional 
    public InterviewResponse updateInterview(InterviewUpdateRequest request, UserDetails loggedInUser) {           
        Interview interviewToUpdate = interviewRepository.findById(request.interviewId())
        .orElseThrow(() -> new ResourceNotFoundException("Resource not found", "Interview with provided ID does not exist"));

        this.applyEditableFields(interviewToUpdate, request.interviewType(), request.scheduledAt(), request.durationMinutes(), request.location(), request.interviewer(), request.description());

        Interview updatedInterview = this.interviewRepository.save(interviewToUpdate);

        return InterviewResponseMapper.mapToResponse(updatedInterview);
    }

    private void applyEditableFields(Interview interview, String interviewType, LocalDateTime scheduledAt,
            Integer durationMinutes, String location, String interviewer, String description) {
        interview.setInterviewType(EInterviewType.valueOf(interviewType));
        interview.setScheduledAt(scheduledAt);
        interview.setDurationMinutes(durationMinutes);
        interview.setLocation(location);
        interview.setDescription(description);

    }

    private void requireHrSpecialist(String username) {
        if (!isHrSpecialist(findUser(username))) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Only HR specialists can view all interviews");
        }
    }

    private User findUser(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Resource not found", "User not found"));
    }

    private boolean isHrSpecialist(User user) {
        return user.getUserRole() == EUserRole.HR_SPECIALIST;
    }


}
