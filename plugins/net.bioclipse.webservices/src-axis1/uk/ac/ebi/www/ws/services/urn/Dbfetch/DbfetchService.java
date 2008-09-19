/**
 * DbfetchService.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package uk.ac.ebi.www.ws.services.urn.Dbfetch;

public interface DbfetchService extends java.rmi.Remote {
    public java.lang.String[] fetchData(java.lang.String query, java.lang.String format, java.lang.String style) throws java.rmi.RemoteException;
    public java.lang.String[] getSupportedDBs() throws java.rmi.RemoteException;
    public java.lang.String[] getSupportedFormats() throws java.rmi.RemoteException;
    public java.lang.String[] getSupportedStyles() throws java.rmi.RemoteException;
}
