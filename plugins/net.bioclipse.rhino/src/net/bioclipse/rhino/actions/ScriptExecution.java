package net.bioclipse.rhino.actions;

import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.List;

import net.bioclipse.rhino.Activator;
import net.bioclipse.rhino.ScriptingTools;
import net.bioclipse.rhino.exceptions.ScriptException;

import org.eclipse.core.runtime.IProgressMonitor;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.ContextFactory;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;

/**
 * 
 * This file is part of the Bioclipse Rhino Plug-in.
 * 
 * Copyright (C) 2008 Johannes Wagener
 * 
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation; either version 3 of the License, or (at your option) any later
 * version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program; if not, see <http://www.gnu.org/licenses>.
 * 
 * @author Johannes Wagener
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