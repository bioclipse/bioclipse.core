/*******************************************************************************
 * Copyright (c) 2008 The Bioclipse Project and others.
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
public interface ISpecmol extends IBioObject{
            /**
             * @return the IMolecule serialized to CML
             * @throws BioclipseException if CML cannot be returned
             */
            public String getCML() throws BioclipseException;
}
