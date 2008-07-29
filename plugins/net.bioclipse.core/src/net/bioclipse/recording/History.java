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

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.widgets.Display;

/**
 * @author jonalv
 *
 */
public class History implements IHistory {

    private List<IRecord> records;
    private List<IHistoryListener> historyListeners;
    
    public History() {
        records          = new ArrayList<IRecord>();
        historyListeners = new ArrayList<IHistoryListener>();
    }
    
    /* (non-Javadoc)
     * @see net.bioclipse.recording.IHistory#addRecord(net.bioclipse.recording.MethodRecord)
     */
    public void addRecord( IRecord record ) {
        records.add(record);
        Display.getDefault().asyncExec( new Runnable() {
            public void run() {
                fireHistoryEvent( new HistoryEvent() );
            }
        } );
    }
    
    private void fireHistoryEvent(HistoryEvent e) {
        for(IHistoryListener l : historyListeners) {
            l.receiveHistoryEvent(e);
        }
    }
    
    /* (non-Javadoc)
     * @see net.bioclipse.recording.IHistory#getRecords()
     */
    public List<IRecord> getRecords() {
        return new ArrayList<IRecord>(records);
    }
    
    public int getRecordCount() {
        return records.size();
    }

    public void addHistoryListener(IHistoryListener l) {
        historyListeners.add(l);
    }

    public void removeHistoryListener(IHistoryListener l) {
        historyListeners.remove(l);
    }
}
