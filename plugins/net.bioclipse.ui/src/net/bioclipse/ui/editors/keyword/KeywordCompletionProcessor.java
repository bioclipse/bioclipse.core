/*******************************************************************************
 * Copyright (c) 2006 Bioclipse Project
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Ola Spjuth - core API and implementation
 *******************************************************************************/
package net.bioclipse.ui.editors.keyword;

import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.contentassist.CompletionProposal;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.text.contentassist.IContentAssistProcessor;
import org.eclipse.jface.text.contentassist.IContextInformation;
import org.eclipse.jface.text.contentassist.IContextInformationValidator;

/**
 * 
 * @author ola
 *
 */
public class KeywordCompletionProcessor implements IContentAssistProcessor {

	
	
	/* (non-Javadoc)
	 * Method declared on IContentAssistProcessor
	 */
	public ICompletionProposal[] computeCompletionProposals(
		ITextViewer viewer,
		int documentOffset) {

        WordPartDetector wordPart = new WordPartDetector(viewer, documentOffset);

//        logger.debug("Wordpart: " + wordPart.getString());
		
/*
			int myOffset;
			int sublen=10;
			if (documentOffset-10<0){
				myOffset=0;
				sublen=documentOffset;
			}
			else
				myOffset=documentOffset-10;

			String startStr = viewer.getDocument().get(myOffset,sublen);
//			logger.debug("s: " + startStr);
	
			int myLastIndex;
			if (startStr.lastIndexOf(" ") < startStr.lastIndexOf("\n"))
				myLastIndex=startStr.lastIndexOf("\n");
			else
				myLastIndex=startStr.lastIndexOf(" ");

			String searchStr=startStr.substring(myLastIndex+1);
			
//			logger.debug("To validate against: " + searchStr);

			*/

        //Look up all that starts with this startStr
			String[] lookedUp=KeywordEditor.lookUpNames(wordPart.getString());

			ICompletionProposal[] result =
				new ICompletionProposal[lookedUp.length];

			for (int i = 0; i < lookedUp.length; i++) {
				result[i] = new CompletionProposal(lookedUp[i], 
								documentOffset-wordPart.getString().length(), 
								wordPart.getString().length(), 
								lookedUp[i].length());
			}
			return result;
			
	}

	/* (non-Javadoc)
	 * Method declared on IContentAssistProcessor
	 */
	public char[] getCompletionProposalAutoActivationCharacters() {
		return new char[] { '\n', ' ' };
//		return null;
	}

	/* (non-Javadoc)
	 * Method declared on IContentAssistProcessor
	 */
	public char[] getContextInformationAutoActivationCharacters() {
		return null;
	}

	// For Context information 
	/* (non-Javadoc)
	 * Method declared on IContentAssistProcessor
	 */
	public IContextInformationValidator getContextInformationValidator() {
		return null;
	}

	/* (non-Javadoc)
	 * Method declared on IContentAssistProcessor
	 */
	public IContextInformation[] computeContextInformation(
		ITextViewer viewer,
		int documentOffset) {

//		IContextInformation[] ci=new ContextInformation[1];
//		ci[0]=new ContextInformation("ola","ola");
//		return ci;

		return null;
	
	}

	
	/* (non-Javadoc)
	 * Method declared on IContentAssistProcessor
	 */
	public String getErrorMessage() {
		return null;
	}
}
