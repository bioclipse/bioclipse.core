/*
 * Created on May 15, 2003
 *========================================================================
 * Modifications history
 *========================================================================
 * $Log: JSClassElement.java,v $
 * Revision 1.2  2003/05/30 20:53:09  agfitzp
 * 0.0.2 : Outlining is now done as the user types. Some other bug fixes.
 *
 *
 *========================================================================
*/
package net.sourceforge.jseditor.editors;

import java.util.HashMap;
import org.eclipse.jface.resource.ImageDescriptor;

/**
 * @author fitzpata
 *
 */
public class JSClassElement extends JSElement
{
	protected HashMap childrenByName;
	protected boolean isPrototype = false;

	/**
	 * @param aName
	 * @param offset
	 * @param length
	 */
	public JSClassElement(String aName, int offset, int length)
	{
		super(aName, offset, length);
		childrenByName = new HashMap();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.model.IWorkbenchAdapter#getImageDescriptor(java.lang.Object)
	 */
	public ImageDescriptor getImageDescriptor(Object object)
	{
		if(isPrototype) {
			return JSImages.ICON_VIEW_DYNAMIC_CLASS;
		}
		return JSImages.ICON_VIEW_CLASS;
	}
	
	
	public void addChildElement(JSElement anElement)
	{
		String elementName = anElement.getName();
		if(!childrenByName.containsKey(elementName))
		{
			this.children.add(anElement);
			this.childrenByName.put(elementName, anElement);
			anElement.setParent(this);
		}
	}

	public int category()
	{
		return CLASS;	
	}

	/**
	 * @return
	 */
	public boolean isPrototype()
	{
		return isPrototype;
	}

	/**
	 * @param b
	 */
	public void setPrototype(boolean b)
	{
		isPrototype = b;
	}

}
