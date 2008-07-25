package net.bioclipse.core;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceVisitor;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.junit.Test;


public class ResourcePathTransformerTest {

    private IFile createVirtualProjectWithFile() throws CoreException {
        IProject project = Activator.getVirtualProject();
        IFile file = project.getFile( "file.txt" );
        
        if ( !file.exists() ) {
            InputStream source = new ByteArrayInputStream(
                                         "File contents".getBytes() );
            file.create(source, IResource.NONE, null);
        }
        return file;
    }
    
    private IFile createProjectWithFile() throws CoreException {
        IWorkspace workspace = ResourcesPlugin.getWorkspace();
        IWorkspaceRoot root = workspace.getRoot();
        IProject project  = root.getProject("myProject");
        IFolder folder = project.getFolder("folder");
        IFile file = folder.getFile("file.txt");
        //at this point, no resources have been created
        if (!project.exists()) project.create(null);
        if (!project.isOpen()) project.open(null);
        if (!folder.exists()) 
           folder.create(IResource.NONE, true, null);
        if (!file.exists()) {
           InputStream source = new ByteArrayInputStream(
                                        "File contents".getBytes() );
           file.create(source, IResource.NONE, null);
        }
        return file;
    }

    private void doWorkSpaceRelativeTesting( IFile file ) {
        ResourcePathTransformer 
            transformer = ResourcePathTransformer.getInstance();
    
        assertEquals( file, transformer.transform( 
                                file.getFullPath().toOSString() ) );
        assertEquals( file, transformer.transform( 
                                file.getFullPath().toPortableString() ) );
        assertEquals( file, transformer.transform( 
                                file.getFullPath().toString() ) );   
    }
    
    private void doURITesting( IFile file ) {
        ResourcePathTransformer 
        transformer = ResourcePathTransformer.getInstance();
    
        assertEquals( file, transformer.transform( 
                                file.getLocationURI().toString() ) );
    }
    
    @Test
    public void testVirtualResourceWorkspaceRelative() throws CoreException {
        IFile file = createVirtualProjectWithFile();
        doWorkSpaceRelativeTesting( file );
    }
    
    @Test
    public void testVirtualResourceURIString() throws CoreException {
        IFile file = createVirtualProjectWithFile();
        doURITesting( file );
    }
    
    @Test
    public void testWorkspaceRelativeResource() throws CoreException {
        IFile file = createProjectWithFile();
        doWorkSpaceRelativeTesting( file );
    }

    @Test
    public void testWorkSpaceURIstring() throws CoreException {
        IFile file = createProjectWithFile();
        doURITesting( file );
    }
    
    @Test
    public void testAbsolutePathToExistingFile() 
                throws IOException, CoreException {
        
        
        File f=new File(".");
        String separator;
        String aPath=f.getAbsolutePath().replaceAll("\\.$", "")+"src"+
        (separator=System.getProperty("file.separator"))+
        this.getClass().getName().replaceAll("\\.",separator)+".java";
        
        IFile file = ResourcePathTransformer.getInstance()
                                            .transform( aPath );
        assertNotNull( file );
        assertTrue( file.exists() );
        InputStream stream1 = new FileInputStream( aPath );
        InputStream stream2 = file.getContents();
        int i = -1;
        int j = -1;
        do {
            i = stream1.read();
            j = stream2.read();
            assertEquals( i, j );
        }
        while (i != -1 && j != -1);
    }
    
    @Test
    public void testAbsolutePathToNonExisitingFile() throws CoreException {
        final IFile file = createVirtualProjectWithFile();
        URL url = this.getClass().getClassLoader().getResource( "." );
        try {
            ResourcePathTransformer.getInstance().transform( 
                url.getPath() + "IDontExist.txt" );
            throw new RuntimeException( "the method should have " +
            		                        "thrown exception" );
        }
        catch (IllegalArgumentException e) {
            //this is what we want
        }
        IProject project = Activator.getVirtualProject();
        IResourceVisitor visitor = new IResourceVisitor() {
            public boolean visit( IResource resource ) throws CoreException {
                if( resource instanceof IFile &&
                   !resource.getName().equals( ".project" ) && !resource.getName().equals("ResourcePathTransformerTest.java")) {
                    assertEquals ( file, resource );
                }
                return true;
            }
        };
        project.accept( visitor );
    }
}
