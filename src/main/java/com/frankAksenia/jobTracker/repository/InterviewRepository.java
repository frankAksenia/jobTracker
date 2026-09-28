package com.frankAksenia.jobTracker.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.frankAksenia.jobTracker.model.Interview;

@Repository 
public interface InterviewRepository extends JpaRepository<Interview, UUID> {

    List<Interview> findByInterviewId(UUID interviewId);
    
}
