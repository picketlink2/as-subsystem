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

import org.jboss.as.controller.SimpleAttributeDefinition;
import org.jboss.as.controller.SimpleAttributeDefinitionBuilder;
import org.jboss.dmr.ModelNode;
import org.jboss.dmr.ModelType;

/**
 * @author pedroigor
 * @sice Mar 8, 2012
 */
public enum ModelDefinition {

    FEDERATION(ModelKeys.FEDERATION, new SimpleAttributeDefinitionBuilder(ModelKeys.FEDERATION, ModelType.OBJECT, false)
            .build()),

    FEDERATION_ALIAS(ModelKeys.FEDERATION_ALIAS, new SimpleAttributeDefinitionBuilder(ModelKeys.FEDERATION_ALIAS,
            ModelType.STRING, false).setDefaultValue(new ModelNode().set("localhost")).setAllowExpression(false).build()),

    IDENTITY_PROVIDER(ModelKeys.IDENTITY_PROVIDER, new SimpleAttributeDefinitionBuilder(ModelKeys.IDENTITY_PROVIDER,
            ModelType.OBJECT, false).build()),

    IDENTITY_PROVIDER_ALIAS(ModelKeys.IDENTITY_PROVIDER_ALIAS, new SimpleAttributeDefinitionBuilder(
            ModelKeys.IDENTITY_PROVIDER_ALIAS, ModelType.STRING, false).setDefaultValue(new ModelNode().set("idp"))
            .setAllowExpression(false).build()),

    IDENTITY_PROVIDER_URL(ModelKeys.IDENTITY_PROVIDER_URL, new SimpleAttributeDefinitionBuilder(
            ModelKeys.IDENTITY_PROVIDER_URL, ModelType.STRING, false).setAllowExpression(false).build()),

    TRUST(ModelKeys.TRUST, new SimpleAttributeDefinitionBuilder(ModelKeys.TRUST, ModelType.OBJECT, false).build()),

    TRUST_DOMAIN(ModelKeys.TRUST_DOMAIN, new SimpleAttributeDefinitionBuilder(ModelKeys.TRUST_DOMAIN, ModelType.OBJECT, false)
            .build()),

    TRUST_DOMAIN_NAME(ModelKeys.TRUST_DOMAIN_NAME, new SimpleAttributeDefinitionBuilder(ModelKeys.TRUST_DOMAIN_NAME,
            ModelType.STRING, false).setAllowExpression(false).build()),

    IDENTITY_PROVIDER_SIGN_OUTGOING_MESSAGES(ModelKeys.IDENTITY_PROVIDER_SIGN_OUTGOING_MESSAGES,
            new SimpleAttributeDefinitionBuilder(ModelKeys.IDENTITY_PROVIDER_SIGN_OUTGOING_MESSAGES, ModelType.STRING, false)
                    .setDefaultValue(new ModelNode().set(false)).setAllowExpression(false).build()),

    IDENTITY_PROVIDER_IGNORE_INCOMING_SIGNATURES(ModelKeys.IDENTITY_PROVIDER_IGNORE_INCOMING_SIGNATURES,
            new SimpleAttributeDefinitionBuilder(ModelKeys.IDENTITY_PROVIDER_IGNORE_INCOMING_SIGNATURES, ModelType.STRING,
                    false).setDefaultValue(new ModelNode().set(true)).setAllowExpression(false).build());

    private final String key;
    private final SimpleAttributeDefinition definition;

    private ModelDefinition(String key, SimpleAttributeDefinition definition) {
        this.key = key;
        this.definition = definition;
    }

    /**
     * @return the key
     */
    public String getKey() {
        return key;
    }

    /**
     * @return the definition
     */
    public SimpleAttributeDefinition getDefinition() {
        return definition;
    }

}
