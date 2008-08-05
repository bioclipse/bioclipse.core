package net.bioclipse.ui.contenttypes;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Hashtable;

import org.eclipse.core.internal.content.ContentMessages;
import org.eclipse.core.internal.content.TextContentDescriber;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExecutableExtension;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.content.IContentDescription;
import org.eclipse.osgi.util.NLS;

@SuppressWarnings("restriction")
public class CmlFileDescriber extends TextContentDescriber 
							implements IExecutableExtension {

	private Hashtable elements = null;

	/**
	 * Store parameters
	 */
	@SuppressWarnings("unchecked")
	public void setInitializationData(final IConfigurationElement config, final String propertyName, final Object data) throws CoreException {
		if (data instanceof String) {
			// why would this happen?
		} else if (data instanceof Hashtable) {
			elements = (Hashtable) data;
		}
		
		if (elements == null) {
			String message = NLS.bind(ContentMessages.content_badInitializationData, CmlFileCoordinatesDescriber.class.getName());
			throw new CoreException(new Status(IStatus.ERROR, ContentMessages.OWNER_NAME, 0, message, null));
		}
	}

	/**
	 * Determine what the CML file contains by quickly scanning the InputStream.
	 */
	public int describe(InputStream contents, IContentDescription description) throws IOException {
		return analyse(new BufferedReader(new InputStreamReader(contents)));
	}
	
	/**
	 * Determine what the CML file contains by quickly scanning the Reader.
	 */
	public int describe(Reader contents, IContentDescription description) throws IOException {
		return analyse(new BufferedReader(contents));
	}

	/**
	 * Read 100 chars, search for x2 or x2
	 */
	private int analyse(BufferedReader input) throws IOException {

		boolean has2D = false;
		boolean has3D = false;
		int moleculeCount = 0;
		
		/*
		 * Sadly, we have to scan the entire file.
		 * This is because we won't know if there are multiple
		 * molecules until all the "/molecule"s are seen.
		 * It might be that a full, SAX style solution would
		 * be better for this.
		 */
		String line;
		while ((line = input.readLine()) != null) {
			if (line.contains("x2")) {
				has2D = true;
			}
			if (line.contains("x3")) {
				has3D = true;
			}
			if (line.contains("/molecule")) {
				moleculeCount++;
			}
		}
		
		// there might be a way to handle mixed 2D/3D at some point
		if (has2D && has3D) {
			return INDETERMINATE;
		}

		String requiredDimension = (String) elements.get("dimension");
		boolean wants2D = requiredDimension.equalsIgnoreCase("2D");
		boolean wants3D = requiredDimension.equalsIgnoreCase("3D");
		
		String requiredCardinality = (String) elements.get("cardinality");
		boolean wantsSingle = requiredCardinality.equalsIgnoreCase("single");
		boolean wantsMultiple = requiredCardinality.equalsIgnoreCase("multiple");
		
		if ((has2D && wants2D) && (moleculeCount == 1 && wantsSingle)) {
			return VALID;
		}
		
		if ((has2D && wants2D) && (moleculeCount > 1 && wantsMultiple)) {
			return VALID;
		}
		
		if ((has3D && wants3D) && (moleculeCount == 1 && wantsSingle)) {
			return VALID;
		}

		if ((has3D && wants3D) && (moleculeCount > 1 && wantsMultiple)) {
			return VALID;
		}

		//Else, invalid (or indeterminate?)
		return INVALID;
	}
}
