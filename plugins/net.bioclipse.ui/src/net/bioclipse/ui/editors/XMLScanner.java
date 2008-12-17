/*******************************************************************************
 *Copyright (c) 2008 The Bioclipse Team and others.
 *All rights reserved. This program and the accompanying materials
 *are made available under the terms of the Eclipse Public License v1.0
 *which accompanies this distribution, and is available at
 *http://www.eclipse.org/legal/epl-v10.html
 *
 *******************************************************************************/
package net.bioclipse.ui.editors;
import org.eclipse.jface.text.rules.*;
import org.eclipse.jface.text.*;
public class XMLScanner extends RuleBasedScanner {
    public XMLScanner(ColorManager manager) {
        IToken procInstr =
            new Token(
                new TextAttribute(
                    manager.getColor(IXMLColorConstants.PROC_INSTR)));
        IRule[] rules = new IRule[2];
        //Add rule for processing instructions
        rules[0] = new SingleLineRule("<?", "?>", procInstr);
        // Add generic whitespace rule.
        rules[1] = new WhitespaceRule(new XMLWhitespaceDetector());
        setRules(rules);
    }
}
