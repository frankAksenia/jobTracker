package com.frankAksenia.jobTracker.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.frankAksenia.jobTracker.dto.interview.InterviewCreateRequest;
import com.frankAksenia.jobTracker.dto.interview.InterviewResponse;
import com.frankAksenia.jobTracker.dto.interview.InterviewUpdateRequest;
import com.frankAksenia.jobTracker.service.InterviewService;

import jakarta.validation.Valid;

@RestController 
@RequestMapping("/api")
public class InterviewController {
    
    private final InterviewService interviewService;

    public InterviewController(InterviewService interviewService) {
        this.interviewService = interviewService;
    }

    @GetMapping("/applications/{applicationId}/interviews")
    public ResponseEntity<List<InterviewResponse>> getInterviewsByApplicationId(
            @PathVariable UUID applicationId,
            @AuthenticationPrincipal UserDetails loggedInUser
    ) {
        List<InterviewResponse> response = interviewService.getAllInterviewsByApplicationId(applicationId, loggedInUser.getUsername());
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @GetMapping("/applicants/{applicantEmail}/interviews")
    public ResponseEntity<List<InterviewResponse>> getInterviewsByApplicant(
        @PathVariable String applicantEmail,
        @AuthenticationPrincipal UserDetails loggedInUser
    ) {
        List<InterviewResponse> response = interviewService.getAllInterviewsByApplicant(applicantEmail, loggedInUser.getUsername());
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PostMapping 
    public ResponseEntity<InterviewResponse> createInterview(
        @Valid @RequestBody InterviewCreateRequest request,
        @AuthenticationPrincipal UserDetails loggedInUser
    ) {
        InterviewResponse response = interviewService.createInterview(request, loggedInUser);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping 
    public ResponseEntity<InterviewResponse> updateInterview(
        @Valid @RequestBody InterviewUpdateRequest request,
        @AuthenticationPrincipal UserDetails loggedInUser
    ) {
        InterviewResponse response = interviewService.updateInterview(request, loggedInUser);
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(response);
    }

}
