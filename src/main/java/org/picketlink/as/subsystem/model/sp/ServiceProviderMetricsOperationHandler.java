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

import org.jboss.as.controller.SimpleAttributeDefinition;
import org.jboss.dmr.ModelNode;
import org.jboss.msc.service.ServiceName;
import org.picketlink.as.subsystem.metrics.AbstractPicketLinkMetricsOperationHandler;
import org.picketlink.as.subsystem.metrics.PicketLinkSubsystemMetrics;
import org.picketlink.as.subsystem.model.ModelElement;
import org.picketlink.as.subsystem.service.ServiceProviderService;

/**
 * <p>
 * Provide access to the metrics collected from a specific Service Provider deployment.
 * </p>
 * 
 * @author <a href="mailto:psilva@redhat.com">Pedro Silva</a>
 * 
 */
public class ServiceProviderMetricsOperationHandler extends AbstractPicketLinkMetricsOperationHandler {

    public static final ServiceProviderMetricsOperationHandler INSTANCE = new ServiceProviderMetricsOperationHandler();

    static final SimpleAttributeDefinition[] ATTRIBUTES = { 
        EXPIRED_ASSERTIONS_COUNT,
        REQUEST_FROM_IDP_COUNT,
        REQUEST_TO_IDP_COUNT,
        RESPONSE_FROM_IDP_COUNT};

    private ServiceProviderMetricsOperationHandler() {

    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.picketlink.as.subsystem.metrics.AbstractPicketLinkMetricsOperationHandler#doPopulateResult(org.jboss.as.controller
     * .OperationContext, java.lang.String, org.jboss.msc.service.ServiceController)
     */
    @Override
    protected void doPopulateResult(PicketLinkSubsystemMetrics metrics, ModelNode result, String attributeName) {
        if (ModelElement.METRICS_EXPIRED_ASSERTIONS_COUNT.getName().equals(attributeName)) {
            result.set("" + metrics.getExpiredAssertionsCount());
        } else if (ModelElement.METRICS_REQUEST_FROM_IDP_COUNT.getName().equals(attributeName)) {
            result.set("" + metrics.getRequestFromIDPCount());
        } else if (ModelElement.METRICS_REQUEST_TO_IDP_COUNT.getName().equals(attributeName)) {
            result.set("" + metrics.getRequestToIDPCount());
        } else if (ModelElement.METRICS_RESPONSE_FROM_IDP_COUNT.getName().equals(attributeName)) {
            result.set("" + metrics.getResponseFromIDPCount());
        }

    }

    @Override
    protected ServiceName createServiceName(String name) {
        return ServiceProviderService.createServiceName(name);
    }

}
