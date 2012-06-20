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


/**
 * @author <a href="mailto:psilva@redhat.com">Pedro Silva</a>
 */
public class STSConfiguration extends STSType implements ProviderConfiguration {

    private String securityDomain = "picketlink-sts";
    private String endpoint = "http://localhost:8080/picketlink-sts/PicketLinkSTS";
    private String alias;
    private String contextRoot = "picketlink-sts";
    private String urlPattern = "/*";
    private String wsdlLocation;
    private String portName = "PicketLinkSTSPort";
    private String namespace = "urn:picketlink:identity-federation:sts";
    
    @Override
    public String getSecurityDomain() {
        return this.securityDomain;
    }

    @Override
    public String getIdentityURL() {
        return null;
    }
    
    public void setSecurityDomain(String securityDomain) {
        this.securityDomain = securityDomain;
    }

    public void setEndpoint(String endpoint) {
        this.endpoint = endpoint;
    }
    
    public String getEndpoint() {
        return this.endpoint;
    }

    @Override
    public String getAlias() {
        return this.alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public String getContextRoot() {
        return this.contextRoot;
    }
    
    public void setContextRoot(String contextRoot) {
        this.contextRoot = contextRoot;
    }

    public String getUrlPattern() {
        return this.urlPattern;
    }
    
    public void setUrlPattern(String urlPattern) {
        this.urlPattern = urlPattern;
    }

    public String getWsdlLocation() {
        if (this.wsdlLocation == null) {
            this.wsdlLocation = this.getClass().getClassLoader().getResource("/sts/PicketLinkSTS.wsdl").toString();
        }
        return this.wsdlLocation;
    }
    
    public void setWsdlLocation(String wsdlLocation) {
        this.wsdlLocation = wsdlLocation;
    }

    public String getPortName() {
        return this.portName;
    }
    
    public void setPortName(String portName) {
        this.portName = portName;
    }

    public String getNamespace() {
        return this.namespace;
    }
    
    public void setNamespace(String namespace) {
        this.namespace = namespace;
    }
    
    @Override
    public String getSTSName() {
        String stsName = super.getSTSName();
        
        if (stsName == null) {
            super.setSTSName("PicketLinkSTS");
        }
        
        return stsName;
    }

    /* (non-Javadoc)
     * @see org.picketlink.identity.federation.core.config.ProviderConfiguration#getTrust()
     */
    @Override
    public TrustType getTrust() {
        throw new IllegalStateException("Method getTrust not implemented. You should not call this method");
    }

}
