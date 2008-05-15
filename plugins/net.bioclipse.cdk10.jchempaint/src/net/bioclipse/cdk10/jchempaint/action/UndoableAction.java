package net.bioclipse.cdk10.jchempaint.action;

import java.util.Vector;

import javax.swing.undo.UndoableEdit;

import org.eclipse.core.commands.operations.IOperationHistory;
import org.eclipse.core.commands.operations.IUndoContext;
import org.eclipse.core.commands.operations.IUndoableOperation;
import org.eclipse.core.commands.operations.OperationHistoryFactory;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.openscience.cdk.applications.jchempaint.DrawingPanel;
import org.openscience.cdk.applications.jchempaint.JChemPaintModel;


/**
 * A class for wrapping UndoableEdits in an IUndoableOperation as needed for eclipse.
 * 
 * @since 0.9
 *
 */
public class UndoableAction implements IUndoableOperation{
	private Vector contexts=new Vector();
	UndoableEdit undoableEdit=null;
	JChemPaintModel model=null;
	DrawingPanel drawingPanel=null;
	
	/**
	 * @param edit The UndoableEdit this class should wrap
	 * @param model The current jcpmodel
	 */
	private UndoableAction(UndoableEdit edit, JChemPaintModel model, DrawingPanel drawingPanel){
		undoableEdit=edit;
		this.model=model;
		this.drawingPanel=drawingPanel;
	}
	/* (non-Javadoc)
	 * @see org.eclipse.core.commands.operations.IUndoableOperation#canUndo()
	 */
	public boolean canUndo(){
		return undoableEdit.canUndo();
	}
	
	 /* (non-Javadoc)
	 * @see org.eclipse.core.commands.operations.IUndoableOperation#getContexts()
	 */
	public IUndoContext[] getContexts(){
		 IUndoContext[] c=new IUndoContext[contexts.size()];
		 for(int i=0;i<contexts.size();i++){
			 c[i]=(IUndoContext)contexts.get(i);
		 }
		 return c;
	 }
	 
	 /* (non-Javadoc)
	 * @see org.eclipse.core.commands.operations.IUndoableOperation#removeContext(org.eclipse.core.commands.operations.IUndoContext)
	 */
	public void removeContext(IUndoContext con){
		 contexts.remove(con);
	 }
	 
	 /* (non-Javadoc)
	 * @see org.eclipse.core.commands.operations.IUndoableOperation#undo(org.eclipse.core.runtime.IProgressMonitor, org.eclipse.core.runtime.IAdaptable)
	 */
	public IStatus	undo(IProgressMonitor monitor, IAdaptable info){
		 undoableEdit.undo();
		 model.fireChange();
		 drawingPanel.repaint();
		 return Status.OK_STATUS;
	 }
	 
	 /* (non-Javadoc)
	 * @see org.eclipse.core.commands.operations.IUndoableOperation#canExecute()
	 */
	public boolean canExecute(){
			return true;
	 }
	 
	 /* (non-Javadoc)
	 * @see org.eclipse.core.commands.operations.IUndoableOperation#canRedo()
	 */
	public boolean canRedo(){
			return undoableEdit.canRedo();
	 }
		
	 /* (non-Javadoc)
	 * @see org.eclipse.core.commands.operations.IUndoableOperation#dispose()
	 */
	public void dispose(){
     }
		
	 public IStatus	execute(IProgressMonitor monitor, IAdaptable info){
		 return Status.OK_STATUS;
	 }
	 
	 public boolean hasContext(IUndoContext context){
		 return contexts.contains(context);
	 }
	 public IStatus	redo(IProgressMonitor monitor, IAdaptable info){
		 undoableEdit.redo();
		 model.fireChange();
		 drawingPanel.repaint();
		 return Status.OK_STATUS;
	 }
	 
	 /* (non-Javadoc)
	 * @see org.eclipse.core.commands.operations.IUndoableOperation#addContext(org.eclipse.core.commands.operations.IUndoContext)
	 */
	public void addContext(IUndoContext context){
		 contexts.add(context);
	 }
	 
	 /* (non-Javadoc)
	 * @see org.eclipse.core.commands.operations.IUndoableOperation#getLabel()
	 */
	public String getLabel(){
		 return undoableEdit.getPresentationName();
	 }
	 
	 /**
	  * This builds an UndoableAction and pushes it to the undo/redo stack. This method is the only interface to the eclispe redo/undo needed.
	  * The UndoableEdit is the actual edit wrapped in an instance of UndoableAction.
	  * 
	  * @param edit The UndoableEdit to wrap 
	  * @param jcpmodel The current jcpmodel
	  * @param undoContext The current undoContext. From the JCPPage you can get this with .getUndoContext().
	  */
	public static void pushToUndoRedoStack(UndoableEdit edit, JChemPaintModel jcpmodel, IUndoContext undoContext, DrawingPanel drawingPanel){
         UndoableAction undoaction=new UndoableAction(edit,jcpmodel,drawingPanel);
         

			IOperationHistory operationHistory = OperationHistoryFactory.getOperationHistory();

			undoaction.addContext(undoContext);

			try{
			  operationHistory.execute(undoaction, null, null);
			}catch(Exception ex){
				ex.printStackTrace();
			}

	 }
	 
}