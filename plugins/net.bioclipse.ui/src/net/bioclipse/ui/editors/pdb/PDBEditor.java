/*******************************************************************************
 *Copyright (c) 2008 The Bioclipse Team and others.
 *All rights reserved. This program and the accompanying materials
 *are made available under the terms of the Eclipse Public License v1.0
 *which accompanies this distribution, and is available at
 *http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Ola Spjuth - core API and implementation
 *******************************************************************************/
package net.bioclipse.ui.editors.pdb;
import net.bioclipse.ui.editors.keyword.KeywordEditor;
import net.bioclipse.ui.editors.keyword.KeywordSourceViewerConfig;
import org.apache.log4j.Logger;
import org.eclipse.jface.text.source.SourceViewerConfiguration;
public class PDBEditor extends KeywordEditor{
    private static final Logger logger = Logger.getLogger(PDBEditor.class);
    public PDBEditor() {
        super(new PDBKeywords());
        setKeywords(new PDBKeywords());
        SourceViewerConfiguration cfg=getSourceViewerConfiguration();
        if (cfg instanceof KeywordSourceViewerConfig) {
            KeywordSourceViewerConfig kcfg = (KeywordSourceViewerConfig) cfg;
            kcfg.setScanner(new PDBRuleScanner(new PDBKeywords()));
        }
        else{
            logger.error("Could not instantiate PDBRuleScanner due to faulty SourceViewerConfig.");
        }
    }
}
