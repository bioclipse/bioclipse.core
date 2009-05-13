/*******************************************************************************
 * Copyright (c) 2008-2009 Carl Masak <carl.masak@farmbio.uu.se>
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * <http://www.eclipse.org/legal/epl-v10.html>
 *
 * Contact: http://www.bioclipse.net/
 ******************************************************************************/
package net.bioclipse.scripting.ui.tabcompletion;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TabCompleter {

    /**
     * Tab completion has a sort of short-term memory in the form of this
     * instance variable, which remembers the expansion "result" of the last
     * tab completion. Something like this is needed so that the tab completer
     * can beep the first time upon encountering an ambiguous completion, and
     * print the alternatives the second time.
     *
     * Note also that even this isn't perfect. There's an unlikely false
     * positive when the value of this variable survives to the next set of tab
     * completions -- let's say that the user tab completes on "foo" and gets
     * first a beep and then a list of alternatives. She goes for a cup of
     * coffee, and five minutes later when she presses tab on "foo" again as
     * part of a different command, she will get the list and not a beep,
     * because this variable still contains "foo".
     */
    private String lastPrefix = null;
    
    private boolean secondTime = false;
    
    public List<String> complete(String prefix, List<String> variables) {
        List<String> result = new ArrayList<String>();
        for (String variable : variables)
            if (variable.toLowerCase().startsWith(prefix.toLowerCase()))
                result.add( variable );
        String longestCommonPrefix = commonPrefix(result);
        if ( prefix.length() > longestCommonPrefix.length() ) {
            return Collections.emptyList();
        }
        secondTime = lastPrefix != null && lastPrefix.equals(prefix);
        lastPrefix = longestCommonPrefix;
        return result;
    }

    /* Returns the longest common prefix of all strings in a list. */
    public String commonPrefix(List<String> strings) {
        if (strings.size() == 0)
            return "";
        String commonPrefix = strings.get(0);
        for (String s : strings)
            while ( !s.toLowerCase().startsWith(commonPrefix.toLowerCase()) )
                commonPrefix
                    = commonPrefix.substring(0, commonPrefix.length() - 1);
        return commonPrefix;
    }

    public boolean secondTime() {
        return secondTime;
    }

    public String completions(List<String> variables) {
        List<String> variablesCopy = new ArrayList<String>(variables);
        Collections.sort( variablesCopy );
        String varList = variablesCopy.toString();
        return varList.substring(1, varList.length() - 1).replace(',', ' ');
    }
}
