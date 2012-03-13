# PicketLink AS 7 Subsystem Project #

This project is a AS7 Subsystem for the PicketLink Project (http://www.jboss.org/picketlink).

For more informations about this project, see https://community.jboss.org/thread/196424. 

Be welcome to join this thread and contribute with it. 

*The PicketLink project team*

# How to install the subsystem  #

Have sure you have your environment configure with Apache Maven 3.

Use 'mvn clean package' to build the project.

Download and install JBoss AS 7.1.0.Final (http://www.jboss.org/jbossas/downloads/)

Copy the contents of target/module/org/picketlink/main to ${jboss.home.dir}/modules/org/picketlink/main

# How to use it  #
 
The current version supports only a small set of the PicketLink configurations.
The actual version of the schema supports only the configurations used in the idp.war and sales.war examples web applications.
 
First, download the examples applications from https://repository.jboss.org/nexus/content/groups/public/org/picketlink/picketlink-fed-webapps-as7-assembly/2.0.2.Final/picketlink-fed-webapps-as7-assembly-2.0.2.Final.zip.

Extract the file and copy the idp.war and sales.war to ${jboss.home.dir}/standalone/deployments.

Open both files (idp.war and sales.war) and remove the following configuration files:

	- WEB-INF/context.xml
	- WEB-INF/picketlink-handlers.xml
	- WEB-INF/picketlink-idfed.xml

This files must be removed since they will be genrated at runtime by the subsystem.

Open the standalone.xml and add the following configuration for the PicketLink subsystem:

<subsystem xmlns="urn:jboss:picketlink:1.0">
    <federation alias="my-fed">
        <identity-provider alias="idp.war" url="http://localhost:8080/idp" signOutgoingMessages="false" ignoreIncomingSignatures="true">
            <trust>
                <trust-domain name="localhost"/>
            </trust>
        </identity-provider>
        <service-providers>
            <service-providers>
                <service-provider alias="sales.war" url="http://localhost:8080/sales"/>
            </service-providers>
        </service-providers>
    </federation>
</subsystem>

Now, start the AS and try to use the applications.