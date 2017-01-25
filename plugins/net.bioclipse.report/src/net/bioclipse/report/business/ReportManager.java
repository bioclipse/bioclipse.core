/* Copyright (c) 2015  Egon Willighagen <egon.willighagen@gmail.com>
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contact: http://www.bioclipse.net/
 */
package net.bioclipse.report.business;

import net.bioclipse.managers.business.IBioclipseManager;
import net.bioclipse.report.data.Report;
import net.bioclipse.report.data.IReport;
import net.bioclipse.report.serializer.HTMLSerializer;

public class ReportManager implements IBioclipseManager {

    /**
     * Gives a short one word name of the manager used as variable name when
     * scripting.
     */
    public String getManagerName() {
        return "report";
    }

    public IReport createReport() {
    	return new Report();
    }

    public String asHTML(IReport report) {
    	return new HTMLSerializer().serialize(report);
    }

}
