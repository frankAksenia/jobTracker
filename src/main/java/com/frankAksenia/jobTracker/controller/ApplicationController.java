package com.frankAksenia.jobTracker.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.frankAksenia.jobTracker.dto.application.ApplicationCreateRequest;
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
    public ResponseEntity<List<ApplicationResponse>> getAllApplications() {
        List<ApplicationResponse> response = this.applicationService.getAllApplications();
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @GetMapping("/application")
    public ResponseEntity<ApplicationResponse> getApplicationById(@RequestParam UUID applicationID) {
        ApplicationResponse response = this.applicationService.getApplicationById(applicationID);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PostMapping
    public ResponseEntity<ApplicationResponse> createApplication(@Valid @RequestBody ApplicationCreateRequest request) {
        ApplicationResponse response = this.applicationService.createApplication(request);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
    
}
