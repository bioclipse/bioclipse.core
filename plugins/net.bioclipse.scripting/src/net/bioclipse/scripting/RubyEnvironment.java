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
