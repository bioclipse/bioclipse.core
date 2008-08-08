/*******************************************************************************
 * Copyright (c) 2008 The Bioclipse Project and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Ola Spjuth
 *     Carl Masak
 *     
 ******************************************************************************/
package net.bioclipse.cdk10.jchempaint.outline;

import java.util.HashMap;
import java.util.Map;

import net.bioclipse.cdk10.jchempaint.Activator;

import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;
import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IChemObject;

/**
 * A LabelProvider for JCPOutline
 * @author ola
 *
 */
@SuppressWarnings("serial")
public class StructureLabelProvider extends LabelProvider {

    private final static String[] cachedAtoms = {
        "h",
        "c", "n", "o",
        "na", "mg", "p", "s", "cl",
        "ca", "fe", "br",
        "unknown"
    };
    
    private final static String[] cachedBonds = {
        "1", "2", "3", "aromatic"
    };
    
    private final static Map<String,Image> cachedImages
        = new HashMap<String,Image>() {
            {
                for ( String atom : cachedAtoms )
                    put(atom, createImage("icons/atom_", atom));
                for ( String bond : cachedBonds )
                    put(bond, createImage("icons/bond_", bond));
            }

            private Image createImage( String prefix, String type ) {
                return Activator.imageDescriptorFromPlugin(
                    Activator.PLUGIN_ID, prefix + type + ".png"
                ).createImage();
            }
    };
        
    private final static Map<Double,String> bondTypes
      = new HashMap<Double,String>() {{

          put(CDKConstants.BONDORDER_SINGLE,   "1");
          put(CDKConstants.BONDORDER_DOUBLE,   "2");
          put(CDKConstants.BONDORDER_TRIPLE,   "3");
          put(CDKConstants.BONDORDER_AROMATIC, "aromatic");
    }};
    
    public String getText(Object obj) {
        
        String s = obj instanceof Container     ? ((Container)    obj).getName()
                 : obj instanceof CDKChemObject ? ((CDKChemObject)obj).getName()
                 : obj.toString();
                 
       return s == null ? "???" : s;
    }
    
    public Image getImage(Object element) {

        if (!(element instanceof CDKChemObject))
            return null;
        
        IChemObject chemobj = ((CDKChemObject)element).getChemobj();

        if (chemobj instanceof IAtom) {
            IAtom atom = (IAtom) chemobj;
            String symbol = atom.getSymbol().toLowerCase();
                    
            return cachedImages.get(
                       cachedImages.containsKey(symbol) ? symbol : "unknown"
                   );
        }
        else if (chemobj instanceof IBond) {
            IBond bond = (IBond) chemobj;
            String type = bondTypes.get( bond.getOrder() );

            return cachedImages.get(type);
        }
        else {
            return null;
        }
    }
}