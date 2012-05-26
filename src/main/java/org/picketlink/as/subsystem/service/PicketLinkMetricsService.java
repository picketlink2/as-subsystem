package org.picketlink.as.subsystem.service;

import org.jboss.msc.service.Service;
import org.jboss.msc.service.ServiceName;
import org.jboss.msc.service.StartContext;
import org.jboss.msc.service.StartException;
import org.jboss.msc.service.StopContext;
import org.picketlink.identity.federation.core.config.ProviderConfiguration;
import org.picketlink.identity.federation.core.exceptions.ConfigurationException;

public class PicketLinkMetricsService implements Service<PicketLinkSubsystemMetrics> {
    
    private ProviderConfiguration configuration;
    private PicketLinkSubsystemMetrics audit;
    
    public PicketLinkMetricsService(ProviderConfiguration configuration) {
        this.configuration = configuration;
    }
    
    /* (non-Javadoc)
     * @see org.jboss.msc.value.Value#getValue()
     */
    @Override
    public PicketLinkSubsystemMetrics getValue() throws IllegalStateException, IllegalArgumentException {
        return audit;
    }
    
    public static ServiceName createServiceName(String alias) {
        return ServiceName.JBOSS.append("PicketLinkMetricsService", alias);
    }
    
    /* (non-Javadoc)
     * @see org.jboss.msc.service.Service#start(org.jboss.msc.service.StartContext)
     */
    @Override
    public void start(StartContext context) throws StartException {
        try {
            audit = new PicketLinkSubsystemMetrics(configuration.getSecurityDomain());
        } catch (ConfigurationException e) {
            throw new RuntimeException("Error installing PicketLink Subsystem Auditing.", e);
        }
    }

    /* (non-Javadoc)
     * @see org.jboss.msc.service.Service#stop(org.jboss.msc.service.StopContext)
     */
    @Override
    public void stop(StopContext context) {
        audit = null;
    }

}
