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

import java.util.HashMap;
import java.util.Map;

/**
 * <p>
 * This class represents a subsystem's model element. A model element is an element that is know and handled by the subsystem.
 * </p>
 * 
 * @author <a href="mailto:psilva@redhat.com">Pedro Silva</a>
 * @since Mar 8, 2012
 */
public enum ModelElement {

    COMMON_ALIAS ("alias"),
    COMMON_URL ("url"),
    COMMON_NAME ("name"),
    COMMON_SECURITY_DOMAIN ("security-domain"),
    COMMON_VALUE ("value"),
    
    FEDERATION("federation"),
    
    IDENTITY_PROVIDER("identity-provider"),
    TRUST_DOMAIN("trust-domain"),
    TRUST_DOMAIN_NAME("name"),
    TRUST_DOMAIN_CERT_ALIAS ("cert-alias"),
    SUPPORTS_SIGNATURES("supportsSignatures"),
    KEY_STORE ("key-store"),
    IDENTITY_PROVIDER_SAML_METADATA ("idp-metadata"),
    IDENTITY_PROVIDER_SAML_METADATA_WANT_AUTHN_REQUESTS_SIGNED ("wantAuthnRequestsSigned"),
    IDENTITY_PROVIDER_SAML_METADATA_ORGANIZATION ("organization"),
    IDENTITY_PROVIDER_EXTERNAL ("external"),
    IDENTITY_PROVIDER_ATTRIBUTE_MANAGER ("attribute-manager"),
    IDENTITY_PROVIDER_ROLE_GENERATOR ("role-generator"),
    STRICT_POST_BINDING ("strict-post-binding"),
    KEY_STORE_PASSWD ("passwd"), 
    KEY_STORE_SIGN_KEY_ALIAS ("sign-key-alias"), 
    KEY_STORE_SIGN_KEY_PASSWD ("sign-key-passwd"),
    CONTACT ("contact"),
    CONTACT_SUR_NAME ("surName"), 
    CONTACT_PHONE ("phone"), 
    CONTACT_EMAIL ("email"), 
    CONTACT_TYPE ("type"), 
    CONTACT_COMPANY ("company"),
    
    SERVICE_PROVIDER("service-provider"), 
    SERVICE_PROVIDER_POST_BINDING ("post-binding"),
    ERROR_PAGE ("error-page"),
    SERVICE_PROVIDER_LOGOUT_PAGE ("logout=page"),
    
    SECURITY_TOKEN_SERVICE ("security-token-service"),
    SAML_TOKEN_TIMEOUT ("token-timeout"),
    SAML_CLOCK_SKEW ("clock-skew"),
    SAML ("saml"), 
    METRICS_CREATED_ASSERTIONS_COUNT ("created-assertions-count"),
    METRICS_RESPONSE_TO_SP_COUNT ("response-to-sp-count"), 
    METRICS_ERROR_RESPONSE_TO_SP_COUNT ("error-response-to-sp-count"), 
    METRICS_ERROR_SIGN_VALIDATION_COUNT ("error-sign-validation-count"), 
    METRICS_ERROR_TRUSTED_DOMAIN_COUNT ("error-trusted-domain-count"), 
    METRICS_EXPIRED_ASSERTIONS_COUNT ("expired-assertions-count"), 
    METRICS_LOGIN_INIT_COUNT ("login-init-count"), 
    METRICS_LOGIN_COMPLETE_COUNT ("login-complete-count"), 
    METRICS_REQUEST_FROM_IDP_COUNT ("request-from-idp-count"), 
    METRICS_RESPONSE_FROM_IDP_COUNT ("response-from-idp-count"), 
    METRICS_REQUEST_TO_IDP_COUNT ("request-to-idp-count"), 
    HANDLER_CLASS ("class"), 
    HANDLER ("handler"), 
    HANDLER_PARAMETER ("handler-parameter");
    
    private static final Map<String, ModelElement> modelElements = new HashMap<String, ModelElement>();
    
    static {
        for (ModelElement element : values()) {
            modelElements.put(element.getName(), element);
        }
    }
    
    private String name;

    private ModelElement(String name) {
        this.name = name;
    }
    
    public String getName() {
        return this.name;
    }

    /**
     * Converts the specified name to a {@link ModelElement}.
     * 
     * @param name a model element name
     * @return the matching model element enum.
     */
    public static ModelElement forName(String name) {
        return modelElements.get(name);
    }
}
