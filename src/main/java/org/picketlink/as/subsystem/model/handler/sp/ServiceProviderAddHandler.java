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

package org.picketlink.as.subsystem.model.handler.sp;


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
import org.jboss.dmr.ModelType;
import org.jboss.dmr.Property;
import org.jboss.msc.service.ServiceController;
import org.jboss.msc.service.ServiceController.Mode;
import org.jboss.msc.service.ServiceName;
import org.picketlink.as.subsystem.PicketLinkExtension;
import org.picketlink.as.subsystem.model.ModelDefinition;
import org.picketlink.as.subsystem.service.SPConfigurationService;

/**
 * @author pedroigor
 * 
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

        node.get(REQUEST_PROPERTIES, ModelDefinition.SERVICE_PROVIDER_ALIAS.getKey(), DESCRIPTION).set(
                "Service Provider's alias");
        node.get(REQUEST_PROPERTIES, ModelDefinition.SERVICE_PROVIDER_ALIAS.getKey(), TYPE).set(
                ModelDefinition.SERVICE_PROVIDER_ALIAS.getDefinition().getType());
        node.get(REQUEST_PROPERTIES, ModelDefinition.SERVICE_PROVIDER_ALIAS.getKey(), REQUIRED).set(
                !ModelDefinition.SERVICE_PROVIDER_ALIAS.getDefinition().isAllowNull());

        node.get(REQUEST_PROPERTIES, ModelDefinition.COMMON_URL.getKey(), DESCRIPTION)
                .set("Service Provider's URL");
        node.get(REQUEST_PROPERTIES, ModelDefinition.COMMON_URL.getKey(), TYPE).set(
                ModelDefinition.COMMON_URL.getDefinition().getType());
        node.get(REQUEST_PROPERTIES, ModelDefinition.COMMON_URL.getKey(), REQUIRED).set(
                !ModelDefinition.COMMON_URL.getDefinition().isAllowNull());

        return node;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.jboss.as.controller.AbstractAddStepHandler#populateModel(org.jboss.dmr.ModelNode, org.jboss.dmr.ModelNode)
     */
    @Override
    protected void populateModel(ModelNode operation, ModelNode model) throws OperationFailedException {
        String name = ModelDefinition.SERVICE_PROVIDER_ALIAS.getDefinition().getDefaultValue().asString();
        String url = null;

        if (operation.hasDefined(ModelDefinition.SERVICE_PROVIDER_ALIAS.getKey())) {
            name = operation.get(ModelDefinition.SERVICE_PROVIDER_ALIAS.getKey()).asString();
        }

        if (operation.hasDefined(ModelDefinition.COMMON_URL.getKey())) {
            url = operation.get(ModelDefinition.COMMON_URL.getKey()).asString();
        }

        model.get(ModelDefinition.IDENTITY_PROVIDER_ALIAS.getKey()).set(name);
        model.get(ModelDefinition.COMMON_URL.getKey()).set(url);
    }

    @Override
    protected void performRuntime(OperationContext context, ModelNode operation, ModelNode model,
            ServiceVerificationHandler verificationHandler, List<ServiceController<?>> newControllers)
            throws OperationFailedException {
        String alias = PathAddress.pathAddress(operation.get(ModelDescriptionConstants.ADDRESS)).getLastElement().getValue();
        String url = operation.get(ModelDefinition.COMMON_URL.getKey()).asString();
        String fedAlias = PathAddress.pathAddress(operation.get(ModelDescriptionConstants.ADDRESS)).getElement(1).getValue();
        String idpUrl = null;
          
        PathAddress addr = PathAddress.pathAddress(PathElement.pathElement(ModelDefinition.FEDERATION.getKey(), fedAlias));
        
        Set<ResourceEntry> federationChilds = context.getRootResource().getChild(
                PathElement.pathElement(
                        SUBSYSTEM, PicketLinkExtension.SUBSYSTEM_NAME))
                        .navigate(addr).getChildren(ModelDefinition.IDENTITY_PROVIDER.getKey());
        
        for (ResourceEntry resourceEntry : federationChilds) {
            if (resourceEntry.getPathElement().getKey().equals(ModelDefinition.IDENTITY_PROVIDER.getKey())) {
                idpUrl = resourceEntry.getModel().get(ModelDefinition.COMMON_URL.getKey()).asString();
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
    
    public ModelNode resolveModelAttribute(OperationContext context, final ModelNode model) throws OperationFailedException {
        final ModelNode node = new ModelNode();
        node.set("alias");
        node.get("alias").set("idp2.war");
        final ModelNode resolved = context.resolveExpressions(node);
        return resolved;
    }

}
