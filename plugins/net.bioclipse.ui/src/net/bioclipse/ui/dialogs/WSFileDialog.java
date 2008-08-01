/*******************************************************************************
 *Copyright (c) 2008 The Bioclipse Team and others.
 *All rights reserved. This program and the accompanying materials
 *are made available under the terms of the Eclipse Public License v1.0
 *which accompanies this distribution, and is available at
 *http://www.eclipse.org/legal/epl-v10.html
 *
 *******************************************************************************/
package net.bioclipse.ui.dialogs;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.viewers.DecoratingLabelProvider;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkingSet;
import org.eclipse.ui.ResourceWorkingSetFilter;
import org.eclipse.ui.model.WorkbenchContentProvider;
import org.eclipse.ui.model.WorkbenchLabelProvider;


/**
 * A filedialog for files in the workspace.
 */
public class WSFileDialog extends Dialog {

	private TreeViewer viewer;
	private IResource rootElement;
	private boolean expand;
	private String[] extensions;
	private FilePatternFilter patternFilter = new FilePatternFilter();
	private ResourceWorkingSetFilter workingSetFilter 
	                            = new ResourceWorkingSetFilter();
//	private UndesiredResourcesFilter undesiredResourcesFilter
//	                            = new UndesiredResourcesFilter();
	private IWorkingSet workingSet;
	private int selectionStyle;
	private IResource[] result;
	private String title;

	 private List<IResource> blackList;

	/**
	 * @param parentShell this shell will be blocked by the modal WSFileDialog
	 * @param selectionStyle must be SWT.SINGLE or SWT.MULTI
	 * @param title the dialog's title
	 * @param rootElement resource to be the rootElement for the tree
	 * @param expand if true, the root element will be expanded
	 * @param extensions if specified only files with these extensions are shown
	 * @param workingSet if specified only files in this workingSet are shown
	 */
	public WSFileDialog(Shell parentShell, int selectionStyle, String title
	                    , IResource rootElement, boolean expand
	                    , String[] extensions, IWorkingSet workingSet) {

    super(parentShell);
		setShellStyle(getShellStyle() | SWT.RESIZE);
		this.title = title;
		this.rootElement = rootElement;
		this.expand = expand;
		this.extensions = extensions;
		this.workingSet = workingSet;
		this.selectionStyle = selectionStyle;
	}
	
	/**
	 * Open on the workspace root without filters or workingset
	 * 
	 * @param parentShell this shell will be blocked by the modal WSFileDialog
	 * @param selectionStyle must be SWT.SINGLE or SWT.MULTI
	 * @param title the dialog title
	 */
	public WSFileDialog(Shell parentShell, int selectionStyle, String title) {
		this(
				parentShell,
				selectionStyle,
				title,
				ResourcesPlugin.getWorkspace().getRoot(),
				true,
				null,
				null);
	}

