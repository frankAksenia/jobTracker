package com.frankAksenia.jobTracker.model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.hibernate.annotations.UuidGenerator;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity 
@NoArgsConstructor 
@AllArgsConstructor 
@Table(name="job_applications")
public class JobApplication {

    @Id 
    @UuidGenerator 
    private UUID application_id;

    private String companyName;

    private String position;

    private String location;

    private String jobUrl;

    private String source;

    @Enumerated(EnumType.STRING)
    private EApplicationStatus application_status;

    private LocalDate applicationDate;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    @ManyToOne (fetch = FetchType.LAZY, optional = false)
    private User user;

    @OneToMany (mappedBy = "jobApplication")
    private List<Interview> interviews = new ArrayList<>();

    @OneToMany (mappedBy = "jobApplication")
    private List<Note> notes = new ArrayList<>();

    @PrePersist 
    private void onCreate() {
        LocalDateTime now = LocalDateTime.now();
        this.createdAt = now;
        this.updatedAt = now;
    }

    @PrePersist 
    private void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
