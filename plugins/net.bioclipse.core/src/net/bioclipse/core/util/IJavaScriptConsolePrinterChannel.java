package net.bioclipse.core.util;


public interface IJavaScriptConsolePrinterChannel {

    public void print(String message);
    public void printError( Throwable t );
}
