//Create a molecule from SMILES, open in JCP and customize rendering
mol=cdk.fromSMILES("C1CCCCC1CC(CCO)CCNC")
mol2=cdk.generate2dCoordinates(mol)
ui.open(mol2)
jcpglobal.setShowAromaticity(true)
