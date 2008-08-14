/*******************************************************************************
 * Copyright (c) 2008 The Bioclipse Project and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Ola Spjuth
 *     
 ******************************************************************************/
package net.bioclipse.cdk10.jchempaint.outline;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IChemFile;
import org.openscience.cdk.interfaces.IChemModel;
import org.openscience.cdk.interfaces.IChemObject;
import org.openscience.cdk.interfaces.IChemSequence;
import org.openscience.cdk.interfaces.IElectronContainer;
import org.openscience.cdk.interfaces.IMoleculeSet;

@SuppressWarnings("serial")
public class StructureContentProvider implements ITreeContentProvider {

    //Use logging
    private static final Logger logger = Logger.getLogger(StructureContentProvider.class);

    private final static String CDK_PREFIX = "org.openscience.cdk."; 
    private final static String PDB_PREFIX = "protein.data.PDB"; 

    public StructureContentProvider() {}

    private static final String[][] symbolsAndNames = {
        { "H",  "Hydrogen"  },
        { "C",  "Carbon"    },
        { "N",  "Nitrogen"  },
        { "O",  "Oxygen"    },
        { "Na", "Sodium"    },
        { "Mg", "Magnesium" },
        { "P",  "Phosphorus"},
        { "S",  "Sulphur"   },
        { "Cl", "Chlorine"  },
        { "Ca", "Calcium"   },
        { "Fe", "Iron"      },
        { "Si", "Silica"    },
        { "Br", "Bromine"   },
    };
    private static final Map<String,String> elementNames
        = new HashMap<String,String>() {{
            for (String[] symbolAndName : symbolsAndNames)
                put(symbolAndName[0], symbolAndName[1]);
        }};
    
    public Object[] getChildren(Object parentElement) {
        if (parentElement instanceof Container) {
            Container container=(Container)parentElement;
            if (container.getChildren()!=null 
                    && container.getChildren().size()>0)
                return container.getChildren().toArray(new CDKChemObject[0]);
            else
                return new Object[0];
        }
        else if (parentElement instanceof CDKChemObject) {
            
            CDKChemObject chemobj=(CDKChemObject)parentElement;
            
            if (!(chemobj.getChemobj() instanceof IAtomContainer)) {
                return new Object[0];
            }
            IAtomContainer ac = (IAtomContainer) chemobj.getChemobj();
            
            Container atoms=new Container("Atoms");
            for (int i=0; i<ac.getAtomCount(); i++){
                IAtom atom = ac.getAtom(i);
                String symbol = atom.getSymbol(),
                       name = elementNames.containsKey( symbol )
                                  ? elementNames.get( symbol )
                                  : "unknown";
                CDKChemObject co
                  = new CDKChemObject( name + " (" + symbol + ")", atom );
                atoms.addChild(co);
            }
            Container bonds=new Container("Bonds");
            for (int i=0; i<ac.getBondCount(); i++){
                IBond bond = ac.getBond(i);
                StringBuilder sb = new StringBuilder();
                char separator
                  = bond.getOrder() == CDKConstants.BONDORDER_DOUBLE   ? '='
                  : bond.getOrder() == CDKConstants.BONDORDER_TRIPLE   ? '#'
                  : bond.getOrder() == CDKConstants.BONDORDER_AROMATIC ? '~'
                                                                       : '-';
                for (java.util.Iterator<IAtom> it=bond.atoms(); it.hasNext();) {
                    sb.append(it.next().getSymbol());
                    if (it.hasNext()) {
                        sb.append(separator);
                    }
                }
                sb.append(   bond.getOrder() == CDKConstants.BONDORDER_DOUBLE
                               ? " (double)"
                           : bond.getOrder() == CDKConstants.BONDORDER_TRIPLE
                               ? " (triple)"
                           : bond.getOrder() == CDKConstants.BONDORDER_AROMATIC
                               ? " (aromatic)"
                               : "" );
                CDKChemObject co=new CDKChemObject(sb.toString(), bond);
                bonds.addChild(co);
            }
            
            Object[] retobj=new Object[2];
            retobj[0]=atoms;
            retobj[1]=bonds;
            
            return retobj;
        }
        
        return new Object[0];
    }

    public Object getParent(Object element) {
        // TODO Auto-generated method stub
        return null;
    }

    public boolean hasChildren(Object element) {
        return (getChildren(element).length > 0);
    }

    public Object[] getElements(Object inputElement) {
        
        if (inputElement instanceof IChemModel) {
            IChemModel model = (IChemModel) inputElement;
            
            IMoleculeSet ms=model.getMoleculeSet();
            if (ms==null || ms.getAtomContainerCount()<=0)
            {
                logger.debug("No AtomContainers in ChemModel.");
                return new Object[0];
            }

            CDKChemObject[] acs=new CDKChemObject[ms.getAtomContainerCount()];
            for (int i=0; i<ms.getAtomContainerCount(); i++){
                acs[i]=new CDKChemObject("AC_" + i, ms.getAtomContainer(i));
            }
            
            if (acs.length>1)
                return acs;
            else if (acs.length==1){
                return getChildren(acs[0]);
            }
                
        }
        
        return new IChemObject[0];
    }

