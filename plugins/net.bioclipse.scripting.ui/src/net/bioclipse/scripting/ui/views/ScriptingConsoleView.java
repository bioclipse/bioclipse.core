/* *****************************************************************************
 * Copyright (c) 2008-2009 Carl Masak <carl.masak@farmbio.uu.se>
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * <http://www.eclipse.org/legal/epl-v10.html>
 *
 * Contact: http://www.bioclipse.net/
 ******************************************************************************/
package net.bioclipse.scripting.ui.views;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.bioclipse.scripting.ui.tabcompletion.TabCompleter;

import org.eclipse.core.resources.IResource;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.util.LocalSelectionTransfer;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DropTarget;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.dnd.DropTargetListener;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.TraverseEvent;
import org.eclipse.swt.events.TraverseListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.ui.texteditor.IWorkbenchActionDefinitionIds;

/**
 * A general scripting console.
 *
 * @author masak
 *
 */
public abstract class ScriptingConsoleView extends ViewPart {
    
    /** A string representing the system's newline. */
    public static String NEWLINE = System.getProperty("line.separator");
    
    /** The preferred maximum length of a line of output. */
    protected static final int MAX_OUTPUT_LINE_LENGTH = 79;

    private Text output;
    private Text input;
    
    /** List of all commands written.
     *
     * Don't be alarmed by the double braces after the constructor call; they
     * are amply explained at http://norvig.com/java-iaq.html under the section
     * "I have a class with six...".
     */
    @SuppressWarnings("serial")
    private List<String> commandHistory = new ArrayList<String>() {{
        add("");
    }};

    /**
     * An index pointer into the command history. Between commands, it is reset
     * to point to the last (still not run) command, but changes when ARROW_UP
     * and ARROW_DOWN keys are used.
     */
    private int currentHistoryLine = 0;
    
    private TabCompleter tabCompleter = new TabCompleter();

    /**
     * Represents something to do when a specific key is pressed. Java 5 doesn't
     * have closures, so we use anonymous classes with a method in it instead,
     * which amounts to the same thing.
     */
    protected static interface KeyAction {
        public void receiveKey(KeyEvent e);
    }

    /**
     * Essentially a switching table for handleKey. So, every time a keypress
     * is made that we intercept, a receiveKey method somewhere in actionTable
     * is called.
     *
     * Don't be alarmed by the double braces after the constructor call; they
     * are amply explained at http://norvig.com/java-iaq.html under the section
     * "I have a class with six...".
     */
    @SuppressWarnings("serial")
    private Map<Integer, KeyAction> actionTable
        = new HashMap<Integer, KeyAction>() {{
        put( new Integer(SWT.CR), new KeyAction() {
            public void receiveKey(KeyEvent e) {
                carryOutCommand(input.getText().trim());
            }
        });
        put( new Integer(SWT.KEYPAD_CR), new KeyAction() {
            public void receiveKey(KeyEvent e) {
                carryOutCommand(input.getText().trim());
            }
        });
        put( new Integer(SWT.ARROW_UP), new KeyAction() {
            public void receiveKey(KeyEvent e) {
                if (currentHistoryLine == commandHistory.size() - 1)
                    commandHistory.set( commandHistory.size() - 1,
                                        input.getText().trim() );
                if (currentHistoryLine > 0) {
                    String previousCommand
                        = commandHistory.get(--currentHistoryLine);
                    input.setText( previousCommand );
                    input.setSelection( previousCommand.length(),
                                        previousCommand.length() );
                }
                
            }
        });
        put( new Integer(SWT.ARROW_DOWN), new KeyAction() {
            public void receiveKey(KeyEvent e) {
                if (currentHistoryLine < commandHistory.size() - 1) {
                    String nextCommand
                        = commandHistory.get(++currentHistoryLine);
                    input.setText( nextCommand );
                    input.setSelection( nextCommand.length(),
                                        nextCommand.length() );
                }
            }
        });
        put( new Integer(32), new KeyAction() { // space
            public void receiveKey(KeyEvent e) {
                if (e.stateMask == SWT.CTRL) {
                    tabComplete();
                }
                else {
                    e.doit = true;
                }
            }
        });
    }};

    /**
     * The constructor. Called by Eclipse reflection when a new console
     * is created.
     */
    public ScriptingConsoleView() {
    }

