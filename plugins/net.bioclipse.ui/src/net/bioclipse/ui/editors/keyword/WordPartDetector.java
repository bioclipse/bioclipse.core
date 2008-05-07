/*******************************************************************************
 * Copyright (c) 2006 Bioclipse Project
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Ola Spjuth - core API and implementation
 *******************************************************************************/

package net.bioclipse.ui.editors.keyword;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.ITextViewer;

/**
 * The WordPartDetector detects where in the document we are, and returns the offset 
 * and part of the current word where the cursor is.
 * 
 * @author ola
 *
 */
public class WordPartDetector {
    String wordPart = "";
    int docOffset;
    
    public WordPartDetector(ITextViewer viewer, int documentOffset) {
        docOffset = documentOffset - 1;        
        try {
            while (((docOffset) >= viewer.getTopIndexStartOffset())   && 
                    (Character.isLetterOrDigit(viewer.getDocument().getChar(docOffset)) ||
                            viewer.getDocument().getChar(docOffset) == '_')) {
                docOffset--;
            }
            //we've been one step too far : increase the offset
            docOffset++;
            wordPart = viewer.getDocument().get(docOffset, documentOffset - docOffset);
        } catch (BadLocationException e) {
            // do nothing
        }
    }
    public String getString() {
        return wordPart;
    }
    
    public int getOffset() {
        return docOffset;
    }

}

