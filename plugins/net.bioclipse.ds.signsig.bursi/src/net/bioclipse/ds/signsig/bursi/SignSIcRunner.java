/*******************************************************************************
 * Copyright (c) 2009 Ola Spjuth.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Ola Spjuth - initial API and implementation
 ******************************************************************************/
package net.bioclipse.ds.signsig.bursi;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import libsvm.svm;
import libsvm.svm_model;
import libsvm.svm_node;

import org.apache.log4j.Logger;

import net.bioclipse.cdk.business.Activator;
import net.bioclipse.cdk.business.CDKManager;
import net.bioclipse.cdk.business.ICDKManager;
import net.bioclipse.core.business.BioclipseException;
import net.bioclipse.core.domain.IMolecule;
import net.bioclipse.ds.model.AbstractWarningTest;
import net.bioclipse.ds.model.IDSTest;
import net.bioclipse.ds.model.ITestResult;
import net.bioclipse.ds.model.SubStructureMatch;
import net.bioclipse.ds.model.TestRun;
import net.bioclipse.ds.model.impl.DSException;


public class SignSIcRunner extends AbstractWarningTest implements IDSTest{

    //The logger of the class
    private static final Logger logger = Logger.getLogger(CDKManager.class);

    //The model execution object
    Object modelpredictor;

