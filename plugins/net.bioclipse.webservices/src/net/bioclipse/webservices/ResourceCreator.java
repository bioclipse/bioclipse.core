package net.bioclipse.webservices;

import java.io.ByteArrayInputStream;

import net.bioclipse.core.business.BioclipseException;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;

/* 
 * This file is part of Bioclipse 2 Web Services Plug-In.
 * 
 * Copyright (C) 2008 Johannes Wagener
 * 
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free Software
 * Foundation; either version 3 of the License, or (at your option) any later
 * version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with
 * this program; if not, see <http://www.gnu.org/licenses>.
 * 
 * @author Johannes Wagener
 */
public class ResourceCreator {
	
	private static IFolder getVirtualFolder()
		throws CoreException {
		IProject root = net.bioclipse.core.Activator.getVirtualProject();
		if(!root.exists(new Path(WebservicesConstants.WEBSERVICES_VIRTUAL_FOLDER))) {
			// If null, create the virtual folder to hold results
			root.getFolder(WebservicesConstants.WEBSERVICES_VIRTUAL_FOLDER).create(true,true, new NullProgressMonitor());
		}
		return root.getFolder(new Path(WebservicesConstants.WEBSERVICES_VIRTUAL_FOLDER));
	}
	
	public static boolean resourceExists(String filename)
	throws CoreException {
		return getVirtualFolder().getFile(filename).exists();
	}
	
	public static IFile createResource(String filename,
			String file_data,
			IProgressMonitor monitor)
		throws CoreException, BioclipseException {
		monitor.worked(1);
		monitor.subTask("new resource");
		
		IFolder wsVirtualFolder = getVirtualFolder();
		monitor.worked(1);
		monitor.subTask("load file data");
		
		if (file_data == null)
			file_data = "* error: no valid data. *";
		
		IFile file = wsVirtualFolder.getFile(filename);
		file.create(new ByteArrayInputStream(file_data.getBytes()),
				false, monitor);
		monitor.worked(1);
		monitor.subTask("parsing resource");
		monitor.worked(1);
		
		return file;
	}
}