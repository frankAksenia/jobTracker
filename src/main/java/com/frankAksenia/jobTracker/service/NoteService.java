package com.frankAksenia.jobTracker.service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.frankAksenia.jobTracker.dto.note.NoteCreateRequest;
import com.frankAksenia.jobTracker.dto.note.NoteUpdateRequest;
import com.frankAksenia.jobTracker.exception.ResourceNotFoundException;
import com.frankAksenia.jobTracker.model.JobApplication;
import com.frankAksenia.jobTracker.model.Note;
import com.frankAksenia.jobTracker.model.User;
import com.frankAksenia.jobTracker.repository.ApplicationRepository;
import com.frankAksenia.jobTracker.repository.NoteRepository;


@Service 
public class NoteService {

    private final NoteRepository noteRepository;

    private final ApplicationRepository applicationRepository;

    private final UserService userService;

    public NoteService(NoteRepository noteRepository, UserService userService, ApplicationRepository applicationRepository) {
        this.noteRepository = noteRepository;
        this.applicationRepository = applicationRepository;
        this.userService = userService;
    }

    public String createNote(UUID applicationId, NoteCreateRequest request, UserDetails loggedInUser) {

        requireHrSpecialist(loggedInUser.getUsername());

        JobApplication application = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new ResourceNotFoundException("Resource not found", "Applcation with provided ID does not exist."));

        Note newNote = new Note();

        newNote.setContent(request.content());
        newNote.setJobApplication(application);

        Note createdNote = noteRepository.save(newNote);

        return createdNote.getContent();
    }
    
    public String updateNote(UUID noteId, NoteUpdateRequest request, UserDetails loggedInUser) {

        requireHrSpecialist(loggedInUser.getUsername());

        Note noteToUpdate = noteRepository.findById(noteId)
            .orElseThrow(() -> new ResourceNotFoundException("Resource not found", "Note with provided ID does not exist."));

        noteToUpdate.setContent(request.content());

        Note updatedNote = noteRepository.save(noteToUpdate);

        return updatedNote.getContent();
    }

    private void requireHrSpecialist(String username) {
        if (!userService.isHrSpecialist(userService.getUserByEmailOrThrow(username))) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Only HR specialists can manage notes");
        }
    }

    public List<String> getNotesByApplicationId(UUID applicationId, UserDetails loggedInUser) {
        User currentUser = userService.getUserByEmailOrThrow(loggedInUser.getUsername());

        JobApplication application = userService.isHrSpecialist(currentUser)
                ? applicationRepository.findById(applicationId)
                    .orElseThrow(() -> new ResourceNotFoundException("Resource not found", "Job application not found"))
                : applicationRepository.findByUserAndApplicationId(currentUser, applicationId)
                .orElseThrow(() -> new ResourceNotFoundException("Resource not found", "Job application not found"));

        return noteRepository.findAllByJobApplication(application)
            .stream()
            .map(Note::getContent)
            .collect(Collectors.toList());
    }

    public void deleteNote(UUID noteId, UserDetails loggedInUser) {
        User user = userService.getUserByEmailOrThrow(loggedInUser.getUsername());
        Note note = this.noteRepository.findById(noteId)
            .orElseThrow(() -> new ResourceNotFoundException("Resource not found", ""));
        if (this.userService.isHrSpecialist(user)) {
            this.noteRepository.delete(note);
        } else {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Only HR Specialists can delete notes");
        }
    }
}
