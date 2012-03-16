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

package org.picketlink.as.subsystem.model;

/**
 * @author pedroigor
 * @since Mar 8, 2012
 */
public interface ModelKeys {

    static final String COMMON_ALIAS = "alias";
    static final String COMMON_URL = "url";
    
    static final String FEDERATION = "federation";
    
    static final String IDENTITY_PROVIDER = "identity-provider";
    static final String TRUST_DOMAIN = "trust-domain";
    static final String TRUST_DOMAIN_NAME = "name";
    static final String IDENTITY_PROVIDER_SIGN_OUTGOING_MESSAGES = "signOutgoingMessages";
    static final String IDENTITY_PROVIDER_IGNORE_INCOMING_SIGNATURES = "ignoreIncomingSignatures";
    
    static final String SERVICE_PROVIDER = "service-provider";

}
