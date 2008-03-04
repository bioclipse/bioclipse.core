package net.bioclipse.ui;

import java.io.IOException;
import java.util.ArrayList;

import net.bioclipse.scripting.OutputProvider;

public interface JsPluginable {

	void eval(ArrayList<Object> al) throws IOException;

	void setOutputProvider(OutputProvider outputProvider);
}