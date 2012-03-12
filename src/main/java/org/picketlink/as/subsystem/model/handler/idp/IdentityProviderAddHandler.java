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

package org.picketlink.as.subsystem.model.handler.idp;

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
import org.jboss.as.controller.PathAddress;
import org.jboss.as.controller.ServiceVerificationHandler;
import org.jboss.as.controller.descriptions.DescriptionProvider;
import org.jboss.as.controller.descriptions.ModelDescriptionConstants;
import org.jboss.dmr.ModelNode;
import org.jboss.msc.service.ServiceController;
import org.jboss.msc.service.ServiceController.Mode;
import org.jboss.msc.service.ServiceName;
import org.picketlink.as.subsystem.model.ModelDefinition;
import org.picketlink.as.subsystem.service.IDPConfigurationService;

/**
 * @author pedroigor
 * 
 */
public class IdentityProviderAddHandler extends AbstractAddStepHandler implements DescriptionProvider {

    public static final IdentityProviderAddHandler INSTANCE = new IdentityProviderAddHandler();

    private IdentityProviderAddHandler() {
    }

    @Override
    public ModelNode getModelDescription(Locale locale) {
        ModelNode node = new ModelNode();

        node.get(OPERATION_NAME).set(ADD);

        node.get(DESCRIPTION).set("Adds a Identity Provider");

        node.get(REQUEST_PROPERTIES, ModelDefinition.IDENTITY_PROVIDER_ALIAS.getKey(), DESCRIPTION).set(
                "Identity Provider's alias");
        node.get(REQUEST_PROPERTIES, ModelDefinition.IDENTITY_PROVIDER_ALIAS.getKey(), TYPE).set(
                ModelDefinition.FEDERATION_ALIAS.getDefinition().getType());
        node.get(REQUEST_PROPERTIES, ModelDefinition.IDENTITY_PROVIDER_ALIAS.getKey(), REQUIRED).set(
                !ModelDefinition.FEDERATION_ALIAS.getDefinition().isAllowNull());

        node.get(REQUEST_PROPERTIES, ModelDefinition.COMMON_URL.getKey(), DESCRIPTION)
                .set("Identity Provider's URL");
        node.get(REQUEST_PROPERTIES, ModelDefinition.COMMON_URL.getKey(), TYPE).set(
                ModelDefinition.COMMON_URL.getDefinition().getType());
        node.get(REQUEST_PROPERTIES, ModelDefinition.COMMON_URL.getKey(), REQUIRED).set(
                !ModelDefinition.COMMON_URL.getDefinition().isAllowNull());

        node.get(REQUEST_PROPERTIES, ModelDefinition.IDENTITY_PROVIDER_SIGN_OUTGOING_MESSAGES.getKey(), DESCRIPTION)
                .set("Identity Provider's URL");
        node.get(REQUEST_PROPERTIES, ModelDefinition.IDENTITY_PROVIDER_SIGN_OUTGOING_MESSAGES.getKey(), TYPE).set(
                ModelDefinition.IDENTITY_PROVIDER_SIGN_OUTGOING_MESSAGES.getDefinition().getType());
        node.get(REQUEST_PROPERTIES, ModelDefinition.IDENTITY_PROVIDER_SIGN_OUTGOING_MESSAGES.getKey(), REQUIRED).set(
                !ModelDefinition.IDENTITY_PROVIDER_SIGN_OUTGOING_MESSAGES.getDefinition().isAllowNull());

        node.get(REQUEST_PROPERTIES, ModelDefinition.IDENTITY_PROVIDER_IGNORE_INCOMING_SIGNATURES.getKey(), DESCRIPTION)
                .set("Identity Provider's URL");
        node.get(REQUEST_PROPERTIES, ModelDefinition.IDENTITY_PROVIDER_IGNORE_INCOMING_SIGNATURES.getKey(), TYPE).set(
                ModelDefinition.IDENTITY_PROVIDER_IGNORE_INCOMING_SIGNATURES.getDefinition().getType());
        node.get(REQUEST_PROPERTIES, ModelDefinition.IDENTITY_PROVIDER_IGNORE_INCOMING_SIGNATURES.getKey(), REQUIRED).set(
                !ModelDefinition.IDENTITY_PROVIDER_IGNORE_INCOMING_SIGNATURES.getDefinition().isAllowNull());

        return node;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.jboss.as.controller.AbstractAddStepHandler#populateModel(org.jboss.dmr.ModelNode, org.jboss.dmr.ModelNode)
     */
    @Override
    protected void populateModel(ModelNode operation, ModelNode model) throws OperationFailedException {
        String name = ModelDefinition.IDENTITY_PROVIDER_ALIAS.getDefinition().getDefaultValue().asString();
        String url = null;
        boolean signOutgoingMessages = ModelDefinition.IDENTITY_PROVIDER_SIGN_OUTGOING_MESSAGES.getDefinition().getDefaultValue().asBoolean();
        boolean ignoreIncomingSignatures = ModelDefinition.IDENTITY_PROVIDER_IGNORE_INCOMING_SIGNATURES.getDefinition().getDefaultValue().asBoolean();

        if (operation.hasDefined(ModelDefinition.IDENTITY_PROVIDER_ALIAS.getKey())) {
            name = operation.get(ModelDefinition.IDENTITY_PROVIDER_ALIAS.getKey()).asString();
        }

        if (operation.hasDefined(ModelDefinition.COMMON_URL.getKey())) {
            url = operation.get(ModelDefinition.COMMON_URL.getKey()).asString();
        }

        if (operation.hasDefined(ModelDefinition.IDENTITY_PROVIDER_SIGN_OUTGOING_MESSAGES.getKey())) {
            signOutgoingMessages = operation.get(ModelDefinition.IDENTITY_PROVIDER_SIGN_OUTGOING_MESSAGES.getKey()).asBoolean();
        }

        if (operation.hasDefined(ModelDefinition.IDENTITY_PROVIDER_IGNORE_INCOMING_SIGNATURES.getKey())) {
            ignoreIncomingSignatures = operation.get(ModelDefinition.IDENTITY_PROVIDER_IGNORE_INCOMING_SIGNATURES.getKey()).asBoolean();
        }

        model.get(ModelDefinition.IDENTITY_PROVIDER_ALIAS.getKey()).set(name);
        model.get(ModelDefinition.COMMON_URL.getKey()).set(url);
        model.get(ModelDefinition.IDENTITY_PROVIDER_SIGN_OUTGOING_MESSAGES.getKey()).set(signOutgoingMessages);
        model.get(ModelDefinition.IDENTITY_PROVIDER_IGNORE_INCOMING_SIGNATURES.getKey()).set(ignoreIncomingSignatures);
    }

    @Override
    protected void performRuntime(OperationContext context, ModelNode operation, ModelNode model,
            ServiceVerificationHandler verificationHandler, List<ServiceController<?>> newControllers)
            throws OperationFailedException {
        String alias = PathAddress.pathAddress(operation.get(ModelDescriptionConstants.ADDRESS)).getLastElement().getValue();
        String url = operation.get(ModelDefinition.COMMON_URL.getKey()).asString();
        boolean signOutgoingMessages = operation.get(ModelDefinition.IDENTITY_PROVIDER_SIGN_OUTGOING_MESSAGES.getKey()).asBoolean();
        boolean ignoreIncomingSignatures = operation.get(ModelDefinition.IDENTITY_PROVIDER_IGNORE_INCOMING_SIGNATURES.getKey()).asBoolean();

        IDPConfigurationService service = new IDPConfigurationService(alias, url);
        ServiceName name = IDPConfigurationService.createServiceName(alias);
        ServiceController<IDPConfigurationService> controller = context.getServiceTarget().addService(name, service)
                .addListener(verificationHandler).setInitialMode(Mode.ACTIVE).install();
        
        controller.getValue().getIdpConfiguration().setSignOutgoingMessages(signOutgoingMessages);
        controller.getValue().getIdpConfiguration().setIgnoreIncomingSignatures(ignoreIncomingSignatures);
        
        newControllers.add(controller);
    }

}
