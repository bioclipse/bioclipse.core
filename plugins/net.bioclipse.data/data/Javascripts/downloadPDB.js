//Download PDB via Web service at EBI and open in Jmol
res=webservices.downloadPDBAsFile("1d66")
ui.open(res)

//Execute some jmol script commands
jmol.run("select all; spacefill 0; wireframe off; cartoon on;select none")
jmol.run("select !dna; isosurface solvent; select none; color isosurface blue")
jmol.spinOn()