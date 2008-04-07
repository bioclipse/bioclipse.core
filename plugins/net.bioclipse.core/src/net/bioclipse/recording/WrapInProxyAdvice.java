package net.bioclipse.recording;

import net.bioclipse.core.Activator;
import net.bioclipse.core.domain.IBioObject;

import org.aopalliance.intercept.MethodInvocation;
import org.springframework.aop.framework.ProxyFactory;

public class WrapInProxyAdvice implements IWrapInProxyAdvice {

	private ProxyFactory pf;
	
	public Object invoke(MethodInvocation invocation) throws Throwable {
		Object returnValue = invocation.proceed();
		if(  returnValue instanceof IBioObject && 
		   !(returnValue instanceof net.sf.cglib.proxy.Factory) ) {
			pf = new ProxyFactory();
			pf.addAdvice(this);
			pf.addAdvice(Activator.getDefault().getRecordingAdvice());
			pf.setTarget(returnValue);
			returnValue = pf.getProxy();
		}
		return returnValue;
	}
}
