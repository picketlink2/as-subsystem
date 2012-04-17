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

package org.picketlink.as.subsystem.model.sp;


import static org.jboss.as.controller.descriptions.ModelDescriptionConstants.SUBSYSTEM;

import java.util.List;
import java.util.Set;

import org.jboss.as.controller.OperationContext;
import org.jboss.as.controller.OperationFailedException;
import org.jboss.as.controller.PathAddress;
import org.jboss.as.controller.PathElement;
import org.jboss.as.controller.ServiceVerificationHandler;
import org.jboss.as.controller.descriptions.ModelDescriptionConstants;
import org.jboss.as.controller.registry.Resource.ResourceEntry;
import org.jboss.dmr.ModelNode;
import org.jboss.msc.service.ServiceController;
import org.jboss.msc.service.ServiceController.Mode;
import org.jboss.msc.service.ServiceName;
import org.picketlink.as.subsystem.PicketLinkExtension;
import org.picketlink.as.subsystem.model.ModelElement;
import org.picketlink.as.subsystem.model.event.IdentityProviderURLEvent;
import org.picketlink.as.subsystem.model.event.KeyProviderEvent;
import org.picketlink.as.subsystem.service.FederationService;
import org.picketlink.as.subsystem.service.SPConfigurationService;

/**
 * @author <a href="mailto:psilva@redhat.com">Pedro Silva</a>
 */
public class ServiceProviderAddHandler extends AbstractResourceAddStepHandler {

    public static final ServiceProviderAddHandler INSTANCE = new ServiceProviderAddHandler();

    private ServiceProviderAddHandler() {
        super(ModelElement.SERVICE_PROVIDER);
    }

    /* (non-Javadoc)
     * @see org.jboss.as.controller.AbstractAddStepHandler#performRuntime(org.jboss.as.controller.OperationContext, org.jboss.dmr.ModelNode, org.jboss.dmr.ModelNode, org.jboss.as.controller.ServiceVerificationHandler, java.util.List)
     */
    @Override
    protected void performRuntime(OperationContext context, ModelNode operation, ModelNode model,
            ServiceVerificationHandler verificationHandler, List<ServiceController<?>> newControllers)
            throws OperationFailedException {
        FederationService federationService = FederationService.getService(context.getServiceRegistry(true), getFederationAlias(operation));
        
        SPConfigurationService spService = createSPService(context, operation, verificationHandler, newControllers);
        
        // if the parent federation has a keyprovider configuration sets it in the sp service
        spService.getSPConfiguration().setKeyProvider(federationService.getKeyProvider());
        
        federationService.getEventManager().addObserver(KeyProviderEvent.class, spService);
        federationService.getEventManager().addObserver(IdentityProviderURLEvent.class, spService);
    }

    /**
     * <p>
     * Creates a new {@link SPConfigurationService} instance for this SP configuration.
     * </p>
     * @param context
     * @param operation
     * @param verificationHandler
     * @param newControllers
     * @return
     */
    private SPConfigurationService createSPService(OperationContext context, ModelNode operation,
            ServiceVerificationHandler verificationHandler, List<ServiceController<?>> newControllers) {
        String alias = PathAddress.pathAddress(operation.get(ModelDescriptionConstants.ADDRESS)).getLastElement().getValue();
        String url = operation.get(ModelElement.COMMON_URL.getName()).asString();
        String securityDomain = operation.get(ModelElement.COMMON_SECURITY_DOMAIN.getName()).asString();
        boolean postBinding = operation.get(ModelElement.SERVICE_PROVIDER_POST_BINDING.getName()).asBoolean();
        String idpUrl = getIdentityURL(context, operation); 
        
        SPConfigurationService service = new SPConfigurationService(alias, url);
        ServiceName name = SPConfigurationService.createServiceName(alias);
        ServiceController<SPConfigurationService> controller = context.getServiceTarget().addService(name, service)
                .addListener(verificationHandler).setInitialMode(Mode.ACTIVE).install();

        service.getSPConfiguration().setIdentityURL(idpUrl);
        service.getSPConfiguration().setPostBinding(postBinding);
        service.getSPConfiguration().setSecurityDomain(securityDomain);
        
        newControllers.add(controller);
        
        return service;
    }

    /**
     * <p>
     * Returns the alias for the parent federation onfiguration. 
     * </p>
     * 
     * @param operation
     * @return
     */
    private String getFederationAlias(ModelNode operation) {
        return PathAddress.pathAddress(operation.get(ModelDescriptionConstants.ADDRESS)).getElement(1).getValue();
    }

    /**
     * <p>
     * Try do discover the IDP URL.
     * </p>
     * 
     * @param context
     * @param operation 
     * @param pathAddress
     * @return
     */
    private String getIdentityURL(OperationContext context, ModelNode operation) {
        String identityURL = null;
        PathAddress pathAddress = PathAddress.pathAddress(PathElement.pathElement(ModelElement.FEDERATION.getName(), getFederationAlias(operation)));
        
        Set<ResourceEntry> federationChilds = context.getRootResource().getChild(
                PathElement.pathElement(
                        SUBSYSTEM, PicketLinkExtension.SUBSYSTEM_NAME))
                        .navigate(pathAddress).getChildren(ModelElement.IDENTITY_PROVIDER.getName());
        
        for (ResourceEntry resourceEntry : federationChilds) {
            if (resourceEntry.getPathElement().getKey().equals(ModelElement.IDENTITY_PROVIDER.getName())) {
                identityURL = resourceEntry.getModel().get(ModelElement.COMMON_URL.getName()).asString();
                break;
            }
        }
        
        return identityURL;
    }
    
}
