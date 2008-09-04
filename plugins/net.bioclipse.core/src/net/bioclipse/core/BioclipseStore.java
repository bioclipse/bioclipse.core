/*******************************************************************************
 * Copyright (c) 2007-2008 The Bioclipse Project and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * <http://www.eclipse.org/legal/epl-v10.html>
 * 
 * Contributors:
 *     Jonathan Alvarsson
 *     Arvid Berg - redesign
 *     
 ******************************************************************************/
package net.bioclipse.core;

import java.lang.ref.SoftReference;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.ResourcesPlugin;

/**
 * @author jonalv
 */
public class BioclipseStore {

    static BioclipseStore instance = new BioclipseStore();
    
    Logger logger = Logger.getLogger(BioclipseStore.class);
   
    Map<IResource,SoftReference<Map<Object,Object>>> resourceMap;
    
    IResourceChangeListener listener;
    
    private BioclipseStore() {
       resourceMap = new HashMap<IResource,SoftReference<Map<Object,Object>>>();
    }
    
    public static Object get( IResource resource, Object key) {
       return instance.getModel(resource,key);
    }
    
    public static void put( IResource resource,Object key, Object model){
        instance.putModel(resource,key,model);
    }
    

    private void putModel(IResource resource, Object key, Object model){
        
        SoftReference<Map<Object,Object>> ref=resourceMap.get(resource);
        Map<Object,Object> values = null;
                
        values = (ref == null?null: ref.get() );
        if(values== null) {
            values = new HashMap<Object,Object>();
        }
        values.put(key, model );
        if(ref==null){           
            ref = new SoftReference<Map<Object,Object>>(values);            
            resourceMap.put(resource,ref);
            addResourceListener(resource );
        }

    }
    private Object getModel(IResource resource, Object key){
        SoftReference<Map<Object,Object>> ref=resourceMap.get(resource);
        if(ref == null) return null;
        Map<Object,Object> values=ref.get();
        if(values!=null){
            //logger.debug("Retriving Object from Cache" );
            return values.get(key );
        }else
            resourceMap.remove(resource );
        return null;
    }
    
    private void addResourceListener(IResource resource){
        if(listener == null){
            listener = new IResourceChangeListener() {
                
                public void resourceChanged(IResourceChangeEvent event) {
                    if ( event.getResource() instanceof IResource ) {
                        resourceMap.remove(event.getResource());
                    }
                }
            };
        }
        ResourcesPlugin.getWorkspace()
                       .addResourceChangeListener(listener);

    }

}
