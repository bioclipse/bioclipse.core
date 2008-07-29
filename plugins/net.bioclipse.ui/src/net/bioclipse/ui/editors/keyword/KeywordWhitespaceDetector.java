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

import org.eclipse.jface.text.rules.IWhitespaceDetector;

/**
 * If whitespace, return true
 * 
 * @author ola
 *
 */
public class KeywordWhitespaceDetector implements IWhitespaceDetector {

    /**
     * @return true if whitespace
     */
    public boolean isWhitespace(char c) {
        return Character.isWhitespace(c);
        //return (c == ' ' || c == '\t' || c == '\n' || c == '\r');
    }
}
