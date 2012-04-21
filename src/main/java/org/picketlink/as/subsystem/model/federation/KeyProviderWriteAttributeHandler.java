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
package org.picketlink.as.subsystem.model.federation;

import org.jboss.as.controller.AbstractWriteAttributeHandler;
import org.jboss.as.controller.OperationContext;
import org.jboss.as.controller.OperationFailedException;
import org.jboss.dmr.ModelNode;

/**
 * @author <a href="mailto:psilva@redhat.com">Pedro Silva</a>
 *
 */
public class KeyProviderWriteAttributeHandler extends AbstractWriteAttributeHandler<Void> {

    public static final KeyProviderWriteAttributeHandler INSTANCE = new KeyProviderWriteAttributeHandler();

    private KeyProviderWriteAttributeHandler() {
        super(KeyProviderResourceDefinition.URL, KeyProviderResourceDefinition.SIGN_KEY_ALIAS, KeyProviderResourceDefinition.PASSWD, KeyProviderResourceDefinition.SIGN_KEY_PASSWD);
    }
    
    /* (non-Javadoc)
     * @see org.jboss.as.controller.AbstractWriteAttributeHandler#applyUpdateToRuntime(org.jboss.as.controller.OperationContext, org.jboss.dmr.ModelNode, java.lang.String, org.jboss.dmr.ModelNode, org.jboss.dmr.ModelNode, org.jboss.as.controller.AbstractWriteAttributeHandler.HandbackHolder)
     */
    @Override
    protected boolean applyUpdateToRuntime(OperationContext context, ModelNode operation, String attributeName,
            ModelNode resolvedValue, ModelNode currentValue,
            org.jboss.as.controller.AbstractWriteAttributeHandler.HandbackHolder<Void> handbackHolder)
            throws OperationFailedException {
        return false;
    }

    /* (non-Javadoc)
     * @see org.jboss.as.controller.AbstractWriteAttributeHandler#revertUpdateToRuntime(org.jboss.as.controller.OperationContext, org.jboss.dmr.ModelNode, java.lang.String, org.jboss.dmr.ModelNode, org.jboss.dmr.ModelNode, java.lang.Object)
     */
    @Override
    protected void revertUpdateToRuntime(OperationContext context, ModelNode operation, String attributeName,
            ModelNode valueToRestore, ModelNode valueToRevert, Void handback) throws OperationFailedException {
        
    }

}
