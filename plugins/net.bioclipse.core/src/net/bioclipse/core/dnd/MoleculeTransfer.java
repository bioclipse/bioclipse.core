package net.bioclipse.core.dnd;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

import net.bioclipse.core.api.BioclipseException;
import net.bioclipse.core.api.domain.CMLMolecule;
import net.bioclipse.core.api.domain.IMolecule;

import org.apache.log4j.Logger;
import org.eclipse.swt.dnd.ByteArrayTransfer;
import org.eclipse.swt.dnd.TransferData;

public class MoleculeTransfer extends ByteArrayTransfer{
    Logger logger = Logger.getLogger( MoleculeTransfer.class.getName() );
    private static MoleculeTransfer instance = new MoleculeTransfer();
    private static final String TYPE_NAME = "molecule-transfer-format";
    private static final int TYPEID = registerType( TYPE_NAME );

    public static MoleculeTransfer getInstance() { return instance; }
    private MoleculeTransfer() {}

    @Override
    protected int[] getTypeIds() {
        return new int[] {TYPEID};
    }

    @Override
    protected String[] getTypeNames() {
        return new String[] {TYPE_NAME};
    }

    public byte[] toByteArray(IMolecule[] acs) {

        StringBuffer buf=new StringBuffer();
        for(IMolecule mol:acs){
            try {
                buf.append( mol.toCML() );
                buf.append( "__SEPARATOR__" );
            } catch ( BioclipseException e ) {
                return null;
            }
        }
        return buf.toString().getBytes();
    }

    protected IMolecule[] fromByteArray(byte[] bytes) {
        
        String fullstr=new String(bytes);
        //Split by separator for multiple mols
        String[] cmlstring = fullstr.split("__SEPARATOR__" );
        List<IMolecule> imols=new ArrayList<IMolecule>();
        for (String mol : cmlstring){
            imols.add(new CMLMolecule( mol ));
        }
        return imols.toArray( new IMolecule[imols.size()] );
    }

    @Override
    protected void javaToNative( Object object, TransferData transferData ) {
        byte[] bytes = toByteArray((IMolecule[])object);
        if(bytes!=null)
            super.javaToNative( bytes, transferData );
    }

    @Override
    protected Object nativeToJava( TransferData transferData ) {
        byte[] bytes = (byte[]) super.nativeToJava( transferData );
        return fromByteArray( bytes );
    }
}
