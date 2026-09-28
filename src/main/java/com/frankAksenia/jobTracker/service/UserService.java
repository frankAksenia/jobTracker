package com.frankAksenia.jobTracker.service;

import org.springframework.stereotype.Service;

import com.frankAksenia.jobTracker.repository.UserRepository;

@Service 
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }
}
