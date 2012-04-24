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
 *
 */
public class SAMLConfiguration {

    private int tokenTimeout;
    private int clockSkew;
    private boolean disableAuthStatement;
    private boolean disableSendingRoles;
    private boolean disableRolePicking;
    private String[] roles;
    private String assertionConsumerURL;
    private String nameIDFormat;

    public int getTokenTimeout() {
        return tokenTimeout;
    }

    public void setTokenTimeout(int tokenTimeout) {
        this.tokenTimeout = tokenTimeout;
    }

    public int getClockSkew() {
        return clockSkew;
    }

    public void setClockSkew(int clockSkew) {
        this.clockSkew = clockSkew;
    }

    public boolean isDisableAuthStatement() {
        return disableAuthStatement;
    }

    public void setDisableAuthStatement(boolean disableAuthStatement) {
        this.disableAuthStatement = disableAuthStatement;
    }

    public boolean isDisableSendingRoles() {
        return disableSendingRoles;
    }

    public void setDisableSendingRoles(boolean disableSendingRoles) {
        this.disableSendingRoles = disableSendingRoles;
    }

    public boolean isDisableRolePicking() {
        return disableRolePicking;
    }

    public void setDisableRolePicking(boolean disableRolePicking) {
        this.disableRolePicking = disableRolePicking;
    }

    public String[] getRoles() {
        return roles;
    }

    public void setRoles(String[] roles) {
        this.roles = roles;
    }

    public String getAssertionConsumerURL() {
        return assertionConsumerURL;
    }

    public void setAssertionConsumerURL(String assertionConsumerURL) {
        this.assertionConsumerURL = assertionConsumerURL;
    }

    public String getNameIDFormat() {
        return nameIDFormat;
    }

    public void setNameIDFormat(String nameIDFormat) {
        this.nameIDFormat = nameIDFormat;
    }

}
