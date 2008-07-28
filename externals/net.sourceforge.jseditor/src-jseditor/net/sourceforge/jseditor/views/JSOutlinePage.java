/*
 * $RCSfile: JSOutlinePage.java,v $
 *
 * Copyright 2002
 * CH-1700 Fribourg, Switzerland
 * All rights reserved.
 *
 *========================================================================
 * Modifications history
 *========================================================================
 * $Log: JSOutlinePage.java,v $
 * Revision 1.3  2003/05/30 20:53:08  agfitzp
 * 0.0.2 : Outlining is now done as the user types. Some other bug fixes.
 *
 * Revision 1.2  2003/05/28 20:47:57  agfitzp
 * Outline the document, not the file.
 *
 * Revision 1.1  2003/05/28 15:17:11  agfitzp
 * net.sourceforge.jseditor 0.0.1 code base
 *
 *========================================================================
*/

package net.sourceforge.jseditor.views;

import net.sourceforge.jseditor.editors.JSElement;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.model.WorkbenchContentProvider;
import org.eclipse.ui.model.WorkbenchLabelProvider;
import org.eclipse.ui.views.contentoutline.ContentOutlinePage;

/**
 * DOCUMENT ME!
 * 
 * @author Addi To change this generated comment edit the template variable "typecomment":
 *         Window>Preferences>Java>Templates. To enable and disable the creation of type comments
 *         go to Window>Preferences>Java>Code Generation.
 */
public class JSOutlinePage extends ContentOutlinePage
{
	protected IDocument input;

	/**
	 * Creates a new JSOutlinePage.
	 * @param input
	 */
	public JSOutlinePage(IDocument input)
	{
		super();
		this.input = input;
	}

	/**
	 * Creates the control and registers the popup menu for this outlinePage Menu id
	 * "org.eclipse.ui.examples.readmetool.outline"
	 * @param parent
	 */
	public void createControl(Composite parent)
	{
		super.createControl(parent);

		TreeViewer viewer = getTreeViewer();
		viewer.setContentProvider(new WorkbenchContentProvider());
		viewer.setLabelProvider(new WorkbenchLabelProvider());

		//TODO: Re-evaluate do we need to set the input here now that update is triggered by the content change?
		//     viewer.setInput(getContentOutline(input));
		viewer.setSorter(new JSNameSorter());
	}

	/**
		* Gets the content outline for a given input element. Returns the outline or null if the outline could not be generated.
		* @param input
		* 
		* @return
		*/
	private IAdaptable getContentOutline(IDocument input)
	{
		return JSSyntaxModelFactory.getInstance().getContentOutline(input);
	}

	/**
	 * Forces the outlinePage to update its contents.
	 * 
	 */
	public void update()
	{
		getControl().setRedraw(false);
		TreeViewer viewer = getTreeViewer();
		
		Object[] expanded =  viewer.getExpandedElements();
		JSElementList currentNodes = (JSElementList) getContentOutline(input); 		
		viewer.setInput(currentNodes);

		/*Is automatically expanding the tree helpful? Should this be a preference?
		 * Or should we only expand those nodes that are already expanded?
		 */
		//      getTreeViewer().expandAll();

		//How about just expanding the root if it's alone?
		if(currentNodes.size() == 1) {
			getTreeViewer().expandAll();
		}
		
		
		//Attempt to determine which nodes are already expanded bearing in mind that the object is not the same.
		for(int i= 0; i< expanded.length; i++)
		{
			JSElement newExpandedNode = currentNodes.findEquivilent((JSElement)expanded[i]);
			if(newExpandedNode != null)
			{
				viewer.setExpandedState(newExpandedNode, true);
			}
		}
		
		getControl().setRedraw(true);
	}
}
