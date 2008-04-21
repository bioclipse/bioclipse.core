/*
 * Software released under Common Public License (CPL) v1.0
 */
package nu.psnet.quickimage.editors;

import java.io.File;
import java.io.IOException;

import nu.psnet.quickimage.QuickImagePlugin;
import nu.psnet.quickimage.core.ImageOrganizer;
import nu.psnet.quickimage.core.QManager;
import nu.psnet.quickimage.widgets.QStatusCanvas;
import nu.psnet.quickimage.widgets.QuickImageCanvas;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IStorage;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Platform;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.IStorageEditorInput;
import org.eclipse.ui.editors.text.ILocationProvider;
import org.eclipse.ui.part.EditorPart;
import org.eclipse.ui.part.FileEditorInput;

/**
 * @author Per Salomonsson
 * 
 */
public class QuickImageEditor extends EditorPart {
	private ToolItem previous, next, rotate, zoomIn, zoomOut, zoomOrg, zoomFit,
			view;
	private Image viewThumb, viewFullsize;
	private Composite parent;
	private String iconsdir = null;
	private QManager manager;

	public void createPartControl(Composite parent) {
		this.parent = parent;
		// fileorg = new FileOrganizer();
		manager = new QManager();
		manager.setImageOrganizer(new ImageOrganizer(manager, parent
				.getDisplay()));
		manager.setImageEditor(this);

		try {
			iconsdir = Platform.resolve(
					QuickImagePlugin.getDefault().getBundle().getEntry("/"))
					.getFile()
					+ "icons" + File.separator;
		} catch (IOException e1) {
		}

		// find out what kind the resource(s) to load is
		IEditorInput editorInput = getEditorInput();
		if (editorInput instanceof FileEditorInput) {
			// opened from file system so lets se what is in the current
			// directory as well
			FileEditorInput fileEditorInput = (FileEditorInput) getEditorInput();
			IEditorInput file = getEditorInput();
			setPartName(file.getName());

			manager.getImageOrganizer().setPath(
					fileEditorInput.getPath().removeLastSegments(1)
							.toOSString(), fileEditorInput.getName());
		} else if (editorInput.getAdapter(ILocationProvider.class) != null) {
			ILocationProvider location = (ILocationProvider) editorInput
					.getAdapter(ILocationProvider.class);
			IPath path = (IPath) location.getPath(editorInput);
			setPartName(editorInput.getName());
			manager.getImageOrganizer().setPath(
					path.removeLastSegments(1).toOSString(),
					editorInput.getName());
		} else if (editorInput instanceof IStorageEditorInput) {
			IStorageEditorInput storageEditorInput = (IStorageEditorInput) editorInput;
			IStorage storage;
			try {
				storage = storageEditorInput.getStorage();
				setPartName(storage.getName());
				manager.getImageOrganizer().setStorage(storage);
			} catch (CoreException e) {
				e.printStackTrace();
			}
		} else if (editorInput.getAdapter(IFile.class) != null) {
			IFile file = (IFile) editorInput.getAdapter(IFile.class);
			setPartName(file.getName());
			manager.getImageOrganizer().setPath(
					file.getLocation().removeLastSegments(1).toOSString(),
					file.getName());
		} else {
			// could not display image show err message instead
		}

		initElements();

		manager.getImageOrganizer().setActiveView(ImageOrganizer.VIEW_FULLSIZE);
		manager.getImageCanvas().setIconsPath(iconsdir);
		manager.getImageCanvas().updateFullsizeData();
		manager.getStatusCanvas().updateWithCurrent();
	}

