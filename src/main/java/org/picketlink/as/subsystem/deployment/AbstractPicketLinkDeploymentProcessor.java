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
import org.jboss.as.server.deployment.DeploymentUnit;
import org.jboss.as.server.deployment.DeploymentUnitProcessingException;
import org.jboss.as.server.deployment.DeploymentUnitProcessor;
import org.jboss.as.server.deployment.Phase;
import org.jboss.msc.service.ServiceController;
import org.jboss.msc.service.ServiceName;
import org.jboss.msc.service.ServiceRegistry;
import org.picketlink.as.subsystem.PicketLinkLogger;
import org.picketlink.as.subsystem.service.PicketLinkService;

/**
 * <p>Abstract class for PicketLink deployment unit processors.</p>
 * 
 * @author <a href="mailto:psilva@redhat.com">Pedro Silva</a>
 * 
 */
public abstract class AbstractPicketLinkDeploymentProcessor<T extends PicketLinkService<T>> implements DeploymentUnitProcessor {

    public static final Phase PHASE = Phase.INSTALL;

    public static final int PRIORITY = 1;

    /*
     * (non-Javadoc)
     * 
     * @see org.jboss.as.server.deployment.DeploymentUnitProcessor#deploy(org.jboss.as.server.deployment.DeploymentPhaseContext)
     */
    @Override
    public void deploy(DeploymentPhaseContext phaseContext) throws DeploymentUnitProcessingException {
        DeploymentUnit deploymentUnit = phaseContext.getDeploymentUnit();
        
        String deploymentUnitName = deploymentUnit.getName();
        
        T service = getService(phaseContext.getServiceRegistry(), deploymentUnitName);

        // if a service exists for this deployment, configure it with the PicketLink configurations defined by the model.
        if (service != null) {
            PicketLinkLogger.ROOT_LOGGER.configuringDeployment(service.getClass().getSimpleName(), deploymentUnitName);
            service.configure(deploymentUnit);
        }
    }

    @SuppressWarnings("unchecked")
    private T getService(ServiceRegistry serviceRegistry, String deploymentUnitName) {
        ServiceController<T> container = (ServiceController<T>) serviceRegistry.getService(createServiceName(deploymentUnitName));

        if (container != null) {
            return container.getValue();
        }

        return null;
    }

    
    /**
     * <p>This method should be overriden by subclasses to return the {@link ServiceName} for the service associated with the given deployment unit name.</p>
     * 
     * @param deploymentUnitName
     * @return
     */
    protected abstract ServiceName createServiceName(String deploymentUnitName);

    /*
     * (non-Javadoc)
     * 
     * @see org.jboss.as.server.deployment.DeploymentUnitProcessor#undeploy(org.jboss.as.server.deployment.DeploymentUnit)
     */
    @Override
    public void undeploy(DeploymentUnit context) {
    }

}
