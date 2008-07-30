/*
 * $RCSfile: JSScanner.java,v $
 *
 * Copyright 2002
 * CH-1700 Fribourg, Switzerland
 * All rights reserved.
 *
 *========================================================================
 * Modifications history
 *========================================================================
 * $Log: JSScanner.java,v $
 * Revision 1.1  2003/05/28 15:17:12  agfitzp
 * net.sourceforge.jseditor 0.0.1 code base
 *
 *========================================================================
*/

package net.sourceforge.jseditor.editors;

import java.util.*;
import org.eclipse.jface.text.rules.*;
import org.eclipse.jface.text.*;

import org.eclipse.swt.graphics.Color;

/**
 * 
 *
 * @author $Author: agfitzp $, $Date: 2003/05/28 15:17:12 $
 *
 * @version $Revision: 1.1 $
 */
public class JSScanner extends RuleBasedScanner
{
   /**
    * Creates a new JSScanner object.
    *
    * @param manager 
    */
   public JSScanner(Color aColor)
   {
      List rules = new ArrayList();
      IToken procInstr = new Token(new TextAttribute(aColor));

      // Add generic whitespace rule.
      rules.add(new WhitespaceRule(new JSWhitespaceDetector()));

      IRule[] result = new IRule[rules.size()];
      rules.toArray(result);
      setRules(result);
   }

}