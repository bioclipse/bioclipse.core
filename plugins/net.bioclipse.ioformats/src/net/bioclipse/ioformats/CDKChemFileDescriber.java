/*******************************************************************************
 * Copyright (c) 2009  Egon Willighagen <egonw@users.sf.net>
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * www.eclipse.org/epl-v10.html <http://www.eclipse.org/legal/epl-v10.html>
 * 
 * Contact: http://www.bioclipse.net/
 ******************************************************************************/
package net.bioclipse.ioformats;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExecutableExtension;
import org.eclipse.core.runtime.QualifiedName;
import org.eclipse.core.runtime.content.IContentDescription;
import org.eclipse.core.runtime.content.ITextContentDescriber;
import org.openscience.cdk.io.formats.CIFFormat;
import org.openscience.cdk.io.formats.IChemFormatMatcher;

/**
 * Class that uses the CDK IO format matchers to detect the format of a file.
 * 
 * @author egonw
 */
public class CDKChemFileDescriber
  implements ITextContentDescriber, IExecutableExtension {

	private final static List<IChemFormatMatcher> formats;
	
	static {
		formats = new ArrayList<IChemFormatMatcher>();
		formats.add((IChemFormatMatcher)CIFFormat.getInstance());
	}
	
	private String format;
	
	public void setInitializationData(IConfigurationElement config,
			String propertyName, Object data) throws CoreException {
		if ("format".equals(propertyName)) {
			format = data.toString();
		}
	}

	public int describe(Reader contents, IContentDescription description)
			throws IOException {
		BufferedReader buffer =
			contents instanceof BufferedReader ?
				(BufferedReader)contents : new BufferedReader(contents);
		String line = null;
        int lineNumber = 1;
        while ((line = buffer.readLine()) != null && (lineNumber < 8)) {
            for (int i=0; i<formats.size(); i++) {
                IChemFormatMatcher cfMatcher = (IChemFormatMatcher)formats.get(i);
                if (cfMatcher.matches(lineNumber, line)) {
                    return cfMatcher.getClass().getName().equals(format) ? 1 : 0;
                }
            }
            lineNumber++;
        }
		return 0;
	}

	public int describe(InputStream contents, IContentDescription description)
			throws IOException {
		return describe(new BufferedReader(new InputStreamReader(contents)), description);
	}

	public QualifiedName[] getSupportedOptions() {
		return new QualifiedName[]{new QualifiedName(null, "format")};
	}

}
