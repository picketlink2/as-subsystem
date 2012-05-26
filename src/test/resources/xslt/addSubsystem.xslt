<?xml version="1.0" encoding="UTF-8"?>
<!-- XSLT file to add the a test configuration for the PicketLink Subsystem 
	to the standalone.xml of the JBoss AS7 installation. -->
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:as="urn:jboss:domain:1.2" 
	xmlns:pl="urn:jboss:picketlink:1.0"
	version="1.0">

	<xsl:output method="xml" indent="yes" />
	
	<!-- If the subsystem is already defined, remove it to configure it again. -->
	<xsl:template match="//as:profile/pl:subsystem"/>
	
	<xsl:template match="as:profile">
		<profile>
			<xsl:copy-of select="document('../picketlink-subsystem.xml')" />
			<xsl:apply-templates select="@* | *" />
		</profile>
	</xsl:template>

	<!-- Copy everything else. -->
	<xsl:template match="@*|node()">
		<xsl:copy>
			<xsl:apply-templates select="@*|node()" />
		</xsl:copy>
	</xsl:template>

</xsl:stylesheet>