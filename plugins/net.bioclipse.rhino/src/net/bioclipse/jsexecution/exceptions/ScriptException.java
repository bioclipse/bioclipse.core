package net.bioclipse.jsexecution.exceptions;

/*
 * This file is part of the Bioclipse JsExecution Plug-in.
 * 
 * Copyright (c) 2008 Johannes Wagener.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Johannes Wagener - initial API and implementation
 */
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