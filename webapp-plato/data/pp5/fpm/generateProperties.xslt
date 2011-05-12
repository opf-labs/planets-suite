<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:fn="http://www.w3.org/2005/xpath-functions" version="2.0" >

	<xsl:output method="xml" indent="yes" name="xml" />

	<xsl:template match="*|text()|@*">
		<xsl:copy>
			<xsl:apply-templates select="@*" />
			<xsl:apply-templates select="*|text()" />
		</xsl:copy>
	</xsl:template>

	<xsl:template match="formats">
		<xsl:variable name="formats" select="."/>
		<xsl:variable name="allPropertyIds" select="fn:distinct-values(//property/id)"/>
		
		<xsl:element name="measurementsDescriptor">
		
			<xsl:for-each select="$allPropertyIds">
				<xsl:variable name="pid" select="."/>
				<xsl:variable name="properties" select="$formats/format/property[id/text()=$pid]"/>
				<xsl:variable name="property" select="$properties[1]"/>
				
				<xsl:element name="property">
					<xsl:element name="category">outcome:object</xsl:element>
					<xsl:element name="propertyId"><xsl:value-of select="concat('xcl/', $property/name/text())" /></xsl:element>
					<xsl:element name="fpmFormats">
						<xsl:for-each select="$properties">
							<xsl:element name="fpmFormat">
								<xsl:value-of select="../@puid"/>
							</xsl:element>
						</xsl:for-each>
					</xsl:element>
					<xsl:element name="name">
						<xsl:value-of select="concat('xcl/', $property/name/text())" />
					</xsl:element>
					<xsl:apply-templates select="$property/description" />
					
					<xsl:choose>
						<!--
							scale mapping: bool > booleanScale int > integerScale rational >
							floatScale all to freestring: string, time8601, XCLLabel, ISO-639.
							they are used in conjunction with metrics which are mapped in turn
						-->
						<xsl:when test="$property/type/text() = 'int'">
							<xsl:element name="integerScale">
								<xsl:choose>
									<xsl:when test="$property/unit/text() != 'NULL'">
										<xsl:apply-templates select="$property/unit" />
									</xsl:when>
								</xsl:choose>
							</xsl:element>
						</xsl:when>
						<xsl:when test="$property/type/text() = 'bool'">
							<xsl:element name="booleanScale">
								<xsl:choose>
									<xsl:when test="$property/unit/text() != 'NULL'">
										<xsl:apply-templates select="$property/unit" />
									</xsl:when>
								</xsl:choose>
							</xsl:element>
						</xsl:when>
						<xsl:when test="$property/type/text() = 'rational'">
							<xsl:element name="floatScale">
								<xsl:choose>
									<xsl:when test="$property/unit/text() != 'NULL'">
										<xsl:apply-templates select="$property/unit" />
									</xsl:when>
								</xsl:choose>
							</xsl:element>
		
						</xsl:when>
						<xsl:otherwise>
							<xsl:element name="freeStringScale">
								<xsl:choose>
									<xsl:when test="$property/unit/text() != 'NULL'">
										<xsl:apply-templates select="$property/unit" />
									</xsl:when>
								</xsl:choose>
							</xsl:element>
		
						</xsl:otherwise>
						</xsl:choose>
						
					<xsl:element name="possibleMetrics">
						<xsl:apply-templates select="$property/metrics/m" />
					</xsl:element>
					
					
					
				</xsl:element>
				
			</xsl:for-each>
		</xsl:element>
	</xsl:template>


	

	<xsl:template match="metrics/m">
		<xsl:element name="metric">
			<xsl:attribute name="metricId">
			<xsl:value-of select="mName/text()" />
		</xsl:attribute>
		</xsl:element>
	</xsl:template>

	<!--
		<property> <id>id15</id> <name>bitsPerSampleBlue</name>
		<description></description>
		<descriptionfilespec></descriptionfilespec> <unit>bit</unit>
		<type>int</type>
	-->
</xsl:stylesheet>

