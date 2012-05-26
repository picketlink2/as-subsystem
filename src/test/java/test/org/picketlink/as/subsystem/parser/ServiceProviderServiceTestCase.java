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

import static junit.framework.Assert.assertEquals;

import java.util.List;

import junit.framework.Assert;

import org.jboss.dmr.ModelNode;
import org.jboss.dmr.Property;
import org.junit.Test;
import org.picketlink.as.subsystem.model.ModelElement;
import org.picketlink.as.subsystem.service.ServiceProviderService;
import org.picketlink.identity.federation.core.config.SPConfiguration;

/**
 * @author <a href="mailto:psilva@redhat.com">Pedro Silva</a>
 *
 */
public class ServiceProviderServiceTestCase extends AbstractPicketLinkSubsystemTestCase {

    /**
     * <p>
     * Tests if the Service Provider services are properly installed.
     * </p>
     * 
     * @throws Exception
     */
    @Test
    public void testServiceProviderServiceInstallation() throws Exception {
        ModelNode serviceProviders = getFederationModel().get(ModelElement.SERVICE_PROVIDER.getName());

        List<Property> serviceProvidersList = serviceProviders.asPropertyList();

        for (Property property : serviceProvidersList) {
            Assert.assertNotNull(getInstalledService(ServiceProviderService.createServiceName(property.getName())));
        }
    }
    
    /**
     * <p>
     * Tests if the PicketLink configurations for the Identity Provider were properly created.
     * </p>
     * 
     * @throws Exception
     */
    @Test
    public void testConfigureServiceProvider() throws Exception {
        ModelNode serviceProviders = getFederationModel().get(ModelElement.SERVICE_PROVIDER.getName());

        List<Property> serviceProvidersList = serviceProviders.asPropertyList();

        for (Property property : serviceProvidersList) {
            ServiceProviderService serviceProviderService = (ServiceProviderService) getInstalledService(ServiceProviderService.createServiceName(property.getName())).getValue();
            SPConfiguration spSubsystemConfig = serviceProviderService.getConfiguration();
            
            assertEquals(getIdentityProviderService().getConfiguration().getIdentityURL(), spSubsystemConfig.getIdentityURL());
            
            assertEquals(property.getValue().get(ModelElement.COMMON_ALIAS.getName()).asString(), spSubsystemConfig.getAlias());
            assertEquals(property.getValue().get(ModelElement.COMMON_URL.getName()).asString(), spSubsystemConfig.getServiceURL());
            assertEquals(property.getValue().get(ModelElement.COMMON_SECURITY_DOMAIN.getName()).asString(), spSubsystemConfig.getSecurityDomain());
            assertEquals(property.getValue().get(ModelElement.SUPPORTS_SIGNATURES.getName()).asBoolean(), spSubsystemConfig.isSupportsSignature());
            assertEquals(property.getValue().get(ModelElement.SERVICE_PROVIDER_POST_BINDING.getName()).asBoolean(), spSubsystemConfig.isPostBinding());
        }
    }

}
