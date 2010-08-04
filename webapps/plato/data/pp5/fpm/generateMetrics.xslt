<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	version="2.0" xmlns:fn="http://www.w3.org/2005/xpath-functions">

	<xsl:output method="xml" indent="yes" name="xml" />

	<xsl:template match="/formats">
		<xsl:variable name="formats" select="."/>
		<xsl:variable name="m_metricIds" select="fn:distinct-values(//m/mId)"/>
	
	
	<xsl:element name="metrics">
			
			<xsl:for-each select="$m_metricIds">
			<xsl:variable name="m_metricId" select="."/>
			
			<xsl:variable name="m_metric" select="($formats/format/property/metrics/m[mId/text()=$m_metricId])[1]"/>
			<xsl:element name="metric">
				<xsl:element name="name">
					<xsl:value-of select="$m_metric/mName/text()"/>
				</xsl:element>
				<xsl:element name="metricId">
					<xsl:value-of select="$m_metric/mName/text()"/>
				</xsl:element>
				<xsl:element name="description">
					<xsl:value-of select="$m_metric/mDescription/text()"/>
				</xsl:element>
				
				
				<xsl:choose>
				<!--
					scale mapping: bool > booleanScale int > integerScale rational >
					floatScale all to freestring: string, time8601, XCLLabel, ISO-639.
					they are used in conjunction with metrics which are mapped in turn
				-->
				<xsl:when test="$m_metric/mType/text() = 'int'">
					<xsl:element name="integerScale"/>
				</xsl:when>
				<xsl:when test="$m_metric/mType/text() = 'bool'">
					<xsl:element name="booleanScale"/>
				</xsl:when>
				<xsl:when test="$m_metric/mType/text() = 'rational'">
					<xsl:element name="floatScale"/>
				</xsl:when>
				<xsl:otherwise>
					<xsl:element name="freeStringScale"/>
				</xsl:otherwise>
			</xsl:choose>
				 
			</xsl:element>
			</xsl:for-each>
	
	</xsl:element>
	</xsl:template>

</xsl:stylesheet>

