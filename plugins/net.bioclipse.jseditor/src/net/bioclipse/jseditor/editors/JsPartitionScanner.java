package net.bioclipse.jseditor.editors;

import org.eclipse.jface.text.rules.EndOfLineRule;
import org.eclipse.jface.text.rules.IPredicateRule;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.MultiLineRule;
import org.eclipse.jface.text.rules.RuleBasedPartitionScanner;
import org.eclipse.jface.text.rules.SingleLineRule;
import org.eclipse.jface.text.rules.Token;
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
public class JsPartitionScanner extends RuleBasedPartitionScanner {

	public JsPartitionScanner() {

		IToken xmlQuotationmarkLine = new Token(JsEditorConstants.QUOTATIONMARK_LINE);
		IToken xmlCommentLine = new Token(JsEditorConstants.COMMENT_LINE);
		IToken xmlCommentSection = new Token(JsEditorConstants.COMMENT_SECTION);

		IPredicateRule[] rules = new IPredicateRule[3];

		
		rules[0] = new SingleLineRule("\"", "\"", xmlQuotationmarkLine, '\\');
		rules[1] = new EndOfLineRule("//", xmlCommentLine);
		rules[2] = new MultiLineRule("/*", "*/", xmlCommentSection);

		setPredicateRules(rules);
	}
}
