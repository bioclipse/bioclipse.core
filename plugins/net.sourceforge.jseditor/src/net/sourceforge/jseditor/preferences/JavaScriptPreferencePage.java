/*
 * Created on May 20th, 2003
 *========================================================================
 * Modifications history
 *========================================================================
 * $Log: JavaScriptPreferencePage.java,v $
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

import org.eclipse.jface.preference.*;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.ui.IWorkbench;
import net.sourceforge.jseditor.JSEditorPlugin;

/**
 * This class represents a preference page that
 * is contributed to the Preferences dialog. By 
 * subclassing <samp>FieldEditorPreferencePage</samp>, we
 * can use the field support built into JFace that allows
 * us to create a page that is small and knows how to 
 * save, restore and apply itself.
 * <p>
 * This page is used to modify preferences only. They
 * are stored in the preference store that belongs to
 * the main plug-in class. That way, preferences can
 * be accessed directly via the preference store.
 */

public class JavaScriptPreferencePage
	extends FieldEditorPreferencePage
	implements IWorkbenchPreferencePage , PreferenceNames {

	public JavaScriptPreferencePage() {
		super(GRID);
		setPreferenceStore(JSEditorPlugin.getDefault().getPreferenceStore());
		setDescription("Preferences for JavaScript editor.");
	}
	
/**
 * Creates the field editors. Field editors are abstractions of
 * the common GUI blocks needed to manipulate various types
 * of preferences. Each field editor knows how to save and
 * restore itself.
 */

	public void createFieldEditors() {
		addField(new BooleanFieldEditor(P_AUTO_OUTLINE, "&Automatic Outlining", getFieldEditorParent()));
		addField(new ColorFieldEditor(P_COMMENT_COLOR, "&Comment Color:", getFieldEditorParent()));
		addField(new ColorFieldEditor(P_STRING_COLOR, "&String Color:", getFieldEditorParent()));
		addField(new ColorFieldEditor(P_KEYWORD_COLOR, "&Keyword Color:", getFieldEditorParent()));
		addField(new ColorFieldEditor(P_DEFAULT_COLOR, "D&efault Color:", getFieldEditorParent()));
	}
	
	public void init(IWorkbench workbench) {
	}
}