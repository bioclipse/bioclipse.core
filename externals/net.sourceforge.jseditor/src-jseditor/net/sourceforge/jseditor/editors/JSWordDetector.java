/*
 * Created on May 13, 2003
 *========================================================================
 * Modifications history
 *========================================================================
 * $Log: JSWordDetector.java,v $
 * Revision 1.2  2003/05/30 20:53:09  agfitzp
 * 0.0.2 : Outlining is now done as the user types. Some other bug fixes.
 *
 *========================================================================
 */
package net.sourceforge.jseditor.editors;

/**
 * @author fitzpata
 *
 */

import org.eclipse.jface.text.rules.IWordDetector;

/**
 * A JavaScript aware word detector.
 * JavaScript tokens are almost identical to Java so this
 * class is borrowed from org.eclipse.jdt.internal.ui.text.JavaWordDetector.
 */
public class JSWordDetector implements IWordDetector {

	/**
	 * @see IWordDetector#isWordStart
	 * JavaScript tokens are almost identical to Java so for now
	 * we can just borrow this behavior.
	 */
	public boolean isWordStart(char c) {
		return Character.isJavaIdentifierStart(c);
	}
	
	/**
	 * @see IWordDetector#isWordPart
	 * JavaScript tokens are almost identical to Java so for now
	 * we can just borrow this behavior.
	 */
	public boolean isWordPart(char c) {
		return Character.isJavaIdentifierPart(c);
	}
}
