/**
 * DbfetchServiceServiceLocator.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package uk.ac.ebi.www.ws.services.urn.Dbfetch;

public class DbfetchServiceServiceLocator extends org.apache.axis.client.Service implements uk.ac.ebi.www.ws.services.urn.Dbfetch.DbfetchServiceService {

    public DbfetchServiceServiceLocator() {
    }


    public DbfetchServiceServiceLocator(org.apache.axis.EngineConfiguration config) {
        super(config);
    }

    public DbfetchServiceServiceLocator(java.lang.String wsdlLoc, javax.xml.namespace.QName sName) throws javax.xml.rpc.ServiceException {
        super(wsdlLoc, sName);
    }

    // Use to get a proxy class for UrnDbfetch
    private java.lang.String UrnDbfetch_address = "http://www.ebi.ac.uk/ws/services/urn:Dbfetch";

    public java.lang.String getUrnDbfetchAddress() {
        return UrnDbfetch_address;
    }

    // The WSDD service name defaults to the port name.
    private java.lang.String UrnDbfetchWSDDServiceName = "urn:Dbfetch";

    public java.lang.String getUrnDbfetchWSDDServiceName() {
        return UrnDbfetchWSDDServiceName;
    }

    public void setUrnDbfetchWSDDServiceName(java.lang.String name) {
        UrnDbfetchWSDDServiceName = name;
    }

    public uk.ac.ebi.www.ws.services.urn.Dbfetch.DbfetchService getUrnDbfetch() throws javax.xml.rpc.ServiceException {
       java.net.URL endpoint;
        try {
            endpoint = new java.net.URL(UrnDbfetch_address);
        }
        catch (java.net.MalformedURLException e) {
            throw new javax.xml.rpc.ServiceException(e);
        }
        return getUrnDbfetch(endpoint);
    }

    public uk.ac.ebi.www.ws.services.urn.Dbfetch.DbfetchService getUrnDbfetch(java.net.URL portAddress) throws javax.xml.rpc.ServiceException {
        try {
            uk.ac.ebi.www.ws.services.urn.Dbfetch.DbfetchSoapBindingStub _stub = new uk.ac.ebi.www.ws.services.urn.Dbfetch.DbfetchSoapBindingStub(portAddress, this);
            _stub.setPortName(getUrnDbfetchWSDDServiceName());
            return _stub;
        }
        catch (org.apache.axis.AxisFault e) {
            return null;
        }
    }

    public void setUrnDbfetchEndpointAddress(java.lang.String address) {
        UrnDbfetch_address = address;
    }

    /**
     * For the given interface, get the stub implementation.
     * If this service has no port for the given interface,
     * then ServiceException is thrown.
     */
    public java.rmi.Remote getPort(Class serviceEndpointInterface) throws javax.xml.rpc.ServiceException {
        try {
            if (uk.ac.ebi.www.ws.services.urn.Dbfetch.DbfetchService.class.isAssignableFrom(serviceEndpointInterface)) {
                uk.ac.ebi.www.ws.services.urn.Dbfetch.DbfetchSoapBindingStub _stub = new uk.ac.ebi.www.ws.services.urn.Dbfetch.DbfetchSoapBindingStub(new java.net.URL(UrnDbfetch_address), this);
                _stub.setPortName(getUrnDbfetchWSDDServiceName());
                return _stub;
            }
        }
        catch (java.lang.Throwable t) {
            throw new javax.xml.rpc.ServiceException(t);
        }
        throw new javax.xml.rpc.ServiceException("There is no stub implementation for the interface:  " + (serviceEndpointInterface == null ? "null" : serviceEndpointInterface.getName()));
    }

    /**
     * For the given interface, get the stub implementation.
     * If this service has no port for the given interface,
     * then ServiceException is thrown.
     */
    public java.rmi.Remote getPort(javax.xml.namespace.QName portName, Class serviceEndpointInterface) throws javax.xml.rpc.ServiceException {
        if (portName == null) {
            return getPort(serviceEndpointInterface);
        }
        java.lang.String inputPortName = portName.getLocalPart();
        if ("urn:Dbfetch".equals(inputPortName)) {
            return getUrnDbfetch();
        }
        else  {
            java.rmi.Remote _stub = getPort(serviceEndpointInterface);
            ((org.apache.axis.client.Stub) _stub).setPortName(portName);
            return _stub;
        }
    }

    public javax.xml.namespace.QName getServiceName() {
        return new javax.xml.namespace.QName("http://www.ebi.ac.uk/ws/services/urn:Dbfetch", "DbfetchServiceService");
    }

    private java.util.HashSet ports = null;

    public java.util.Iterator getPorts() {
        if (ports == null) {
            ports = new java.util.HashSet();
            ports.add(new javax.xml.namespace.QName("http://www.ebi.ac.uk/ws/services/urn:Dbfetch", "urn:Dbfetch"));
        }
        return ports.iterator();
    }

    /**
    * Set the endpoint address for the specified port name.
    */
    public void setEndpointAddress(java.lang.String portName, java.lang.String address) throws javax.xml.rpc.ServiceException {
        
if ("UrnDbfetch".equals(portName)) {
            setUrnDbfetchEndpointAddress(address);
        }
        else 
{ // Unknown Port Name
            throw new javax.xml.rpc.ServiceException(" Cannot set Endpoint Address for Unknown Port" + portName);
        }
    }

    /**
    * Set the endpoint address for the specified port name.
    */
    public void setEndpointAddress(javax.xml.namespace.QName portName, java.lang.String address) throws javax.xml.rpc.ServiceException {
        setEndpointAddress(portName.getLocalPart(), address);
    }

}
