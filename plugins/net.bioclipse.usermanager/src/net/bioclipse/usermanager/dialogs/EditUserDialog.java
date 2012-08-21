/* *****************************************************************************
 * Copyright (c) 2007-2009 Bioclipse Project
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *
 *******************************************************************************/

package net.bioclipse.usermanager.dialogs;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Set;

import net.bioclipse.usermanager.AccountType;
import net.bioclipse.usermanager.NewAccountWizard;
import net.bioclipse.usermanager.UserContainer;
import net.bioclipse.usermanager.AccountType.Property;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ICellModifier;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.ListViewer;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Item;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.PlatformUI;

/**
 * Dialog for editing an user and the users accounts
 *
 * @author jonalv
 *
 */
public class EditUserDialog extends Dialog {

    private static final String ALREADY_SUCH_AN_ACCOUNT
        = "There is already an account of that type.";
    private static final String FILL_IN_REQUIRED
        = "Please fill in the values for all required account properties.";

    private Text accountTypeText;
    private Label accountTypeLabel;
    private TableViewer propertiesTableViewer;
    private Label propertiesLabel;
    private ListViewer accountsListViewer;
    private Group accountGroup;
    private Button deleteAccountButton;
    private Button addAccountButton;
    private Button changeKeyringUserButton;
    private Button editAccountButton;
    private Button showHidePassword;
    private Table propertiesTable;
    private List list;
    private UserContainer sandBoxUserContainer, dummySandbox;
    private EditUserDialogModel model;
    private static final String[] COLUMN_NAMES = { "Property",
                                                  "Value",
                                                  "Required" };

    /**
     * Create the dialog
     * @param parentShell
     */
    public EditUserDialog( Shell parentShell,
                           UserContainer sandBoxUserManager ) {
        super(parentShell);
        this.sandBoxUserContainer = sandBoxUserManager;
        this.model = new EditUserDialogModel(sandBoxUserManager);
    }

