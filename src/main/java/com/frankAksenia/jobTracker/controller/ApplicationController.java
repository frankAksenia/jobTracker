package com.frankAksenia.jobTracker.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.frankAksenia.jobTracker.dto.application.ApplicationCreateOrUpdateRequest;
import com.frankAksenia.jobTracker.dto.application.ApplicationResponse;
import com.frankAksenia.jobTracker.service.ApplicationService;

import jakarta.validation.Valid;

@RestController 
@RequestMapping ("api/applications")
public class ApplicationController {

    private final ApplicationService applicationService;

    public ApplicationController(ApplicationService applicationService) {
        this.applicationService = applicationService;
    }

    @GetMapping 
    public ResponseEntity<List<ApplicationResponse>> getAllApplications(
            @AuthenticationPrincipal UserDetails loggedInUser) {
        List<ApplicationResponse> response = this.applicationService.getAllApplications(loggedInUser.getUsername());
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @GetMapping("/application")
    public ResponseEntity<ApplicationResponse> getApplicationById(
            @RequestParam UUID applicationId,
            @AuthenticationPrincipal UserDetails loggedInUser) {
        ApplicationResponse response = this.applicationService.getApplicationById(applicationId, loggedInUser.getUsername());
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @DeleteMapping("/application")
    public ResponseEntity<Void> deleteApplication(
            @RequestParam UUID applicationId,
            @AuthenticationPrincipal UserDetails loggedInUser) {
        applicationService.deleteApplication(applicationId, loggedInUser.getUsername());
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/application")
    public ResponseEntity<ApplicationResponse> createApplication(
            @Valid @RequestBody ApplicationCreateOrUpdateRequest request,
            @AuthenticationPrincipal UserDetails loggedInUser
    ) {
        ApplicationResponse response = this.applicationService.createApplication(request, loggedInUser.getUsername());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/application")
    public ResponseEntity<ApplicationResponse> updateApplication(
            @Valid @RequestBody ApplicationCreateOrUpdateRequest request,
            @AuthenticationPrincipal UserDetails loggedInUser
    ) {
        ApplicationResponse response = this.applicationService.updateApplication(request, loggedInUser.getUsername());
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PatchMapping("/application/status")
    public ResponseEntity<ApplicationResponse> updateApplicationStatus(
            @RequestParam UUID applicationId,
            @RequestParam String newStatus,
            @AuthenticationPrincipal UserDetails loggedInUser
    ) {
        ApplicationResponse response = this.applicationService.updateApplicationStatus(applicationId, newStatus, loggedInUser.getUsername());
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

}
