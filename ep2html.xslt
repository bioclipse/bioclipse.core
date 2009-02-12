<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

  <xsl:output method="html" encoding="UTF-8" doctype-public="-//W3C//DTD HTML 4.01 Transitional//EN"
        doctype-system="http://www.w3.org/TR/html4/loose.dtd" indent="yes"/>

<xsl:template match="/">
<html>
<head>
  <title>Extension Point '<xsl:value-of select="/list/extension/@point"/>' in Bioclipse</title>
  <style type="text/css">                                          
     body { font:normal verdana,arial,helvetica; color:#000000; }
     table { margin-left: 5%; }
  </style>
</head>
<body>
<h1>Extension Point '<xsl:value-of select="/list/extension/@point"/>' in Bioclipse</h1>
<xsl:for-each select="./*">
  <xsl:apply-templates select="."/>
</xsl:for-each>
</body>
</html>
</xsl:template>

<xsl:template match="content-type">
<h5><xsl:value-of select="./@name"/></h5>
<table>
<tr><td><b>parent</b></td><td><xsl:value-of select="./@base-type"/></td></tr>
<tr><td><b>id</b></td><td><xsl:value-of select="./@id"/></td></tr>
<xsl:if test="./describer">
<tr><td><b>describer</b></td><td><xsl:value-of select="./describer/@class"/></td></tr>
  <xsl:if test="./describer/parameter">
    <xsl:for-each select="./describer//parameter">
      <tr><td></td><td><i><xsl:value-of select="./@name"/></i>:<xsl:value-of select="./@value"/></td></tr>
    </xsl:for-each>
  </xsl:if>
</xsl:if>
</table>
</xsl:template>

</xsl:stylesheet>
