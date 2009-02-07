package net.bioclipse.rhino.editors;

import org.eclipse.jface.text.rules.IWordDetector;

/**
 * 
 * This file is part of the Bioclipse Rhino Plug-in.
 * 
 * Copyright (C) 2008 Johannes Wagener
 * 
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation; either version 3 of the License, or (at your option) any later
 * version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program; if not, see <http://www.gnu.org/licenses>.
 * 
 * @author Johannes Wagener
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