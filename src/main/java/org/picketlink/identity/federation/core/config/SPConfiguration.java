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

package org.picketlink.identity.federation.core.config;

import java.util.Map;

import org.picketlink.identity.federation.core.config.SPType;

/**
 * <p>
 * This class is responsible to store all informations about a given Service Provider deployment. The state is
 * populated with values from the subsystem configuration. 
 * </p>
 * 
 * @author <a href="mailto:psilva@redhat.com">Pedro Silva</a>
 * @since Mar 12, 2012
 */
public class SPConfiguration extends SPType implements ProviderConfiguration {

    private boolean postBinding;
    private String securityDomain;
    private String alias;

    /**
     * @param url
     */
    public void setPostBinding(boolean url) {
        this.postBinding = url;
    }
    
    /**
     * @return the postBinding
     */
    public boolean isPostBinding() {
        return this.postBinding;
    }

    public String getSecurityDomain() {
        return securityDomain;
    }

    public void setSecurityDomain(String securityDomain) {
        this.securityDomain = securityDomain;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }
    
    public String getAlias() {
        return alias;
    }

    @Override
    public Map<String, String> getTrustDomainAlias() {
        // TODO Auto-generated method stub
        return null;
    }
    
}
