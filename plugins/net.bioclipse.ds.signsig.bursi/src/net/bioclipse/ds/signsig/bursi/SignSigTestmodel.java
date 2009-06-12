package net.bioclipse.ds.signsig.bursi;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.bioclipse.cdk.domain.ICDKMolecule;
import net.bioclipse.core.business.BioclipseException;
import net.bioclipse.core.domain.IMolecule;
import net.bioclipse.ds.model.ITestResult;
import net.bioclipse.ds.model.TestRun;
import net.bioclipse.ds.model.report.AbstractTestReportModel;
import net.bioclipse.ds.model.report.DSRow;
import net.bioclipse.ds.model.report.ReportHelper;


public class SignSigTestmodel extends AbstractTestReportModel{

   
    public SignSigTestmodel() {
        super();
    }
    
    public List<DSRow> extractRows(TestRun run){

        //for a testrunm transform to a DSRow with a structure image and paams
        List<DSRow> newrows=new ArrayList<DSRow>();
        for (int i=0; i<run.getMatches().size(); i++){
            
            ITestResult match = run.getMatches().get( i );

                Map<String, String> params=new HashMap<String, String>();
                params.put("name",  match.getName());
                params.put("classification",  ReportHelper.statusToString(
                                                      match.getClassification()));

                //Ok, we need to take the query molecule and highlight the 
                // substructure from this match
                ICDKMolecule mainmol = run.getMolecule();
                byte[] structureImage = null;
                try {
                    structureImage = ReportHelper.createImage(mainmol, match);
                } catch ( BioclipseException e ) {
                    e.printStackTrace();
                }
                DSRow row=new DSRow(structureImage, params );
                newrows.add( row );

            }

        return newrows;
    }

}
