/*
 * $RCSfile: JSElement.java,v $
 *
 * Copyright 2002
 * CH-1700 Fribourg, Switzerland
 * All rights reserved.
 *
 *========================================================================
 * Modifications history
 *========================================================================
 * $Log: JSElement.java,v $
 * Revision 1.2  2003/05/30 20:53:08  agfitzp
 * 0.0.2 : Outlining is now done as the user types. Some other bug fixes.
 *
 * Revision 1.1  2003/05/28 15:17:12  agfitzp
 * net.sourceforge.jseditor 0.0.1 code base
 *
 *========================================================================
*/

package net.sourceforge.jseditor.editors;

import java.util.List;
import java.util.LinkedList;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.ui.model.IWorkbenchAdapter;

/**
 * DOCUMENT ME!
 * 
 * @author Addi 
 */
abstract public class JSElement implements IWorkbenchAdapter, IAdaptable, JSElementCategories
{
	protected String name;
	protected int offset;
	protected int numberOfLines;
	protected int length;

	protected JSElement parent;
	protected List children;

	/**
	 * Creates a new JSElement and stores parent element and location in the text.
	 * 
	 * @param aName text corresponding to the func
	 * @param offset  the offset into the Readme text
	 * @param length  the length of the element
	 */
	public JSElement(String aName, int offset, int length)
	{
		this.name = aName;
		this.offset = offset;
		this.length = length;
		this.children = new LinkedList();
	}

	/**
	 * Method declared on IAdaptable
	 * @param adapter
	 * 
	 * @return
	 */
	public Object getAdapter(Class adapter)
	{
		if (adapter == IWorkbenchAdapter.class)
		{
			return this;
		}

		return null;
	}

	/**
	 * Method declared on IWorkbenchAdapter
	 * @param o
	 * 
	 * @return
	 */
	public String getLabel(Object o)
	{
		return name;
	}

	/**
	 * Returns the number of characters in this section.
	 * @return
	 */
	public int getLength()
	{
		return length;
	}

	/**
	 * Returns the number of lines in the element.
	 * 
	 * @return the number of lines in the element
	 */
	public int getNumberOfLines()
	{
		return numberOfLines;
	}

	/**
	 * Returns the offset of this section in the file.
	 * @return
	 */
	public int getStart()
	{
		return offset;
	}

	/**
	 * Sets the number of lines in the element
	 * 
	 * @param newNumberOfLines  the number of lines in the element
	 */
	public void setNumberOfLines(int newNumberOfLines)
	{
		numberOfLines = newNumberOfLines;
	}

	/**
	 * @see java.lang.Object#toString()
	 */
	public String toString()
	{
		return getLabel(this);
	}

	/**
	 * @see org.eclipse.ui.model.IWorkbenchAdapter#getChildren(Object)
	 */
	public Object[] getChildren(Object o)
	{
		Object[] result = new Object[children.size()];
		return children.toArray(result);
	}

	/**
	 * @see org.eclipse.ui.model.IWorkbenchAdapter#getParent(Object)
	 */
	public Object getParent(Object o)
	{
		return null;
	}
	
	/**
	 * 
	 * @return A category enumeration for sub-types.
	 */
	abstract public int category();

	/**
	 * @return
	 */
	public String getName()
	{
		return name;
	}

	/**
	 * @return
	 */
	public int getOffset()
	{
		return offset;
	}

	/**
	 * @return
	 */
	public JSElement getParent()
	{
		return parent;
	}

	/**
	 * @param element
	 */
	protected void setParent(JSElement element)
	{
		parent = element;
	}

	public boolean sharesParentWith(JSElement anElement)
	{
		if(parent == null) {
			return anElement.getParent() == null;
		}
		
		return parent.equals(anElement.getParent());
	}

	public boolean equals(JSElement anElement)
	{
		return sharesParentWith(anElement) && name.equals(anElement.getName());
	}

}