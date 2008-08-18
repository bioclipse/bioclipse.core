/*******************************************************************************
 * Copyright (c) 2007-2008 The Bioclipse Project and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * <http://www.eclipse.org/legal/epl-v10.html>
 * 
 * Contributors:
 *     Jonathan Alvarsson
 *     
 ******************************************************************************/
package net.bioclipse.core;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.ResourcesPlugin;

/**
 * @author jonalv
 */
public class BioclipseStore {

    static BioclipseStore instance = new BioclipseStore();
   
    Map<String, Object> models = new WeakHashMap<String, Object>();
    
    Map<String, Set<String>> modelKeysForLocation 
        = new HashMap<String, Set<String>>();
    
    private BioclipseStore() {
        
    }
    
    public static Object get( IFile file, Class<?> clazz ) {
        return instance.models.get( generateModelsKey( file, clazz ) );
    }

    public static void put( Object model, 
                            IFile file,
                            Class<?> clazz ) {
        
        IResourceChangeListener listener 
            = new IResourceChangeListener() {
            
            public void resourceChanged(IResourceChangeEvent event) {
                if ( event.getResource() instanceof IFile ) {
                    instance.modelKeysForLocation.remove( 
                        generateLocationsKey( 
                            (IFile) event.getResource() ) );
                }
            }
        };
        ResourcesPlugin.getWorkspace()
                       .addResourceChangeListener(listener);

        String modelskey = generateModelsKey( file, clazz );
        instance.models.put( modelskey, model );
        updateModelsKeyHash( file, modelskey );
    }

    private static void updateModelsKeyHash( IFile file, String modelskey ) {
        
        Set<String> s = instance.modelKeysForLocation.get( 
            generateLocationsKey( file ) );
        if (s == null) {
            s = new HashSet<String>();
        }
        s.add( modelskey );
    }

    private static String generateModelsKey( IFile file, 
                                             Class<?> clazz ) {
        return file.getLocationURI() + clazz.getName();
    }
    
    private static String generateLocationsKey( IFile file ) {
        return file.getLocationURI().toString();
    }
}
