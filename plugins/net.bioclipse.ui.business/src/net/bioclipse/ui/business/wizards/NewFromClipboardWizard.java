/* *****************************************************************************
 * Copyright (c) 2009  Jonathan Alvarsson <jonalv@users.sourceforge.net>
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * www.eclipse.org—epl-v10.html <http://www.eclipse.org/legal/epl-v10.html>
 *
 * Contact: http://www.bioclipse.net/
 ******************************************************************************/
package net.bioclipse.ui.business.wizards;


import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import net.bioclipse.core.util.LogUtils;
import net.bioclipse.ui.business.Activator;
import net.bioclipse.ui.business.IBioObjectFromStringBuilder;

import org.apache.log4j.Logger;
import org.eclipse.core.resources.IStorage;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IPersistableElement;
import org.eclipse.ui.IStorageEditorInput;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;


/**
 * @author jonalv
 *
 */
public class NewFromClipboardWizard extends Wizard implements INewWizard {

    private static final Logger logger 
        = Logger.getLogger(NewFromClipboardWizard.class);
    public static final String WIZARD_ID 
        = "net.bioclipse.ui.business.wizards.NewFromClipboardWizard";
    
    @Override
    public boolean performFinish() {
        final Clipboard clipBoard 
            = new Clipboard( PlatformUI.getWorkbench()
                                       .getActiveWorkbenchWindow()
                                       .getShell()
                                       .getDisplay() );
        TextTransfer transfer = TextTransfer.getInstance();
        final String data = (String)clipBoard.getContents(transfer);
        clipBoard.dispose();
        if (data != null) {
            List<IBioObjectFromStringBuilder> builders = getBuilders();
            
            for (IBioObjectFromStringBuilder builder : builders) {
                if ( builder.recognize( data ) ) {
                    try {
                        Activator.getDefault()
                                 .getUIManager()
                                 .open( builder.fromString( data ) );
                        return true;
                    }
                    catch ( Exception e ) {
                        LogUtils.handleException( 
                            e, logger, "net.bioclipse.ui.business" );
                    }
                }
            }
        }
        openIntextEditor(data);
        return true;
    }

    /**
     * @param data
     */
    private void openIntextEditor( final String data ) {

        IWorkbenchPage page = PlatformUI.getWorkbench()
                                        .getActiveWorkbenchWindow()
                                        .getActivePage();
        final String name 
            = data.length() > 10 ? data.substring( 0, 7 ) + "..."
                                 : data;
        try {
            IEditorInput input = new IStorageEditorInput() {

                public boolean exists() {
                    return true;
                }

                public ImageDescriptor getImageDescriptor() {
                    return ImageDescriptor.getMissingImageDescriptor();
                }

                public String getName() {
                    return name;
                }

                public IPersistableElement getPersistable() {
                    return null;
                }

                public String getToolTipText() {
                    return name;
                }

                @SuppressWarnings("unchecked")
                public Object getAdapter( Class adapter ) {
                    if ( adapter == String.class ) {
                        return data;
                    }
                    return null;
                }

                public IStorage getStorage() throws CoreException {
                    return new IStorage() {

                        public Object getAdapter( Class adapter ) {

                            if ( adapter == String.class ) {
                                return data;
                            }
                            return null;
                        }

                        public boolean isReadOnly() {
                            return false;
                        }

                        public String getName() {
                            return name;
                        }

                        public IPath getFullPath() {
                            return null;
                        }

                        public InputStream getContents() throws CoreException {
                            return new ByteArrayInputStream(data.getBytes());
                        }
                    };
                }
            };
            page.openEditor( input, 
                             org.eclipse.ui.editors.text
                             .EditorsUI.DEFAULT_TEXT_EDITOR_ID );
        } 
        catch (PartInitException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * @return the builders from the EP
     */
    private List<IBioObjectFromStringBuilder> getBuilders() {
        List<IBioObjectFromStringBuilder> builders 
            = new ArrayList<IBioObjectFromStringBuilder>();
        IExtensionRegistry registry = Platform.getExtensionRegistry(); 
        IExtensionPoint serviceObjectExtensionPoint 
            = registry.getExtensionPoint(
                  "net.bioclipse.ui.business.BioObjectFromStringBuilder" );
        IExtension[] serviceObjectExtensions
            = serviceObjectExtensionPoint.getExtensions();
        for ( IExtension extension : serviceObjectExtensions ) {
            for ( IConfigurationElement element
                 : extension.getConfigurationElements() ) {
                Object service = null;
                try {
                    service = element.createExecutableExtension("object");
                }
                catch (CoreException e) {
                    throw new RuntimeException(e);
                }
                if ( service == null || 
                     ( service != null && 
                       !(service instanceof IBioObjectFromStringBuilder) ) )
                    throw new IllegalStateException( 
                                  "Wrong object given by EP" );
                builders.add( (IBioObjectFromStringBuilder) service );
            }
        }
        return builders;
    }

    public void init( IWorkbench workbench, IStructuredSelection selection ) {

        setWindowTitle("New Molecule");
        setNeedsProgressMonitor(true);
    }

}
