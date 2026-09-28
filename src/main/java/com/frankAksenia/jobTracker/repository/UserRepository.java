package com.frankAksenia.jobTracker.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.frankAksenia.jobTracker.model.JobApplication;
import com.frankAksenia.jobTracker.model.User;

@Repository 
public interface UserRepository extends JpaRepository<User, UUID> {

    List<JobApplication> findByUserUd(UUID userID);
    
}
