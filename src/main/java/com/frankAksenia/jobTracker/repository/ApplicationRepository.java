package com.frankAksenia.jobTracker.repository;

import java.util.Optional;
import java.util.UUID;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.frankAksenia.jobTracker.model.EApplicationStatus;
import com.frankAksenia.jobTracker.model.JobApplication;
import com.frankAksenia.jobTracker.model.User;

@Repository 
public interface ApplicationRepository extends JpaRepository<JobApplication, UUID>{

    List<JobApplication> findAllByUser(User user);

    Optional<JobApplication> findByUserAndApplicationId(User user, UUID applicationId);

    List<JobApplication> findByUser_UserIdAndStatus(UUID userId, EApplicationStatus status);
}
