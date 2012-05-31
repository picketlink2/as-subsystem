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

package org.picketlink.as.subsystem.metrics;

import org.jboss.security.audit.AuditEvent;
import org.picketlink.identity.federation.core.audit.PicketLinkAuditEvent;
import org.picketlink.identity.federation.core.audit.PicketLinkAuditHelper;
import org.picketlink.identity.federation.core.exceptions.ConfigurationException;

/**
 * <p>
 * This class provides ways to store metrics collected from the PicketLink providers (IDPs and SPs).
 * </p>
 * 
 * @author <a href="mailto:psilva@redhat.com">Pedro Silva</a>
 *
 */
public class PicketLinkSubsystemMetrics extends PicketLinkAuditHelper {
    
    private int createdAssertionsCount;
    private int responseToSPCount;
    private int errorResponseToSPCount;
    private int errorSignValidationCount;
    private int errorTrustedDomainCount;
    private int expiredAssertionsCount;
    private int loginInitCount;
    private int loginCompleteCount;
    private int requestFromIDPCount;
    private int responseFromIDPCount;
    private int requestToIDPCount;

    public PicketLinkSubsystemMetrics(String securityDomainName) throws ConfigurationException {
        super(securityDomainName);
    }
    
    @Override
    public void audit(AuditEvent event) {
        PicketLinkAuditEvent picketLinkEvent = (PicketLinkAuditEvent) event;
        
        switch (picketLinkEvent.getType()) {
            case CREATED_ASSERTION:
                createdAssertionsCount++;
                break;
            case RESPONSE_TO_SP:
                responseToSPCount++;
                break;
            case ERROR_RESPONSE_TO_SP:
                errorResponseToSPCount++;
                break;
            case ERROR_SIG_VALIDATION:
                errorSignValidationCount++;
                break;
            case ERROR_TRUSTED_DOMAIN:
                errorTrustedDomainCount++;
                break;
            case EXPIRED_ASSERTION:
                expiredAssertionsCount++;
                break;
            case LOGIN_INIT:
                loginInitCount++;
                break;
            case LOGIN_COMPLETE:
                loginCompleteCount++;
                break;
            case REQUEST_FROM_IDP:
                requestFromIDPCount++;
                break;
            case REQUEST_TO_IDP:
                requestToIDPCount++;
                break;
            case RESPONSE_FROM_IDP:
                responseFromIDPCount++;
                break;
        }
        
        super.audit(picketLinkEvent);
    }
    
    public int getCreatedAssertionsCount() {
        return createdAssertionsCount;
    }
    
    public int getResponseToSPCount() {
        return responseToSPCount;
    }

    /**
     * @return the errorResponseToSPCount
     */
    public int getErrorResponseToSPCount() {
        return errorResponseToSPCount;
    }

    /**
     * @return the errorSignValidationCount
     */
    public int getErrorSignValidationCount() {
        return errorSignValidationCount;
    }

    /**
     * @return the errorTrustedDomainCount
     */
    public int getErrorTrustedDomainCount() {
        return errorTrustedDomainCount;
    }

    /**
     * @return the expiredAssertionsCount
     */
    public int getExpiredAssertionsCount() {
        return expiredAssertionsCount;
    }

    /**
     * @return the loginInitCount
     */
    public int getLoginInitCount() {
        return loginInitCount;
    }

    /**
     * @return the loginCompleteCount
     */
    public int getLoginCompleteCount() {
        return loginCompleteCount;
    }

    /**
     * @return the requestFromIDPCount
     */
    public int getRequestFromIDPCount() {
        return requestFromIDPCount;
    }

    /**
     * @return the responseFromIDPCount
     */
    public int getResponseFromIDPCount() {
        return responseFromIDPCount;
    }

    /**
     * @return the requestToIDPCount
     */
    public int getRequestToIDPCount() {
        return requestToIDPCount;
    }
    
    
}
