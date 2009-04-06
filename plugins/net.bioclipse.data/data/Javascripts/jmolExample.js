mol = cdk.fromSMILES("C(C1C(C(C(C(O1)O)O)O)O)O");
mol = cdk.addExplicitHydrogens(mol);
cdk.generate3dCoordinates(mol);
fileName = "/Virtual/" + cdk.molecularFormula(mol) + ".cml";
if ( ui.fileExists(fileName) ) {
	ui.remove(fileName);
} 
cdk.saveCML(mol, fileName);
ui.open(fileName);
jmol.run("background black");
jmol.run("select all; dots on; select none");
jmol.minimize();
jmol.run("move 60 360 30 -45 0 0 0 0 7");
