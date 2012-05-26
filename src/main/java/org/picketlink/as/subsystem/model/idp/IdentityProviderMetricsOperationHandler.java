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

import static org.jboss.as.controller.descriptions.ModelDescriptionConstants.NAME;
import static org.jboss.as.controller.descriptions.ModelDescriptionConstants.OP_ADDR;
import static org.jboss.as.web.WebMessages.MESSAGES;

import org.apache.catalina.connector.Connector;
import org.apache.coyote.RequestGroupInfo;
import org.jboss.as.controller.OperationContext;
import org.jboss.as.controller.OperationFailedException;
import org.jboss.as.controller.OperationStepHandler;
import org.jboss.as.controller.PathAddress;
import org.jboss.as.controller.SimpleAttributeDefinition;
import org.jboss.as.controller.SimpleAttributeDefinitionBuilder;
import org.jboss.as.web.Constants;
import org.jboss.as.web.WebSubsystemServices;
import org.jboss.dmr.ModelNode;
import org.jboss.dmr.ModelType;
import org.jboss.msc.service.ServiceController;
import org.picketlink.as.subsystem.model.ModelElement;
import org.picketlink.as.subsystem.service.PicketLinkMetricsService;
import org.picketlink.as.subsystem.service.PicketLinkSubsystemMetrics;

/**
 * @author <a href="mailto:psilva@redhat.com">Pedro Silva</a>
 *
 */
public class IdentityProviderMetricsOperationHandler implements OperationStepHandler {

    public static final IdentityProviderMetricsOperationHandler INSTANCE = new IdentityProviderMetricsOperationHandler();
    
    protected static final SimpleAttributeDefinition CREATED_ASSERTIONS_COUNT =
            new SimpleAttributeDefinitionBuilder(ModelElement.METRICS_CREATED_ASSERTIONS_COUNT.getName(), ModelType.INT, true)
                    .setStorageRuntime()
                    .build();

    protected static final SimpleAttributeDefinition RESPONSE_TO_SP_COUNT =
            new SimpleAttributeDefinitionBuilder(ModelElement.METRICS_RESPONSE_TO_SP_COUNT.getName(), ModelType.INT, true)
                    .setStorageRuntime()
                    .build();
    
    static final SimpleAttributeDefinition[] ATTRIBUTES = {
        CREATED_ASSERTIONS_COUNT,
        RESPONSE_TO_SP_COUNT
    };
    
    private IdentityProviderMetricsOperationHandler() {
        
    }
    
    /* (non-Javadoc)
     * @see org.jboss.as.controller.OperationStepHandler#execute(org.jboss.as.controller.OperationContext, org.jboss.dmr.ModelNode)
     */
    @Override
    public void execute(OperationContext context, ModelNode operation) throws OperationFailedException {
        if (context.isNormalServer()) {
            context.addStep(new OperationStepHandler() {
                @Override
                public void execute(OperationContext context, ModelNode operation) throws OperationFailedException {
                    final PathAddress address = PathAddress.pathAddress(operation.require(OP_ADDR));
                    final String name = address.getLastElement().getValue();
                    final String attributeName = operation.require(NAME).asString();

                    final ServiceController<?> controller = context.getServiceRegistry(false)
                            .getService(PicketLinkMetricsService.createServiceName(name));
                    if (controller != null) {
                        try {
                            final PicketLinkSubsystemMetrics metrics = (PicketLinkSubsystemMetrics) controller.getValue();
                            final ModelNode result = context.getResult();
                            if (ModelElement.METRICS_CREATED_ASSERTIONS_COUNT.getName().equals(attributeName)) {
                                result.set("" + metrics.getCreatedAssertionsCount());
                            } else if (ModelElement.METRICS_RESPONSE_TO_SP_COUNT.getName().equals(attributeName)) {
                                result.set("" + metrics.getResponseToSPCount());
                            }
                        } catch (Exception e) {
                            throw new OperationFailedException(new ModelNode().set(MESSAGES.failedToGetMetrics(e.getMessage())));
                        }
                    } else {
                        context.getResult().set(MESSAGES.noMetricsAvailable());
                    }
                    context.completeStep(OperationContext.RollbackHandler.NOOP_ROLLBACK_HANDLER);
                }
            }, OperationContext.Stage.RUNTIME);
        } else {
            context.getResult().set(MESSAGES.noMetricsAvailable());
        }
        context.completeStep(OperationContext.RollbackHandler.NOOP_ROLLBACK_HANDLER);
    }

}
