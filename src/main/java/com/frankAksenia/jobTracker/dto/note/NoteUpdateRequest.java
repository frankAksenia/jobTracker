package com.frankAksenia.jobTracker.dto.note;

import jakarta.validation.constraints.NotBlank;

public record NoteUpdateRequest(
    @NotBlank 
    String content
) 
{}
