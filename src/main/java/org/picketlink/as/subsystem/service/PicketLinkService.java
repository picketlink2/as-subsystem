package org.picketlink.as.subsystem.service;

import org.jboss.as.server.deployment.DeploymentUnit;
import org.jboss.msc.service.Service;
import org.picketlink.as.subsystem.metrics.PicketLinkSubsystemMetrics;
import org.picketlink.identity.federation.core.config.ProviderConfiguration;

public interface PicketLinkService<T> extends Service<T> {

    FederationService getFederationService();
    
    ProviderConfiguration getConfiguration();
    
    void configure(DeploymentUnit deploymentUnit);

    PicketLinkSubsystemMetrics getMetrics();
    
    void reset();
    
}
