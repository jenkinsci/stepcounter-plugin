package org.jenkinsci.plugins.stepcounter.exception;

public class PaserNotFoundException extends RuntimeException {

	private static final long serialVersionUID = 6343748144877203504L;

	public PaserNotFoundException(String message) {
		super(message);
	}

}
