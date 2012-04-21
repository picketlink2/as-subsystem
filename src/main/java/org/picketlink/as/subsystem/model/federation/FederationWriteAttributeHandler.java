package org.picketlink.as.subsystem.model.federation;

import org.jboss.as.controller.AbstractWriteAttributeHandler;
import org.jboss.as.controller.OperationContext;
import org.jboss.as.controller.OperationFailedException;
import org.jboss.dmr.ModelNode;

public class FederationWriteAttributeHandler extends AbstractWriteAttributeHandler<Void> {

    public static final FederationWriteAttributeHandler INSTANCE = new FederationWriteAttributeHandler();

    private FederationWriteAttributeHandler() {
        super(FederationResourceDefinition.ALIAS);
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
