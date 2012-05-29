package org.picketlink.as.subsystem.model.sts.endpoint;

/*
 * JBoss, Home of Professional Open Source. Copyright 2009, Red Hat Middleware LLC, and individual contributors as
 * indicated by the @author tags. See the copyright.txt file in the distribution for a full listing of individual
 * contributors.
 * 
 * This is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either version 2.1 of the License, or (at your option) any
 * later version.
 * 
 * This software is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Lesser General Public License along with this software; if not, write to
 * the Free Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA, or see the FSF site:
 * http://www.fsf.org.
 */

import java.io.InputStream;
import java.net.URL;

import javax.annotation.Resource;
import javax.xml.ws.Service;
import javax.xml.ws.ServiceMode;
import javax.xml.ws.WebServiceContext;
import javax.xml.ws.WebServiceProvider;

import org.apache.cxf.annotations.EndpointProperties;
import org.apache.cxf.annotations.EndpointProperty;
import org.apache.cxf.interceptor.InInterceptors;
import org.apache.log4j.Logger;
import org.picketlink.identity.federation.core.ErrorCodes;
import org.picketlink.identity.federation.core.config.STSType;
import org.picketlink.identity.federation.core.exceptions.ConfigurationException;
import org.picketlink.identity.federation.core.parsers.sts.STSConfigParser;
import org.picketlink.identity.federation.core.wstrust.PicketLinkSTS;
import org.picketlink.identity.federation.core.wstrust.PicketLinkSTSConfiguration;
import org.picketlink.identity.federation.core.wstrust.STSConfiguration;

/**
 * <p>
 * Default implementation of the {@code SecurityTokenService} interface.
 * </p>
 * 
 * @author <a href="mailto:sguilhen@redhat.com">Stefan Guilhen</a>
 * @author <a href="mailto:pskopek@redhat.com">Peter Skopek</a>
 */
@WebServiceProvider(serviceName = "PicketLinkSTS", portName = "PicketLinkSTSPort", targetNamespace = "urn:picketlink:identity-federation:sts")
@ServiceMode(value = Service.Mode.MESSAGE)
@EndpointProperties(value = { 
        @EndpointProperty(key = "ws-security.callback-handler", value = "org.picketlink.as.subsystem.model.sts.endpoint.STSCallbackHandler") 
})
@InInterceptors(interceptors = { "org.jboss.wsf.stack.cxf.security.authentication.SubjectCreatingPolicyInterceptor",
        "org.picketlink.as.subsystem.model.sts.endpoint.POJOAuthorizationInterpcetor" })
public class PicketLinkSTService extends PicketLinkSTS {

    private static final String DEFAULT_STS_SUBSYSTEM_CONFIG_FILE = "/sts/picketlink-sts.xml";

    private static Logger log = Logger.getLogger(PicketLinkSTService.class);

    private STSType configToMerge;
    
    @Resource
    protected WebServiceContext context;

    @Resource
    public void setWSC(WebServiceContext wctx) {
        log.debug("Setting WebServiceContext = " + wctx);
        this.context = wctx;
    }

    @Override
    protected STSConfiguration getConfiguration() throws ConfigurationException {
        URL configurationFileURL = null;

        try {
            configurationFileURL = getClass().getResource(DEFAULT_STS_SUBSYSTEM_CONFIG_FILE);
            
            if (configurationFileURL == null) {
                return new PicketLinkSTSConfiguration();
            }

            InputStream stream = configurationFileURL.openStream();
            STSType stsConfig = (STSType) new STSConfigParser().parse(stream);
            STSConfiguration configuration = new PicketLinkSTSConfiguration(stsConfig);
            
            PicketLinkSTSConfiguration mergedConfig = new PicketLinkSTSConfiguration(this.configToMerge);

            mergedConfig.copy(configuration);

            return mergedConfig;
        } catch (Exception e) {
            throw new ConfigurationException(ErrorCodes.STS_CONFIGURATION_FILE_PARSING_ERROR + configurationFileURL + "]", e);
        }
    }

    public void setConfigToMerge(STSType configToMerge) {
        this.configToMerge = configToMerge;
    }
}