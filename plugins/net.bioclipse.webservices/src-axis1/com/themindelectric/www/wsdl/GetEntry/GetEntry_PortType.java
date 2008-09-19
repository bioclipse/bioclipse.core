/**
 * GetEntry_PortType.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.themindelectric.www.wsdl.GetEntry;

public interface GetEntry_PortType extends java.rmi.Remote {

    /**
     * Get entry by database and unique ID//public String getEntry(String
     * database, String var, String param1, String param2) throws Throwable;//
     * Get entry by database and unique ID//public String getEntry(String
     * database, String var) throws Throwable;// Get DDBJ entry of Flat File
     * Format by Accession Number
     */
    public java.lang.String getDDBJEntry(java.lang.String accession) throws java.rmi.RemoteException;

    /**
     * Get DDBJ contig entry of Flat File Format by Accession Number
     */
    public java.lang.String getDDBJCONEntry(java.lang.String accession) throws java.rmi.RemoteException;

    /**
     * Get EMBL entry of Flat File Format by Accession Number//public
     * String getEMBLEntry(String accession) throws Throwable;// Get DAD
     * entry of Flat File Format by Accession Number
     */
    public java.lang.String getDADEntry(java.lang.String accession) throws java.rmi.RemoteException;

    /**
     * Get SWISSPROT entry of Flat File Format by Accession Number
     */
    public java.lang.String getSWISSEntry(java.lang.String accession) throws java.rmi.RemoteException;

    /**
     * Get UNIPROT entry of Flat File Format by Accession Number
     */
    public java.lang.String getUNIPROTEntry(java.lang.String accession) throws java.rmi.RemoteException;

    /**
     * Get PDB entry of Flat File Format by Accession Number
     */
    public java.lang.String getPDBEntry(java.lang.String accession) throws java.rmi.RemoteException;

    /**
     * Get PRF entry of Flat File Format by Accession Number
     */
    public java.lang.String getPRFEntry(java.lang.String accession) throws java.rmi.RemoteException;

    /**
     * Get Quality Value of DDBJ entry by Accession Number
     */
    public java.lang.String getQVEntry(java.lang.String accession) throws java.rmi.RemoteException;

    /**
     * Get DDBJ entry of FASTA Format by Accession Number
     */
    public java.lang.String getFASTA_DDBJEntry(java.lang.String accession) throws java.rmi.RemoteException;

    /**
     * Get DDBJ contig entry of FASTA Format by Accession Number
     */
    public java.lang.String getFASTA_DDBJCONEntry(java.lang.String accession) throws java.rmi.RemoteException;

    /**
     * Get DAD entry of FASTA Format by Accession Number
     */
    public java.lang.String getFASTA_DADEntry(java.lang.String accession) throws java.rmi.RemoteException;

    /**
     * Get SWISSPROT entry of FASTA Format by Accession Number
     */
    public java.lang.String getFASTA_SWISSEntry(java.lang.String accession) throws java.rmi.RemoteException;

    /**
     * Get UNIPROT entry of FASTA Format by Accession Number
     */
    public java.lang.String getFASTA_UNIPROTEntry(java.lang.String accession) throws java.rmi.RemoteException;

    /**
     * Get PDB entry of FASTA Format by Accession Number
     */
    public java.lang.String getFASTA_PDBEntry(java.lang.String accession) throws java.rmi.RemoteException;

    /**
     * Get PRF entry of FASTA Format by Accession Number
     */
    public java.lang.String getFASTA_PRFEntry(java.lang.String accession) throws java.rmi.RemoteException;

    /**
     * Get CDS information of DDBJ FASTA entry by Accession Number
     */
    public java.lang.String getFASTA_CDSEntry(java.lang.String accession) throws java.rmi.RemoteException;

    /**
     * Get DDBJ entry by Locus information
     */
    public java.lang.String getLocus_DDBJEntry(java.lang.String locus) throws java.rmi.RemoteException;

    /**
     * Get DDBJ entry by gene name information
     */
    public java.lang.String getGene_DDBJEntry(java.lang.String gene) throws java.rmi.RemoteException;

    /**
     * Get DDBJ entry by products information
     */
    public java.lang.String getProd_DDBJEntry(java.lang.String products) throws java.rmi.RemoteException;

    /**
     * Get DDBJ entry by PID information
     */
    public java.lang.String getPID_DDBJEntry(java.lang.String pid) throws java.rmi.RemoteException;

    /**
     * Get DDBJ entry by clone information
     */
    public java.lang.String getClone_DDBJEntry(java.lang.String clone) throws java.rmi.RemoteException;

    /**
     * Get DAD entry by PID information
     */
    public java.lang.String getPID_DADEntry(java.lang.String pid) throws java.rmi.RemoteException;

    /**
     * Get DDBJ entry of XML format by Accession Number
     */
    public java.lang.String getXML_DDBJEntry(java.lang.String accession) throws java.rmi.RemoteException;

    /**
     * Get DDBJ entry of Flat file format by Accession Number with
     * version
     */
    public java.lang.String getDDBJVerEntry(java.lang.String accession) throws java.rmi.RemoteException;

    /**
     * Get DDBJ entry of FASTA file format by Accession Number with
     * version
     */
    public java.lang.String getFASTA_DDBJVerEntry(java.lang.String accession) throws java.rmi.RemoteException;

    /**
     * Get DDBJ entry of FASTA file format by Accession Number with
     * version
     */
    public java.lang.String getFASTA_DDBJSeqEntry(java.lang.String accession, java.lang.String start, java.lang.String end) throws java.rmi.RemoteException;

    /**
     * Get entry by database and unique ID
     */
    public java.lang.String getEntryAsync(java.lang.String database, java.lang.String var, java.lang.String param1, java.lang.String param2) throws java.rmi.RemoteException;

    /**
     * Get entry by database and unique ID
     */
    public java.lang.String getEntryAsync(java.lang.String database, java.lang.String var) throws java.rmi.RemoteException;

    /**
     * Get DDBJ entry of Flat File Format by Accession Number
     */
    public java.lang.String getDDBJEntryAsync(java.lang.String accession) throws java.rmi.RemoteException;

    /**
     * Get DDBJ contig entry of Flat File Format by Accession Number
     */
    public java.lang.String getDDBJCONEntryAsync(java.lang.String accession) throws java.rmi.RemoteException;

    /**
     * Get EMBL entry of Flat File Format by Accession Number//public
     * String getEMBLEntryAsync(String accession) throws Throwable;// Get
     * DAD entry of Flat File Format by Accession Number
     */
    public java.lang.String getDADEntryAsync(java.lang.String accession) throws java.rmi.RemoteException;

    /**
     * Get SWISSPROT entry of Flat File Format by Accession Number
     */
    public java.lang.String getSWISSEntryAsync(java.lang.String accession) throws java.rmi.RemoteException;

    /**
     * Get UNIPROT entry of Flat File Format by Accession Number
     */
    public java.lang.String getUNIPROTEntryAsync(java.lang.String accession) throws java.rmi.RemoteException;

    /**
     * Get PDB entry of Flat File Format by Accession Number
     */
    public java.lang.String getPDBEntryAsync(java.lang.String accession) throws java.rmi.RemoteException;

    /**
     * Get PRF entry of Flat File Format by Accession Number
     */
    public java.lang.String getPRFEntryAsync(java.lang.String accession) throws java.rmi.RemoteException;

    /**
     * Get Quality Value of DDBJ entry by Accession Number
     */
    public java.lang.String getQVEntryAsync(java.lang.String accession) throws java.rmi.RemoteException;

    /**
     * Get DDBJ entry of FASTA Format by Accession Number
     */
    public java.lang.String getFASTA_DDBJEntryAsync(java.lang.String accession) throws java.rmi.RemoteException;

    /**
     * Get DDBJ contig entry of FASTA Format by Accession Number
     */
    public java.lang.String getFASTA_DDBJCONEntryAsync(java.lang.String accession) throws java.rmi.RemoteException;

    /**
     * Get DAD entry of FASTA Format by Accession Number
     */
    public java.lang.String getFASTA_DADEntryAsync(java.lang.String accession) throws java.rmi.RemoteException;

    /**
     * Get SWISSPROT entry of FASTA Format by Accession Number
     */
    public java.lang.String getFASTA_SWISSEntryAsync(java.lang.String accession) throws java.rmi.RemoteException;

    /**
     * Get UNIPROT entry of FASTA Format by Accession Number
     */
    public java.lang.String getFASTA_UNIPROTEntryAsync(java.lang.String accession) throws java.rmi.RemoteException;

    /**
     * Get PDB entry of FASTA Format by Accession Number
     */
    public java.lang.String getFASTA_PDBEntryAsync(java.lang.String accession) throws java.rmi.RemoteException;

    /**
     * Get PRF entry of FASTA Format by Accession Number
     */
    public java.lang.String getFASTA_PRFEntryAsync(java.lang.String accession) throws java.rmi.RemoteException;

    /**
     * Get CDS information of DDBJ FASTA entry by Accession Number
     */
    public java.lang.String getFASTA_CDSEntryAsync(java.lang.String accession) throws java.rmi.RemoteException;

    /**
     * Get DDBJ entry by Locus information
     */
    public java.lang.String getLocus_DDBJEntryAsync(java.lang.String locus) throws java.rmi.RemoteException;

    /**
     * Get DDBJ entry by gene name information
     */
    public java.lang.String getGene_DDBJEntryAsync(java.lang.String gene) throws java.rmi.RemoteException;

    /**
     * Get DDBJ entry by products information
     */
    public java.lang.String getProd_DDBJEntryAsync(java.lang.String products) throws java.rmi.RemoteException;

    /**
     * Get DDBJ entry by PID information
     */
    public java.lang.String getPID_DDBJEntryAsync(java.lang.String pid) throws java.rmi.RemoteException;

    /**
     * Get DDBJ entry by clone information
     */
    public java.lang.String getClone_DDBJEntryAsync(java.lang.String clone) throws java.rmi.RemoteException;

    /**
     * Get DAD entry by PID information
     */
    public java.lang.String getPID_DADEntryAsync(java.lang.String pid) throws java.rmi.RemoteException;

    /**
     * Get DDBJ entry of XML format by Accession Number
     */
    public java.lang.String getXML_DDBJEntryAsync(java.lang.String accession) throws java.rmi.RemoteException;

    /**
     * Get DDBJ entry of Flat file format by Accession Number with
     * version
     */
    public java.lang.String getDDBJVerEntryAsync(java.lang.String accession) throws java.rmi.RemoteException;

    /**
     * Get DDBJ entry of FASTA file format by Accession Number with
     * version
     */
    public java.lang.String getFASTA_DDBJVerEntryAsync(java.lang.String accession) throws java.rmi.RemoteException;

    /**
     * Get DDBJ entry of FASTA file format by Accession Number with
     * version
     */
    public java.lang.String getFASTA_DDBJSeqEntryAsync(java.lang.String accession, java.lang.String start, java.lang.String end) throws java.rmi.RemoteException;
}
