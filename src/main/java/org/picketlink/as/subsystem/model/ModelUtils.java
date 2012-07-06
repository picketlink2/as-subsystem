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
import static org.picketlink.as.subsystem.model.ModelElement.COMMON_SECURITY_DOMAIN;
import static org.picketlink.as.subsystem.model.ModelElement.COMMON_URL;
import static org.picketlink.as.subsystem.model.ModelElement.IDENTITY_PROVIDER_ENCRYPT;
import static org.picketlink.as.subsystem.model.ModelElement.SERVICE_PROVIDER_ERROR_PAGE;
import static org.picketlink.as.subsystem.model.ModelElement.SERVICE_PROVIDER_LOGOUT_PAGE;
import static org.picketlink.as.subsystem.model.ModelElement.SERVICE_PROVIDER_POST_BINDING;
import static org.picketlink.as.subsystem.model.ModelElement.COMMON_STRICT_POST_BINDING;
import static org.picketlink.as.subsystem.model.ModelElement.COMMON_SUPPORTS_SIGNATURES;

import org.jboss.as.controller.PathAddress;
import org.jboss.as.controller.descriptions.ModelDescriptionConstants;
import org.jboss.dmr.ModelNode;
import org.picketlink.identity.federation.core.config.AuthPropertyType;
import org.picketlink.identity.federation.core.config.IDPConfiguration;
import org.picketlink.identity.federation.core.config.KeyProviderType;
import org.picketlink.identity.federation.core.config.SPConfiguration;
import org.picketlink.identity.federation.core.config.STSConfiguration;

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
    
    /**
     * <p>
     *  Converts a {@ModelNode} instance to a {@link STSConfiguration} instance.
     *  This method should be used to extract attributes from the <code>ModelElement.SECURITY_TOKEN_SERVICE</code> model. 
     * </p>
     * 
     * @param model
     * @return
     */
    public static final STSConfiguration toSTSConfig(ModelNode fromModel) {
        String alias = PathAddress.pathAddress(fromModel.get(ModelDescriptionConstants.ADDRESS)).getLastElement().getValue();
        String securityDomain = fromModel.get(COMMON_SECURITY_DOMAIN.getName()).asString();

        STSConfiguration stsType = new STSConfiguration();
        
        stsType.setAlias(alias);
        stsType.setSecurityDomain(securityDomain);
        
        return stsType;
    }

    /**
     * <p>
     *  Converts a {@ModelNode} instance to a {@link STSConfiguration} instance. 
     *  This method only extract the attributes defined in the <code>ModelElement.SAML</code> model.
     * </p>
     * 
     * @param model
     * @return
     */
    public static final STSConfiguration toSAMLConfig(ModelNode fromModel) {
        int tokenTimeout = fromModel.get(ModelElement.SAML_TOKEN_TIMEOUT.getName()).asInt();
        int clockSkew = fromModel.get(ModelElement.SAML_CLOCK_SKEW.getName()).asInt();

        STSConfiguration stsType = new STSConfiguration();
        
        stsType.setTokenTimeout(tokenTimeout);
        stsType.setClockSkew(clockSkew);
        
        return stsType;
    }

    /**
     * <p>
     *  Converts a {@ModelNode} instance to a {@link SPConfiguration} instance. 
     * </p>
     * 
     * @param model
     * @return
     */
    public static SPConfiguration toSPConfig(ModelNode fromModel) {
        SPConfiguration spType = new SPConfiguration();
        
        String alias = fromModel.get(COMMON_ALIAS.getName()).asString();
        
        spType.setAlias(alias);
        
        String url = fromModel.get(COMMON_URL.getName()).asString();
        
        spType.setServiceURL(url);
        
        String securityDomain = fromModel.get(COMMON_SECURITY_DOMAIN.getName()).asString();
        
        spType.setSecurityDomain(securityDomain);
        
        boolean postBinding = fromModel.get(SERVICE_PROVIDER_POST_BINDING.getName()).asBoolean();
        
        if (postBinding) {
            spType.setBindingType("POST");
        } else {
            spType.setBindingType("REDIRECT");
        }
        
        spType.setPostBinding(postBinding);
        
        ModelNode supportsSignatures = fromModel.get(COMMON_SUPPORTS_SIGNATURES.getName());
        
        if (supportsSignatures.isDefined()) {
            spType.setSupportsSignature(supportsSignatures.asBoolean());
        }
        
        ModelNode strictPostBinding = fromModel.get(COMMON_STRICT_POST_BINDING.getName());
        
        if (strictPostBinding.isDefined()) {
            spType.setIdpUsesPostBinding(strictPostBinding.asBoolean());
        }

        ModelNode errorPage = fromModel.get(SERVICE_PROVIDER_ERROR_PAGE.getName());
        
        if (errorPage.isDefined()) {
            spType.setErrorPage(errorPage.asString());
        }

        ModelNode logoutPage = fromModel.get(SERVICE_PROVIDER_LOGOUT_PAGE.getName());
        
        if (logoutPage.isDefined()) {
            spType.setLogOutPage(logoutPage.asString());
        }

        return spType;
    }
    
    /**
     * <p>
     *  Converts a {@ModelNode} instance to a {@link IDPConfiguration} instance. 
     * </p>
     * 
     * @param model
     * @return
     */
    public static IDPConfiguration toIDPConfig(ModelNode fromModel) {
        IDPConfiguration idpType = new IDPConfiguration();
        
        String alias = fromModel.get(COMMON_ALIAS.getName()).asString();
        
        idpType.setAlias(alias);
        
        String url = fromModel.get(COMMON_URL.getName()).asString();
        
        idpType.setIdentityURL(url);
        
        ModelNode supportsSignatures = fromModel.get(COMMON_SUPPORTS_SIGNATURES.getName());
        
        if (supportsSignatures.isDefined()) {
            idpType.setSupportsSignature(supportsSignatures.asBoolean());
        }

        ModelNode encrypt = fromModel.get(IDENTITY_PROVIDER_ENCRYPT.getName());
        
        if (encrypt.isDefined()) {
            idpType.setEncrypt(encrypt.asBoolean());
        }

        ModelNode strictPostBinding = fromModel.get(COMMON_STRICT_POST_BINDING.getName());
        
        if (strictPostBinding.isDefined()) {
            idpType.setStrictPostBinding(strictPostBinding.asBoolean());
        }

        String securityDomain = fromModel.get(COMMON_SECURITY_DOMAIN.getName()).asString();
        
        idpType.setSecurityDomain(securityDomain);

        ModelNode attributeManager = fromModel.get(ModelElement.IDENTITY_PROVIDER_ATTRIBUTE_MANAGER.getName());
        
        if (attributeManager.isDefined()) {
            idpType.setAttributeManager(attributeManager.asString());
        }

        ModelNode roleGenerator = fromModel.get(ModelElement.IDENTITY_PROVIDER_ROLE_GENERATOR.getName());
        
        if (roleGenerator.isDefined()) {
            idpType.setRoleGenerator(roleGenerator.asString());
        }

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
