/*
 * JBoss, Home of Professional Open Source.
 * Copyright 2012, Red Hat, Inc., and individual contributors
 * as indicated by the @author tags. See the copyright.txt file in the
 * distribution for a full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package test.org.picketlink.as.subsystem.integration;

import java.net.URL;

import org.jboss.arquillian.drone.api.annotation.Drone;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Assert;
import org.junit.runner.RunWith;

import com.thoughtworks.selenium.DefaultSelenium;

/**
 * @author <a href="mailto:psilva@redhat.com">Pedro Silva</a>
 *
 */
@RunWith(Arquillian.class)
public class AbstractIntegrationTests {
    
    private static final String DEPLOYMENT_ROOT_DIR = "deployment";
    private static final String IDP_DEPLOYMENT_ROOT_DIR = DEPLOYMENT_ROOT_DIR + "/idp";
    private static final String SP_DEPLOYMENT_ROOT_DIR = DEPLOYMENT_ROOT_DIR + "/sp";

    
    @Drone DefaultSelenium browser;
    @ArquillianResource URL deploymentURL;
    
    /**
     * <p>
     * Creates an Identity Provider {@link WebArchive} instance. The contents used are located at <code>IDP_DEPLOYMENT_ROOT_DIR</code>
     * </p>
     * 
     * @param warName
     * @return
     */
    protected static WebArchive createIdentityProviderWebArchive(String warName) {
        return ShrinkWrap
                .create(WebArchive.class, warName).setWebXML(IDP_DEPLOYMENT_ROOT_DIR + "/WEB-INF/web.xml")
                .addAsWebResource(IDP_DEPLOYMENT_ROOT_DIR + "/WEB-INF/jboss-web.xml", "WEB-INF/jboss-web.xml")
                .addAsWebResource(IDP_DEPLOYMENT_ROOT_DIR + "/WEB-INF/classes/users.properties", "WEB-INF/classes/users.properties")
                .addAsWebResource(IDP_DEPLOYMENT_ROOT_DIR + "/WEB-INF/classes/roles.properties", "WEB-INF/classes/roles.properties")
                .addAsWebResource(DEPLOYMENT_ROOT_DIR + "/jbid_test_keystore.jks", "WEB-INF/classes/jbid_test_keystore.jks")
                .addAsWebResource(IDP_DEPLOYMENT_ROOT_DIR + "/jsp/login.jsp", "jsp/login.jsp")
                .addAsWebResource(IDP_DEPLOYMENT_ROOT_DIR + "/jsp/error.jsp", "jsp/error.jsp")
                .addAsWebResource(IDP_DEPLOYMENT_ROOT_DIR + "/index.jsp", "index.jsp");
    }

    /**
     * <p>
     * Creates a Service Provider {@link WebArchive} instance. The contents used are located at <code>SP_DEPLOYMENT_ROOT_DIR</code>
     * </p>
     * 
     * @param warName
     * @return
     */
    protected static WebArchive createServiceProviderWebArchive(String warName) {
        return ShrinkWrap
                .create(WebArchive.class, warName).setWebXML(IDP_DEPLOYMENT_ROOT_DIR + "/WEB-INF/web.xml")
                .addAsWebResource(SP_DEPLOYMENT_ROOT_DIR + "/WEB-INF/jboss-web.xml", "WEB-INF/jboss-web.xml")
                .addAsWebResource(DEPLOYMENT_ROOT_DIR + "/jbid_test_keystore.jks", "WEB-INF/classes/jbid_test_keystore.jks")
                .addAsWebResource(SP_DEPLOYMENT_ROOT_DIR + "/index.jsp", "index.jsp")
                .addAsWebResource(SP_DEPLOYMENT_ROOT_DIR + "/customErrorPage.jsp", "customErrorPage.jsp")
                .addAsWebResource(SP_DEPLOYMENT_ROOT_DIR + "/logout.jsp", "logout.jsp");
    }

    /**
     * <p>
     * Asserts a successful login. Try to login at the Identity Provider and asserts if the welcome page is displayed.
     * After that try to logout.
     * </p>
     * 
     * @throws InterruptedException
     */
    protected void assertLoginAndLogout() throws InterruptedException {
        login();
    
        Assert.assertTrue("Service Provider welcomePage page should be presented",
                browser.isElementPresent("xpath=//h1[@id='welcomePage']"));
        
        logout();
        
        Thread.sleep(2000l);
    }

    private void logout() {
        if (browser.isElementPresent("xpath=//h1[@id='welcomePage']")) {
            browser.click("id=logoutLink");
        }
    }

    /**
     * <p>
     * Try to login at the Identity Provider and asserts if the welcome page is displayed.
     * </p>
     * 
     * @throws InterruptedException
     */
    protected void login() throws InterruptedException {
        browser.open(deploymentURL.toString());
        
        Thread.sleep(2000l);
        
        Assert.assertTrue("IDP login page should be presented",
                browser.isElementPresent("xpath=//input[@type='submit' and @value='login']"));
        
        browser.type("id=usernameText", "tomcat");
        browser.type("id=passwordText", "tomcat");
        browser.click("id=loginButton");
        
        Thread.sleep(2000l);
    }

}