//Calculates 2 chemical properties (descriptors) for a molecule
//Requires the Bioclipse QSAR feature to be installed
mol=cdk.fromSMILES("C1CNCCC1CC(COC)CCNC");
descid="http://www.blueobelisk.org/ontologies/chemoinformatics-algorithms/#bpol";
descid2="http://www.blueobelisk.org/ontologies/chemoinformatics-algorithms/#hBondDonors";
dres = qsar.calculate(mol, descid);
js.print("Result for " + dres.getLabels()[0] + ": " + dres.getValues()[0])
dres = qsar.calculate(mol, descid2);
js.print("Result for " + dres.getLabels()[0] + ": " + dres.getValues()[0])