    /**
     * Create contents of the dialog
     * @param parent
     */
    @Override
    protected Control createDialogArea(Composite parent) {
        Composite container = (Composite) super.createDialogArea(parent);
        container.setLayout(new FormLayout());

        accountGroup = new Group(container, SWT.NONE);
        accountGroup.setText("Account");
        final FormData formData = new FormData();
        formData.left = new FormAttachment(29, 0);
        formData.top = new FormAttachment(0, 5);
        formData.bottom = new FormAttachment(100, -5);
        formData.right = new FormAttachment(100, -5);
        accountGroup.setLayoutData(formData);
        accountGroup.setLayout(new FormLayout());

        accountsListViewer = new ListViewer(container, SWT.BORDER | SWT.SINGLE);
        accountsListViewer.addSelectionChangedListener(
            new ISelectionChangedListener() {
            /*
             * SELECTION CHANGED ON THE ACCOUNTS LIST
             */
            public void selectionChanged(SelectionChangedEvent event) {

                refreshOnSelectionChanged();
            }
        });
        accountsListViewer.setSorter(new Sorter());
        accountsListViewer.setLabelProvider(new ListLabelProvider());
        accountsListViewer.setContentProvider(new ListContentProvider());
        accountsListViewer.setInput( model.dummyAccounts.keySet() );

        list = accountsListViewer.getList();

        final FormData formData_2 = new FormData();
        formData_2.bottom = new FormAttachment(0, 77);
        formData_2.top = new FormAttachment(0, 60);
        formData_2.right = new FormAttachment(0, 127);
        formData_2.left = new FormAttachment(0, 50);

        final FormData formData_3 = new FormData();
        formData_3.left = new FormAttachment(0, 152);
        formData_3.right = new FormAttachment(100, -295);
        formData_3.top = new FormAttachment(0, 90);

        final FormData formData_1_1 = new FormData();
        formData_1_1.bottom = new FormAttachment(0, 117);
        formData_1_1.top = new FormAttachment(0, 100);

        propertiesLabel = new Label(accountGroup, SWT.NONE);
        final FormData formData_5 = new FormData();
        formData_5.right = new FormAttachment(0, 79);
        formData_5.left = new FormAttachment(0, 5);
        propertiesLabel.setLayoutData(formData_5);
        propertiesLabel.setText("Properties:");

        propertiesTableViewer = new TableViewer(accountGroup, SWT.BORDER);
        propertiesTableViewer.setLabelProvider(new TableLabelProvider());
        propertiesTableViewer.setContentProvider(new TableContentProvider());
        propertiesTable = propertiesTableViewer.getTable();
        formData_5.top = new FormAttachment(propertiesTable, -25, SWT.TOP);
        formData_5.bottom = new FormAttachment(propertiesTable, -5, SWT.TOP);

        /*
         * Table columns
         */
        TableColumn column1 = new TableColumn(propertiesTable, SWT.LEFT, 0);
        column1.setText(COLUMN_NAMES [0]);
        column1.setWidth(100);
        TableColumn column2 = new TableColumn(propertiesTable, SWT.LEFT, 1);
        column2.setText(COLUMN_NAMES[1]);
        column2.setWidth(100);
        TableColumn column3 = new TableColumn(propertiesTable, SWT.LEFT, 2);
        column3.setText(COLUMN_NAMES[2]);
        column3.setWidth(100);
        propertiesTable.setLinesVisible(true);
        propertiesTable.setHeaderVisible(true);

        /*
         * Cell editors
         */
        CellEditor[] editors = new CellEditor[COLUMN_NAMES.length];
        editors[1] = new TextCellEditor(propertiesTable);
        propertiesTableViewer.setCellEditors(editors);
        propertiesTableViewer.setCellModifier( new PropertyCellModifier() );

        final FormData formData_4 = new FormData();
        formData_4.top = new FormAttachment(0, 85);
        formData_4.bottom = new FormAttachment(100, -33);
        formData_4.left = new FormAttachment(propertiesLabel, 0, SWT.LEFT);
        formData_4.right = new FormAttachment(100, -5);
        propertiesTable.setLayoutData(formData_4);
        propertiesTableViewer.setColumnProperties(COLUMN_NAMES);

        accountTypeLabel = new Label(accountGroup, SWT.NONE);
        final FormData formData_11 = new FormData();
        formData_11.top = new FormAttachment(0, 23);
        formData_11.bottom = new FormAttachment(0, 40);
        formData_11.left = new FormAttachment(propertiesLabel, 0, SWT.LEFT);
        accountTypeLabel.setLayoutData(formData_11);
        accountTypeLabel.setText("Account Type:");

        accountTypeText = new Text(accountGroup, SWT.BORDER);
        accountTypeText.setEditable(false);
        final FormData formData_12 = new FormData();
        formData_12.right = new FormAttachment(100, -194);
        formData_12.bottom = new FormAttachment(accountTypeLabel, 0, SWT.BOTTOM);
        formData_12.left = new FormAttachment(accountTypeLabel, 5, SWT.RIGHT);
        accountTypeText.setLayoutData(formData_12);
        accountGroup.setTabList( new Control[] {propertiesLabel, propertiesTable, accountTypeLabel, accountTypeText} );
        final FormData formData_1 = new FormData();
        formData_1.right = new FormAttachment(accountGroup, -2, SWT.LEFT);
        formData_1.left = new FormAttachment(0, 0);
        formData_1.bottom = new FormAttachment(100, -39);
        formData_1.top = new FormAttachment(0, 65);
        list.setLayoutData(formData_1);

        showHidePassword = new Button(accountGroup, SWT.CHECK);
        final FormData formData_13 = new FormData();
        formData_13.top = new FormAttachment( propertiesTable, 0, SWT.BOTTOM );
        formData_13.right = new FormAttachment( propertiesTable, 0, SWT.RIGHT );
        showHidePassword.setText( "Show password" );
        showHidePassword.setLayoutData( formData_13 );
        showHidePassword.addSelectionListener( new SelectionAdapter() {
            /*
             * SHOW OR HIDE PASSWORD 
             */
            public void widgetSelected(SelectionEvent e) {
                refreshTable();
            }
        });

        addAccountButton = new Button(container, SWT.NONE);
        addAccountButton.addSelectionListener(new SelectionAdapter() {
            /*
             * ADD ACCOUNT
             */
            public void widgetSelected(SelectionEvent e) {
                dummySandbox = sandBoxUserContainer.clone();
                NewAccountWizard wizard = new NewAccountWizard(dummySandbox);
                NewAcccountWizardDialog wd = new NewAcccountWizardDialog( 
                                                   PlatformUI.getWorkbench()
                                                   .getActiveWorkbenchWindow()
                                                   .getShell(), wizard );
                
                if (wd.open() == Window.OK) {
                    
                    for( DummyAccount ac : model.dummyAccounts.values() ) {
                        if( ac.accountType.equals( wizard.getAccountType() ) ) {
//                            MessageDialog.openInformation(
//                                    PlatformUI
//                                    .getWorkbench()
//                                    .getActiveWorkbenchWindow()
//                                    .getShell(),
//                                    "Account type already used",
//                                    ALREADY_SUCH_AN_ACCOUNT );
                            return;
                        }
                    }
                    
                    DummyAccount da = new DummyAccount();
                    da.accountId = wizard.getAccountId();
                    da.accountType = dummySandbox.getAccountType( da.accountId );
                    String key = "";
                    for( Property property : da.accountType.getProperties() ) {
                        key = property.getName();
                        da.properties.put(key, dummySandbox.getProperty( da.accountId, key ) );
                    }
                    model.dummyAccounts.put( da.accountId, da );
                    
                    refreshList();
                    accountTypeText.setText(da.accountType.toString());
                    int pos = 0;
                    for( String item : list.getItems() ) {
                        if(da.accountId.equals(extractAccountId(item))) {
                            break;
                        }
                        pos++;
                    }
                    list.select(pos);
                    refreshTable();
                    refreshList();
                    refreshOnSelectionChanged();
                } 

            }

        });
        final FormData formData_6 = new FormData();
        formData_6.bottom = new FormAttachment(list, 0, SWT.TOP);
        formData_6.left = new FormAttachment(list, 0, SWT.LEFT);
        addAccountButton.setLayoutData(formData_6);
        addAccountButton.setText("Add account...");
        
        deleteAccountButton = new Button(container, SWT.NONE);
        deleteAccountButton.addSelectionListener(new SelectionAdapter() {
            /*
             * DELETE ACCOUNT
             */
            public void widgetSelected(SelectionEvent e) {
                String selectedAccountId = 
                        extractAccountId( accountsListViewer.getList()
                                          .getSelection()[0] );

                model.dummyAccounts.remove( selectedAccountId );
                refreshList();
                if(accountsListViewer.getList().getItemCount() > 0) {
                    accountsListViewer.getList().select(0);
                }
                refreshOnSelectionChanged();
                refreshTable();
                refreshList();
            }
        });
        final FormData formData_7 = new FormData();
        formData_7.top = new FormAttachment(list, 0, SWT.BOTTOM);
        formData_7.right = new FormAttachment(accountGroup, -5, SWT.LEFT);
        deleteAccountButton.setLayoutData(formData_7);
        deleteAccountButton.setText("Delete account");

        changeKeyringUserButton = new Button(container, SWT.NONE);
        changeKeyringUserButton.addSelectionListener(new SelectionAdapter() {
            /*
             * CHANGE USER PASSWORD
             */
            public void widgetSelected(SelectionEvent e) {
                ChangePasswordDialog dialog =
                    new ChangePasswordDialog( PlatformUI
                                              .getWorkbench()
                                              .getActiveWorkbenchWindow()
                                              .getShell() );
                if(dialog.open() == Window.OK) {
                    sandBoxUserContainer.changePassword( dialog.getOldPassword(),
                                                    dialog.getNewPassword() );
                }
            }
        });
        final FormData formData_8 = new FormData();
        formData_8.top = new FormAttachment(-2, 21); //(0, 21)
        formData_8.left = new FormAttachment(0, 33);
        changeKeyringUserButton.setLayoutData(formData_8);
        changeKeyringUserButton.setText("Change Bioclipse Account Password");
        
        editAccountButton = new Button(container, SWT.NONE);
        editAccountButton.addSelectionListener(new SelectionAdapter() {
            /*
             * EDIT ACCOUNT
             */
            public void widgetSelected(SelectionEvent e) {
                String selectedAccountId = 
                        extractAccountId( accountsListViewer.getList()
                                          .getSelection()[0] );

                EditAccountDialog dialog = 
                        new EditAccountDialog( PlatformUI
                                               .getWorkbench()
                                               .getActiveWorkbenchWindow()
                                               .getShell(),
                                               model.dummyAccounts.get( selectedAccountId ) );
                if(dialog.open() == Window.OK) {
                    /* To be sure the update is done correctly we do the the 
                     * updating here and not in the dialog */
                    model.dummyAccounts.get( selectedAccountId ).properties.putAll( dialog.getProperties() );
                }
                refreshList();
                refreshOnSelectionChanged();
                refreshTable();
            }
        });
        final FormData formData_9 = new FormData();
        formData_9.top = new FormAttachment(list, 0, SWT.BOTTOM);
        formData_9.left = new FormAttachment(list, 0, SWT.LEFT);
        editAccountButton.setLayoutData(formData_9);
        editAccountButton.setText("Edit account...");
        
        container.setTabList(new Control[] { changeKeyringUserButton,
                                             addAccountButton,
                                             editAccountButton,
                                             deleteAccountButton,
                                             accountGroup,
                                             list } );

      refreshList();
      if(accountsListViewer.getList().getItemCount() > 0) {
          accountsListViewer.getList().select(0);
      }
      refreshOnSelectionChanged();
        
        return container;
    }

