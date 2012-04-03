package test.org.picketlink.as.subsystem.integration;

import java.net.URL;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.OperateOnDeployment;
import org.jboss.arquillian.container.test.api.TargetsContainer;
import org.jboss.arquillian.drone.api.annotation.Drone;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.thoughtworks.selenium.DefaultSelenium;

/**
 * <p>
 * Integration Test to verify if an IDP configured using the subsystem is working as expected. See
 * <b>src/test/resources/picketlink-subsystem.xml.</b>
 * </p>
 * 
 * @author <a href="mailto:psilva@redhat.com">Pedro Silva</a>
 * @since Mar 9, 2012
 */
@RunWith(Arquillian.class)
public class PicketLinkSubsystemIDPDeploymentTestCase {

    /**
     * Directory where the configuration files used to deploy the IDP are located.
     */
    private static final String DEPLOYMENT_ROOT_DIR = "deployment/idp";

    @Drone
    private DefaultSelenium browser;

    @ArquillianResource
    private URL deploymentURL;

    /**
     * Configures an IDP deployment.
     * 
     * @return
     */
    @Deployment(name = "idp", testable = false)
    @TargetsContainer("jboss-as7")
    public static WebArchive createDeployment() {
        return ShrinkWrap
                .create(WebArchive.class, "idp.war")
                .addAsManifestResource(DEPLOYMENT_ROOT_DIR + "/META-INF/jboss-deployment-structure.xml",
                        "jboss-deployment-structure.xml").setWebXML(DEPLOYMENT_ROOT_DIR + "/WEB-INF/web.xml")
                .addAsWebResource(DEPLOYMENT_ROOT_DIR + "/WEB-INF/jboss-web.xml", "WEB-INF/jboss-web.xml")
                .addAsWebResource(DEPLOYMENT_ROOT_DIR + "/jsp/login.jsp", "jsp/login.jsp")
                .addAsWebResource(DEPLOYMENT_ROOT_DIR + "/jsp/error.jsp", "jsp/error.jsp");
    }

    @Test
    @OperateOnDeployment("idp")
    public void testDeploy() {
        browser.open(deploymentURL.toString());

        Assert.assertTrue("IDP login page should be presented",
                browser.isElementPresent("xpath=//input[@type='submit' and @value='login']"));
    }

}
