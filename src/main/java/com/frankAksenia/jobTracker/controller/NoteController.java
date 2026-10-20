package com.frankAksenia.jobTracker.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.frankAksenia.jobTracker.dto.note.NoteCreateRequest;
import com.frankAksenia.jobTracker.dto.note.NoteUpdateRequest;
import com.frankAksenia.jobTracker.service.NoteService;

import jakarta.validation.Valid;

@RestController 
@RequestMapping("/api")
public class NoteController {

    private final NoteService noteService;

    public NoteController(NoteService noteService) {
        this.noteService = noteService;
    }

    @GetMapping("/applications/{applicationId}/notes")
    public ResponseEntity<List<String>> getNotesByApplicationId(
        @PathVariable UUID applicationId,
        @AuthenticationPrincipal UserDetails loggedInUser
    ) {
        List<String> response = noteService.getNotesByApplicationId(applicationId, loggedInUser);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PostMapping("/applications/{applicationId}/notes")
    public ResponseEntity<String> createNote(
        @PathVariable UUID applicationId,
        @Valid @RequestBody NoteCreateRequest request,
        @AuthenticationPrincipal UserDetails loggedInUser
    ) {
            String response = noteService.createNote(applicationId, request, loggedInUser);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        }

    @PutMapping("/notes/{noteId}")
    public ResponseEntity<String> updateNote(
        @PathVariable UUID noteId,
        @Valid @RequestBody NoteUpdateRequest request,
        @AuthenticationPrincipal UserDetails loggedInUser
    ) {
        String response = noteService.updateNote(noteId, request, loggedInUser);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/notes/{noteId}")
    public ResponseEntity<Void> deleteNoteById(
        @PathVariable UUID noteId,
        @AuthenticationPrincipal UserDetails loggedInUser
    ) {
        this.noteService.deleteNote(noteId, loggedInUser);
        return ResponseEntity.noContent().build();
    }

}
