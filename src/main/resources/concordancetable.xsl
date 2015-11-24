<xsl:stylesheet version="2.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:ead="urn:isbn:1-931666-22-9">

    <xsl:output method="text"/>
    <xsl:strip-space elements="*"/>
    <xsl:param name="archivalID"/>

    <xsl:template match="@*|node()">
        <xsl:apply-templates select="@*|node()"/>
    </xsl:template>

    <xsl:template match="ead:dsc">
        <xsl:text>Archiefcode,Objectnummer,Inventarisnummer,Title</xsl:text>
        <xsl:for-each select="node()//ead:unitid[not(../../*/ead:did/ead:unitid)]">

            <xsl:variable name="file_title">
                <xsl:apply-templates select="../../../../../../../../../../../../../../ead:did"/>
                <xsl:apply-templates select="../../../../../../../../../../../../../ead:did"/>
                <xsl:apply-templates select="../../../../../../../../../../../../ead:did"/>
                <xsl:apply-templates select="../../../../../../../../../../../ead:did"/>
                <xsl:apply-templates select="../../../../../../../../../../ead:did"/>
                <xsl:apply-templates select="../../../../../../../../../ead:did"/>
                <xsl:apply-templates select="../../../../../../../../ead:did"/>
                <xsl:apply-templates select="../../../../../../../ead:did"/>
                <xsl:apply-templates select="../../../../../../ead:did"/>
                <xsl:apply-templates select="../../../../../ead:did"/>
                <xsl:apply-templates select="../../../../ead:did"/>
                <xsl:apply-templates select="../../../ead:did"/>
            </xsl:variable>
            <xsl:variable name="item_title">
                <xsl:for-each select="../ead:unittitle">
                    <xsl:apply-templates select="." mode="title" />
                    <xsl:if test="not(position()=last())">-</xsl:if>
                </xsl:for-each>
            </xsl:variable>
            <xsl:variable name="title">
                <xsl:if test="string-length($file_title)>0"><xsl:value-of select="$file_title"/>:
                </xsl:if>
                <xsl:value-of select="$item_title"/>
            </xsl:variable>
            <xsl:text>
</xsl:text>
            <xsl:value-of
                    select="concat('&quot;', $archivalID, '&quot;', ',', position(), ',&quot;', text(), '&quot;', ',&quot;', normalize-space( $title) , '&quot;')"/>
        </xsl:for-each>
    </xsl:template>

    <xsl:template match="ead:did[ancestor::*[@level='item' or @level='file']]">
        <xsl:for-each select="ead:unittitle">
            <xsl:apply-templates select="." mode="title" />
            <xsl:if test="not(position()=last())">-</xsl:if>
        </xsl:for-each>
    </xsl:template>


    <xsl:template match="node()" mode="title">
        <xsl:for-each select="text()">
            <xsl:value-of select="."/>
            <xsl:text> </xsl:text>
        </xsl:for-each>
        <xsl:apply-templates select="node()" mode="title"/>
    </xsl:template>


</xsl:stylesheet>