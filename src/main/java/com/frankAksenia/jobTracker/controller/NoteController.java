package com.frankAksenia.jobTracker.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.frankAksenia.jobTracker.service.NoteService;

@RestController 
@RequestMapping ("api/notes")
public class NoteController {
    
    private final NoteService noteService;

    public NoteController(NoteService noteService) {
        this.noteService = noteService;
    }
}
