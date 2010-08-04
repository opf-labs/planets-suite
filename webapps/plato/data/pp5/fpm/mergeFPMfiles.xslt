<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	version="2.0">

	<xsl:output method="xml" indent="yes" name="xml" />

	<xsl:template match="/">
		<xsl:for-each select="//file">
			<xsl:variable select="document(.)" name="contents" />
			<xsl:apply-templates select="$contents/*" />
		</xsl:for-each>
	</xsl:template>

	<xsl:template match="*|text()|@*">
		<xsl:copy>
			<xsl:apply-templates select="@*" />
			<xsl:apply-templates select="*|text()" />
		</xsl:copy>
	</xsl:template>
</xsl:stylesheet>