	/**
	 * Only files with the given file extensions will be shown
	 * @param extensions
	 */
	public void setExtensions(String[] extensions) {
		this.extensions = extensions;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.dialogs.Dialog#createDialogArea(org.eclipse.swt.widgets.Composite)
	 */
	protected Control createDialogArea(Composite parent) {
		Composite comp = (Composite) super.createDialogArea(parent);
		getShell().setText(title);
		TreeViewer viewer = createViewer(comp);
		GridData data = new GridData(GridData.FILL_BOTH);
		data.grabExcessHorizontalSpace=true;
		data.grabExcessVerticalSpace=true;
		data.heightHint = 400;
		data.widthHint = 300;
		viewer.getControl().setLayoutData(data);
		this.viewer = viewer;
		return comp;
	}
	
	protected TreeViewer createViewer(Composite parent) {
		TreeViewer viewer =
			new TreeViewer(parent, selectionStyle | SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER);
		viewer.setUseHashlookup(true);
		initContentProvider(viewer);
		initLabelProvider(viewer);
		initFilters(viewer);
		viewer.addDoubleClickListener( new IDoubleClickListener(){

            public void doubleClick( DoubleClickEvent event ) {
                okPressed();
            }
		    
		});
		viewer.setInput(rootElement);
		if (expand) {
			viewer.expandToLevel(2);
		}
		return viewer;
	}
	
	/**
	 * Attach the filters to the tree viewer
	 * @param viewer
	 */
	protected void initFilters(TreeViewer viewer) {
		viewer.addFilter(patternFilter);
		if (workingSet != null) {
			workingSetFilter.setWorkingSet(workingSet);
			viewer.addFilter(workingSetFilter);
		}

		if (blackList!=null)
		    viewer.addFilter( new UndesiredResourcesFilter() );
		
	}

	/**
	 * This is the key, the WorkBenchContentProvider provides us 
	 * with all the resource information
	 * @param viewer
	 */
	protected void initContentProvider(TreeViewer viewer) {
		viewer.setContentProvider(new WorkbenchContentProvider());
	}
	
	protected void initLabelProvider(TreeViewer viewer) {
		viewer.setLabelProvider(
			new DecoratingLabelProvider(
				new WorkbenchLabelProvider(),
				/*IDEWorkbenchPlugin.getDefault().getWorkbench().getDecoratorManager().getLabelDecorator()*/null));
	}

	/**
	 * process the tree selection and keep as the resultIResource[] until needed by our client
	 * 
	 * @see org.eclipse.jface.dialogs.Dialog#okPressed()
	 */
	@SuppressWarnings("unchecked")
	protected void okPressed() {
		ISelection selection = viewer.getSelection();
		List data = new ArrayList();
		if (!selection.isEmpty()) {
			if (selection instanceof IStructuredSelection) {
				IStructuredSelection sel = (IStructuredSelection)selection;
				for (Iterator i = sel.iterator();i.hasNext();) {
					Object next = i.next();
					IResource resource= null;			
					if (next instanceof IResource)
						resource= (IResource)next;
					else if (next instanceof IAdaptable) {
						if (resource == null)
							resource= (IResource)((IAdaptable)next).getAdapter(IResource.class);
					}
					if (resource != null) {
						data.add(resource);
					}
				}
			}
		}
		result = (IResource[])data.toArray(new IResource[]{});
		super.okPressed();
	}
	
	/**
	 * Get the single selection result if any or the first selected 
	 * element if SWT.MULTI was used as the selectionType
	 * @return one selected resource or null if none or canceled
	 */
	public IResource getSingleResult() {
		if (getReturnCode() == OK) {
			return getMultiResult()[0];
		} else return null;
	}
	
	/**
	 * Get an array of selected resources or null if canceled
	 * @return selected resources or null if none or canceled
	 */
	public IResource[] getMultiResult() {
		if (getReturnCode() == OK) {
			return result;
		} else return null;
	}
	
	/**
	 * ViewerFilter to only show non-derived folders and those files matching the file extensions
	 * @author Frank Sauer
	 */
	private class FilePatternFilter extends ViewerFilter {

		/** Select all folders and files matching the desired file extensions
		 * @see org.eclipse.jface.viewers.ViewerFilter#select(org.eclipse.jface.viewers.Viewer, java.lang.Object, java.lang.Object)
		 */
		public boolean select(Viewer viewer, Object parentElement, Object element) {
			if (extensions == null || extensions.length == 0) return true;
	        IResource resource = null;
	        if (element instanceof IResource) {
	            resource = (IResource) element;
	        } else if (element instanceof IAdaptable) {
	            IAdaptable adaptable = (IAdaptable) element;
	            resource = (IResource) adaptable.getAdapter(IResource.class);
	        }
	        if (resource != null && !resource.isDerived()) {
	        	   if (resource.getType() != IResource.FILE) return true;
	            String extension = resource.getFileExtension();
	            if (extension == null) return true;
	            for (int i = 0; i < extensions.length;i++) {
	            		if (extension.equalsIgnoreCase(extensions[i])) return true;
	            }
	        }
	        return false;
		}
	}
	
	
	 /**
   * ViewerFilter to only show non-derived folders and those files matching the file extensions
   * @author Frank Sauer
   */
  private class UndesiredResourcesFilter extends ViewerFilter {

      /** Omit resources in blacklist
       */
      public boolean select(Viewer viewer, Object parentElement, Object element) {

          if (!(element instanceof IResource)) {
              return false;
          }
          IResource resource = (IResource) element;

          if (blackList.contains( resource )) return false;
          else return true;

      }
  }

  public void addBlacklistFilter(List<IResource> list){
      this.blackList=list;
  }
    
    public List<IResource> getUndesiredList() {
    
        return blackList;
    }

    
    public void setUndesiredList( List<IResource> undesiredList ) {
    
        this.blackList = undesiredList;
    }

}
