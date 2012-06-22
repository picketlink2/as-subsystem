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

import org.jboss.as.controller.AbstractWriteAttributeHandler;
import org.jboss.as.controller.OperationContext;
import org.jboss.as.controller.OperationFailedException;
import org.jboss.as.controller.PathAddress;
import org.jboss.dmr.ModelNode;
import org.picketlink.as.subsystem.model.ModelElement;
import org.picketlink.as.subsystem.model.ModelUtils;
import org.picketlink.as.subsystem.service.FederationService;
import org.picketlink.as.subsystem.service.IdentityProviderService;
import org.picketlink.identity.federation.core.config.IDPConfiguration;

/**
 * @author <a href="mailto:psilva@redhat.com">Pedro Silva</a>
 * 
 */
public class IDPWriteAttributeHandler extends AbstractWriteAttributeHandler<Void> {

    public static final IDPWriteAttributeHandler INSTANCE = new IDPWriteAttributeHandler();

    private IDPWriteAttributeHandler() {
        super(IdentityProviderResourceDefinition.ALIAS, IdentityProviderResourceDefinition.EXTERNAL,
                IdentityProviderResourceDefinition.SUPPORTS_SIGNATURES, IdentityProviderResourceDefinition.SECURITY_DOMAIN,
                IdentityProviderResourceDefinition.URL, IdentityProviderResourceDefinition.STRICT_POST_BINDING);
    }

    @Override
    protected boolean applyUpdateToRuntime(OperationContext context, ModelNode operation, String attributeName,
            ModelNode resolvedValue, ModelNode currentValue,
            org.jboss.as.controller.AbstractWriteAttributeHandler.HandbackHolder<Void> handbackHolder)
            throws OperationFailedException {
        ModelNode node = context.readResource(PathAddress.EMPTY_ADDRESS).getModel();

        String alias = node.get(ModelElement.COMMON_ALIAS.getName()).asString();

        IdentityProviderService service = (IdentityProviderService) context.getServiceRegistry(true)
                .getRequiredService(IdentityProviderService.createServiceName(alias)).getValue();

        IDPConfiguration updatedIDPConfig = ModelUtils.toIDPConfig(node);

        // the node has only the idp attributes, we need to get the child elements configuration and set them again
        updatedIDPConfig.setKeyProvider(FederationService.getService(context.getServiceRegistry(true), operation)
                .getKeyProvider());
        updatedIDPConfig.setTrust(service.getConfiguration().getTrust());
        updatedIDPConfig.setTrustDomainAlias(service.getConfiguration().getTrustDomainAlias());

        service.setConfiguration(updatedIDPConfig);

        service.raiseUpdateEvent();
        
        return false;
    }

    @Override
    protected void revertUpdateToRuntime(OperationContext context, ModelNode operation, String attributeName,
            ModelNode valueToRestore, ModelNode valueToRevert, Void handback) throws OperationFailedException {

    }

}
