/*******************************************************************************
 *Copyright (c) 2008 The Bioclipse Team and others.
 *All rights reserved. This program and the accompanying materials
 *are made available under the terms of the Eclipse Public License v1.0
 *which accompanies this distribution, and is available at
 *http://www.eclipse.org/legal/epl-v10.html
 *
 *******************************************************************************/
package net.bioclipse.ui.editors;
import org.eclipse.jface.text.rules.IWhitespaceDetector;
public class XMLWhitespaceDetector implements IWhitespaceDetector {
    public boolean isWhitespace(char c) {
        return (c == ' ' || c == '\t' || c == '\n' || c == '\r');
    }
}
