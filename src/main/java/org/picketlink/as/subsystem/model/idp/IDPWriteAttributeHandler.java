package org.picketlink.as.subsystem.model.idp;

import org.jboss.as.controller.AbstractWriteAttributeHandler;
import org.jboss.as.controller.OperationContext;
import org.jboss.as.controller.OperationFailedException;
import org.jboss.dmr.ModelNode;

public class IDPWriteAttributeHandler extends AbstractWriteAttributeHandler<Void> {

    public static final IDPWriteAttributeHandler INSTANCE = new IDPWriteAttributeHandler();
    
    private IDPWriteAttributeHandler() {
        super(IdentityProviderResourceDefinition.ALIAS, IdentityProviderResourceDefinition.EXTERNAL,
                IdentityProviderResourceDefinition.IGNORE_INCOMING_SIGNATURES, IdentityProviderResourceDefinition.SECURITY_DOMAIN,
                IdentityProviderResourceDefinition.SIGN_OUTGOING_MESSAGES, IdentityProviderResourceDefinition.URL);
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
