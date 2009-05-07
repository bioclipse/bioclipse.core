package net.bioclipse.scripting.ui.business;

import org.aopalliance.intercept.MethodInvocation;
import org.eclipse.swt.widgets.Display;

/**
 * Advice that runs the method called in an asynch block if that method 
 * is annotated with the <code>GuiAction</code> annotation
 * 
 * jonalv
 *
 */
public class GuiActionAdvice implements IGuiActionAdvice {

    private IJsConsoleManager jsConsoleManager;
    
    public void setJsConsoleManager(IJsConsoleManager jsConsoleManager) {
        this.jsConsoleManager = jsConsoleManager;
    }
    
    public Object invoke( final MethodInvocation invocation ) 
                  throws Throwable {

        if ( !invocation.getMethod().isAnnotationPresent(GuiAction.class) ) {
            return invocation.proceed();
        }
        
        Display.getDefault().asyncExec( new Runnable() {
            public void run() {
                try {
                    invocation.proceed();
                }
                catch (Throwable t) {
                    jsConsoleManager.printError(t);
                }
            }
        } );
        return null;
    }
}
