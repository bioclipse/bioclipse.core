/**
 * GetEntry_ServiceLocator.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.themindelectric.www.wsdl.GetEntry;

public class GetEntry_ServiceLocator extends org.apache.axis.client.Service implements com.themindelectric.www.wsdl.GetEntry.GetEntry_Service {

    public GetEntry_ServiceLocator() {
    }


    public GetEntry_ServiceLocator(org.apache.axis.EngineConfiguration config) {
        super(config);
    }

    public GetEntry_ServiceLocator(java.lang.String wsdlLoc, javax.xml.namespace.QName sName) throws javax.xml.rpc.ServiceException {
        super(wsdlLoc, sName);
    }

    // Use to get a proxy class for GetEntry
    private java.lang.String GetEntry_address = "http://xml.nig.ac.jp/xddbj/GetEntry";

    public java.lang.String getGetEntryAddress() {
        return GetEntry_address;
    }

    // The WSDD service name defaults to the port name.
    private java.lang.String GetEntryWSDDServiceName = "GetEntry";

    public java.lang.String getGetEntryWSDDServiceName() {
        return GetEntryWSDDServiceName;
    }

    public void setGetEntryWSDDServiceName(java.lang.String name) {
        GetEntryWSDDServiceName = name;
    }

    public com.themindelectric.www.wsdl.GetEntry.GetEntry_PortType getGetEntry() throws javax.xml.rpc.ServiceException {
       java.net.URL endpoint;
        try {
            endpoint = new java.net.URL(GetEntry_address);
        }
        catch (java.net.MalformedURLException e) {
            throw new javax.xml.rpc.ServiceException(e);
        }
        return getGetEntry(endpoint);
    }

    public com.themindelectric.www.wsdl.GetEntry.GetEntry_PortType getGetEntry(java.net.URL portAddress) throws javax.xml.rpc.ServiceException {
        try {
            com.themindelectric.www.wsdl.GetEntry.GetEntryStub _stub = new com.themindelectric.www.wsdl.GetEntry.GetEntryStub(portAddress, this);
            _stub.setPortName(getGetEntryWSDDServiceName());
            return _stub;
        }
        catch (org.apache.axis.AxisFault e) {
            return null;
        }
    }

    public void setGetEntryEndpointAddress(java.lang.String address) {
        GetEntry_address = address;
    }

    /**
     * For the given interface, get the stub implementation.
     * If this service has no port for the given interface,
     * then ServiceException is thrown.
     */
    public java.rmi.Remote getPort(Class serviceEndpointInterface) throws javax.xml.rpc.ServiceException {
        try {
            if (com.themindelectric.www.wsdl.GetEntry.GetEntry_PortType.class.isAssignableFrom(serviceEndpointInterface)) {
                com.themindelectric.www.wsdl.GetEntry.GetEntryStub _stub = new com.themindelectric.www.wsdl.GetEntry.GetEntryStub(new java.net.URL(GetEntry_address), this);
                _stub.setPortName(getGetEntryWSDDServiceName());
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
        if ("GetEntry".equals(inputPortName)) {
            return getGetEntry();
        }
        else  {
            java.rmi.Remote _stub = getPort(serviceEndpointInterface);
            ((org.apache.axis.client.Stub) _stub).setPortName(portName);
            return _stub;
        }
    }

    public javax.xml.namespace.QName getServiceName() {
        return new javax.xml.namespace.QName("http://www.themindelectric.com/wsdl/GetEntry/", "GetEntry");
    }

    private java.util.HashSet ports = null;

    public java.util.Iterator getPorts() {
        if (ports == null) {
            ports = new java.util.HashSet();
            ports.add(new javax.xml.namespace.QName("http://www.themindelectric.com/wsdl/GetEntry/", "GetEntry"));
        }
        return ports.iterator();
    }

    /**
    * Set the endpoint address for the specified port name.
    */
    public void setEndpointAddress(java.lang.String portName, java.lang.String address) throws javax.xml.rpc.ServiceException {
        
if ("GetEntry".equals(portName)) {
            setGetEntryEndpointAddress(address);
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
