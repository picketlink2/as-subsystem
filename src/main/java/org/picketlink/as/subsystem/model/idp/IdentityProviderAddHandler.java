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

import static org.picketlink.as.subsystem.model.ModelElement.COMMON_SECURITY_DOMAIN;
import static org.picketlink.as.subsystem.model.ModelElement.COMMON_URL;
import static org.picketlink.as.subsystem.model.ModelElement.IDENTITY_PROVIDER_IGNORE_INCOMING_SIGNATURES;
import static org.picketlink.as.subsystem.model.ModelElement.IDENTITY_PROVIDER_SIGN_OUTGOING_MESSAGES;

import java.util.List;

import org.jboss.as.controller.OperationContext;
import org.jboss.as.controller.OperationFailedException;
import org.jboss.as.controller.PathAddress;
import org.jboss.as.controller.ServiceVerificationHandler;
import org.jboss.as.controller.descriptions.ModelDescriptionConstants;
import org.jboss.dmr.ModelNode;
import org.jboss.msc.service.ServiceController;
import org.jboss.msc.service.ServiceController.Mode;
import org.jboss.msc.service.ServiceName;
import org.picketlink.as.subsystem.model.ModelElement;
import org.picketlink.as.subsystem.model.event.KeyProviderEvent;
import org.picketlink.as.subsystem.model.sp.AbstractResourceAddStepHandler;
import org.picketlink.as.subsystem.service.FederationService;
import org.picketlink.as.subsystem.service.IDPConfigurationService;

/**
 * @author <a href="mailto:psilva@redhat.com">Pedro Silva</a>
 */
public class IdentityProviderAddHandler extends AbstractResourceAddStepHandler {

    public static final IdentityProviderAddHandler INSTANCE = new IdentityProviderAddHandler();

    private IdentityProviderAddHandler() {
        super(ModelElement.IDENTITY_PROVIDER);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.jboss.as.controller.AbstractAddStepHandler#performRuntime(org.jboss.as.controller.OperationContext,
     * org.jboss.dmr.ModelNode, org.jboss.dmr.ModelNode, org.jboss.as.controller.ServiceVerificationHandler, java.util.List)
     */
    @Override
    protected void performRuntime(OperationContext context, ModelNode operation, ModelNode model,
            ServiceVerificationHandler verificationHandler, List<ServiceController<?>> newControllers)
            throws OperationFailedException {
        PathAddress pathAddress = PathAddress.pathAddress(operation.get(ModelDescriptionConstants.ADDRESS));

        IDPConfigurationService idpService = createIDPService(pathAddress.getLastElement().getValue(), context, operation, verificationHandler, newControllers);
        
        FederationService federationService = FederationService.getService(context.getServiceRegistry(true), pathAddress.getElement(1).getValue());
        
        // if the parent federation has a keyprovider configuration sets it in the idp service
        idpService.getIdpConfiguration().setKeyProvider(federationService.getKeyProvider());
        
        federationService.getEventManager().addObserver(KeyProviderEvent.class, idpService);
    }

    /**
     * <p>
     * Creates a new {@link IDPConfigurationService} instance for this IDP configuration.
     * </p>
     * 
     * @param alias
     * @param context
     * @param operation
     * @param verificationHandler
     * @param newControllers
     * @return
     */
    private IDPConfigurationService createIDPService(String alias, OperationContext context, ModelNode operation,
            ServiceVerificationHandler verificationHandler, List<ServiceController<?>> newControllers) {
        String url = operation.get(COMMON_URL.getName()).asString();
        boolean signOutgoingMessages = operation.get(IDENTITY_PROVIDER_SIGN_OUTGOING_MESSAGES.getName()).asBoolean();
        boolean ignoreIncomingSignatures = operation.get(IDENTITY_PROVIDER_IGNORE_INCOMING_SIGNATURES.getName()).asBoolean();
        String securityDomain = operation.get(COMMON_SECURITY_DOMAIN.getName()).asString();

        IDPConfigurationService identityProviderService = new IDPConfigurationService(alias, url);
        ServiceName name = IDPConfigurationService.createServiceName(alias);

        ServiceController<IDPConfigurationService> controller = context.getServiceTarget()
                .addService(name, identityProviderService).addListener(verificationHandler).setInitialMode(Mode.ACTIVE)
                .install();

        controller.getValue().getIdpConfiguration().setSignOutgoingMessages(signOutgoingMessages);
        controller.getValue().getIdpConfiguration().setIgnoreIncomingSignatures(ignoreIncomingSignatures);
        controller.getValue().getIdpConfiguration().setSecurityDomain(securityDomain);

        newControllers.add(controller);
        
        return identityProviderService;
    }

}
