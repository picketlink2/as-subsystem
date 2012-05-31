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

package org.picketlink.as.subsystem.metrics;

import static org.jboss.as.controller.descriptions.ModelDescriptionConstants.NAME;
import static org.jboss.as.controller.descriptions.ModelDescriptionConstants.OP_ADDR;
import static org.jboss.as.web.WebMessages.MESSAGES;

import org.jboss.as.controller.OperationContext;
import org.jboss.as.controller.OperationFailedException;
import org.jboss.as.controller.OperationStepHandler;
import org.jboss.as.controller.PathAddress;
import org.jboss.as.controller.SimpleAttributeDefinition;
import org.jboss.as.controller.SimpleAttributeDefinitionBuilder;
import org.jboss.dmr.ModelNode;
import org.jboss.dmr.ModelType;
import org.jboss.msc.service.ServiceController;
import org.jboss.msc.service.ServiceName;
import org.picketlink.as.subsystem.model.ModelElement;
import org.picketlink.as.subsystem.service.IdentityProviderService;
import org.picketlink.as.subsystem.service.PicketLinkService;

/**
 * @author <a href="mailto:psilva@redhat.com">Pedro Silva</a>
 *
 */
public abstract class AbstractPicketLinkMetricsOperationHandler implements OperationStepHandler {

    protected static final SimpleAttributeDefinition CREATED_ASSERTIONS_COUNT =
            new SimpleAttributeDefinitionBuilder(ModelElement.METRICS_CREATED_ASSERTIONS_COUNT.getName(), ModelType.INT, true)
                    .setStorageRuntime()
                    .build();

    protected static final SimpleAttributeDefinition RESPONSE_TO_SP_COUNT =
            new SimpleAttributeDefinitionBuilder(ModelElement.METRICS_RESPONSE_TO_SP_COUNT.getName(), ModelType.INT, true)
                    .setStorageRuntime()
                    .build();

    protected static final SimpleAttributeDefinition ERROR_RESPONSE_TO_SP_COUNT =
            new SimpleAttributeDefinitionBuilder(ModelElement.METRICS_ERROR_RESPONSE_TO_SP_COUNT.getName(), ModelType.INT, true)
                    .setStorageRuntime()
                    .build();

    protected static final SimpleAttributeDefinition ERROR_SIGN_VALIDATION_COUNT =
            new SimpleAttributeDefinitionBuilder(ModelElement.METRICS_ERROR_SIGN_VALIDATION_COUNT.getName(), ModelType.INT, true)
                    .setStorageRuntime()
                    .build();

    protected static final SimpleAttributeDefinition ERROR_TRUSTED_DOMAIN_COUNT =
            new SimpleAttributeDefinitionBuilder(ModelElement.METRICS_ERROR_TRUSTED_DOMAIN_COUNT.getName(), ModelType.INT, true)
                    .setStorageRuntime()
                    .build();

    protected static final SimpleAttributeDefinition EXPIRED_ASSERTIONS_COUNT =
            new SimpleAttributeDefinitionBuilder(ModelElement.METRICS_EXPIRED_ASSERTIONS_COUNT.getName(), ModelType.INT, true)
                    .setStorageRuntime()
                    .build();

    protected static final SimpleAttributeDefinition LOGIN_INIT_COUNT =
            new SimpleAttributeDefinitionBuilder(ModelElement.METRICS_LOGIN_INIT_COUNT.getName(), ModelType.INT, true)
                    .setStorageRuntime()
                    .build();

    protected static final SimpleAttributeDefinition LOGIN_COMPLETE_COUNT =
            new SimpleAttributeDefinitionBuilder(ModelElement.METRICS_LOGIN_COMPLETE_COUNT.getName(), ModelType.INT, true)
                    .setStorageRuntime()
                    .build();

    protected static final SimpleAttributeDefinition REQUEST_FROM_IDP_COUNT =
            new SimpleAttributeDefinitionBuilder(ModelElement.METRICS_REQUEST_FROM_IDP_COUNT.getName(), ModelType.INT, true)
                    .setStorageRuntime()
                    .build();

    protected static final SimpleAttributeDefinition RESPONSE_FROM_IDP_COUNT =
            new SimpleAttributeDefinitionBuilder(ModelElement.METRICS_RESPONSE_FROM_IDP_COUNT.getName(), ModelType.INT, true)
                    .setStorageRuntime()
                    .build();

    protected static final SimpleAttributeDefinition REQUEST_TO_IDP_COUNT =
            new SimpleAttributeDefinitionBuilder(ModelElement.METRICS_REQUEST_TO_IDP_COUNT.getName(), ModelType.INT, true)
                    .setStorageRuntime()
                    .build();

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

                    final ServiceController<PicketLinkService<?>> controller = (ServiceController<PicketLinkService<?>>) context.getServiceRegistry(false)
                            .getService(createServiceName(name));
                    if (controller != null) {
                        try {
                            doPopulateResult(controller.getValue().getMetrics(), context.getResult(), attributeName);
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
    
    protected void doPopulateResult(PicketLinkSubsystemMetrics metrics, ModelNode result, String attributeName) {
    }

    protected abstract ServiceName createServiceName(String name);

}
