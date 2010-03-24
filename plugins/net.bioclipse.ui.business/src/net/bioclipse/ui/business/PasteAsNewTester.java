/* *****************************************************************************
 * Copyright (c) 2009  Jonathan Alvarsson <jonalv@users.sourceforge.net>
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * www.eclipse.org—epl-v10.html <http://www.eclipse.org/legal/epl-v10.html>
 *
 * Contact: http://www.bioclipse.net/
 ******************************************************************************/
package net.bioclipse.ui.business;

import org.eclipse.core.expressions.PropertyTester;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.ui.PlatformUI;


/**
 * @author jonalv
 *
 */
public class PasteAsNewTester extends PropertyTester {

    public boolean test( Object receiver,
                         String property,
                         Object[] args,
                         Object expectedValue ) {

        if ("TextInClipBoard".equalsIgnoreCase(property)) {

            if (!(expectedValue instanceof Boolean)) return false;

            boolean expected = (Boolean)expectedValue;
            Clipboard clipBoard 
                = new Clipboard( PlatformUI.getWorkbench()
                                           .getActiveWorkbenchWindow()
                                           .getShell()
                                           .getDisplay() );
            TextTransfer transfer = TextTransfer.getInstance();
            final String data = (String)clipBoard.getContents(transfer);
            boolean actual = (data != null );
            clipBoard.dispose();
            return (actual==expected);
        }
        return false;
    }
}
