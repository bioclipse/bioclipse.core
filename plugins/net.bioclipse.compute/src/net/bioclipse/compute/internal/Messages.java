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
package net.bioclipse.compute.internal;
import org.eclipse.osgi.util.NLS;
/**
 * @author Rob Schellhorn
 */
public class Messages extends NLS {
        private static final String BUNDLE_NAME = "net.bioclipse.compute.internal.messages"; //$NON-NLS-1$
        public static String ComputationWizardRegistry_0;
        static {
                NLS.initializeMessages(BUNDLE_NAME, Messages.class);
        }
        private Messages() {
                // 
        }
}