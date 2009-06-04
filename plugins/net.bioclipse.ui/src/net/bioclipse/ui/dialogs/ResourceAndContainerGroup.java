/*******************************************************************************
 * Copyright (c) 2000, 2007 IBM Corporation and others.
 *                     2009 Egon Willighagen <egonw@user.sf.net>
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *      Alexander Fedorov <Alexander.Fedorov@borland.com>
 *          - Bug 172000 [Wizards] WizardNewFileCreationPage should support overwriting existing resources
 *******************************************************************************/
package net.bioclipse.ui.dialogs;

import java.util.List;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.internal.ide.IDEWorkbenchMessages;
import org.eclipse.ui.internal.ide.misc.ContainerSelectionGroup;
import org.openscience.cdk.io.formats.IResourceFormat;

/**
 * Workbench-level composite for resource and container specification by the
 * user. Services such as field validation are performed by the group. The group
 * can be configured to accept existing resources, or only new resources.
 */
public class ResourceAndContainerGroup implements Listener {
    // problem identifiers
    
    /**
     * Constant for no problem.
     */
    public static final int PROBLEM_NONE = 0;
    /**
     * Constant for empty resource.
     */
    public static final int PROBLEM_RESOURCE_EMPTY = 1;

    /**
     * Constant for resource already exists.
     */
    public static final int PROBLEM_RESOURCE_EXIST = 2;

    /**
     * Constant for invalid path.
     */
    public static final int PROBLEM_PATH_INVALID = 4;

    /**
     * Constant for empty container.
     */
    public static final int PROBLEM_CONTAINER_EMPTY = 5;

    /**
     * Constant for project does not exist.
     */
    public static final int PROBLEM_PROJECT_DOES_NOT_EXIST = 6;

    /**
     * Constant for invalid name.
     */
    public static final int PROBLEM_NAME_INVALID = 7;

    
    /**
     * Constant for path already occupied.
     */
    public static final int PROBLEM_PATH_OCCUPIED = 8;

    // the client to notify of changes
    private Listener client;

    // whether to allow existing resources
    private boolean allowExistingResources = false;

    // show closed projects in the tree, by default
    private boolean showClosedProjects = true;

    // problem indicator
    private String problemMessage = "";//$NON-NLS-1$

    private int problemType = PROBLEM_NONE;

    // widgets
    private ContainerSelectionGroup containerGroup;

    private Text resourceNameField;
    private Combo resourceTypeCombo;

    private List<IResourceFormat> formats;

    // constants
    private static final int SIZING_TEXT_FIELD_WIDTH = 250;

    /**
     * Create an instance of the group to allow the user to enter/select a
     * container and specify a resource name.
     * 
     * @param parent
     *            composite widget to parent the group
     * @param client
     *            object interested in changes to the group's fields value
     * @param resourceFieldLabel
     *            label to use in front of the resource name field
     * @param resourceType
     *            one word, in lowercase, to describe the resource to the user
     *            (file, folder, project)
     */
    public ResourceAndContainerGroup(Composite parent, Listener client,
            String resourceFieldLabel, List<IResourceFormat> formats) {
        this(parent, client, resourceFieldLabel, formats, true);
    }

    /**
     * Create an instance of the group to allow the user to enter/select a
     * container and specify a resource name.
     * 
     * @param parent
     *            composite widget to parent the group
     * @param client
     *            object interested in changes to the group's fields value
     * @param resourceFieldLabel
     *            label to use in front of the resource name field
     * @param resourceType
     *            one word, in lowercase, to describe the resource to the user
     *            (file, folder, project)
     * @param showClosedProjects
     *            whether or not to show closed projects
     */
    public ResourceAndContainerGroup(Composite parent, Listener client,
            String resourceFieldLabel, List<IResourceFormat> formats,
            boolean showClosedProjects) {
        this(parent, client, resourceFieldLabel, formats,
                showClosedProjects, SWT.DEFAULT);
    }

    /**
     * Create an instance of the group to allow the user to enter/select a
     * container and specify a resource name.
     * 
     * @param parent
     *            composite widget to parent the group
     * @param client
     *            object interested in changes to the group's fields value
     * @param resourceFieldLabel
     *            label to use in front of the resource name field
     * @param resourceType
     *            one word, in lowercase, to describe the resource to the user
     *            (file, folder, project)
     * @param showClosedProjects
     *            whether or not to show closed projects
     * @param heightHint
     *            height hint for the container selection widget group
     */
    public ResourceAndContainerGroup(Composite parent, Listener client,
            String resourceFieldLabel, List<IResourceFormat> formats,
            boolean showClosedProjects, int heightHint) {
        super();
        this.showClosedProjects = showClosedProjects;
        this.formats = formats;
        createContents(parent, resourceFieldLabel, heightHint);
        this.client = client;
    }

