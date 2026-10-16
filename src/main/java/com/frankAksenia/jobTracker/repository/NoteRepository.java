package com.frankAksenia.jobTracker.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.frankAksenia.jobTracker.model.Note;
import com.frankAksenia.jobTracker.model.JobApplication;

@Repository 
public interface NoteRepository extends JpaRepository<Note, UUID> {

    List<Note> findByNoteId(UUID noteId);

    List<Note> findAllByJobApplication(JobApplication jobApplication);
    
}
