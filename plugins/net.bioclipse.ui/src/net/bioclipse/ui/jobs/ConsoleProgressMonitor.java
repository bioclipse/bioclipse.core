package net.bioclipse.ui.jobs;

import net.bioclipse.ui.Activator;
import net.bioclipse.ui.ConsoleEchoer;

import org.eclipse.core.runtime.IProgressMonitor;


public class ConsoleProgressMonitor implements IProgressMonitor {

    private static final int WIDTH = 50;
    private int totalWork;
    private int current;
    private int painted = 1;
    private IProgressMonitor parent; 
    private static final ConsoleEchoer CONSOLE 
        = Activator.getDefault().CONSOLE; 
    
    public ConsoleProgressMonitor(IProgressMonitor monitor) {
        parent = monitor;
    }
    
    public void beginTask( String name, int totalWork ) {
        parent.beginTask( name, totalWork );
        this.totalWork = totalWork;
        CONSOLE.echo( "|1%" + spaces(WIDTH - 8) + "100%|\n|" );
    }
    
    private String spaces( int i ) {
        
        StringBuilder s = new StringBuilder();
        for ( int j = 0; j < i; j++ ) {
            s.append( " " );
        }
        return s.toString();
    }
    
    public void done() {
        parent.done();
        current = totalWork;
        CONSOLE.echo( "\n" );
        updateText();
    }
    
    public void internalWorked( double work ) {
        parent.internalWorked( work );
    }
    
    public boolean isCanceled() {
        return parent.isCanceled();
    }
    
    public void setCanceled( boolean value ) {
        parent.setCanceled( value );
    }
    
    public void setTaskName( String name ) {
        parent.setTaskName( name );
    }
    
    public void subTask( String name ) {
        parent.subTask( name );
    }
    
    public void worked( int work ) {
        parent.worked( work );
        this.current += work;
        updateText();
    }
    
    private void updateText() {
        double done = current / (totalWork * 1.0) ;
        if( done*WIDTH > painted ) {
            double numOfChars = done*WIDTH-painted;
            for( int i = 0 ; i < numOfChars ; i++) {
                CONSOLE.echo( "|" );
                painted++;
            }
        }
    }
}
