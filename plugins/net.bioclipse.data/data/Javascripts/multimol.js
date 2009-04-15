//Demonstrates basic manipulation of multiple molecules in Bioclipse
//Requires sample data installed in default location
mols=cdk.loadMolecules("/Sample Data/SDF/Fragments2.sdf")
mols.size()
mol=mols.get(3)
cdk.calculateSMILES(mols.get(4))
ui.open(mols)
