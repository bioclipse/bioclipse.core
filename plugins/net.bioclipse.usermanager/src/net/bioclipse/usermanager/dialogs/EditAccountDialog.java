/* *****************************************************************************
 * Copyright (c) 2007-2009, 2012 Bioclipse Project
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     
 ******************************************************************************/
package net.bioclipse.usermanager.dialogs;

import java.util.HashMap;
import java.util.Iterator;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

/**
 * A dialog for changing the properties of an account
 * 
 * @author Klas Jšnsson (klas.joensson@gmail.com)
 *
 */
public class EditAccountDialog extends Dialog {
    
    private HashMap<String, String> properties;
    private Label[] propertiesNames;
    private Text[] propertiesTxts;
    private Button showPassword;
    private EditUserDialog.DummyAccount thisAccount;
    
    /**
     * Create the dialog
     * @param parentShell
     */
    protected EditAccountDialog(Shell shell, 
                                EditUserDialog.DummyAccount account) {

        super( shell );
        this.properties = account.properties;
        this.thisAccount = account;
    }

    /**
     * Create contents of the dialog
     * @param parent 
     */
    @Override
    protected Control createDialogArea(Composite parent) {
        Composite container = (Composite) super.createDialogArea(parent);
        container.setLayout( new GridLayout() );
        
        Group propertiesGroup = new Group(container, SWT.NONE);
        propertiesGroup.setLayout( new GridLayout(2, false) );;
        propertiesGroup.setText( "Properties of "+thisAccount.accountId );
        GridData propertiesGroupData = new GridData();
        propertiesGroupData.horizontalSpan = 2;
        propertiesGroupData.horizontalAlignment = SWT.FILL;
        propertiesGroup.setLayoutData( propertiesGroupData );

        int size = properties.size();
        propertiesNames = new Label[size];
        propertiesTxts = new Text[size];
        GridData txtData = new GridData(SWT.FILL, SWT.NONE, true, true);
        Iterator<String> keyItr = properties.keySet().iterator();
        int index = 0;
        String keyName = "";
        while ( keyItr.hasNext() ) {
            keyName = keyItr.next();
            propertiesNames[index] = new Label(propertiesGroup, SWT.NONE);
            propertiesNames[index].setText( keyName );
            propertiesTxts[index] = new Text(propertiesGroup, SWT.BORDER);
            propertiesTxts[index].setText( properties.get( keyName ) );
            propertiesTxts[index].setLayoutData( txtData );
            index++;
        }
        
        Composite bottomCompsite = new Composite(container, SWT.NONE);
        bottomCompsite.setLayout( new GridLayout( 2, true ) );
        new Label(bottomCompsite, SWT.NONE);
        showPassword = new Button(bottomCompsite, SWT.CHECK);
        showPassword.addSelectionListener( new SelectionAdapter() {
            public void widgetSelected(SelectionEvent e) {
                updateTxtFeilds();
            }
        } );
        showPassword.setText( "Show password" );
        
        updateTxtFeilds();
        
        return container;
    }
    
    private void updateTxtFeilds() {
        for (int i = 0; i < propertiesTxts.length; i++) {
            if (thisAccount.accountType.getProperty( propertiesNames[i].getText() ).isSecret() ) {
                if (!showPassword.getSelection()) 
                    propertiesTxts[i].setEchoChar( '\u25CF' );
                 else
                     propertiesTxts[i].setEchoChar( '\0' );
            }
                
        }
    }
    
    protected void buttonPressed(int buttonId) {
        if (buttonId == IDialogConstants.OK_ID) {
            /*The last thing we do is to update the hash-map with the 
             * properties*/
            for (int i = 0; i < propertiesNames.length; i++) {
                properties.put( propertiesNames[i].getText(), propertiesTxts[i].getText() );
            }
        }
        super.buttonPressed(buttonId);
    }
    
    /**
     * A method to get the (chanced) properties. 
     * @return 
     */
    protected HashMap<String, String> getProperties() {
        return properties;
    }
}