    public void dispose() {
        // TODO Auto-generated method stub

    }

    public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
        // do nothing
    }
    
    
    
    
    @SuppressWarnings("unchecked")
    private Object[] getChemObjectChildren(IChemObject object) {

        Class reflectedClass = object.getClass();
//        logger.debug("getTree for class: " + reflectedClass);
        // get all fields in this ChemObject
        Field[] fields = getFields(reflectedClass);
//        logger.debug(reflectedClass.getName() + " #fields: " + fields.length);

        List children = new ArrayList();
        for (int i=0; i<fields.length; i++) {
            Field f = fields[i];
            f.setAccessible(true);
//            logger.debug("Field name: " + f.getName());
//            logger.debug("Field type: " + f.getType().getName());
            try {
                // get an instance of the object in the field
                Object fieldObject = f.get(object);
                if (fieldObject != null) {
//                    logger.debug("Field value: " + fieldObject.getClass().getName());
                    if (fieldObject instanceof IChemObject) {
                        // yes, found a ChemObject!
//                        logger.debug("Recursing into this object");

                        CDKChemObject co=new CDKChemObject((IChemObject)fieldObject);

//                        children.add(fieldObject);
                        children.add(co);
                    } else if (fieldObject instanceof IChemObject[]) {
                        // yes, found a Array!
//                        logger.debug("Recursing into this Array");
                        // determine what kind of Array
                        IChemObject[] objects = (IChemObject[])fieldObject;
                        int count = objects.length;
                        if (count>0){
                        // Because the count above gives the array length and not the number
                        // of not null objects the array, some intelligence must be added
                        if (object instanceof IAtomContainer && objects[0] != null) {
//                            logger.debug("field class: " + objects[0].getClass().getName());
                            if (objects[0] instanceof IAtom) {
                                count = ((IAtomContainer)object).getAtomCount();
                            } else if (objects[0] instanceof IElectronContainer) {
                                count = ((IAtomContainer)object).getElectronContainerCount();
                            } else {
//                                logger.debug("Object not counted!");
                            }
                        } else if (object instanceof IChemSequence && objects[0] != null) {
                            if (objects[0] instanceof IChemModel) {
                                count = ((IChemSequence)object).getChemModelCount();
                            } else {
//                                logger.debug("Object not counted!");
                            }
                        } else if (object instanceof IChemFile && objects[0] != null) {
                            if (objects[0] instanceof IChemSequence) {
                                count = ((IChemFile)object).getChemSequenceCount();
                            } else {
                                logger.debug("Object not counted!");
                            }
                        } else {
//                            logger.debug("Not going to recurse into arrays that are not field of AtomContainer");
                        }
//                        logger.debug("Found #entries in array: " + count);
                        // now start actual looping over child objects
                        for (int j=0; j<count; j++) {
                            if (objects[j] != null) {

                                CDKChemObject co=new CDKChemObject((IChemObject)objects[j]);
                                children.add(co);

//                                children.add(objects[j]);
                            }
                        }
                        }
                    }
                } else {
//                    logger.debug("Field value: null");
                }
            } catch (Exception e) {
                logger.debug("Error while constructing COT: " + e.getMessage());
                logger.debug(e);
                e.printStackTrace();
            }
        }
        return (children.toArray());
    }

    private Field[] getFields(Class reflectedClass) {
        Field[] fields = new Field[0];

        try {
            if (reflectedClass.newInstance() instanceof IChemObject) {
                Field[] ownFields = reflectedClass.getDeclaredFields();            

                // try its super class too, as long as it is still a ChemObject...
                Class superClass = reflectedClass.getSuperclass();
                Field[] superFields = getFields(superClass);

                if (superFields.length > 0) { 
                    // merge them
                    fields = new Field[ownFields.length + superFields.length];
                    System.arraycopy(ownFields, 0, fields, 0, ownFields.length);
                    System.arraycopy(superFields, 0, fields, ownFields.length, superFields.length);
                } else {
                    fields = ownFields;
                }
            }
        } catch (IllegalAccessException event) {
            logger.error("IllegalAccess: " + event.getMessage());
        } catch (InstantiationException event) {
//            logger.error("InstantiationException: " + event.getMessage());
        };
        return fields;
    }

    private String removePrefixes(String className) {
        className = className.substring(CDK_PREFIX.length());
        if (className.startsWith(PDB_PREFIX)) {
            className = className.substring(PDB_PREFIX.length());
        }
        return className;

    }
}
