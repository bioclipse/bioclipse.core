/* *****************************************************************************
 *Copyright (c) 2012 The Bioclipse Team and others.
 *All rights reserved. This program and the accompanying materials
 *are made available under the terms of the Eclipse Public License v1.0
 *which accompanies this distribution, and is available at
 *http://www.eclipse.org/legal/epl-v10.html
 *
 *******************************************************************************/
package net.bioclipse.ui.actions;

import java.util.Properties;

import net.bioclipse.core.util.LogUtils;

import org.apache.log4j.Logger;
import org.eclipse.core.commands.Command;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IParameter;
import org.eclipse.core.commands.NotEnabledException;
import org.eclipse.core.commands.NotHandledException;
import org.eclipse.core.commands.Parameterization;
import org.eclipse.core.commands.ParameterizedCommand;
import org.eclipse.core.commands.common.NotDefinedException;
import org.eclipse.ui.commands.ICommandService;
import org.eclipse.ui.handlers.IHandlerService;
import org.eclipse.ui.intro.IIntroSite;
import org.eclipse.ui.intro.config.IIntroAction;
import org.eclipse.ui.services.IServiceLocator;

public class LaunchUpdateIntroAction implements IIntroAction {

	private static Logger logger = Logger.getLogger(LaunchUpdateIntroAction.class);
	private static final String pluginId = "net.bioclipse.ui";

	private static final String commandId ="org.eclipse.equinox.p2.ui.discovery.commands.ShowRepositoryCatalog";
	private static final String parmName = "org.eclipse.equinox.p2.ui.discovery.commands.RepositoryParameter";

	public LaunchUpdateIntroAction() {
	}

	public void run(IIntroSite site, Properties params) {
		try {
			ICommandService commandService = getService(site,ICommandService.class);
			Command cmd = commandService.getCommand(commandId);
			IParameter parm = cmd.getParameter(parmName);
			Parameterization[] parameters = new Parameterization[] {
					new Parameterization( parm,
							"http://pele.farmbio.uu.se/bioclipse/releases/2.5")
			};

			ParameterizedCommand parameterizedCommand = new ParameterizedCommand(cmd,parameters);
			IHandlerService handlerService = getService(site,IHandlerService.class);
			handlerService.executeCommand(parameterizedCommand, null);
		} catch (ExecutionException e) {
			LogUtils.handleException(e, logger, pluginId);
		} catch (NotDefinedException e) {
			LogUtils.handleException(e, logger, pluginId);
		} catch (NotEnabledException e) {
			LogUtils.handleException(e, logger, pluginId);
		} catch (NotHandledException e) {
			LogUtils.handleException(e, logger, pluginId);
		}
	}

	private <T> T getService(IServiceLocator loc,Class<T> clazz) {
		return clazz.cast(loc.getService(clazz));
	}
}
