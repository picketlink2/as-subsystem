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

package org.picketlink.as.subsystem.model.idp.metadata;

import org.jboss.as.controller.SimpleAttributeDefinition;
import org.jboss.as.controller.SimpleAttributeDefinitionBuilder;
import org.jboss.as.controller.registry.ManagementResourceRegistration;
import org.jboss.dmr.ModelType;
import org.picketlink.as.subsystem.model.AbstractResourceDefinition;
import org.picketlink.as.subsystem.model.ModelElement;

/**
 * @author <a href="mailto:psilva@redhat.com">Pedro Silva</a>
 * @since Mar 16, 2012
 */
public class OrganizationResourceDefinition extends AbstractResourceDefinition {

    public static final OrganizationResourceDefinition INSTANCE = new OrganizationResourceDefinition();
    
    public static final SimpleAttributeDefinition NAME = new SimpleAttributeDefinitionBuilder(
            ModelElement.COMMON_NAME.getName(), ModelType.STRING, false).setAllowExpression(false).build();
    public static final SimpleAttributeDefinition URL = new SimpleAttributeDefinitionBuilder(
            ModelElement.COMMON_URL.getName(), ModelType.STRING, false).setAllowExpression(false).build();
    
    private OrganizationResourceDefinition() {
        super(ModelElement.IDENTITY_PROVIDER_SAML_METADATA_ORGANIZATION, OrganizationAddHandler.INSTANCE, OrganizationRemoveHandler.INSTANCE);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.jboss.as.controller.SimpleResourceDefinition#registerAttributes(org.jboss.as.controller.registry.
     * ManagementResourceRegistration)
     */
    @Override
    public void registerAttributes(ManagementResourceRegistration resourceRegistration) {
        addAttributeDefinition(NAME, null, OrganizationNameHandler.INSTANCE, resourceRegistration);
        addAttributeDefinition(URL, null, OrganizationURLHandler.INSTANCE, resourceRegistration);
    }

}
