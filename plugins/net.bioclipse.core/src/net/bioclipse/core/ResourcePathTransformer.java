/*******************************************************************************
 * Copyright (c) 2007 Bioclipse Project
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors: Jonathan Alvarsson
 *     
 *******************************************************************************/
package net.bioclipse.core;

import java.net.URI;
import java.net.URISyntaxException;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.ResourcesPlugin;

/**
 * @author jonalv
 *
 */
public class ResourcePathTransformer {

    private static ResourcePathTransformer instance = 
        new ResourcePathTransformer();
    
    private ResourcePathTransformer() {
        
    }
    
    public ResourcePathTransformer getInstance() {
        return instance;
    }
    
    public IFile transform(String resourceString) {
  
        IFile result;
        result = parseURI(resourceString);
        if (result == null) result = parseRelative(resourceString);
        if (result == null) result = parsePath(resourceString);
        if (result == null) throw new RuntimeException(
                            "Could not handle " + resourceString );
        return result;
    }

    private IFile parsePath( String resourceString ) {
//        TODO FIXME create virtual linked resource and return the IFile
        return null;
    }

    private IFile parseRelative( String resourceString ) {
//        TODO FIXME create IFile from the wrokspace relative path
        return null;
    }

    private IFile parseURI( String resourceString ) {
        try {
            IFile[] files = ResourcesPlugin.getWorkspace()
                                           .getRoot()
                                           .findFilesForLocationURI(
                                               new URI(resourceString) );
            if ( files.length == 1 )
                return files[0];
            
            throw new IllegalStateException( 
                "Multiple IFiles correspond to the uri:" 
                + resourceString);               
        } 
        catch ( URISyntaxException e ) {
            return null; //It wasn't an uri...
        }
    }
}
