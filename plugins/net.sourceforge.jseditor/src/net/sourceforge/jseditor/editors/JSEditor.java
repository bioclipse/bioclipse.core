/*
 * $RCSfile: JSEditor.java,v $
 *
 * Copyright 2002
 * CH-1700 Fribourg, Switzerland
 * All rights reserved.
 *
 *========================================================================
 * Modifications history
 *========================================================================
 * $Log: JSEditor.java,v $
 * Revision 1.5  2003/08/14 15:14:15  agfitzp
 * Removed thread hack from automatic update
 *
 * Revision 1.4  2003/07/04 17:26:56  agfitzp
 * New hack, update in a new thread only if we're not already in the middle of updating
 *
 * Revision 1.3  2003/06/21 03:48:51  agfitzp
 * fixed global variables as functions bug
 * fixed length calculation of instance variables
 * Automatic outlining is now a preference
 *
 * Revision 1.2  2003/05/28 20:47:58  agfitzp
 * Outline the document, not the file.
 *
 * Revision 1.1  2003/05/28 15:17:12  agfitzp
 * net.sourceforge.jseditor 0.0.1 code base
 *
 *========================================================================
*/

package net.sourceforge.jseditor.editors;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.ui.editors.text.TextEditor;
import org.eclipse.ui.views.contentoutline.IContentOutlinePage;
import net.sourceforge.jseditor.views.JSOutlinePage;

/**
 * DOCUMENT ME!
 * 
 * @version $Revision: 1.5 $
 * @author $Author: agfitzp $, $Date: 2003/08/14 15:14:15 $
 */
public class JSEditor extends TextEditor implements ISelectionChangedListener
{
	protected JSColorManager colorManager = new JSColorManager();
	protected JSOutlinePage outlinePage;
	protected JSConfiguration configuration;
	
	protected boolean updating = false;

	/**
	 * Constructor for SampleEditor.
	 */
	public JSEditor()
	{
		super();
		configuration = new JSConfiguration(colorManager);
		
		setSourceViewerConfiguration(configuration);
		setDocumentProvider(new JSDocumentProvider());
	}

	/**
	 * Method declared on IEditorPart
	 * @param monitor
	 */
	public void doSave(IProgressMonitor monitor)
	{
		super.doSave(monitor);

		if (outlinePage != null)
		{
			outlinePage.update();
		}
	}

	/**
	 *
	 */
	public void dispose()
	{
		colorManager.dispose();
		super.dispose();
	}

	/**
	 * Method declared on IAdaptable
	 * @param key
	 * 
	 * @return
	 */
	public Object getAdapter(Class key)
	{
		if (key.equals(IContentOutlinePage.class))
		{
			IDocument document = getDocumentProvider().getDocument(getEditorInput());

			outlinePage = new JSOutlinePage(document);
			outlinePage.addSelectionChangedListener(this);
			return outlinePage;
		}

		return super.getAdapter(key);
	}
	
	/**
	 * @see org.eclipse.jface.viewers.ISelectionChangedListener#selectionChanged(SelectionChangedEvent)
	 */
	public void selectionChanged(SelectionChangedEvent event)
	{
		if (null != event)
		{
			if (event.getSelection() instanceof IStructuredSelection)
			{
				IStructuredSelection sel = (IStructuredSelection) event.getSelection();
				if (null != sel)
				{
					JSElement fe = (JSElement) sel.getFirstElement();
					if (null != fe)
					{
						selectAndReveal(fe.getStart(), fe.getLength());
					}
				}
			}
		}
	}

	/**
	 * Updates all content dependent actions.
	 * 
	 * This might be a hack: We're trapping this update to ensure that the 
	 * outline is always up to date.
	 */
	protected void updateContentDependentActions()
	{
		super.updateContentDependentActions();
		
		if(!updating)
		{
			if (configuration.getAutomaticOutliningPreference())
			{
				if (outlinePage != null)
				{
					updating = true;
	
					outlinePage.update();
					updating = false;
				}
			}
		}
	}
}