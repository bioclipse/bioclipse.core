var mols = cdk.loadMolecules("/Sample Data/SDF/Fragments2.sdf");
for (var i = 0; i < mols.size(); i++) {
	js.print(mols.get(i).getSMILES() + "\n");
}