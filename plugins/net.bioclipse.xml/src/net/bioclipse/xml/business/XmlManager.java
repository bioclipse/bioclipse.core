/*******************************************************************************
 * Copyright (c) 2009  Egon Willighagen <egonw@users.sf.net>
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contact: http://www.bioclipse.net/
 ******************************************************************************/
package net.bioclipse.xml.business;

import java.io.IOException;

import net.bioclipse.core.business.BioclipseException;
import net.bioclipse.managers.business.IBioclipseManager;
import nu.xom.Builder;
import nu.xom.ParsingException;
import nu.xom.ValidityException;

import org.apache.log4j.Logger;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;

public class XmlManager implements IBioclipseManager {

    private static final Logger logger = Logger.getLogger(XmlManager.class);

    /**
     * Gives a short one word name of the manager used as variable name when
     * scripting.
     */
    public String getManagerName() {
        return "xml";
    }

    public boolean isWellFormed(IFile file, IProgressMonitor monitor)
        throws BioclipseException, CoreException {
        logger.debug("Checking for well-formedness");
        try {
            Builder parser = new Builder(true);
            parser.build(file.getContents());
        } catch (ValidityException exception) {
            return false;
        } catch (ParsingException exception) {
            return false;
        } catch (IOException exception) {
            throw new BioclipseException(
                "Error while opening file",
                exception
            );
        } catch (CoreException exception) {
            throw new BioclipseException(
                "Error while opening file",
                exception
            );
        }
        return true;
    }

}
