package com.frankAksenia.jobTracker.service;

import org.springframework.stereotype.Service;

import com.frankAksenia.jobTracker.repository.NoteRepository;

@Service 
public class NoteService {

    private final NoteRepository noteRepository;

    public NoteService(NoteRepository noteRepository) {
        this.noteRepository = noteRepository;
    }
    
}
