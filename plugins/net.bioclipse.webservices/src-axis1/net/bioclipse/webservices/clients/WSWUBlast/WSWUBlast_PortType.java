/**
 * WSWUBlast_PortType.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package net.bioclipse.webservices.clients.WSWUBlast;

public interface WSWUBlast_PortType extends java.rmi.Remote {
    public java.lang.String blastp(java.lang.String database, java.lang.String sequence, java.lang.String email) throws java.rmi.RemoteException;
    public java.lang.String blastn(java.lang.String database, java.lang.String sequence, java.lang.String email) throws java.rmi.RemoteException;
    public java.lang.String getOutput(java.lang.String jobid) throws java.rmi.RemoteException;
    public java.lang.String getXML(java.lang.String jobid) throws java.rmi.RemoteException;
    public java.lang.String runWUBlast(net.bioclipse.webservices.clients.WSWUBlast.InputParams params, net.bioclipse.webservices.clients.WSWUBlast.Data[] content) throws java.rmi.RemoteException;
    public java.lang.String checkStatus(java.lang.String jobid) throws java.rmi.RemoteException;
    public byte[] poll(java.lang.String jobid, java.lang.String type) throws java.rmi.RemoteException;
    public net.bioclipse.webservices.clients.WSWUBlast.WSFile[] getResults(java.lang.String jobid) throws java.rmi.RemoteException;
    public java.lang.String[] getIds(java.lang.String jobid) throws java.rmi.RemoteException;
    public byte[] polljob(java.lang.String jobid, java.lang.String outformat) throws java.rmi.RemoteException;
    public byte[] doWUBlast(net.bioclipse.webservices.clients.WSWUBlast.InputParams params, byte[] content) throws java.rmi.RemoteException;
}
