/* Copyright (c) 2015  Egon Willighagen <egon. willighagen@gmail.com>
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contact: http://www.bioclipse.net/
 */
package net.bioclipse.report.business;

import net.bioclipse.core.PublishedClass;
import net.bioclipse.core.PublishedMethod;
import net.bioclipse.core.Recorded;
import net.bioclipse.managers.business.IBioclipseManager;
import net.bioclipse.report.data.IReport;

@PublishedClass(
    value="Manager that supports the creation of reports."
)
public interface IReportManager extends IBioclipseManager {

    @Recorded
    @PublishedMethod(
        methodSummary = "Creates an empty report."
    )
	public IReport createReport();

    @Recorded
    @PublishedMethod(
        methodSummary = "Convert the report to HTML."
    )
    public String asHTML(IReport report); // FIXME: in some future, this should use an extension point

}
