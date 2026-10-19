package com.frankAksenia.jobTracker.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import lombok.Getter;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class ResourceNotFoundException extends RuntimeException {
	private static final long serialVersionUID = 1L;
	
    @Getter
	private final String exceptionName;
	
	public ResourceNotFoundException(String exceptionName, String exceptionMessage) {
		super(exceptionMessage);
		this.exceptionName = exceptionName;
	}
}
