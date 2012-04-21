package org.picketlink.as.subsystem.model.sts;

import org.jboss.as.controller.AbstractWriteAttributeHandler;
import org.jboss.as.controller.OperationContext;
import org.jboss.as.controller.OperationFailedException;
import org.jboss.dmr.ModelNode;

public class STSWriteAttributeHandler extends AbstractWriteAttributeHandler<Void>{

    public static final STSWriteAttributeHandler INSTANCE = new STSWriteAttributeHandler();
    
    private STSWriteAttributeHandler() {
        super(STSResourceDefinition.ENDPOINT, STSResourceDefinition.ENDPOINT, STSResourceDefinition.SECURITY_DOMAIN);
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
