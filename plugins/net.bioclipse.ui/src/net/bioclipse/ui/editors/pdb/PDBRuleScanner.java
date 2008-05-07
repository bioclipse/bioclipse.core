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

package net.bioclipse.ui.editors.pdb;

import java.util.ArrayList;

import net.bioclipse.ui.editors.keyword.KeywordWhitespaceDetector;

import org.eclipse.jface.text.TextAttribute;
import org.eclipse.jface.text.rules.EndOfLineRule;
import org.eclipse.jface.text.rules.IPredicateRule;
import org.eclipse.jface.text.rules.IRule;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.NumberRule;
import org.eclipse.jface.text.rules.RuleBasedScanner;
import org.eclipse.jface.text.rules.SingleLineRule;
import org.eclipse.jface.text.rules.Token;
import org.eclipse.jface.text.rules.WhitespaceRule;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Display;

/**
 * RuleScanner for the PDBEditor
 * 
 * @author ola
 *
 */

public class PDBRuleScanner extends RuleBasedScanner {
    protected static Color DEFAULT_COLOR= new Color(Display.getCurrent(), new RGB(0, 0, 0));
    protected static Color HEADER_COLOR= new Color(Display.getCurrent(), new RGB( 150,0 ,150));
    protected static Color COMMENT_COLOR= new Color(Display.getCurrent(), new RGB(0, 100, 0));
    protected static Color DATA_COLOR= new Color(Display.getCurrent(), new RGB(75, 75, 200));
    protected static Color DATAHET_COLOR= new Color(Display.getCurrent(), new RGB(200, 75, 200));
    protected static Color DATASECONDARY_COLOR= new Color(Display.getCurrent(), new RGB(100, 200, 200));
    protected static Color DATAATOM_COLOR= new Color(Display.getCurrent(), new RGB(50, 100, 150));
    protected static Color DATABOND_COLOR= DATAATOM_COLOR;
    
    
    public PDBRuleScanner(PDBKeywords keywords) {
        
        IToken headerToken = new Token(new TextAttribute(HEADER_COLOR, null, SWT.BOLD));
        IToken commentToken= new Token(new TextAttribute(COMMENT_COLOR, null, SWT.ITALIC));
        IToken dataToken= new Token(new TextAttribute(DATA_COLOR, null, SWT.NORMAL));
        IToken dataHetToken= new Token(new TextAttribute(DATAHET_COLOR, null, SWT.NORMAL));
        IToken dataSecondaryToken= new Token(new TextAttribute(DATASECONDARY_COLOR, null, SWT.NORMAL));
        IToken dataAtomToken= new Token(new TextAttribute(DATAATOM_COLOR, null, SWT.NORMAL));
        IToken dataBondToken= new Token(new TextAttribute(DATABOND_COLOR, null, SWT.NORMAL));
        IToken numberToken= new Token(new TextAttribute(DEFAULT_COLOR, null, SWT.ITALIC));

//        WordRule keywordRule = new WordRule(new KeywordWordDetector(),defaultToken);
//        SingleLineRule linerule=new SingleLineRule("*", null,keyToken, "#".charAt(0), true);

        
        ArrayList<IPredicateRule> listOfRules=new ArrayList<IPredicateRule>();
        
        //Add all header keywords as line rules
        for (int i = 0; i < keywords.getKeywordsHeader().length; i++) {
            SingleLineRule linerule=new SingleLineRule(keywords.getKeywordsHeader()[i], null,headerToken, "#".charAt(0), true);
            listOfRules.add(linerule);
        }
        
        //Add all comments to rule
        for (int i = 0; i < keywords.getKeywordsComments().length; i++) {
            SingleLineRule linerule=new SingleLineRule(keywords.getKeywordsComments()[i], null,commentToken, "#".charAt(0), true);
            listOfRules.add(linerule);
        }
        
        //Add all data to rule
        for (int i = 0; i < keywords.getKeywordsData().length; i++) {
            SingleLineRule linerule=new SingleLineRule(keywords.getKeywordsData()[i], null,dataToken, "#".charAt(0), true);
            listOfRules.add(linerule);
        }

        //Add all dataHet to rule
        for (int i = 0; i < keywords.getKeywordsDataHet().length; i++) {
            SingleLineRule linerule=new SingleLineRule(keywords.getKeywordsDataHet()[i], null,dataHetToken, "#".charAt(0), true);
            listOfRules.add(linerule);
        }

        //Add all dataSecondary to rule
        for (int i = 0; i < keywords.getKeywordsDataSecondary().length; i++) {
            SingleLineRule linerule=new SingleLineRule(keywords.getKeywordsDataSecondary()[i], null,dataSecondaryToken, "#".charAt(0), true);
            listOfRules.add(linerule);
        }

        //Add all dataAtom to rule
        for (int i = 0; i < keywords.getKeywordsDataAtom().length; i++) {
            SingleLineRule linerule=new SingleLineRule(keywords.getKeywordsDataAtom()[i], null,dataAtomToken, "#".charAt(0), true);
            listOfRules.add(linerule);
        }

        //Add all dataAtom to rule
        for (int i = 0; i < keywords.getKeywordsDataBond().length; i++) {
            SingleLineRule linerule=new SingleLineRule(keywords.getKeywordsDataBond()[i], null,dataBondToken, "#".charAt(0), true);
            listOfRules.add(linerule);
        }

        IRule[] rules = new IRule[listOfRules.size()+3];
        rules[0] = (new EndOfLineRule("#", commentToken));
        rules[1] = new WhitespaceRule(new KeywordWhitespaceDetector());

        for (int i=0; i < listOfRules.size(); i++){
            rules[i+2]=listOfRules.get(i);
        }

        rules[listOfRules.size()+2] = new NumberRule(numberToken);
        
        setRules(rules);
    }
}

