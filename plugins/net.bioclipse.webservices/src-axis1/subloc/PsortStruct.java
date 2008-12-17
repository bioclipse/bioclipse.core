/**
 * PsortStruct.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */
package subloc;
public class PsortStruct  implements java.io.Serializable {
    private java.lang.String prediction;
    private java.lang.String detail;
    public PsortStruct() {
    }
    public PsortStruct(
           java.lang.String prediction,
           java.lang.String detail) {
           this.prediction = prediction;
           this.detail = detail;
    }
    /**
     * Gets the prediction value for this PsortStruct.
     * 
     * @return prediction
     */
    public java.lang.String getPrediction() {
        return prediction;
    }
    /**
     * Sets the prediction value for this PsortStruct.
     * 
     * @param prediction
     */
    public void setPrediction(java.lang.String prediction) {
        this.prediction = prediction;
    }
    /**
     * Gets the detail value for this PsortStruct.
     * 
     * @return detail
     */
    public java.lang.String getDetail() {
        return detail;
    }
    /**
     * Sets the detail value for this PsortStruct.
     * 
     * @param detail
     */
    public void setDetail(java.lang.String detail) {
        this.detail = detail;
    }
    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof PsortStruct)) return false;
        PsortStruct other = (PsortStruct) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.prediction==null && other.getPrediction()==null) || 
             (this.prediction!=null &&
              this.prediction.equals(other.getPrediction()))) &&
            ((this.detail==null && other.getDetail()==null) || 
             (this.detail!=null &&
              this.detail.equals(other.getDetail())));
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
        if (getPrediction() != null) {
            _hashCode += getPrediction().hashCode();
        }
        if (getDetail() != null) {
            _hashCode += getDetail().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }
    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(PsortStruct.class, true);
    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("urn:MySOAP/SubLoc", "PsortStruct"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("prediction");
        elemField.setXmlName(new javax.xml.namespace.QName("", "Prediction"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("detail");
        elemField.setXmlName(new javax.xml.namespace.QName("", "Detail"));
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
