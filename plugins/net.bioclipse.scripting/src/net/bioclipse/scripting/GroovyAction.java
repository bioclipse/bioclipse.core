/* *****************************************************************************
 *Copyright (c) 2008-2009 The Bioclipse Team and others.
 *All rights reserved. This program and the accompanying materials
 *are made available under the terms of the Eclipse Public License v1.0
 *which accompanies this distribution, and is available at
 *http://www.eclipse.org/legal/epl-v10.html
 *
 *******************************************************************************/
package net.bioclipse.scripting;

public class GroovyAction {
    private String command;
    private Hook preCommandHook;
    private Hook postCommandHook;
    
    public GroovyAction(String command, Hook postCommandHook) {
        this(command, null, postCommandHook);
    }

    public GroovyAction(String command, Hook preCommandHook, Hook postCommandHook) {

        this.command = command;
        this.preCommandHook  = preCommandHook;
        this.postCommandHook = postCommandHook;
    }
    
    public String getCommand() {
        return command;
    }
    
    public void runPreCommandHook() {
        if (preCommandHook == null)
            return;
        preCommandHook.run(null);
    }

    public void runPostCommandHook(Object result) {
        if (postCommandHook == null)
            return;
        postCommandHook.run(result);
    }
}
