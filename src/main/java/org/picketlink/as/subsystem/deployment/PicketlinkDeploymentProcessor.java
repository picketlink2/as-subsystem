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
import org.jboss.as.web.ext.WebContextFactory;
import org.jboss.msc.service.ServiceController;
import org.jboss.msc.service.ServiceRegistry;
import org.picketlink.as.subsystem.service.AbstractEntityProviderService;
import org.picketlink.as.subsystem.service.IdentityProviderService;
import org.picketlink.as.subsystem.service.PicketLinkWebContextFactory;
import org.picketlink.as.subsystem.service.SecurityTokenServiceService;
import org.picketlink.as.subsystem.service.ServiceProviderService;
import org.picketlink.identity.federation.core.config.IDPConfiguration;

/**
 * <p>
 * A custom deployment unit processor to handle application deployments, usually WAR files, and configuring them based in the
 * configuration defined for the PicketLink subsystem.
 * </p>
 * 
 * @author <a href="mailto:psilva@redhat.com">Pedro Silva</a>
 * @since Mar 9, 2012
 */
public class PicketlinkDeploymentProcessor implements DeploymentUnitProcessor {

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
        String name = phaseContext.getDeploymentUnit().getName();

        deployIdentityProvider(phaseContext, name);

        deployServiceProvider(phaseContext, name);

        deploySecurityTokenService(phaseContext, name);
    }
    
    private void deploySecurityTokenService(DeploymentPhaseContext phaseContext, String name) {
        SecurityTokenServiceService stsService = getSecurityTokenServiceService(phaseContext.getServiceRegistry(), name);

        if (stsService != null) {
            stsService.configure(phaseContext.getDeploymentUnit());
        }
    }

    private void deployServiceProvider(DeploymentPhaseContext phaseContext, String name) {
        ServiceProviderService spService = getServiceProviderService(phaseContext.getServiceRegistry(), name);

        if (spService != null) {
            spService.configure(phaseContext.getDeploymentUnit());
        }
    }

    private void deployIdentityProvider(DeploymentPhaseContext phaseContext, String name) {
        AbstractEntityProviderService<IdentityProviderService, IDPConfiguration> idpService = getIdentityProviderService(phaseContext.getServiceRegistry(), name);

        if (idpService != null) {
            idpService.configure(phaseContext.getDeploymentUnit());
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.jboss.as.server.deployment.DeploymentUnitProcessor#undeploy(org.jboss.as.server.deployment.DeploymentUnit)
     */
    @Override
    public void undeploy(DeploymentUnit context) {

    }

    /**
     * Returns a instance of the service responsible to configure applications as an IDP.
     * 
     * @param registry
     * @param name
     * @return
     */
    private AbstractEntityProviderService<IdentityProviderService, IDPConfiguration> getIdentityProviderService(ServiceRegistry registry, String name) {
        ServiceController<?> container = registry.getService(IdentityProviderService.createServiceName(name));

        if (container != null) {
            return (AbstractEntityProviderService<IdentityProviderService, IDPConfiguration>) container.getValue();
        }

        return null;
    }

    /**
     * Returns a instance of the service responsible to configure applications as an Service Provider.
     * 
     * @param registry
     * @param name
     * @return
     */
    private ServiceProviderService getServiceProviderService(ServiceRegistry registry, String name) {
        ServiceController<?> container = registry.getService(ServiceProviderService.createServiceName(name));

        if (container != null) {
            return (ServiceProviderService) container.getValue();
        }

        return null;
    }

    /**
     * Returns a instance of the service responsible to configure applications as an Security Token Service.
     * 
     * @param registry
     * @param name
     * @return
     */
    private SecurityTokenServiceService getSecurityTokenServiceService(ServiceRegistry registry, String name) {
        ServiceController<?> container = registry.getService(SecurityTokenServiceService.createServiceName(name));

        if (container != null) {
            return (SecurityTokenServiceService) container.getValue();
        }

        return null;
    }

}
