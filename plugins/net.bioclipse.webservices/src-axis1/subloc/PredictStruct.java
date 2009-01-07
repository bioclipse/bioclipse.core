/**
 * PredictStruct.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package subloc;

public class PredictStruct  implements java.io.Serializable {
    private java.lang.String seq;

    private java.lang.String prediction;

    private java.lang.String RI;

    private java.lang.String expectAcc;

    public PredictStruct() {
    }

    public PredictStruct(
           java.lang.String seq,
           java.lang.String prediction,
           java.lang.String RI,
           java.lang.String expectAcc) {
           this.seq = seq;
           this.prediction = prediction;
           this.RI = RI;
           this.expectAcc = expectAcc;
    }


    /**
     * Gets the seq value for this PredictStruct.
     * 
     * @return seq
     */
    public java.lang.String getSeq() {
        return seq;
    }


    /**
     * Sets the seq value for this PredictStruct.
     * 
     * @param seq
     */
    public void setSeq(java.lang.String seq) {
        this.seq = seq;
    }


    /**
     * Gets the prediction value for this PredictStruct.
     * 
     * @return prediction
     */
    public java.lang.String getPrediction() {
        return prediction;
    }


    /**
     * Sets the prediction value for this PredictStruct.
     * 
     * @param prediction
     */
    public void setPrediction(java.lang.String prediction) {
        this.prediction = prediction;
    }


    /**
     * Gets the RI value for this PredictStruct.
     * 
     * @return RI
     */
    public java.lang.String getRI() {
        return RI;
    }


    /**
     * Sets the RI value for this PredictStruct.
     * 
     * @param RI
     */
    public void setRI(java.lang.String RI) {
        this.RI = RI;
    }


    /**
     * Gets the expectAcc value for this PredictStruct.
     * 
     * @return expectAcc
     */
    public java.lang.String getExpectAcc() {
        return expectAcc;
    }


    /**
     * Sets the expectAcc value for this PredictStruct.
     * 
     * @param expectAcc
     */
    public void setExpectAcc(java.lang.String expectAcc) {
        this.expectAcc = expectAcc;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof PredictStruct)) return false;
        PredictStruct other = (PredictStruct) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.seq==null && other.getSeq()==null) || 
             (this.seq!=null &&
              this.seq.equals(other.getSeq()))) &&
            ((this.prediction==null && other.getPrediction()==null) || 
             (this.prediction!=null &&
              this.prediction.equals(other.getPrediction()))) &&
            ((this.RI==null && other.getRI()==null) || 
             (this.RI!=null &&
              this.RI.equals(other.getRI()))) &&
            ((this.expectAcc==null && other.getExpectAcc()==null) || 
             (this.expectAcc!=null &&
              this.expectAcc.equals(other.getExpectAcc())));
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
        if (getSeq() != null) {
            _hashCode += getSeq().hashCode();
        }
        if (getPrediction() != null) {
            _hashCode += getPrediction().hashCode();
        }
        if (getRI() != null) {
            _hashCode += getRI().hashCode();
        }
        if (getExpectAcc() != null) {
            _hashCode += getExpectAcc().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(PredictStruct.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("urn:MySOAP/SubLoc", "PredictStruct"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("seq");
        elemField.setXmlName(new javax.xml.namespace.QName("", "Seq"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("prediction");
        elemField.setXmlName(new javax.xml.namespace.QName("", "Prediction"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("RI");
        elemField.setXmlName(new javax.xml.namespace.QName("", "RI"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("expectAcc");
        elemField.setXmlName(new javax.xml.namespace.QName("", "ExpectAcc"));
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
