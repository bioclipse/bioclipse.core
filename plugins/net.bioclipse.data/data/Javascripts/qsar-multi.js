/*******************************************************************************
 * Bioclipse-script to calculate a list of descriptors for a list of molecules.
 * This script does not support parameters to descriptors and requires the 
 * Chemoinformatics and QSAR features to be installed. 
 *
 * Author: Ola Spjuth
 *
 ******************************************************************************/

//Set up list of descriptors
dlist = java.util.ArrayList();
d1="http://www.blueobelisk.org/ontologies/chemoinformatics-algorithms/#hBondDonors";
d2="http://www.blueobelisk.org/ontologies/chemoinformatics-algorithms/#bpol";
dlist.add(d1);
dlist.add(d2);

//Set up list of molecules
mols = java.util.ArrayList();
mols.add(cdk.fromSMILES("C(=O)N(Cc1ccco1)C(c1cc2ccccc2cc1)C(=O)NCc1ccccc1"));
mols.add(cdk.fromSMILES("C(=O)(CNC(=O)OC(C)(C)C)N(Cc1oc(C(F)(F)F)cc1)C(c1ccccc1)C(=O)NCc1ccccc1"));
mols.add(cdk.fromSMILES("C(=O)(C=CC)N(CCCCCCC)C(c1cc2c(cccc2)c2ccccc12)C(=O)NC(C)(C)C"));

//Some debug output
js.print("\nWe have " + mols.size() +" mols and " + dlist.size() + " descriptors.\n\n");

//Do descriptor calculation
res=qsar.calculateNoParams(mols,dlist)

//Display results
//Loop over all molecules in result
for (i=0; i<res.keySet().size();i++){
	mol=res.keySet().toArray()[i];
	molres=res.get(mol);
	
	js.print("Result for molecule: " + mol +"\n");
	
	//Loop over all results for this molecule
	for (d=0; d<molres.size(); d++){
		dres=molres.toArray()[d]

		//Loop over all values in this result
	   for (j=0; j<dres.getValues().length;j++){
            js.print("    " + dres.getLabels()[j] + "=" + dres.getValues()[j]  +"\n"); 
        }
	}
}
