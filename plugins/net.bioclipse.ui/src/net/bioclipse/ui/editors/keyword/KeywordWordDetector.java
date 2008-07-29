/*******************************************************************************
 * Copyright (c) 2006, 2008 Bioclipse Project
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Ola Spjuth - core API and implementation
 *******************************************************************************/
package net.bioclipse.ui.editors.keyword;

import org.eclipse.jface.text.rules.IWordDetector;

/**
 * 
 * Marks as a word if character is identified as first in a new word.
 * 
 * @author ola
 */
public class KeywordWordDetector implements IWordDetector {

    
    public boolean isWordStart(char c) {
        return Character.isJavaIdentifierStart(c);
    }
    public boolean isWordPart(char c) {
        return Character.isJavaIdentifierPart(c);
    }

}
