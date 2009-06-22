/*******************************************************************************
 * Copyright (c) 2008 The Bioclipse Project and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * <http://www.eclipse.org/legal/epl-v10.html>
 *
 * Contributors:
 *     Jonathan Alvarsson
 *     Carl Masak
 *
 ******************************************************************************/
package net.bioclipse.ui.business;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import net.bioclipse.core.ResourcePathTransformer;
import net.bioclipse.core.business.BioclipseException;
import net.bioclipse.core.domain.IBioObject;
import net.bioclipse.managers.business.IBioclipseManager;
import net.bioclipse.scripting.ui.Activator;
import net.bioclipse.scripting.ui.business.IJsConsoleManager;
import net.bioclipse.ui.business.describer.ExtensionPointHelper;
import net.bioclipse.ui.business.describer.IBioObjectDescriber;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.content.IContentType;
import org.eclipse.core.runtime.content.IContentTypeManager;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorDescriptor;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.IPersistableElement;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.IDE;
import org.eclipse.ui.navigator.CommonNavigator;
import org.eclipse.ui.part.FileEditorInput;
import org.eclipse.update.configurator.ConfiguratorUtils;
import org.eclipse.update.configurator.IPlatformConfiguration.IFeatureEntry;

/**
 * Contains general methods for interacting with the Bioclipse graphical
 * user interface (GUI).
 *
 * @author masak
 */
public class UIManager implements IBioclipseManager {

    private static final String NAVIGATOR_ID = "net.bioclipse.navigator";

    public String getManagerName() {
        return "ui";
    }

    public void remove( IFile file, IProgressMonitor monitor ) {
        try {
            if (!file.exists())
                throw new IllegalArgumentException(
                    "File not found: " + file.getName()
                );
            file.delete(true, monitor);
        } catch (PartInitException e) {
            throw new RuntimeException(e);
        } catch (CoreException e) {
            throw new RuntimeException(e);
        }
    }

    public void open( final IFile file ) {

        IWorkbenchPage page = PlatformUI.getWorkbench()
                                        .getActiveWorkbenchWindow()
                                        .getActivePage();
        try {
            IDE.openEditor(page, file);
        } 
        catch (PartInitException e) {
            throw new RuntimeException(e);
        }
    }

    public void open( IFile file, String editor ) throws BioclipseException {

        //Determine editorID from putative alias
        final String editorID = getEditorID( editor );
        
        if (editorID==null){
            throw new BioclipseException( "No editor with ID: " 
                                          + editor + " found" );
        }
        
        IWorkbenchPage page = PlatformUI.getWorkbench()
                                        .getActiveWorkbenchWindow()
                                        .getActivePage();
        
        try {
            IDE.openEditor( page, file, editorID);
        }
        catch ( PartInitException e ) {
            throw new RuntimeException(e);
        }
    }
    
    public void open( final IBioObject bioObject, final String editor)
        throws BioclipseException {
        
        //Determine editorID from putative alias
        final String editorID = getEditorID( editor );
        
        if (editorID==null){
            Activator.getDefault().getJsConsoleManager().print(
                    "No editor with ID: " + editor + " found"
            );
            return;
        }
        
        
        IWorkbenchPage page = PlatformUI.getWorkbench()
        .getActiveWorkbenchWindow()
        .getActivePage();
        try {
            IEditorInput input = new IEditorInput() {

                public boolean exists() {
                    return true;
                }

                public ImageDescriptor getImageDescriptor() {
                    return ImageDescriptor.getMissingImageDescriptor();
                }

                public String getName() {
                    return "BioObject";
                }

                public IPersistableElement getPersistable() {
                    return null;
                }

                public String getToolTipText() {
                    return bioObject.getUID().toString();
                }

                @SuppressWarnings("unchecked")
                public Object getAdapter( Class adapter ) {
                    return bioObject.getAdapter( adapter );
                }
            };
            page.openEditor( input, editorID );
        } catch (PartInitException e) {
            throw new RuntimeException(e);
        }
    }
    
