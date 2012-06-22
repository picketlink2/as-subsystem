<?xml version="1.0" encoding="UTF-8"?>
<!-- XSLT file to add the security domains to the standalone.xml used during 
	the integration tests. -->
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:as="urn:jboss:domain:1.2" xmlns:log="urn:jboss:domain:logging:1.1"
	version="1.0">

	<xsl:output method="xml" indent="yes" />

	<xsl:template match="//as:profile/log:subsystem" />

	<xsl:template match="as:profile/log:subsystem">
		<subsystem xmlns="urn:jboss:domain:logging:1.1">
			<periodic-rotating-file-handler name="PICKETLINK">
				<file relative-to="jboss.server.log.dir" path="picketlink.log" />
				<suffix value=".yyyy-MM-dd" />
				<append value="true" />
			</periodic-rotating-file-handler>
			<logger
				category="org.picketlink.identity.federation.audit">
				<level name="INFO" />
				<handlers>
					<handler name="PICKETLINK" />
				</handlers>
			</logger>
			<xsl:apply-templates select="@* | *" />
		</subsystem>
	</xsl:template>

	<!-- Copy everything else. -->
	<xsl:template match="@*|node()">
		<xsl:copy>
			<xsl:apply-templates select="@*|node()" />
		</xsl:copy>
	</xsl:template>

</xsl:stylesheet>