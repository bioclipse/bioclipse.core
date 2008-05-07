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

import java.util.ArrayList;
import java.util.HashMap;

import org.eclipse.ui.views.properties.IPropertyDescriptor;
import org.eclipse.ui.views.properties.PropertyDescriptor;
import org.eclipse.ui.views.properties.TextPropertyDescriptor;
import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IChemObject;
import org.openscience.cdk.interfaces.IMonomer;
import org.openscience.cdk.interfaces.IPseudoAtom;
import org.openscience.cdk.interfaces.IStrand;
import org.openscience.cdk.protein.data.PDBAtom;

import net.bioclipse.core.domain.props.BasicPropertySource;

public class ChemObjectPropertySource extends BasicPropertySource {

        //General properties for ChemObjects
      //  protected static final String PROPERTY_NAME = "Name"; 

        // general to all IChemObject
        protected static final String OBJECT_TITLE = "Title";
        protected static final String OBJECT_ID = "Identifier";
        
        //Atom specific
        protected static final String ATOM_TYPE = "Atom Type";
        protected static final String ATOMIC_NUMBER = "Atomic Number";
        protected static final String MASS_NUMBER = "Mass Number";
        protected static final String ATOM_SYMBOL = "Symbol";
        protected static final String ATOM_COORD2D = "2D Coordinates";
        protected static final String ATOM_COORD3D = "3D Coordinates";

        //PseudoAtom specific
        protected static final String ATOM_LABEL = "Label";

        //PDB specific
        protected static final String PDB_RES_NAME = "Residue Name";
        protected static final String PDB_CHAIN_ID = "Chain ID";
        protected static final String PDB_RES_SEQ  = "Residue Name";
        
        // IStrand specific
        protected static final String STRAND_NAME  = "Name";
        protected static final String STRAND_TYPE  = "Type";

        // IMonomer specific
        protected static final String MONOMER_NAME  = "Strand Name";
        
        //Bond specific
        protected static final String BOND_ORDER = "Bond order";


        private Object ChemObjectPropertiesTable[][] =
        {
            { OBJECT_TITLE, new TextPropertyDescriptor(OBJECT_TITLE,"Title")},
            { OBJECT_ID, new TextPropertyDescriptor(OBJECT_ID,"Identifier")},
        };
        
        private Object PseudoAtomPropertiesTable[][] =
        {
            { ATOM_LABEL, new TextPropertyDescriptor(ATOM_LABEL,"Label")},
        };
        
        private Object AtomPropertiesTable[][] = 
        {
            { ATOM_TYPE, new TextPropertyDescriptor(ATOM_TYPE,"Type")},
            { ATOMIC_NUMBER, new TextPropertyDescriptor(ATOMIC_NUMBER,"Atomic Number")},
            { MASS_NUMBER, new TextPropertyDescriptor(MASS_NUMBER,"Mass Number")},
            { ATOM_SYMBOL, new TextPropertyDescriptor(ATOM_SYMBOL,"Symbol")},
            { ATOM_COORD2D, new TextPropertyDescriptor(ATOM_COORD2D,"2D Coordinates")},
            { ATOM_COORD3D, new TextPropertyDescriptor(ATOM_COORD3D,"3D Coordinates")},
        };  

        private Object PDBAtomPropertiesTable[][] = 
        {
            { PDB_RES_NAME, new TextPropertyDescriptor(PDB_RES_NAME,"Residue Name")},
            { PDB_CHAIN_ID, new TextPropertyDescriptor(PDB_CHAIN_ID,"Chain ID")},
            { PDB_RES_SEQ, new TextPropertyDescriptor(PDB_RES_SEQ,"Residue Name")},
        };  

        private Object StrandPropertiesTable[][] = 
        {
            { STRAND_NAME, new TextPropertyDescriptor(STRAND_NAME,"Name")},
            { STRAND_TYPE, new TextPropertyDescriptor(STRAND_TYPE,"Type")},
        };  

        private Object MonomerPropertiesTable[][] = 
        {
            { MONOMER_NAME, new TextPropertyDescriptor(MONOMER_NAME,"Monomer Name")},
        };  

        private Object BondPropertiesTable[][] = 
        {
            { BOND_ORDER, new TextPropertyDescriptor(BOND_ORDER,"Order")},
        };  


