package net.bioclipse.ds.signsig.bursi;

import java.util.List;

import org.apache.log4j.Logger;
import org.openscience.cdk.interfaces.IAtom;

import net.bioclipse.cdk.domain.ICDKMolecule;
import net.bioclipse.cdk.ui.sdfeditor.business.IPropertyCalculator;
import net.bioclipse.core.business.BioclipseException;
import net.bioclipse.core.util.LogUtils;
import net.bioclipse.ds.business.IDSManager;
import net.bioclipse.ds.model.ITestResult;


public class SignSigPropertyCalculator implements IPropertyCalculator<List <ITestResult>> {

    private static final Logger logger = Logger.getLogger(
                                               SignSigPropertyCalculator.class);

    public SignSigPropertyCalculator() {
    }

    public List<ITestResult> calculate( ICDKMolecule molecule ) {

        IDSManager ds = net.bioclipse.ds.Activator.getDefault().getJavaManager();
        try {
            List<ITestResult> results = ds.runTest( Activator.DS_TEST_ID, molecule );
            return results;
        } catch ( BioclipseException e ) {
            LogUtils.handleException( e, logger, Activator.PLUGIN_ID);
        }
        return null;
    }

    public String getPropertyName() {
        return "net.bioclipse.ds.signsig.bursi";
    }

    public List<ITestResult> parse( String value ) {

        // TODO IMPLEMENT!
        return null;
    }

    public String toString( Object value ) {

        @SuppressWarnings("unchecked")
        List<ITestResult> results = (List<ITestResult>)value;

        //Format is: [CC]/1,4,5; e.g. name/atomnumber1,atomnumber2...;
        String ret = "";
        for (ITestResult res : results){
            ret=ret+res.getName()+"/";
            for (IAtom atom : res.getAtomContainer().atoms()){
                int atomnr=res.getTestRun().getAtomContainer().getAtomNumber( atom );
                ret=ret+atomnr +",";
            }
            ret=ret+";";
        }
        //TODO: remove last ; ?
        
        return ret;
    }

}
