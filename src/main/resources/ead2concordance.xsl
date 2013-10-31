<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:ead="urn:isbn:1-931666-22-9">

    <xsl:output method="text"/>
    <xsl:strip-space elements="*"/>
    <xsl:param name="archivalID"/>

    <xsl:template match="@*|node()">
        <xsl:apply-templates select="@*|node()"/>
    </xsl:template>

    <xsl:template match="ead:dsc">
<xsl:text>Archiefcode,Objectnummer,Inventarisnummer</xsl:text>
        <xsl:for-each select="node()//ead:unitid[not(../../*/ead:did/ead:unitid)]">
<xsl:text>
</xsl:text>
            <xsl:value-of select="concat($archivalID, ',', position(), ',&quot;', text(), '&quot;')"/>
        </xsl:for-each>
    </xsl:template>

</xsl:stylesheet>