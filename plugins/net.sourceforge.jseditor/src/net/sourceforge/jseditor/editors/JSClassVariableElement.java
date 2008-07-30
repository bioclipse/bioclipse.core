/*
 * Created on May 20, 2003
 *========================================================================
 * Modifications history
 *========================================================================
 * $Log: JSClassVariableElement.java,v $
 * Revision 1.2  2003/05/30 20:53:09  agfitzp
 * 0.0.2 : Outlining is now done as the user types. Some other bug fixes.
 *
 *========================================================================
 */
package net.sourceforge.jseditor.editors;

import org.eclipse.jface.resource.ImageDescriptor;

/**
 * @author fitzpata
 *
 */
public class JSClassVariableElement extends JSElement
{

	/**
	 * @param aName
	 * @param offset
	 * @param length
	 */
	public JSClassVariableElement(String aName, int offset, int length)
	{
		super(aName, offset, length);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.model.IWorkbenchAdapter#getImageDescriptor(java.lang.Object)
	 */
	public ImageDescriptor getImageDescriptor(Object object)
	{
		return JSImages.ICON_VIEW_CLASS_VARIABLE;
	}

	public int category()
	{
		return CLASS_VARIABLE;	
	}

}
