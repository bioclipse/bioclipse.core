package net.bioclipse.jseditor.editors;

import org.eclipse.jface.text.rules.IWordDetector;

/*
 * This file is part of the Bioclipse Javascript Editor Plug-in.
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
public class CommonWordDetector implements IWordDetector {

	//private final String abcLetter = "abcdefghijklmnopqrstuvwxyz";
	private char abcArray[] = {'a','b','c','d','e','f',
								'g','h','i','j','k','l',
								'm','n','o','p','q','r',
								's','t','u','v','w','x',
								'y','z',
								'A','B','C','D','E','F',
								'G','H','I','J','K','L',
								'M','N','O','P','Q','R',
								'S','T','U','V','W','X',
								'Y','Z'};
	// set the charset section....
	public boolean isWordPart(char c) {
		for (int i = 0; i<abcArray.length; i++) {
			if (abcArray[i] == c)
				return true;
		}
		return false;
	}

	public boolean isWordStart(char c) {
		for (int i = 0; i<abcArray.length; i++) {
			if (abcArray[i] == c)
				return true;
		}
		return false;
	}
}