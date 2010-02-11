	/*******************************************************************************
	 * Copyright (c) 2008-2009 The Bioclipse Project and others.
	 * All rights reserved. This program and the accompanying materials
	 * are made available under the terms of the Eclipse Public License v1.0
	 * which accompanies this distribution, and is available at
	 * www.eclipse.org—epl-v10.html <http://www.eclipse.org/legal/epl-v10.html>
	 * 
	 * Contributors:
	 *     Stefan Kuhn
	 *     
	 ******************************************************************************/
package net.bioclipse.core.domain;

import net.bioclipse.core.business.BioclipseException;

/**
 * Object to hold bibliographic information, like an article or book
 * reference.
 *
 * @author egonw
 */
public interface IBibliodata extends IBioObject{

		    /**
		     * Returns a BibTeXML serialization of this object.
		     *
		     * @return the {@link IBibliodata} serialized to BibTeXML
		     * @throws BioclipseException if BibTeXML cannot be returned
		     */
		    public String getBibtexML() throws BioclipseException;
}
