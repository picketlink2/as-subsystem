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

import java.util.HashMap;
import java.util.Map;


/**
 * <p>
 * This class is responsible to store all informations about a given Identity Provider deployment. The state is populated with
 * values from the subsystem configuration.
 * </p>
 * 
 * @author <a href="mailto:psilva@redhat.com">Pedro Silva</a>
 * @since Mar 12, 2012
 */
public class IDPConfiguration extends IDPType implements ProviderConfiguration {

    private String alias;
    private String securityDomain;
    
    private boolean signOutgoingMessages;
    private boolean ignoreIncomingSignatures = true;
    
    private Map<String, String> trustDomainAlias = new HashMap<String, String>();

    public IDPConfiguration() {
        this.setTrust(new TrustType());
        this.getTrust().setDomains("");
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    /**
     * Adds a new trust domain
     * 
     * @param domain
     */
    public void addTrustDomain(String domain, String certAlias) {
        if (this.getTrust().getDomains() != null 
                && this.getTrust().getDomains().indexOf(domain) == -1) {
            if (!this.getTrust().getDomains().isEmpty()) {
                this.getTrust().setDomains(this.getTrust().getDomains() + ",");
            }
            
            this.getTrust().setDomains(this.getTrust().getDomains() + domain);
            
            if (certAlias != null && !certAlias.trim().isEmpty()) {
                getTrustDomainAlias().put(domain, certAlias);
            } else {
                getTrustDomainAlias().put(domain, domain);
            }
        }
    }

    public void removeTrustDomain(String domain) {
        if (this.getTrust().getDomains() != null && !this.getTrust().getDomains().isEmpty()) {
            this.getTrust().setDomains("");
            
            String[] domains = this.getTrust().getDomains().split(",");

            for (String currentDomain : domains) {
                if (!domain.equals(currentDomain) && !"".equals(currentDomain.trim())) {
                    this.getTrust().setDomains(currentDomain + ",");
                }
            }
        } else if (this.getTrust().getDomains() == null){
            this.getTrust().setDomains("");
        }
        
        this.getTrustDomainAlias().remove(domain);
    }

    public String getSecurityDomain() {
        return this.securityDomain;
    }

    public void setSecurityDomain(String securityDomain) {
        this.securityDomain = securityDomain;
    }
 
    public Map<String, String> getTrustDomainAlias() {
        return trustDomainAlias;
    }
}