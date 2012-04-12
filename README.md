# PicketLink AS7 Subsystem Project #
 
This project is a AS7 Subsystem for the [PicketLink Project](http://www.jboss.org/picketlink "PicketLink Project").

For more information about this project, see this [thread](https://community.jboss.org/thread/196424 "PicketLink Subsystem Discussion Thread"). 

## How to build ##

To execute a simple(default) build run **mvn clean install**.

To execute a build running all unit tests run **mvn -Punit-tests clean install**. 

To execute a build running all integration tests run **mvn -Pintegration-tests clean install**.

To execute a release build run **mvn -Punit-tests,integration-tests,release clean install**. *(the release build process is still being defined)*

## How to install ##

Download and install [JBoss AS 7.1.0.Final](http://www.jboss.org/jbossas/downloads/ "JBoss AS7 Downloads").

*Make sure you have your environment configured with Apache Maven 3.*

Use **mvn clean package** to build the project.

Copy the contents of **target/module/org/picketlink/main** to **${jboss.home.dir}/modules/org/picketlink/main**.

Change your standalone.xml to add an extension for the PicketLink module:

          <extensions>
                    ...
                  <extension module="org.picketlink"/>
          </extensions>

Open the standalone.xml and add the following configuration for the PicketLink subsystem:

          <subsystem xmlns="urn:jboss:picketlink:1.0">
            <federation alias="acme-prod">
                <identity-provider alias="idp.war" url="http://localhost:8080/idp" signOutgoingMessages="false" ignoreIncomingSignatures="true">
                    <trust>
                        <trust-domain name="localhost"/>
                    </trust>
                </identity-provider>
                <service-providers>
                    <service-providers>
                        <service-provider alias="sales.war" url="http://localhost:8080/sales" post-binding="true" />
                    </service-providers>
                </service-providers>
            </federation>
        </subsystem>

The XML above is just a sample generated from the supported schema. You can access it from [here](https://github.com/picketlink/as-subsystem/blob/master/src/main/resources/schema/picketlink-subsystem.xsd).

## How to use ##
 
*The current version supports only a small set of the PicketLink configurations. Take a look at the schema for all available configurations.*
 
Take a look at our [Getting Started Guide] (https://docs.jboss.org/author/display/PLINK/Getting+Started).

Download the example web applications.

Extract the file and copy the idp.war and sales.war to ${jboss.home.dir}/standalone/deployments.

Open both files (idp.war and sales.war) and remove the following configuration files:

	- WEB-INF/context.xml
	- WEB-INF/jboss-web.xml: Remove the valve definitions. The subsystem will automatically configure this for you.
	- WEB-INF/picketlink-handlers.xml
	- WEB-INF/picketlink-idfed.xml

These files must be removed since they will be generated at runtime by the subsystem.

To make sure that everything is ok, please start the JBoss AS and try to access the sales application. You should be redirected to the idp application. If you want to log in the sales and idp applications, don't forget to configure the security domain for both. See [https://community.jboss.org/wiki/SAMLWebBrowserSSOOnJBossAS70](https://community.jboss.org/wiki/SAMLWebBrowserSSOOnJBossAS70).