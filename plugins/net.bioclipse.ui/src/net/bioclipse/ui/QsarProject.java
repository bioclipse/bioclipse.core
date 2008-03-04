/*******************************************************************************
 * Copyright (c) 2007 The Bioclipse Project and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * www.eclipse.org—epl-v10.html <http://www.eclipse.org/legal/epl-v10.html>
 * 
 * Contributors:
 *     Ola Spjuth - initial API and implementation
 *     
 *******************************************************************************/

package net.bioclipse.ui;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectNature;
import org.eclipse.core.runtime.CoreException;

/**
 * 
 * @author ola
 *
 */
public class QsarProject implements IProjectNature {

	
	public static final String NATURE_ID = "net.bioclipse.ui.qsarNature";

	public void configure() throws CoreException {
		// TODO Auto-generated method stub
		
	}

	public void deconfigure() throws CoreException {
		// TODO Auto-generated method stub
		
	}

	public IProject getProject() {
		// TODO Auto-generated method stub
		return null;
	}

	public void setProject(IProject project) {
		// TODO Auto-generated method stub
		
	}
	
	
	
}
