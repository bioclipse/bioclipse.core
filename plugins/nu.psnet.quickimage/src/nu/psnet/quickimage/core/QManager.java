/*
 * Software released under Common Public License (CPL) v1.0
 */
package nu.psnet.quickimage.core;

import nu.psnet.quickimage.editors.QuickImageEditor;
import nu.psnet.quickimage.widgets.QStatusCanvas;
import nu.psnet.quickimage.widgets.QuickImageCanvas;

/**
 * @author Per Salomonsson
 * 
 */
public class QManager {
	private QuickImageCanvas imageCanvas;
	private QStatusCanvas statusCanvas;
	private ImageOrganizer imageOrganizer;
	private QuickImageEditor imageEditor;

	public QuickImageCanvas getImageCanvas() {
		return imageCanvas;
	}

	public void setImageCanvas(QuickImageCanvas imageCanvas) {
		this.imageCanvas = imageCanvas;
	}

	public QuickImageEditor getImageEditor() {
		return imageEditor;
	}

	public void setImageEditor(QuickImageEditor imageEditor) {
		this.imageEditor = imageEditor;
	}

	public ImageOrganizer getImageOrganizer() {
		return imageOrganizer;
	}

	public void setImageOrganizer(ImageOrganizer imageOrganizer) {
		this.imageOrganizer = imageOrganizer;
	}

	public QStatusCanvas getStatusCanvas() {
		return statusCanvas;
	}

	public void setStatusCanvas(QStatusCanvas statusCanvas) {
		this.statusCanvas = statusCanvas;
	}
}
