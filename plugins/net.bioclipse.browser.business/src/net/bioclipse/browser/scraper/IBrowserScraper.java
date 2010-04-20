/* *****************************************************************************
 * Copyright (c) 2010  Ola Spjuth <ospjuth@users.sf.net>
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * www.eclipse.org/epl-v10.html <http://www.eclipse.org/legal/epl-v10.html>
 * 
 * Contact: http://www.bioclipse.net/
 ******************************************************************************/
package net.bioclipse.browser.scraper;

import java.util.List;

import net.bioclipse.core.domain.IBioObject;

/**
 * 
 * @author ola
 *
 */
public interface IBrowserScraper {

    public boolean matchesURL(String URL);
    
    public boolean matchesContent(String content);

    public List<? extends IBioObject> 
        extractObjects(String url, String content);
    
}
