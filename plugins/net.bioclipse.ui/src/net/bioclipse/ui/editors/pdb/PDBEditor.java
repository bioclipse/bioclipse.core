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