    /**
     * Create contents of the button bar
     * @param parent
     */
    @Override
    protected void createButtonsForButtonBar(Composite parent) {
        createButton(parent, IDialogConstants.OK_ID, IDialogConstants.OK_LABEL,
                true);
        createButton(parent, IDialogConstants.CANCEL_ID,
                IDialogConstants.CANCEL_LABEL, false);
    }

    /**
     * Return the initial size of the dialog
     */
    @Override
    protected Point getInitialSize() {
        return new Point(985, 625);
    }

    protected void configureShell(Shell newShell) {
        super.configureShell(newShell);
        newShell.setText("Bioclipse Account Properties");
    }

    private void refreshList() {

        accountsListViewer.setInput( model.dummyAccounts.keySet() );
    }

    private void refreshTable() {
        if( list.getSelection().length > 0 ) {
            propertiesTableViewer.refresh();
        }
    }

    private void refreshOnSelectionChanged() {
        if ( accountsListViewer.getList().getSelection().length == 0 )
            return;
        String selectedAccountId = 
                extractAccountId( accountsListViewer.getList()
                                  .getSelection()[0] );
                
        accountTypeText.setText(
                model.dummyAccounts.get( selectedAccountId)
                                         .accountType.toString() );
        propertiesTableViewer.setInput(
                model.dummyAccounts.get(selectedAccountId) );
    }

