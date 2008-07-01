package net.bioclipse.ui.editors;

import java.io.InputStream;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Scanner;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.MouseMoveListener;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.EditorPart;
import org.eclipse.ui.part.FileEditorInput;


public class Aligner extends EditorPart {

    private int squareSize = 20;

    static final Display display = Display.getCurrent();
    static final ColorManager colorManager = new ColorManager();
    
    static private final Color
        normalAAColor   = display.getSystemColor( SWT.COLOR_WHITE ),
        polarAAColor    = colorManager.getColor( new RGB(0xD0, 0xFF, 0xD0) ),
        nonpolarAAColor = colorManager.getColor( new RGB(0xFF, 0xFF, 0xD0) ),
        acidicAAColor   = colorManager.getColor( new RGB(0xFF, 0xD0, 0xA0) ),
        basicAAColor    = colorManager.getColor( new RGB(0xD0, 0xFF, 0xFF) ),
        smallAAColor    = colorManager.getColor( new RGB(0xFF, 0xD0, 0xD0) ),
        cysteineColor   = colorManager.getColor( new RGB(0xFF, 0xFF, 0xD0) ),
        textColor       = display.getSystemColor( SWT.COLOR_BLACK ),
        nameColor       = display.getSystemColor( SWT.COLOR_WHITE ),
        buttonColor     = colorManager.getColor( new RGB(0x66, 0x66, 0x66) ),
        consensusColor  = colorManager.getColor( new RGB(0xAA, 0xAA, 0xAA) ),
        selectionColor1 = display.getSystemColor( SWT.COLOR_BLACK ),
        selectionColor2 = display.getSystemColor( SWT.COLOR_WHITE ),
        olBorderColor   = display.getSystemColor( SWT.COLOR_BLACK );
    
    static private final Color[] consensusColors
        = new Color[] { colorManager.getColor( new RGB(0xFF, 0xFF, 0xDD) ), // 1
                        colorManager.getColor( new RGB(0xEE, 0xEE, 0xCC) ), // 2
                        colorManager.getColor( new RGB(0xDD, 0xDD, 0xBB) ), // 3
                        colorManager.getColor( new RGB(0xCC, 0xCC, 0xAA) ), // 4
                        colorManager.getColor( new RGB(0xBB, 0xBB, 0x99) ), // 5
                        colorManager.getColor( new RGB(0xAA, 0xAA, 0x88) ), // 6
                        colorManager.getColor( new RGB(0x99, 0x99, 0x77) ), // 7
                        colorManager.getColor( new RGB(0x88, 0x88, 0x66) ), // 8
                        colorManager.getColor( new RGB(0x77, 0x77, 0x55) )  // 9
                      };

    private Map<String, String> sequences; // sequence_name => sequence
    
    private int canvasWidthInSquares, canvasHeightInSquares;

    private int consensusRow;

    private Point selectionStart = new Point(0, 0),
                  selectionEnd = new Point(0, 0);
    private boolean currentlySelecting = false;
    
    private Outline outline;
    
    @Override
    public void doSave( IProgressMonitor monitor ) {
    }

    @Override
    public void doSaveAs() {
    }

    @Override
    public void init( IEditorSite site, IEditorInput input )
        throws PartInitException {
        
        if (!(input instanceof IFileEditorInput))
            throw new PartInitException(
                "Invalid Input: Must be IFileEditorInput");
        
        setSite(site);
        setInput(input);
    }
    
