/*******************************************************************************
 * Copyright (c) 2008 The Bioclipse Project and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Ola Spjuth
 *     Stefan Kuhn
 *     Carl MŠsak
 *     
 ******************************************************************************/
package net.bioclipse.ui.contenttypes;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.util.Hashtable;


import org.eclipse.core.internal.content.ContentMessages;
import org.eclipse.core.internal.content.TextContentDescriber;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExecutableExtension;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.content.IContentDescription;
import org.eclipse.osgi.util.NLS;

@SuppressWarnings("restriction")
public class CmlFileCoordinatesDescriber extends TextContentDescriber 
										 implements IExecutableExtension {
	
	private static final String ELEMENT_TO_FIND = "dimension"; //$NON-NLS-1$

	private String elementToFind = null;


	/**
	 * Read 100 chars, search for x2 or x2
	 */
	public int describe(InputStream contents, IContentDescription description) throws IOException {

		byte[] first=new byte[200];
		contents.read(first,0,200);
		
		return deduceCoordinates(new String(first));
	}

	/**
	 * Read 100 chars, search for x2 or x2
	 */
	private int deduceCoordinates(String input) {

		boolean has2D=input.contains("x2");
		boolean has3D=input.contains("x3");
		
		if (has2D && elementToFind.equalsIgnoreCase("2D")){
			return VALID;
		}

		if (has3D && elementToFind.equalsIgnoreCase("3D")){
			return VALID;
		}

		//Else, invalid (or indeterminate?)
		return INVALID;
	}

	/**
	 * Read 100 chars, search for x2 or x2
	 */
	public int describe(Reader contents, IContentDescription description) throws IOException {

		char[] buf=new char[200];
		contents.read(buf,0,200);
		return deduceCoordinates(new String(buf));
		
	}

	/**
	 * Store parameters
	 */
	@SuppressWarnings("unchecked")
	public void setInitializationData(final IConfigurationElement config, final String propertyName, final Object data) throws CoreException {
		if (data instanceof String)
			elementToFind = (String) data;
		else if (data instanceof Hashtable) {
			Hashtable parameters = (Hashtable) data;
 			elementToFind = (String) parameters.get(ELEMENT_TO_FIND);
		}
		if (elementToFind == null) {
			String message = NLS.bind(ContentMessages.content_badInitializationData, CmlFileCoordinatesDescriber.class.getName());
			throw new CoreException(new Status(IStatus.ERROR, ContentMessages.OWNER_NAME, 0, message, null));
		}
	}
}
