package net.bioclipse.managers.business;

import net.bioclipse.core.api.managers.IJavaManagerDispatcherAdvisor;

import org.springframework.aop.support.RegexpMethodPointcutAdvisor;


/**
 * @author jonalv
 *
 */
public class JavaManagerDispatcherAdvisor 
       extends RegexpMethodPointcutAdvisor
       implements IJavaManagerDispatcherAdvisor {

    private static final long serialVersionUID = 1L;

}
