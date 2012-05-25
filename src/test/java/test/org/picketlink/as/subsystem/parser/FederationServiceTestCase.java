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

import java.util.List;

import junit.framework.Assert;

import org.jboss.dmr.ModelNode;
import org.junit.Test;
import org.picketlink.as.subsystem.model.ModelElement;
import org.picketlink.as.subsystem.service.FederationService;
import org.picketlink.identity.federation.core.config.AuthPropertyType;
import org.picketlink.identity.federation.core.impl.KeyStoreKeyManager;

/**
 * @author <a href="mailto:psilva@redhat.com">Pedro Silva</a>
 * 
 */
public class FederationServiceTestCase extends AbstractPicketLinkSubsystemTestCase {

    /**
     * <p>
     * Tests if the Federation services are properly installed.
     * </p>
     * 
     * @throws Exception
     */
    @Test
    public void testIdentityProviderServiceInstallation() throws Exception {
        Assert.assertNotNull(getFederationService());
    }

    /**
     * <p>
     * Tests if the PicketLink configurations for the Federation were properly created.
     * </p>
     * 
     * @throws Exception
     */
    @Test
    public void testConfigureFederationService() throws Exception {
        FederationService federationService = getFederationService();

        assertNotNull(federationService);
        assertNotNull(federationService.getIdentityProviderService());
        assertNotNull(federationService.getKeyProvider());
        assertNotNull(federationService.getSamlConfig());
        
    }

    /**
     * <p>
     * Tests if the KeyProvider configurations were properly created.
     * </p>
     * 
     * @throws Exception
     */
    @Test
    public void testKeyProviderConfigurations() throws Exception {
        ModelNode keyStoreNode = getFederationModel().asProperty().getValue().get(ModelElement.KEY_STORE.getName()).asProperty().getValue();
        
        assertTrue(keyStoreNode.isDefined());
        
        assertEquals(keyStoreNode.get(ModelElement.KEY_STORE_PASSWD.getName()).asString(), getKeyStoreAttribute(KeyStoreKeyManager.KEYSTORE_PASS));
        assertEquals(keyStoreNode.get(ModelElement.KEY_STORE_SIGN_KEY_ALIAS.getName()).asString(), getKeyStoreAttribute(KeyStoreKeyManager.SIGNING_KEY_ALIAS));
        assertEquals(keyStoreNode.get(ModelElement.KEY_STORE_SIGN_KEY_PASSWD.getName()).asString(), getKeyStoreAttribute(KeyStoreKeyManager.SIGNING_KEY_PASS));
        assertEquals(keyStoreNode.get(ModelElement.COMMON_URL.getName()).asString(), getKeyStoreAttribute(KeyStoreKeyManager.KEYSTORE_URL));
    }
    
    /**
     * <p>
     * Tests if the KeyProvider configurations were properly created.
     * </p>
     * 
     * @throws Exception
     */
    @Test
    public void testSAMLConfigurations() throws Exception {
        ModelNode keyStoreNode = getFederationModel().asProperty().getValue().get(ModelElement.SAML.getName()).asProperty().getValue();
        
        assertTrue(keyStoreNode.isDefined());
        
        assertEquals(keyStoreNode.get(ModelElement.SAML_TOKEN_TIMEOUT.getName()).asInt(), getFederationService().getSamlConfig().getTokenTimeout());
        assertEquals(keyStoreNode.get(ModelElement.SAML_CLOCK_SKEW.getName()).asInt(), getFederationService().getSamlConfig().getClockSkew());
    }

    private String getKeyStoreAttribute(String authKey) {
        List<AuthPropertyType> auth = getFederationService().getKeyProvider().getAuth();
        String value = null;
        
        for (AuthPropertyType authPropertyType : auth) {
            if (authPropertyType.getKey().equals(authKey)) {
                value = authPropertyType.getValue();
                break;
            }
        }
        return value;
    }

}