    @Override
    public void setInput( IEditorInput input ) {
        super.setInput(input);
        
        sequences     = new LinkedHashMap<String, String>();
        if (input instanceof FileEditorInput) {
            FileEditorInput fei = (FileEditorInput)input;
            if (!fei.exists())
                return;
            
            InputStream is;
            try {
                is = fei.getFile().getContents();
            } catch ( CoreException e ) {
                e.printStackTrace();
                return;
            }
            
            Scanner sc = new Scanner(is);
            StringBuilder sb = new StringBuilder();
            String oldName = null;
            
            while (sc.hasNextLine()) {
                String line = sc.nextLine();
                if (line.startsWith( ">" )) {

                    if ( oldName != null )
                        sequences.put( oldName, sb.toString() );

                    line = line.split( "\\s+" )[0].replaceFirst( ".*\\|", "" );
                    oldName = line.substring( 1 );

                    sb = new StringBuilder();
                }
                else { // it's (part of) a sequence
                    
                    line.replaceAll( "\\n$", "" );
                    sb.append(line);
                }
            }
            if (sb.length() > 0)
                sequences.put( oldName, sb.toString() );
            
            // We only show a consensus sequence if there is more than one
            // sequence already.
            consensusRow  = sequences.size();
            if (consensusRow > 1) {
                sequences.put( "Consensus",
                               consensusSequence( sequences.values() )
                             );
            }
        }
        
        canvasHeightInSquares = sequences.size();
        canvasWidthInSquares = maxLength( sequences.values() );
        
        outline = new Outline(sequences, squareSize, consensusRow);
    }

    private static String consensusSequence( final Collection<String>
                                                   sequences ) {

        final StringBuilder consensus = new StringBuilder();
        for ( int i = 0, n = maxLength(sequences); i < n; ++i ) {
            consensus.append( consensusChar(sequences, i) );
        }
        
        return consensus.toString();
    }
    
    private static int maxLength( final Collection<String> strings ) {
        
        int maxLength = 0;
        for ( String s : strings )
            if ( maxLength < s.length() )
                maxLength = s.length();
        
        return maxLength;
    }
    
    private static char consensusChar( final Collection<String> sequences,
                                       final int index ) {
        
        Map<Character, Boolean> chars
            = new HashMap<Character, Boolean>();
        
        for ( String seq : sequences )
            chars.put( seq.length() > index ? seq.charAt(index) : '\0', true );
        
        return chars.size() == 1
               ? chars.keySet().iterator().next()
               : chars.size() < 10
                 ? Character.forDigit( chars.size(), 10 )
                 : '9';
    }

    @Override
    public boolean isDirty() {
        return false;
    }

    @Override
    public boolean isSaveAsAllowed() {
        return true;
    }

