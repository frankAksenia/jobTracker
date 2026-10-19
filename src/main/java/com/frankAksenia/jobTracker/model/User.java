package com.frankAksenia.jobTracker.model;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.hibernate.annotations.UuidGenerator;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity 
@Data 
@NoArgsConstructor 
@Table(name = "users")
public class User {

    @Id 
    @UuidGenerator 
    private UUID userId;

    private String firstName;

    private String lastName;
    
    @Column(nullable = false, unique = true)
    private String email;

    @Column
    @ToString.Exclude
    private String passwordHash;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 32)
    private EUserRole userRole = EUserRole.APPLICANT;

    private LocalDateTime createdAt;

    @OneToMany (mappedBy = "user")
    private List<JobApplication> jobApplications;

    @PrePersist 
    private void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}
