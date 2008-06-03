package net.bioclipse.ui.editors;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.RGB;
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
    
    static Display display = Display.getCurrent();
    static ColorManager colorManager = new ColorManager();
    
    static private Color
        normalAAColor   = display.getSystemColor( SWT.COLOR_WHITE ),
        polarAAColor    = colorManager.getColor(new RGB(0xD0, 0xFF, 0xD0)),
        nonpolarAAColor = colorManager.getColor(new RGB(0xFF, 0xFF, 0xD0)),
        acidicAAColor   = colorManager.getColor(new RGB(0xFF, 0xD0, 0xA0)),
        basicAAColor    = colorManager.getColor(new RGB(0xD0, 0xFF, 0xFF)),
        smallAAColor    = colorManager.getColor(new RGB(0xFF, 0xD0, 0xD0)),
        cysteineColor   = colorManager.getColor(new RGB(0xFF, 0xFF, 0xD0)),
        textColor       = display.getSystemColor( SWT.COLOR_BLACK ),
        nameColor       = display.getSystemColor( SWT.COLOR_WHITE ),
        buttonColor     = colorManager.getColor(new RGB(0x66, 0x66, 0x66));

    private List<String> sequences, sequenceNames;
    
    private int canvasWidthInSquares, canvasHeightInSquares;
    
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
        
        sequences     = new ArrayList<String>();
        sequenceNames = new ArrayList<String>();
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
            while (sc.hasNextLine()) {
                String line = sc.nextLine();
                if (line.startsWith( ">" )) {
                    line = line.split( "\\s+" )[0];
                    sequenceNames.add( line.substring( 1 ) );
                    if (sb.length() > 0)
                        sequences.add( sb.toString() );
                    sb = new StringBuilder();
                    continue;
                }
                // otherwise it's a sequence
                if (line.charAt( line.length()-1 ) == '\n')
                    line = line.substring( 0, line.length()-1 );
                sb.append(line);
            }
            if (sb.length() > 0)
                sequences.add( sb.toString() );            
        }
        
        canvasHeightInSquares = sequences.size();
        canvasWidthInSquares = 0;
        for ( String sequence : sequences )
            if ( canvasWidthInSquares < sequence.length() )
                canvasWidthInSquares = sequence.length();
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

                int yCoord = 0;
                for ( String name : sequenceNames ) {
                    gc.fillRectangle(0, yCoord, 8 * squareSize, squareSize);
                    gc.drawText( name, 5, yCoord + 2 );
                    yCoord += squareSize;
                }
            }
        });
        
        ScrolledComposite sc
            = new ScrolledComposite( parent, SWT.H_SCROLL | SWT.V_SCROLL );
        GridData sc_data = new GridData(GridData.VERTICAL_ALIGN_BEGINNING
                                        | GridData.FILL_BOTH);
        sc.setLayoutData( sc_data );
        
        Composite c = new Composite(sc, SWT.NONE);
        c.setLayout(new FillLayout());
        
        Canvas canvas = new Canvas( c, SWT.NONE );
        canvas.setLocation( 0, 0 );
        c.setSize( canvasWidthInSquares * squareSize,
                   canvasHeightInSquares * squareSize );
        sc.setContent( c );
        
        canvas.addPaintListener( new PaintListener() {
            public void paintControl(PaintEvent e) {
                GC gc = e.gc;
                gc.setTextAntialias( SWT.ON );
                gc.setFont( new Font(gc.getDevice(), "Arial", 14, SWT.NONE) );
                gc.setForeground( textColor );

                int yCoord = 0;
                for ( String sequence : sequences ) {
                    char[] fasta = sequence.toCharArray();
                    
                    int xCoord = 0;
                    for ( char c : fasta ) {
                        
                        String cc = c + "";
                        gc.setBackground(
                            "HKR".contains(  cc ) ? basicAAColor
                          : "DE".contains(   cc ) ? acidicAAColor
                          : "TQSN".contains( cc ) ? polarAAColor
                          : "FYW".contains(  cc ) ? nonpolarAAColor
                          : "GP".contains(   cc ) ? smallAAColor
                          : c == 'C'              ? cysteineColor
                                                  : normalAAColor );
                        
                        gc.fillRectangle(xCoord, yCoord, squareSize, squareSize);
                        gc.drawText( "" + c, xCoord + 4, yCoord + 2 );
                        xCoord += squareSize;
                    }
                    yCoord += squareSize;
                }
            }
        });
    }

    @Override
    public void setFocus() {
    }

}
