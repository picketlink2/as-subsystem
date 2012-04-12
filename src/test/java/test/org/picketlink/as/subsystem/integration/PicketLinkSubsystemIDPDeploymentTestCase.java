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
import org.openqa.selenium.firefox.FirefoxDriver;

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

    private static final String DEPLOYMENT_ROOT_DIR = "deployment";
    private static final String IDP_DEPLOYMENT_ROOT_DIR = DEPLOYMENT_ROOT_DIR + "/idp";
    private static final String SP_DEPLOYMENT_ROOT_DIR = DEPLOYMENT_ROOT_DIR + "/sp";

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
    public static WebArchive createIDPDeployment() {
        return ShrinkWrap
                .create(WebArchive.class, "idp.war")
                .addAsManifestResource(IDP_DEPLOYMENT_ROOT_DIR + "/META-INF/jboss-deployment-structure.xml",
                        "jboss-deployment-structure.xml").setWebXML(IDP_DEPLOYMENT_ROOT_DIR + "/WEB-INF/web.xml")
                .addAsWebResource(IDP_DEPLOYMENT_ROOT_DIR + "/WEB-INF/jboss-web.xml", "WEB-INF/jboss-web.xml")
                .addAsWebResource(IDP_DEPLOYMENT_ROOT_DIR + "/WEB-INF/classes/users.properties", "WEB-INF/classes/users.properties")
                .addAsWebResource(IDP_DEPLOYMENT_ROOT_DIR + "/WEB-INF/classes/roles.properties", "WEB-INF/classes/roles.properties")
                .addAsWebResource(DEPLOYMENT_ROOT_DIR + "/jbid_test_keystore.jks", "WEB-INF/classes/jbid_test_keystore.jks")
                .addAsWebResource(IDP_DEPLOYMENT_ROOT_DIR + "/jsp/login.jsp", "jsp/login.jsp")
                .addAsWebResource(IDP_DEPLOYMENT_ROOT_DIR + "/jsp/error.jsp", "jsp/error.jsp");
    }

    /**
     * Configures an IDP deployment.
     * 
     * @return
     */
    @Deployment(name = "sales", testable = false)
    @TargetsContainer("jboss-as7")
    public static WebArchive createSalesDeployment() {
        return ShrinkWrap
                .create(WebArchive.class, "sales.war")
                .addAsManifestResource(SP_DEPLOYMENT_ROOT_DIR + "/META-INF/jboss-deployment-structure.xml",
                        "jboss-deployment-structure.xml")
                 .setWebXML(IDP_DEPLOYMENT_ROOT_DIR + "/WEB-INF/web.xml")
                .addAsWebResource(SP_DEPLOYMENT_ROOT_DIR + "/WEB-INF/jboss-web.xml", "WEB-INF/jboss-web.xml")
                .addAsWebResource(DEPLOYMENT_ROOT_DIR + "/jbid_test_keystore.jks", "WEB-INF/classes/jbid_test_keystore.jks")
                .addAsWebResource(SP_DEPLOYMENT_ROOT_DIR + "/index.jsp", "index.jsp");
    }

    @Test
    @OperateOnDeployment("sales")
    public void testDeploy() throws InterruptedException {
        browser.open(deploymentURL.toString());
        
        Thread.sleep(5000l);
        
        Assert.assertTrue("IDP login page should be presented",
                browser.isElementPresent("xpath=//input[@type='submit' and @value='login']"));
        
        browser.type("id=usernameText", "tomcat");
        browser.type("id=passwordText", "tomcat");
        browser.click("id=loginButton");
        
        Thread.sleep(5000l);

        Assert.assertTrue("Service Provider welcomePage page should be presented",
                browser.isElementPresent("xpath=//h1[@id='welcomePage']"));
    }

}
