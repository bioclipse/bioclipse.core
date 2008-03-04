package net.bioclipse.ui.views;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.bioclipse.scripting.JsEnvironment;
import net.bioclipse.scripting.OutputProvider;
import net.bioclipse.ui.Activator;
import net.bioclipse.ui.EchoEvent;
import net.bioclipse.ui.EchoListener;
import net.bioclipse.ui.JsPluginable;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.Platform;


/**
 * A console for a Javascript session. For more on Javascript, see
 * <a href="http://www.ecmascript.org/">the ECMAScript home page</a>.
 * 
 * @author masak
 *
 */
public class JsConsoleView extends ScriptingConsoleView
                           implements EchoListener {
	
	private static Matcher jsBacktickMatcher;
	
	static {
		String noBackTick
          = "("                                   // capture into group:
			  + "(?:"                               // either...
			    + "[^`\'\"]+"                          // no backticks or quotes
			    + "|"                                  // ...or...
			    + "(?:\"(?:[^\"\\\\]|(?:\\\\.))*\")"   // a double quoted string
			    + "|"                                  // ...or...
		        + "(?:\'(?:[^\'\\\\]|(?:\\\\.))*\')"   // a single quoted string
		      + ")*+"                               // zero or more of the above
		    + ")";

		jsBacktickMatcher
		  = Pattern.compile( noBackTick + "`" + noBackTick + "`" ).matcher("");
	}
	
	private static JsEnvironment js
		= net.bioclipse.scripting.Activator.getDefault().JS_SESSION;
    
    private JsPluginable rConnection = null;
    
    private static enum Mode {
    	JS,
    	R;
    }
    
    private Mode mode = Mode.JS;
    
	/**
     * The constructor. Called by Eclipse reflection when a new console
     * is created.
     * 
     * @see ScriptingConsoleView.<init>
     */
    public JsConsoleView() {
    	super();
       	Activator.getDefault().CONSOLE.addListener(this);
    }
    
    void setMode(Mode newMode) {
    	if (newMode == Mode.R && rConnection == null)
    		installR();
    	
    	if (newMode == Mode.R && rConnection == null) {
    		printMessage("R is not installed -- get net.bioclipse.r");
    		return;
    	}
    	
    	mode = newMode;
    }
    
    /* (non-Javadoc)
     * @see net.bioclipse.core.views.ScriptingConsoleView#commandLinePrefix()
     */
    protected String commandLinePrompt() {
    	return mode == Mode.JS ? "js> " : "R> ";
    }

	private void installR() {

		IExtensionRegistry registry = Platform.getExtensionRegistry();
		
		if (registry == null) { // for example, when we are running the tests
			// TODO: Change to logging
			System.out.println("Registry does not exist. If tests are running, "
					+ "this is in order.");
			return;             // nothing we can do anyway
		}
		
		IExtensionPoint extensionPoint
			= registry.getExtensionPoint("net.bioclipse.ui.r_provider");

		IExtension[] extensions = extensionPoint.getExtensions();

		if (extensions.length == 0)
			return;
		
		IExtension e = extensions[0];
		IConfigurationElement[] cfgElems = e.getConfigurationElements();
		
		for (IConfigurationElement cfgElem : cfgElems) {
			
			try {
				this.rConnection = (JsPluginable)cfgElem
					               .createExecutableExtension("service");
				this.rConnection.setOutputProvider( new OutputProvider(){
						public void output(final String r) {
							Activator.getDefault().CONSOLE.echo(r);
						}
					}
				);
			}
			catch (Exception ex) {
				throw new RuntimeException(
						"Failed to instantiate factory: "
						+ cfgElem.getAttribute("class")
						+ "\n in type: "
						+ extensionPoint.getUniqueIdentifier()
						+ "\n in plugin: "
						+ e.getExtensionPointUniqueIdentifier()
						+ "\n"
						+ ex.getMessage());
			}
		}
	}

	private String executeJsCommand(String command) {
		String result = js.eval(command);
    	
		return result == "undefined" ? "" : result;
	}

	private String executeRCommand(String command) {

		ArrayList<Object> al;
		try {
			al = interpolateJsVariables(command);
		}
		catch (RuntimeException rte) {
			return rte.toString();
		}
			
		try {
			rConnection.eval(al);
		} catch (IOException e) {
			e.printStackTrace();
		}

		return "";
	}

	private ArrayList<Object> interpolateJsVariables(String command) {
		
		ArrayList<Object> al = new ArrayList<Object>();
		jsBacktickMatcher.reset(command);

		int e = 0;
		while(jsBacktickMatcher.find()){
			
			al.add(jsBacktickMatcher.group(1));
			al.add(js.evalToObject(jsBacktickMatcher.group(2)));
			e = jsBacktickMatcher.end();
		}
		al.add(command.substring(e));

		return al;
	}

    /* (non-Javadoc)
     * @see net.bioclipse.core.views.ScriptingConsoleView#executeCommand(java.lang.String)
     */
    protected String executeCommand(String command) {
    	if (mode == Mode.JS) {
    		if ("R".equals(command.trim())) {
    			setMode(Mode.R);
    			return "";
    		}
    		return executeJsCommand(command);
    	}
    	else {
    		if ("q()".equals(command.trim())) {
    			setMode(Mode.JS);
    			return "";
    		}
    		return executeRCommand(command);
    	}
    }

	public void receiveLogEvent(EchoEvent e) {
		printMessage(e.getMessage());		
	}
	
	protected List<String> getAllVariablesIn(String object) {
		
		if (object == null || "".equals(object))
			object = "this";
		
		List<String> variables
		  = new ArrayList<String>( Arrays.asList( executeJsCommand(
				"zzz1 = new Array(); zzz2 = 0;"
				+ "for (zzz3 in " + object + ") { zzz1[zzz2++] = zzz3 }"
				+ "zzz1"
			).split(",") ) );
		
		// The following happens sometimes when we tab complete on something
		// unexpected. We choose to beep instead of outputting "syntax error".
		if (variables.size() == 1 &&
				("syntax error".equals(variables.get(0)) ||
	             variables.get(0).startsWith("ReferenceError"))) {
			beep();
			return new ArrayList<String>();
		}
		
		variables.remove("zzz1");
		variables.remove("zzz2");
		variables.remove("zzz3");
		
		return variables;
	}
}