package net.bioclipse.ui.views;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
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
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.ui.texteditor.IWorkbenchActionDefinitionIds;

// TODO: make sure text cannot be pasted anywhere
// TODO: colorise commands/output/external output
// TODO: persist history (IMemento)
// TODO: Convert \n in pasted text into command executions

/**
 * A general scripting console.
 * 
 * @author masak
 *
 */
public abstract class ScriptingConsoleView extends ViewPart {

    /** The text contents of this console. */
    private Text text;
    
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
     * An index pointer into the command history. It is reset to point to the
     * last (still not run) command, but changes when ARROW_UP and ARROW_DOWN
     * keys are used.
     */
    private int currentHistoryLine = 0;
    
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
    
    /**
     * Represents something to do when a specific key is pressed. Java 5 doesn't
     * have closures, so we use anonymous classes with a method in it instead,
     * which amounts to the same thing.
     */
    protected static interface KeyAction {
        public void receiveKey(KeyEvent e);
    }
    
    /** Controls whether the results of commands should be output. */
    protected boolean verbose = true;
    
    /**
     * Some asynchronous output comes in little packets, many per line. This
     * variable is <code>true</code> if no newline was sent with the last such
     * output. 
     */
    private boolean outputIsMidLine = false;
    
    /** The preferred maximum length of a line of output. */
    protected static final int MAX_OUTPUT_LINE_LENGTH = 79;
    
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
                
