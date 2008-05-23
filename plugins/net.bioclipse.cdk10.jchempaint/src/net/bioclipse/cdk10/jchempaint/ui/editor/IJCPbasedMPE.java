package net.bioclipse.cdk10.jchempaint.ui.editor;

import org.openscience.cdk.interfaces.IChemModel;

/**
 * An interface for MultiPageEditors (MPEs) containing multiple ChemModels
 * For example, SDFEditor that contains multiple models for JCP
 * @author ola
 *
 */
public interface IJCPbasedMPE {

    public IChemModel getNextModel();
    public IChemModel getPrevModel();
    public IChemModel getModel( int i );

}
