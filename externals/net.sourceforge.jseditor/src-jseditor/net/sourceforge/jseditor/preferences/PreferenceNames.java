/*
 * Created on May 15, 2003
 *========================================================================
 * Modifications history
 *========================================================================
 * $Log: PreferenceNames.java,v $
 * Revision 1.4  2003/12/10 20:19:16  agfitzp
 * 3.0 port
 *
 * Revision 1.3  2003/06/21 03:48:51  agfitzp
 * fixed global variables as functions bug
 * fixed length calculation of instance variables
 * Automatic outlining is now a preference
 *
 * Revision 1.2  2003/05/30 20:53:08  agfitzp
 * 0.0.2 : Outlining is now done as the user types. Some other bug fixes.
 *
 *========================================================================
 */
package net.sourceforge.jseditor.preferences;

/**
 * Publicly available list of preference names as constants.
 * @author fitzpata
 */
public interface PreferenceNames
{
	String P_COMMENT_COLOR = "commentColor";
	String P_STRING_COLOR = "stringColor";
	String P_KEYWORD_COLOR = "keywordColor";
	String P_DEFAULT_COLOR = "defaultColor";
	String P_AUTO_OUTLINE = "autoOutline";
}
