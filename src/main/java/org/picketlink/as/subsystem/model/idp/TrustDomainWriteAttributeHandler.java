package org.picketlink.as.subsystem.model.idp;

import org.jboss.as.controller.AbstractWriteAttributeHandler;
import org.jboss.as.controller.OperationContext;
import org.jboss.as.controller.OperationFailedException;
import org.jboss.dmr.ModelNode;

public class TrustDomainWriteAttributeHandler extends AbstractWriteAttributeHandler<Void> {

    public static final TrustDomainWriteAttributeHandler INSTANCE = new TrustDomainWriteAttributeHandler();
    
    private TrustDomainWriteAttributeHandler() {
        super(TrustDomainResourceDefinition.NAME);
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
