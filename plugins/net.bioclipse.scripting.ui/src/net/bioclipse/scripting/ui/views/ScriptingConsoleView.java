package net.bioclipse.scripting.ui.views;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
                String command = input.getText().trim();
                printMessage(NEWLINE + "> " + command + NEWLINE);
                if ( !"".equals(command) ) {
                    commandHistory.remove( commandHistory.size() - 1 );
                    commandHistory.add( command );
                    commandHistory.add( "" );
                    currentHistoryLine = commandHistory.size() - 1;
                }
                executeCommand(command);
                input.setText("");
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
        
        output = new Text(parent, SWT.READ_ONLY | SWT.MULTI | SWT.V_SCROLL | SWT.BORDER);
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
            }
            public void keyReleased(KeyEvent _) { }
        });
        
        input = new Text(parent, SWT.SINGLE | SWT.BORDER);
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
            int nLines     = message.split(NEWLINE, -1).length,
                curLineNum = 0;
            for (String line : message.split(NEWLINE, -1)) {
                if (line.length() > MAX_OUTPUT_LINE_LENGTH) {
                    for (String splitLine
                         : splitIntoSeveralLines(line,
                                                 MAX_OUTPUT_LINE_LENGTH))
                        output.append(splitLine);
                }
                else {
                    output.append(line);
                }
                // Output newlines on all lines but the last (or only) one.
                if (curLineNum++ < nLines - 1)
                    output.append(NEWLINE);
            }
            output.redraw();
        }
    }

    /** Makes a system notification sound. */
    protected void beep() {
        Display.getCurrent().beep();
    }

    /**
     * Splits text into several lines, each not longer than the proposed line
     * length. The text returned has newline characters inserted, in
     * such a way that the distance between two consecutive such characters is
     * never longer than proposed line length.
     * 
     * @param text the text to be split up
     * @param maxLineLength the proposed line length
     * @return the same text but with line breaks
     */
    protected String[] splitIntoSeveralLines( String text,
                                              int maxLineLength ) {
        
        if (text == null || text.length() == 0)
            return new String[] { "" };
        
        if (maxLineLength <= 0 || text.length() <= maxLineLength )
            return new String[] { text };

        List<String> result = new ArrayList<String>();
        int currentPos = 0;
        while ( currentPos < text.length() ) {
            
            int toPos
                    = convenientLineBreakPoint(text, currentPos, maxLineLength); 

            String line = text.substring(currentPos, toPos);

            currentPos = toPos;

            // Line breaks only between lines, and only in the absence of
            // natural ones.
            if (currentPos < text.length()
                && !text.substring(currentPos-NEWLINE.length(),
                                   currentPos).equals(NEWLINE))
                line += NEWLINE;

            result.add( line );

            // The following condition is necessary to prevent infinite looping
            // when breaking on spaces. We skip over the space that just
            // contributed in the line breaking. On the other hand, often
            // it's important to keep spaces used for indentation in the
            // beginnings of lines, so we don't skip any space if they come
            // directly after a NEWLINE.
            if (currentPos < text.length() - 1
                && !NEWLINE.equals(text.substring(currentPos-NEWLINE.length(),
                                                  currentPos))
                && " ".equals(text.substring(currentPos,
                                             currentPos + 1)))
                currentPos++;
        }
        
        return result.toArray(new String[0]);
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
                            currentPos + maxLineLength).contains(NEWLINE) )
            return text.indexOf(NEWLINE, currentPos) + NEWLINE.length();
        
        // ...or at the last possible space...
        if ( text.substring(currentPos,
                            currentPos + maxLineLength).contains(" ") )
            return text.lastIndexOf(' ', currentPos + maxLineLength);
        
        // ...or just break at the last possible moment, no matter what.
        return currentPos + maxLineLength;
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
            while ( !s.toLowerCase().startsWith(commonPrefix.toLowerCase()) )
                commonPrefix
                    = commonPrefix.substring(0, commonPrefix.length() - 1);
        return commonPrefix;
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
     * override <code>getAllVariablesIn</code>, which returns the relevant
     * things to tab-complete on.
     */
    protected void tabComplete() {
        String command = input.getText();
        int pos = input.getCaretPosition() - 1;
        String prefix = "";
        for (char additionalCharacter; pos >= 0 && Character.isLetterOrDigit(
                additionalCharacter = command.charAt(pos)); --pos)
            prefix = additionalCharacter + prefix;
        deleteBackwards(input.getCaretPosition() - (pos+1));
        String object = "";
        if ( pos > 0 && command.charAt(pos) == '.' ) {
            --pos;
            if ( Character.isLetterOrDigit( command.charAt(pos) ) ) {
                for (char additionalCharacter; pos >= 0
                     && (Character.isLetterOrDigit(
                             additionalCharacter = command.charAt(pos) )
                         || additionalCharacter == '.' ); --pos)
                    object = additionalCharacter + object;
            }
            else {
                object = null;
            }
        }
        List<String> variables = object == null
                                   ? new ArrayList<String>()
                                   : getAllVariablesIn(object);
        List<String> interestingVariables = new ArrayList<String>();
        for (String variable : variables)
            if (variable.toLowerCase().startsWith(prefix.toLowerCase()))
                interestingVariables.add( variable );
        String longestCommonPrefix = commonPrefix(interestingVariables);
        if ( prefix.length() > longestCommonPrefix.length() ) {
            addAtCursor(prefix);
            beep();
        }
        else if ( longestCommonPrefix.equals(lastPrefix)
             && interestingVariables.size() > 1 ) {
            Collections.sort( interestingVariables );
            String varList = interestingVariables.toString();
            varList
                = varList.substring(1, varList.length() - 1).replace(',', ' ');
            printMessage( varList + NEWLINE );
            addAtCursor(prefix);
        }
        else {
            addAtCursor( longestCommonPrefix );
            if ( interestingVariables.size() == 1 )
                addAtCursor( tabCompletionHook(object,
                                               interestingVariables.get(0)) );
            if (interestingVariables.size() != 1)
                beep();
        }
        lastPrefix = longestCommonPrefix;
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
     * A convenience method to simulate that a user enters a command in the 
     * Input Textbox. This is used e.g. in ScriptAction to simulate that users 
     * enter command from e.g. a Cheat Sheet.
     * 
     * FIXME: for masak, see bug 940
     * This method is copied from ActionTable and should probably be refactored.
     * 
     * @param content String with simulated command
     */
    public void simulateInputWithReturn(String content){
        String command = content.trim();
        printMessage(NEWLINE + "> " + command + NEWLINE);
        if ( !"".equals(command) ) {
            commandHistory.remove( commandHistory.size() - 1 );
            commandHistory.add( command );
            commandHistory.add( "" );
            currentHistoryLine = commandHistory.size() - 1;
        }
        executeCommand(command);
        input.setText("");

    }
}