    protected void buttonPressed(int buttonId) {
        if (buttonId == IDialogConstants.OK_ID) {

            // We need to move focus away from any open editors before we
            // check if the dialog is completed, so we do this. Doesn't matter
            // what we move focus to, really.
            addAccountButton.setFocus();

            if( !dialogInputIsComplete() ) {
                MessageDialog.openInformation( PlatformUI
                                               .getWorkbench()
                                               .getActiveWorkbenchWindow()
                                               .getShell(),
                                               "Not complete",
                                               FILL_IN_REQUIRED);
                return;
            }
            saveDummyAccountToSandBoxUserManager();
        }
//        UserContainer.fireUpdate();
        super.buttonPressed(buttonId);
    }

    private void saveDummyAccountToSandBoxUserManager() {

        sandBoxUserContainer.clearAccounts();

        for( DummyAccount dm : model.dummyAccounts.values() ) {
            sandBoxUserContainer.createAccount( dm.accountId,
                                              dm.properties,
                                              dm.accountType );
        }
    }

    private boolean dialogInputIsComplete() {

        for(DummyAccount dm : model.dummyAccounts.values() ) {
            for( Property p : dm.accountType.getRequiredProperties() ) {
                if( dm.properties.get( p.getName() ).equals("") ) {
                    return false;
                }
            }
        }
        return true;
    }
    
    private String extractAccountId(String selectedAccountListItem) {
        String selectedAccountId = selectedAccountListItem;
        int endIndex = selectedAccountId.indexOf( " {" );
        if (endIndex > -1)
            selectedAccountId = selectedAccountId.substring( 0, endIndex );

        return selectedAccountId;
    }
    
    /**
     * Label provider for the accounts list
     *
     * @author jonathan
     *
     */
    class ListLabelProvider extends LabelProvider {
        public String getText(Object element) {
            if (model.dummyAccounts.containsKey( element )) {
                return element.toString() + " {" + 
                        model.dummyAccounts.get( element ).accountType
                        .toString() + "}";
            } else 
                return element.toString();
        }
        public Image getImage(Object element) {
            return null;
        }
    }

