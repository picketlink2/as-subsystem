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
        return createIdentityProviderWebArchive("idp.war");
    }

    /**
     * Configures an IDP deployment.
     * 
     * @return
     */
    @Deployment(name = "idp-sig", testable = false)
    @TargetsContainer("jboss-as7")
    public static WebArchive createIDPSigDeployment() {
        return createIdentityProviderWebArchive("idp-sig.war");
    }

    private static WebArchive createIdentityProviderWebArchive(String warName) {
        return ShrinkWrap
                .create(WebArchive.class, warName)
                .addAsManifestResource(IDP_DEPLOYMENT_ROOT_DIR + "/META-INF/jboss-deployment-structure.xml",
                        "jboss-deployment-structure.xml").setWebXML(IDP_DEPLOYMENT_ROOT_DIR + "/WEB-INF/web.xml")
                .addAsWebResource(IDP_DEPLOYMENT_ROOT_DIR + "/WEB-INF/jboss-web.xml", "WEB-INF/jboss-web.xml")
                .addAsWebResource(IDP_DEPLOYMENT_ROOT_DIR + "/WEB-INF/classes/users.properties", "WEB-INF/classes/users.properties")
                .addAsWebResource(IDP_DEPLOYMENT_ROOT_DIR + "/WEB-INF/classes/roles.properties", "WEB-INF/classes/roles.properties")
                .addAsWebResource(DEPLOYMENT_ROOT_DIR + "/jbid_test_keystore.jks", "WEB-INF/classes/jbid_test_keystore.jks")
                .addAsWebResource(IDP_DEPLOYMENT_ROOT_DIR + "/jsp/login.jsp", "jsp/login.jsp")
                .addAsWebResource(IDP_DEPLOYMENT_ROOT_DIR + "/jsp/error.jsp", "jsp/error.jsp")
                .addAsWebResource(IDP_DEPLOYMENT_ROOT_DIR + "/index.jsp", "index.jsp");
    }

    /**
     * Configures an IDP deployment.
     * 
     * @return
     */
    @Deployment(name = "sales-redirect", testable = false)
    @TargetsContainer("jboss-as7")
    public static WebArchive createSalesRedirectDeployment() {
        return createServiceProviderWebArchive("sales-redirect.war");
    }

    /**
     * Configures an IDP deployment.
     * 
     * @return
     */
    @Deployment(name = "sales-post", testable = false)
    @TargetsContainer("jboss-as7")
    public static WebArchive createSalesPostDeployment() {
        return createServiceProviderWebArchive("sales-post.war");
    }
    
    @Deployment(name = "sales-post-sig", testable = false)
    @TargetsContainer("jboss-as7")
    public static WebArchive createSalesPostSigDeployment() {
        return createServiceProviderWebArchive("sales-post-sig.war");
    }

    @Deployment(name = "sales-redirect-sig", testable = false)
    @TargetsContainer("jboss-as7")
    public static WebArchive createSalesRedirectSigDeployment() {
        return createServiceProviderWebArchive("sales-redirect-sig.war");
    }

    private static WebArchive createServiceProviderWebArchive(String warName) {
        return ShrinkWrap
                .create(WebArchive.class, warName)
                .addAsManifestResource(SP_DEPLOYMENT_ROOT_DIR + "/META-INF/jboss-deployment-structure.xml",
                        "jboss-deployment-structure.xml")
                 .setWebXML(IDP_DEPLOYMENT_ROOT_DIR + "/WEB-INF/web.xml")
                .addAsWebResource(SP_DEPLOYMENT_ROOT_DIR + "/WEB-INF/jboss-web.xml", "WEB-INF/jboss-web.xml")
                .addAsWebResource(DEPLOYMENT_ROOT_DIR + "/jbid_test_keystore.jks", "WEB-INF/classes/jbid_test_keystore.jks")
                .addAsWebResource(SP_DEPLOYMENT_ROOT_DIR + "/index.jsp", "index.jsp")
                .addAsWebResource(SP_DEPLOYMENT_ROOT_DIR + "/logout.jsp", "logout.jsp");
    }

    @Test
    @OperateOnDeployment("sales-post")
    public void testSalesPost() throws InterruptedException {
        assertLoginAndLogout();
    }

    @Test
    @OperateOnDeployment("sales-post-sig")
    public void testSalesPostSig() throws InterruptedException {
        assertLoginAndLogout();
    }

    @Test
    @OperateOnDeployment("sales-redirect-sig")
    public void testSalesRedirectSig() throws InterruptedException {
        assertLoginAndLogout();
    }

    @Test
    @OperateOnDeployment("sales-redirect")
    public void testSalesRedirect() throws InterruptedException {
        assertLoginAndLogout();
    }

    private void assertLoginAndLogout() throws InterruptedException {
        browser.open(deploymentURL.toString());
        
        Thread.sleep(2000l);
        
        Assert.assertTrue("IDP login page should be presented",
                browser.isElementPresent("xpath=//input[@type='submit' and @value='login']"));
        
        browser.type("id=usernameText", "tomcat");
        browser.type("id=passwordText", "tomcat");
        browser.click("id=loginButton");
        
        Thread.sleep(2000l);

        Assert.assertTrue("Service Provider welcomePage page should be presented",
                browser.isElementPresent("xpath=//h1[@id='welcomePage']"));
        
        browser.click("id=logoutLink");
        
        Thread.sleep(2000l);
    }

}