    /**
     * This is a callback that will allow us to create the view and
     * initialize it.
     */
    public void createPartControl(Composite parent) {
        GridLayout layout = new GridLayout();
        layout.numColumns = 1;
        parent.setLayout(layout);
        
        output = new Text(
            parent,
            SWT.READ_ONLY | SWT.MULTI | SWT.V_SCROLL | SWT.BORDER | SWT.WRAP
        );
        output.setFont(JFaceResources.getTextFont());
        GridData outputData = new GridData(GridData.FILL_BOTH);
        output.setBackground(new Color(parent.getDisplay(), 0xFF, 0xFF, 0xFF));
        output.setLayoutData(outputData);
        output.addKeyListener(new KeyListener() {
            public void keyPressed(KeyEvent e) {
                if (e.character != '\0' && e.stateMask == 0) {
                    e.doit = false;
                    input.setText( input.getText() + e.character );
                    input.setSelection( input.getText().length() );
                    input.setFocus();
                }
                else if (actionTable.containsKey( e.keyCode )) {
                    input.setFocus();
                    handleKey(e);
                }
                // "Paste" forwarding.
                // SWT.MOD1 is Ctrl or Command as appropriate based on the
                // platform. That funny '&' is a bitop. See the JLS.
                else if ((Character.toLowerCase(e.character) == 'v'
                          || e.keyCode == 'v')
                         && (e.stateMask & SWT.MOD1) != 0) {
                    input.setFocus();
                    input.paste();
                }
                else if (Character.toLowerCase(e.character) == 'c'
                         && (e.stateMask & SWT.MOD1) != 0) {
                    // We'll want to let this one pass through, so that
                    // the output can do copying as it should.
                    // Added because of #1076, shk3++.
                }
            }
            public void keyReleased(KeyEvent _) { }
        });
        
        input = new Text(parent, SWT.MULTI | SWT.BORDER | SWT.V_SCROLL);
        input.setFont(JFaceResources.getTextFont());
        input.addKeyListener( new KeyListener() {
            public void keyPressed(KeyEvent e) { handleKey(e); }
            public void keyReleased(KeyEvent _) { }
        });
        input.addTraverseListener( new TraverseListener() {
            public void keyTraversed(TraverseEvent e) {
                if (e.detail == SWT.TRAVERSE_TAB_NEXT) {
                    e.doit = false;
                    tabComplete();
                }
            }
        });
        GridData inputData = new GridData(GridData.FILL_HORIZONTAL);
        inputData.heightHint = 20;
        input.setLayoutData(inputData);
        
        hookContextMenu();
        enableResourceDropSupport();
    }
    
    protected void handleKey(KeyEvent e) {
        if (actionTable.containsKey( e.keyCode )) {
            e.doit = false;
            actionTable.get( e.keyCode ).receiveKey( e );
        }
    }
    
    /** Sets up and installs the context menu for the console view. */
    private void hookContextMenu() {
        final Action cutInputAction = new Action("Cut") {
            public void run() {
                input.cut();
            }
        },
        copyInputAction = new Action("Copy") {
            public void run() {
                input.copy();
            }
        },
        pasteInputAction = new Action("Paste") {
            public void run() {
                input.paste();
            }
        },
        copyOutputAction = new Action("Copy") {
            public void run() {
                output.copy();
            }
        };
        final Action clearAction = new Action("Clear") {
            public void run() {
                output.setText( "" );
            }
        };

        cutInputAction.setActionDefinitionId(
                IWorkbenchActionDefinitionIds.CUT);
        copyInputAction.setActionDefinitionId(
                IWorkbenchActionDefinitionIds.COPY);
        pasteInputAction.setActionDefinitionId(
                IWorkbenchActionDefinitionIds.PASTE);
        copyOutputAction.setActionDefinitionId(
                IWorkbenchActionDefinitionIds.COPY);

        final MenuManager inputMenuMgr = new MenuManager("#PopupMenu");
        inputMenuMgr.setRemoveAllWhenShown(true);
        inputMenuMgr.addMenuListener(new IMenuListener() {
            public void menuAboutToShow(IMenuManager mgr) {
                inputMenuMgr.add( cutInputAction );
                inputMenuMgr.add( copyInputAction );
                inputMenuMgr.add( pasteInputAction );
            }
        });
        input.setMenu( inputMenuMgr.createContextMenu( input ) );

        final MenuManager outputMenuMgr = new MenuManager("#PopupMenu");
        outputMenuMgr.setRemoveAllWhenShown(true);
        outputMenuMgr.addMenuListener(new IMenuListener() {
            public void menuAboutToShow(IMenuManager mgr) {
                outputMenuMgr.add( copyOutputAction );
                outputMenuMgr.add( clearAction );
            }
        });
        output.setMenu( outputMenuMgr.createContextMenu( output ) );
    }

