package com.frankAksenia.jobTracker.exception;

import lombok.Getter;

public class ResourceNotFoundException extends RuntimeException {
	private static final long serialVersionUID = 1L;
	
    @Getter
	private final String exceptionName;
	
	public ResourceNotFoundException(String exceptionName, String exceptionMessage) {
		super(exceptionMessage);
		this.exceptionName = exceptionName;
	}
}