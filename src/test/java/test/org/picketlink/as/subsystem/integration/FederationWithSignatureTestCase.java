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

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.OperateOnDeployment;
import org.jboss.arquillian.container.test.api.TargetsContainer;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Test;

/**
 * @author <a href="mailto:psilva@redhat.com">Pedro Silva</a>
 *
 */
public class FederationWithSignatureTestCase extends AbstractIntegrationTests {

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
}
