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
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;
import junit.framework.Assert;

import org.junit.Test;
import org.picketlink.as.subsystem.service.IdentityProviderService;
import org.picketlink.identity.federation.core.config.IDPConfiguration;
import org.picketlink.identity.federation.core.config.TrustType;

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
        
        assertEquals("idp.war", idpSubsystemConfig.getAlias());
        assertEquals("http://localhost:8080/idp/", idpSubsystemConfig.getIdentityURL());
        assertEquals("idp", idpSubsystemConfig.getSecurityDomain());
        assertFalse(idpSubsystemConfig.isSupportsSignature());
        assertTrue(idpSubsystemConfig.isStrictPostBinding());
        
        TrustType trustType = idpSubsystemConfig.getTrust();
        
        assertNotNull(trustType);
        assertNotNull(trustType.getDomains());
        Assert.assertEquals("localhost,mycompany.com2,mycompany.com3,mycompany.com4", trustType.getDomains());
        
        assertNotNull(identityProviderService.getPicketLinkType());
        assertNotNull(identityProviderService.getPicketLinkType().getStsType());
        
        assertEquals(identityProviderService.getPicketLinkType().getStsType().getTokenTimeout(), getFederationService().getSamlConfig().getTokenTimeout());
        assertEquals(identityProviderService.getPicketLinkType().getStsType().getClockSkew(), getFederationService().getSamlConfig().getClockSkew());
    }
    
}
