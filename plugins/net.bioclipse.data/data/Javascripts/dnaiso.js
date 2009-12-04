//Download PDB via Web service at EBI and open in Jmol
mol=ws.queryPDB("1d66")
ui.open(mol.get(0))

//Execute some jmol script commands
jmol.run("select all; spacefill 0; wireframe off; cartoon on;select none")
jmol.run("select !dna; isosurface solvent; select none; color isosurface blue")
jmol.spinOn()