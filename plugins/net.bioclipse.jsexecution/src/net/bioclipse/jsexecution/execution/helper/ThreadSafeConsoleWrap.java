package net.bioclipse.jsexecution.execution.helper;

import java.text.SimpleDateFormat;
import java.util.Date;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.console.MessageConsole;
import org.eclipse.ui.console.MessageConsoleStream;

/*
 * This file is part of the Bioclipse JsExecution Plug-in.
 * 
 * Copyright (c) 2008 Johannes Wagener.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Johannes Wagener - initial API and implementation
 */
public class ThreadSafeConsoleWrap {
        private MessageConsole messageConsole = null;
        private MessageConsoleStream out = null, out_blue = null, out_red = null;
        
        public ThreadSafeConsoleWrap(MessageConsole console) {
        	messageConsole = console;
        }
        
        public void writeToConsole(final String message) {
                Runnable r = new Runnable() {
                        public void run() {
                                getConsoleStream().println(message);
                        }
                };
                Display.getDefault().asyncExec(r);
        }
        public void writeToConsoleBlue(final String message) {
                Runnable r = new Runnable() {
                        public void run() {
                                getConsoleStreamBlue().println(message);
                        }
                };
                Display.getDefault().asyncExec(r);
        }
        public void writeToConsoleRed(final String message) {
                Runnable r = new Runnable() {
                        public void run() {
                                getConsoleStreamRed().println(message);
                        }
                };
                Display.getDefault().asyncExec(r);
        }
        // with time-stamp
        public void writeToConsoleBlueT(String message) {
                writeToConsoleBlue(getCurrentTime() + " " + message);
        }
        // with time-stamp
        public void writeToConsoleT(String message) {
                writeToConsole(getCurrentTime() + " " + message);
        }
        // with time-stamp
        public void writeToConsoleRedT(String message) {
                writeToConsoleRed(getCurrentTime() + " " + message);
        }
        private MessageConsoleStream getConsoleStream() {
                if (out == null)
                        out = messageConsole.newMessageStream();
                return out;
        }
        private MessageConsoleStream getConsoleStreamBlue() {
                if (out_blue == null) {
                        Color color_blue = PlatformUI.getWorkbench().getDisplay().getSystemColor(SWT.COLOR_BLUE);
                        out_blue = messageConsole.newMessageStream();
                        out_blue.setColor(color_blue);
                }
                return out_blue;
        }
        private MessageConsoleStream getConsoleStreamRed() {
                if (out_red == null) {
                        Color color_red = PlatformUI.getWorkbench().getDisplay().getSystemColor(SWT.COLOR_RED);
                        out_red = messageConsole.newMessageStream();
                        out_red.setColor(color_red);
                }
                return out_red;
        }
        private String getCurrentTime() {
                SimpleDateFormat simpleDateForm = new SimpleDateFormat("hh:mm:ss");
                Date current = new Date();
                current.setTime(System.currentTimeMillis());
                return simpleDateForm.format(current);
        }
}