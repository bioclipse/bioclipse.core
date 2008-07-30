/*
 * $RCSfile: JSNameSorter.java,v $
 *
 * Copyright 2002
 * CH-1700 Fribourg, Switzerland
 * All rights reserved.
 *
 *========================================================================
 * Modifications history
 *========================================================================
 * $Log: JSNameSorter.java,v $
 * Revision 1.1  2003/05/28 15:17:11  agfitzp
 * net.sourceforge.jseditor 0.0.1 code base
 *
 *========================================================================
*/

package net.sourceforge.jseditor.views;

import org.eclipse.jface.viewers.ViewerSorter;
import net.sourceforge.jseditor.editors.JSElement;


/**
 * Name sorter
 *
 * @author $Author: agfitzp $, $Date: 2003/05/28 15:17:11 $
 *
 * @version $Revision: 1.1 $
 */
class JSNameSorter extends ViewerSorter
{

	/**
	 * Returns the category of the given element. The category is a
	 * number used to allocate elements to bins; the bins are arranged
	 * in ascending numeric order. The elements within a bin are arranged
	 * via a second level sort criterion.
	 * @param element the element
	 * @return the category
	 */
	public int category(Object element) {
		return ((JSElement) element).category();
	}
}