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

package org.picketlink.as.subsystem.model.idp;


import org.jboss.as.controller.PathElement;
import org.jboss.as.controller.SimpleAttributeDefinition;
import org.jboss.as.controller.SimpleAttributeDefinitionBuilder;
import org.jboss.as.controller.SimpleResourceDefinition;
import org.jboss.as.controller.registry.ManagementResourceRegistration;
import org.jboss.dmr.ModelNode;
import org.jboss.dmr.ModelType;
import org.picketlink.as.subsystem.PicketLinkExtension;
import org.picketlink.as.subsystem.model.ModelElement;

/**
 * @author <a href="mailto:psilva@redhat.com">Pedro Silva</a>
 * @since Mar 16, 2012
 */
public class IdentityProviderResourceDefinition extends SimpleResourceDefinition {

    public static final IdentityProviderResourceDefinition INSTANCE = new IdentityProviderResourceDefinition();

    public static final SimpleAttributeDefinition URL = new SimpleAttributeDefinitionBuilder(ModelElement.COMMON_URL.getName(),
            ModelType.STRING, false).setAllowExpression(false).build();
    public static final SimpleAttributeDefinition ALIAS = new SimpleAttributeDefinitionBuilder(
            ModelElement.COMMON_ALIAS.getName(), ModelType.STRING, false).setDefaultValue(new ModelNode().set("idp"))
            .setAllowExpression(false).build();
    public static final SimpleAttributeDefinition SIGN_OUTGOING_MESSAGES = new SimpleAttributeDefinitionBuilder(
            ModelElement.IDENTITY_PROVIDER_SIGN_OUTGOING_MESSAGES.getName(), ModelType.STRING, false)
            .setDefaultValue(new ModelNode().set(false)).setAllowExpression(false).build();
    public static final SimpleAttributeDefinition IGNORE_INCOMING_SIGNATURES = new SimpleAttributeDefinitionBuilder(
            ModelElement.IDENTITY_PROVIDER_IGNORE_INCOMING_SIGNATURES.getName(), ModelType.STRING, false)
            .setDefaultValue(new ModelNode().set(true)).setAllowExpression(false).build();

    private IdentityProviderResourceDefinition() {
        super(PathElement.pathElement(ModelElement.IDENTITY_PROVIDER.getName()), PicketLinkExtension
                .getResourceDescriptionResolver(ModelElement.IDENTITY_PROVIDER.getName()), IdentityProviderAddHandler.INSTANCE,
                IdentityProviderRemoveHandler.INSTANCE);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.jboss.as.controller.SimpleResourceDefinition#registerAttributes(org.jboss.as.controller.registry.
     * ManagementResourceRegistration)
     */
    @Override
    public void registerAttributes(ManagementResourceRegistration resourceRegistration) {
        resourceRegistration.registerReadWriteAttribute(ALIAS, null, IdentityProviderAliasHandler.INSTANCE);
        resourceRegistration.registerReadWriteAttribute(URL, null, IdentityProviderURLHandler.INSTANCE);
        resourceRegistration.registerReadWriteAttribute(SIGN_OUTGOING_MESSAGES, null,
                IdentityProviderSignOutgoingMessagesHandler.INSTANCE);
        resourceRegistration.registerReadWriteAttribute(IGNORE_INCOMING_SIGNATURES, null,
                IdentityProviderIgnoreInSignMsgHandler.INSTANCE);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.jboss.as.controller.SimpleResourceDefinition#registerChildren(org.jboss.as.controller.registry.
     * ManagementResourceRegistration)
     */
    @Override
    public void registerChildren(ManagementResourceRegistration resourceRegistration) {
        resourceRegistration.registerSubModel(TrustDomainResourceDefinition.INSTANCE);
    }
}
