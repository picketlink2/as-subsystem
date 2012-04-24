package org.picketlink.as.subsystem.service;

import org.jboss.as.controller.OperationContext;
import org.jboss.dmr.ModelNode;
import org.jboss.msc.service.Service;
import org.jboss.msc.service.StartContext;
import org.jboss.msc.service.StartException;
import org.jboss.msc.service.StopContext;
import org.picketlink.as.subsystem.model.event.KeyProviderEvent;
import org.picketlink.as.subsystem.model.event.KeyProviderObserver;
import org.picketlink.identity.federation.core.config.KeyProviderType;
import org.picketlink.identity.federation.core.config.ProviderConfiguration;

public abstract class AbstractEntityProviderService<T, C extends ProviderConfiguration> implements Service<T>, KeyProviderObserver {

    private C configuration;
    private FederationService federationService;
    
    public AbstractEntityProviderService(OperationContext context, ModelNode operation) {
        this.federationService = FederationService.getService(context.getServiceRegistry(true), operation);
        this.configuration = toProviderType(operation);
        this.configuration.setKeyProvider(this.federationService.getKeyProvider());
    }

    protected abstract C toProviderType(ModelNode operation);

    @Override
    public void start(StartContext context) throws StartException {
        this.federationService.getEventManager().addObserver(KeyProviderEvent.class, this);
    }
    
    /* (non-Javadoc)
     * @see org.jboss.msc.service.Service#stop(org.jboss.msc.service.StopContext)
     */
    @Override
    public void stop(StopContext context) {
        this.federationService.getEventManager().removeObserver(this);
    }
    
    @SuppressWarnings("unchecked")
    @Override
    public T getValue() throws IllegalStateException, IllegalArgumentException {
        return (T) this;
    }
    
    public C getConfiguration() {
        return configuration;
    }
    
    public void setConfiguration(C configuration) {
        this.configuration = configuration;
    }
    
    protected FederationService getFederationService() {
        return this.federationService;
    }
    
    /* (non-Javadoc)
     * @see org.picketlink.as.subsystem.model.events.KeyStoreObserver#onUpdateKeyStore(org.picketlink.identity.federation.core.config.KeyProviderType)
     */
    @Override
    public void onUpdateKeyProvider(KeyProviderType keyProviderType) {
        this.configuration.setKeyProvider(keyProviderType);
    }
}
