 /*******************************************************************************
 * Copyright (c) 2007-2008 The Bioclipse Project and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * www.eclipse.org—epl-v10.html <http://www.eclipse.org/legal/epl-v10.html>
 * 
 * Contributors:
 *     Jonathan Alvarsson
 *     Ola Spjuth
 *     
 ******************************************************************************/
package net.bioclipse.recording;

import java.lang.reflect.Method;

import net.bioclipse.core.Recorded;
import net.bioclipse.core.domain.BioObject;
import net.bioclipse.core.domain.IBioObject;
import net.bioclipse.managers.business.IBioclipseManager;

import org.apache.log4j.Logger;


/**
 * @author jonalv, olas
 *
 */
public class RecordingAdvice implements IRecordingAdvice {

    private History history;
    
    private static final Logger logger = Logger.getLogger(RecordingAdvice.class);
    
    
    public RecordingAdvice(History history) {
        this.history = history;
    }
    
    
    public void afterReturning( Object returnValue, 
                                Method method,
                                Object[] args, 
                                Object target ) throws Throwable {

        if ( !method.isAnnotationPresent(Recorded.class) ) {
            return;
        }
        
        if (target instanceof IBioObject) {
            history.addRecord( 
                    new BioObjectRecord( method.getName(),
                                         ((BioObject) target).getUID(),
                                         args,
                                         returnValue ) );            
        }
        else if (target instanceof IBioclipseManager) {
            IBioclipseManager manager = (IBioclipseManager) target;
            history.addRecord( 
                    new ManagerObjectRecord( method.getName(), 
                                             manager.getNamespace(),
                                             args, 
                                             returnValue ) );            
        }
        else {
            String message = "@Recorded method is on object of unexpected "
                + "type: " + target.getClass().getName() + "." 
                + method.getName();
            
            assert false: message;        // for development time
            logger.warn(message);         // for logged end-user distrib
        }
    }
}
