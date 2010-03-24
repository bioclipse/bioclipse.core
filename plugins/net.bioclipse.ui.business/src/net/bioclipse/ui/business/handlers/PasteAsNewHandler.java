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
package net.bioclipse.ui.business.handlers;

import net.bioclipse.ui.business.wizards.NewFromClipboardWizard;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;


/**
 * @author jonalv
 *
 */
public class PasteAsNewHandler extends AbstractHandler {

    public Object execute( ExecutionEvent event ) throws ExecutionException {

        NewFromClipboardWizard w = new NewFromClipboardWizard();
        w.performFinish();
        return null;
    }

}
