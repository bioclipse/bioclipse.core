package net.bioclipse.scripting.ui.views;

import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
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
        output = new Text(parent, SWT.READ_ONLY | SWT.MULTI | SWT.V_SCROLL);
        output.setFont(JFaceResources.getTextFont());
        input = new Text(parent, SWT.SINGLE);
        input.setFont(JFaceResources.getTextFont());
        input.addKeyListener( new KeyListener() {
            public void keyPressed(KeyEvent e) {
                if (e.keyCode == SWT.CR) {
                    String command = input.getText().trim();
                    executeCommand(command);
                    input.setText("");
                }
            }
            public void keyReleased(KeyEvent e) { }
        });
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
        // TODO: Focus on the lower text box
    }
    
    /**
     * Executes a command in the underlying scripting engine.
     * 
     * @param command the command to be executed
     * @return the return value/error message (if any) from the command
     */
    protected abstract String executeCommand(String command);
}
