/*
 * JBoss, Home of Professional Open Source.
 * Copyright 2012, Red Hat, Inc., and individual contributors
 * as indicated by the @author tags. See the copyright.txt file in the
 * distribution for a full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.picketlink.as.subsystem.service;

import static org.picketlink.identity.federation.core.config.PicketLinkConfigUtil.addHandler;
import static org.picketlink.identity.federation.core.config.PicketLinkConfigUtil.createSTSType;

import java.util.ArrayList;
import java.util.List;

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
import org.picketlink.identity.federation.core.config.KeyValueType;
import org.picketlink.identity.federation.core.config.PicketLinkType;
import org.picketlink.identity.federation.core.config.ProviderConfiguration;
import org.picketlink.identity.federation.core.config.ProviderType;
import org.picketlink.identity.federation.core.config.STSConfiguration;
import org.picketlink.identity.federation.core.config.TokenProviderType;
import org.picketlink.identity.federation.core.exceptions.ConfigurationException;
import org.picketlink.identity.federation.core.handler.config.Handler;
import org.picketlink.identity.federation.core.handler.config.Handlers;
import org.picketlink.identity.federation.core.saml.v2.constants.JBossSAMLURIConstants;
import org.picketlink.identity.federation.web.constants.GeneralConstants;
import org.picketlink.identity.federation.web.handlers.saml2.RolesGenerationHandler;
import org.picketlink.identity.federation.web.handlers.saml2.SAML2AuthenticationHandler;
import org.picketlink.identity.federation.web.handlers.saml2.SAML2IssuerTrustHandler;
import org.picketlink.identity.federation.web.handlers.saml2.SAML2LogOutHandler;
import org.picketlink.identity.federation.web.handlers.saml2.SAML2SignatureGenerationHandler;
import org.picketlink.identity.federation.web.handlers.saml2.SAML2SignatureValidationHandler;

/**
 * @author <a href="mailto:psilva@redhat.com">Pedro Silva</a>
 *
 * @param <T>
 * @param <C>
 */
public abstract class AbstractEntityProviderService<T extends PicketLinkService<T>, C extends ProviderConfiguration> implements PicketLinkService<T>, KeyProviderObserver {
    
    private PicketLinkType picketLinkType;
    private C configuration;
    private FederationService federationService;
    private PicketLinkSubsystemMetrics metrics;
    private static List<Class> commonHandlersList;

    static {
        commonHandlersList = new ArrayList<Class>();
        commonHandlersList.add(SAML2IssuerTrustHandler.class);
        commonHandlersList.add(SAML2LogOutHandler.class);
        commonHandlersList.add(SAML2AuthenticationHandler.class);
        commonHandlersList.add(RolesGenerationHandler.class);
        commonHandlersList.add(SAML2SignatureGenerationHandler.class);
        commonHandlersList.add(SAML2SignatureValidationHandler.class);
    }
    
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
    public void reset() {
        this.federationService.getEventManager().removeObserver(this);
        this.configuration = null;
        this.picketLinkType = null;
        this.metrics = null;
    }
    
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
        configureHandlers();
        configureWarMetadata(deploymentUnit);
        configurePicketLinkWebContextFactory(deploymentUnit);
        doConfigureDeployment(deploymentUnit);
    }

    /**
     * <p>Configure the STS Token Providers.</p>
     */
    private void configureTokenProviders() {
        STSConfiguration samlConfig = getFederationService().getSamlConfig();
        
        if (samlConfig != null) {
            int tokenTimeout = samlConfig.getTokenTimeout();
            int clockSkew = samlConfig.getClockSkew();
            
            this.picketLinkType.getStsType().setTokenTimeout(tokenTimeout);
            this.picketLinkType.getStsType().setClockSkew(clockSkew);
            
            List<TokenProviderType> tokenProviders = this.picketLinkType.getStsType().getTokenProviders().getTokenProvider();
            
            for (TokenProviderType tokenProviderType : tokenProviders) {
                if (tokenProviderType.getTokenType().equals(JBossSAMLURIConstants.ASSERTION_NSURI.get())) {
                    KeyValueType keyValueTypeTokenTimeout = new KeyValueType();
                    
                    keyValueTypeTokenTimeout.setKey(GeneralConstants.ASSERTIONS_VALIDITY);
                    keyValueTypeTokenTimeout.setValue(String.valueOf(tokenTimeout));

                    KeyValueType keyValueTypeClockSkew = new KeyValueType();
                    
                    keyValueTypeClockSkew.setKey(GeneralConstants.CLOCK_SKEW);
                    keyValueTypeClockSkew.setValue(String.valueOf(clockSkew));

                    tokenProviderType.add(keyValueTypeTokenTimeout);
                    tokenProviderType.add(keyValueTypeClockSkew);
                }
            }
        }
    }

    /**
     * <p>Configure the SAML Handlers.</p>
     */
    private void configureHandlers() {
        List<Handler> handlers = getPicketLinkType().getHandlers().getHandler();
        
        // remove the common handlers from the configuration. leaving only the user defined handlers.
        for (Class commonHandlerClass : commonHandlersList) {
            for (Handler handler : new ArrayList<Handler>(handlers)) {
                if (handler.getClazz().equals(commonHandlerClass.getName())) {
                    getPicketLinkType().getHandlers().remove(handler);
                }
            }
        }
        
        doAddHandlers();
    }
    
    /**
     * <p>Adds the common handlers into the configuration.</p>
     */
    protected void doAddHandlers() {
        for (Class commonHandlerClass : commonHandlersList) {
            addHandler(commonHandlerClass, getPicketLinkType());
        }
    }

    /**
     * <p>Configures the {@link WarMetaData}.</p>
     * 
     * @param deploymentUnit
     */
    private void configureWarMetadata(DeploymentUnit deploymentUnit) {
        WarMetaData warMetaData = deploymentUnit.getAttachment(WarMetaData.ATTACHMENT_KEY);
        
        warMetaData.getMergedJBossWebMetaData().setSecurityDomain(this.getConfiguration().getSecurityDomain());
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
    private void configurePicketLinkWebContextFactory(DeploymentUnit deploymentUnit) {
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

    /* (non-Javadoc)
     * @see org.picketlink.as.subsystem.service.PicketLinkService#getMetrics()
     */
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
    
    /* (non-Javadoc)
     * @see org.picketlink.as.subsystem.model.events.KeyStoreObserver#onUpdateKeyStore(org.picketlink.identity.federation.core.config.KeyProviderType)
     */
    @Override
    public void onUpdateKeyProvider(KeyProviderType keyProviderType) {
        this.configuration.setKeyProvider(keyProviderType);
    }

    public C getConfiguration() {
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

    public PicketLinkType getPicketLinkType() {
        if (this.picketLinkType == null) {
            this.picketLinkType = new PicketLinkType();
            this.picketLinkType.setStsType(createSTSType());
            this.picketLinkType.setHandlers(new Handlers());
            this.picketLinkType.setEnableAudit(true);
        }
        
        this.picketLinkType.setIdpOrSP((ProviderType) getConfiguration());
        
        configureTokenProviders();

        return this.picketLinkType;
    }

}
