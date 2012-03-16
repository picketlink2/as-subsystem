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


import static org.jboss.as.controller.descriptions.ModelDescriptionConstants.ADD;
import static org.jboss.as.controller.descriptions.ModelDescriptionConstants.DESCRIPTION;
import static org.jboss.as.controller.descriptions.ModelDescriptionConstants.OPERATION_NAME;
import static org.jboss.as.controller.descriptions.ModelDescriptionConstants.REQUEST_PROPERTIES;
import static org.jboss.as.controller.descriptions.ModelDescriptionConstants.REQUIRED;
import static org.jboss.as.controller.descriptions.ModelDescriptionConstants.SUBSYSTEM;
import static org.jboss.as.controller.descriptions.ModelDescriptionConstants.TYPE;

import java.util.List;
import java.util.Locale;
import java.util.Set;

import org.jboss.as.controller.AbstractAddStepHandler;
import org.jboss.as.controller.OperationContext;
import org.jboss.as.controller.OperationFailedException;
import org.jboss.as.controller.PathAddress;
import org.jboss.as.controller.PathElement;
import org.jboss.as.controller.ServiceVerificationHandler;
import org.jboss.as.controller.descriptions.DescriptionProvider;
import org.jboss.as.controller.descriptions.ModelDescriptionConstants;
import org.jboss.as.controller.registry.Resource.ResourceEntry;
import org.jboss.dmr.ModelNode;
import org.jboss.msc.service.ServiceController;
import org.jboss.msc.service.ServiceController.Mode;
import org.jboss.msc.service.ServiceName;
import org.picketlink.as.subsystem.PicketLinkExtension;
import org.picketlink.as.subsystem.model.ModelKeys;
import org.picketlink.as.subsystem.service.SPConfigurationService;

/**
 * @author <a href="mailto:psilva@redhat.com">Pedro Silva</a>
 */
public class ServiceProviderAddHandler extends AbstractAddStepHandler implements DescriptionProvider {

    public static final ServiceProviderAddHandler INSTANCE = new ServiceProviderAddHandler();

    private ServiceProviderAddHandler() {
    }

    @Override
    public ModelNode getModelDescription(Locale locale) {
        ModelNode node = new ModelNode();

        node.get(OPERATION_NAME).set(ADD);

        node.get(DESCRIPTION).set("Adds a Service Provider");

        node.get(REQUEST_PROPERTIES, ModelKeys.COMMON_ALIAS, DESCRIPTION).set(
                "Service Provider's alias");
        node.get(REQUEST_PROPERTIES, ModelKeys.COMMON_ALIAS, TYPE).set(
                ServiceProviderResourceDefinition.ALIAS.getType());
        node.get(REQUEST_PROPERTIES, ModelKeys.COMMON_ALIAS, REQUIRED).set(
                !ServiceProviderResourceDefinition.ALIAS.isAllowNull());

        node.get(REQUEST_PROPERTIES, ModelKeys.COMMON_URL, DESCRIPTION)
                .set("Service Provider's URL");
        node.get(REQUEST_PROPERTIES, ModelKeys.COMMON_URL, TYPE).set(
                ServiceProviderResourceDefinition.URL.getType());
        node.get(REQUEST_PROPERTIES, ModelKeys.COMMON_URL, REQUIRED).set(
                !ServiceProviderResourceDefinition.URL.isAllowNull());

        return node;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.jboss.as.controller.AbstractAddStepHandler#populateModel(org.jboss.dmr.ModelNode, org.jboss.dmr.ModelNode)
     */
    @Override
    protected void populateModel(ModelNode operation, ModelNode model) throws OperationFailedException {
        ServiceProviderResourceDefinition.ALIAS.validateAndSet(operation, model);
        ServiceProviderResourceDefinition.URL.validateAndSet(operation, model);
    }

    @Override
    protected void performRuntime(OperationContext context, ModelNode operation, ModelNode model,
            ServiceVerificationHandler verificationHandler, List<ServiceController<?>> newControllers)
            throws OperationFailedException {
        String alias = PathAddress.pathAddress(operation.get(ModelDescriptionConstants.ADDRESS)).getLastElement().getValue();
        String url = operation.get(ModelKeys.COMMON_URL).asString();
        String fedAlias = PathAddress.pathAddress(operation.get(ModelDescriptionConstants.ADDRESS)).getElement(1).getValue();
        String idpUrl = null;
          
        PathAddress addr = PathAddress.pathAddress(PathElement.pathElement(ModelKeys.FEDERATION, fedAlias));
        
        Set<ResourceEntry> federationChilds = context.getRootResource().getChild(
                PathElement.pathElement(
                        SUBSYSTEM, PicketLinkExtension.SUBSYSTEM_NAME))
                        .navigate(addr).getChildren(ModelKeys.IDENTITY_PROVIDER);
        
        for (ResourceEntry resourceEntry : federationChilds) {
            if (resourceEntry.getPathElement().getKey().equals(ModelKeys.IDENTITY_PROVIDER)) {
                idpUrl = resourceEntry.getModel().get(ModelKeys.COMMON_URL).asString();
                break;
            }
        } 
        
        SPConfigurationService service = new SPConfigurationService(alias, url);
        ServiceName name = SPConfigurationService.createServiceName(alias);
        ServiceController<SPConfigurationService> controller = context.getServiceTarget().addService(name, service)
                .addListener(verificationHandler).setInitialMode(Mode.ACTIVE).install();

        service.getSPConfiguration().setIdentityURL(idpUrl);
        
        newControllers.add(controller);
    }
    
}
