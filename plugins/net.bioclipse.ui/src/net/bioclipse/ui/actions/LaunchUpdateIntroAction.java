/*******************************************************************************
 * Copyright (c) 2000, 2008 IBM Corporation and others. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: IBM Corporation - initial API and implementation
 ******************************************************************************/
package net.bioclipse.ui.actions;

import java.util.Properties;
import org.eclipse.core.commands.*;
import org.eclipse.core.commands.common.NotDefinedException;
import org.eclipse.core.runtime.*;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.commands.ICommandService;
import org.eclipse.ui.handlers.IHandlerService;
import org.eclipse.ui.intro.IIntroSite;
import org.eclipse.ui.intro.config.IIntroAction;
import org.osgi.framework.Bundle;

public class LaunchUpdateIntroAction implements IIntroAction {


	public LaunchUpdateIntroAction() {
	}

	public void run(IIntroSite site, Properties params) {
		Runnable r = new Runnable() {
			public void run() {
				
				SoftwareUpdatesAction action=new SoftwareUpdatesAction(PlatformUI.getWorkbench().getActiveWorkbenchWindow());
				action.run();
			}
		};
		Shell currentShell = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
		currentShell.getDisplay().asyncExec(r);
	}

}
