package com.frankAksenia.jobTracker.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.frankAksenia.jobTracker.service.InterviewService;

@RestController 
@RequestMapping ("api/interviews")
public class InterviewController {
    
    private final InterviewService interviewService;

    public InterviewController(InterviewService interviewService) {
        this.interviewService = interviewService;
    }
}
