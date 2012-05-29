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

import java.util.HashMap;
import java.util.List;

import javax.xml.namespace.QName;

import org.jboss.as.controller.OperationContext;
import org.jboss.as.security.plugins.SecurityDomainContext;
import org.jboss.as.server.deployment.DeploymentUnit;
import org.jboss.as.web.VirtualHost;
import org.jboss.as.webservices.publish.EndpointPublisherImpl;
import org.jboss.as.webservices.publish.WSEndpointDeploymentUnit;
import org.jboss.dmr.ModelNode;
import org.jboss.msc.inject.Injector;
import org.jboss.msc.service.ServiceController;
import org.jboss.msc.service.ServiceName;
import org.jboss.msc.service.ServiceRegistry;
import org.jboss.msc.service.StartContext;
import org.jboss.msc.service.StartException;
import org.jboss.msc.service.StopContext;
import org.jboss.msc.value.InjectedValue;
import org.jboss.wsf.spi.deployment.Endpoint;
import org.jboss.wsf.spi.deployment.Reference;
import org.jboss.wsf.spi.metadata.webservices.PortComponentMetaData;
import org.jboss.wsf.spi.metadata.webservices.WebserviceDescriptionMetaData;
import org.jboss.wsf.spi.metadata.webservices.WebservicesMetaData;
import org.jboss.wsf.spi.publish.Context;
import org.picketlink.as.subsystem.model.ModelUtils;
import org.picketlink.as.subsystem.model.sts.endpoint.PicketLinkSTService;
import org.picketlink.identity.federation.core.config.STSConfiguration;

/**
 * <p>
 * Service implementation to enable a deployed applications as a Security Token Service.
 * </p>
 * 
 * @author <a href="mailto:psilva@redhat.com">Pedro Silva</a>
 */
public class SecurityTokenServiceService extends AbstractEntityProviderService<SecurityTokenServiceService, STSConfiguration> {

    private static final String SERVICE_NAME = "STSConfigurationService";
    private List<Endpoint> publishedEndpoints;

    private final InjectedValue<VirtualHost> hostInjector = new InjectedValue<VirtualHost>();
    private final InjectedValue<SecurityDomainContext> securityDomainContextValue = new InjectedValue<SecurityDomainContext>();

    public SecurityTokenServiceService(OperationContext context, ModelNode operation) {
        super(context, operation);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.picketlink.as.subsystem.service.AbstractEntityProviderService#start(org.jboss.msc.service.StartContext)
     */
    @Override
    public void start(final StartContext ctx) throws StartException {
        publishEndpoint();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.picketlink.as.subsystem.service.AbstractEntityProviderService#stop(org.jboss.msc.service.StopContext)
     */
    @Override
    public void stop(final StopContext ctx) {
        try {
            EndpointPublisherImpl publisher = new EndpointPublisherImpl(hostInjector.getValue().getHost());
            publisher.destroy(new Context(getConfiguration().getContextRoot(), publishedEndpoints));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.picketlink.as.subsystem.service.AbstractEntityProviderService#doConfigureDeployment(org.jboss.as.server.deployment
     * .DeploymentUnit)
     */
    @Override
    protected void doConfigureDeployment(DeploymentUnit deploymentUnit) {
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.jboss.msc.value.Value#getValue()
     */
    @Override
    public SecurityTokenServiceService getValue() throws IllegalStateException, IllegalArgumentException {
        return this;
    }

    /**
     * <p>
     * Publish the Security Token Service as a WS endpoint.
     * </p>
     * 
     * @throws StartException
     */
    private void publishEndpoint() throws StartException {
        try {
            EndpointPublisherImpl publisher = new EndpointPublisherImpl(getHostInjector().getValue().getHost(),
                    this.securityDomainContextValue.getValue());

            HashMap<String, String> urlPatternToClassName = new HashMap<String, String>();

            urlPatternToClassName.put(getConfiguration().getUrlPattern(), getEndpointImplementorClass().getName());

            WSEndpointDeploymentUnit unit = new WSEndpointDeploymentUnit(getEndpointImplementorClass().getClassLoader(),
                    getConfiguration().getContextRoot(), urlPatternToClassName, createWebServiceMetadata());

            publishedEndpoints = publisher.publish(null, unit);

            Endpoint endpoint = this.publishedEndpoints.get(0);

            Reference reference = endpoint.getInstanceProvider().getInstance(getEndpointImplementorClass().getName());

            PicketLinkSTService stsService = (PicketLinkSTService) reference.getValue();

            stsService.setConfigToMerge(getConfiguration());
        } catch (Exception e) {
            throw new StartException(e);
        }
    }

    private Class<PicketLinkSTService> getEndpointImplementorClass() {
        return PicketLinkSTService.class;
    }

    private WebservicesMetaData createWebServiceMetadata() {
        WebservicesMetaData webservicesMetaData = new WebservicesMetaData();

        WebserviceDescriptionMetaData webserviceDescription = new WebserviceDescriptionMetaData(webservicesMetaData);

        webserviceDescription.setWsdlFile(getConfiguration().getWsdlLocation());

        PortComponentMetaData portMetadata = new PortComponentMetaData(webserviceDescription);

        portMetadata.setPortComponentName(getConfiguration().getPortName());
        portMetadata.setWsdlPort(new QName(getConfiguration().getNamespace(), getConfiguration().getPortName()));
        portMetadata.setWsdlService(new QName(getConfiguration().getNamespace(), getConfiguration().getSTSName()));

        webserviceDescription.addPortComponent(portMetadata);

        webservicesMetaData.addWebserviceDescription(webserviceDescription);

        return webservicesMetaData;
    }

    /**
     * Returns a instance of the service associated with the given name.
     * 
     * @param registry
     * @param name
     * @return
     */
    public static SecurityTokenServiceService getService(ServiceRegistry registry, String name) {
        ServiceController<?> container = registry.getService(SecurityTokenServiceService.createServiceName(name));

        if (container != null) {
            return (SecurityTokenServiceService) container.getValue();
        }

        return null;
    }

    public static ServiceName createServiceName(String alias) {
        return ServiceName.JBOSS.append(SERVICE_NAME, alias);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.picketlink.as.subsystem.service.AbstractEntityProviderService#toProviderType(org.jboss.dmr.ModelNode)
     */
    @Override
    protected STSConfiguration toProviderType(ModelNode fromModel) {
        return ModelUtils.toSTSConfig(fromModel);
    }

    public InjectedValue<VirtualHost> getHostInjector() {
        return hostInjector;
    }

    public Injector<SecurityDomainContext> getSecurityDomainContextInjector() {
        return securityDomainContextValue;
    }

}