    @Override
    public void createPartControl( Composite parent ) {
        GridLayout layout = new GridLayout();
        layout.numColumns = 2;
        layout.horizontalSpacing = 0;
        layout.verticalSpacing = 0;
        parent.setLayout( layout );
        
        Canvas nameCanvas = new Canvas( parent, SWT.NONE );
        GridData data = new GridData(GridData.FILL_VERTICAL);
        data.widthHint = 8 * squareSize;
        nameCanvas.setLayoutData( data );
        
        nameCanvas.addPaintListener( new PaintListener() {
            public void paintControl(PaintEvent e) {
                GC gc = e.gc;
                gc.setForeground( nameColor );
                gc.setBackground( buttonColor );
                gc.setTextAntialias( SWT.ON );
                gc.setFont( new Font(gc.getDevice(), "Arial", 14, SWT.NONE) );

                int index = 0;
                for ( String name : sequences.keySet() ) {
                    
                    if ( index == consensusRow )
                        gc.setBackground( consensusColor );
                    
                    gc.fillRectangle(0, index * squareSize,
                                    8 * squareSize, squareSize);
                    gc.drawText( name, 5, index * squareSize + 2 );
                    ++index;
                }
            }
        });
        
        final ScrolledComposite sc
            = new ScrolledComposite( parent, SWT.H_SCROLL | SWT.V_SCROLL );
        GridData sc_data = new GridData(GridData.VERTICAL_ALIGN_BEGINNING
                                        | GridData.FILL_BOTH);
        sc.setLayoutData( sc_data );
        
        final Composite c = new Composite(sc, SWT.NONE);
        c.setLayout( new FillLayout() );
        
        final Canvas canvas = new Canvas( c, SWT.NONE );
        canvas.setLocation( 0, 0 );
        c.setSize( canvasWidthInSquares * squareSize,
                   canvasHeightInSquares * squareSize );
        sc.setContent( c );
        
        final char fasta[][] = new char[ sequences.size() ][];
        
        int i = 0;
        for ( String sequence : sequences.values() )
            fasta[i++] = sequence.toCharArray();
        
        canvas.addPaintListener( new PaintListener() {
            public void paintControl(PaintEvent e) {
                GC gc = e.gc;
                gc.setTextAntialias( SWT.ON );
                gc.setFont( new Font(gc.getDevice(), "Arial", 14, SWT.NONE) );
                gc.setForeground( textColor );

                int firstVisibleColumn
                        = sc.getHorizontalBar().getSelection() / squareSize,
                    lastVisibleColumn
                        = firstVisibleColumn
                          + sc.getBounds().width / squareSize
                          + 2; // compensate for 2 possible round-downs
                
                drawSequences(fasta, firstVisibleColumn, lastVisibleColumn, gc);
                
                drawSelection( gc );
                
                outline.draw( c.getLocation(), sc.getBounds(),
                              firstVisibleColumn, gc );
            }

            private void drawSequences( final char[][] fasta,
                                        int firstVisibleColumn,
                                        int lastVisibleColumn, GC gc ) {

                for ( int column = firstVisibleColumn;
                      column < lastVisibleColumn; ++column ) {

                    for ( int row = 0; row < canvasHeightInSquares; ++row ) {
                        
                        char c = fasta[row].length > column
                                 ? fasta[row][column] : ' ';
                        String cc = c + "";

                        gc.setBackground(
                             "HKR".contains( cc ) ? basicAAColor
                          :   "DE".contains( cc ) ? acidicAAColor
                          : "TQSN".contains( cc ) ? polarAAColor
                          :  "FYW".contains( cc ) ? nonpolarAAColor
                          :   "GP".contains( cc ) ? smallAAColor
                          :    'C' == c           ? cysteineColor
                                                  : normalAAColor );
                        
                        if ( row == consensusRow ) {
                            
                            int consensusDegree = 1;
                            if ( Character.isDigit(c) )
                                consensusDegree = c - '0';
                            
                            gc.setBackground(
                                consensusColors[ consensusDegree-1 ]
                            );
                        }
                        
                        int xCoord = column * squareSize;
                        int yCoord =    row * squareSize;
                        
                        gc.fillRectangle(xCoord, yCoord, squareSize, squareSize);
                        
                        if ( Character.isUpperCase( c ))
                            gc.drawText( "" + c, xCoord + 4, yCoord + 2 );
                    }
                }
            }
            
            private void drawSelection( GC gc ) {

                int xRight  = Math.min( selectionStart.x, selectionEnd.x ),
                    xLeft   = Math.max( selectionStart.x, selectionEnd.x ),
                    yTop    = Math.min( selectionStart.y, selectionEnd.y ),
                    yBottom = Math.max( selectionStart.y, selectionEnd.y );
                
                // clipping
                xRight  = Math.max( xRight, 0 );
                xLeft   = Math.min( xLeft, canvasWidthInSquares * squareSize );
                yTop    = Math.max( yTop, 0 );
                yBottom = Math.min( yBottom,
                                    (canvasHeightInSquares-1) * squareSize );
                
                // rounding down
                xRight  =                 xRight / squareSize * squareSize;
                yTop    =                   yTop / squareSize * squareSize;
                
                // rounding up
                xLeft   =   (xLeft+squareSize-1) / squareSize * squareSize - 1;
                yBottom = (yBottom+squareSize-1) / squareSize * squareSize - 1;
                
                // Special case: marking along the consensus row
                if ( yTop == yBottom + 1 ) {
                    yTop = 0;
                }
                
                gc.setForeground( selectionColor1 );
                gc.drawRectangle( xRight, yTop,
                                  xLeft - xRight, yBottom - yTop );
                
                gc.setBackground( selectionColor2 );
                gc.setAlpha( 64 ); // 25%
                gc.fillRectangle( xRight + 1, yTop + 1,
                                  xLeft - xRight - 2, yBottom - yTop - 2 );
                gc.setAlpha( 255 ); // restore
            }
        });

        canvas.addMouseListener( new MouseListener() {

            public void mouseDoubleClick( MouseEvent e ) {
                // we're not interested in double clicks
            }

            public void mouseDown( MouseEvent e ) {
                if (e.button == 1) {
                    currentlySelecting = true;
                    selectionStart.x = selectionEnd.x = e.x;
                    selectionStart.y = selectionEnd.y = e.y;
                    canvas.redraw();
                }
            }

            public void mouseUp( MouseEvent e ) {
                currentlySelecting = false;
            }
            
        });
        
        canvas.addMouseMoveListener( new MouseMoveListener() {

            public void mouseMove( MouseEvent e ) {

                // e.stateMask contains info on shift keys
                if (currentlySelecting) {
                  selectionEnd.x = e.x;
                  selectionEnd.y = e.y;

                  int viewPortLeft  = -c.getLocation().x,
                      viewPortRight = viewPortLeft + sc.getBounds().width,
                      viewPortTop   = -c.getLocation().y,
                      maximumLeft   = sc.getHorizontalBar().getMaximum();
                  
                  if ( e.x > viewPortRight ) {
                      viewPortLeft += e.x - viewPortRight;
                      if (viewPortRight >= maximumLeft )
                          viewPortLeft = maximumLeft - sc.getBounds().width;
                  }
                  else if ( e.x < viewPortLeft ) {
                      viewPortLeft -= viewPortLeft - e.x;
                      if (viewPortLeft < 0)
                          viewPortLeft = 0;
                  }
                  
                  if ( viewPortLeft != -c.getLocation().x ) {
                      sc.getHorizontalBar().setSelection( viewPortLeft );
                      c.setLocation( -viewPortLeft, -viewPortTop );
                  }
                  
                  canvas.redraw();
                }
            }
            
        });
    }

