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
package org.picketlink.as.subsystem.deployment;

import org.jboss.as.server.deployment.DeploymentPhaseContext;
import org.jboss.as.server.deployment.DeploymentUnitProcessor;
import org.jboss.msc.service.ServiceController;
import org.jboss.msc.service.ServiceRegistry;
import org.picketlink.as.subsystem.service.ServiceProviderService;

/**
 * <p>
 * Specific {@link DeploymentUnitProcessor} for Service Provider applications.
 * </p>
 * 
 * @author <a href="mailto:psilva@redhat.com">Pedro Silva</a>
 * @since Mar 9, 2012
 */
public class ServiceProviderDeploymentProcessor extends AbstractPicketLinkDeploymentProcessor<ServiceProviderService> {
    
    /* (non-Javadoc)
     * @see org.picketlink.as.subsystem.deployment.AbstractPicketLinkDeploymentProcessor#getService(org.jboss.as.server.deployment.DeploymentPhaseContext, java.lang.String)
     */
    @Override
    protected ServiceProviderService getService(ServiceRegistry serviceRegistry, String sufix) {
        ServiceController<?> container = serviceRegistry.getService(ServiceProviderService.createServiceName(sufix));

        if (container != null) {
            return (ServiceProviderService) container.getValue();
        }

        return null;
    }

}
