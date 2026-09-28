package com.frankAksenia.jobTracker.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.frankAksenia.jobTracker.model.JobApplication;

@Repository 
public interface UserRepository extends JpaRepository<JobApplication, UUID> {

    List<JobApplication> findByUserUd(UUID userID);
    
}
