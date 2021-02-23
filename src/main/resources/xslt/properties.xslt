<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet
        version="1.0"
        xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
        xmlns:pxrutils="xalan://com.brentcroft.pxr.PxrUtils"
        xmlns:xalan="http://xml.apache.org/xalan">

    <xsl:output
            method="text"
            encoding="ISO-8859-1"

            xalan:line-separator="&#xa;"/>

    <xsl:variable name="NL"><xsl:text>&#10;</xsl:text></xsl:variable>


    <xsl:template match="text()"/>

    <xsl:template match="/">
        <xsl:for-each select="//comment[ @key='_header' ]">
            <xsl:apply-templates select="."/>
        </xsl:for-each>
        <xsl:for-each select="//entry[ not( @key='_header' or @key='_footer' ) ]">
            <xsl:sort select="@index" data-type="number"/>
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
        <xsl:variable name="text" select="text()"/>
        <xsl:call-template name="lines-before">
            <xsl:with-param name="lines" select="number( @lines-before )"/>
        </xsl:call-template>
        <xsl:if test="string-length( normalize-space( $text ) ) &gt; 1 and not( starts-with( $text, '#' ) )">
            <xsl:text>#</xsl:text>
        </xsl:if>
        <xsl:value-of select="$text"/>
        <xsl:if test="not( @eol = '0' )">
            <xsl:value-of select="$NL"/>
        </xsl:if>
    </xsl:template>

    <xsl:template match="entry">
        <xsl:call-template name="lines-before">
            <xsl:with-param name="lines" select="number( @lines-before )"/>
        </xsl:call-template>
        <xsl:value-of select="pxrutils:escapeKey( @key )"/>
        <xsl:choose>
            <xsl:when test="@sep">
                <xsl:value-of select="@sep"/>
            </xsl:when>
            <xsl:otherwise>=</xsl:otherwise>
        </xsl:choose>
        <xsl:choose>
            <xsl:when test="text">
                <xsl:apply-templates select="text">
                    <xsl:sort select="@key" data-type="number"/>
                </xsl:apply-templates>
            </xsl:when>
            <xsl:otherwise>
                <xsl:value-of select="pxrutils:escapeValue( text() )"/>
            </xsl:otherwise>
        </xsl:choose>
        <xsl:if test="not( @eol = '0' )">
            <xsl:value-of select="$NL"/>
        </xsl:if>
    </xsl:template>

    <xsl:template match="text">
        <xsl:variable name="value" select="text()"/>
        <xsl:choose>
            <xsl:when test="( @key = '0' )"><xsl:value-of select="pxrutils:escapeValue( $value )"/></xsl:when>
            <xsl:when test="( @eol = '0' ) and not( $value ) and not( @prefix )">
                <xsl:text>\</xsl:text>
            </xsl:when>
            <xsl:otherwise>
                <xsl:text>\</xsl:text>
                <xsl:if test="not( @eol = '0' ) or $value or @prefix">
                    <xsl:value-of select="$NL"/>
                </xsl:if>
                <xsl:value-of select="@prefix"/>
                <xsl:value-of select="pxrutils:escapeValue( $value )"/>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:template>

    <xsl:template name="lines-before">
        <xsl:param name="lines" select="0"/>
        <xsl:if test="$lines &gt; 0">
            <xsl:value-of select="$NL"/>
            <xsl:call-template name="lines-before">
                <xsl:with-param name="lines" select="$lines - 1"/>
            </xsl:call-template>
        </xsl:if>
    </xsl:template>
</xsl:stylesheet>