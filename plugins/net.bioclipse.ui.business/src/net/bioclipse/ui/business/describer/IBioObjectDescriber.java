/* *****************************************************************************
 * Copyright (c) 2009 Ola Spjuth.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Ola Spjuth - initial API and implementation
 ******************************************************************************/
package net.bioclipse.ui.business.describer;

import net.bioclipse.core.api.BioclipseException;
import net.bioclipse.core.api.domain.IBioObject;

/**
 * An interface for describing BioObjects.
 * @author ola
 *
 */
public interface IBioObjectDescriber {

    /**
     * Returns an EditorID for the object or NULL is none found
     * @param object The IBioObject to determine editor for
     * @return
     * @throws BioclipseException 
     */
    String getPreferredEditorID( IBioObject object ) throws BioclipseException;

}
