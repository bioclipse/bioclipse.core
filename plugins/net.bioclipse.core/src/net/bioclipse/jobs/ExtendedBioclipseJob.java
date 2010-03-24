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
package net.bioclipse.jobs;


/**
 * Extended version of the BioclipseJob class which is not automagicly scheduled
 * by the manager method dispatcher when created.
 * 
 * @author jonalv
 *
 */
public class ExtendedBioclipseJob<T> extends BioclipseJob<T> {

    /**
     * @param name
     */
    public ExtendedBioclipseJob(String name) {
        super( name );
    }

}