    public void save( IFile target, 
                      InputStream toWrite, 
                      IProgressMonitor monitor ) {
       save ( target, toWrite, null, monitor);
    }

    public void save( final IFile target, 
                      InputStream toWrite,
                      Runnable callbackFunction,
                      IProgressMonitor monitor ) {
        if (monitor == null) monitor = new NullProgressMonitor();
        try {
            int ticks = 10000;
            monitor.beginTask("Writing file", ticks);
            if (target.exists()) {
                target.setContents(toWrite, false, true, monitor);
            } else {
                target.create(toWrite, false, monitor);
            }
            monitor.worked(ticks);
        } catch (Exception exception) {
            throw new RuntimeException(
                                       "Error while saving to IFile", exception
            );
        } finally {
            monitor.done();
        }
        if (callbackFunction != null) {
            Display.getDefault().asyncExec(callbackFunction);
        }
    }

    public boolean fileExists(IFile file) {
        return file.exists();
    }

    public void open(IBioObject bioObject) throws BioclipseException, 
    CoreException, 
    IOException {

        //Strategy: Determine editor ID from IBioObject and open in this

        IJsConsoleManager js = net.bioclipse.scripting.ui.Activator
        .getDefault().getJsConsoleManager();

        //If bioObject has a resource, 
        //use Content Type on this to determine editor
        if (bioObject.getResource()!=null) {
            if ( bioObject.getResource() instanceof IFile ) {
                IFile file = (IFile) bioObject.getResource();

                IContentTypeManager contentTypeManager 
                    = Platform.getContentTypeManager();
                InputStream stream = file.getContents();
                IContentType contentType 
                    = contentTypeManager.findContentTypeFor( stream, 
                                                             file.getName() );

                IEditorDescriptor editor 
                    = PlatformUI.getWorkbench().getEditorRegistry()
                                .getDefaultEditor(file.getName(), contentType);
                if (editor != null) {
                    open( bioObject, editor.getId() );
                    return;
                }
            }
        }

        //Ok, either we had a file but could not get an editor for it, 
        //or we don't have a resource for the IBioObject

        //Get all describers that are valid for this bioObject
        List<IBioObjectDescriber> describers 
        = ExtensionPointHelper.getAvailableDescribersFromEP();

        for (IBioObjectDescriber describer : describers) {
            String editorId=describer.getPreferredEditorID( bioObject );
            //For now, just grab the first that comes.
            //TODO: implement some sort of hierarchy here if multiple matches
            if (editorId != null) {
                IEditorDescriptor editor 
                = PlatformUI.getWorkbench().getEditorRegistry()
                .findEditor( editorId );
                if (editor != null) {
                    open( bioObject, editor.getId() );
                    return;
                }
            }
        }
        throw new IllegalArgumentException(
            "No editor found for object: " + bioObject
        );
    }


    public void getEditors() throws BioclipseException{

        String retstr="Alias\t\tEditorID\n========================\n";
        Map<String, String> aliasmap
            = ExtensionPointHelper.getAvailableAliasesFromEP();

        for (Iterator<String> it = aliasmap.keySet().iterator();it.hasNext();) {
            String alias=it.next();
            retstr=retstr+alias + "\t\t" + aliasmap.get( alias ) +"\n";
        }

        Activator.getDefault().getJsConsoleManager().print( retstr );
    }

