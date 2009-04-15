//Download PDB
res=webservices.downloadPDBAsFile("1d66")
ui.open(res)
 
//Do some jmol visualization
jmol.run("move 0 0 90 00 0 0 0 0 1;set selectionhalos off; select all; spacefill 0; cartoon on;")
jmol.run("select nucleic; isosurface dna1 solvent; delay 1; color isosurface red;");
jmol.run("delay 1; color isosurface blue; delay 1; color isosurface translucent; move 0 360 90 90 0 0 0 0 4;");