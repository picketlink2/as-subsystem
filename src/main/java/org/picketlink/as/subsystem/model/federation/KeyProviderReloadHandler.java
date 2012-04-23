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
package org.picketlink.as.subsystem.model.federation;

import java.util.Locale;

import org.jboss.as.controller.OperationContext;
import org.jboss.as.controller.OperationFailedException;
import org.jboss.as.controller.OperationStepHandler;
import org.jboss.as.controller.PathAddress;
import org.jboss.as.controller.descriptions.DescriptionProvider;
import org.jboss.dmr.ModelNode;
import org.picketlink.as.subsystem.model.ModelUtils;
import org.picketlink.as.subsystem.model.SubsystemDescriber;
import org.picketlink.as.subsystem.model.event.KeyProviderEvent;
import org.picketlink.as.subsystem.service.FederationService;
import org.picketlink.identity.federation.core.config.KeyProviderType;

/**
 * @author <a href="mailto:psilva@redhat.com">Pedro Silva</a>
 *
 */
public class KeyProviderReloadHandler implements OperationStepHandler, DescriptionProvider{

    public static final String OPERATION_NAME = "reload";
    
    public static final KeyProviderReloadHandler INSTANCE = new KeyProviderReloadHandler();

    private KeyProviderReloadHandler() {
        
    }
    
    /* (non-Javadoc)
     * @see org.jboss.as.controller.descriptions.DescriptionProvider#getModelDescription(java.util.Locale)
     */
    @Override
    public ModelNode getModelDescription(Locale locale) {
        return SubsystemDescriber.getOperationDescription(OPERATION_NAME, "Relodas the Key Provider configuration.");
    }

    /* (non-Javadoc)
     * @see org.jboss.as.controller.OperationStepHandler#execute(org.jboss.as.controller.OperationContext, org.jboss.dmr.ModelNode)
     */
    @Override
    public void execute(OperationContext context, ModelNode operation) throws OperationFailedException {
        ModelNode model = context.readResource(PathAddress.EMPTY_ADDRESS).getModel();
        
        KeyProviderType keyProviderType = ModelUtils.toKeyProviderType(model);
        
        raiseKeyProviderChangeEvent(context, operation, keyProviderType);
    }
    
    /**
     * <p>
     * Notify registered observers about the new configuration.
     * </p>
     * 
     * @param context
     * @param operation
     * @param keyProviderType
     */
    private void raiseKeyProviderChangeEvent(OperationContext context, ModelNode operation, KeyProviderType keyProviderType) {
        FederationService federationService = FederationService.getService(context.getServiceRegistry(true), operation);
        
        federationService.setKeyProvider(keyProviderType);
        
        new KeyProviderEvent(keyProviderType, federationService.getEventManager()).raise();
    }

}
