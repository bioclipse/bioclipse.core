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

    private static void println(final MessageConsoleStream consolestream,
            final String message) {
        if (consolestream == null)
            return;

        int message_length = message.length();
        final int MAX_SIZE = 25000;
        if (message_length > MAX_SIZE) {
            Runnable r = new Runnable() {
                public void run() {
                    consolestream.print(message.substring(0, MAX_SIZE));
                    consolestream.println(" [...]");
                    consolestream.println(" A String was truncated: the String "
                            + "exceeded the maxiumum size of "
                            + MAX_SIZE);
                }
            };
            Display.getDefault().asyncExec(r);
        } else {
            Runnable r = new Runnable() {
                public void run() {
                    consolestream.println(message);
                }
            };
            Display.getDefault().asyncExec(r);
        }
    }

    public void writeToConsole(final String message) {
        println(getConsoleStream(), message);

    }

    public void writeToConsoleBlue(final String message) {
        println(getConsoleStreamBlue(), message);
    }

    public void writeToConsoleRed(final String message) {
        println(getConsoleStreamRed(), message);
    }

    // The three methods ending in 'T' below are variations of the three
    // methods above, but they output the message with a time-stamp.

    public void writeToConsoleBlueT(String message) {
        writeToConsoleBlue(getCurrentTime() + " " + message);
    }

    public void writeToConsoleT(String message) {
        writeToConsole(getCurrentTime() + " " + message);
    }

    public void writeToConsoleRedT(String message) {
        writeToConsoleRed(getCurrentTime() + " " + message);
    }

    private MessageConsoleStream getConsoleStream() {
        if (out == null) {
            if (messageConsole == null)
                return null;
            out = messageConsole.newMessageStream();
        }
        return out;
    }

    private MessageConsoleStream getConsoleStreamBlue() {
        if (out_blue == null) {
            Color color_blue = PlatformUI.getWorkbench().getDisplay()
            .getSystemColor(SWT.COLOR_BLUE);
            if (messageConsole == null)
                return null;
            out_blue = messageConsole.newMessageStream();
            out_blue.setColor(color_blue);
        }
        return out_blue;
    }

    private MessageConsoleStream getConsoleStreamRed() {
        if (out_red == null) {
            Color color_red = PlatformUI.getWorkbench().getDisplay()
            .getSystemColor(SWT.COLOR_RED);
            if (messageConsole == null)
                return null;
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