# PicketLink AS 7 Subsystem Project #

This project is a AS7 Subsystem for the PicketLink Project (http://www.jboss.org/picketlink).

For more informations about this project, see https://community.jboss.org/thread/196424. 

Be welcome to join this thread and contribute with it. 

*The PicketLink project team*

# How to install the subsystem  #

Make sure you have your environment configure with Apache Maven 3.

Use 'mvn clean package' to build the project.

Download and install JBoss AS 7.1.0.Final (http://www.jboss.org/jbossas/downloads/)

Copy the contents of target/module/org/picketlink/main to ${jboss.home.dir}/modules/org/picketlink/main

# How to use it  #
 
The current version supports only a small set of the PicketLink configurations.
The actual version of the schema supports only the configurations used in the idp.war and sales.war web applications.
 
First, download the web applications from https://repository.jboss.org/nexus/content/groups/public/org/picketlink/picketlink-fed-webapps-as7-assembly/2.0.2.Final/picketlink-fed-webapps-as7-assembly-2.0.2.Final.zip.

Extract the file and copy the idp.war and sales.war to ${jboss.home.dir}/standalone/deployments.

Open both files (idp.war and sales.war) and remove the following configuration files:

	- WEB-INF/context.xml
	- WEB-INF/picketlink-handlers.xml
	- WEB-INF/picketlink-idfed.xml

This files must be removed since they will be generated at runtime by the subsystem.

Open the standalone.xml and add the following configuration for the PicketLink subsystem:

&lt;subsystem xmlns=&quot;urn:jboss:picketlink:1.0&quot;&gt;<br/>
    &lt;federation alias=&quot;my-fed&quot;&gt;<br/>
        &lt;identity-provider alias=&quot;idp.war&quot; url=&quot;http://localhost:8080/idp&quot; signOutgoingMessages=&quot;false&quot; ignoreIncomingSignatures=&quot;true&quot;&gt;<br/>
            &lt;trust&gt;<br/>
                &lt;trust-domain name=&quot;localhost&quot;/&gt;<br/>
            &lt;/trust&gt;<br/>
        &lt;/identity-provider&gt;<br/>
        &lt;service-providers&gt;<br/>
            &lt;service-providers&gt;<br/>
                &lt;service-provider alias=&quot;sales.war&quot; url=&quot;http://localhost:8080/sales&quot;/&gt;<br/>
            &lt;/service-providers&gt;<br/>
        &lt;/service-providers&gt;<br/>
    &lt;/federation&gt;<br/>
&lt;/subsystem&gt;<br/>

Now, start the AS and try to use the applications.