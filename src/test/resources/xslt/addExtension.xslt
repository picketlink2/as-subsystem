<?xml version="1.0" encoding="UTF-8"?>
<!-- 
	XSLT file to add the a the PicketLink Extension to the standalone.xml of the JBoss AS7 installation.
 -->
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:as="urn:jboss:domain:1.3"
	version="1.0">

	<xsl:output method="xml" indent="yes" />
	
	<!-- If the extension is already defined, remove it to configure it again. -->
	<xsl:template match="//as:extensions/as:extension[@module='org.picketlink']"/>
	
	<xsl:template match="as:extensions">
		<extensions>
			<xsl:apply-templates select="@* | *"/>
			<extension module="org.picketlink"/>
		</extensions>
	</xsl:template>

	<!-- Copy everything else. -->
	<xsl:template match="@*|node()">
		<xsl:copy>
			<xsl:apply-templates select="@*|node()" />
		</xsl:copy>
	</xsl:template>

</xsl:stylesheet>