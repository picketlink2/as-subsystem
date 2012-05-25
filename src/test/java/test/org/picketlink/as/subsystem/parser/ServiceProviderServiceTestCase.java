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
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;

import java.io.File;
import java.io.FileInputStream;
import java.util.List;

import junit.framework.Assert;

import org.jboss.dmr.ModelNode;
import org.jboss.dmr.Property;
import org.junit.Test;
import org.picketlink.as.subsystem.model.ModelElement;
import org.picketlink.as.subsystem.service.ServiceProviderService;
import org.picketlink.identity.federation.core.config.AuthPropertyType;
import org.picketlink.identity.federation.core.config.KeyValueType;
import org.picketlink.identity.federation.core.config.PicketLinkType;
import org.picketlink.identity.federation.core.config.SPConfiguration;
import org.picketlink.identity.federation.core.config.SPType;
import org.picketlink.identity.federation.core.config.parser.SPTypeConfigWriter;
import org.picketlink.identity.federation.core.parsers.config.PicketLinkConfigParser;

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
        ModelNode federation = getFederationModel();

        ModelNode serviceProviders = federation.get(federation.asProperty().getName(), ModelElement.SERVICE_PROVIDER.getName());

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
        ModelNode federation = getFederationModel();

        ModelNode serviceProviders = federation.get(federation.asProperty().getName(), ModelElement.SERVICE_PROVIDER.getName());

        List<Property> serviceProvidersList = serviceProviders.asPropertyList();

        for (Property property : serviceProvidersList) {
            ServiceProviderService serviceProviderService = (ServiceProviderService) getInstalledService(ServiceProviderService.createServiceName(property.getName())).getValue();
            SPConfiguration spSubsystemConfig = serviceProviderService.getConfiguration();

            // write the subsystem config into a picketlink-idp.xml file.
            SPTypeConfigWriter idpTypeConfigWriter = new SPTypeConfigWriter(spSubsystemConfig);

            File picketlinkSPConfig = new File("target/picketlink-sp-" + spSubsystemConfig.getAlias() + ".xml");

            picketlinkSPConfig.createNewFile();

            idpTypeConfigWriter.write(picketlinkSPConfig);

            // try to parse the generated config using the PicketLink parsers
            PicketLinkConfigParser configParser = new PicketLinkConfigParser();

            PicketLinkType resultingConfig = (PicketLinkType) configParser.parse(new FileInputStream(picketlinkSPConfig));

            assertNotNull(resultingConfig);
            assertTrue(resultingConfig.getIdpOrSP() instanceof SPType);
            
            SPType spParsedType = (SPType) resultingConfig.getIdpOrSP();
            
            assertEquals(spSubsystemConfig.getIdentityURL(), spParsedType.getIdentityURL());
            assertEquals(spSubsystemConfig.getServiceURL(), spParsedType.getServiceURL());
            
            assertNotNull(spParsedType.getKeyProvider());
            
            List<AuthPropertyType> auth = spSubsystemConfig.getKeyProvider().getAuth();
            
            for (AuthPropertyType authPropertyType : auth) {
                boolean isDefined = false;
                
                for (AuthPropertyType authParsedPropertyType : spParsedType.getKeyProvider().getAuth()) {
                    if (authPropertyType.getKey().equals(authParsedPropertyType.getKey())) {
                        assertEquals(authPropertyType.getValue(), authParsedPropertyType.getValue());
                        isDefined = true;
                        break;
                    }
                }
                
                assertTrue(isDefined); 
            }
            
            
            KeyValueType spSubsystemValidationAlias = spSubsystemConfig.getKeyProvider().getValidatingAlias().get(0);
            KeyValueType spParsedValidationAlias = spParsedType.getKeyProvider().getValidatingAlias().get(0);
            
            assertEquals(spSubsystemValidationAlias.getKey(), spParsedValidationAlias.getKey());
            assertEquals(spSubsystemValidationAlias.getValue(), spParsedValidationAlias.getValue());
        }
    }

}
