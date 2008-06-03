package net.bioclipse.cdk10.jchempaint.colorers;

import java.awt.Color;

import net.bioclipse.cdk10.business.ICDK10Constants;

import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IChemObject;
import org.openscience.cdk.renderer.color.CPKAtomColors;
import org.openscience.cdk.renderer.color.IAtomColorer;

/**
 * Color atoms based on property 
 * @author ola
 *
 */
public class PropertyColorer extends CPKAtomColors {

    /**
     * 
     */
    private static final long serialVersionUID = 1891873637030937964L;

    public Color getAtomColor( IAtom atom ) {
        if (getColorFromProps( atom )!=null)
            return getColorFromProps( atom );
        else
            return super.getAtomColor( atom );
    }

    public Color getAtomColor( IAtom atom, Color defaultColor ) {
        return super.getAtomColor( atom, defaultColor );
    }

    //Read color from property
    private Color getColorFromProps( IChemObject obj ) {

        Object col=obj.getProperty( ICDK10Constants.COLOR_PROPERTY );
        if (col==null){
            return null;
        }

        if ( col instanceof Color ) {
            Color color = (Color) col;
            return color;
        }
        return null;
    }

}
