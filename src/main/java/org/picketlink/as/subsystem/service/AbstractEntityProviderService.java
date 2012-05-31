package org.picketlink.as.subsystem.service;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import org.jboss.as.controller.OperationContext;
import org.jboss.as.server.deployment.DeploymentUnit;
import org.jboss.as.web.deployment.WarMetaData;
import org.jboss.as.web.ext.WebContextFactory;
import org.jboss.dmr.ModelNode;
import org.jboss.msc.service.StartContext;
import org.jboss.msc.service.StartException;
import org.jboss.msc.service.StopContext;
import org.picketlink.as.subsystem.PicketLinkLogger;
import org.picketlink.as.subsystem.metrics.PicketLinkSubsystemMetrics;
import org.picketlink.as.subsystem.model.event.KeyProviderEvent;
import org.picketlink.as.subsystem.model.event.KeyProviderObserver;
import org.picketlink.identity.federation.core.config.KeyProviderType;
import org.picketlink.identity.federation.core.config.PicketLinkType;
import org.picketlink.identity.federation.core.config.ProviderConfiguration;
import org.picketlink.identity.federation.core.config.ProviderType;
import org.picketlink.identity.federation.core.config.STSType;
import org.picketlink.identity.federation.core.exceptions.ConfigurationException;
import org.picketlink.identity.federation.core.handler.config.Handler;
import org.picketlink.identity.federation.core.handler.config.Handlers;
import org.picketlink.identity.federation.core.parsers.sts.STSConfigParser;
import org.picketlink.identity.federation.core.saml.v2.interfaces.SAML2Handler;
import org.picketlink.identity.federation.web.handlers.saml2.RolesGenerationHandler;
import org.picketlink.identity.federation.web.handlers.saml2.SAML2AuthenticationHandler;
import org.picketlink.identity.federation.web.handlers.saml2.SAML2IssuerTrustHandler;
import org.picketlink.identity.federation.web.handlers.saml2.SAML2LogOutHandler;
import org.picketlink.identity.federation.web.handlers.saml2.SAML2SignatureGenerationHandler;
import org.picketlink.identity.federation.web.handlers.saml2.SAML2SignatureValidationHandler;

public abstract class AbstractEntityProviderService<T, C extends ProviderConfiguration> implements PicketLinkService<T>, KeyProviderObserver {
    
    private PicketLinkType picketLinkType;
    private C configuration;
    private FederationService federationService;
    private PicketLinkSubsystemMetrics metrics;

    public AbstractEntityProviderService(OperationContext context, ModelNode operation) {
        this.federationService = FederationService.getService(context.getServiceRegistry(true), operation);
        this.configuration = toProviderType(operation);
        this.configuration.setKeyProvider(this.federationService.getKeyProvider());
    }

    /**
     * <p>
     * Converts a {@link ModelNode} to a {@link ProviderConfiguration} instance.
     * </p>
     * 
     * @param operation
     * @return
     */
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
    
    /**
     * <p>
     * Configures a {@link DeploymentUnit} as a PicketLink Provider.
     * </p>
     * 
     * @param deploymentUnit
     */
    public void configure(DeploymentUnit deploymentUnit) {
        installPicketLinkWebContextFactory(deploymentUnit);
        
        WarMetaData warMetaData = deploymentUnit.getAttachment(WarMetaData.ATTACHMENT_KEY);
        
        warMetaData.getMergedJBossWebMetaData().setSecurityDomain(this.getConfiguration().getSecurityDomain());
        
        doConfigureDeployment(deploymentUnit);
    }

    /**
     * <p>
     * Add a instance of {@link PicketLinkWebContextFactory} to the attachment list for this {@link DeploymentUnit} instance.
     * This methods allows to pass to the JBoss Web subsystem a custom {@link WebContextFactory} implementation that will be used
     * to configure the deployment unit.
     * </p>
     * 
     * @param deploymentUnit
     */
    private void installPicketLinkWebContextFactory(DeploymentUnit deploymentUnit) {
        deploymentUnit.putAttachment(WebContextFactory.ATTACHMENT, createPicketLinkWebContextFactory());
    }

