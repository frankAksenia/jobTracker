package com.frankAksenia.jobTracker.service;

import org.springframework.stereotype.Service;

import com.frankAksenia.jobTracker.repository.ApplicationRepository;

@Service 
public class ApplicationService {

    private final ApplicationRepository applicationRepository;

    public ApplicationService(ApplicationRepository applicationRepository) {
        this.applicationRepository = applicationRepository;
    }
    
}
