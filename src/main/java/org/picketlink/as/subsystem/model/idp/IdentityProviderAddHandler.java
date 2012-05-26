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

import java.util.List;

import org.jboss.as.controller.OperationContext;
import org.jboss.as.controller.OperationFailedException;
import org.jboss.as.controller.PathAddress;
import org.jboss.as.controller.ServiceVerificationHandler;
import org.jboss.as.controller.descriptions.ModelDescriptionConstants;
import org.jboss.as.naming.deployment.JndiNamingDependencyProcessor;
import org.jboss.as.naming.service.NamingService;
import org.jboss.as.security.SecuritySubsystemRootResourceDefinition;
import org.jboss.as.security.service.SecurityBootstrapService;
import org.jboss.as.security.service.SecurityDomainService;
import org.jboss.dmr.ModelNode;
import org.jboss.msc.service.ServiceController;
import org.jboss.msc.service.ServiceController.Mode;
import org.jboss.security.jacc.SecurityService;
import org.picketlink.as.subsystem.model.AbstractResourceAddStepHandler;
import org.picketlink.as.subsystem.model.ModelElement;
import org.picketlink.as.subsystem.service.AbstractEntityProviderService;
import org.picketlink.as.subsystem.service.IdentityProviderService;
import org.picketlink.as.subsystem.service.PicketLinkMetricsService;
import org.picketlink.as.subsystem.service.PicketLinkSubsystemMetrics;
import org.picketlink.identity.federation.core.config.IDPConfiguration;

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

        AbstractEntityProviderService<IdentityProviderService, IDPConfiguration> identityProviderService = new IdentityProviderService(
                context, operation);

        ServiceController<IdentityProviderService> controller = context
                .getServiceTarget()
                .addService(IdentityProviderService.createServiceName(pathAddress.getLastElement().getValue()),
                        identityProviderService).addListener(verificationHandler).setInitialMode(Mode.ACTIVE).install();

        PicketLinkMetricsService metricsService = new PicketLinkMetricsService(identityProviderService.getConfiguration());

        ServiceController<PicketLinkSubsystemMetrics> controllerMetrics = context
                .getServiceTarget()
                .addService(PicketLinkMetricsService.createServiceName(pathAddress.getLastElement().getValue()), metricsService)
                .addDependencies(NamingService.SERVICE_NAME).addDependencies(SecurityBootstrapService.SERVICE_NAME).addListener(verificationHandler).setInitialMode(Mode.ACTIVE).install();

        identityProviderService.setMetricsService(metricsService);

        newControllers.add(controller);
        newControllers.add(controllerMetrics);
    }

}
