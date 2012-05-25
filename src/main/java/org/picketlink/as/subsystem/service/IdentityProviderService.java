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
import org.picketlink.as.subsystem.model.event.IdentityProviderUpdateEvent;
import org.picketlink.identity.federation.core.config.IDPConfiguration;
import org.picketlink.identity.federation.core.config.KeyProviderType;
import org.picketlink.identity.federation.core.config.parser.ConfigWriter;
import org.picketlink.identity.federation.core.config.parser.IDPTypeConfigWriter;
import org.picketlink.identity.federation.core.config.parser.JBossWebConfigWriter;

/**
 * <p>
 * Service implementation to enable a deployed applications as a Identity Provider.
 * </p>
 * 
 * @author <a href="mailto:psilva@redhat.com">Pedro Silva</a>
 */
public class IdentityProviderService extends AbstractEntityProviderService<IdentityProviderService, IDPConfiguration> {

    private static final String SERVICE_NAME = "IDPConfigurationService";

    public IdentityProviderService(OperationContext context, ModelNode modelNode) {
        super(context, modelNode);
    }
    
    @Override
    protected IDPConfiguration toProviderType(ModelNode operation) {
        return ModelUtils.toIDPConfig(operation);
    }

    /* (non-Javadoc)
     * @see org.jboss.msc.service.Service#start(org.jboss.msc.service.StartContext)
     */
    @Override
    public void start(StartContext context) throws StartException {
        super.start(context);
        this.getFederationService().setIdentityProviderService(this);
        this.raiseUpdateEvent();
    }

    /* (non-Javadoc)
     * @see org.jboss.msc.service.Service#stop(org.jboss.msc.service.StopContext)
     */
    @Override
    public void stop(StopContext context) {
        super.stop(context);
        this.setConfiguration(new IDPConfiguration());
        this.getFederationService().setIdentityProviderService(null);
        this.raiseUpdateEvent();
    }

    /**
     * Configures a WAR as a Identity Provider.
     * 
     * @param warDeployment
     */
    public void configure(ResourceRoot warDeployment) {
        writeJBossWebConfig(warDeployment);
        writePicketLinkConfig(warDeployment);
    }

    /**
     * <p>
     * Writes the picketlink-idfed.xml config file.
     * </p>
     * 
     * @param warDeployment
     */
    private void writePicketLinkConfig(ResourceRoot warDeployment) {
        VirtualFile config = warDeployment.getRoot().getChild("WEB-INF/picketlink.xml");
        writeConfig(config, new IDPTypeConfigWriter(this.getConfiguration()), true);
    }

    /**
     * <p>
     * Writes the jboss-web.xml config file.
     * </p>
     * 
     * @param warDeployment
     */
    private void writeJBossWebConfig(ResourceRoot warDeployment) {
        VirtualFile context = warDeployment.getRoot().getChild("WEB-INF/jboss-web.xml");
        
        writeConfig(context, new JBossWebConfigWriter(this.getConfiguration()), false);
    }
    
    /**
     * <p>
     * Writes the contents to a file given the {@link ConfigWriter} instance.
     * </p>
     * 
     * @param file File to be created or to have the configurations added.
     * @param writer {@link ConfigWriter} instance specific to a given configuration file.
     * @param recreate Indicates if the file has to be recreated. 
     */
    private void writeConfig(VirtualFile file, ConfigWriter writer, boolean recreate) {
        try {
            if (recreate) {
                file.delete();
                file.getPhysicalFile().createNewFile();
            }

            writer.write(file.getPhysicalFile());
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }

    /**
     * Returns a instance of the service associated with the given name.
     * 
     * @param registry
     * @param name
     * @return
     */
    public static IdentityProviderService getService(ServiceRegistry registry, String name) {
        ServiceController<?> container = registry.getService(IdentityProviderService.createServiceName(name));
        
        if (container != null) {
            return (IdentityProviderService) container.getValue();
        }
        
        return null;
    }
    
    public static ServiceName createServiceName(String alias) {
        return ServiceName.JBOSS.append(SERVICE_NAME, alias);
    }

    /* (non-Javadoc)
     * @see org.picketlink.as.subsystem.model.events.KeyStoreObserver#onUpdateKeyStore(org.picketlink.identity.federation.core.config.KeyProviderType)
     */
    @Override
    public void onUpdateKeyProvider(KeyProviderType keyProviderType) {
        super.onUpdateKeyProvider(keyProviderType);
        
        if (keyProviderType == null) {
            this.getConfiguration().setSignOutgoingMessages(false);
            this.getConfiguration().setIgnoreIncomingSignatures(true);
        }
    }

    /**
     * <p>
     * Raises a {@IdentityProviderUpdateEvent}.
     * </p>
     */
    public void raiseUpdateEvent() {
        new IdentityProviderUpdateEvent(this.getConfiguration(), this.getFederationService().getEventManager()).raise();
    }

}