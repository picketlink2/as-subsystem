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
import org.jboss.msc.service.ServiceRegistry;
import org.picketlink.as.subsystem.PicketLinkLogger;
import org.picketlink.as.subsystem.service.PicketLinkService;

/**
 * <p>
 * Abstract class for PicketLink deployment unit processors. Subclasses should handle application
 * deployments, usually WAR files, and configuring them based in the configuration defined for the PicketLink subsystem.
 * </p>
 * 
 * 
 * @author <a href="mailto:psilva@redhat.com">Pedro Silva</a>
 * 
 */
public abstract class AbstractPicketLinkDeploymentProcessor<T extends PicketLinkService<T>> implements DeploymentUnitProcessor {

    /**
     * See {@link Phase} for a description of the different phases
     */
    public static final Phase PHASE = Phase.INSTALL;

    /**
     * The relative order of this processor within the {@link #PHASE}. The current number is large enough for it to happen after
     * all the standard deployment unit processors that come with JBoss AS.
     */
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

        if (service != null) {
            PicketLinkLogger.ROOT_LOGGER.configuringDeployment(service.getClass().getSimpleName(), deploymentUnitName);
            service.configure(deploymentUnit);
        }
    }

    protected abstract T getService(ServiceRegistry serviceRegistry, String sufix);

    /*
     * (non-Javadoc)
     * 
     * @see org.jboss.as.server.deployment.DeploymentUnitProcessor#undeploy(org.jboss.as.server.deployment.DeploymentUnit)
     */
    @Override
    public void undeploy(DeploymentUnit context) {
    }

}
