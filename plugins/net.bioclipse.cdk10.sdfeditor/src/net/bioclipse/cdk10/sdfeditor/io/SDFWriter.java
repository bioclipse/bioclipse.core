/* $Revision$ $Author$ $Date$
 *
 * Copyright (C) 2008  Egon Willighagen <egonw@users.sf.net>
 *
 * Contact: cdk-devel@lists.sourceforge.net
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 2.1
 * of the License, or (at your option) any later version.
 * All we ask is that proper credit is given for our work, which includes
 * - but is not limited to - adding the above copyright notice to the beginning
 * of your source code files, and to any copyright notice that you may distribute
 * with programs based on this work.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 */
package net.bioclipse.cdk10.sdfeditor.io;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Iterator;

import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IChemObject;
import org.openscience.cdk.interfaces.IMolecule;
import org.openscience.cdk.interfaces.IMoleculeSet;
import org.openscience.cdk.io.DefaultChemObjectWriter;
import org.openscience.cdk.io.MDLWriter;
import org.openscience.cdk.io.formats.IResourceFormat;
import org.openscience.cdk.io.formats.SDFFormat;

/**
 * MDL SD file writer, which outputs all IMolecule.getProperties() as SD properties.
 *
 * @author egonw
 */
public class SDFWriter extends DefaultChemObjectWriter {

    private Writer writer;

    public void setWriter( Writer writer ) throws CDKException {
        this.writer = writer;
    }

    public void setWriter( OutputStream writer ) throws CDKException {
        this.writer = new OutputStreamWriter(writer);
    }

    public void write( IChemObject object ) throws CDKException {
        if (object instanceof IMoleculeSet) {
            writeMoleculeSet((IMoleculeSet)object);
        } else {
            throw new CDKException("Cannot writer anything other than IMoleculeSet.");
        }
    }

    private void writeMoleculeSet(IMoleculeSet set) throws CDKException {
        try {
            Iterator<IMolecule> molecules = set.molecules();
            while (molecules.hasNext()) {
                IMolecule mol = molecules.next();
                StringWriter sWriter = new StringWriter();
                MDLWriter mdlWriter = new MDLWriter(sWriter);
                mdlWriter.setSdFields(mol.getProperties());
                mdlWriter.write(mol);
                mdlWriter.close();
                this.writer.write(sWriter.toString());
                if (molecules.hasNext()) this.writer.write("$$$$\n");
                writer.flush();
            }
        } catch (IOException exception) {
            throw new CDKException(
                "Error while writing SD file: " + exception.getMessage(),
                exception
            );
        }
    }
    
    public boolean accepts( Class classObject ) {
        Class[] interfaces = classObject.getInterfaces();
        for (int i=0; i<interfaces.length; i++) {
            if (IMoleculeSet.class.equals(interfaces[i])) return true;
        }
        return false;
    }

    public void close() throws IOException {
        this.writer.close();
    }

    public IResourceFormat getFormat() {
        return SDFFormat.getInstance();
    }
    
}
