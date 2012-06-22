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

import java.util.List;

import org.jboss.as.controller.AbstractRemoveStepHandler;
import org.jboss.as.controller.OperationContext;
import org.jboss.as.controller.OperationFailedException;
import org.jboss.as.controller.PathAddress;
import org.jboss.as.controller.descriptions.ModelDescriptionConstants;
import org.jboss.dmr.ModelNode;
import org.jboss.dmr.Property;
import org.jboss.msc.service.ServiceName;
import org.picketlink.as.subsystem.model.ModelElement;
import org.picketlink.as.subsystem.service.FederationService;
import org.picketlink.as.subsystem.service.IdentityProviderService;
import org.picketlink.as.subsystem.service.ServiceProviderService;

/**
 * @author <a href="mailto:psilva@redhat.com">Pedro Silva</a>
 */
public class FederationRemoveHandler extends AbstractRemoveStepHandler  {

    public static final FederationRemoveHandler INSTANCE = new FederationRemoveHandler();

    private FederationRemoveHandler() {
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see org.jboss.as.controller.AbstractRemoveStepHandler#performRuntime(org.jboss.as.controller.OperationContext,
     * org.jboss.dmr.ModelNode, org.jboss.dmr.ModelNode)
     */
    @Override
    protected void performRuntime(OperationContext context, ModelNode operation, ModelNode model)
            throws OperationFailedException {
        removeIdentityProviderService(context, model);
        removeServiceProviderService(context, model);
        removeFederationService(context, operation);
        context.restartRequired();
    }

    /**
     * <p>
     * Removes the registered {@ FederationService}.
     * </p>
     * 
     * @param context
     * @param operation
     */
    private void removeFederationService(OperationContext context, ModelNode operation) {
        String suffix = PathAddress.pathAddress(operation.get(ModelDescriptionConstants.ADDRESS)).getLastElement().getValue();
        ServiceName name = FederationService.createServiceName(suffix);
        context.removeService(name);
    }

    /**
     * <p>
     * Removes all services registered for the configured service providers.
     * </p>
     * 
     * @param context
     * @param model
     */
    private void removeServiceProviderService(OperationContext context, ModelNode model) {
        if (model.get(ModelElement.SERVICE_PROVIDER.getName()).isDefined()) {
            for (Property serviceProviders : getServiceProviders(model)) {
                ServiceName name = ServiceProviderService.createServiceName(serviceProviders.getName());
                
                context.removeService(name);
            }
        }
    }

    /**
     * <p>
     * Removes all services registered for the configured identity provider.
     * </p>
     * 
     * @param context
     * @param model
     */
    private void removeIdentityProviderService(OperationContext context, ModelNode model) {
        if (hasIdentityProvider(model)) {
            String idpAlias = getIdentityProvider(model).getName();
            
            ServiceName name = IdentityProviderService.createServiceName(idpAlias);
            
            context.removeService(name);
        }
    }

    private List<Property> getServiceProviders(ModelNode model) {
        return model.get(ModelElement.SERVICE_PROVIDER.getName()).asPropertyList();
    }

    private Property getIdentityProvider(ModelNode model) {
        return model.get(ModelElement.IDENTITY_PROVIDER.getName()).asPropertyList().get(0);
    }

    private boolean hasIdentityProvider(ModelNode model) {
        return model.get(ModelElement.IDENTITY_PROVIDER.getName()).isDefined() && !model.get(ModelElement.IDENTITY_PROVIDER.getName()).asPropertyList().isEmpty();
    }
}
