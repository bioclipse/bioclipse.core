package net.bioclipse.core;

import org.eclipse.core.resources.IFile;

public interface IResourcePathTransformer {

    /**
     * Converts resourceString to an IFile. First check if the path is a
     * workspace relative path, if that fails it tries to lookup the file using
     * an URI. Last it assumes the path is an absolute path to the file system
     * not in the workspace and creates a link in /Virtual.
     *
     * @param resourceString
     * @return IFile
     */
    public IFile transform( String resourceString );

}