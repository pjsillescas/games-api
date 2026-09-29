package com.pdrosoft.games.api.exception;

public class NotFoundException extends RuntimeException {

	private static final long serialVersionUID = -3541245907911664980L;

	public NotFoundException(String message) {
		super(message);
	}
}