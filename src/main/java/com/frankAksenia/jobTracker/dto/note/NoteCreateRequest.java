package com.frankAksenia.jobTracker.dto.note;

import jakarta.validation.constraints.NotEmpty;

public record NoteCreateRequest(
    @NotEmpty 
    String content
) 
{}
