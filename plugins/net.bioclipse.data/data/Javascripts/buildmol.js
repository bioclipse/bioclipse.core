/*******************************************************************************
* Bioclipse-script to iteratively build a molecule in JChemPaint
* Author: Ola Spjuth
* Author: Egon Willighagen
******************************************************************************/
 
//Start with a carbon and open in JCP
mol=cdk.fromSMILES("C")
mol2=cdk.generate2dCoordinates(mol)
ui.open(mol2)
 
//Add some atoms and bonds and wait 1 sec in between
atom=jcp.getModel().getAtomContainer().getAtom(0);
atom=jcp.addAtom("C", atom)
js.delay(1)
atom=jcp.addAtom("C", atom)
js.delay(1)
atom=jcp.addAtom("C", atom)
js.delay(1)
atom=jcp.addAtom("C", atom)
js.delay(1)
jcp.addPhenyl(atom)
js.delay(1)
molecule=jcp.getModel().getAtomContainer()
atom=molecule.getAtom(molecule.getAtomCount()-1);
atom=jcp.addAtom("C", atom)
js.delay(1)
atom=jcp.addAtom("C", atom)
js.delay(1)
jcp.addRing(atom,5)