package org.picketlink.as.subsystem.model.sp;

import org.jboss.as.controller.AbstractWriteAttributeHandler;
import org.jboss.as.controller.OperationContext;
import org.jboss.as.controller.OperationFailedException;
import org.jboss.dmr.ModelNode;

public class SPWriteAttributeHandler extends AbstractWriteAttributeHandler<Void> {

    public static final SPWriteAttributeHandler INSTANCE = new SPWriteAttributeHandler();
    
    private SPWriteAttributeHandler() {
        super(ServiceProviderResourceDefinition.ALIAS, ServiceProviderResourceDefinition.POST_BINDING,
                ServiceProviderResourceDefinition.SECURITY_DOMAIN, ServiceProviderResourceDefinition.URL);
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
