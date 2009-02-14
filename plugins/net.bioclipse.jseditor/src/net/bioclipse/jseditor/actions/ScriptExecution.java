package net.bioclipse.jseditor.actions;

import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.List;

import net.bioclipse.jseditor.Activator;
import net.bioclipse.jseditor.ScriptingTools;
import net.bioclipse.jseditor.exceptions.ScriptException;

import org.eclipse.core.runtime.IProgressMonitor;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.ContextFactory;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;

/*
 * This file is part of the Bioclipse Javascript Editor Plug-in.
 * 
 * Copyright (c) 2008 Johannes Wagener.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Johannes Wagener - initial API and implementation
 */
public class ScriptExecution {
	
	public static String runRhinoScript(String scriptString)
	throws ScriptException {
		return runRhinoScript(scriptString, null);
	}
	
	@SuppressWarnings("unchecked")
	public static String runRhinoScript(String scriptString,
			IProgressMonitor monitor) throws ScriptException {
		String scriptResult = "Invalid result.";
		// DO THE ACTUAL EXECUTION OF THE SCRIPT

		if (!ContextFactory.hasExplicitGlobal())
			ContextFactory.initGlobal(new ContextFactory());
		Context cx = ContextFactory.getGlobal().enterContext();
		
		if (cx == null) {
			return "Could not create context.";
		}

		try {
			// Initialize the standard objects (Object, Function, etc.)
			// This must be done before scripts can be executed. Returns
			// a scope object that we use in later calls.
			Scriptable scope = cx.initStandardObjects();

			ScriptingTools tools;
			if (monitor == null)
				tools = new ScriptingTools();
			else
				tools = new ScriptingTools(monitor);
			
			Object wrappedOut = Context.javaToJS(tools, scope);
			ScriptableObject.putProperty(scope, "rhino", wrappedOut);

			// also add all managers
			List<Object> managers = Activator.getManagers();
			if (managers != null && managers.size() > 0) {
				Iterator<Object> it = managers.iterator();
				while (it.hasNext() == true) {
					Object object = it.next();
					
					Class managerclass = object.getClass();
					// access the method in this ugly way however it is not protected...
					Method method = managerclass.getDeclaredMethod("getNamespace", new Class[0]);
					//method.setAccessible(true);
					Object managerName = (String)method.invoke(object);
					if (managerName instanceof String) {
						wrappedOut = Context.javaToJS(object, scope);
						ScriptableObject.putProperty(scope, (String)managerName, wrappedOut);
					}
				}
			}
			
			// Now evaluate the string we've colected.
			Object ev = cx.evaluateString(scope, scriptString, "line: ", 1, null);

			// Convert the result to a string and print it.
			scriptResult = Context.toString(ev);
		} catch (Exception e){
			throw new ScriptException(e);
		} finally {
			// Exit from the context.
			Context.exit();
		}

		return scriptResult;
	}
}