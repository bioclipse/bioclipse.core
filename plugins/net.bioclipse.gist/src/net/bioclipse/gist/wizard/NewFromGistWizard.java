/*******************************************************************************
 * Copyright (c) 2009  Egon Willighagen <egonw@users.sf.net>
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contact: http://www.bioclipse.net/
 ******************************************************************************/
package net.bioclipse.gist.wizard;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.dialogs.WizardNewFileCreationPage;
import org.eclipse.ui.ide.IDE;
import org.eclipse.ui.wizards.newresource.BasicNewResourceWizard;

/**
 * Creates a resource from a Gist resource.
 * 
 * @author egonw
 */
public class NewFromGistWizard extends BasicNewResourceWizard {

	public static final String WIZARD_ID =
		"net.bioclipse.gist.wizards.NewFromGistWizard"; //$NON-NLS-1$
	
    private GistNumberWizardPage mainPage;
    private WizardNewFileCreationPage selectFilePage;
    
    private int gist = 0;

	/**
     * Creates a wizard for creating a new file resource in the workspace.
     */
    public NewFromGistWizard() {
        super();
    }

    public void addPages() {
        super.addPages();
        mainPage = new GistNumberWizardPage("newFilePage0", this);//$NON-NLS-1$
        mainPage.setTitle("Download Gist");
        mainPage.setDescription("Create a new resource from a downloaded Gist"); 
        addPage(mainPage);
        
        selectFilePage = new WizardNewFileCreationPage("newFilePage1", getSelection());//$NON-NLS-1$
        selectFilePage.setTitle("Select File");
        selectFilePage.setDescription("Select target file"); 
        addPage(selectFilePage);
    }

    public void init(IWorkbench workbench, IStructuredSelection currentSelection) {
        super.init(workbench, currentSelection);
        setWindowTitle("Download Gist");
        setNeedsProgressMonitor(true);
    }

    @Override
    public boolean canFinish() {
    	return (mainPage.canFlipToNextPage()) && (selectFilePage.isPageComplete());
    }
    
	class DownloadGistRunnable implements IRunnableWithProgress {
		
		private IFile file;
		
		public DownloadGistRunnable(IFile file) {
			this.file = file;
		}
		
		public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
			downloadGist(monitor, file);
		}
		
	    private void downloadGist(IProgressMonitor monitor, IFile file) throws InvocationTargetException {
	    	monitor.beginTask("Downloading Gist...", 3);

	    	if (file == null) {
				throw new InvocationTargetException(new NullPointerException(), "File is null");
			}
	        
	        try {
	        	int gist = getGist();
	        		
	        	monitor.subTask("Determining Gist revision");
	        	URL gistURL = new URL("http://gist.github.com/" + gist);
	        	URLConnection gistConn = gistURL.openConnection();
	        	
	        	String rawURLPattern = "\"/raw/" + gist + "/([0-9[a-f]]+)";
	        	Pattern p = Pattern.compile(rawURLPattern);
	        	
	        	BufferedReader reader = new BufferedReader(new InputStreamReader(gistConn.getInputStream()));
	        	String line = reader.readLine();
	        	String rev = null;
	        	while (line != null) {
	        		Matcher m = p.matcher(line);
	            	if (m.find()) {
	            		rev = m.group(1);
	            		reader.close();
	                	break;
	            	}
	            	line = reader.readLine();
	        	}
	        	monitor.worked(1);
	        	
        		if (monitor.isCanceled()) {
        			return;
        		}

        		if (rev != null) {
        		    monitor.subTask("Downloading Gist revision: " + rev);
        		    URL rawURL = new URL("http://gist.github.com/raw/" + gist + "/" + rev);
        		    URLConnection rawConn = rawURL.openConnection();
        		    file.setContents(rawConn.getInputStream(), true, false, null);
        		    monitor.worked(1);
        		} else {
        		    Display.getDefault().syncExec(
        		        new Runnable() {
        		            public void run(){
        		                MessageBox mb = new MessageBox(new Shell(), SWT.OK);
        		                mb.setText("Gist Download Error");
        		                mb.setMessage("Could not find Gist");
        		                mb.open();
        		            }
        		        });
        		    file.delete(true, monitor);
        		    monitor.done();
        		    return;
        		}
	        } catch (PatternSyntaxException e) {
	            System.err.println ("Regex syntax error: " + e.getMessage());
	            System.err.println ("Error description: " + e.getDescription());
	            System.err.println ("Error index: " + e.getIndex());
	            System.err.println ("Erroneous pattern: " + e.getPattern());
	        } catch (MalformedURLException exception) {
	        	exception.printStackTrace();
	        	new InvocationTargetException(new NullPointerException(), "Invalid URL.");
	        } catch (IOException exception) {
	        	exception.printStackTrace();
	        	new InvocationTargetException(new NullPointerException(), "Error while downloading Gist...");
			} catch (CoreException e1) {
				e1.printStackTrace();
				new InvocationTargetException(new NullPointerException(), "Error while downloading Gist...");
			}

			monitor.subTask("Opening the Gist");
	        selectAndReveal(file);

	        IWorkbenchWindow bench = getWorkbench().getActiveWorkbenchWindow();
	        try {
	            if (bench != null) {
	                IWorkbenchPage page = bench.getActivePage();
	                if (page != null) {
	                    IDE.openEditor(page, file, true);
	                }
	            }
	        } catch (PartInitException e) {
	        	new InvocationTargetException(new NullPointerException(), "Error while opening editor...");
	        }
	        monitor.done();
	    }

	}

	public boolean performFinish() {
        IFile file = selectFilePage.createNewFile();
        try {
    		getContainer().run(true, true, new DownloadGistRunnable(file));
		} catch (InvocationTargetException e) {
			e.printStackTrace();
			return false;
    	} catch (InterruptedException exception) {
    		return false; // return, but keep wizard
    	}
    	return true;
    }
    
    public int getGist() {
		return gist;
	}

	public void setGist(int gist) {
		this.gist = gist;
		selectFilePage.setFileName("gist." + gist + ".txt");
	}

}
