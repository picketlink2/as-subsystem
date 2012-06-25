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
package org.picketlink.as.subsystem.service;

import org.jboss.dmr.ModelNode;
import org.jboss.msc.service.Service;
import org.jboss.msc.service.ServiceController;
import org.jboss.msc.service.ServiceName;
import org.jboss.msc.service.ServiceRegistry;
import org.jboss.msc.service.StartContext;
import org.jboss.msc.service.StartException;
import org.jboss.msc.service.StopContext;
import org.picketlink.as.subsystem.model.ModelUtils;
import org.picketlink.identity.federation.core.config.KeyProviderType;
import org.picketlink.identity.federation.core.config.STSConfiguration;

/**
 * <p>
 * Service implementation for the Federation configuration.
 * </p>
 * 
 * @author <a href="mailto:psilva@redhat.com">Pedro Silva</a>
 */
public class FederationService implements Service<FederationService> {

    private static final String SERVICE_NAME = "FederationService";

    private String alias;
    
    private KeyProviderType keyProvider;
    private STSConfiguration samlConfig;

    private IdentityProviderService identityProviderService;

    public FederationService(String alias) {
        this.alias = alias;
    }

    /* (non-Javadoc)
     * @see org.jboss.msc.value.Value#getValue()
     */
    @Override
    public FederationService getValue() throws IllegalStateException, IllegalArgumentException {
        return this;
    }

    /* (non-Javadoc)
     * @see org.jboss.msc.service.Service#start(org.jboss.msc.service.StartContext)
     */
    @Override
    public void start(StartContext context) throws StartException {
      //TODO: start identity provider service
    }

    /* (non-Javadoc)
     * @see org.jboss.msc.service.Service#stop(org.jboss.msc.service.StopContext)
     */
    @Override
    public void stop(StopContext context) {
      //TODO: start identity provider service
    }

    /**
     * Returns a instance of the service associated with the given name.
     * 
     * @param registry
     * @param name
     * @return
     */
    public static FederationService getService(ServiceRegistry registry, ModelNode model) {
        ServiceController<?> container = registry.getService(FederationService.createServiceName(ModelUtils.getFederationAlias(model)));
        
        if (container != null) {
            return (FederationService) container.getValue();
        }
        
        return null;
    }
    
    /**
     * @return the idpConfiguration
     */
    public KeyProviderType getKeyProvider() {
        return this.keyProvider;
    }
    
    public void setKeyProvider(KeyProviderType keyProviderType) {
        this.keyProvider = keyProviderType;
    }
    
    public STSConfiguration getSamlConfig() {
        return this.samlConfig;
    }
    
    public void setSamlConfig(STSConfiguration samlConfig) {
        this.samlConfig = samlConfig;
    }

    /**
     * @param fedAlias
     * @param alias2
     * @return
     */
    public static ServiceName createServiceName(String alias) {
        return ServiceName.JBOSS.append(SERVICE_NAME, alias);
    }
    
    public void setIdentityProviderService(IdentityProviderService identityProviderService) {
        this.identityProviderService = identityProviderService;
    }

    public IdentityProviderService getIdentityProviderService() {
        return this.identityProviderService;
    }
    
}