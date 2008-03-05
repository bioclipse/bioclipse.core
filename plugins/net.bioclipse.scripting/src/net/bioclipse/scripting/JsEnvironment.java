package net.bioclipse.scripting;

import java.util.HashMap;
import java.util.Map;

import net.bioclipse.core.business.IBioclipseManager;
import net.bioclipse.scripting.INamespaceProvider;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.Platform;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.EcmaError;
import org.mozilla.javascript.EvaluatorException;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;
import org.mozilla.javascript.WrappedException;

/**
 * Javascript environment. Holds variables and evaluates expressions.
 * 
 * @author masak
 *
 */
public class JsEnvironment implements ScriptingEnvironment {

    private Context context;
    private Scriptable scope;
    
    public JsEnvironment() {
    	reset();
    }
    
    /**
     * Initializes the Javascript environment for use.
     */
    public final void reset() {
    	if (context != null)
    		Context.exit();
    	
    	context = Context.enter();
    	scope = context.initStandardObjects();
    	
    	installJsTools();
    }
    
	private void installJsTools() {

		IExtensionRegistry registry = Platform.getExtensionRegistry();

		if ( registry == null )
			return; // it likely means that the Eclipse workbench has not
		            // started, for example when running tests

		/*
		 * service objects 
		 */
		IExtensionPoint serviceObjectExtensionPoint = registry
			.getExtensionPoint("net.bioclipse.scripting.contribution");
				
		IExtension[] serviceObjectExtensions 
			= serviceObjectExtensionPoint.getExtensions();
		for(IExtension extension : serviceObjectExtensions) {
			for( IConfigurationElement element 
				 : extension.getConfigurationElements() ) {
				Object service;
				try {
					service = element.createExecutableExtension("service");
				} 
				catch (CoreException e) {
					throw new RuntimeException("Failed to get service", e);
				}
				if( !(service instanceof IBioclipseManager) ) {
					throw new RuntimeException( "service object: " + service
							                   + "does not implement " 
							                   + "IBioclipseManager" );
				}
				Object jsObject = Context.javaToJS(service, scope);
				ScriptableObject.putProperty( scope, 
						                      ( (IBioclipseManager)service )
						                      	.getNamespace(), 
						                      jsObject );
			}
		}
		
	}

	/**
	 * Evaluates a given Javascript expression.
	 * 
	 * @param expression the expression to be evaluated
	 * @return the result
	 */
	public String eval(String expression) {
		try {
			Object ev = context.evaluateString(scope, expression,
					                           null, 0, null);
			return Context.toString(ev);
		}
		catch (WrappedException e) {
			e.printStackTrace();
			return e.getWrappedException().getMessage();
		}
		catch (EvaluatorException e) {
			e.printStackTrace();
			return e.getMessage();
		}
		catch (EcmaError e) {
			e.printStackTrace();
			return e.getMessage();
		}
		catch (Exception e) {
			e.printStackTrace();
			return e.getMessage();
		}
	}

	/**
	 * Evaluates a given Javascript expression.
	 * 
	 * @param expression the expression to be evaluated
	 * @return the resulting object
	 * 
	 * @throws RuntimeException when the evaluator couldn't parse
	 *                          the expression
	 * @throws EcmaError when the Javascript runtime produced an
	 *                   error evaluating the expression
	 */
	public Object evalToObject(String expression) {
		try{
			return context.evaluateString(scope, expression, null, 0, null);
		}
		catch(EvaluatorException e){
			throw new RuntimeException(e);
		}
		catch (EcmaError e) {
			throw new RuntimeException(e);
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}
