package net.bioclipse.webservices.services;
/**
 * 
 * Supports access to EBI's WebService in async mode.
 * 
 * @author edrin
 *
 */
import java.net.MalformedURLException;
import org.apache.axis.client.async.*;
import org.apache.axis.client.Call;
import org.apache.axis.soap.SOAPConstants;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import javax.xml.namespace.QName;
public class WSDbfetch {
        public static final String URL = "http://www.ebi.ac.uk/ws/services/WSDbfetch";
        public java.lang.String fetchData(String query,
                                                                                String format,
                                                                                String style,
                                                                                IProgressMonitor monitor)
                throws CoreException {
                String result = null;
                try {
                        Call call = new Call(URL);
                        call.setUseSOAPAction(true);
                        call.setSOAPActionURI("");
                        call.setSOAPVersion(SOAPConstants.SOAP11_CONSTANTS);
                        call.setOperationName(new QName("http://wsdbfetch.ws.jdbfetch.ebi.ac.uk", "fetchData"));
                        AsyncCall ac = new AsyncCall(call);
                        IAsyncResult ar = ac.invoke(new Object[] {query, format, style});
                        org.apache.axis.client.async.Status status = null;
                        boolean isCanceled = false;
                        while ((status = ar.getStatus()) == 
                                org.apache.axis.client.async.Status.NONE &&
                                (isCanceled = monitor.isCanceled()) == false) {
                                Thread.sleep(50);
                        }
                        if (isCanceled == true) {
                                throwCoreException("Operation was canceled.");
                        } else if (status == org.apache.axis.client.async.Status.COMPLETED) {
                                // trim the result to remove empty lines before and after the string...
                                result = ((String)ar.getResponse()).trim();
                        } else if (status == org.apache.axis.client.async.Status.EXCEPTION) {
                                throwCoreException("Axis Exception: " + ar.getException().getMessage());
                        }
                } catch (MalformedURLException e) {
                        throwCoreException("MalformedURLException: " + e.getMessage());
                } catch (InterruptedException e) {
                        throwCoreException("InterruptedException: " + e.getMessage());
                }
                return result;
        }
        public java.lang.String[] getSupportedDBs(IProgressMonitor monitor)
                throws CoreException  {
                String[] result = new String[0];
                try {
                        Call call = new Call(URL);
                        call.setUseSOAPAction(true);
                        call.setSOAPActionURI("");
                        call.setSOAPVersion(SOAPConstants.SOAP11_CONSTANTS);
                        call.setOperationName(new QName("http://wsdbfetch.ws.jdbfetch.ebi.ac.uk", "getSupportedDBs"));
                        AsyncCall ac = new AsyncCall(call);
                        IAsyncResult ar = ac.invoke(new Object[0]);
                        org.apache.axis.client.async.Status status = null;
                        boolean isCanceled = false;
                        while ((status = ar.getStatus()) == 
                                org.apache.axis.client.async.Status.NONE &&
                                (isCanceled = monitor.isCanceled()) == false) {
                                Thread.sleep(50);
                        }
                        if (isCanceled == true) {
                                throwCoreException("Operation was canceled.");
                        } else if (status == org.apache.axis.client.async.Status.COMPLETED) {
                                result = (String[])ar.getResponse();
                        } else if (status == org.apache.axis.client.async.Status.EXCEPTION) {
                                throwCoreException("Axis Exception: " + ar.getException().getMessage());
                        }
                } catch (MalformedURLException e) {
                        throwCoreException("MalformedURLException: " + e.getMessage());
                } catch (InterruptedException e) {
                        throwCoreException("InterruptedException: " + e.getMessage());
                }
                return result;
        }
        public java.lang.String[] getSupportedFormats(IProgressMonitor monitor)
    	throws CoreException {
                String[] result = new String[0];
                try {
                        Call call = new Call(URL);
                        call.setUseSOAPAction(true);
                        call.setSOAPActionURI("");
                        call.setSOAPVersion(SOAPConstants.SOAP11_CONSTANTS);
                        call.setOperationName(new QName("http://wsdbfetch.ws.jdbfetch.ebi.ac.uk", "getSupportedFormats"));
                        AsyncCall ac = new AsyncCall(call);
                        IAsyncResult ar = ac.invoke(new Object[0]);
                        org.apache.axis.client.async.Status status = null;
                        boolean isCanceled = false;
                        while ((status = ar.getStatus()) == 
                                org.apache.axis.client.async.Status.NONE &&
                                (isCanceled = monitor.isCanceled()) == false) {
                                Thread.sleep(50);
                        }
                        if (isCanceled == true) {
                                throwCoreException("Operation was canceled.");
                        } else if (status == org.apache.axis.client.async.Status.COMPLETED) {
                                result = (String[])ar.getResponse();
                        } else if (status == org.apache.axis.client.async.Status.EXCEPTION) {
                                throwCoreException("Axis Exception: " + ar.getException().getMessage());
                        }
                } catch (MalformedURLException e) {
                        throwCoreException("MalformedURLException: " + e.getMessage());
                } catch (InterruptedException e) {
                        throwCoreException("InterruptedException: " + e.getMessage());
                }
                return result;
        }
        public java.lang.String[] getSupportedStyles(IProgressMonitor monitor)
                throws CoreException {
                String[] result = new String[0];
                try {
                        Call call = new Call(URL);
                        call.setUseSOAPAction(true);
                        call.setSOAPActionURI("");
                        call.setSOAPVersion(SOAPConstants.SOAP11_CONSTANTS);
                        call.setOperationName(new QName("http://wsdbfetch.ws.jdbfetch.ebi.ac.uk", "getSupportedStyles"));
                        AsyncCall ac = new AsyncCall(call);
                        IAsyncResult ar = ac.invoke(new Object[0]);
                        org.apache.axis.client.async.Status status = null;
                        boolean isCanceled = false;
                        while ((status = ar.getStatus()) == 
                                org.apache.axis.client.async.Status.NONE &&
                                (isCanceled = monitor.isCanceled()) == false) {
                                Thread.sleep(50);
                        }
                        if (isCanceled == true) {
                                throwCoreException("Operation was canceled.");
                        } else if (status == org.apache.axis.client.async.Status.COMPLETED) {
                                result = (String[])ar.getResponse();
                        } else if (status == org.apache.axis.client.async.Status.EXCEPTION) {
                                throwCoreException("Axis Exception: " + ar.getException().getMessage());
                        }
                } catch (MalformedURLException e) {
                        throwCoreException("MalformedURLException: " + e.getMessage());
                } catch (InterruptedException e) {
                        throwCoreException("InterruptedException: " + e.getMessage());
                }
                return result;
        }
        private void throwCoreException(String message) throws CoreException {
                IStatus status =
                        new Status(IStatus.ERROR, "net.bioclipse.webservices", IStatus.OK, message, null);
                throw new CoreException(status);
        }
}
