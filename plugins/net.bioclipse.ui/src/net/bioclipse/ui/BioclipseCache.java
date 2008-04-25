/*******************************************************************************
 * Copyright (c) 2008 The Bioclipse Project and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Ola Spjuth
 *     
 ******************************************************************************/
package net.bioclipse.ui;

import java.io.File;

import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;

public class BioclipseCache {

	public static File getCacheDir() throws CoreException{

		String path=ResourcesPlugin.getWorkspace().getRoot().getLocation().toFile().getAbsolutePath()+File.separator+"tmp";
		File folder=new File(path);

		if (folder.exists()==false){
			if (folder.mkdir()==false) return null;
			//This is how you batch resource updates
//			ResourcesPlugin.getWorkspace().run(new IWorkspaceRunnable() {
//				public void run(IProgressMonitor monitor) throws CoreException {
//					folder.create(IResource.NONE, true, null);
//				}
//			}, null);
		}		

		return folder;
	}

}
