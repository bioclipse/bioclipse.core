 /*******************************************************************************
* Copyright (c) 2007-2008 The Bioclipse Project and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * www.eclipse.org—epl-v10.html <http://www.eclipse.org/legal/epl-v10.html>
 * 
 * Contributors:
 *     Jonathan Alvarsson
 *     
 ******************************************************************************/

package net.bioclipse.recording;

import java.util.List;

/**
 * @author jonalv
 *
 */
public interface IHistory {

    public void addRecord(IRecord record);

    public List<IRecord> getRecords();
    
    public int getRecordCount();
    
    public void addHistoryListener(IHistoryListener l);
    
    public void removeHistoryListener(IHistoryListener l);
}