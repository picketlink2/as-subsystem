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

package org.picketlink.as.subsystem.model;

import static org.picketlink.as.subsystem.model.ModelElement.COMMON_ALIAS;
import static org.picketlink.as.subsystem.model.ModelElement.COMMON_ENDPOINT;
import static org.picketlink.as.subsystem.model.ModelElement.COMMON_SECURITY_DOMAIN;
import static org.picketlink.as.subsystem.model.ModelElement.COMMON_URL;
import static org.picketlink.as.subsystem.model.ModelElement.IDENTITY_PROVIDER_IGNORE_INCOMING_SIGNATURES;
import static org.picketlink.as.subsystem.model.ModelElement.IDENTITY_PROVIDER_SIGN_OUTGOING_MESSAGES;

import org.jboss.as.controller.PathAddress;
import org.jboss.as.controller.descriptions.ModelDescriptionConstants;
import org.jboss.dmr.ModelNode;
import org.picketlink.identity.federation.core.config.AuthPropertyType;
import org.picketlink.identity.federation.core.config.KeyProviderType;
import org.picketlink.identity.federation.core.config.parser.IDPTypeSubsystem;
import org.picketlink.identity.federation.core.config.parser.SPTypeSubsystem;
import org.picketlink.identity.federation.core.config.parser.STSTypeSubsystem;

/**
 * <p>
 * Utility methods for the PicketLink Subsystem's model.
 * </p>
 * 
 * @author <a href="mailto:psilva@redhat.com">Pedro Silva</a>
 *
 */
public class ModelUtils {

    /**
     * <p>
     * Extract from the ${@ModelNode} instance the federation's alias attribute value.
     * </p>
     * 
     * @param fromModel
     * @return
     */
    public static String getFederationAlias(ModelNode fromModel) {
        return fromModel.get(ModelDescriptionConstants.ADDRESS).asPropertyList().get(1).getValue().asString();
    }
    
    public static final STSTypeSubsystem toSTSType(ModelNode fromModel) {
        String alias = PathAddress.pathAddress(fromModel.get(ModelDescriptionConstants.ADDRESS)).getLastElement().getValue();
        String endpoint = fromModel.get(COMMON_ENDPOINT.getName()).asString();
        String securityDomain = fromModel.get(COMMON_SECURITY_DOMAIN.getName()).asString();

        STSTypeSubsystem stsType = new STSTypeSubsystem();
        
        stsType.setAlias(alias);
        stsType.setEndpoint(endpoint);
        stsType.setSecurityDomain(securityDomain);
        
        return stsType;
    }
    
    public static SPTypeSubsystem toSPType(ModelNode fromModel) {
        String alias = fromModel.get(ModelElement.COMMON_ALIAS.getName()).asString();
        String url = fromModel.get(ModelElement.COMMON_URL.getName()).asString();
        String securityDomain = fromModel.get(ModelElement.COMMON_SECURITY_DOMAIN.getName()).asString();
        boolean postBinding = fromModel.get(ModelElement.SERVICE_PROVIDER_POST_BINDING.getName()).asBoolean();
        
        SPTypeSubsystem spType = new SPTypeSubsystem();

        spType.setAlias(alias);
        spType.setPostBinding(postBinding);
        spType.setSecurityDomain(securityDomain);
        spType.setServiceURL(url);
        
        return spType;
    }
    
    /**
     * <p>
     *  Converts a {@ModelNode} instance to a {@IDPTypeSubsystem} instance. 
     * </p>
     * 
     * @param model
     * @return
     */
    public static IDPTypeSubsystem toIDPType(ModelNode fromModel) {
        IDPTypeSubsystem idpType = new IDPTypeSubsystem();
        
        String alias = fromModel.get(COMMON_ALIAS.getName()).asString();
        String url = fromModel.get(COMMON_URL.getName()).asString();
        boolean signOutgoingMessages = fromModel.get(IDENTITY_PROVIDER_SIGN_OUTGOING_MESSAGES.getName()).asBoolean();
        boolean ignoreIncomingSignatures = fromModel.get(IDENTITY_PROVIDER_IGNORE_INCOMING_SIGNATURES.getName()).asBoolean();
        String securityDomain = fromModel.get(COMMON_SECURITY_DOMAIN.getName()).asString();
        
        idpType.setIdentityURL(url);
        idpType.setSignOutgoingMessages(signOutgoingMessages);
        idpType.setIgnoreIncomingSignatures(ignoreIncomingSignatures);
        idpType.setSecurityDomain(securityDomain);
        
        return idpType;
    }
    
    /**
     * <p>
     *  Converts a {@ModelNode} instance to a {@KeyProviderType} instance. 
     * </p>
     * 
     * @param model
     * @return
     */
    public static KeyProviderType toKeyProviderType(ModelNode model) {
        KeyProviderType keyProviderType = new KeyProviderType();
        
        keyProviderType.setSigningAlias(model.get(ModelElement.KEY_STORE_SIGN_KEY_ALIAS.getName()).asString());
        
        AuthPropertyType keyStoreURL = new AuthPropertyType();
        
        keyStoreURL.setKey("KeyStoreURL");
        keyStoreURL.setValue(model.get(ModelElement.COMMON_URL.getName()).asString());
        
        keyProviderType.add(keyStoreURL);
        
        AuthPropertyType keyStorePass = new AuthPropertyType();

        keyStorePass.setKey("KeyStorePass");
        keyStorePass.setValue(model.get(ModelElement.KEY_STORE_PASSWD.getName()).asString());

        keyProviderType.add(keyStorePass);
        
        AuthPropertyType signingKeyPass = new AuthPropertyType();

        signingKeyPass.setKey("SigningKeyPass");
        signingKeyPass.setValue(model.get(ModelElement.KEY_STORE_SIGN_KEY_PASSWD.getName()).asString());

        keyProviderType.add(signingKeyPass);

        AuthPropertyType signingKeyAlias = new AuthPropertyType();

        signingKeyAlias.setKey("SigningKeyAlias");
        signingKeyAlias.setValue(model.get(ModelElement.KEY_STORE_SIGN_KEY_ALIAS.getName()).asString());

        keyProviderType.add(signingKeyAlias);
        
        return keyProviderType;
    }
}