    /**
     * Returns a boolean indicating whether all controls in this group contain
     * valid values.
     * 
     * @return boolean
     */
    public boolean areAllValuesValid() {
        return problemType == PROBLEM_NONE;
    }

    /**
     * Creates this object's visual components.
     * 
     * @param parent
     *            org.eclipse.swt.widgets.Composite
     * @param heightHint
     *            height hint for the container selection widget group
     */
    protected void createContents(Composite parent, String resourceLabelString,
            int heightHint) {

        Font font = parent.getFont();
        // server name group
        Composite composite = new Composite(parent, SWT.NONE);
        GridLayout layout = new GridLayout();
        layout.marginWidth = 0;
        layout.marginHeight = 0;
        composite.setLayout(layout);
        composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
        composite.setFont(font);

        // container group
        if (heightHint == SWT.DEFAULT) {
            containerGroup = new ContainerSelectionGroup(composite, this, true,
                    null, showClosedProjects);
        } else {
            containerGroup = new ContainerSelectionGroup(composite, this, true,
                    null, showClosedProjects, heightHint,
                    SIZING_TEXT_FIELD_WIDTH);
        }

        // resource group
        Composite nameGroup = new Composite(composite, SWT.NONE);
        layout = new GridLayout();
        layout.numColumns = 2;
        layout.marginWidth = 0;
        nameGroup.setLayout(layout);
        nameGroup.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_FILL
                | GridData.GRAB_HORIZONTAL));
        nameGroup.setFont(font);

        Label label = new Label(nameGroup, SWT.NONE);
        label.setText(resourceLabelString);
        label.setFont(font);

        // resource name entry field
        resourceNameField = new Text(nameGroup, SWT.BORDER);
        resourceNameField.addListener(SWT.Modify, this);
        resourceNameField.addFocusListener(new FocusAdapter() {
            public void focusLost(FocusEvent e) {
                handleFileExtensionUpdate();
            }
        });
        GridData data = new GridData(GridData.HORIZONTAL_ALIGN_FILL
                | GridData.GRAB_HORIZONTAL);
        data.widthHint = SIZING_TEXT_FIELD_WIDTH;
        resourceNameField.setLayoutData(data);
        resourceNameField.setFont(font);

        // resource type label
        label = new Label(nameGroup, SWT.NONE);
        label.setText("File type:");
        label.setFont(font);

        // resource type entry field
        resourceTypeCombo = new Combo(nameGroup, SWT.BORDER);
        for (IResourceFormat format : this.formats) {
            resourceTypeCombo.add(format.getFormatName());
        }
        // the first format is the default.
        resourceTypeCombo.setText(formats.get(0).getFormatName());
        resourceTypeCombo.addListener(SWT.Modify, this);
        resourceTypeCombo.addModifyListener(new ModifyListener() {
            public void modifyText(ModifyEvent e) {
                handleFileExtensionUpdate();
            }
        });
        resourceTypeCombo.addFocusListener(new FocusAdapter() {
            public void focusLost(FocusEvent e) {
                handleFileExtensionUpdate();
            }
        });
        data = new GridData(GridData.HORIZONTAL_ALIGN_FILL
                | GridData.GRAB_HORIZONTAL);
        data.widthHint = SIZING_TEXT_FIELD_WIDTH;
        resourceTypeCombo.setLayoutData(data);
        resourceTypeCombo.setFont(font);

        validateControls();
    }

    /**
     * Returns the path of the currently selected container or null if no
     * container has been selected. Note that the container may not exist yet if
     * the user entered a new container name in the field.
     * 
     * @return The path of the container, or <code>null</code>
     */
    public IPath getContainerFullPath() {
        return containerGroup.getContainerFullPath();
    }

    /**
     * Returns an error message indicating the current problem with the value of
     * a control in the group, or an empty message if all controls in the group
     * contain valid values.
     * 
     * @return java.lang.String
     */
    public String getProblemMessage() {
        return problemMessage;
    }

    /**
     * Returns the type of problem with the value of a control in the group.
     * 
     * @return one of the PROBLEM_* constants
     */
    public int getProblemType() {
        return problemType;
    }

    /**
     * Returns a string that is the name of the chosen resource, or an empty
     * string if no resource has been entered. <br>
     * <br>
     * The name will include the resource extension if the preconditions are
     * met.
     * 
     * @see ResourceAndContainerGroup#setResourceExtension(String)
     * 
     * @return The resource name
     * @since 3.3
     */
    public String getResource() {
        String resource = resourceNameField.getText();
        IResourceFormat resourceFormat = getSelectedFormat();
        if (useResourceExtension()) {
            return resource + '.' + resourceFormat.getPreferredNameExtension();
        }
        return resource;
    }

    private IResourceFormat getSelectedFormat() {
        int selection = resourceTypeCombo.getSelectionIndex();
        if (selection == -1) selection = 0;
        IResourceFormat resourceFormat = formats.get(selection);
        return resourceFormat;
    }

    /**
     * Returns the resource extension.
     * 
     * @return The resource extension or <code>null</code>.
     * @see ResourceAndContainerGroup#setResourceExtension(String)
     * @since 3.3
     */
    public String getResourceExtension() {
        return getSelectedFormat().getPreferredNameExtension();
    }

    /**
     * Determines whether the resource extension should be added to the resource
     * name field. <br>
     * <br>
     * 
     * @see ResourceAndContainerGroup#setResourceExtension(String)
     * @return <code>true</code> if the preconditions are met; otherwise,
     *         <code>false</code>.
     * @since 3.3
     */
    private boolean useResourceExtension() {
        String resource = resourceNameField.getText();
        String resourceExtension = getSelectedFormat().getPreferredNameExtension();
        if ((resourceExtension != null) && (resourceExtension.length() > 0)
                && (resource.length() > 0)
                && (resource.endsWith('.' + resourceExtension) == false)) {
            return true;
        }
        return false;
    }

    private void handleFileExtensionUpdate() {
        if (useResourceExtension()) {
            String fileName = resourceNameField.getText();
            fileName = removeRecognizedExtension(fileName);
            setResource(
                fileName + '.' + getSelectedFormat().getPreferredNameExtension()
            );
        }
    }

    private String removeRecognizedExtension(String resourceName) {
        for (IResourceFormat format : formats) {
            for (String extension : format.getNameExtensions()) {
                String postfix = "." + extension;
                if (resourceName.endsWith(postfix)) {
                    resourceName = resourceName.substring(
                        0, resourceName.length() - postfix.length()
                    );
                    return resourceName;
                }
            }
        }
        return resourceName;
    }

    /**
     * Handles events for all controls in the group.
     * 
     * @param e
     *            org.eclipse.swt.widgets.Event
     */
    public void handleEvent(Event e) {
        validateControls();
        if (client != null) {
            client.handleEvent(e);
        }
    }

    /**
     * Sets the flag indicating whether existing resources are permitted.
     * @param value
     */
    public void setAllowExistingResources(boolean value) {
        allowExistingResources = value;
    }

    /**
     * Sets the value of this page's container.
     * 
     * @param path
     *            Full path to the container.
     */
    public void setContainerFullPath(IPath path) {
        IResource initial = ResourcesPlugin.getWorkspace().getRoot()
                .findMember(path);
        if (initial != null) {
            if (!(initial instanceof IContainer)) {
                initial = initial.getParent();
            }
            containerGroup.setSelectedContainer((IContainer) initial);
        }
        validateControls();
    }

    /**
     * Gives focus to the resource name field and selects its contents
     */
    public void setFocus() {
        // select the whole resource name.
        resourceNameField.setSelection(0, resourceNameField.getText().length());
        resourceNameField.setFocus();
    }

    /**
     * Sets the value of this page's resource name.
     * 
     * @param value
     *            new value
     */
    public void setResource(String value) {
        resourceNameField.setText(value);
        validateControls();
    }

    /**
     * Returns a <code>boolean</code> indicating whether a container name
     * represents a valid container resource in the workbench. An error message
     * is stored for future reference if the name does not represent a valid
     * container.
     * 
     * @return <code>boolean</code> indicating validity of the container name
     */
    protected boolean validateContainer() {
        IPath path = containerGroup.getContainerFullPath();
        if (path == null) {
            problemType = PROBLEM_CONTAINER_EMPTY;
            problemMessage = IDEWorkbenchMessages.ResourceGroup_folderEmpty;
            return false;
        }
        IWorkspace workspace = ResourcesPlugin.getWorkspace();
        String projectName = path.segment(0);
        if (projectName == null
                || !workspace.getRoot().getProject(projectName).exists()) {
            problemType = PROBLEM_PROJECT_DOES_NOT_EXIST;
            problemMessage = IDEWorkbenchMessages.ResourceGroup_noProject;
            return false;
        }
        // path is invalid if any prefix is occupied by a file
        IWorkspaceRoot root = workspace.getRoot();
        while (path.segmentCount() > 1) {
            if (root.getFile(path).exists()) {
                problemType = PROBLEM_PATH_OCCUPIED;
                problemMessage = NLS.bind(
                        IDEWorkbenchMessages.ResourceGroup_pathOccupied, path
                                .makeRelative());
                return false;
            }
            path = path.removeLastSegments(1);
        }
        return true;
    }

    /**
     * Validates the values for each of the group's controls. If an invalid
     * value is found then a descriptive error message is stored for later
     * reference. Returns a boolean indicating the validity of all of the
     * controls in the group.
     */
    protected boolean validateControls() {
        // don't attempt to validate controls until they have been created
        if (containerGroup == null) {
            return false;
        }
        problemType = PROBLEM_NONE;
        problemMessage = "";//$NON-NLS-1$

        if (!validateContainer() || !validateResourceName()) {
            return false;
        }

        IPath path = containerGroup.getContainerFullPath()
                .append(getResource());
        return validateFullResourcePath(path);
    }

    /**
     * Returns a <code>boolean</code> indicating whether the specified
     * resource path represents a valid new resource in the workbench. An error
     * message is stored for future reference if the path does not represent a
     * valid new resource path.
     * 
     * @param resourcePath
     *            the path to validate
     * @return <code>boolean</code> indicating validity of the resource path
     */
    protected boolean validateFullResourcePath(IPath resourcePath) {
        IWorkspace workspace = ResourcesPlugin.getWorkspace();

        IStatus result = workspace.validatePath(resourcePath.toString(),
                IResource.FOLDER);
        if (!result.isOK()) {
            problemType = PROBLEM_PATH_INVALID;
            problemMessage = result.getMessage();
            return false;
        }

        if (!allowExistingResources
                && (workspace.getRoot().getFolder(resourcePath).exists() || workspace
                        .getRoot().getFile(resourcePath).exists())) {
            problemType = PROBLEM_RESOURCE_EXIST;
            problemMessage = NLS.bind(
                    IDEWorkbenchMessages.ResourceGroup_nameExists,
                    getResource());
            return false;
        }
        return true;
    }

    /**
     * Returns a <code>boolean</code> indicating whether the resource name
     * represents a valid resource name in the workbench. An error message is
     * stored for future reference if the name does not represent a valid
     * resource name.
     * 
     * @return <code>boolean</code> indicating validity of the resource name
     */
    protected boolean validateResourceName() {
        String resourceName = getResource();

        if (resourceName.length() == 0) {
            problemType = PROBLEM_RESOURCE_EMPTY;
            problemMessage = NLS.bind(
                    IDEWorkbenchMessages.ResourceGroup_emptyName, "");
            return false;
        }

        if (!hasProperFileExtension(resourceName)) {
            problemType = PROBLEM_NAME_INVALID;
            problemMessage = NLS.bind(
                 IDEWorkbenchMessages.ResourceGroup_invalidFilename,
                 resourceName
            );
            return false;
        }

        if (!Path.ROOT.isValidPath(resourceName)) {
            problemType = PROBLEM_NAME_INVALID;
            problemMessage = NLS.bind(
                    IDEWorkbenchMessages.ResourceGroup_invalidFilename,
                    resourceName);
            return false;
        }
        return true;
    }

    private boolean hasProperFileExtension( String resourceName ) {
        IResourceFormat format = getSelectedFormat();
        for (String extension : format.getNameExtensions()) {
            if (resourceName.endsWith(extension))
                return true;
        }
        return false;
    }

    private boolean hasRecognizedFileExtension( String resourceName ) {
        for (IResourceFormat format : formats) {
            for (String extension : format.getNameExtensions()) {
                if (resourceName.endsWith(extension))
                    return true;
            }
        }
        return false;
    }

    /**
     * Returns the flag indicating whether existing resources are permitted.
     * 
     * @return The allow existing resources flag.
     * @see ResourceAndContainerGroup#setAllowExistingResources(boolean)
     * @since 3.4
     */
    public boolean getAllowExistingResources() {
        return allowExistingResources;
    }

}
