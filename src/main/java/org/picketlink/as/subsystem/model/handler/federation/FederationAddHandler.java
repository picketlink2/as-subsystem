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

package org.picketlink.as.subsystem.model.handler.federation;

import static org.jboss.as.controller.descriptions.ModelDescriptionConstants.ADD;
import static org.jboss.as.controller.descriptions.ModelDescriptionConstants.DESCRIPTION;
import static org.jboss.as.controller.descriptions.ModelDescriptionConstants.OPERATION_NAME;
import static org.jboss.as.controller.descriptions.ModelDescriptionConstants.REQUEST_PROPERTIES;
import static org.jboss.as.controller.descriptions.ModelDescriptionConstants.REQUIRED;
import static org.jboss.as.controller.descriptions.ModelDescriptionConstants.TYPE;

import java.util.List;
import java.util.Locale;

import org.jboss.as.controller.AbstractAddStepHandler;
import org.jboss.as.controller.OperationContext;
import org.jboss.as.controller.OperationFailedException;
import org.jboss.as.controller.ServiceVerificationHandler;
import org.jboss.as.controller.descriptions.DescriptionProvider;
import org.jboss.dmr.ModelNode;
import org.jboss.msc.service.ServiceController;
import org.picketlink.as.subsystem.model.ModelDefinition;
import org.picketlink.as.subsystem.model.ModelKeys;

/**
 * @author pedroigor
 * 
 */
public class FederationAddHandler extends AbstractAddStepHandler implements DescriptionProvider {

    public static final FederationAddHandler INSTANCE = new FederationAddHandler();

    private FederationAddHandler() {
    }

    /* (non-Javadoc)
     * @see org.jboss.as.controller.descriptions.DescriptionProvider#getModelDescription(java.util.Locale)
     */
    @Override
    public ModelNode getModelDescription(Locale locale) {
        ModelNode node = new ModelNode();

        node.get(OPERATION_NAME).set(ADD);
        node.get(DESCRIPTION).set("Adds a federation configuration.");
        
        node.get(REQUEST_PROPERTIES, ModelKeys.COMMON_ALIAS, DESCRIPTION).set("Federation's alias");
        node.get(REQUEST_PROPERTIES, ModelKeys.COMMON_ALIAS, TYPE).set(ModelDefinition.FEDERATION_ALIAS.getDefinition().getType());
        node.get(REQUEST_PROPERTIES, ModelKeys.COMMON_ALIAS, REQUIRED).set(!ModelDefinition.FEDERATION_ALIAS.getDefinition().isAllowNull());
        
        return node;
    }

    /* (non-Javadoc)
     * @see org.jboss.as.controller.AbstractAddStepHandler#populateModel(org.jboss.dmr.ModelNode, org.jboss.dmr.ModelNode)
     */
    @Override
    protected void populateModel(ModelNode operation, ModelNode model) throws OperationFailedException {
        String name = null;

        if (operation.hasDefined(ModelDefinition.FEDERATION_ALIAS.getKey())) {
            name = operation.get(ModelDefinition.FEDERATION_ALIAS.getKey()).asString();
        } else {
            name = ModelDefinition.FEDERATION_ALIAS.getDefinition().getDefaultValue().asString();
        }

        model.get(ModelDefinition.FEDERATION_ALIAS.getKey()).set(name);
    }

    /* (non-Javadoc)
     * @see org.jboss.as.controller.AbstractAddStepHandler#performRuntime(org.jboss.as.controller.OperationContext, org.jboss.dmr.ModelNode, org.jboss.dmr.ModelNode, org.jboss.as.controller.ServiceVerificationHandler, java.util.List)
     */
    @Override
    protected void performRuntime(OperationContext context, ModelNode operation, ModelNode model,
            ServiceVerificationHandler verificationHandler, List<ServiceController<?>> newControllers)
            throws OperationFailedException {
    }
}
