/*
 * Created on May 20, 2003
 *========================================================================
 * Modifications history
 *========================================================================
 * $Log: JSClassMethodElement.java,v $
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
public class JSClassMethodElement extends JSFunctionElement
{
	public JSClassMethodElement(String aName, String argumentString, int offset, int length)
	{
		super(aName, argumentString, offset, length);
	}
	
	public ImageDescriptor getImageDescriptor(Object object)
	{
		return JSImages.ICON_VIEW_CLASS_METHOD;
	}

	public int category()
	{
		return CLASS_METHOD;	
	}

}
