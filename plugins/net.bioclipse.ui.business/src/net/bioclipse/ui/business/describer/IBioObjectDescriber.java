package net.bioclipse.ui.business.describer;

import net.bioclipse.core.business.BioclipseException;
import net.bioclipse.core.domain.IBioObject;

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
