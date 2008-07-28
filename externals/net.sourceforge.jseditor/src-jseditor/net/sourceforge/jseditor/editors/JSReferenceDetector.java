/*
 * Created on May 14, 2003
 *========================================================================
 * Modifications history
 *========================================================================
 * $Log: JSReferenceDetector.java,v $
 * Revision 1.2  2003/05/30 20:53:09  agfitzp
 * 0.0.2 : Outlining is now done as the user types. Some other bug fixes.
 *
 *========================================================================
 */
package net.sourceforge.jseditor.editors;

/**
 * @author fitzpata
 */
public class JSReferenceDetector extends JSWordDetector {
	/**
	 * @see IWordDetector#isWordStart
	 * Try to detect tokens starting with a reference operator.
	 */
	public boolean isWordStart(char c) {
		return (c == '.');
	}
}
