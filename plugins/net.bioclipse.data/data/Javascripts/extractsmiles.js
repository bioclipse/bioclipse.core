//Output SMILES for all molecules in an SDFile
var mols = cdk.loadMolecules("/Sample Data/SDF/Fragments2.sdf");
for (var i = 0; i < mols.size(); i++) {
	js.print(cdk.calculateSMILES(mols.get(i)) + "\n");
}