                synchronized (text) {

                    // Sometimes we end up with spurious newlines at the end
                    // of pastes. This leads to the preconditions not being
                    // upheld for currentCommand(). Here we try to save those
                    // preconditions.
                    while ( !lastLineIsCommandLine() ) {
                        text.setText(
                                text.getText().substring(
                                        0,
                                        text.getText().lastIndexOf('\n')
                                ) + "; "
                                + text.getText().substring(
                                        text.getText().lastIndexOf('\n') + 1
                                )
                        );
                    }
                    
                    String command = currentCommand().trim();
                    text.append( "\n" );
                    
                    if (command.matches( "^//\\s*quiet$" ))
                        verbose = false;
                    if (command.matches( "^//\\s*verbose$" ))
                        verbose = true;
                    
                    e.doit = false;
                    
                    int blockLevel = occurrences(command, '{')
                                   - occurrences(command, '}');
      
                    if ( blockLevel > 0
                         || endsWithBinaryOperator(command) ) {
                        
                        text.append( continuationLinePrompt() );
                        text.showSelection();
                        
                        return;
                    }
    
                    command = command.replace('\n', ' ');
                    
                    if ( !"".equals(command) ) {
                        commandHistory.remove( commandHistory.size() - 1 );
                        
                        commandHistory.add( command );
                        commandHistory.add( "" );
                        currentHistoryLine = commandHistory.size() - 1;
                    }
                    
                    String result;
                    
                    try {
                        result = executeCommand(command);
                    }
                    catch (Exception ex) {
                        result = "Evaluation fatal error: " + ex.getMessage();
                    }
                    
                    if (verbose) {
                        text.append( splitIntoSeveralLines(
                                result, MAX_OUTPUT_LINE_LENGTH) );
                        
                        if ( result != null && !result.equals("") )
                            text.append( "\n" );
                    }
                    
                    text.append( commandLinePrompt() );
    
                    scrollDownToPrompt();
                    
                    outputIsMidLine = false;
                }
            }
        });
        
        put( new Integer(SWT.ARROW_UP), new KeyAction() {
            public void receiveKey(KeyEvent e) {
                if (currentHistoryLine > 0)
                    setCurrentCommand(
                            commandHistory.get(--currentHistoryLine) );
    
                e.doit = false;
            }
        });

        put( new Integer(SWT.ARROW_DOWN), new KeyAction() {
            public void receiveKey(KeyEvent e) {
                if (currentHistoryLine < commandHistory.size() - 1)
                    setCurrentCommand(
                            commandHistory.get(++currentHistoryLine) );

                e.doit = false;
            }
        });

        put( new Integer(SWT.ARROW_LEFT), new KeyAction() {
            public void receiveKey(KeyEvent e) {
                text.setSelection(text.getCaretPosition() - 1);
                e.doit = cursorIsOnCommandLine();
                text.setSelection(text.getCaretPosition() + 1);
            }
        });

        put( new Integer(SWT.ARROW_RIGHT), new KeyAction() {
            public void receiveKey(KeyEvent e) {
                e.doit = cursorIsOnCommandLine();
            }
        });

        put( new Integer(SWT.HOME), new KeyAction() {
            public void receiveKey(KeyEvent e) {
                e.doit = false;
                text.setSelection(startOfCommandLine());
            }
        });

        put( new Integer(SWT.BS), new KeyAction() {
            public void receiveKey(KeyEvent e) {
                Point oldSelection = text.getSelection();

                // So, oldSelection.x corresponds to the start of the old
                // selection, and oldSelection.y to its end. For some reason
                // someone thought it was a good idea to use Point, instances of
                // which "represent places on the (x, y) coordinate plane", for
                // representing the start and end, respectively, of a text
                // selection. There should be a lesson for posterity in here
                // somewhere...
                
                text.setSelection(oldSelection.x - 1);
                if ( !cursorIsOnCommandLine() ) {
                    text.setSelection( oldSelection.y );
                    
                    if (cursorIsOnCommandLine()) {
                        int newStart
                            = text.getText().lastIndexOf("\n", oldSelection.y)
                               + 1 + commandLinePrompt().length();
                        
                        if (newStart == oldSelection.y) {
                            e.doit = false;
                            return;
                        }
                        
                        text.setSelection(newStart, oldSelection.y);
                        return; // and thus delete only the command
                    }
                    e.doit = false;
                }

                text.setSelection(oldSelection);
            }
        });
        
        put(new Integer(SWT.TAB), new KeyAction() {
            public void receiveKey(KeyEvent e) {
                tabComplete();
                e.doit = false;
            }
        });
    }};

    /**
     * The constructor. Called by Eclipse reflection when a new console
     * is created.
     */
    public ScriptingConsoleView() {

    }

    /** Returns the number of occurrences of a character in a string. */
    private int occurrences(String command, char c) {
        
        int pos = -1, occurrences = 0;
        
        while ((pos = command.indexOf(c, pos+1)) != -1)
            occurrences++;
        
        return occurrences;
    }

    /** Receives a KeyEvent e and takes appropriate action. */
    private void handleKey(KeyEvent e) {

        if (actionTable.containsKey( e.keyCode )) {
            actionTable.get( e.keyCode ).receiveKey( e );
        }
        else if (isInsertedChar(e) && !cursorIsOnCommandLine()) {
            putCursorOnCommandLine();
        }
    }

    /**
     * This is a callback that will allow us to create the viewer and
     * initialize it.
     */
    public void createPartControl(Composite parent) {
        text = new Text(parent, SWT.MULTI | SWT.V_SCROLL);
        text.setFont(JFaceResources.getTextFont());
        text.append( commandLinePrompt() );
                
        text.addKeyListener( new KeyListener() {
            public void keyPressed(KeyEvent e) { handleKey(e); }
            public void keyReleased(KeyEvent e) { }
        });

        hookContextMenu();
        enableResourceDropSupport();
    }

    
    /** Makes a system notification sound. */
    protected void beep() {
        Display.getCurrent().beep();
    }

    /**
     * An inserted character is one which, when the corresponding key is
     * pressed, is inserted into the text (as opposed to arrow keys and
     * other actions).
     * 
     * @return <code>true</code> if the event represents the insertion of a
     *         character
     */
    private static boolean isInsertedChar(KeyEvent e) {
        return e.keyCode >= 32 && e.keyCode < 128
            && (e.stateMask == 0 || e.stateMask == SWT.SHIFT);
    }
    
    /**
     * Finds out if the last line qualifies as a command line. Technically, a
     * command line is one which starts with a prompt, either the command line
     * prompt or the continuation prompt. This method is called to uphold the
     * precondition that the last line be a command line before any command is
     * executed, because the command parsing code depends on it being so. 
     * 
     * @return <code>true</code> if the cursor is located on the active command
     *         line
     */
    private boolean lastLineIsCommandLine() {
        
        String allText = text.getText();
        
        int endOfLastLine = text.getCharCount(),
            startOfLastLine = allText.lastIndexOf("\n", endOfLastLine - 1) + 1;
        
        String lastLine = allText.substring( startOfLastLine, endOfLastLine );
        
        return lastLine.startsWith(commandLinePrompt())
               || lastLine.startsWith(continuationLinePrompt());
    }
    
    /**
     * Returns whatever is written on the command line. Combines possible
     * content after continuation prompts into one single command.
     * 
     *  @return the current command written on the active command line
     */
    protected String currentCommand() {
        
        return currentCommand( text.getCharCount() );
    }
    
    /** Helper method for the parameterless <code>currentCommand()</code>. */
    private String currentCommand(int endOfLine) {

        String allText = text.getText();
        
        int startOfLine = allText.lastIndexOf("\n", endOfLine - 1) + 1;
        
        String wholeLine = allText.substring( startOfLine, endOfLine ),
                 command = wholeLine.substring( commandLinePrompt().length() ),
                  prompt = wholeLine.substring(0, commandLinePrompt().length());
        
        if (prompt.equals(commandLinePrompt()))
            return command;
        else
            return currentCommand(startOfLine - 1) + "\n" + command;
    }
    
    /**
     * Returns the string index marking the start of the active command line,
     * excluding the prompt.
     * 
     * @return the string index
     */
    private int startOfCommandLine() {
        String allText = text.getText();
        
        int endOfLine = text.getCharCount(),
        startOfLine = allText.lastIndexOf("\n", endOfLine - 1) + 1;
        
        return startOfLine + commandLinePrompt().length();
    }

    /**
     * Sets the text on the command line.
     * 
     * @param newCommand the replacement text
     */
    private void setCurrentCommand(String newCommand) {
        
        String allText = text.getText();
        
        int endOfLine = text.getCharCount();
        
        String textWithReplacedCommand
            = allText.substring( 0, startOfCommandLine() )
              + newCommand
              + allText.substring( endOfLine ); 
        
        text.setText( textWithReplacedCommand );
        text.setSelection( textWithReplacedCommand.length() );
    }
    
    /**
     * Returns the cursor position within the command line string.
     * 
     * @return the position relative the start of the command line
     */
    protected int positionOnCommandLine() {
        return text.getCaretPosition() - startOfCommandLine();
    }
    
    /**
     * Returns <code>true</code> if the cursor is in the writing area of the
     * last line, false otherwise.
     */
    private boolean cursorIsOnCommandLine() {
        String allText = text.getText();
        int pos = text.getCaretPosition();
        
        return !allText.substring(pos).contains("\n")
               && pos >= allText.lastIndexOf("\n", pos)
                         + 1 + commandLinePrompt().length();
    }
    
    /** Sets up and installs the context menu for the console view. */
    private void hookContextMenu() {
        
        final Action cutAction = new Action("Cut") {
            public void run() {
                text.cut();
            }
        },
        
        copyAction = new Action("Copy") {
            public void run() {
                text.copy();
            }
        },

        pasteAction = new Action("Paste") {
            public void run() {
                text.paste();
            }
        };

        cutAction.setActionDefinitionId(IWorkbenchActionDefinitionIds.CUT);
        copyAction.setActionDefinitionId(IWorkbenchActionDefinitionIds.COPY);
        pasteAction.setActionDefinitionId(IWorkbenchActionDefinitionIds.PASTE);

        final MenuManager menuMgr = new MenuManager("#PopupMenu");
        menuMgr.setRemoveAllWhenShown(true);
        menuMgr.addMenuListener(new IMenuListener() {
            public void menuAboutToShow(IMenuManager mgr) {
                menuMgr.add( cutAction );
                menuMgr.add( copyAction );
                menuMgr.add( pasteAction );
            }
        });
        
        text.setMenu( menuMgr.createContextMenu( text ) );
//        getSite().registerContextMenu(menuMgr, text);
    }
    
    private void enableResourceDropSupport() {
        int ops = DND.DROP_MOVE;
        DropTarget target = new DropTarget(text, ops);
        target.setTransfer(
                new Transfer[] { LocalSelectionTransfer.getTransfer()} );
        
        target.addDropListener( new DropTargetListener() {
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
                      String content = "";
                      if (item instanceof IFile) {
                          IFile file = (IFile)item;

                          content = "\"" + file.getLocation().toString() + "\"";
                      }
                      if (item instanceof IFolder) {
                          IFolder folder = (IFolder)item;

                          content = "\""
                                    + folder.getLocation().toString()
                                    + "\"";
                      }
                      else if (item instanceof IProject) {
                          IProject project = (IProject)item;

                          content = "\""
                                    + project.getLocation().toString()
                                    + "\"";
                      }

                      if ( !cursorIsOnCommandLine() )
                          putCursorOnCommandLine();
                      
                      addAtCursor(content);
                      setFocus();
                  }
                  
              }
            }
          });
    }

    /**
     * Makes the visible part of the text widget show the cursor.
     */
    private void scrollDownToPrompt() {
        text.redraw();
        text.showSelection();
    }

    /**
     * Finds a good place to break a long line into several lines. Prefers not
     * breaking (if the line is shorter than the maximal allowed line length),
     * or breaking at a space (if there is one). Otherwise, breaks at the last
     * possible point.
     * 
     * Note that with the current implementation, tab characters will freak
     * this method out in ugly but non-dangerous ways. 
     * 
     * @param text the text to be broken up
     * @param currentPos the starting point of the breaking up. It's cheaper to
     *                   pass around indexes like this than to split strings
     *                   repeatedly
     * @param maxLineLength the length of the largest possible line
     * 
     * @return the index where the breaking should be made
     */
    private int convenientLineBreakPoint( String text,
                                          int currentPos,
                                          int maxLineLength ) {

        // Prefer not to break at all...
        if ( currentPos + maxLineLength >= text.length() )
            return text.length();
        
        // ...or to break where there is already a break...
        if ( text.substring(currentPos,
                            currentPos + maxLineLength).contains("\n") )
            return text.indexOf('\n', currentPos) + 1;
        
        // ...or at the last possible space...
        if ( text.substring(currentPos,
                            currentPos + maxLineLength).contains(" ") )
            return text.lastIndexOf(' ', currentPos + maxLineLength);
        
        // ...or just break at the last possible moment, no matter what.
        return currentPos + maxLineLength;
    }

    /**
     * Splits text into several lines, each not longer than the proposed line
     * length. The text returned has <code>\n</code> characters inserted, in
     * such a way that the distance between two consecutive such characters is
     * never longer than proposed line length.
     * 
     * @param text the text to be split up
     * @param maxLineLength the proposed line length
     * @return the same text but with line breaks
     */
    private String splitIntoSeveralLines( String text,
                                          int maxLineLength ) {
        
        if (text == null || text.length() == 0)
            return "";
        
        if (maxLineLength <= 0)
            return text;

        StringBuffer result = new StringBuffer();
        int currentPos = 0;
        while ( currentPos < text.length() ) {
            
            int toPos
                    = convenientLineBreakPoint(text, currentPos, maxLineLength); 

            result.append( text.substring(currentPos, toPos) );

            currentPos = toPos;

            // Line breaks only between lines, and only in the absence of
            // natural ones.
            if (currentPos < text.length() && text.charAt(currentPos-1) != '\n')
                result.append('\n');
            
            // And we can live without the spaces at which we chose to break.
            while (currentPos < text.length() && text.charAt(currentPos) == ' ')
                ++currentPos;
        }
        
        return result.toString();
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
        
        if (message.length() > MAX_OUTPUT_LINE_LENGTH)
            message = splitIntoSeveralLines(message, MAX_OUTPUT_LINE_LENGTH);
        
        synchronized (text) {
                
            boolean onCommandLine = cursorIsOnCommandLine();
            
            String allText = text.getText();
            int oldPos = text.getCaretPosition(),
                posBeforePrompt = allText.lastIndexOf("\n") + 1;
    
            if ( !outputIsMidLine )
                message = "\n" + message;
            
            if (posBeforePrompt < 1) {
                posBeforePrompt = 1;
                message += "\n";
            }
            
            String newText = allText.substring(0, posBeforePrompt - 1)
                           + message
                           + allText.substring(posBeforePrompt - 1);
    
            text.setText(newText);
            
            if (onCommandLine)
                text.setSelection(oldPos + message.length());
            else
                text.setSelection(oldPos);
            
            scrollDownToPrompt();
            text.redraw();

            outputIsMidLine = !message.endsWith("\n") || message.equals("\n");
        }
    }
    
    /**
     * Empties the console of contents. Does not add back a prompt, since this
     * is commonly done later in the REPL loop.
     */
    public void clearConsole() {
        synchronized (text) {
            text.setText( "" );
        }
    }
    
    /**
     * Passing the focus request to the viewer's control.
     */
    public void setFocus() {
        text.setFocus();
    }
    
    /** Returns whether a string ends with a binary operator. */
    protected boolean endsWithBinaryOperator(String command) {

        for (char c : new char[] { '+', '-', '*', '/', '%',
                                   '=', '<', '>', '&', '|',
                                   ',' } )
            if (command.endsWith("" + c))
                return true;
        
        return false;
    }

    /** Puts the cursor on the command line, after any already written text. */
    protected void putCursorOnCommandLine() {

        int pos = text.getCharCount();
        text.setSelection(pos);
    }
    
    /**
     * Returns all variable names contained in a certain container object.
     * @param object The container object of interest
     * @return A list of all the variable names in the container object
     */
    @SuppressWarnings("unchecked")
    protected List<String> getAllVariablesIn(String object) {
        return Collections.EMPTY_LIST;
    }
    
    /* Returns the longest common prefix of all strings in a list. */
    private String commonPrefix(List<String> strings) {
        if (strings.size() == 0)
            return "";
        
        String commonPrefix = strings.get(0);
        for (String s : strings)
            while ( !s.startsWith(commonPrefix) )
                commonPrefix
                    = commonPrefix.substring(0, commonPrefix.length() - 1);
        
        return commonPrefix;
    }
    
    /**
     * Automatically writes to the command line the rest of a variable or
     * method name. Beeps if no unique such completion exists. Gives a list
     * of possible completions if called a second time.
     */
    protected void tabComplete() {
        
        String command = currentCommand();
        int pos = positionOnCommandLine() - 1;
        
        String prefix = "";
        for (char additionalCharacter; pos >= 0 && Character.isLetterOrDigit(
                additionalCharacter = command.charAt(pos)); --pos)
            prefix = additionalCharacter + prefix;
        
        String object = "";
        if ( pos > 0 && command.charAt(pos) == '.' ) {
            --pos;
            object = "";
            for (char additionalCharacter; pos >= 0
                 && (Character.isLetterOrDigit(
                         additionalCharacter = command.charAt(pos) )
                     || additionalCharacter == '.' ); --pos)
                
                object = additionalCharacter + object;
        }
        
        List<String> variables = getAllVariablesIn(object);
        
        List<String> interestingVariables = new ArrayList<String>();
        for (String variable : variables)
            if (variable.startsWith( prefix ))
                interestingVariables.add( variable );
        
        String longestPossiblePrefix = commonPrefix(interestingVariables);

        if ( prefix.length() > longestPossiblePrefix.length() ) {
            beep();
        }
        else if ( longestPossiblePrefix.equals(lastPrefix)
             && interestingVariables.size() > 1 ) {
            
            Collections.sort( interestingVariables );
            String varList = interestingVariables.toString();
            varList
                = varList.substring(1, varList.length() - 1).replace(',', ' ');
            printMessage( varList + "\n" );
        }
        else {
            addAtCursor( longestPossiblePrefix.substring( prefix.length() ));
            
            if (interestingVariables.size() != 1)
                beep();
        }
        
        lastPrefix = longestPossiblePrefix;
    }
    
    /**
     * Inserts text at the cursor.
     * 
     * @param newText the text to insert
     */
    protected void addAtCursor(String newText) {
        int oldPosition = text.getSelection().x;
        
        String allText = text.getText();
        
        String textWithNewTextAdded
            = allText.substring( 0, text.getCaretPosition() )
              + newText
              + allText.substring( text.getCaretPosition() );
        
        text.setText( textWithNewTextAdded );
        text.setSelection( oldPosition + newText.length() );
    }

    /**
     * Returns the prompt with which to prefix every command line.
     * 
     * @return the prompt
     */
    protected abstract String commandLinePrompt();

    /**
     * Returns the prompt with which to prefix every continued command line. Must
     * be of the same length as the normal prompt. The provided implementation
     * returns a prompt on the form <code>"...>"</code>, but trimmed to the
     * length of the normal prompt.
     * 
     * @return the continuation line prompt
     */
    protected String continuationLinePrompt() {
        String normalPrompt = commandLinePrompt();
        
        String continuationPrompt = "...> ";
        
        if (continuationPrompt.length() > normalPrompt.length())
            continuationPrompt = continuationPrompt.substring(
                    continuationPrompt.length() - normalPrompt.length() );
        
        while (continuationPrompt.length() < normalPrompt.length())
            continuationPrompt = " " + continuationPrompt;
        
        return continuationPrompt;
    }

    /**
     * Executes a command in the underlying scripting engine.
     * 
     * @param command the command to be executed
     * @return the return value/error message (if any) from the command
     */
    protected abstract String executeCommand(String command);
}