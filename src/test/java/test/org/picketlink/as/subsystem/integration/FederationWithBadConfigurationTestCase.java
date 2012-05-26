package test.org.picketlink.as.subsystem.integration;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.OperateOnDeployment;
import org.jboss.arquillian.container.test.api.TargetsContainer;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Assert;
import org.junit.Test;

/**
 * <p>
 * Integration Test to verify if an IDP configured using the subsystem is working as expected. See
 * <b>src/test/resources/picketlink-subsystem.xml.</b>
 * </p>
 * 
 * @author <a href="mailto:psilva@redhat.com">Pedro Silva</a>
 * @since Mar 9, 2012
 */
public class FederationWithBadConfigurationTestCase extends AbstractIntegrationTests {

    /**
     * Configures an IDP deployment.
     * 
     * @return
     */
    @Deployment(name = "idp-bad", testable = false)
    @TargetsContainer("jboss-as7")
    public static WebArchive createIDPBadDeployment() {
        return createIdentityProviderWebArchive("idp-bad.war");
    }

    /**
     * Configures an IDP deployment.
     * 
     * @return
     */
    @Deployment(name = "sales-bad", testable = false)
    @TargetsContainer("jboss-as7")
    public static WebArchive createSalesBadDeployment() {
        return createServiceProviderWebArchive("sales-bad.war");
    }
    
    @Test
    @OperateOnDeployment("sales-bad")
    public void testSalesBad() throws InterruptedException {
        login();
        
        Assert.assertTrue("Custom Error Page",
                browser.isElementPresent("xpath=//h1[@id='customErrorPage']"));

    }

}
