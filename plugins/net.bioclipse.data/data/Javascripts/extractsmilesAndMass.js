var mols = cdk.loadMolecules("/Sample Data/SDF/Fragments2.sdf");

for (var i = 0; i < mols.size(); i++) {
	var mol = mols.get(i);

	print(cdk.calculateMass(mol) +
	       "\t" +
	       cdk.calculateSMILES(mol) +
	       "\n" );
}