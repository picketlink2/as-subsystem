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
import org.jboss.msc.service.ServiceName;
import org.junit.Test;
import org.picketlink.as.subsystem.model.ModelElement;
import org.picketlink.as.subsystem.service.IdentityProviderService;
import org.picketlink.identity.federation.core.config.AuthPropertyType;
import org.picketlink.identity.federation.core.config.IDPConfiguration;
import org.picketlink.identity.federation.core.config.IDPType;
import org.picketlink.identity.federation.core.config.KeyValueType;
import org.picketlink.identity.federation.core.config.PicketLinkType;
import org.picketlink.identity.federation.core.config.parser.IDPTypeConfigWriter;
import org.picketlink.identity.federation.core.parsers.config.PicketLinkConfigParser;

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

        // write the subsystem config into a picketlink-idp.xml file.
        IDPTypeConfigWriter idpTypeConfigWriter = new IDPTypeConfigWriter(idpSubsystemConfig);

        File picketlinkIDPConfig = new File("target/picketlink-idp.xml");

        picketlinkIDPConfig.createNewFile();

        idpTypeConfigWriter.write(picketlinkIDPConfig);

        // try to parse the generated config using the PicketLink parsers
        PicketLinkConfigParser configParser = new PicketLinkConfigParser();

        PicketLinkType resultingConfig = (PicketLinkType) configParser.parse(new FileInputStream(picketlinkIDPConfig));

        assertNotNull(resultingConfig);
        assertTrue(resultingConfig.getIdpOrSP() instanceof IDPType);
        
        IDPType idpParsedType = (IDPType) resultingConfig.getIdpOrSP();
        
        assertEquals(idpSubsystemConfig.getIdentityURL(), idpParsedType.getIdentityURL());
        assertEquals(idpSubsystemConfig.getTrust().getDomains().trim(), idpParsedType.getTrust().getDomains().trim());
        
        assertNotNull(idpParsedType.getKeyProvider());
        
        List<AuthPropertyType> auth = idpSubsystemConfig.getKeyProvider().getAuth();
        
        for (AuthPropertyType authPropertyType : auth) {
            boolean isDefined = false;
            
            for (AuthPropertyType authParsedPropertyType : idpParsedType.getKeyProvider().getAuth()) {
                if (authPropertyType.getKey().equals(authParsedPropertyType.getKey())) {
                    assertEquals(authPropertyType.getValue(), authParsedPropertyType.getValue());
                    isDefined = true;
                    break;
                }
            }
            
            assertTrue(isDefined); 
        }
        
        List<KeyValueType> validatingAlias = idpSubsystemConfig.getKeyProvider().getValidatingAlias();
        
        for (KeyValueType keyValueType : validatingAlias) {
            boolean isDefined = false;
            
            for (KeyValueType keyParsedValueType : idpParsedType.getKeyProvider().getValidatingAlias()) {
                if (keyValueType.getKey().equals(keyParsedValueType.getKey())) {
                    assertEquals(keyParsedValueType.getValue(), keyParsedValueType.getValue());
                    isDefined = true;
                    break;
                }
            }
            
            assertTrue(isDefined); 
        }
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
