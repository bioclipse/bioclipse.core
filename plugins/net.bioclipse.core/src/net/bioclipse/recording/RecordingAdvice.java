 /*******************************************************************************
 * Copyright (c) 2007 The Bioclipse Project and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * www.eclipse.org—epl-v10.html <http://www.eclipse.org/legal/epl-v10.html>
 * 
 * Contributors:
 *     Jonathan Alvarsson
 *     
 ******************************************************************************/
package net.bioclipse.recording;

import java.lang.reflect.Method;

import net.bioclipse.core.Recorded;
import net.bioclipse.core.business.IBioclipseManager;
import net.bioclipse.core.domain.BioObject;
import net.bioclipse.core.domain.IBioObject;

/**
 * @author jonalv
 *
 */
public class RecordingAdvice implements IRecordingAdvice {

	private History history;
	
	public RecordingAdvice(History history) {
		this.history = history;
	}
	
	public void afterReturning( Object returnValue, 
			                    Method method,
			                    Object[] args, 
			                    Object target ) throws Throwable {

		boolean methodIsAnnotated = method.isAnnotationPresent(Recorded.class);
		if( !methodIsAnnotated ) {
			return;
		}
		if( !(target instanceof IBioObject) ) {
			if( target instanceof IBioclipseManager) {
				IBioclipseManager manager = (IBioclipseManager)target;
				history.addRecord( 
						new ManagerObjectRecord( method.getName(), 
                                                 manager.getNamespace(),
                                                 args, 
                                                 returnValue ) );
			}
		}
		else {
			history.addRecord( 
					new BioObjectRecord( method.getName(),
					                     ( (BioObject)target ).getId(),
                                         args, 
                                         returnValue ) );
		}
	}
}