	private void initElements() {
		FormLayout layout = new FormLayout();
		Composite compos = new Composite(parent, SWT.NONE);
		compos.setLayout(layout);
		compos.setLayoutData(new FormData());
		FormData toolbarData = new FormData();

		manager.setImageCanvas(new QuickImageCanvas(manager, compos, SWT.NONE));
		manager.setStatusCanvas(new QStatusCanvas(manager, compos, SWT.NONE));

		ToolBar toolBar = new ToolBar(compos, SWT.FLAT);
		toolBar.setLayoutData(toolbarData);

		previous = new ToolItem(toolBar, SWT.FLAT);
		previous.setToolTipText("Previous Image");
		previous.setImage(new Image(parent.getDisplay(), iconsdir
				+ "previous.gif"));
		previous.setSelection(true);

		next = new ToolItem(toolBar, SWT.FLAT);
		next.setToolTipText("Next Image");
		next.setImage(new Image(parent.getDisplay(), iconsdir + "next.gif"));

		rotate = new ToolItem(toolBar, SWT.FLAT);
		rotate.setToolTipText("Rotate");
		rotate
				.setImage(new Image(parent.getDisplay(), iconsdir
						+ "rotate.gif"));

		viewThumb = new Image(parent.getDisplay(), iconsdir + "thumb.gif");
		viewFullsize = new Image(parent.getDisplay(), iconsdir + "fullsize.gif");
		view = new ToolItem(toolBar, SWT.FLAT);
		view.setToolTipText("view Thumbnails");
		view.setImage(viewThumb);

		new ToolItem(toolBar, SWT.SEPARATOR);

		zoomIn = new ToolItem(toolBar, SWT.FLAT);
		zoomIn.setToolTipText("zoom in");
		zoomIn
				.setImage(new Image(parent.getDisplay(), iconsdir
						+ "zoom_in.gif"));

		zoomOut = new ToolItem(toolBar, SWT.FLAT);
		zoomOut.setToolTipText("zoom out");
		zoomOut.setImage(new Image(parent.getDisplay(), iconsdir
				+ "zoom_out.gif"));

		zoomOrg = new ToolItem(toolBar, SWT.FLAT);
		zoomOrg.setToolTipText("zoom original size");
		zoomOrg.setImage(new Image(parent.getDisplay(), iconsdir
				+ "zoom_100.gif"));

		zoomFit = new ToolItem(toolBar, SWT.CHECK);
		zoomFit.setToolTipText("fit image in window");
		zoomFit.setImage(new Image(parent.getDisplay(), iconsdir
				+ "zoom_fit.gif"));

		FormData canvasData = new FormData();

		// imgCanvas = new QuickImageCanvas(compos, SWT.NONE);
		manager.getImageCanvas().setLayoutData(canvasData);

		FormData statusData = new FormData();

		// statusCanvas = new QStatusCanvas(imgCanvas, compos, SWT.NONE);
		manager.getStatusCanvas().setLayoutData(statusData);

		canvasData.top = new FormAttachment(toolBar, 0);
		canvasData.bottom = new FormAttachment(100, -18);
		canvasData.right = new FormAttachment(100, 0);
		canvasData.left = new FormAttachment(0, 0);

		toolbarData.top = new FormAttachment(0, 0);
		toolbarData.left = new FormAttachment(0, 0);
		toolbarData.right = new FormAttachment(100, 0);

		statusData.top = new FormAttachment(manager.getImageCanvas(), 0);
		statusData.bottom = new FormAttachment(100, 0);
		statusData.right = new FormAttachment(100, 0);
		statusData.left = new FormAttachment(0, 0);

		rotate.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				zoomFit.setSelection(false);
				manager.getImageCanvas().rotate();
			}
		});

		previous.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				clickedPrevious(e);
			}
		});

		next.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				clickedNext(e);
			}
		});

		view.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				toggleView();
			}
		});

		// parent.addDisposeListener(new DisposeListener() {
		//
		// public void widgetDisposed(DisposeEvent e)
		// {
		// disposeAll();
		// }
		// });

		zoomIn.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				zoomFit.setSelection(false);
				manager.getImageCanvas().zoomIn();
			}
		});

		zoomOut.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				zoomFit.setSelection(false);
				manager.getImageCanvas().zoomOut();
			}
		});

		zoomFit.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				manager.getImageCanvas().zoomFit();
			}
		});

		zoomOrg.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				zoomFit.setSelection(false);
				manager.getImageCanvas().zoomOriginal();
			}
		});

		manager.getImageCanvas().addControlListener(new ControlAdapter() {
			public void controlResized(ControlEvent e) {
				if (zoomFit.getSelection()) {
					manager.getImageCanvas().zoomFit();
				}
			}
		});

		previous.setEnabled(manager.getImageOrganizer().hasPrevious());
		next.setEnabled(manager.getImageOrganizer().hasNext());

		if (manager.getImageOrganizer().isSingle()) {
			next.setEnabled(false);
			previous.setEnabled(false);
			view.setEnabled(false);
		}
	}

	public void dispose() {
		manager.getImageOrganizer().dispose();
		manager.getImageCanvas().dispose();
		manager.getStatusCanvas().dispose();
		// Runtime.getRuntime().gc();
		super.dispose();
	}

	// private void disposeAll()
	// {
	// System.out.println("QuickImageEditor.disposeAll()");
	// // previous.getImage().dispose();
	// // next.getImage().dispose();
	// // rotate.getImage().dispose();
	// // zoomIn.getImage().dispose();
	// // zoomOut.getImage().dispose();
	// // view.getImage().dispose();
	// }

	public void toggleView() {
		if (manager.getImageOrganizer().getActiveView() == ImageOrganizer.VIEW_FULLSIZE) {
			previous.setEnabled(false);
			next.setEnabled(false);
			rotate.setEnabled(false);
			manager.getImageOrganizer()
					.setActiveView(ImageOrganizer.VIEW_THUMB);
			manager.getImageOrganizer().setCurrentToSelected();
			view.setImage(viewFullsize);
			view.setToolTipText("View Fullsize");
			view.setEnabled(false);
			view.setEnabled(true);
			zoomIn.setEnabled(false);
			zoomOut.setEnabled(false);
			zoomFit.setEnabled(false);
			zoomOrg.setEnabled(false);
		} else {
			previous.setEnabled(manager.getImageOrganizer().hasPrevious());
			next.setEnabled(manager.getImageOrganizer().hasNext());
			rotate.setEnabled(true);
			manager.getImageOrganizer().setActiveView(
					ImageOrganizer.VIEW_FULLSIZE);
			// manager.getImageOrganizer().setSelectedToCurrent();
			view.setImage(viewThumb);
			view.setToolTipText("View Thumbnails");
			manager.getImageCanvas().updateFullsizeData();
			view.setEnabled(false);
			view.setEnabled(true);
			zoomIn.setEnabled(true);
			zoomOut.setEnabled(true);
			zoomFit.setSelection(false);
			zoomFit.setEnabled(true);
			zoomOrg.setEnabled(true);
		}

		manager.getImageCanvas().updateThumbData();

	}

	private void clickedPrevious(SelectionEvent e) {
		manager.getImageOrganizer().getPrevious();
		manager.getImageCanvas().updateFullsizeData();
		manager.getStatusCanvas().updateWithCurrent();
		setPartName(manager.getImageOrganizer().getCurrent().getDisplayName());
		previous.setEnabled(manager.getImageOrganizer().hasPrevious());
		next.setEnabled(manager.getImageOrganizer().hasNext());
	}

	private void clickedNext(SelectionEvent e) {
		manager.getImageOrganizer().getNext();
		manager.getImageCanvas().updateFullsizeData();
		manager.getStatusCanvas().updateWithCurrent();
		setPartName(manager.getImageOrganizer().getCurrent().getDisplayName());
		previous.setEnabled(manager.getImageOrganizer().hasPrevious());
		next.setEnabled(manager.getImageOrganizer().hasNext());
	}

	public void setPartName(String s) {
		super.setPartName(s);
	}

	public void init(IEditorSite site, IEditorInput input) {
		setSite(site);
		setInput(input);
	}

	public void setFocus() {
		if (manager.getImageCanvas() != null)
			manager.getImageCanvas().setFocus();
	}

	public void doSave(IProgressMonitor monitor) {
	}

	public void doSaveAs() {
	}

	public boolean isDirty() {
		return false;
	}

	public boolean isSaveAsAllowed() {
		return false;
	}

	public String getIconsdir() {
		return iconsdir;
	}

	public void setIconsdir(String iconsdir) {
		this.iconsdir = iconsdir;
	}
}