    // Block of member variables. Can they reside here?
    // Most of the member variables below are hardcoded for the specific example with 198 descriptors.
	// This is an array of the signatures used in the model. All model specific files resides in the directory ModelSpecificFiles.
	public static final String[] signatureList = {"[C]([C][Cl][Cl])","[P]([C][O][O][O])","[C]([O][O])","[S]([C][O][O][O])","[C]([C][F])","[N]([C][P])","[N]([C][O][O])","[N]([C][S][S])","[C]([C][C][C][C])","[C]([Cl][Cl][Cl][S])","[Cl]([C])","[C]([Br][Cl][Cl][Cl])","[P]([Cl][N][N][O])","[C]([Br][Br][Br][N])","[C]([C][C][Cl][F])","[N]([C][H][N])","[C]([C][C][P])","[C]([Br][C][Cl])","[P]([O][O][O][O])","[C]([Cl][N][N])","[P]([N][O][O][S])","[C]([I][I])","[P]([N][N][N][S])","[S]([C])","[C]([C][C][C][S])","[S]([C][Cl][O][O])","[N]([C][C][C][N])","[C]([C][I])","[P]([C][Cl][O][O])","[N]([Br][C][C])","[N]([C][N])","[N]([P][P])","[O]([C][C])","[O]([N][P])","[S]([C][N])","[S]([C][C][N][O])","[C]([C][F][F][F])","[N]([C][N][O])","[Br]([N])","[C]([C][C][C][Cl])","[C]([C][Cl][Cl][N])","[C]([Cl][Cl][Cl][N])","[N]([C][C][N])","[N]([C])","[N]([N][O][O])","[P]([N][N][N][N])","[P]([C][C][C])","[N]([C][S])","[C]([O][O][S])","[C]([C][Cl][F][F])","[O]([C][S])","[Cl]([N])","[C]([C][Cl])","[C]([H][N][N])","[C]([N][N][S])","[N]([C][C][C])","[C]([C][C][Cl])","[C]([O][P])","[H]([C])","[C]([C][C][O][O])","[C]([N][O][S])","[C]([C][O][P][P])","[C]([C][C][O])","[S]([C][S])","[C]([C][Cl][Cl][Cl])","[N]([C][C][Cl])","[O]([P])","[C]([C][C][N][O])","[N]([C][C])","[C]([Cl][N][O])","[N]([C][Cl])","[C]([N][P])","[C]([O])","[C]([Cl][Cl])","[C]([C][P])","[C]([C][O][O])","[C]([Br][Cl])","[C]([C][F][S])","[N]([O][O][O])","[C]([C][O][S])","[C]([Br][C][C])","[C]([N][N][N])","[N]([O])","[C]([S])","[S]([C][C])","[C]([Cl][Cl][N])","[N]([O][S])","[N]([C][C][S])","[C]([C][Cl][F])","[C]([N][N][N][N])","[O]([C][N])","[C]([C][C][F][F])","[S]([C][C][O][O])","[C]([N][N])","[C]([C][N][O][O])","[C]([C][C][F])","[C]([C][Cl][N])","[C]([C][N])","[S]([C][C][O])","[P]([N][N][N][O])","[N]([O][O])","[Cl]([P])","[C]([C][Cl][O])","[C]([C][C][Cl][Cl])","[P]([N][O][O][O])","[P]([C][F][O][O])","[N]([N])","[H]([N])","[C]([C][C][N][N])","[C]([N][N][O])","[N]([C][O])","[I]([C])","[S]([C][P])","[C]([N][S][S])","[C]([C][N][P])","[N]([P])","[C]([C][F][N])","[N]([C][C][O])","[O]([C])","[C]([C][F][F])","[C]([C][C][N])","[C]([C])","[N]([C][C][C][O])","[S]([N][N][O][O])","[C]([C][N][O])","[N]([C][C][H])","[C]([S][S][S])","[N]([C][C][C][C])","[C]([C][S])","[N]([S])","[P]([O][O][O][S])","[C]([Cl][Cl][F][S])","[C]([Br][C][C][C])","[F]([P])","[O]([C][P])","[P]([N][N][O][O])","[N]([N][O])","[C]([N][S])","[S]([O][O][O])","[C]([Cl][Cl][Cl][F])","[C]([C][Cl][S])","[N]([C][C][P])","[N]([N][N][O])","[F]([C])","[C]([C][F][F][O])","[P]([O][O][S][S])","[C]([C][C][I])","[C]([N][O])","[C]([Cl][S])","[C]([Cl][O][S])","[P]([O][O][O])","[C]([Br][Br])","[S]([C][N][O][O])","[C]([C][C][C][O])","[O]([O])","[S]([O][O][O][O])","[N]([N][N])","[C]([C][O])","[C]([O][O][O])","[C]([C][N][S])","[C]([P])","[O]([N])","[O]([S])","[P]([C][C][C][C])","[P]([C][N][O][O])","1","[S]([N][N])","[N]([N][S])","[C]([Br])","[C]([C][C][S])","[C]([C][C][C][F])","[N]([C][H][S])","[C]([C][C][C][N])","[C]([C][C][S][S])","[C]([C][S][S])","[O]([P][P])","[N]([C][N][N])","[C]([Br][Br][C])","[C]([N])","[C]([C][O][P])","[O]([C][O])","[S]([N][O][O][O])","[C]([C][C][Cl][N])","[Br]([C])","[C]([Cl][O][O])","[C]([C][N][N])","[C]([C][C][C])","[S]([C][C][C])","[C]([Br][C])","[C]([C][O][O][O])","[Cl]([S])","[S]([P])","[C]([Cl][N])","[C]([N][O][O])","[C]([C][C][N][S])","[C]([F][F][F][S])","[C]([S][S])","[C]([C][C])"};
	public static final int nrSignatures = 198;
	// These variables are defined by the range file. We include them here to avoid parsing the range file.
	public static final double lower = -1.0;
	public static final double upper = 1.0;
	public static final double[] feature_min = {0.0 , 0.0 , 0.0 , 0.0 , 0.0 , 0.0 , 0.0 , 0.0 , 0.0 , 0.0 , 0.0 , 0.0 , 0.0 , 0.0 , 0.0 , 0.0 , 0.0 , 0.0 , 0.0 , 0.0 , 0.0 , 0.0 , 0.0 , 0.0 , 0.0 , 0.0 , 0.0 , 0.0 , 0.0 , 0.0 , 0.0 , 0.0 , 0.0 , 0.0 , 0.0 , 0.0 , 0.0 , 0.0 , 0.0 , 0.0 , 0.0 , 0.0 , 0.0 , 0.0 , 0.0 , 0.0 , 0.0 , 0.0 , 0.0 , 0.0 , 0.0 , 0.0 , 0.0 , 0.0 , 0.0 , 0.0 , 0.0 , 0.0 , 0.0 , 0.0 , 0.0 , 0.0 , 0.0 , 0.0 , 0.0 , 0.0 , 0.0 , 0.0 , 0.0 , 0.0 , 0.0 , 0.0 , 0.0 , 0.0 , 0.0 , 0.0 , 0.0 , 0.0 , 0.0 , 0.0 , 0.0 , 0.0 , 0.0 , 0.0 , 0.0 , 0.0 , 0.0 , 0.0 , 0.0 , 0.0 , 0.0 , 0.0 , 0.0 , 0.0 , 0.0 , 0.0 , 0.0 , 0.0 , 0.0 , 0.0 , 0.0 , 0.0 , 0.0 , 0.0 , 0.0 , 0.0 , 0.0 , 0.0 , 0.0 , 0.0 , 0.0 , 0.0 , 0.0 , 0.0 , 0.0 , 0.0 , 0.0 , 0.0 , 0.0 , 0.0 , 0.0 , 0.0 , 0.0 , 0.0 , 0.0 , 0.0 , 0.0 , 0.0 , 0.0 , 0.0 , 0.0 , 0.0 , 0.0 , 0.0 , 0.0 , 0.0 , 0.0 , 0.0 , 0.0 , 0.0 , 0.0 , 0.0 , 0.0 , 0.0 , 0.0 , 0.0 , 0.0 , 0.0 , 0.0 , 0.0 , 0.0 , 0.0 , 0.0 , 0.0 , 0.0 , 0.0 , 0.0 , 0.0 , 0.0 , 0.0 , 0.0 , 0.0 , 0.0 , 0.0 , 0.0 , 0.0 , 0.0 , 0.0 , 0.0 , 0.0 , 0.0 , 0.0 , 0.0 , 0.0 , 0.0 , 0.0 , 0.0 , 0.0 , 0.0 , 0.0 , 0.0 , 0.0 , 0.0 , 0.0 , 0.0 , 0.0 , 0.0 , 0.0 , 0.0 , 0.0 , 0.0 , 0.0 , 0.0 , 0.0 , 0.0 , 0.0 , 0.0 , 0.0 };
	public static final double[] feature_max = {2.0 , 2.0 , 3.0 , 4.0 , 1.0 , 6.0 , 6.0 , 1.0 , 6.0 , 1.0 , 12.0 , 1.0 , 1.0 , 1.0 , 3.0 , 2.0 , 3.0 , 1.0 , 2.0 , 2.0 , 1.0 , 1.0 , 1.0 , 2.0 , 1.0 , 1.0 , 1.0 , 1.0 , 1.0 , 1.0 , 8.0 , 3.0 , 14.0 , 1.0 , 1.0 , 1.0 , 2.0 , 1.0 , 1.0 , 8.0 , 1.0 , 1.0 , 2.0 , 13.0 , 1.0 , 3.0 , 1.0 , 2.0 , 1.0 , 1.0 , 2.0 , 3.0 , 6.0 , 1.0 , 2.0 , 6.0 , 8.0 , 4.0 , 1.0 , 3.0 , 1.0 , 1.0 , 23.0 , 2.0 , 2.0 , 3.0 , 6.0 , 1.0 , 32.0 , 1.0 , 3.0 , 1.0 , 6.0 , 1.0 , 1.0 , 7.0 , 1.0 , 1.0 , 4.0 , 1.0 , 10.0 , 6.0 , 1.0 , 2.0 , 3.0 , 1.0 , 1.0 , 1.0 , 1.0 , 1.0 , 4.0 , 8.0 , 2.0 , 6.0 , 1.0 , 5.0 , 2.0 , 10.0 , 1.0 , 1.0 , 1.0 , 1.0 , 2.0 , 2.0 , 1.0 , 1.0 , 2.0 , 3.0 , 1.0 , 3.0 , 2.0 , 6.0 , 2.0 , 2.0 , 1.0 , 1.0 , 2.0 , 2.0 , 39.0 , 1.0 , 23.0 , 12.0 , 1.0 , 1.0 , 29.0 , 3.0 , 1.0 , 1.0 , 4.0 , 1.0 , 2.0 , 1.0 , 1.0 , 1.0 , 6.0 , 1.0 , 2.0 , 2.0 , 1.0 , 1.0 , 1.0 , 3.0 , 1.0 , 19.0 , 1.0 , 1.0 , 6.0 , 2.0 , 1.0 , 1.0 , 2.0 , 1.0 , 2.0 , 4.0 , 1.0 , 1.0 , 2.0 , 15.0 , 2.0 , 2.0 , 1.0 , 12.0 , 12.0 , 1.0 , 1.0 , 1.0 , 1.0 , 1.0 , 1.0 , 4.0 , 1.0 , 1.0 , 2.0 , 1.0 , 1.0 , 1.0 , 2.0 , 1.0 , 6.0 , 1.0 , 4.0 , 1.0 , 1.0 , 10.0 , 1.0 , 3.0 , 16.0 , 1.0 , 3.0 , 1.0 , 1.0 , 2.0 , 1.0 , 2.0 , 2.0 , 1.0 , 1.0 , 35.0 };
	public static final svm_node[] xScaled = new svm_node[198];
	public static final double[] x = new double[198];
	public static svm_model bursiModel = new svm_model();
	public static final String signatureExecutable = "/home/lc/workspace/signatures/Release/signatures --filename /home/lc/Molecules/omeprazole.mol --height 1 --atomtype XYZ";
	//public static final String signatureExecutable = "/home/lc/workspace/signatures/Release/signatures --filename /home/lc/Molecules/Cocaine.mol --height 1 --atomtype XYZ";
	//public static final String signatureExecutable = "/home/lc/workspace/signatures/Release/signatures --filename /home/lc/Molecules/noHHydrogen.mol --height 1 --atomtype XYZ";
	public static Map<Integer,Integer> attributeValues = new HashMap<Integer,Integer>();