    @Override
    public void setFocus() {
    }

    protected static class Outline {
        int innerWidth, innerHeight,
            outerWidth, outerHeight;
        
        int yTop;
        
        static final int BORDER_THICKNESS = 1;
        
        Color[][] colors;
        
        public Outline(Map<String,String> sequences,
                       int squareSize, int consensusRow) {
            
            innerWidth  = maxLength( sequences.values() );
            innerHeight = sequences.size();
            
            outerWidth  = innerWidth  + 2 * BORDER_THICKNESS;
            outerHeight = innerHeight + 2 * BORDER_THICKNESS;
            
            yTop = sequences.size() * squareSize - outerHeight - 1;
            
            colors = new Color[innerHeight][innerWidth];
            int row = 0;
            for ( String sequence : sequences.values() ) {
                for ( int column = 0; column < sequence.length(); ++column ) {
              
                    char c = sequence.charAt( column );
                    String cc = c + "";

                    colors[row][column] = 
                        "HKR".contains( cc ) ? basicAAColor
                    :   "DE".contains( cc ) ? acidicAAColor
                    : "TQSN".contains( cc ) ? polarAAColor
                    :  "FYW".contains( cc ) ? nonpolarAAColor
                    :   "GP".contains( cc ) ? smallAAColor
                    :    'C' == c           ? cysteineColor
                                            : normalAAColor;
              
                    if ( row == consensusRow ) {
                  
                        int consensusDegree = 1;
                        if ( Character.isDigit(c) )
                            consensusDegree = c - '0';
                  
                        colors[row][column]
                          = consensusColors[ consensusDegree-1 ];
                    }
                }
                ++row;
            }

        }

        public void draw( Point viewPortPos,
                          Rectangle viewPortSize,
                          int pos,
                          GC gc ) {
            
            int xRight = -viewPortPos.x + viewPortSize.width - outerWidth - 1;
            
            gc.setForeground( olBorderColor );
            gc.drawRectangle( xRight, yTop,
                              outerWidth, outerHeight );
            
            for (int row = 0; row < innerHeight; ++row) {
                for (int column = 0; column < innerWidth; ++column) {
                    gc.setForeground( colors[row][column] );
                    gc.drawPoint( xRight + 1 + column, yTop + 1 + row );
                }
            }

            gc.setAlpha(64); // 25%
            gc.setBackground( display.getSystemColor( SWT.COLOR_RED ) );
            gc.fillRectangle( xRight + 1 + pos, yTop + 1, 30, innerHeight );
            gc.setAlpha(255); // opaque
        }
    }
}
