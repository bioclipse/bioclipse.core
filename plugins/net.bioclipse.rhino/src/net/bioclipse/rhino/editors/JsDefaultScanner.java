package net.bioclipse.rhino.editors;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.text.TextAttribute;
import org.eclipse.jface.text.rules.IRule;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.RuleBasedScanner;
import org.eclipse.jface.text.rules.SingleLineRule;
import org.eclipse.jface.text.rules.Token;
import org.eclipse.jface.text.rules.WhitespaceRule;
import org.eclipse.jface.text.rules.WordRule;
import org.eclipse.swt.SWT;
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
public class JsDefaultScanner extends RuleBasedScanner {
	
	private List<IRule> rules = new ArrayList<IRule>();
	
	public JsDefaultScanner(JsColorManager colorManager) {
		
		IToken quotationmark_token = new Token(
				new TextAttribute(
						colorManager.getColor(
								JsEditorConstants.COLOR_QUOTATIONMARK)));
		IToken var_token = new Token(
				new TextAttribute(
						colorManager.getColor(
								JsEditorConstants.COLOR_VAR),
								null,
								SWT.BOLD));
		IToken statements_token = new Token(
				new TextAttribute(
						colorManager.getColor(
								JsEditorConstants.COLOR_STATEMENTS),
								null,
								SWT.BOLD));
		IToken literals_token = new Token(
				new TextAttribute(
								null,
								null,
								SWT.BOLD));
		
		// Quotation mark rules with escape character '\'
		rules.add(new SingleLineRule("\"", "\"", quotationmark_token, '\\'));

		CommonWordDetector worddetector = new CommonWordDetector();
		WordRule wordRule = new WordRule(worddetector, Token.WHITESPACE);
		//WordRule wordRule = new WordRule(worddetector, global_token);
		// 'var', 'new', function rule		
		wordRule.addWord("var", var_token);
		wordRule.addWord("new", var_token);
		wordRule.addWord("function", var_token);
		
		// Ecma-262 7.5.2 Keywords
		wordRule.addWord("break", statements_token);
		wordRule.addWord("case", statements_token);
		wordRule.addWord("catch", statements_token);
		wordRule.addWord("continue", statements_token);
		wordRule.addWord("default", statements_token);
		wordRule.addWord("delete", statements_token);
		wordRule.addWord("do", statements_token);
		wordRule.addWord("else", statements_token);
		wordRule.addWord("finally", statements_token);
		wordRule.addWord("for", statements_token);
		/*wordRule.addWord("function", statements_token);*/
		wordRule.addWord("if", statements_token);
		wordRule.addWord("in", statements_token);
		wordRule.addWord("instanceof", statements_token);
		/*wordRule.addWord("new", statements_token);*/
		wordRule.addWord("return", statements_token);
		wordRule.addWord("switch", statements_token);
		wordRule.addWord("this", statements_token);
		wordRule.addWord("throw", statements_token);
		wordRule.addWord("try", statements_token);
		/*wordRule.addWord("var", statements_token);*/
		wordRule.addWord("void", statements_token);
		wordRule.addWord("while", statements_token);
		wordRule.addWord("with", statements_token);
		
		// Ecma-262 7.5.3 Future Reserved Words
		wordRule.addWord("abstract", statements_token);
		wordRule.addWord("boolean", statements_token);
		wordRule.addWord("byte", statements_token);
		wordRule.addWord("char", statements_token);
		wordRule.addWord("class", statements_token);
		wordRule.addWord("const", statements_token);
		wordRule.addWord("debugger", statements_token);
		wordRule.addWord("double", statements_token);
		wordRule.addWord("enum", statements_token);
		wordRule.addWord("export", statements_token);
		wordRule.addWord("extends", statements_token);
		wordRule.addWord("final", statements_token);
		wordRule.addWord("float", statements_token);
		wordRule.addWord("goto", statements_token);
		wordRule.addWord("implements", statements_token);
		wordRule.addWord("import", statements_token);
		wordRule.addWord("int", statements_token);
		wordRule.addWord("interface", statements_token);
		wordRule.addWord("long", statements_token);
		wordRule.addWord("native", statements_token);
		wordRule.addWord("package", statements_token);
		wordRule.addWord("private", statements_token);
		wordRule.addWord("protected", statements_token);
		wordRule.addWord("public", statements_token);
		wordRule.addWord("short", statements_token);
		wordRule.addWord("static", statements_token);
		wordRule.addWord("super", statements_token);
		wordRule.addWord("synchronized", statements_token);
		wordRule.addWord("throws", statements_token);
		wordRule.addWord("transient", statements_token);
		wordRule.addWord("volatile", statements_token);
	
		// Ecma-262 7.8 Literals
		wordRule.addWord("null", literals_token);
		wordRule.addWord("true", literals_token);
		wordRule.addWord("false", literals_token);
		wordRule.addWord("e", literals_token);
		wordRule.addWord("E", literals_token);
		wordRule.addWord("0x", literals_token);
		wordRule.addWord("0X", literals_token);
		
		rules.add(wordRule);
		
		
		
		//new WordPatternRule()
		
		// A generic whitespace rule.
		rules.add(new WhitespaceRule(new WhitespaceDetector()));

		IRule[] rules_array = new IRule[rules.size()];
		rules.toArray(rules_array);
		
		setRules(rules_array);
	}
}
