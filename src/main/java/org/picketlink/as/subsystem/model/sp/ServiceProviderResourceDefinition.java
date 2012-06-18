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
package org.picketlink.as.subsystem.model.sp;

import org.jboss.as.controller.OperationStepHandler;
import org.jboss.as.controller.SimpleAttributeDefinition;
import org.jboss.as.controller.SimpleAttributeDefinitionBuilder;
import org.jboss.as.controller.registry.ManagementResourceRegistration;
import org.jboss.dmr.ModelNode;
import org.jboss.dmr.ModelType;
import org.picketlink.as.subsystem.model.AbstractResourceDefinition;
import org.picketlink.as.subsystem.model.ModelElement;
import org.picketlink.as.subsystem.model.handlers.HandlerResourceDefinition;
import org.picketlink.as.subsystem.model.idp.IdentityProviderMetricsOperationHandler;
import org.picketlink.as.subsystem.model.idp.TrustDomainResourceDefinition;

/**
 * @author <a href="mailto:psilva@redhat.com">Pedro Silva</a>
 * @since Mar 16, 2012
 */
public class ServiceProviderResourceDefinition extends AbstractResourceDefinition {

    public static final ServiceProviderResourceDefinition INSTANCE = new ServiceProviderResourceDefinition();

    public static final SimpleAttributeDefinition ALIAS = new SimpleAttributeDefinitionBuilder(
            ModelElement.COMMON_ALIAS.getName(), ModelType.STRING, false).setDefaultValue(new ModelNode().set("sp"))
            .setAllowExpression(false).build();
    public static final SimpleAttributeDefinition SECURITY_DOMAIN = new SimpleAttributeDefinitionBuilder(
            ModelElement.COMMON_SECURITY_DOMAIN.getName(), ModelType.STRING, false).setAllowExpression(false).build();
    public static final SimpleAttributeDefinition URL = new SimpleAttributeDefinitionBuilder(ModelElement.COMMON_URL.getName(),
            ModelType.STRING, false).setAllowExpression(false).build();
    public static final SimpleAttributeDefinition POST_BINDING = new SimpleAttributeDefinitionBuilder(ModelElement.SERVICE_PROVIDER_POST_BINDING.getName(),
            ModelType.STRING, true).setAllowExpression(false).setDefaultValue(new ModelNode().set(true)).build();
    public static final SimpleAttributeDefinition SUPPORTS_SIGNATURES = new SimpleAttributeDefinitionBuilder(
            ModelElement.SUPPORTS_SIGNATURES.getName(), ModelType.BOOLEAN, true)
            .setDefaultValue(new ModelNode().set(false)).setAllowExpression(false).build();
    public static final SimpleAttributeDefinition STRICT_POST_BINDING = new SimpleAttributeDefinitionBuilder(
            ModelElement.STRICT_POST_BINDING.getName(), ModelType.BOOLEAN, true)
            .setDefaultValue(new ModelNode().set(true)).setAllowExpression(false).build();
    public static final SimpleAttributeDefinition ERROR_PAGE = new SimpleAttributeDefinitionBuilder(
            ModelElement.ERROR_PAGE.getName(), ModelType.STRING, true).setAllowExpression(false).build();
 
    static {
        INSTANCE.addAttribute(ALIAS);
        INSTANCE.addAttribute(SECURITY_DOMAIN);
        INSTANCE.addAttribute(URL);
        INSTANCE.addAttribute(POST_BINDING);
        INSTANCE.addAttribute(SUPPORTS_SIGNATURES);
        INSTANCE.addAttribute(STRICT_POST_BINDING);
        INSTANCE.addAttribute(ERROR_PAGE);
    }

    private ServiceProviderResourceDefinition() {
        super(ModelElement.SERVICE_PROVIDER, ServiceProviderAddHandler.INSTANCE, ServiceProviderRemoveHandler.INSTANCE);
    }

    /* (non-Javadoc)
     * @see org.jboss.as.controller.SimpleResourceDefinition#registerChildren(org.jboss.as.controller.registry.ManagementResourceRegistration)
     */
    @Override
    public void registerChildren(ManagementResourceRegistration resourceRegistration) {
        addChildResourceDefinition(HandlerResourceDefinition.INSTANCE, resourceRegistration);
    }
    
    /* (non-Javadoc)
     * @see org.picketlink.as.subsystem.model.AbstractResourceDefinition#registerResourceOperation(org.jboss.as.controller.registry.ManagementResourceRegistration)
     */
    @Override
    public void registerResourceOperation(ManagementResourceRegistration resourceRegistration) {
        resourceRegistration.registerOperationHandler(ServiceProviderReloadOperationHandler.OPERATION_NAME, ServiceProviderReloadOperationHandler.INSTANCE, ServiceProviderReloadOperationHandler.INSTANCE);
    }
    
    @Override
    public void registerAttributes(ManagementResourceRegistration resourceRegistration) {
        super.registerAttributes(resourceRegistration);
        
        for (final SimpleAttributeDefinition def : ServiceProviderMetricsOperationHandler.ATTRIBUTES) {
            resourceRegistration.registerMetric(def, ServiceProviderMetricsOperationHandler.INSTANCE);
        }
    }


    @Override
    protected OperationStepHandler doGetAttributeWriterHandler() {
        return SPWriteAttributeHandler.INSTANCE;
    }
}
