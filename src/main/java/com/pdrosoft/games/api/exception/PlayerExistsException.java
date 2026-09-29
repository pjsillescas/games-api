package com.pdrosoft.games.api.exception;

public class PlayerExistsException extends RuntimeException {

	private static final long serialVersionUID = -3541245907911664980L;

	public PlayerExistsException(String message) {
		super(message);
	}
}