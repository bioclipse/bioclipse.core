/**
 * SOAPService.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */
package subloc;
public interface SOAPService extends javax.xml.rpc.Service {
    public java.lang.String getAPIAddress();
    public subloc.API_PortType getAPI() throws javax.xml.rpc.ServiceException;
    public subloc.API_PortType getAPI(java.net.URL portAddress) throws javax.xml.rpc.ServiceException;
}
