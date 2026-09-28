package com.frankAksenia.jobTracker.controller;

import org.springframework.web.bind.annotation.RestController;

import com.frankAksenia.jobTracker.service.AuthService;

@RestController 
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;    
    }
    
}
