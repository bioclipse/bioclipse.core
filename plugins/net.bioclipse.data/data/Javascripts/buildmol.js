/*******************************************************************************
 * Bioclipse-script to iteratively build a molecule in JChemPaint
 * Author: Ola Spjuth
 ******************************************************************************/

//Start with a carbon and open in JCP
mol=cdk.fromSMILES("C")
mol2=cdk.generate2dCoordinates(mol)
ui.open(mol2)

//Add some atoms and bonds and wait 1 sec in between
atom=jcp.getModel().getAtomContainer().getAtom(0);
jcp.addAtom("C", atom)
js.delay(1)
atom=jcp.getModel().getAtomContainer().getAtom(1);
jcp.addAtom("C", atom)
js.delay(1)
atom=jcp.getModel().getAtomContainer().getAtom(2);
jcp.addAtom("C", atom)
js.delay(1)
atom=jcp.getModel().getAtomContainer().getAtom(3);
jcp.addAtom("C", atom)
js.delay(1)
atom=jcp.getModel().getAtomContainer().getAtom(4);
jcp.addPhenyl(atom)
js.delay(1)
atom=jcp.getModel().getAtomContainer().getAtom(jcp.getModel().getAtomContainer().getAtomCount()-1);
jcp.addAtom("C", atom)
js.delay(1)
atom=jcp.getModel().getAtomContainer().getAtom(jcp.getModel().getAtomContainer().getAtomCount()-1);
jcp.addAtom("C", atom)
js.delay(1)
atom=jcp.getModel().getAtomContainer().getAtom(jcp.getModel().getAtomContainer().getAtomCount()-1);
jcp.addRing(atom,5)
