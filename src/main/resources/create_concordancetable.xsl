<xsl:stylesheet version="2.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:ead="urn:isbn:1-931666-22-9">

    <xsl:output method="text"/>
    <xsl:param name="archivalID"/>
    <xsl:preserve-space elements="*"/>

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
                    <xsl:call-template name="unittitle">
                        <xsl:with-param name="title" select="."/>
                    </xsl:call-template>
                    <xsl:if test="not(position()=last())"> - </xsl:if>
                </xsl:for-each>
            </xsl:variable>
            <xsl:variable name="title">
                <xsl:call-template name="double_quote">
                    <xsl:with-param name="text">
                        <xsl:if test="string-length($file_title)>0"><xsl:value-of select="$file_title"/>: </xsl:if>
                        <xsl:value-of select="$item_title"/>
                    </xsl:with-param>
                </xsl:call-template>
            </xsl:variable>
            <xsl:text>
</xsl:text>
            <xsl:value-of
                    select="concat('&quot;', $archivalID, '&quot;', ',', position(), ',&quot;', text(), '&quot;', ',&quot;', $title, '&quot;')"/>
        </xsl:for-each>
    </xsl:template>

    <xsl:template match="ead:did[ancestor::*[@level='item' or @level='file']]">
        <xsl:for-each select="ead:unittitle">
            <xsl:call-template name="unittitle">
                <xsl:with-param name="title" select="."/>
            </xsl:call-template>
            <xsl:if test="not(position()=last())"> - </xsl:if>
        </xsl:for-each>
    </xsl:template>


    <xsl:template name="unittitle">
        <xsl:param name="title"/>
        <xsl:value-of select="$title//text()"/>
    </xsl:template>

    <xsl:template name="double_quote">
        <xsl:param name="text"/>
        <xsl:value-of select="translate(normalize-space($text), '&quot;', '''')"/>
    </xsl:template>


</xsl:stylesheet>