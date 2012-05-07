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
package org.picketlink.identity.federation.core.config.parser;

import org.picketlink.identity.federation.core.config.IDPType;
import org.picketlink.identity.federation.core.config.TrustType;

/**
 * <p>
 * This class is responsible to store all informations about a given Identity Provider deployment. The state is populated with
 * values from the subsystem configuration.
 * </p>
 * 
 * @author <a href="mailto:psilva@redhat.com">Pedro Silva</a>
 * @since Mar 12, 2012
 */
public class IDPTypeSubsystem extends IDPType implements ProviderType {

    private String alias;
    private String securityDomain;
    
    private boolean signOutgoingMessages;
    private boolean ignoreIncomingSignatures = true;

    public IDPTypeSubsystem() {
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
     * @return the signOutgoingMessages
     */
    public boolean isSignOutgoingMessages() {
        return signOutgoingMessages;
    }

    /**
     * @param signOutgoingMessages the signOutgoingMessages to set
     */
    public void setSignOutgoingMessages(boolean signOutgoingMessages) {
        this.signOutgoingMessages = signOutgoingMessages;
    }

    /**
     * @return the ignoreIncomingSignatures
     */
    public boolean isIgnoreIncomingSignatures() {
        return ignoreIncomingSignatures;
    }

    /**
     * @param ignoreIncomingSignatures the ignoreIncomingSignatures to set
     */
    public void setIgnoreIncomingSignatures(boolean ignoreIncomingSignatures) {
        this.ignoreIncomingSignatures = ignoreIncomingSignatures;
    }

    public String getSecurityDomain() {
        return this.securityDomain;
    }

    public void setSecurityDomain(String securityDomain) {
        this.securityDomain = securityDomain;
    }
    
}