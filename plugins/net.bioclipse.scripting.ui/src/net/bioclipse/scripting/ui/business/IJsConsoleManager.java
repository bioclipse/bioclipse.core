/* *****************************************************************************
 * Copyright (c) 2007-2009 The Bioclipse Project and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * <http://www.eclipse.org/legal/epl-v10.html>
 * 
 * Contributors:
 *     Jonathan Alvarsson
 *     Carl Masak
 *
 ******************************************************************************/
package net.bioclipse.scripting.ui.business;

import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IProgressMonitor;

import net.bioclipse.core.PublishedClass;
import net.bioclipse.core.PublishedMethod;
import net.bioclipse.core.Recorded;
import net.bioclipse.core.TestClasses;
import net.bioclipse.core.TestMethods;
import net.bioclipse.managers.business.IBioclipseManager;
import net.bioclipse.core.business.BioclipseException;
import net.bioclipse.core.util.IJavaScriptConsolePrinterChannel;

/**
 * Controls programmatic access to the JavaScript Console.
 * 
 * @author masak
 *
 */
@PublishedClass("Controls access to the JavaScript Console.")
@TestClasses("net.bioclipse.scripting.ui.tests.JsConsoleManagerTest")
public interface IJsConsoleManager extends IBioclipseManager, 
                                           IJavaScriptConsolePrinterChannel {

    @Recorded
    @PublishedMethod(methodSummary="Clears the console.")
    @TestMethods("testClear")
    public void clear();

    @Recorded
    @PublishedMethod(params="String message",
                     methodSummary="Prints a message to the console.")
    @TestMethods("testPrint")
    public void print(String message);

    @Recorded
    @PublishedMethod(params="String message",
                     methodSummary="Prints a message to the console, adding a "
                                   + "trailing newline.")
    @TestMethods("testSay")
    public void say(String message);

    @Recorded
    @PublishedMethod(params="String command",
                     methodSummary="Evaluates a script.")
    @TestMethods("testEval")
    public String eval(String command);

    @Recorded
    @TestMethods("testExecute")
    public void execute(IFile file);
    
    @Recorded
    @TestMethods("testExecute")
    @PublishedMethod( params="String file",
                      methodSummary="Runs a js script file.")
    public void execute(String file);
    
    @Recorded
    @TestMethods("testExecute")
    @PublishedMethod( params="List files",
                      methodSummary="Runs a js script file.")
    public void execute(List<?> files) throws BioclipseException;
    
    @Recorded
    @TestMethods("testExecuteFile")
    @Deprecated
    public void executeFile(IFile file);

    @Recorded
    @PublishedMethod(
        params="String filePath",
        methodSummary="Runs a js script file. Marked for removal. " +
                      "Use js.execute instead." )
    @TestMethods("testExecuteFile")
    @Deprecated
    public void executeFile(String filePath);

    public void printError( Throwable t );

    @Recorded
    @PublishedMethod(params="int seconds ",
                     methodSummary="Holds execution a number of seconds")
    @TestMethods("testDelay")
    void delay( int seconds );
}
