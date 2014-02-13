<xsl:stylesheet version="2.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:ead="urn:isbn:1-931666-22-9"
                xmlns="urn:isbn:1-931666-22-9"
                xmlns:xlink="http://www.w3.org/1999/xlink"
                xmlns:ext="java:org.socialhistory.ead.Daogrp"
                exclude-result-prefixes="ext xlink ead">

    <xsl:param name="archivalID"/>

    <xsl:template match="@*|node()">
        <xsl:copy>
            <xsl:apply-templates select="@*|node()"/>
        </xsl:copy>
    </xsl:template>

    <xsl:template match="ead:did/ead:unitid">
        <xsl:if test="ext:availableOnline(concat('http://disseminate.objectrepository.org/mets/10622/', $archivalID, '.', ext:urlencode(text())))">
            <xsl:variable name="handle"
                          select="concat('http://hdl.handle.net/10622/', $archivalID, '%2E', ext:urlencode(text()))"/>
            <daogrp xlink:type="extended">
                <xsl:element name="daoloc">
                    <xsl:attribute name="xlink:href"
                                   select="concat($handle, '?locatt=view:level3')"/>
                    <xsl:attribute name="xlink:type">locator</xsl:attribute>
                    <xsl:attribute name="xlink:label">thumbnail</xsl:attribute>
                </xsl:element>
                <xsl:element name="daoloc">
                    <xsl:attribute name="xlink:href"
                                   select="concat($handle, '?locatt=view:pdf')"/>
                    <xsl:attribute name="xlink:type">locator</xsl:attribute>
                    <xsl:attribute name="xlink:label">pdf</xsl:attribute>
                </xsl:element>
                <xsl:element name="daoloc">
                    <xsl:attribute name="xlink:href"
                                   select="concat($handle, '?locatt=view:mets')"/>
                    <xsl:attribute name="xlink:type">locator</xsl:attribute>
                    <xsl:attribute name="xlink:label">mets</xsl:attribute>
                </xsl:element>
                <xsl:element name="daoloc">
                    <xsl:attribute name="xlink:href"
                                   select="concat($handle, '?locatt=view:catalog')"/>
                    <xsl:attribute name="xlink:type">locator</xsl:attribute>
                    <xsl:attribute name="xlink:label">catalog</xsl:attribute>
                </xsl:element>
            </daogrp>
        </xsl:if>

        <xsl:copy>
            <xsl:apply-templates select="@*|node()"/>
        </xsl:copy>

    </xsl:template>

    <xsl:template match="ead:unitid/ead:doaloc"/>

</xsl:stylesheet>