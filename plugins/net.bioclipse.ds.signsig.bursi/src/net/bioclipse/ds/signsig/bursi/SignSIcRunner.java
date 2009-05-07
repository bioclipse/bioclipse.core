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

import java.util.ArrayList;
import java.util.List;

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