    /**
     * Returns a valid editorID or null if no editor could be found
     * @param request an alias or editorID
     * @return
     * @throws BioclipseException
     */
    private String getEditorID(String request) throws BioclipseException{

        //Start by looking if the submitted itself is a valid editorID
        IEditorDescriptor editor
            = PlatformUI.getWorkbench().getEditorRegistry().findEditor(request);
        if (editor!=null)
            return request;

        //Next, look up in aliasmap
        Map<String, String> aliasmap
            = ExtensionPointHelper.getAvailableAliasesFromEP();
        if (aliasmap.keySet().contains( request ))
            return aliasmap.get( request );

        //Nothing found
        return null;
    }

    
    public void newFile( String path) throws CoreException, BioclipseException {
        IWorkspace workspace = ResourcesPlugin.getWorkspace();
        IWorkspaceRoot root = workspace.getRoot();
        IPath ipath=new Path(path);
        IFile file=root.getFile( ipath );

        if (!file.exists()){
            byte[] bytes = "".getBytes();
            InputStream source = new ByteArrayInputStream(bytes);
            file.create(source, IResource.NONE, new NullProgressMonitor());
        }
    }

    public void closeActiveEditor() {
        Display.getDefault().asyncExec( new Runnable() {
            public void run() {

                IWorkbench workB = PlatformUI.getWorkbench();
                IWorkbenchPage page
                    = workB.getActiveWorkbenchWindow().getActivePage();
                page.closeEditor( page.getActiveEditor(), false );
            }
        });
    }

    public void closeEditor( final IFile file) {
        Display.getDefault().asyncExec( new Runnable() {

            public void run() {
                IWorkbench workB = PlatformUI.getWorkbench();
                IWorkbenchPage page
                    = workB.getActiveWorkbenchWindow().getActivePage();
                List<IEditorReference> toClose
                    = new ArrayList<IEditorReference>();
                IFileEditorInput ei= new FileEditorInput(file);
                IEditorReference[] editorRefs = page.getEditorReferences();

                for(IEditorReference ref:editorRefs) {
                    try {
                        if(ei.equals( ref.getEditorInput()))
                            toClose.add(ref);
                    } catch ( PartInitException e ) {
                        // Failed to close one edtior
                    }
                }
                editorRefs = toClose.toArray(
                    new IEditorReference[toClose.size()]
                );
                page.closeEditors( editorRefs, false );

            }
        });

    }
    
    public void revealAndSelect(final IFile file) throws BioclipseException {
        
        //Get navigator view and reveal in UI thread
        if (!file.exists())
            throw new RuntimeException("The file: " + file.getName() + 
                                         " does not exist.");
        IViewPart view
            = PlatformUI.getWorkbench()
                        .getActiveWorkbenchWindow()
                        .getActivePage()
                        .findView( NAVIGATOR_ID );
        CommonNavigator nav=(CommonNavigator)view;
        nav.getCommonViewer().reveal( file );
        nav.getCommonViewer().setSelection(
            new StructuredSelection(file)
        );
    }

    public void refresh(String path,IProgressMonitor monitor)
        throws BioclipseException{

        IWorkspaceRoot root=ResourcesPlugin.getWorkspace().getRoot();
        IResource resource = root.findMember( new Path(path) );
        try {
            resource.refreshLocal( IResource.DEPTH_ONE, monitor );
        } catch ( CoreException e ) {
            throw new BioclipseException( "Failed to refresh "
                                   + resource.getLocation().toPortableString());
        }
    }
    
    public void assertInstalled(String feature) throws BioclipseException{

        if (getInstalledFeatures().contains( feature ))
            return;
        else
            throw new BioclipseException("The feature: " + feature + 
                                         " is not installed.");
    }
    
    public List<String> getInstalledFeatures() {
        List<String> installedFeatures=new ArrayList<String>();
        for (IFeatureEntry en
                : ConfiguratorUtils.getCurrentPlatformConfiguration()
                                   .getConfiguredFeatureEntries()) {
            //Omit Eclipse features
            if (!en.getFeatureIdentifier().startsWith( "org.eclipse" )){
                System.out.println(en.getFeatureIdentifier());
                installedFeatures.add( en.getFeatureIdentifier());
            }
        }
        
        return installedFeatures;
    }
}
