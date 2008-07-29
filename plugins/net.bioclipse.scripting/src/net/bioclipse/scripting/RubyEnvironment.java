/*******************************************************************************
 *Copyright (c) 2008 The Bioclipse Team and others.
 *All rights reserved. This program and the accompanying materials
 *are made available under the terms of the Eclipse Public License v1.0
 *which accompanies this distribution, and is available at
 *http://www.eclipse.org/legal/epl-v10.html
 *
 *******************************************************************************/
package net.bioclipse.scripting;

import org.jruby.Ruby;
import org.jruby.runtime.builtin.IRubyObject;

/**
 * Ruby environment. Holds variables and evaluates expressions.
 * 
 * @author masak
 *
 */
public class RubyEnvironment implements ScriptingEnvironment {

    Ruby runtime = Ruby.getDefaultInstance();
    
    public String eval(String expression) {
        try {
            IRubyObject result = runtime.evalScript(expression);
            return (String)
                org.jruby.javasupport.JavaEmbedUtils.rubyToJava(runtime, 
                (org.jruby.runtime.builtin.IRubyObject) result.asString(),
                String.class);
        }
        catch (Exception e) {
            return e.getMessage();
        }
    }

    public void reset() {
        // not sure this is possible with JRuby
    }

}
