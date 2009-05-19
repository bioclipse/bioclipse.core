/*******************************************************************************
 * Copyright (c) 2007-2008 The Bioclipse Project and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * www.eclipse.org�epl-v10.html <http://www.eclipse.org/legal/epl-v10.html>
 * 
 * Contributors:
 *     Ola Spjuth - initial API and implementation
 *     
 ******************************************************************************/
package net.bioclipse.ui;

import java.net.URL;

import net.bioclipse.ui.dialogs.PickWorkspaceDialog;

import org.eclipse.core.runtime.Platform;
import org.eclipse.equinox.app.IApplication;
import org.eclipse.equinox.app.IApplicationContext;
import org.eclipse.osgi.service.datalocation.Location;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.PlatformUI;

/**
 * This class controls all aspects of the application's execution
 */
public class Application implements IApplication {

    public Object start(IApplicationContext context) {
        Display display = PlatformUI.createDisplay();
        try {
            int returnCode 
                = PlatformUI
                  .createAndRunWorkbench( display, 
                                          new ApplicationWorkbenchAdvisor() );
            if (returnCode == PlatformUI.RETURN_RESTART) {
                return IApplication.EXIT_RESTART;
            }
            return IApplication.EXIT_OK;
        } catch (Throwable exception) {
            System.out.println( "Error while booting Bioclipse: " 
                                + exception.getClass().getSimpleName()  
                                + exception.getMessage() );
            exception.printStackTrace();
        } finally {
            display.dispose();
        }
        return IApplication.EXIT_OK;
    }

    public void stop() {
        final IWorkbench workbench = PlatformUI.getWorkbench();
        if (workbench == null)
            return;
        final Display display = workbench.getDisplay();
        display.syncExec(new Runnable() {
            public void run() {
                if (!display.isDisposed())
                    workbench.close();
            }
        });
    }
}