    /**
     * <p>
     * Creates a instance of {@link PicketLinkWebContextFactory}.
     * </p>
     * 
     * @return
     */
    private PicketLinkWebContextFactory createPicketLinkWebContextFactory() {
        PicketLinkWebContextFactory webContextFactory = new PicketLinkWebContextFactory(new DomainModelConfigProvider(getPicketLinkType()));
        
        webContextFactory.setAuditHelper(getMetrics());
        
        return webContextFactory;
    }

    public PicketLinkSubsystemMetrics getMetrics() {
        if (this.metrics == null) {
            try {
                this.metrics = new PicketLinkSubsystemMetrics(configuration.getSecurityDomain());
            } catch (ConfigurationException e) {
                PicketLinkLogger.ROOT_LOGGER.error("Error while configuring the metrics collector. Metrics will not be collected.", e);
            }
        }
        
        return this.metrics;
    }
    
    /**
     * <p>
     * Subclasses should implement this method to configure a specific PicketLink Provider type. Eg.: Identity Provider or Service Provider. 
     * </p>
     * 
     * @param deploymentUnit
     */
    protected abstract void doConfigureDeployment(DeploymentUnit deploymentUnit);

    @SuppressWarnings("unchecked")
    @Override
    public T getValue() throws IllegalStateException, IllegalArgumentException {
        return (T) this;
    }
    
    public C getConfiguration() {
        // the subsystem does not support changing the keystoremanager class name.
        if (this.configuration.getKeyProvider() != null) {
            this.configuration.getKeyProvider().setClassName("org.picketlink.identity.federation.core.impl.KeyStoreKeyManager");
        }
        
        return configuration;
    }
    
    public void setConfiguration(C configuration) {
        this.configuration = configuration;
    }
    
    public FederationService getFederationService() {
        return this.federationService;
    }
    
    /* (non-Javadoc)
     * @see org.picketlink.as.subsystem.model.events.KeyStoreObserver#onUpdateKeyStore(org.picketlink.identity.federation.core.config.KeyProviderType)
     */
    @Override
    public void onUpdateKeyProvider(KeyProviderType keyProviderType) {
        this.configuration.setKeyProvider(keyProviderType);
    }
    
    public PicketLinkType getPicketLinkType() {
        if (this.picketLinkType == null) {
            this.picketLinkType = new PicketLinkType();
            this.picketLinkType.setStsType(createSTSType());
            this.picketLinkType.setHandlers(new Handlers());
            this.picketLinkType.setEnableAudit(true);
            configureCommonHandlers();
        }
        
        if (getFederationService().getSamlConfig() != null) {
            this.picketLinkType.getStsType().setTokenTimeout(getFederationService().getSamlConfig().getTokenTimeout());
            this.picketLinkType.getStsType().setClockSkew(getFederationService().getSamlConfig().getClockSkew());
        }
        
        this.picketLinkType.setIdpOrSP((ProviderType) getConfiguration());

        return this.picketLinkType;
    }

    private STSType createSTSType() {
        STSType stsType = null;
        
        InputStream stream = null;
        
        try {
            ClassLoader clazzLoader = getClass().getClassLoader();
            
            URL url = clazzLoader.getResource("core-sts.xml");

            if (url == null) {
                clazzLoader = Thread.currentThread().getContextClassLoader();
                url = clazzLoader.getResource("core-sts");
            }

            stream = url.openStream();
            stsType = (STSType) new STSConfigParser().parse(stream);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                stream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        
        return stsType;
    }

    protected void configureCommonHandlers() {
        addHandler(SAML2IssuerTrustHandler.class);
        addHandler(SAML2LogOutHandler.class);
        addHandler(SAML2AuthenticationHandler.class);
        addHandler(RolesGenerationHandler.class);
        addHandler(SAML2SignatureGenerationHandler.class);
        addHandler(SAML2SignatureValidationHandler.class);
    }
    
    protected void addHandler(Class<? extends SAML2Handler> handlerClassName) {
        Handler handler = new Handler();
        
        handler.setClazz(handlerClassName.getName());
        
        getPicketLinkType().getHandlers().add(handler);
    }
}
