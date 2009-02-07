package net.bioclipse.rhino.exceptions;

public class ScriptException extends Exception {
	
	private static final long serialVersionUID = -1536851006065532451L;
	
	public ScriptException() {
		super();
	}

	public ScriptException(String message) {
		super(message);
	}

	public ScriptException(Throwable t) {
		super(t);
	}
}