/*
 * $RCSfile: JSConfiguration.java,v $
 *
 * Copyright 2002
 * CH-1700 Fribourg, Switzerland
 * All rights reserved.
 *
 *========================================================================
 * Modifications history
 *========================================================================
 * $Log: JSConfiguration.java,v $
 * Revision 1.3  2003/12/10 20:19:16  agfitzp
 * 3.0 port
 *
 * Revision 1.2  2003/06/21 03:48:51  agfitzp
 * fixed global variables as functions bug
 * fixed length calculation of instance variables
 * Automatic outlining is now a preference
 *
 * Revision 1.1  2003/05/28 15:17:12  agfitzp
 * net.sourceforge.jseditor 0.0.1 code base
 *
 *========================================================================
*/

package net.sourceforge.jseditor.editors;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.jface.resource.StringConverter;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITextDoubleClickStrategy;
import org.eclipse.jface.text.TextAttribute;
import org.eclipse.jface.text.presentation.IPresentationReconciler;
import org.eclipse.jface.text.presentation.PresentationReconciler;
import org.eclipse.jface.text.rules.DefaultDamagerRepairer;
import org.eclipse.jface.text.rules.Token;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.jface.text.source.SourceViewerConfiguration;
import org.eclipse.jface.preference.IPreferenceStore;

import net.sourceforge.jseditor.JSEditorPlugin;
import net.sourceforge.jseditor.preferences.PreferenceNames;

/**
 * 
 *
 * @author $Author: agfitzp $, $Date: 2003/12/10 20:19:16 $
 *
 * @version $Revision: 1.3 $
 */
public class JSConfiguration extends SourceViewerConfiguration
{
	private JSDoubleClickStrategy doubleClickStrategy;
	private JSStringScanner stringScanner;
	private JSScanner scanner;
	private JSColorManager colorManager;
	private IPreferenceStore preferences;

	/**
	 * Creates a new JSConfiguration object.
	 *
	 * @param colorManager 
	 */
	public JSConfiguration(JSColorManager colorManager)
	{
		this.colorManager = colorManager;
		this.preferences = JSEditorPlugin.getDefault().getPreferenceStore();
	}
	
	public boolean getAutomaticOutliningPreference()
	{
		return preferences.getBoolean(PreferenceNames.P_AUTO_OUTLINE);	
	}

	protected RGB getColorPreference(String categoryColor)
	{
		String rgbString = preferences.getString(categoryColor);

		if (rgbString.length() <= 0)
		{
			rgbString = preferences.getDefaultString(categoryColor);
			if(rgbString.length() <= 0) 
			{
				rgbString = "0,0,0";
			}
		}
		return StringConverter.asRGB(rgbString);
	}

	public Color getContentColor(String categoryColor)
	{
		return colorManager.getColor(getColorPreference(categoryColor));
	}

	/**
	 *
	 *
	 * @param sourceViewer 
	 *
	 * @return 
	 */
	public String[] getConfiguredContentTypes(ISourceViewer sourceViewer)
	{
		return new String[] {
			IDocument.DEFAULT_CONTENT_TYPE,
			JSPartitionScanner.JS_COMMENT,
			JSPartitionScanner.JS_KEYWORD,
			JSPartitionScanner.JS_STRING };
	}

	/**
	 *
	 *
	 * @param sourceViewer 
	 * @param contentType 
	 *
	 * @return 
	 */
	public ITextDoubleClickStrategy getDoubleClickStrategy(ISourceViewer sourceViewer, String contentType)
	{
		if (doubleClickStrategy == null)
		{
			doubleClickStrategy = new JSDoubleClickStrategy();
		}

		return doubleClickStrategy;
	}

	/**
	 *
	 *
	 * @return 
	 */
	protected JSScanner getJSScanner()
	{
		if (scanner == null)
		{
			Color defaultColor = getContentColor(PreferenceNames.P_DEFAULT_COLOR);
			scanner = new JSScanner(defaultColor);
			scanner.setDefaultReturnToken(new Token(new TextAttribute(defaultColor)));
		}

		return scanner;
	}

	/**
	 *
	 *
	 * @return 
	 */
	protected JSStringScanner getJSStringScanner()
	{
		if (stringScanner == null)
		{
			Color stringColor = getContentColor(PreferenceNames.P_STRING_COLOR);
			stringScanner = new JSStringScanner(stringColor);
			stringScanner.setDefaultReturnToken(new Token(new TextAttribute(stringColor)));
		}

		return stringScanner;
	}

	/**
	 *
	 *
	 * @param sourceViewer 
	 *
	 * @return 
	 */
	public IPresentationReconciler getPresentationReconciler(ISourceViewer sourceViewer)
	{
		PresentationReconciler reconciler = new PresentationReconciler();

		DefaultDamagerRepairer dr = new DefaultDamagerRepairer(getJSScanner());
		reconciler.setDamager(dr, IDocument.DEFAULT_CONTENT_TYPE);
		reconciler.setRepairer(dr, IDocument.DEFAULT_CONTENT_TYPE);

		NonRuleBasedDamagerRepairer commentRepairer =
			new NonRuleBasedDamagerRepairer(new TextAttribute(getContentColor(PreferenceNames.P_COMMENT_COLOR)));
		reconciler.setDamager(commentRepairer, JSPartitionScanner.JS_COMMENT);
		reconciler.setRepairer(commentRepairer, JSPartitionScanner.JS_COMMENT);

		NonRuleBasedDamagerRepairer stringRepairer =
			new NonRuleBasedDamagerRepairer(new TextAttribute(getContentColor(PreferenceNames.P_STRING_COLOR)));
		reconciler.setDamager(stringRepairer, JSPartitionScanner.JS_STRING);
		reconciler.setRepairer(stringRepairer, JSPartitionScanner.JS_STRING);

		NonRuleBasedDamagerRepairer keywordRepairer =
			new NonRuleBasedDamagerRepairer(new TextAttribute(getContentColor(PreferenceNames.P_KEYWORD_COLOR), null, SWT.BOLD));
		reconciler.setDamager(keywordRepairer, JSPartitionScanner.JS_KEYWORD);
		reconciler.setRepairer(keywordRepairer, JSPartitionScanner.JS_KEYWORD);

		return reconciler;
	}
	/**
	 * @return
	 */
	public IPreferenceStore getPreferences() {
		return preferences;
	}

	/**
	 * @param store
	 */
	public void setPreferences(IPreferenceStore store) {
		preferences = store;
	}

}