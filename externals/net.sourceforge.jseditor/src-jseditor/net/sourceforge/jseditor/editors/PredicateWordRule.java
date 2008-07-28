/*
 * Created on May 13, 2003
 *========================================================================
 * Modifications history
 *========================================================================
 * $Log: PredicateWordRule.java,v $
 * Revision 1.2  2003/05/30 20:53:09  agfitzp
 * 0.0.2 : Outlining is now done as the user types. Some other bug fixes.
 *
 *========================================================================
 */
package net.sourceforge.jseditor.editors;

import org.eclipse.jface.text.rules.ICharacterScanner;
import org.eclipse.jface.text.rules.IPredicateRule;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.Token;
import org.eclipse.jface.text.rules.WordRule;
import org.eclipse.jface.text.rules.IWordDetector;

/**
 * @author fitzpata
 */
public class PredicateWordRule extends WordRule implements IPredicateRule {

	/* (non-Javadoc)
	 * @see org.eclipse.jface.text.rules.IPredicateRule#getSuccessToken()
	 */
	 
	protected IToken successToken = Token.UNDEFINED;
	 
	public void addWords(String[] tokens, IToken token)
	{
		for (int i = 0; i < tokens.length; i++) {
			addWord(tokens[i], token);
		}
		
	}
	 
	public IToken getSuccessToken() {
		return successToken;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.text.rules.IPredicateRule#evaluate(org.eclipse.jface.text.rules.ICharacterScanner, boolean)
	 */
	public IToken evaluate(ICharacterScanner scanner, boolean resume) {
		successToken = this.evaluate(scanner, resume);//true);
		return successToken;
	}
	
	/**
	 * Creates a rule which, with the help of an word detector, will return the token
	 * associated with the detected word. If no token has been associated, the scanner 
	 * will be rolled back and an undefined token will be returned in order to allow 
	 * any subsequent rules to analyze the characters.
	 *
	 * @param detector the word detector to be used by this rule, may not be <code>null</code>
	 *
	 * @see #addWord
	 */

	public PredicateWordRule(IWordDetector detector) {
		super(detector);
	}

	/**
	 * Creates a rule which, with the help of an word detector, will return the token
	 * associated with the detected word. If no token has been associated, the
	 * specified default token will be returned.
	 *
	 * @param detector the word detector to be used by this rule, may not be <code>null</code>
	 * @param defaultToken the default token to be returned on success 
	 *		if nothing else is specified, may not be <code>null</code>
	 *
	 * @see #addWord
	 */
	public PredicateWordRule(IWordDetector detector, IToken defaultToken) {
		super(detector, defaultToken);
	}


	public PredicateWordRule(IWordDetector detector, String tokenString, IToken tokenType) {
		super(detector);
		this.addWord(tokenString, tokenType);
	}
	
	public PredicateWordRule(IWordDetector detector, String[] tokens, IToken tokenType) {
		super(detector);
		this.addWords(tokens, tokenType);
	}

	public PredicateWordRule(IWordDetector detector, IToken defaultToken, String[] tokens, IToken tokenType) {
		super(detector, defaultToken);
		this.addWords(tokens, tokenType);
	}

}
