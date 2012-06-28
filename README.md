# PicketLink AS7 Subsystem Project #
 
This project is a AS7 Subsystem for the [PicketLink Project](http://www.jboss.org/picketlink "PicketLink Project").

For more information about this project, see the [PicketLink AS7 Subsystem documentation](https://docs.jboss.org/author/display/PLINK/PicketLink+AS7+Subsystem "PicketLink AS7 Subsystem documentation"). 

## How to build ##

To execute a simple(default) build do: **mvn clean install**.

To execute a build running all unit tests do: **mvn -Punit-tests clean install**. 

To execute a build running all integration tests do: **mvn -Pintegration-tests clean install**. 

To release do:  **mvn -Punit-tests,integration-tests,release clean install**. *(the release build process is still being defined)*

## How to install ##

Download and install [JBoss AS 7.1.1.Final](http://www.jboss.org/jbossas/downloads/ "JBoss AS7 Downloads").

*Make sure you have your environment configured with Apache Maven 3.*

Use **mvn -Pinstall-as7 -Djboss.as.home={JBOSS_HOME} clean install** to build the project.

The above command should reconfigure the PicketLink module shipped with your JBoss AS7 installation with the latest configuration and libraries. Including the PicketLink libraries used by the subsystem.

Change your standalone.xml to add an extension for the PicketLink module:

          <extensions>
                    ...
                  <extension module="org.picketlink"/>
          </extensions>

Deploy your applications and use the subsystem [domain model] (https://github.com/picketlink/as-subsystem/blob/master/src/main/resources/schema/picketlink-subsystem.xsd) to configure them as PicketLink deployments.

## How to use ##
 
Please, follow the documentation at https://docs.jboss.org/author/display/PLINK/PicketLink+AS7+Subsystem.