	/**
     * Default constructor
     */
    public SignSIcRunner(){
        super();
    }


    /**
     * Set up the predictor, read from file etc
     */
    private void initialize() {

        //TODO: implement
        
    }

	private static double partialDerivative(int component)
	{
		// Component numbering starts from 1.
		double pD, xScaledCompOld;
		xScaledCompOld = xScaled[component-1].value; // Store the old component so we can copy it back.

		// Forward difference high value point.
		xScaled[component-1].value = lower + (upper-lower) * 
			(1.0+x[component-1]-feature_min[component-1])/
			(feature_max[component-1]-feature_min[component-1]);
		
		// Retrieve the decision function value.
		double[] decValues = new double[1]; // We only have two classes so this should be one. Look in svm_predict_values for an explanation. 
		svm.svm_predict_values(bursiModel, xScaled, decValues);
		System.out.println(decValues[0]);
		
		xScaled[component-1].value = xScaledCompOld;
		
		pD = decValues[0];

		return pD;
	}
	
	private static void predict()
	{
		
		try {
			bursiModel = svm.svm_load_model("/home/lc/workspace/cdkws2009/ModelSpecificFiles/bursiSignsXYZ_1.txt.model");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// Do a prediction using libsvm.
		System.out.println(svm.svm_predict(bursiModel, xScaled));
		//double[] decValues = new double[bursiModel.nSV[0]];
		
		// Retrieve the decision function value.
		double lowPointDecisionFuncValue;
		double[] decValues = new double[1]; // We only have two classes so this should be one. Look in svm_predict_values for an explanation. 
		svm.svm_predict_values(bursiModel, xScaled, decValues);
		System.out.println(decValues[0]);
		lowPointDecisionFuncValue = decValues[0];
		
		boolean maximum;
		double highPointDecisionFuncValue;
		if (lowPointDecisionFuncValue > 0)
		{
			maximum = true;
		}
		else
		{
			maximum = false;
		}
		double extremeValue = 0;
		int significantSignatureNr = 1;
		for (int key : attributeValues.keySet()) {
			System.out.println("Keys:" + key);
			highPointDecisionFuncValue = partialDerivative(key);
			if (maximum)
			{
				if (extremeValue < highPointDecisionFuncValue)
				{
					extremeValue = highPointDecisionFuncValue;
					significantSignatureNr = key;
				}
			}
			else
			{
				if (extremeValue > highPointDecisionFuncValue)
				{
					extremeValue = highPointDecisionFuncValue;
					significantSignatureNr = key;
				}
			}
			System.out.println(highPointDecisionFuncValue);			
		}
		System.out.println("Extreme value: " + extremeValue);
		System.out.println("Keys: " + significantSignatureNr);
		System.out.println(signatureList[significantSignatureNr-1]);
	}
	
	
	private static void scale()
	{
		System.out.println("Scaling descriptors.");
		//Initialize xScaled. In this case the lower value is -1. This is defined in the range file.
		for (int i = 0; i < nrSignatures; i++){
			//System.out.println(i);
			//System.out.println(x[i]);
			//System.out.println("feature_max" + feature_max[i]);
			xScaled[i] = new svm_node();
			xScaled[i].index = i + 1;
			xScaled[i].value = lower + (upper-lower) * 
				(x[i]-feature_min[i])/
				(feature_max[i]-feature_min[i]);
			//System.out.println(xScaled[i].value);
		}
	}

	
	private static void predictAndComputeSignificance() {
		System.out.println("Predicting and computing significance.");

		// The unscaled attributes. 
		for (int i = 0; i < nrSignatures; i++){
			int signatureNr = i + 1;
			if (attributeValues.containsKey(signatureNr) ){
				x[i] = attributeValues.get(signatureNr);
				System.out.println("Singature number: " + signatureNr + ", value: " + x[i]);
			}
			else{
				x[i] = 0.0;
			}
		}
		
		scale();
		
	}
	
	private static void createSignatures(){
		Process signatureRun;
		try {
			signatureRun = Runtime.getRuntime().exec(signatureExecutable);
			BufferedReader br = new BufferedReader (new InputStreamReader(signatureRun.getInputStream ()));
	        String line;
	        int lineNr = 0;
	        while ( (line = br.readLine()) != null ){
	        	lineNr = lineNr + 1;
	        	if ( lineNr > 1 ){
	        		//System.out.println(line);
	        		int signatureNr = 0;
	        		Integer currentAttributeValue = 0;
	            	for (String signature : signatureList) {
	            		signatureNr = signatureNr + 1;
	            		if (signature.equals(line)){
	            			//System.out.println("Matching sign: "+signature);
	            			if (attributeValues.containsKey(signatureNr)){
	            				currentAttributeValue = (Integer) attributeValues.get(signatureNr);
	            				attributeValues.put(signatureNr, new Integer(currentAttributeValue + 1));
	            			}
	            			else {
	            				attributeValues.put(signatureNr, new Integer(1));
	            			}
	            		}
					}
				}
	        }
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
    /**
     * Method that takes an IMolecule as input and deliver a list of testresults
     * in the form of SubstructureMatches.
     */
    public List<ITestResult> runWarningTest( IMolecule molecule )
                                                            throws DSException {
        if (modelpredictor==null)
            initialize();

        //Get the CDKManager that can carry out cheminfo stuff
        ICDKManager cdk = Activator.getDefault().getCDKManager();

        //Get the MDL string if you like
        String mdlstring=null;
        try {
            mdlstring=cdk.getMDLMolfileString( molecule );
        } catch ( BioclipseException e ) {
            logger.error("Could not get MDLString from molecule.");
            throw new DSException(e.getMessage());
        }
        
        //TODO Lars: Work your QSAR magic here...
        
        
        //Let's say we get the atoms to highlight back as 1,5,8
        SubStructureMatch match=new SubStructureMatch();
        List<Integer> atoms=new ArrayList<Integer>();
        atoms.add( 1 );
        atoms.add( 5 );
        atoms.add( 8 );
        match.setMatchingAtoms( atoms );
        match.setName( "WEEHOOW" ); //Will appear in GUI

        //We can have multiple hits...
        List<ITestResult> results=new ArrayList<ITestResult>();
        //...but here we only have one
        results.add( match );
        
        return results;
    }


    
    
    
    
    

    /**
     * DO NOT USE THIS; SCHEDULED FOR REMOVAL
     */
    @Deprecated
    public List<ITestResult> runWarningTest( IMolecule molecule, TestRun testrun )
                                                            throws DSException {
        //DO NOT USE THIS; SCHEDULED FOR REMOVAL
        return null;
    }


}
