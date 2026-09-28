package com.frankAksenia.jobTracker.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.hibernate.annotations.UuidGenerator;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity 
@Data 
@NoArgsConstructor 
@AllArgsConstructor 
@Table(name = "users")
public class User {

    @Id 
    @UuidGenerator 
    private UUID user_id;

    private String firstName;

    private String lastName;
    
    @Column(nullable = false, unique = true)
    private String email;

    private LocalDateTime createdAt;

    @OneToMany (mappedBy = "user")
    private List<JobApplication> jobApplications = new ArrayList<>();

    @PrePersist 
    private void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}
