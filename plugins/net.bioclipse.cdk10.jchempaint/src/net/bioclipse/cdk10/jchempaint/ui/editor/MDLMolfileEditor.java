package net.bioclipse.cdk10.jchempaint.ui.editor;

import net.bioclipse.core.util.LogUtils;

import org.apache.log4j.Logger;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.editors.text.TextEditor;
import org.eclipse.ui.part.MultiPageEditorPart;

/**
 * JChemPaint-based editor for MDL molfile V2000 files.
 * 
 * @author egonw
 */
public class MDLMolfileEditor extends MultiPageEditorPart implements IResourceChangeListener{

    private static final Logger logger = Logger.getLogger(MDLMolfileEditor.class);

    JCPPage jcpPage;
    TextEditor textEditor;
    int textEditorIndex;
    
	@Override
	public void init(IEditorSite site, IEditorInput input)
			throws PartInitException {
		super.init(site, input);
		setPartName(input.getName());
	}

	/**
	 * Create JCP on page 1 and texteditor on page2
	 */
	@Override
	protected void createPages() {
		
		jcpPage=new JCPPage();
		textEditor=new TextEditor();
		
		try {
			int ix=addPage(jcpPage, getEditorInput());
			setPageText(ix, "Structure");

			textEditorIndex=addPage(textEditor, getEditorInput());
			setPageText(textEditorIndex, "Source");
		} catch (PartInitException e) {
			LogUtils.debugTrace(logger, e);
		}
		
	}

	@Override
	public void doSave(IProgressMonitor monitor) {
		
		//Synch from JCP to texteditor
		//TODO

		//Use textEditor to save
		textEditor.doSave(monitor);
		
	}

	@Override
	public void doSaveAs() {
		//Synch from JCP to texteditor
		//TODO

		//Use textEditor to save
		textEditor.doSaveAs();
	}

	@Override
	public boolean isSaveAsAllowed() {

		//TODO: not implemented yet
		return false;
	}

	public void resourceChanged(IResourceChangeEvent event) {

		//React if resource is changed on disc.
		
	}
	
	
}
