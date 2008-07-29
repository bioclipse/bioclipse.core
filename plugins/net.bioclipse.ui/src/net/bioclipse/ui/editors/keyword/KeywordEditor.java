/*******************************************************************************
 * Copyright (c) 2006, 2008 Bioclipse Project
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Ola Spjuth - core API and implementation
 *******************************************************************************/

package net.bioclipse.ui.editors.keyword;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.editors.text.TextEditor;


/**
 * Add jmol syntax completion and coloring
 *
 * @author ola
 *
 */
public abstract class KeywordEditor extends TextEditor {

    public static final String ID = "net.bioclipse.editors.jmol.KeywordEditor";
    Keywords keywords;

//    IAction runScriptAction;



    protected static String[] subStrings;

    protected static List<String> startList;
    protected static List<String> fullList;


    /**
     * Constructor with no params, set up no keywords or rules
     *
     */
    public KeywordEditor()
    {
        super();
//        computeSubStrings();
//        setSourceViewerConfiguration(new KeywordSourceViewerConfig());
    }

    /**
     * Constructor with IKeywords as parameter to set up keywords
     *
     */
    public KeywordEditor(Keywords keywords) {
        super();
        setKeywords(keywords);
        computeSubStrings();
        setSourceViewerConfiguration(new KeywordSourceViewerConfig());
    }

    /**
     * Add action bars by overriding
     */
    @Override
    public void createPartControl(Composite parent) {
        super.createPartControl(parent);

        contributeToActionBars();

    }

    private void contributeToActionBars() {
        IActionBars bars = getEditorSite().getActionBars();
//        fillLocalPullDown(bars.getMenuManager());
        fillLocalToolBar(bars.getToolBarManager());
    }

    private void fillLocalToolBar(IToolBarManager manager) {
//        manager.add(runScriptAction);
        manager.add(new Separator());
        // Other plug-ins can contribute there actions here
        manager.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
    }

    /**
     * Add script acction by overriding
     */
    @Override
    protected void createActions() {
        super.createActions();

//        runScriptAction = new RunScriptAction();
//        runScriptAction.setToolTipText("Run Script");
//        runScriptAction.setImageDescriptor(ImageUtils.getImageDescriptor("run"));


    }

    public static String[] getSubStrings() {
        return subStrings;
    }
/*
    public static String[] getJmolProposals() {
        return jmolProposals;
    }
*/
    /**
     * Compute all substrings of the proposals
     */

    private void computeSubStrings() {

        startList = new ArrayList<String>();
        fullList = new ArrayList<String>();

        //Concatenate JmolKeywords to proposals

        String[] proposals=getAllKeywords();

        //All words
        for (int i=0;i<proposals.length;i++){

            String name=proposals[i];

            //This word
            for (int j=1;j<name.length();j++){
                String startAdd=name.substring(0,j);
//                String endAdd=name.substring(j,name.length());
                startList.add(startAdd);
                fullList.add(name);
            }
        }

        subStrings=new String[startList.size()];
        for (int i=0; i< fullList.size();i++){
            subStrings[i]=(String)startList.get(i);
        }
    }



    @SuppressWarnings("unchecked")
    public static String[] lookUpNames(String start){

        if (startList==null) return null;
        if (startList.size()<=0) return null;

        //Temp list
        ArrayList lst=new ArrayList();

        //Look through all in startlist and add matching names to ret
        for (int i=0; i< startList.size();i++){
            String thisStart = (String)startList.get(i).toString().toLowerCase();
            if (thisStart.startsWith(start.toLowerCase())){
                if (!(lst.contains(fullList.get(i).toString().toLowerCase())))
                    lst.add(fullList.get(i).toString().toLowerCase());
            }
        }

        Collections.sort(lst);

        //Convert list to array of strings
        String[] ret=new String[lst.size()];
        for (int i=0; i< lst.size();i++){
            ret[i]=(String)lst.get(i);
        }

        return ret;
    }


    //=========================
    // Abstract classes
    //=========================

    /**
     * Should be overridden by subclasses
     * @return
     */
    protected String[] getAllKeywords(){
        return keywords.getAllKeywords();
    }

    public Keywords getKeywords() {
        return keywords;
    }

    public void setKeywords(Keywords keywords) {
        this.keywords = keywords;
    }


}

