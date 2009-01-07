/**
 * API_PortType.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package subloc;

public interface API_PortType extends java.rmi.Remote {
    public subloc.Struct[] id_search(java.lang.String param1, java.lang.String param2) throws java.rmi.RemoteException;
    public subloc.Struct[] name_search(java.lang.String param1) throws java.rmi.RemoteException;
    public subloc.BlastResult blast_search(java.lang.String param1, java.lang.String param2, java.lang.String param3, java.lang.String param4) throws java.rmi.RemoteException;
    public subloc.PredictStruct pro_predict(java.lang.String param1) throws java.rmi.RemoteException;
    public subloc.PredictStruct eu_predict(java.lang.String param1) throws java.rmi.RemoteException;
    public subloc.PsortStruct psort_predict(java.lang.String param1) throws java.rmi.RemoteException;
    public java.lang.String feed_entry(java.lang.String param1, java.lang.String param2, java.lang.String param3, java.lang.String param4, java.lang.String param5, java.lang.String param6) throws java.rmi.RemoteException;
}
