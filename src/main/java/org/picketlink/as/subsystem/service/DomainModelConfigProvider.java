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

import org.picketlink.identity.federation.core.config.IDPType;
import org.picketlink.identity.federation.core.config.PicketLinkType;
import org.picketlink.identity.federation.core.config.SPType;
import org.picketlink.identity.federation.core.exceptions.ProcessingException;
import org.picketlink.identity.federation.web.config.AbstractSAMLConfigurationProvider;
import org.picketlink.identity.federation.web.util.SAMLConfigurationProvider;

/**
 * <p>
 * This class is a custom {@link SAMLConfigurationProvider} to be used to configure identity providers and service providers
 * with the configurations defined in the subsystem.
 * </p>
 * 
 * @author <a href="mailto:psilva@redhat.com">Pedro Silva</a>
 * 
 * @param <C>
 */
public class DomainModelConfigProvider extends AbstractSAMLConfigurationProvider {

    private PicketLinkType configuration;

    public DomainModelConfigProvider(PicketLinkType picketLinkType) {
        this.configuration = picketLinkType;
    }

    public PicketLinkType getConfiguration() {
        return this.configuration;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.picketlink.identity.federation.web.config.AbstractSAMLConfigurationProvider#getIDPConfiguration()
     */
    @Override
    public IDPType getIDPConfiguration() throws ProcessingException {
        if (this.configuration.getIdpOrSP() != null && this.configuration.getIdpOrSP() instanceof IDPType) {
            return (IDPType) this.configuration.getIdpOrSP();
        }

        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.picketlink.identity.federation.web.config.AbstractSAMLConfigurationProvider#getSPConfiguration()
     */
    @Override
    public SPType getSPConfiguration() throws ProcessingException {
        if (this.configuration.getIdpOrSP() != null && this.configuration.getIdpOrSP() instanceof SPType) {
            return (SPType) this.configuration.getIdpOrSP();
        }

        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.picketlink.identity.federation.web.config.AbstractSAMLConfigurationProvider#getPicketLinkConfiguration()
     */
    @Override
    public PicketLinkType getPicketLinkConfiguration() throws ProcessingException {
        return this.configuration;
    }

    /**
     * <p>
     * Indicates if this provider is a Service Provider Configuration Provider.
     * </p>
     * 
     * @return
     */
    public boolean isServiceProviderConfiguration() {
        try {
            return getSPConfiguration() != null;
        } catch (ProcessingException e) {
            e.printStackTrace();
        }

        return false;
    }

    /**
     * <p>
     * Indicates if this provider is an Identity Provider Configuration Provider.
     * </p>
     * 
     * @return
     */
    public boolean isIdentityProviderConfiguration() {
        try {
            return getIDPConfiguration() != null;
        } catch (ProcessingException e) {
            e.printStackTrace();
        }

        return false;
    }

}