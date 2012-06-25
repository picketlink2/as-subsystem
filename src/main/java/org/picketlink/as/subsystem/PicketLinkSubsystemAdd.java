package org.picketlink.as.subsystem;

import java.util.List;

import org.jboss.as.controller.AbstractBoottimeAddStepHandler;
import org.jboss.as.controller.OperationContext;
import org.jboss.as.controller.OperationFailedException;
import org.jboss.as.controller.ServiceVerificationHandler;
import org.jboss.as.server.AbstractDeploymentChainStep;
import org.jboss.as.server.DeploymentProcessorTarget;
import org.jboss.dmr.ModelNode;
import org.jboss.msc.service.ServiceController;
import org.picketlink.as.subsystem.deployment.IdentityProviderDeploymentProcessor;
import org.picketlink.as.subsystem.deployment.PicketLinkDependencyDeploymentProcessor;
import org.picketlink.as.subsystem.deployment.ServiceProviderDeploymentProcessor;

/**
 * <p>
 * Handler responsible for adding the subsystem resource to the model and install the PicketLink deployment unit processors.
 * </p>
 * 
 * @author <a href="mailto:psilva@redhat.com">Pedro Silva</a>
 * @since Mar 16, 2012
 */
public class PicketLinkSubsystemAdd extends AbstractBoottimeAddStepHandler {

    public static final PicketLinkSubsystemAdd INSTANCE = new PicketLinkSubsystemAdd();

    /*
     * (non-Javadoc)
     * 
     * @see org.jboss.as.controller.AbstractAddStepHandler#populateModel(org.jboss.dmr.ModelNode, org.jboss.dmr.ModelNode)
     */
    @Override
    protected void populateModel(ModelNode operation, ModelNode model) throws OperationFailedException {
    }

    /* (non-Javadoc)
     * @see org.jboss.as.controller.AbstractBoottimeAddStepHandler#performBoottime(org.jboss.as.controller.OperationContext, org.jboss.dmr.ModelNode, org.jboss.dmr.ModelNode, org.jboss.as.controller.ServiceVerificationHandler, java.util.List)
     */
    @Override
    public void performBoottime(OperationContext context, ModelNode operation, ModelNode model,
            ServiceVerificationHandler verificationHandler, List<ServiceController<?>> newControllers)
            throws OperationFailedException {

        PicketLinkLogger.ROOT_LOGGER.activatingSubsystem();
        
        context.addStep(new AbstractDeploymentChainStep() {
            public void execute(DeploymentProcessorTarget processorTarget) {
                PicketLinkLogger.ROOT_LOGGER.trace("Installing the PicketLink Dependency deployment processor.");
                processorTarget.addDeploymentProcessor(PicketLinkDependencyDeploymentProcessor.PHASE, PicketLinkDependencyDeploymentProcessor.PRIORITY,
                        new PicketLinkDependencyDeploymentProcessor());
                PicketLinkLogger.ROOT_LOGGER.trace("Installing the PicketLink Identity Provider deployment processor.");
                processorTarget.addDeploymentProcessor(IdentityProviderDeploymentProcessor.PHASE,
                        IdentityProviderDeploymentProcessor.PRIORITY, new IdentityProviderDeploymentProcessor());
                PicketLinkLogger.ROOT_LOGGER.trace("Installing the PicketLink Service Provider deployment processor.");
                processorTarget.addDeploymentProcessor(ServiceProviderDeploymentProcessor.PHASE,
                        ServiceProviderDeploymentProcessor.PRIORITY, new ServiceProviderDeploymentProcessor());
            }
        }, OperationContext.Stage.RUNTIME);

    }

}