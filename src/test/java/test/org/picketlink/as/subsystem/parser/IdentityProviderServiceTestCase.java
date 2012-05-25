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
package test.org.picketlink.as.subsystem.parser;

import junit.framework.Assert;

import org.jboss.dmr.ModelNode;
import org.jboss.msc.service.ServiceName;
import org.junit.Test;
import org.picketlink.as.subsystem.model.ModelElement;
import org.picketlink.as.subsystem.service.IdentityProviderService;
import org.picketlink.identity.federation.core.config.IDPConfiguration;

/**
 * @author <a href="mailto:psilva@redhat.com">Pedro Silva</a>
 * 
 */
public class IdentityProviderServiceTestCase extends AbstractPicketLinkSubsystemTestCase {

    /**
     * <p>
     * Tests if the Identity Provider services are properly installed.
     * </p>
     * 
     * @throws Exception
     */
    @Test
    public void testIdentityProviderServiceInstallation() throws Exception {
        Assert.assertNotNull(getIdentityProviderService());
    }

    /**
     * <p>
     * Tests if the PicketLink configurations for the Identity Provider were properly created.
     * </p>
     * 
     * @throws Exception
     */
    @Test
    public void testConfigureIdentityProvider() throws Exception {
        IdentityProviderService identityProviderService = getIdentityProviderService();

        IDPConfiguration idpSubsystemConfig = identityProviderService.getConfiguration();
        
        //TODO: update test case
    }
    
    private IdentityProviderService getIdentityProviderService() {
        ServiceName serviceName = IdentityProviderService.createServiceName(getIdentityProvider().asProperty().getName());

        return (IdentityProviderService) getInstalledService(serviceName).getValue();
    }

    /**
     * <p>
     * Returns a {@link ModelNode} instance for the configured Identity Provider.
     * </p>
     * 
     * @return
     */
    private ModelNode getIdentityProvider() {
        ModelNode federation = getFederationModel();

        return federation.get(federation.asProperty().getName(), ModelElement.IDENTITY_PROVIDER.getName());
    }

}
