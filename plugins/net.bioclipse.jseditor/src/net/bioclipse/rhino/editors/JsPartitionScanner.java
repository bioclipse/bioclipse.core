package net.bioclipse.rhino.editors;

import org.eclipse.jface.text.rules.EndOfLineRule;
import org.eclipse.jface.text.rules.IPredicateRule;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.MultiLineRule;
import org.eclipse.jface.text.rules.RuleBasedPartitionScanner;
import org.eclipse.jface.text.rules.Token;
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
public class JsPartitionScanner extends RuleBasedPartitionScanner {

	public JsPartitionScanner() {

		IToken xmlCommentLine = new Token(JsEditorConstants.COMMENT_LINE);
		IToken xmlCommentSection = new Token(JsEditorConstants.COMMENT_SECTION);

		IPredicateRule[] rules = new IPredicateRule[2];

		rules[0] = new EndOfLineRule("//", xmlCommentLine);
		rules[1] = new MultiLineRule("/*", "*/", xmlCommentSection);

		setPredicateRules(rules);
	}
}