        //Constructor
        public ChemObjectPropertySource(CDKChemObject item) {
          super(item);
          
          // clean the table
          setProperties(new ArrayList<IPropertyDescriptor>());
          setValueMap(new HashMap<String, String>());

          
          //The ChemObject that holds the actual properties
          IChemObject chemobj=item.getChemobj();

          // setup the new properties
          
          // the general ones first
          for (int i=0;i<ChemObjectPropertiesTable.length;i++) {        
            // Add each property supported.
            PropertyDescriptor descriptor;
            descriptor = (PropertyDescriptor)ChemObjectPropertiesTable[i][1];
            descriptor.setCategory("General");
            getProperties().add((IPropertyDescriptor)descriptor);
          }   
          
          addToValueMap(OBJECT_TITLE,(String)chemobj.getProperty(CDKConstants.TITLE));
          addToValueMap(OBJECT_ID,chemobj.getID());
          
          //======
          //IAtom
          //======

          if (chemobj instanceof IAtom) {
            IAtom atom = (IAtom) chemobj;

            //Build the arraylist of propertydescriptors
            for (int i=0;i<AtomPropertiesTable.length;i++) {        
              // Add each property supported.
              PropertyDescriptor descriptor;
              descriptor = (PropertyDescriptor)AtomPropertiesTable[i][1];
              descriptor.setCategory("Atom");
              getProperties().add((IPropertyDescriptor)descriptor);
            }

            //Build the hashmap of property->value pair
            addToValueMap(ATOM_TYPE,atom.getAtomTypeName());
            addToValueMap(ATOMIC_NUMBER,String.valueOf(atom.getAtomicNumber()));
            addToValueMap(MASS_NUMBER,String.valueOf(atom.getMassNumber()));
            addToValueMap(ATOM_SYMBOL,atom.getSymbol());
            addToValueMap(ATOM_COORD2D,
              atom.getPoint2d() != null ? "" + atom.getPoint2d() : null
            );
            addToValueMap(ATOM_COORD3D,
              atom.getPoint3d() != null ? "" + atom.getPoint3d() : null
            );
            
            if (chemobj instanceof IPseudoAtom) {
              IPseudoAtom pseudo = (IPseudoAtom)atom;

              //Build the arraylist of propertydescriptors
              for (int i=0;i<PseudoAtomPropertiesTable.length;i++) {        
                // Add each property supported.
                PropertyDescriptor descriptor;
                descriptor = (PropertyDescriptor)PseudoAtomPropertiesTable[i][1];
                descriptor.setCategory("Pseudo Atom");
                getProperties().add((IPropertyDescriptor)descriptor);
              }

              //Build the hashmap of property->value pair
              addToValueMap(ATOM_TYPE,pseudo.getLabel());
            }
          }   

          if (chemobj instanceof PDBAtom) {
            PDBAtom atom = (PDBAtom) chemobj;

            //Build the arraylist of propertydescriptors
            for (int i=0;i<PDBAtomPropertiesTable.length;i++) {       
              // Add each property supported.
              PropertyDescriptor descriptor;
              descriptor = (PropertyDescriptor)PDBAtomPropertiesTable[i][1];
              descriptor.setCategory("PDB Properties");
              getProperties().add((IPropertyDescriptor)descriptor);
            }

            //Build the hashmap of property->value pair
            addToValueMap(PDB_RES_NAME,atom.getResName());
            addToValueMap(PDB_CHAIN_ID,atom.getChainID());
            addToValueMap(PDB_RES_SEQ,atom.getResSeq());
          }
          
          
          //======
          //IBond
          //======
          
          if (chemobj instanceof IBond) {
            IBond bond = (IBond) chemobj;

            //Build the arraylist of propertydescriptors
            for (int i=0;i<BondPropertiesTable.length;i++) {        
              // Add each property supported.
              PropertyDescriptor descriptor;
              descriptor = (PropertyDescriptor)BondPropertiesTable[i][1];
              descriptor.setCategory("Bond");
              getProperties().add((IPropertyDescriptor)descriptor);
            }

            //Build the hashmap of property->value pair
            addToValueMap(BOND_ORDER,String.valueOf(bond.getOrder()));
          }   
        
          //======
          //IStrand
          //======
          
          if (chemobj instanceof IStrand) {
            IStrand strand = (IStrand) chemobj;

            //Build the arraylist of propertydescriptors
            for (int i=0;i<StrandPropertiesTable.length;i++) {        
              // Add each property supported.
              PropertyDescriptor descriptor;
              descriptor = (PropertyDescriptor)StrandPropertiesTable[i][1];
              descriptor.setCategory("Polymer Strand");
              getProperties().add((IPropertyDescriptor)descriptor);
            }

            //Build the hashmap of property->value pair
            addToValueMap(STRAND_NAME,strand.getStrandName());
            addToValueMap(STRAND_TYPE,strand.getStrandType());
          }   
        
          //======
          //IMonomer
          //======
          
          if (chemobj instanceof IMonomer) {
            IMonomer monomer = (IMonomer) chemobj;

            //Build the arraylist of propertydescriptors
            for (int i=0;i<StrandPropertiesTable.length;i++) {        
              // Add each property supported.
              PropertyDescriptor descriptor;
              descriptor = (PropertyDescriptor)StrandPropertiesTable[i][1];
              descriptor.setCategory("Monomer");
              getProperties().add((IPropertyDescriptor)descriptor);
            }

            //Build the hashmap of property->value pair
            addToValueMap(STRAND_NAME,monomer.getMonomerName());
            addToValueMap(STRAND_TYPE,monomer.getMonomerType());
          }   
        
          return;
        }

        /**
         * Validate strings are non-empty or else add "N/A"
         * @param keyString
         * @param valueString
         */
        private void addToValueMap(String keyString, String valueString) {
          if (keyString==null || keyString=="") return;
          
          if (valueString==null || valueString=="")
            getValueMap().put(keyString,"N/A");
          else
            getValueMap().put(keyString,valueString);
        }

        
      }
