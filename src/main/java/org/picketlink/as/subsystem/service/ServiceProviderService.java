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

import org.jboss.as.controller.OperationContext;
import org.jboss.as.server.deployment.module.ResourceRoot;
import org.jboss.dmr.ModelNode;
import org.jboss.msc.service.ServiceController;
import org.jboss.msc.service.ServiceName;
import org.jboss.msc.service.ServiceRegistry;
import org.jboss.msc.service.StartContext;
import org.jboss.msc.service.StartException;
import org.jboss.msc.service.StopContext;
import org.jboss.vfs.VirtualFile;
import org.picketlink.as.subsystem.model.ModelUtils;
import org.picketlink.as.subsystem.model.event.IdentityProviderObserver;
import org.picketlink.as.subsystem.model.event.IdentityProviderUpdateEvent;
import org.picketlink.identity.federation.core.config.IDPConfiguration;
import org.picketlink.identity.federation.core.config.KeyProviderType;
import org.picketlink.identity.federation.core.config.SPConfiguration;
import org.picketlink.identity.federation.core.config.parser.JBossWebConfigWriter;
import org.picketlink.identity.federation.core.config.parser.SPTypeConfigWriter;

/**
 * <p>
 * Service implementation to enable a deployed applications as a Service Provider.
 * </p>
 * 
 * @author <a href="mailto:psilva@redhat.com">Pedro Silva</a>
 */

public class ServiceProviderService extends AbstractEntityProviderService<ServiceProviderService, SPConfiguration> implements IdentityProviderObserver {

    public ServiceProviderService(OperationContext context, ModelNode modelNode) {
        super(context, modelNode);
        updateIdentityURL();
    }
    
    /* (non-Javadoc)
     * @see org.picketlink.as.subsystem.service.AbstractEntityProviderService#toProviderType(org.jboss.dmr.ModelNode)
     */
    @Override
    protected SPConfiguration toProviderType(ModelNode operation) {
        return ModelUtils.toSPConfig(operation);
    }

    /* (non-Javadoc)
     * @see org.jboss.msc.service.Service#start(org.jboss.msc.service.StartContext)
     */
    @Override
    public void start(StartContext context) throws StartException {
        super.start(context);
        getFederationService().getEventManager().addObserver(IdentityProviderUpdateEvent.class, this);
    }

    /* (non-Javadoc)
     * @see org.jboss.msc.service.Service#stop(org.jboss.msc.service.StopContext)
     */
    @Override
    public void stop(StopContext context) {
        super.stop(context);
        this.setConfiguration(null);
    }

    /**
     * Configures a WAR as a Identity Provider.
     * 
     * @param warDeployment
     */
    public void configure(ResourceRoot warDeployment) {
        VirtualFile context = warDeployment.getRoot().getChild("WEB-INF/jboss-web.xml");
        VirtualFile config = warDeployment.getRoot().getChild("WEB-INF/picketlink-idfed.xml");

        try {
            new JBossWebConfigWriter(getConfiguration()).write(context.getPhysicalFile());
            
            if (config.exists()) {
                config.delete();
            }
            
            if (config.getPhysicalFile().createNewFile()) {
                new SPTypeConfigWriter(getConfiguration()).write(config.getPhysicalFile());                    
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
    public static ServiceProviderService getService(ServiceRegistry registry, String name) {
        ServiceController<?> container = registry.getService(ServiceProviderService.createServiceName(name));
        
        if (container != null) {
            return (ServiceProviderService) container.getValue();
        }
        
        return null;
    }

    @Override
    public void setConfiguration(SPConfiguration configuration) {
        super.setConfiguration(configuration);
        updateIdentityURL();
    }
    
    private void updateIdentityURL() {
        if (getFederationService().getIdentityProviderService() != null) {
            getConfiguration().setIdentityURL(getFederationService().getIdentityProviderService().getConfiguration().getIdentityURL());            
        }
    }
    
    @Override
    public void onUpdateKeyProvider(KeyProviderType keyProviderType) {
        getConfiguration().setKeyProvider(keyProviderType);
    }

    @Override
    public void onUpdateIdentityProvider(IDPConfiguration idpType) {
        getConfiguration().setIdentityURL(idpType.getIdentityURL());
    }
}