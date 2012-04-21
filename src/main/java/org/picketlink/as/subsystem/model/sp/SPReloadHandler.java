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

import java.util.Locale;
import java.util.Set;

import org.jboss.as.controller.OperationContext;
import org.jboss.as.controller.OperationFailedException;
import org.jboss.as.controller.OperationStepHandler;
import org.jboss.as.controller.PathAddress;
import org.jboss.as.controller.PathElement;
import org.jboss.as.controller.descriptions.DescriptionProvider;
import org.jboss.as.controller.descriptions.ModelDescriptionConstants;
import org.jboss.as.controller.registry.Resource.ResourceEntry;
import org.jboss.dmr.ModelNode;
import org.picketlink.as.subsystem.PicketLinkExtension;
import org.picketlink.as.subsystem.model.ModelElement;
import org.picketlink.as.subsystem.model.SubsystemDescriber;
import org.picketlink.as.subsystem.service.SPConfigurationService;

/**
 * @author <a href="mailto:psilva@redhat.com">Pedro Silva</a>
 *
 */
public class SPReloadHandler implements OperationStepHandler, DescriptionProvider{

    public static final String OPERATION_NAME = "reload";
    
    public static final SPReloadHandler INSTANCE = new SPReloadHandler();

    private SPReloadHandler() {
        
    }
    
    @Override
    public ModelNode getModelDescription(Locale locale) {
        return SubsystemDescriber.getOperationDescription(OPERATION_NAME, "Relodas the SP configuration.");
    }
    
    @Override
    public void execute(OperationContext context, ModelNode operation) throws OperationFailedException {
        ModelNode node = context.readResource(PathAddress.EMPTY_ADDRESS).getModel();
        
        final String alias = node.get(ModelElement.COMMON_ALIAS.getName()).asString();
        String url = operation.get(ModelElement.COMMON_URL.getName()).asString();
        String securityDomain = operation.get(ModelElement.COMMON_SECURITY_DOMAIN.getName()).asString();
        boolean postBinding = operation.get(ModelElement.SERVICE_PROVIDER_POST_BINDING.getName()).asBoolean();
        String idpUrl = getIdentityURL(context, operation); 

        SPConfigurationService service = (SPConfigurationService) context.getServiceRegistry(true).getRequiredService(SPConfigurationService.createServiceName(alias)).getValue();
        
        service.getSPConfiguration().setServiceURL(url);
        service.getSPConfiguration().setIdentityURL(idpUrl);
        service.getSPConfiguration().setPostBinding(postBinding);
        service.getSPConfiguration().setSecurityDomain(securityDomain);
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

}
