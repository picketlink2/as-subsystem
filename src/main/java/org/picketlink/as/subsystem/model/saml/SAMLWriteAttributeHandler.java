package org.picketlink.as.subsystem.model.saml;

import org.jboss.as.controller.AbstractWriteAttributeHandler;
import org.jboss.as.controller.OperationContext;
import org.jboss.as.controller.OperationFailedException;
import org.jboss.dmr.ModelNode;

public class SAMLWriteAttributeHandler extends AbstractWriteAttributeHandler<Void> {

    public static final SAMLWriteAttributeHandler INSTANCE = new SAMLWriteAttributeHandler();
    
    private SAMLWriteAttributeHandler() {
        super(SAMLResourceDefinition.CLOCK_SKEW, SAMLResourceDefinition.TOKEN_TIMEOUT);
    }

    @Override
    protected boolean applyUpdateToRuntime(OperationContext context, ModelNode operation, String attributeName,
            ModelNode resolvedValue, ModelNode currentValue,
            org.jboss.as.controller.AbstractWriteAttributeHandler.HandbackHolder<Void> handbackHolder)
            throws OperationFailedException {
        return false;
    }

    @Override
    protected void revertUpdateToRuntime(OperationContext context, ModelNode operation, String attributeName,
            ModelNode valueToRestore, ModelNode valueToRevert, Void handback) throws OperationFailedException {

    }

}
