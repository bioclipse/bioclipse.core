/**
 * BlastResult.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package subloc;

public class BlastResult  implements java.io.Serializable {
    private java.lang.String plain;

    private java.lang.String parsed;

    public BlastResult() {
    }

    public BlastResult(
           java.lang.String plain,
           java.lang.String parsed) {
           this.plain = plain;
           this.parsed = parsed;
    }


    /**
     * Gets the plain value for this BlastResult.
     * 
     * @return plain
     */
    public java.lang.String getPlain() {
        return plain;
    }


    /**
     * Sets the plain value for this BlastResult.
     * 
     * @param plain
     */
    public void setPlain(java.lang.String plain) {
        this.plain = plain;
    }


    /**
     * Gets the parsed value for this BlastResult.
     * 
     * @return parsed
     */
    public java.lang.String getParsed() {
        return parsed;
    }


    /**
     * Sets the parsed value for this BlastResult.
     * 
     * @param parsed
     */
    public void setParsed(java.lang.String parsed) {
        this.parsed = parsed;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof BlastResult)) return false;
        BlastResult other = (BlastResult) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.plain==null && other.getPlain()==null) || 
             (this.plain!=null &&
              this.plain.equals(other.getPlain()))) &&
            ((this.parsed==null && other.getParsed()==null) || 
             (this.parsed!=null &&
              this.parsed.equals(other.getParsed())));
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
        if (getPlain() != null) {
            _hashCode += getPlain().hashCode();
        }
        if (getParsed() != null) {
            _hashCode += getParsed().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(BlastResult.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("urn:MySOAP/SubLoc", "BlastResult"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("plain");
        elemField.setXmlName(new javax.xml.namespace.QName("", "plain"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("parsed");
        elemField.setXmlName(new javax.xml.namespace.QName("", "parsed"));
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
