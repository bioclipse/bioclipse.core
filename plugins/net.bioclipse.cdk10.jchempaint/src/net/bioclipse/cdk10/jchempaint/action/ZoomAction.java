/*
 *  $RCSfile: ZoomAction.java,v $
 *  $Author: shk3 $
 *  $Date: 2006/01/12 17:22:49 $
 *  $Revision: 1.5.2.1 $
 *
 *  Copyright (C) 2003-2005  The JChemPaint project
 *
 *  Contact: jchempaint-devel@lists.sourceforge.net
 *
 *  This program is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Lesser General Public License
 *  as published by the Free Software Foundation; either version 2.1
 *  of the License, or (at your option) any later version.
 *  All we ask is that proper credit is given for our work, which includes
 *  - but is not limited to - adding the above copyright notice to the beginning
 *  of your source code files, and to any copyright notice that you may distribute
 *  with programs based on this work.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public License
 *  along with this program; if not, write to the Free Software
 *  Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 */
package net.bioclipse.cdk10.jchempaint.action;

import java.awt.event.ActionEvent;

import net.bioclipse.cdk10.jchempaint.ui.editor.IJCPBasedEditor;
import net.bioclipse.cdk10.jchempaint.ui.editor.JCPPage;
import net.bioclipse.cdk10.jchempaint.ui.editor.action.JCPAction;

import org.openscience.cdk.applications.jchempaint.JChemPaintModel;
import org.openscience.cdk.renderer.Renderer2DModel;


/**
 *  Description of the Class
 *
 * @cdk.module jchempaint
 * @author     steinbeck
 */
public class ZoomAction extends JCPAction
{

	public void run() {
		run(null);
	}
	
	/**
	 *  Description of the Method
	 *
	 *@param  e  Description of the Parameter
	 */
	public void run(ActionEvent e)
	{
		logger.debug("Zooming in/out in mode: " + type);

		JChemPaintModel jcpm=null;
    if ( this.getContributor().getActiveEditorPart() instanceof IJCPBasedEditor ) {
        IJCPBasedEditor ed = (IJCPBasedEditor) this.getContributor().getActiveEditorPart();
        jcpm = ed.getJcpModel();
    }
    else if (this.getContributor().getActiveEditorPart() instanceof JCPPage ){
        JCPPage ed = (JCPPage) this.getContributor().getActiveEditorPart();
        jcpm = ed.getJcpModel();
    }
		
		
		Renderer2DModel renderModel = jcpm.getRendererModel();
		if (type.equals("in"))
		{
			renderModel.setZoomFactor(renderModel.getZoomFactor() * 1.5);
//			jcpPanel.getScrollPane().getViewport().setViewPosition(new Point((int)(jcpPanel.getScrollPane().getViewport().getViewPosition().x*1.5),(int)(jcpPanel.getScrollPane().getViewport().getViewPosition().y*1.5)));
		} else if (type.equals("out"))
		{
			renderModel.setZoomFactor(renderModel.getZoomFactor() / 1.5);
//			jcpPanel.getScrollPane().getViewport().setViewPosition(new Point((int)(jcpPanel.getScrollPane().getViewport().getViewPosition().x/1.5),(int)(jcpPanel.getScrollPane().getViewport().getViewPosition().y/1.5)));
		} else if (type.equals("original"))
		{
			renderModel.setZoomFactor(1.0);
		} else
		{
			logger.error("Unkown zoom command: " + type);
		}
		jcpm.fireChange();
	}

}

