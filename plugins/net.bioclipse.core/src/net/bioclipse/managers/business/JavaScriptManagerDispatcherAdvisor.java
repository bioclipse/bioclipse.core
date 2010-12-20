package net.bioclipse.managers.business;

import net.bioclipse.core.api.managers.IJavaScriptManagerDispatcherAdvisor;

import org.springframework.aop.support.RegexpMethodPointcutAdvisor;


/**
 * @author jonalv
 *
 */
public class JavaScriptManagerDispatcherAdvisor 
       extends RegexpMethodPointcutAdvisor 
       implements IJavaScriptManagerDispatcherAdvisor {

    private static final long serialVersionUID = 1L;

}
