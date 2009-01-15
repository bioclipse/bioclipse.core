package net.bioclipse.scripting.ui.views;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.bioclipse.scripting.ui.views.ScriptingConsoleView.KeyAction;

import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.part.ViewPart;


public abstract class NewScriptingConsoleView extends ViewPart {
    
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
                    commandHistory.set( commandHistory.size()-1,
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
    }};

    /**
     * The constructor. Called by Eclipse reflection when a new console
     * is created.
     */
    public NewScriptingConsoleView() {
    }

    /**
     * This is a callback that will allow us to create the view and
     * initialize it.
     */
    public void createPartControl(Composite parent) {
        GridLayout layout = new GridLayout();
        layout.numColumns = 1;
//        data.grabExcessHorizontalSpace = true;
        parent.setLayout(layout);
        
        output = new Text(parent, SWT.READ_ONLY | SWT.MULTI | SWT.V_SCROLL);
        output.setFont(JFaceResources.getTextFont());
        GridData outputData = new GridData(GridData.FILL_BOTH);
        output.setBackground(new Color(parent.getDisplay(), 0xFF, 0xFF, 0xFF));
        output.setLayoutData(outputData);
        
        input = new Text(parent, SWT.SINGLE);
        input.setFont(JFaceResources.getTextFont());
        input.addKeyListener( new KeyListener() {
            public void keyPressed(KeyEvent e) { handleKey(e); }
            public void keyReleased(KeyEvent e) { }
        });
        GridData inputData = new GridData(GridData.FILL_HORIZONTAL);
        inputData.heightHint = 20;
        input.setLayoutData(inputData);
    }
    
    protected void handleKey(KeyEvent e) {
        if (actionTable.containsKey( e.keyCode )) {
            e.doit = false;
            actionTable.get( e.keyCode ).receiveKey( e );
        }
    }

    /**
     * Empties the console of contents.
     */
    public void clearConsole() {
        synchronized (output) {
            output.setText( "" );
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
        
        if (message.length() > MAX_OUTPUT_LINE_LENGTH)
            message = splitIntoSeveralLines(message, MAX_OUTPUT_LINE_LENGTH);
        
        synchronized (output) {
            output.append(message);
            output.redraw();
        }
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
            if (currentPos < text.length()
                && !text.substring(currentPos-NEWLINE.length(),
                                   currentPos).equals(NEWLINE))
                result.append(NEWLINE);
            
            // And we can live without the spaces at which we chose to break.
            while (currentPos < text.length() && text.charAt(currentPos) == ' ')
                ++currentPos;
        }
        
        return result.toString();
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
            return text.indexOf(NEWLINE, currentPos) + 1;
        
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
}
