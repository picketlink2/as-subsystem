package org.picketlink.as.subsystem.model.federation;

import java.util.List;

import org.jboss.as.controller.OperationContext;
import org.jboss.as.controller.OperationFailedException;
import org.jboss.as.controller.PathAddress;
import org.jboss.as.controller.ServiceVerificationHandler;
import org.jboss.as.controller.descriptions.ModelDescriptionConstants;
import org.jboss.dmr.ModelNode;
import org.jboss.msc.service.ServiceController;
import org.jboss.msc.service.ServiceController.Mode;
import org.picketlink.as.subsystem.model.ModelElement;
import org.picketlink.as.subsystem.model.sp.AbstractResourceAddStepHandler;
import org.picketlink.as.subsystem.service.FederationService;
import org.picketlink.as.subsystem.service.PicketLinkService;

public abstract class AbstractFederationChildAddHandler<T extends PicketLinkService<T>> extends AbstractResourceAddStepHandler {
    
    public AbstractFederationChildAddHandler(ModelElement modelElement) {
        super(modelElement);
    }

    @Override
    protected void performRuntime(OperationContext context, ModelNode operation, ModelNode model,
            ServiceVerificationHandler verificationHandler, List<ServiceController<?>> newControllers)
            throws OperationFailedException {
        doPerformRuntime(context, operation, model);
    }

    protected abstract void doPerformRuntime(OperationContext context, ModelNode operation, ModelNode model);

    protected FederationService getFederationService(OperationContext context, ModelNode operation) {
        return FederationService.getService(context.getServiceRegistry(true), getFederationAlias(operation));
    }

    protected T createChildService(OperationContext context, ModelNode operation,
            ServiceVerificationHandler verificationHandler, List<ServiceController<?>> newControllers) {
        T service = doCreateChildService(operation);

        ServiceController<T> controller = context.getServiceTarget().addService(service.getName(), service)
                .addListener(verificationHandler).setInitialMode(Mode.ACTIVE).install();

        newControllers.add(controller);

        return service;
    }

    private String getFederationAlias(ModelNode operation) {
        PathAddress pathAddress = PathAddress.pathAddress(operation.get(ModelDescriptionConstants.ADDRESS));
        return pathAddress.getElement(1).getValue();
    }

    protected T doCreateChildService(ModelNode operation) {
        throw new IllegalStateException("The service could not be created.");
    }

}
