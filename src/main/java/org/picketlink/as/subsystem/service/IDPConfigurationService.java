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

import java.io.FileOutputStream;
import java.io.IOException;

import org.jboss.as.server.deployment.module.ResourceRoot;
import org.jboss.msc.service.Service;
import org.jboss.msc.service.ServiceName;
import org.jboss.msc.service.StartContext;
import org.jboss.msc.service.StartException;
import org.jboss.msc.service.StopContext;
import org.jboss.vfs.VirtualFile;
import org.picketlink.identity.federation.core.config.parser.ContextConfigWriter;
import org.picketlink.identity.federation.core.config.parser.HandlersConfigWriter;
import org.picketlink.identity.federation.core.config.parser.IDPTypeConfigWriter;
import org.picketlink.identity.federation.core.config.parser.IDPTypeSubsystem;

/**
 * @author pedroigor
 * 
 */
public class IDPConfigurationService implements Service<IDPConfigurationService> {

    private String alias;
    
    private IDPTypeSubsystem idpConfiguration = new IDPTypeSubsystem(); 

    public IDPConfigurationService(String alias, String url) {
        this.alias = alias;
        this.idpConfiguration.setIdentityURL(url);
    }

    @Override
    public IDPConfigurationService getValue() throws IllegalStateException, IllegalArgumentException {
        return this;
    }

    @Override
    public void start(StartContext context) throws StartException {
    }

    @Override
    public void stop(StopContext context) {
    }

    public void configure(ResourceRoot root) {
        VirtualFile context = root.getRoot().getChild("WEB-INF/context.xml");
        VirtualFile handlers = root.getRoot().getChild("WEB-INF/picketlink-handlers.xml");
        VirtualFile config = root.getRoot().getChild("WEB-INF/picketlink-idfed.xml");

        if (!context.exists()) {
            try {
                boolean contextCreated = context.getPhysicalFile().createNewFile();
                boolean handlersCreated = handlers.getPhysicalFile().createNewFile();
                boolean configCreated = config.getPhysicalFile().createNewFile();

                if (contextCreated) {
                    new ContextConfigWriter(this.idpConfiguration).write(new FileOutputStream(context.getPhysicalFile()));
                }
                if (handlersCreated) {
                    new HandlersConfigWriter(this.idpConfiguration).write(new FileOutputStream(handlers.getPhysicalFile()));
                }
                if (configCreated) {
                    new IDPTypeConfigWriter(this.idpConfiguration).write(new FileOutputStream(config.getPhysicalFile()));                    
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static ServiceName createServiceName(String alias) {
        return ServiceName.JBOSS.append("IDPConfigurationService", alias);
    }

    /**
     * @return the idpConfiguration
     */
    public IDPTypeSubsystem getIdpConfiguration() {
        return this.idpConfiguration;
    }
}