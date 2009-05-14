package net.bioclipse.managers.business;

import java.io.FileNotFoundException;

import org.eclipse.core.resources.IFile;

import net.bioclipse.core.IResourcePathTransformer;
import net.bioclipse.core.MockIFile;


public abstract class UglyHacker {

    public static void switchTransformer(JavaScriptManagerMethodDispatcher d) {
        d.transformer = new IResourcePathTransformer() {

            public IFile transform( String resourceString ) {

                try {
                    return new MockIFile();
                } catch ( FileNotFoundException e ) {
                    throw new RuntimeException(e);
                }
            }
            
        };
    }
}
