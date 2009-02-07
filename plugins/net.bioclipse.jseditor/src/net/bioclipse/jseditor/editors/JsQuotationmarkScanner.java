package net.bioclipse.jseditor.editors;

import org.eclipse.jface.text.rules.IRule;
import org.eclipse.jface.text.rules.RuleBasedScanner;
import org.eclipse.jface.text.rules.WhitespaceRule;
/*
 * 
 * This file is part of the Bioclipse Javascript Editor Plug-in.
 * 
 * Copyright (C) 2008 Johannes Wagener
 * 
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free Software
 * Foundation; either version 3 of the License, or (at your option) any later
 * version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with
 * this program; if not, see <http://www.gnu.org/licenses>.
 * 
 * @author Johannes Wagener
 */
public class JsQuotationmarkScanner extends RuleBasedScanner {

	public JsQuotationmarkScanner(JsColorManager colorManager) {
		IRule[] rules = new IRule[1];
		// A generic whitespace rule.
		rules[0] = new WhitespaceRule(new WhitespaceDetector());

		setRules(rules);
	}
}