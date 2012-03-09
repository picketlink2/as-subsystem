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
import java.util.ArrayList;
import java.util.List;

import org.jboss.as.server.deployment.module.ResourceRoot;
import org.jboss.msc.service.Service;
import org.jboss.msc.service.ServiceName;
import org.jboss.msc.service.StartContext;
import org.jboss.msc.service.StartException;
import org.jboss.msc.service.StopContext;
import org.jboss.vfs.VirtualFile;

/**
 * @author pedroigor
 * 
 */
public class IDPConfigurationService implements Service<IDPConfigurationService> {

    private String alias;
    private String url;

    private List<String> trustDomains = new ArrayList<String>();
    private boolean signOutgoingMessages;
    private boolean ignoreIncomingSignatures;

    public IDPConfigurationService(String alias, String url) {

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

                // TODO: change this logic to use stax writers to generate the xml configuration files ! This is only for testing.
                if (contextCreated) {
                    FileOutputStream fis = new FileOutputStream(context.getPhysicalFile());
                    fis.write(("<Context><Valve className=\"org.picketlink.identity.federation.bindings.tomcat.idp.IDPSAMLDebugValve\" /><Valve className=\"org.picketlink.identity.federation.bindings.tomcat.idp.IDPWebBrowserSSOValve\" signOutgoingMessages=\""
                            + this.signOutgoingMessages + "\"  ignoreIncomingSignatures=\"" + this.ignoreIncomingSignatures + "\"/></Context>")
                            .getBytes());
                    fis.flush();
                    fis.close();
                }
                if (handlersCreated) {
                    FileOutputStream fis = new FileOutputStream(handlers.getPhysicalFile());
                    fis.write("<Handlers xmlns=\"urn:picketlink:identity-federation:handler:config:1.0\"><Handler class=\"org.picketlink.identity.federation.web.handlers.saml2.SAML2IssuerTrustHandler\"/><Handler class=\"org.picketlink.identity.federation.web.handlers.saml2.SAML2LogOutHandler\"/><Handler class=\"org.picketlink.identity.federation.web.handlers.saml2.SAML2AuthenticationHandler\"/><Handler class=\"org.picketlink.identity.federation.web.handlers.saml2.RolesGenerationHandler\"/></Handlers>"
                            .getBytes());
                    fis.flush();
                    fis.close();
                }
                if (configCreated) {
                    FileOutputStream fis = new FileOutputStream(config.getPhysicalFile());
                    StringBuffer configBuffer = new StringBuffer();

                    StringBuffer trustDomains = new StringBuffer();

                    for (String domain : this.trustDomains) {
                        if (trustDomains.length() > 0) {
                            trustDomains.append(",");
                        }
                        trustDomains.append(domain);
                    }

                    configBuffer.append(
                            "<PicketLinkIDP xmlns=\"urn:picketlink:identity-federation:config:1.0\"><IdentityURL>" + this.url
                                    + "</IdentityURL><Trust><Domains>" + trustDomains.toString() + "</Domains>").append("</Trust></PicketLinkIDP>");

                    fis.write(configBuffer.toString().getBytes());
                    fis.flush();
                    fis.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void addTrustDomain(String domain) {
        this.trustDomains.add(domain);
    }

    public static ServiceName createServiceName(String suffix) {
        return ServiceName.JBOSS.append("IDPConfigurationService", suffix);
    }

    /**
     * @param url
     */
    public void setUrl(String url) {
        this.url = url;
    }

    /**
     * @param signOutgoingMessages
     */
    public void setSignOutgoingMessages(boolean signOutgoingMessages) {
        this.signOutgoingMessages = signOutgoingMessages;
    }

    /**
     * @param ignoreIncomingSignatures
     */
    public void setIgnoreIncomingSignatures(boolean ignoreIncomingSignatures) {
        this.ignoreIncomingSignatures = ignoreIncomingSignatures;
    }
}