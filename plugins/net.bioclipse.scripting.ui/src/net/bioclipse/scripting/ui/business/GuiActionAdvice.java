/*******************************************************************************
 * Copyright (c) 2009 Jonathan Alvarsson <jonathan.alvarsson@farmbio.uu.se>
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * <http://www.eclipse.org/legal/epl-v10.html>
 *
 * Contact: http://www.bioclipse.net/
 ******************************************************************************/
package net.bioclipse.scripting.ui.business;

import org.aopalliance.intercept.MethodInvocation;
import org.eclipse.swt.widgets.Display;

/**
 * Advice that runs the method called in an async block if that method
 * is annotated with the <code>GuiAction</code> annotation
 * 
 * jonalv
 *
 */
public class GuiActionAdvice implements IGuiActionAdvice {

    private IJsConsoleManager jsConsoleManager;
    
    public void setJsConsoleManager(IJsConsoleManager jsConsoleManager) {
        this.jsConsoleManager = jsConsoleManager;
    }
    
    public Object invoke( final MethodInvocation invocation ) 
                  throws Throwable {

        if ( !invocation.getMethod().isAnnotationPresent(GuiAction.class) ) {
            return invocation.proceed();
        }
        
        Display.getDefault().asyncExec( new Runnable() {
            public void run() {
                try {
                    invocation.proceed();
                }
                catch (Throwable t) {
                    jsConsoleManager.printError(t);
                }
            }
        } );
        return null;
    }
}
