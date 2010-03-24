/* *****************************************************************************
 * Copyright (c) 2009 Ola Spjuth.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Ola Spjuth - initial API and implementation
 ******************************************************************************/
package net.bioclipse.webservices.wizards.newwizards;

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

/**
 * A page offering input of PDBIDs
 * @author ola
 *
 */
public class DownloadPDBPage extends WizardPage {

    private Text txtPDBid;

    protected DownloadPDBPage(String pageName) {
        super( pageName );
    }

    public void createControl( Composite parent ) {
        
        setTitle( "Download PDB files" );
        setDescription( "Please enter one or more PDB ID to download files." );

        Composite composite = new Composite(parent, SWT.NONE);
        GridLayout gl = new GridLayout(2,false);
        composite.setLayout(gl);    

        Label lblPDBID = new Label (composite, SWT.NONE);
        lblPDBID.setText("Enter PDB id's separated by comma: ");
        GridData gd = new GridData(SWT.LEFT,SWT.CENTER, false, false);
        gd.horizontalAlignment = GridData.BEGINNING;
        lblPDBID.setLayoutData(gd);

        txtPDBid = new Text(composite, SWT.BORDER);
        GridData gd2 = new GridData(SWT.LEFT,SWT.CENTER, true, false);
//        gd2.horizontalAlignment = GridData.BEGINNING;
//        gd2.grabExcessHorizontalSpace=true;
        gd2.widthHint=300;
        txtPDBid.setLayoutData(gd2);
        txtPDBid.addModifyListener( new ModifyListener() {
            public void modifyText( ModifyEvent e ) {
                ((DownloadPDBWizard)getWizard()).setPdbids( txtPDBid.getText());
                getWizard().getContainer().updateButtons();
            }
        });
        
        setControl( composite );
        
    }
    
}
