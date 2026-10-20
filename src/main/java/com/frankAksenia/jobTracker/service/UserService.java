package com.frankAksenia.jobTracker.service;

import org.springframework.stereotype.Service;

import com.frankAksenia.jobTracker.exception.ResourceNotFoundException;
import com.frankAksenia.jobTracker.model.EUserRole;
import com.frankAksenia.jobTracker.model.User;
import com.frankAksenia.jobTracker.repository.UserRepository;

@Service 
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User getUserByEmailOrThrow(String userEmail) {
        return userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Resource not found", "User with the given email does not exist."));
    }

    public boolean isHrSpecialist(User user) {
        return user.getUserRole() == EUserRole.HR_SPECIALIST;
    }
}
