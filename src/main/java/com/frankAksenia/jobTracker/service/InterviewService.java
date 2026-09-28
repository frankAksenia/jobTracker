package com.frankAksenia.jobTracker.service;

import org.springframework.stereotype.Service;

import com.frankAksenia.jobTracker.repository.InterviewRepository;

@Service 
public class InterviewService {

    private final InterviewRepository interviewRepository;

    public InterviewService(InterviewRepository interviewRepository) {
        this.interviewRepository = interviewRepository;
    }
    
}
