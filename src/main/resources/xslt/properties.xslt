<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

    <xsl:output method="text" version="1.0" encoding="UTF-8"/>

    <xsl:template match="text()"/>

    <xsl:template match="/">
        <xsl:for-each select="//comment[ @key='_header' ]">
            <xsl:apply-templates select="."/>
        </xsl:for-each>
        <xsl:for-each select="//entry[ not( @key='_header' or @key='_footer' ) ]">
            <xsl:sort select="@index"/>
            <xsl:variable name="key" select="@key"/>
            <xsl:for-each select="preceding-sibling::comment[ @key = $key ]">
                <xsl:apply-templates select="."/>
            </xsl:for-each>
            <xsl:apply-templates select="."/>
        </xsl:for-each>
        <xsl:for-each select="//comment[ @key='_footer' ]">
            <xsl:apply-templates select="."/>
        </xsl:for-each>
    </xsl:template>

    <xsl:template match="comment">
        <xsl:call-template name="lines-before">
            <xsl:with-param name="lines" select="number( @lines-before )"/>
        </xsl:call-template>
        <xsl:value-of select="."/>
        <xsl:if test="not( @eol = 0 )">
            <xsl:text>&#10;</xsl:text>
        </xsl:if>
    </xsl:template>

    <xsl:template match="entry">
        <xsl:call-template name="lines-before">
            <xsl:with-param name="lines" select="number( @lines-before )"/>
        </xsl:call-template>
        <xsl:value-of select="@key"/>
        <xsl:choose>
            <xsl:when test="@sep"><xsl:value-of select="@sep"/></xsl:when>
            <xsl:otherwise>=</xsl:otherwise>
        </xsl:choose>
        <xsl:choose>
            <xsl:when test="text"><xsl:apply-templates select="text"/></xsl:when>
            <xsl:otherwise><xsl:value-of select="."/></xsl:otherwise>
        </xsl:choose>
        <xsl:if test="not( @eol = 0 )">
            <xsl:text>&#10;</xsl:text>
        </xsl:if>
    </xsl:template>

    <xsl:template match="text">
        <xsl:choose>
            <xsl:when test="@key='0'"><xsl:value-of select="."/></xsl:when>
            <xsl:otherwise>
                <xsl:value-of select="@cont"/>
                <xsl:text>&#10;</xsl:text>
                <xsl:value-of select="@prefix"/>
                <xsl:value-of select="."/>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:template>

    <xsl:template name="lines-before">
        <xsl:param name="lines" select="0"/>
        <xsl:if test="$lines &gt; 0">
            <xsl:text>&#10;</xsl:text>
            <xsl:call-template name="lines-before">
                <xsl:with-param name="lines" select="$lines - 1"/>
            </xsl:call-template>
        </xsl:if>
    </xsl:template>
</xsl:stylesheet>