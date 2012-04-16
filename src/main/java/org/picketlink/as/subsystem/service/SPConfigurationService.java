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


import java.io.IOException;

import org.picketlink.as.subsystem.model.event.IdentityProviderURLEvent;
import org.jboss.as.server.deployment.module.ResourceRoot;
import org.jboss.msc.service.Service;
import org.jboss.msc.service.ServiceController;
import org.jboss.msc.service.ServiceName;
import org.jboss.msc.service.ServiceRegistry;
import org.jboss.msc.service.StartContext;
import org.jboss.msc.service.StartException;
import org.jboss.msc.service.StopContext;
import org.jboss.vfs.VirtualFile;
import org.picketlink.as.subsystem.model.event.KeyProviderEvent;
import org.picketlink.identity.federation.core.config.KeyProviderType;
import org.picketlink.identity.federation.core.config.parser.HandlersConfigWriter;
import org.picketlink.identity.federation.core.config.parser.JBossWebConfigWriter;
import org.picketlink.identity.federation.core.config.parser.SPTypeConfigWriter;
import org.picketlink.identity.federation.core.config.parser.SPTypeSubsystem;

/**
 * <p>
 * Service implementation to enable a deployed applications as a Service Provider.
 * </p>
 * 
 * @author <a href="mailto:psilva@redhat.com">Pedro Silva</a>
 */

public class SPConfigurationService implements Service<SPConfigurationService>, KeyProviderEvent.KeyStoreObserver, IdentityProviderURLEvent.IdentityProviderURLObserver {

    private String alias;
    
    private SPTypeSubsystem spConfiguration = new SPTypeSubsystem(); 

    public SPConfigurationService(String alias, String url) {
        this.alias = alias;
        this.spConfiguration.setServiceURL(url);
    }

    /* (non-Javadoc)
     * @see org.jboss.msc.value.Value#getValue()
     */
    @Override
    public SPConfigurationService getValue() throws IllegalStateException, IllegalArgumentException {
        return this;
    }

    /* (non-Javadoc)
     * @see org.jboss.msc.service.Service#start(org.jboss.msc.service.StartContext)
     */
    @Override
    public void start(StartContext context) throws StartException {
        //TODO: start service provider service
    }

    /* (non-Javadoc)
     * @see org.jboss.msc.service.Service#stop(org.jboss.msc.service.StopContext)
     */
    @Override
    public void stop(StopContext context) {
      //TODO: stop service provider service
    }

    /**
     * Configures a WAR as a Identity Provider.
     * 
     * @param warDeployment
     */
    public void configure(ResourceRoot warDeployment) {
        VirtualFile context = warDeployment.getRoot().getChild("WEB-INF/jboss-web.xml");
        VirtualFile handlers = warDeployment.getRoot().getChild("WEB-INF/picketlink-handlers.xml");
        VirtualFile config = warDeployment.getRoot().getChild("WEB-INF/picketlink-idfed.xml");

        try {
            new JBossWebConfigWriter(this.spConfiguration).write(context.getPhysicalFile());
            
            if (handlers.getPhysicalFile().createNewFile()) {
                new HandlersConfigWriter(this.spConfiguration).write(handlers.getPhysicalFile());
            }
            if (config.getPhysicalFile().createNewFile()) {
                new SPTypeConfigWriter(this.spConfiguration).write(config.getPhysicalFile());                    
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static ServiceName createServiceName(String alias) {
        return ServiceName.JBOSS.append("SPConfigurationService", alias);
    }

    /**
     * Returns a instance of the service associated with the given name.
     * 
     * @param registry
     * @param name
     * @return
     */
    public static SPConfigurationService getService(ServiceRegistry registry, String name) {
        ServiceController<?> container = registry.getService(SPConfigurationService.createServiceName(name));
        
        if (container != null) {
            return (SPConfigurationService) container.getValue();
        }
        
        return null;
    }

    /**
     * @return the idpConfiguration
     */
    public SPTypeSubsystem getSPConfiguration() {
        return this.spConfiguration;
    }

    @Override
    public void onUpdateKeyStore(KeyProviderType keyProviderType) {
        this.spConfiguration.setKeyProvider(keyProviderType);
    }

    @Override
    public void onUpdateIdentityURL(String identityURL) {
        this.spConfiguration.setIdentityURL(identityURL);
    }
}