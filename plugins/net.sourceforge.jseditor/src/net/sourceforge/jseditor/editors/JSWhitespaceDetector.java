/*
 * $RCSfile: JSWhitespaceDetector.java,v $
 *
 * Copyright 2002
 * CH-1700 Fribourg, Switzerland
 * All rights reserved.
 *
 *========================================================================
 * Modifications history
 *========================================================================
 * $Log: JSWhitespaceDetector.java,v $
 * Revision 1.1  2003/05/28 15:17:12  agfitzp
 * net.sourceforge.jseditor 0.0.1 code base
 *
 *========================================================================
*/

package net.sourceforge.jseditor.editors;

import org.eclipse.jface.text.rules.IWhitespaceDetector;


/**
 * 
 *
 * @author $Author: agfitzp $, $Date: 2003/05/28 15:17:12 $
 *
 * @version $Revision: 1.1 $
 */
public class JSWhitespaceDetector implements IWhitespaceDetector
{
   /**
    *
    *
    * @param c 
    *
    * @return 
    */
   public boolean isWhitespace(char c)
   {
      return (c == ' ' || c == '\t' || c == '\n' || c == '\r');
   }
}