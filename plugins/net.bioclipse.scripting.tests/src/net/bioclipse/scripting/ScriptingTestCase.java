package net.bioclipse.scripting;

import static org.junit.Assert.*;
import org.junit.BeforeClass;
import org.junit.Test;

public class ScriptingTestCase {

	private static ScriptingEnvironment env;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		env = new JsEnvironment();
	}

	@Test
	public void scriptingWorks() {
		assertEquals( "the scripting class can be called and returns the"
				      + " right answer",
				      env.eval("2+2"),
				      "4" );
	}
}
