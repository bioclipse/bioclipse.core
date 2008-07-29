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

import org.eclipse.jface.text.rules.ICharacterScanner;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.MultiLineRule;

/**
 * The TagRule marks words between '<' and '>'.
 * 
 * @author ola
 *
 */
public class TagRule extends MultiLineRule {

    public TagRule(IToken token) {
        super("<", ">", token);  //$NON-NLS-1$//$NON-NLS-2$
    }
    protected boolean sequenceDetected(
        ICharacterScanner scanner,
        char[] sequence,
        boolean eofAllowed) {
        int c = scanner.read();
        if (sequence[0] == '<') {
            if (c == '?') {
                // processing instruction - abort
                scanner.unread();
                return false;
            }
            if (c == '!') {
                scanner.unread();
                // comment - abort
                return false;
            }
        } else if (sequence[0] == '>') {
            scanner.unread();
        }
        return super.sequenceDetected(scanner, sequence, eofAllowed);
    }
}