    private void enableResourceDropSupport() {
        int ops = DND.DROP_MOVE;

        DropTargetListener dropListener = new DropTargetListener() {
            public void dragEnter(DropTargetEvent event) {};
            public void dragOver(DropTargetEvent event) {};
            public void dragLeave(DropTargetEvent event) {};
            public void dragOperationChanged(DropTargetEvent event) {};
            public void dropAccept(DropTargetEvent event) {}
            public void drop(DropTargetEvent event) {
                if (event.data == null) {
                  event.detail = DND.DROP_NONE;
                  return;
                }
                if (event.data instanceof TreeSelection) {
                    TreeSelection selection = (TreeSelection) event.data;
                    for (Object item : selection.toArray()) {
                        String content
                            = item instanceof IResource
                              ? interceptDroppedString(((IResource)item)
                                      .getFullPath()
                                      .toOSString())
                              : "[O_o]"; // unrecognized content

                        int pos = input.getCaretPosition();
                        String
                          quote        = "\"",
                          beforeCursor = pos > 0
                                           ? input.getText()
                                                  .substring(pos-1, pos)
                                           : "",
                          afterCursor  = pos < input.getText().length()
                                           ? input.getText()
                                                  .substring(pos, pos+1)
                                           : "";
                        if ( !beforeCursor.equals(quote) )
                            content = quote + content;
                        if ( !afterCursor.equals(quote))
                            content += quote;
                        addAtCursor(content);
                        setFocus();
                    }
                }
            }
        };
          
        DropTarget inputTarget = new DropTarget(input, ops),
                   outputTarget = new DropTarget(output, ops);
        inputTarget.setTransfer(
                new Transfer[] { LocalSelectionTransfer.getTransfer() } );
        outputTarget.setTransfer(
                new Transfer[] { LocalSelectionTransfer.getTransfer() } );

        inputTarget.addDropListener( dropListener );
        outputTarget.addDropListener( dropListener );
    }

    /**
     * Intercepts a string before it is dropped into the console.
     * Meant to be overridden by deriving classes.
     */
    protected String interceptDroppedString( String s ) {
        // Fix for Windows, because single backslashes are treated as meta-
        // characters in js strings. Note that the quadruple backslashes are
        // needed because '\' is a metacharacter in Java strings as well as
        // in the regex language.
        return s.replaceAll("\\\\", "/");
    }

    public String currentCommand() {
        return input.getText();
    }

    /**
     * Empties the console of contents.
     */
    public void clearConsole() {
        synchronized (output) {
            output.setText( NEWLINE );
        }
    }
    
    /**
     * Prints a piece of text to the console. The text ends up before the
     * active command line.
     * 
     * @param message the text to be printed
     */
    public void printMessage(String message) {
        
        if (message == null)
            return;

        // Non-printable characters are removed because people have complained
        // of seeing them. See http://en.wikipedia.org/wiki/Robustness_Principle
        // for more information. Also, feel free to add other disturbing non-
        // printables here.
        message = message.replaceAll("\u0008", "");
        // R has a tendency to output newlines as "\r\n". Bringing those in line
        // here. If you read the below code and think that it could be
        // shortened to one call, you're probably not taking R's chunking into
        // account.
        message = message.replaceAll("\r", "");
        message = message.replaceAll("\n", NEWLINE);
        
        synchronized (output) {
            output.append(message);
            output.setFont(JFaceResources.getTextFont());
            output.redraw();
        }
    }

    /** Makes a system notification sound. */
    protected void beep() {
        Display.getCurrent().beep();
    }

    @Override
    public void setFocus() {
        input.setFocus();
    }
    
    /**
     * Executes a command in the underlying scripting engine.
     * 
     * @param command the command to be executed
     * @return the return value/error message (if any) from the command
     */
    protected abstract String executeCommand(String command);
    
    /**
     * Returns all names of variables and methods contained in a certain
     * container object, or, if <code>""</code> or <code>null</code> is passed,
     * in the root container object. This method is meant to be overridden
     * by deriving classes.
     *
     * @param object The container object of interest
     * @return A list of all the variable names in the container object
     */
    @SuppressWarnings("unchecked")
    protected List<String> allNamesIn(String object) {
        return Collections.EMPTY_LIST;
    }

