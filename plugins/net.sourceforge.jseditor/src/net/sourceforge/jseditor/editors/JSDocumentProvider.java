/*
 * $RCSfile: JSDocumentProvider.java,v $
 *
 * Copyright 2002
 * CH-1700 Fribourg, Switzerland
 * All rights reserved.
 *
 *========================================================================
 * Modifications history
 *========================================================================
 * $Log: JSDocumentProvider.java,v $
 * Revision 1.1  2003/05/28 15:17:11  agfitzp
 * net.sourceforge.jseditor 0.0.1 code base
 *
 *========================================================================
*/

package net.sourceforge.jseditor.editors;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IDocumentPartitioner;
import org.eclipse.jface.text.rules.DefaultPartitioner;
import org.eclipse.ui.editors.text.FileDocumentProvider;

/**
 * 
 *
 * @author $Author: agfitzp $, $Date: 2003/05/28 15:17:11 $
 *
 * @version $Revision: 1.1 $
 */
public class JSDocumentProvider extends FileDocumentProvider {

	/**
	 * Array of constant token types that will be color hilighted.
	 */
	private static String[] colorTokens= { 
		JSPartitionScanner.JS_COMMENT,
		JSPartitionScanner.JS_STRING, 
		JSPartitionScanner.JS_KEYWORD 
	};

	/**
	 * Constructor for JSDocumentProvider.
	 */
	public JSDocumentProvider() {
		super();
	}

	/**
	 * @param element 
	 *
	 * @return 
	 *
	 * @throws CoreException 
	 */
	protected IDocument createDocument(Object element) throws CoreException {
		IDocument document = super.createDocument(element);

		if (document != null) {
			IDocumentPartitioner partitioner =
				new DefaultPartitioner(new JSPartitionScanner(), colorTokens);
			partitioner.connect(document);
			document.setDocumentPartitioner(partitioner);
		}

		return document;
	}
}