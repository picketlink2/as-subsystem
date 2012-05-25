package org.picketlink.as.subsystem.service;

import org.apache.catalina.core.StandardContext;
import org.jboss.as.server.deployment.DeploymentUnit;
import org.jboss.as.server.deployment.DeploymentUnitProcessingException;
import org.jboss.as.web.ext.WebContextFactory;
import org.picketlink.identity.federation.bindings.tomcat.idp.IDPWebBrowserSSOValve;
import org.picketlink.identity.federation.bindings.tomcat.sp.ServiceProviderAuthenticator;

public class PicketLinkWebContextFactory implements WebContextFactory {

    private final DomainModelConfigProvider configProvider;

    public PicketLinkWebContextFactory(DomainModelConfigProvider picketLinkSubsysteConfigProvider) {
        this.configProvider = picketLinkSubsysteConfigProvider;
    }

    /* (non-Javadoc)
     * @see org.jboss.as.web.ext.WebContextFactory#createContext(org.jboss.as.server.deployment.DeploymentUnit)
     */
    @Override
    public StandardContext createContext(DeploymentUnit deploymentUnit) throws DeploymentUnitProcessingException {
        return new StandardContext();
    }

    /* (non-Javadoc)
     * @see org.jboss.as.web.ext.WebContextFactory#postProcessContext(org.jboss.as.server.deployment.DeploymentUnit, org.apache.catalina.core.StandardContext)
     */
    @Override
    public void postProcessContext(DeploymentUnit deploymentUnit, StandardContext webContext) {
        if (this.configProvider.isIdentityProviderConfiguration()) {
            IDPWebBrowserSSOValve valve = new IDPWebBrowserSSOValve();
            
            valve.setConfigProvider(this.configProvider);
            
            webContext.addValve(valve);
        } else {
            ServiceProviderAuthenticator valve = new ServiceProviderAuthenticator();
            
            valve.setConfigProvider(this.configProvider);
            
            webContext.addValve(valve);
        }
    }

}