    /**
     * Returns all special commands for this scripting console. A special
     * command is an out-of-band command given at the start of a command line.
     * This method is meant to be overridden by deriving classes.
     *
     * @param object The container object of interest
     * @return A list of all the variable names in the container object
     */
    @SuppressWarnings("unchecked")
    protected List<String> allSpecialCommands() {
        return Collections.EMPTY_LIST;
    }

    /**
     * Automatically writes to the command line the rest of a variable or
     * method name. (Names are completed case-insensitively; the case of the
     * already-written parts doesn't matter.) Beeps if no unique such
     * completion exists. Gives a list of possible completions if called a
     * second time.
     *
     * This method is meant to implement generic tab-completion and should
     * generally not need to be overridden in deriving classes. Instead,
     * override <code>allNamesIn</code> and <code>allSpecialCommands</code>,
     * which returns the relevant things to tab-complete on.
     */
    protected void tabComplete() {
        String command = input.getText();
        int pos = input.getCaretPosition() - 1;
        String prefix = eatTermBackwards(command, pos);
        pos -= prefix.length();
        int startOfCompletedWord = pos + 1;
        List<String> variables = new ArrayList<String>();
        String parent = "";
        if ( pos > 0 && command.charAt(pos) == '.' ) {
            if ( Character.isLetterOrDigit( command.charAt(pos-1) ) ) {
                parent = eatTermBackwards(command, pos-1, ".");
                pos -= parent.length() + 1;
            }
            else {
                return;
            }
        }
        else if (pos == -1) {
            variables.addAll(allSpecialCommands());
        }
        variables.addAll(allNamesIn(parent));
        List<String> interestingVariables
            = tabCompleter.complete(prefix, variables);
        if ( interestingVariables.isEmpty() ) {
            beep();
        }
        else if ( interestingVariables.size() > 1
                  && tabCompleter.secondTime() ) {

            printMessage( NEWLINE
                          + tabCompleter.completions(interestingVariables)
                          + NEWLINE );
        }
        else {
            deleteBackwards(input.getCaretPosition() - startOfCompletedWord);
            addAtCursor( tabCompleter.commonPrefix(interestingVariables) );
            if ( interestingVariables.size() == 1 )
                addAtCursor( tabCompletionHook(parent,
                                               interestingVariables.get(0)) );
            else
                beep();
        }
    }

    private String eatTermBackwards(String string, int pos) {
        return eatTermBackwards(string, pos, "");
    }

    private String eatTermBackwards(String string, int pos, String okChars) {
        String accTerm = "";
        for (char additionalCharacter; pos >= 0 && (Character.isLetterOrDigit(
                additionalCharacter = string.charAt(pos))
                || okChars.contains("" + additionalCharacter)); --pos)
            accTerm = additionalCharacter + accTerm;
        return accTerm;
    }

    private void deleteBackwards(int length) {
        int oldPosition = input.getCaretPosition(),
            newPosition = oldPosition - length;
        
        String oldText = input.getText(),
               before = oldText.substring(0, newPosition),
               after = oldText.substring(oldPosition);
                
        input.setText(before + after);
        input.setSelection( newPosition );
    }

    /**
     * Outputs extra characters after the actual name of the completed thing.
     * For managers, this could be a period ("."), because that's what the
     * user will write herself anyway. For methods, it could be "(", or "()"
     * if the method has no parameters.
     *
     * @param object the thing written before the dot (if any) when completing
     * @param completedVariable the variable that was just tab-completed
     * @return any extra characters to be output after the completed name
     */
    protected String tabCompletionHook( String parent, String completedName ) {
        return "";
    }

    /**
     * Inserts text at the cursor.
     *
     * @param newText the text to insert
     */
    protected void addAtCursor(String newText) {
        int oldPosition = input.getSelection().x;
        String allText = input.getText();
        String textWithNewTextAdded
            = allText.substring( 0, input.getCaretPosition() )
              + newText
              + allText.substring( input.getCaretPosition() );
        input.setText( textWithNewTextAdded );
        input.setSelection( oldPosition + newText.length() );
    }
    
    /**
     * Prints the command to the console with an appropriate prefix,
     * and executes it. Doesn't do anything if the command string is empty.
     * 
     * @param command the command to be printed and executed
     */
    public void carryOutCommand(String command){
        input.setText("");
        if ("".equals(command))
            return;
        commandHistory.remove( commandHistory.size() - 1 );
        commandHistory.add( command );
        commandHistory.add( "" );
        currentHistoryLine = commandHistory.size() - 1;
        executeCommand(command);
    }
}
