/*******************************************************************************
 *Copyright (c) 2008 The Bioclipse Team and others.
 *All rights reserved. This program and the accompanying materials
 *are made available under the terms of the Eclipse Public License v1.0
 *which accompanies this distribution, and is available at
 *http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Ola Spjuth - core API and implementation
 *******************************************************************************/
package net.bioclipse.ui.prefs;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.bioclipse.ui.Activator;

import org.apache.log4j.Logger;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.ui.PlatformUI;

public class UpdateSitesPreferencePage extends PreferencePage implements
IWorkbenchPreferencePage {

    //Init logger
    private static final Logger logger = Logger.getLogger(UpdateSitesPreferencePage.class.toString());

    private static IPreferenceStore prefsStore=Activator.getDefault().getPreferenceStore();
    private List<String[]> appList;
    //	private CheckboxTableViewer checkboxTableViewer;
    private TableViewer checkboxTableViewer;

    private Button rememberButton;

    private Button openDialogButton;

    public UpdateSitesPreferencePage() {
        super();
    }



    /**
     * The label provider for the table that displays 2 columns: name and URL
     * @author ola
     *
     */
    class ApplicationsLabelProvider extends LabelProvider implements ITableLabelProvider {

        public Image getColumnImage(Object element, int columnIndex) {
            return null;
        }

        public String getColumnText(Object element, int index) {
            if (!(element instanceof String[])) return "Wrong type in column text";
            String[] retList = (String[]) element;

            if (index==0){
                if (retList.length>0)
                    return retList[0];
                else
                    return "NA";
            }
            else if (index==1){
                if (retList.length>1)
                    return retList[1];
                else
                    return "NA";

            }
            else
                return "???";
        }

    }

    class ApplicationsContentProvider implements IStructuredContentProvider {
        @SuppressWarnings("unchecked")
        public Object[] getElements(Object inputElement) {
            if (inputElement instanceof ArrayList) {
                ArrayList retList = (ArrayList) inputElement;
                return retList.toArray();
            }
            return new Object[0];

        }
        public void dispose() {
        }
        public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
        }
    }


    public Control createContents(Composite parent) {
        Composite container = new Composite(parent, SWT.NULL);
        setSize(new Point(600,420));
        container.setSize(600,420);

        //		checkboxTableViewer = CheckboxTableViewer.newCheckList(container, SWT.BORDER);
        checkboxTableViewer = new TableViewer(container, SWT.BORDER | SWT.SINGLE);
        checkboxTableViewer.setContentProvider(new ApplicationsContentProvider());
        checkboxTableViewer.setLabelProvider(new ApplicationsLabelProvider());
        final Table table = checkboxTableViewer.getTable();

        table.setBounds(10, 10, 450, 345);

        table.setHeaderVisible(true);
        table.setLinesVisible(true);

        TableColumn tableColumn = new TableColumn(table, SWT.LEFT);
        tableColumn.setText("Name");
        tableColumn.setWidth(180);
        TableColumn tableColumn2 = new TableColumn(table, SWT.LEFT);
        tableColumn2.setText("URL");
        tableColumn2.setWidth(270);

        appList=getPreferencesFromStore();
        checkboxTableViewer.setInput(appList);



        final Button addButton = new Button(container, SWT.NONE);
        addButton.setBounds(460, 25, 100, 35);
        addButton.setText("Add");
        addButton.addMouseListener(new MouseAdapter() {
            public void mouseUp(MouseEvent e) {

                UpdateSitesEditDialog dlg=new UpdateSitesEditDialog(getShell());
                dlg.open();

                String[] ret=dlg.getUpdateSites();
                if (ret.length==2){
                    appList.add(ret);
                    checkboxTableViewer.refresh();
                }
            }
        });

        final Button editButton = new Button(container, SWT.NONE);
        editButton.setBounds(460, 70, 100, 35);
        editButton.setText("Edit");
        editButton.addMouseListener(new MouseAdapter() {
            public void mouseUp(MouseEvent e) {

                //Get selection from viewer
                ISelection sel=checkboxTableViewer.getSelection();
                if (!(sel instanceof IStructuredSelection)) {
                    logger.debug("Item of wrong type selected.");
                    showMessage("Please select an entry to edit first.");
                    return;
                }

                IStructuredSelection ssel = (IStructuredSelection) sel;
                Object obj=ssel.getFirstElement();

                if (!(obj instanceof String[])) {
                    logger.debug("Object of wrong type selected.");
                    showMessage("Please select an entry to edit first.");
                    return;
                }

                String[] chosen = (String[]) obj;
                //				logger.debug("(:) " + chosen[0]);

                UpdateSitesEditDialog dlg=new UpdateSitesEditDialog(getShell(), chosen[0], chosen[1]);

                dlg.open();

                String[] ret=dlg.getUpdateSites();
                //If OK pressed
                if (dlg.getReturnCode()==0){
                    if (ret.length==2){
                        chosen[0]=ret[0]; //ext
                        chosen[1]=ret[1]; //url
                        checkboxTableViewer.refresh();
                    }
                    else{
                        logger.debug("Error getting result from dialog!");
                        showMessage("Error getting result from dialog.");
                    }
                }

            }
        });

        final Button removeButton = new Button(container, SWT.NONE);
        removeButton.setBounds(460, 120, 100, 35);
        removeButton.setText("Remove");
        removeButton.addMouseListener(new MouseAdapter() {
            public void mouseUp(MouseEvent e) {

                //Get selection from viewer
                if(checkboxTableViewer.getSelection() instanceof IStructuredSelection) {
                    IStructuredSelection selection = (IStructuredSelection)checkboxTableViewer.getSelection();
                    Object[] objSelection=selection.toArray();

                    for (int i=0;i<objSelection.length;i++){
                        if (objSelection[i] instanceof String[]) {
                            String[] row = (String[]) objSelection[i];
                            if (appList.contains(row)){
                                appList.remove(row);
                            }
                        }
                    }
                    checkboxTableViewer.refresh();
                }


            }
        });

        //Add remember button
        rememberButton = new Button(container, SWT.CHECK);
        rememberButton.setBounds(10, 360, 300, 16);
        rememberButton.setText("Run automatic updates on startup");

        //Add remember button
        openDialogButton = new Button(container, SWT.CHECK);
        openDialogButton.setBounds(10, 380, 300, 16);
        openDialogButton.setText("Install automatic updates in background");

        //Sync with preference

        boolean skipUpdate=prefsStore.getBoolean(
                           IPreferenceConstants.SKIP_UPDATE_ON_STARTUP );
//        boolean skipUpdate=Activator.getDefault().getDialogSettings().getBoolean( 
//              net.bioclipse.ui.dialogs.IDialogConstants.SKIP_UPDATE_ON_STARTUP );

        boolean skipDialog=prefsStore.getBoolean( 
                           IPreferenceConstants.SKIP_UPDATE_DIALOG_ON_STARTUP );
//        boolean skipDialog=Activator.getDefault().getDialogSettings().getBoolean( 
//                 net.bioclipse.ui.dialogs.IDialogConstants.SKIP_UPDATE_DIALOG_ON_STARTUP );

        logger.debug("Preference said SKIP_UPDATE_ON_STARTUP: " + skipUpdate);
        logger.debug("Preference said SKIP_UPDATE_DIALOG_ON_STARTUP: " + skipDialog);

        //Meaning is inverted
        rememberButton.setSelection( !skipUpdate );
        //Meaning is not inverted here :)
        openDialogButton.setSelection( skipDialog );


        if (table.getItemCount()>0)
            table.setSelection(0);
        return container;
    }


    public void init(IWorkbench workbench) {
        setPreferenceStore(Activator.getDefault().getPreferenceStore());
    }

    /**
     * Override to store results
     */
    public boolean performOk() {

        String value=convertToPreferenceString(appList);
        logger.debug("Update sites prefs to store: " + value);
        prefsStore.setValue(IPreferenceConstants.UPDATE_SITES,value);

        //Save remember auto updates from checkbox
        boolean skipUpdate=rememberButton.getSelection();
        prefsStore.setValue( IPreferenceConstants.SKIP_UPDATE_ON_STARTUP, !skipUpdate );
        logger.debug("Stored checkbox SKIP_UPDATE_ON_STARTUP=" + !skipUpdate);

        //Save remember OPEN DIALOG from checkbox
        boolean skipDialog=openDialogButton.getSelection();
        prefsStore.setValue( IPreferenceConstants.SKIP_UPDATE_DIALOG_ON_STARTUP, skipDialog);
        logger.debug("Stored checkbox SKIP_UPDATE_DIALOG_ON_STARTUP=" + skipDialog);

        //Save prefs as this must be done explicitly
        Activator.getDefault().savePluginPreferences();

        
//        Activator.getDefault().getDialogSettings().put(
//                                                       net.bioclipse.ui.dialogs.IDialogConstants.SKIP_UPDATE_ON_STARTUP,
//                                                       !skipUpdate );

//        Activator.getDefault().getDialogSettings().put(
//                                                       net.bioclipse.ui.dialogs.IDialogConstants.SKIP_UPDATE_DIALOG_ON_STARTUP,
//                                                       skipDialog );

        return true;
    }

    /**
     * @return List<String[]> containing the preferences
     * 
     */
    public static List<String[]> getPreferencesFromStore() {


        String entireString=prefsStore.getString(IPreferenceConstants.UPDATE_SITES);

        return convertPreferenceStringToArraylist(entireString);
    }


    /**
     * @return List<String[]> containing the default preferences
     * 
     */
    public static List<String[]> getDefaultPreferencesFromStore() {
        String entireString=prefsStore.getDefaultString(IPreferenceConstants.UPDATE_SITES);

        return convertPreferenceStringToArraylist(entireString);
    }

    /**
     * Converts input to arraylist using delimiters from BioclipseConstants
     * @param entireString
     * @return
     */
    public static List<String[]> convertPreferenceStringToArraylist(String entireString) {
        List<String[]> myList=new ArrayList<String[]>();
        //		logger.debug("prefs read from store: " + entireString);
        String[] ret=entireString.split(IPreferenceConstants.PREFERENCES_OBJECT_DELIMITER);
        String[] partString=new String[0];
        for (int i = 0; i < ret.length; i++) {
            partString = ret[i].split(IPreferenceConstants.PREFERENCES_DELIMITER);
            myList.add(partString);
        }

        if (ret.length==1){
            if (partString.length<2){
                //We should only have "" in string[]
                logger.debug("UpdateSite prefs is not in correct format, hence cleared");
                myList.clear();
                //				myList.add(new String[]{"",""});
            }
        }
        return myList;

    }


    /**
     * 
     * 
     * @param appList2
     * @return
     */
    private String convertToPreferenceString(List<String[]> appList2) {
        Iterator<String[]> it=appList2.iterator();
        String ret="";

        // TODO: update to handle short and empty strings

        while (it.hasNext()){
            String[] str=(String[]) it.next();
            String singleRet="";
            for (int i=0; i<str.length;i++){
                singleRet = singleRet + str[i];
                if ((i+1)<str.length) { // there is another column
                    singleRet += IPreferenceConstants.PREFERENCES_DELIMITER;
                }
            }
            ret=ret + singleRet;
            if (it.hasNext()) { // there is another row
                ret += IPreferenceConstants.PREFERENCES_OBJECT_DELIMITER;
            }
        }
        return ret;
    }

    protected void performDefaults() {
        super.performDefaults();

        appList=getDefaultPreferencesFromStore();
        checkboxTableViewer.setInput(appList);

    }

    private void showMessage(String message) {
        MessageDialog.openInformation(
                                      PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),
                                      "Update Sites Message",
                                      message);
    }

}
