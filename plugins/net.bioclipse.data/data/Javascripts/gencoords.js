/*******************************************************************************
 * Bioclipse-script to generate different coordinates on a list of molecules.
 * Author: Ola Spjuth
 ******************************************************************************/

//Create list of molecules
mols=cdk.createMoleculeList();
mols.add(cdk.fromSMILES("CCC"));
mols.add(cdk.fromSMILES("CCCNCC"));
mols.add(cdk.fromSMILES("CCNCCOC(C)CC"));

//Print out molecules with no coordinates
js.print("\n\nWe have " + mols.size() + " mols in list: mols\n");
for (i=0; i<mols.size();i++){
	js.print( i +" has 2D: " + cdk.has2d(mols.get(i)) + " , has 3D:" + cdk.has3d(mols.get(i)) +" \n");
}

//Generate 2D coordinates from no coordinates
mols2=cdk.generate2dCoordinates(mols);
js.print("\nWe have " + mols2.size() + " mols in list: mols2\n");
for (i=0; i<mols2.size();i++){
	js.print( i +" has 2D: " + cdk.has2d(mols2.get(i)) + " , has 3D:" + cdk.has3d(mols2.get(i)) +" \n");
}

//Generate 3D coordinates from no coordinates
mols3=cdk.generate3dCoordinates(mols);
js.print("\nWe have " + mols3.size() + " mols in list: mols3\n");
for (i=0; i<mols3.size();i++){
	js.print( i +" has 2D: " + cdk.has2d(mols3.get(i)) + " , has 3D:" + cdk.has3d(mols3.get(i)) +" \n");
}

//Generate 3D coordinates from 2D coordinates
mols4=cdk.generate3dCoordinates(mols2);
js.print("\nWe have " + mols4.size() + " mols in list: mols4\n");
for (i=0; i<mols4.size();i++){
	js.print( i +" has 2D: " + cdk.has2d(mols4.get(i)) + " , has 3D:" + cdk.has3d(mols4.get(i)) +" \n");
}

//Test open MolTable on Molecules with 5D
ui.open(mols);
