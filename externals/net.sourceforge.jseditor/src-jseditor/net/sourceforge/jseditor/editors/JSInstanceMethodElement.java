/*
 * Created on May 20, 2003
 *========================================================================
 * Modifications history
 *========================================================================
 * $Log: JSInstanceMethodElement.java,v $
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
public class JSInstanceMethodElement extends JSFunctionElement
{
	public JSInstanceMethodElement(String aName, String argumentString, int offset, int length)
	{
		super(aName, argumentString, offset, length);
	}
	
	public ImageDescriptor getImageDescriptor(Object object)
	{
		return JSImages.ICON_VIEW_INSTANCE_METHOD;
	}

	public int category()
	{
		return INSTANCE_METHOD;	
	}

}
