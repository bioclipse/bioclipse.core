/*
 * $RCSfile: JSStringScanner.java,v $
 *
 * Copyright 2002
 * CH-1700 Fribourg, Switzerland
 * All rights reserved.
 *
 *========================================================================
 * Modifications history
 *========================================================================
 * $Log: JSStringScanner.java,v $
 * Revision 1.1  2003/05/28 15:17:11  agfitzp
 * net.sourceforge.jseditor 0.0.1 code base
 *
 *========================================================================
*/

package net.sourceforge.jseditor.editors;

import org.eclipse.jface.text.*;
import java.util.*;
import org.eclipse.jface.text.rules.*;

import org.eclipse.swt.graphics.Color;


/**
 * 
 *
 * @author $Author: agfitzp $, $Date: 2003/05/28 15:17:11 $
 *
 * @version $Revision: 1.1 $
 */
public class JSStringScanner extends RuleBasedScanner
{
   /**
    * Creates a new JSFuncScanner object.
    *
    * @param manager 
    */
   public JSStringScanner(Color aColor)
   {
      IToken string = new Token(new TextAttribute(aColor));
      Vector rules = new Vector();

      // Add rule for single and double quotes
      rules.add(new SingleLineRule("\"", "\"", string, '\\'));
      rules.add(new SingleLineRule("'", "'", string, '\\'));


      // Add generic whitespace rule.
      rules.add(new WhitespaceRule(new JSWhitespaceDetector()));

      IRule[] result = new IRule[rules.size()];
      rules.copyInto(result);
      setRules(result);
   }

   /**
    *
    *
    * @return 
    */
   public IToken nextToken()
   {
      return super.nextToken();
   }
}