/**
 * Struct.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package subloc;

public class Struct  implements java.io.Serializable {
    private java.lang.String ID;

    private java.lang.String LC;

    private java.lang.String CX;

    private java.lang.String SQ;

    private java.lang.String OS;

    private java.lang.String DE;

    public Struct() {
    }

    public Struct(
           java.lang.String ID,
           java.lang.String LC,
           java.lang.String CX,
           java.lang.String SQ,
           java.lang.String OS,
           java.lang.String DE) {
           this.ID = ID;
           this.LC = LC;
           this.CX = CX;
           this.SQ = SQ;
           this.OS = OS;
           this.DE = DE;
    }


    /**
     * Gets the ID value for this Struct.
     * 
     * @return ID
     */
    public java.lang.String getID() {
        return ID;
    }


    /**
     * Sets the ID value for this Struct.
     * 
     * @param ID
     */
    public void setID(java.lang.String ID) {
        this.ID = ID;
    }


    /**
     * Gets the LC value for this Struct.
     * 
     * @return LC
     */
    public java.lang.String getLC() {
        return LC;
    }


    /**
     * Sets the LC value for this Struct.
     * 
     * @param LC
     */
    public void setLC(java.lang.String LC) {
        this.LC = LC;
    }


    /**
     * Gets the CX value for this Struct.
     * 
     * @return CX
     */
    public java.lang.String getCX() {
        return CX;
    }


    /**
     * Sets the CX value for this Struct.
     * 
     * @param CX
     */
    public void setCX(java.lang.String CX) {
        this.CX = CX;
    }


    /**
     * Gets the SQ value for this Struct.
     * 
     * @return SQ
     */
    public java.lang.String getSQ() {
        return SQ;
    }


    /**
     * Sets the SQ value for this Struct.
     * 
     * @param SQ
     */
    public void setSQ(java.lang.String SQ) {
        this.SQ = SQ;
    }


    /**
     * Gets the OS value for this Struct.
     * 
     * @return OS
     */
    public java.lang.String getOS() {
        return OS;
    }


    /**
     * Sets the OS value for this Struct.
     * 
     * @param OS
     */
    public void setOS(java.lang.String OS) {
        this.OS = OS;
    }


    /**
     * Gets the DE value for this Struct.
     * 
     * @return DE
     */
    public java.lang.String getDE() {
        return DE;
    }


    /**
     * Sets the DE value for this Struct.
     * 
     * @param DE
     */
    public void setDE(java.lang.String DE) {
        this.DE = DE;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof Struct)) return false;
        Struct other = (Struct) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.ID==null && other.getID()==null) || 
             (this.ID!=null &&
              this.ID.equals(other.getID()))) &&
            ((this.LC==null && other.getLC()==null) || 
             (this.LC!=null &&
              this.LC.equals(other.getLC()))) &&
            ((this.CX==null && other.getCX()==null) || 
             (this.CX!=null &&
              this.CX.equals(other.getCX()))) &&
            ((this.SQ==null && other.getSQ()==null) || 
             (this.SQ!=null &&
              this.SQ.equals(other.getSQ()))) &&
            ((this.OS==null && other.getOS()==null) || 
             (this.OS!=null &&
              this.OS.equals(other.getOS()))) &&
            ((this.DE==null && other.getDE()==null) || 
             (this.DE!=null &&
              this.DE.equals(other.getDE())));
        __equalsCalc = null;
        return _equals;
    }

    private boolean __hashCodeCalc = false;
    public synchronized int hashCode() {
        if (__hashCodeCalc) {
            return 0;
        }
        __hashCodeCalc = true;
        int _hashCode = 1;
        if (getID() != null) {
            _hashCode += getID().hashCode();
        }
        if (getLC() != null) {
            _hashCode += getLC().hashCode();
        }
        if (getCX() != null) {
            _hashCode += getCX().hashCode();
        }
        if (getSQ() != null) {
            _hashCode += getSQ().hashCode();
        }
        if (getOS() != null) {
            _hashCode += getOS().hashCode();
        }
        if (getDE() != null) {
            _hashCode += getDE().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(Struct.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("urn:MySOAP/SubLoc", "Struct"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("ID");
        elemField.setXmlName(new javax.xml.namespace.QName("", "ID"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("LC");
        elemField.setXmlName(new javax.xml.namespace.QName("", "LC"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("CX");
        elemField.setXmlName(new javax.xml.namespace.QName("", "CX"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("SQ");
        elemField.setXmlName(new javax.xml.namespace.QName("", "SQ"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("OS");
        elemField.setXmlName(new javax.xml.namespace.QName("", "OS"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("DE");
        elemField.setXmlName(new javax.xml.namespace.QName("", "DE"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
    }

    /**
     * Return type metadata object
     */
    public static org.apache.axis.description.TypeDesc getTypeDesc() {
        return typeDesc;
    }

    /**
     * Get Custom Serializer
     */
    public static org.apache.axis.encoding.Serializer getSerializer(
           java.lang.String mechType, 
           java.lang.Class _javaType,  
           javax.xml.namespace.QName _xmlType) {
        return 
          new  org.apache.axis.encoding.ser.BeanSerializer(
            _javaType, _xmlType, typeDesc);
    }

    /**
     * Get Custom Deserializer
     */
    public static org.apache.axis.encoding.Deserializer getDeserializer(
           java.lang.String mechType, 
           java.lang.Class _javaType,  
           javax.xml.namespace.QName _xmlType) {
        return 
          new  org.apache.axis.encoding.ser.BeanDeserializer(
            _javaType, _xmlType, typeDesc);
    }

}
