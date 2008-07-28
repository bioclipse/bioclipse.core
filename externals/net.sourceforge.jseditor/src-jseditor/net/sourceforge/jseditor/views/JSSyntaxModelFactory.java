/*
 * $RCSfile: JSSyntaxModelFactory.java,v $
 *
 * Copyright 2002
 * CH-1700 Fribourg, Switzerland
 * All rights reserved.
 *
 *========================================================================
 * Modifications history
 *========================================================================
 * $Log: JSSyntaxModelFactory.java,v $
 * Revision 1.3  2003/05/30 20:53:08  agfitzp
 * 0.0.2 : Outlining is now done as the user types. Some other bug fixes.
 *
 * Revision 1.2  2003/05/28 20:47:56  agfitzp
 * Outline the document, not the file.
 *
 * Revision 1.1  2003/05/28 15:17:11  agfitzp
 * net.sourceforge.jseditor 0.0.1 code base
 *
 *========================================================================
*/

package net.sourceforge.jseditor.views;

import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.jface.text.IDocument;

import net.sourceforge.jseditor.editors.JSParser;

/**
 * @author Addi 
 */
public class JSSyntaxModelFactory
{
	private static JSSyntaxModelFactory instance = new JSSyntaxModelFactory();
	private boolean registryLoaded = false;

	/**
	 * Creates a new JSSyntaxModelFactory.
	 */
	private JSSyntaxModelFactory()
	{
	}

	/**
	 * @param adaptable  
	 * 
	 * @return 
	 */
	public JSElementList getContentOutline(IFile adaptable)
	{
		return new JSElementList(getSyntacticElements(adaptable));
	}

	/**
		* @param document  
		* 
		* @return 
		*/
	public JSElementList getContentOutline(IDocument document)
	{
		return new JSElementList(getSyntacticElements(document));
	}

	/**
	 * Returns the singleton readme adapter.
	 * @return
	 */
	public static JSSyntaxModelFactory getInstance()
	{
		return instance;
	}

	/**
	 * @param file  
	 * 
	 * @return 
	 */
	private List getSyntacticElements(IFile file)
	{
		return (new JSParser()).parse(file);
	}

	/**
		* @param file  
		* 
		* @return 
		*/
	private List getSyntacticElements(IDocument document)
	{
		return (new JSParser()).parse(document);
	}
}