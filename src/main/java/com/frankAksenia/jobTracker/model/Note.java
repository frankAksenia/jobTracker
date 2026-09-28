package com.frankAksenia.jobTracker.model;

import java.time.LocalDateTime;
import java.util.UUID;

import org.hibernate.annotations.UuidGenerator;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data 
@Entity 
@NoArgsConstructor 
@AllArgsConstructor 
@Table (name="notes")
public class Note {
    
    @Id 
    @UuidGenerator 
    private UUID noteId;

    @Column (length = 2500)
    private String content;

    @Column (updatable = false)
    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    @ManyToOne (fetch = FetchType.LAZY, optional = false)
    private JobApplication jobApplication;

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
