/*******************************************************************************
 * Copyright (c) 2006 Bioclipse Project
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Rob Schellhorn
 ******************************************************************************/
package net.bioclipse.compute.wizards;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.ui.IWorkbenchWizard;
/**
 * @author Rob Schellhorn
 */
public interface IComputationWizard extends IWorkbenchWizard {
        /**
         * Returns the configured computation job encapsulated in a Job. If this
         * wizard finishes ok, this method <em>must</em> return a ready-to-use
         * instance.
         * 
         * @return The computation job.
         */
        public Job getComputationJob();
}