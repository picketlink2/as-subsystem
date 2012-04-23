package org.picketlink.as.subsystem.deployment;

import org.jboss.as.server.deployment.Attachments;
import org.jboss.as.server.deployment.DeploymentPhaseContext;
import org.jboss.as.server.deployment.DeploymentUnit;
import org.jboss.as.server.deployment.DeploymentUnitProcessingException;
import org.jboss.as.server.deployment.DeploymentUnitProcessor;
import org.jboss.as.server.deployment.Phase;
import org.jboss.as.server.deployment.module.ResourceRoot;
import org.jboss.msc.service.ServiceController;
import org.jboss.msc.service.ServiceRegistry;
import org.picketlink.as.subsystem.service.IdentityProviderService;
import org.picketlink.as.subsystem.service.SecurityTokenServiceService;
import org.picketlink.as.subsystem.service.ServiceProviderService;

/**
 * <p>A custom deployment unit processor to handle application deployments, usually WAR files, and configuring them
 * based in the configuration defined for the PicketLink subsystem.</p>
 * 
 * @author <a href="mailto:psilva@redhat.com">Pedro Silva</a>
 * @since Mar 9, 2012
 */
public class PicketlinkDeploymentProcessor implements DeploymentUnitProcessor {

    /**
     * See {@link Phase} for a description of the different phases
     */
    public static final Phase PHASE = Phase.PARSE;

    /**
     * The relative order of this processor within the {@link #PHASE}. The current number is large enough for it to happen after
     * all the standard deployment unit processors that come with JBoss AS.
     */
    public static final int PRIORITY = 1;

    /* (non-Javadoc)
     * @see org.jboss.as.server.deployment.DeploymentUnitProcessor#deploy(org.jboss.as.server.deployment.DeploymentPhaseContext)
     */
    @Override
    public void deploy(DeploymentPhaseContext phaseContext) throws DeploymentUnitProcessingException {
        String name = phaseContext.getDeploymentUnit().getName();
        IdentityProviderService idpService = getIdentityProviderService(phaseContext.getServiceRegistry(), name);

        if (idpService != null) {
            ResourceRoot root = phaseContext.getDeploymentUnit().getAttachment(Attachments.DEPLOYMENT_ROOT);
            idpService.configure(root);
        }

        ServiceProviderService spService = getServiceProviderService(phaseContext.getServiceRegistry(), name);

        if (spService != null) {
            ResourceRoot root = phaseContext.getDeploymentUnit().getAttachment(Attachments.DEPLOYMENT_ROOT);
            spService.configure(root);
        }
        
        SecurityTokenServiceService stsService = getSecurityTokenServiceService(phaseContext.getServiceRegistry(), name);
        
        if (stsService != null) {
            ResourceRoot root = phaseContext.getDeploymentUnit().getAttachment(Attachments.DEPLOYMENT_ROOT);
            stsService.configure(root);
        }
}

    /* (non-Javadoc)
     * @see org.jboss.as.server.deployment.DeploymentUnitProcessor#undeploy(org.jboss.as.server.deployment.DeploymentUnit)
     */
    @Override
    public void undeploy(DeploymentUnit context) {
        //TODO: handle undeploy
    }

    /**
     * Returns a instance of the service responsible to configure applications as an IDP.
     * 
     * @param registry
     * @param name
     * @return
     */
    private IdentityProviderService getIdentityProviderService(ServiceRegistry registry, String name) {
        ServiceController<?> container = registry.getService(IdentityProviderService.createServiceName(name));
        
        if (container != null) {
            return (IdentityProviderService) container.getValue();
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
