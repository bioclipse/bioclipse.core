package net.bioclipse.jseditor.exceptions;

/*
 * 
 * This file is part of the Bioclipse Javascript Editor Plug-in.
 * 
 * Copyright (C) 2008 Johannes Wagener
 * 
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free Software
 * Foundation; either version 3 of the License, or (at your option) any later
 * version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with
 * this program; if not, see <http://www.gnu.org/licenses>.
 * 
 * @author Johannes Wagener
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