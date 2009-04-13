//Output SMILES and MASS for all molecules in an SDFile
var mols = cdk.loadMolecules("/Sample Data/SDF/Fragments2.sdf");

for (var i = 0; i < mols.size(); i++) {
	var mol = mols.get(i);

	js.print( cdk.calculateSMILES(mol) +
	       ": \t" +
	       cdk.calculateMass(mol) +
	       "\n" );
}