    /**
     * Content provider for the accounts list
     *
     * @author jonathan
     *
     */
    class ListContentProvider implements IStructuredContentProvider {
        public Object[] getElements(Object inputElement) {
            return ( (Set<?>)inputElement ).toArray();
        }
        public void dispose() {
        }
        public void inputChanged( Viewer viewer,
                                  Object oldInput,
                                  Object newInput ) {
        }
    }

    /**
     * Sorter for the accounts list
     *
     * @author jonathan
     *
     */
    class Sorter extends ViewerSorter {
        public int compare(Viewer viewer, Object e1, Object e2) {
            return e1.toString().compareTo(e2.toString());
        }
    }

    /**
     * Content provider for the properties table
     *
     * @author jonathan
     *
     */
    class TableContentProvider implements IStructuredContentProvider {
        public Object[] getElements(Object inputElement) {
            DummyAccount dm = (DummyAccount)inputElement;
            HashMap<String, String> properties
                = (HashMap<String, String>)dm.properties;
            ArrayList<ArrayList<String>> rows
                = new ArrayList<ArrayList<String>>();
            for( String key : properties.keySet() ) {
                ArrayList<String> row = new ArrayList<String>();
                row.add(key);
                if (dm.accountType.getProperty(key).isSecret() &&
                        !showHidePassword.getSelection() ) {
                    String mask = "";
                    for (int i = 0; i < properties.get( key ).length(); i++)
                        mask += "\u25CF";
                    row.add(mask);
                } else
                    row.add(properties.get(key));
                
                row.add(dm.accountType.getProperty(key).isRequired()+"");
                rows.add(row);
            }
            return rows.toArray();
        }
        public void dispose() {
        }
        public void inputChanged( Viewer viewer,
                                  Object oldInput,
                                  Object newInput ) {
        }
    }

    /**
     * Label provider for the properties table
     *
     * @author jonathan
     *
     */
    class TableLabelProvider extends LabelProvider
                             implements ITableLabelProvider {
        public String getColumnText(Object element, int columnIndex) {
            return ( (ArrayList<?>)element ).get(columnIndex).toString();
        }
        public Image getColumnImage(Object element, int columnIndex) {
            return null;
        }
    }

    /**
     * Cell modifier for the properties table
     *
     * @author jonathan
     *
     */
    class PropertyCellModifier implements ICellModifier {

        public boolean canModify(Object element, String property) {

            int columnIndex = Arrays.asList(COLUMN_NAMES).indexOf(property);
            if( columnIndex == 1) {
                return true;
            }
            return false;
        }

        public Object getValue(Object element, String property) {

            int columnIndex = Arrays.asList(COLUMN_NAMES).indexOf(property);
            ArrayList<?> row = (ArrayList<?>)element;

            return row.get(columnIndex).toString();
        }

        public void modify(Object element, String property, Object value) {

            int columnIndex = Arrays.asList(COLUMN_NAMES).indexOf(property);
            if(element instanceof Item) {
                element = ((Item) element).getData();
            }
            ArrayList<?> row = (ArrayList<?>)element;

            switch (columnIndex) {
            case 0:
                break;

            case 1:
                String accountId = extractAccountId(accountsListViewer.getList()
                                  .getSelection()[0]);
                model.dummyAccounts.get( accountId ).
                        properties.put( (String)row.get(0), (String)value);
                refreshTable();
                break;
            default:
                break;
            }
        }
    }

    /**
     * Data holder for the edit super user dialog that can be thrown away if
     * canceled, or stored if "Ok" is pressed.
     *
     * @author jonathan
     *
     */
    class EditUserDialogModel {

        HashMap<String, DummyAccount> dummyAccounts
            = new HashMap<String, DummyAccount>();

        public EditUserDialogModel(UserContainer userContainer) {

            for ( String accountId : userContainer
                                     .getLoggedInUsersAccountNames() ) {

                DummyAccount d = new DummyAccount();
                d.accountId    = accountId;
                d.accountType  = userContainer.getAccountType(accountId);

                for( String property : userContainer
                                       .getPropertyKeys(accountId) ) {
                    /*
                     * getProperty decrypts the property value
                     */
                    d.properties.put(property, userContainer
                                               .getProperty( accountId,
                                                             property ));
                }
                dummyAccounts.put(d.accountId, d);
            }
        }
    }

    /**
     * Simple data storing class representing an account.
     *
     * @author jonathan
     *
     */
    class DummyAccount {

        String accountId = "";
        AccountType accountType;

        HashMap<String, String> properties = new HashMap<String, String>();
    }
}
