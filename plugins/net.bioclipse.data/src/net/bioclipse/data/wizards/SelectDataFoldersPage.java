/*******************************************************************************
 * Copyright (c) 2008 The Bioclipse Project and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * www.eclipse.org—epl-v10.html <http://www.eclipse.org/legal/epl-v10.html>
 * 
 * Contributors:
 *     Ola Spjuth - initial API and implementation
 *     
 ******************************************************************************/
package net.bioclipse.data.wizards;

import java.util.ArrayList;
import java.util.List;

import net.bioclipse.data.IDataConstants;

import org.apache.log4j.Logger;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.viewers.CheckStateChangedEvent;
import org.eclipse.jface.viewers.CheckboxTableViewer;
import org.eclipse.jface.viewers.ICheckStateListener;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.Text;

/**
 * Page that reads EP and displays folders in CheckboxTableView.
 * @author ola
 *
 */
public class SelectDataFoldersPage extends WizardPage {

    private Text text;

    //Store folders here
    private ArrayList<InstallableFolder> folders = 
        new ArrayList<InstallableFolder>();

	private NewDataProjectWizard wizard;

    private static final Logger logger = 
        Logger.getLogger(SelectDataFoldersPage.class);
    
    public ArrayList<InstallableFolder> getFolders(){
        return folders;
    }

    /**
     * Provide content for dataview: Folders in data folder.
     * @author ola
     *
     */
    class InstallDataContentProvider implements IStructuredContentProvider{

        @SuppressWarnings("unchecked")
		public Object[] getElements(Object inputElement) {

            if (inputElement instanceof ArrayList) {
                ArrayList folders = (ArrayList) inputElement;
                return folders.toArray(new InstallableFolder[folders.size()]);
            }

            return new Object[0];
        }

        public void dispose() {
        }

        public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
        }
    }

    /**
     * Provide labels for dataview
     * @author ola
     *
     */
    class InstallDataLabelProvider implements ITableLabelProvider{

        public Image getColumnImage(Object element, int columnIndex) {
            return null;
        }

        public String getColumnText(Object element, int columnIndex) {
            if (element instanceof InstallableFolder) {
                InstallableFolder folder = (InstallableFolder) element;
                return folder.getName();
            }

            return "??";
        }

        public void addListener(ILabelProviderListener listener) {
        }

        public void dispose() {
        }

        public boolean isLabelProperty(Object element, String property) {
            return false;
        }

        public void removeListener(ILabelProviderListener listener) {
        }

    }

    /**
     * Create the wizard
     */
    public SelectDataFoldersPage() {
        super("Select data");
        setTitle("Select data");
        setDescription("Select the data folders to install in the project");

    }

    /**
     * Create contents of the wizard
     * @param parent
     */
    public void createControl(Composite parent) {
    	
        //Cache our wizard for convenience
        wizard=(NewDataProjectWizard) getWizard();

        Composite container=new Composite(parent, SWT.NONE);

        GridLayout layout = new GridLayout();
        layout.numColumns = 2;
        container.setLayout(layout);
        container.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

        final Label folderLabel = new Label(container, SWT.NONE);
        folderLabel.setText("Folder");

        final Label descriptionLabel = new Label(container, SWT.NONE);
        descriptionLabel.setText("Description");


        //Add viewer for data folders to install
        Table table=new Table(container, SWT.CHECK | SWT.BORDER);
        GridData da=new GridData(SWT.LEFT, SWT.FILL, false, true);
        da.widthHint=200;
        table.setLayoutData(da);
        CheckboxTableViewer viewer=new CheckboxTableViewer(table);

        viewer.setContentProvider(new InstallDataContentProvider());
        viewer.setLabelProvider(new InstallDataLabelProvider());
        viewer.addCheckStateListener(new ICheckStateListener(){

            public void checkStateChanged(CheckStateChangedEvent event) {
                if (event.getElement() instanceof InstallableFolder) {
                    InstallableFolder folder = (InstallableFolder) event.getElement();
                    folder.setChecked(event.getChecked());
                    checkForCompletion();
                }
            }
        });
        viewer.addSelectionChangedListener(new ISelectionChangedListener(){

            public void selectionChanged(SelectionChangedEvent event) {
                // TODO Auto-generated method stub
                if (event.getSelection() instanceof IStructuredSelection) {
                    IStructuredSelection sel = (IStructuredSelection) event.getSelection();
                    if (sel.getFirstElement() instanceof InstallableFolder) {
                        InstallableFolder folder = (InstallableFolder) sel.getFirstElement();
                        text.setText(folder.getDescription());
                    }
                }
            }
            
        });

        text = new Text(container, SWT.MULTI | SWT.READ_ONLY | SWT.BORDER);
        text.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));


        /*
        URL dataURL=Platform.getBundle(Activator.PLUGIN_ID).getEntry("/data");
        URL fileURL;
        try {
            fileURL = FileLocator.toFileURL(dataURL);
        } catch (IOException e) {
            System.out.println("Could not convert URL to file: " + dataURL);
            return;
        }

        IPath dataPath=new Path(fileURL.getPath());
         */

        ArrayList folders=getInstallableFolders();
        if (folders==null) return;
        if (folders.size()<=0) return;

        //Set data folder as input
        viewer.setInput(folders);

        //Select all by default
        viewer.setAllChecked(true);
        viewer.setSelection(new StructuredSelection(folders.get(0)));
        checkForCompletion();
        
        setControl(container);

    }

    /**
     * Check if all is complete on page
     */
    protected void checkForCompletion() {
        
        setPageComplete(false);
        setErrorMessage(null);

        if (getFolders()==null){
            setErrorMessage("Please select at least one folder");
        }
        for (InstallableFolder folder : folders){
            if (folder.isChecked()==true){
                setErrorMessage(null);
                setPageComplete(true);
                getWizard().getContainer().updateButtons();
                return;
            }
        }

        setErrorMessage("Please select at least one folder to install");
        getWizard().getContainer().updateButtons();
        return;

    }

    /**
     * Read extension point and provide the installable folders
     */
    public ArrayList<InstallableFolder> getInstallableFolders(){

        //Store folders here
        folders=new ArrayList<InstallableFolder>();
        
        IExtensionRegistry registry = Platform.getExtensionRegistry();

        if (registry == null) { // for example, when we are running the tests
            logger.warn("Registry does not exist. If tests are running, "
                    + "this is in order.");
            return null;             // nothing we can do anyway
        }

        IExtensionPoint extensionPoint
        = registry.getExtensionPoint(IDataConstants.INSTALL_DATA_EXTENSION_POINT);

        IExtension[] extensions = extensionPoint.getExtensions();

        for (int i=0; i<extensions.length; i++) {

            IConfigurationElement[] configelements
            = extensions[i].getConfigurationElements();
            for (int j=0; j<configelements.length; j++) {

                String name=configelements[j].getAttribute("name");
                String description=configelements[j].getAttribute("description");
                String location=configelements[j].getAttribute("location");
                String pluginid=configelements[j].getNamespaceIdentifier();
                String wizid=configelements[j].getAttribute("wizard");

                //Use default wizard if no wizard id in extension
                if (wizid==null){
                	wizid=IDataConstants.DEFAULT_INSTALL_WIZARD;
                }

                //If the current wizard is specified in extension, add the folder
                if (wizard.getWizardID().equals(wizid)){
                    InstallableFolder folder=new InstallableFolder(name, description, location, pluginid, wizid);
                    folder.setChecked(true);
                    folders.add(folder);
                    logger.debug("Added installable folder: " + name + " to wizard: " + wizard.getWizardID());
                }

            }
        }

        return folders;

    }


}
