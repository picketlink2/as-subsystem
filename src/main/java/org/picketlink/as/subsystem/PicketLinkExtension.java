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
package org.picketlink.as.subsystem;

import static org.jboss.as.controller.descriptions.ModelDescriptionConstants.ADD;

import org.jboss.as.controller.Extension;
import org.jboss.as.controller.ExtensionContext;
import org.jboss.as.controller.PathElement;
import org.jboss.as.controller.SubsystemRegistration;
import org.jboss.as.controller.descriptions.ModelDescriptionConstants;
import org.jboss.as.controller.parsing.ExtensionParsingContext;
import org.jboss.as.controller.registry.AttributeAccess.Storage;
import org.jboss.as.controller.registry.ManagementResourceRegistration;
import org.picketlink.as.subsystem.describer.SubsystemProviders;
import org.picketlink.as.subsystem.model.ModelDefinition;
import org.picketlink.as.subsystem.model.handler.federation.FederationAddHandler;
import org.picketlink.as.subsystem.model.handler.federation.FederationAliasHandler;
import org.picketlink.as.subsystem.model.handler.federation.FederationRemoveHandler;
import org.picketlink.as.subsystem.model.handler.idp.DomainAddHandler;
import org.picketlink.as.subsystem.model.handler.idp.DomainNameHandler;
import org.picketlink.as.subsystem.model.handler.idp.DomainRemoveHandler;
import org.picketlink.as.subsystem.model.handler.idp.IdentityProviderAddHandler;
import org.picketlink.as.subsystem.model.handler.idp.IdentityProviderAliasHandler;
import org.picketlink.as.subsystem.model.handler.idp.IdentityProviderIgnoreInSignMsgHandler;
import org.picketlink.as.subsystem.model.handler.idp.IdentityProviderRemoveHandler;
import org.picketlink.as.subsystem.model.handler.idp.IdentityProviderSignOutgoingMessagesHandler;
import org.picketlink.as.subsystem.model.handler.idp.IdentityProviderURLHandler;

/**
 * <p>An extension to the JBoss Application Server to enable PicketLink configurations.</p>
 * <p>This class is the entry point for the initialization of subsystem's configurations.</p>
 * 
 * @author pedroigor
 * @sice Mar 7, 2012
 */
public class PicketLinkExtension implements Extension {

    /**
     * PicketLink Subsystem name
     */
    public static final String SUBSYSTEM_NAME = "picketlink";

    /* (non-Javadoc)
     * @see org.jboss.as.controller.Extension#initializeParsers(org.jboss.as.controller.parsing.ExtensionParsingContext)
     */
    @Override
    public void initializeParsers(ExtensionParsingContext context) {
        context.setSubsystemXmlMapping(SUBSYSTEM_NAME, Namespace.CURRENT.getUri(), Namespace.CURRENT.getXMLReader());
    }

    /* (non-Javadoc)
     * @see org.jboss.as.controller.Extension#initialize(org.jboss.as.controller.ExtensionContext)
     */
    @Override
    public void initialize(ExtensionContext context) {
        SubsystemRegistration subsystem = context.registerSubsystem(SUBSYSTEM_NAME, Namespace.CURRENT.getMajor(), Namespace.CURRENT.getMinor());

        registerSubsystemModels(subsystem);
        
        subsystem.registerXMLElementWriter(Namespace.CURRENT.getXMLWriter());
    }

    /**
     * Register all PicketLink subsystem models and configurations
     * 
     * @param subsystem
     */
    private void registerSubsystemModels(SubsystemRegistration subsystem) {
        ManagementResourceRegistration picketlink = registerPicketLinkSubsystem(subsystem);
        ManagementResourceRegistration federationChild = registerFederationModel(picketlink);
        ManagementResourceRegistration identityProviderChild = registerIDPModel(federationChild);
        registerTrusDomainModel(identityProviderChild);
        
        //TODO: Support more configurations
    }
    
    /**
     * Register operations and configurations for the PicketLink subsystem model.
     * 
     * @param subsystem
     * @return
     */
    private ManagementResourceRegistration registerPicketLinkSubsystem(SubsystemRegistration subsystem) {
        ManagementResourceRegistration picketlink = subsystem.registerSubsystemModel(SubsystemProviders.SUBSYSTEM);

        picketlink.registerOperationHandler(ADD, PicketLinkSubsystemAdd.INSTANCE, SubsystemProviders.SUBSYSTEM_ADD, false);

        return picketlink;
    }
    
    /**
     * Register operations and configurations for the federation model.
     * 
     * @param picketlink
     * @return
     */
    private ManagementResourceRegistration registerFederationModel(ManagementResourceRegistration picketlink) {
        ManagementResourceRegistration federationChild = picketlink.registerSubModel(
                PathElement.pathElement(ModelDefinition.FEDERATION.getKey()), SubsystemProviders.FEDERATION);
        federationChild.registerOperationHandler(ModelDescriptionConstants.ADD, FederationAddHandler.INSTANCE,
                FederationAddHandler.INSTANCE);
        federationChild.registerOperationHandler(ModelDescriptionConstants.REMOVE, FederationRemoveHandler.INSTANCE,
                FederationRemoveHandler.INSTANCE);
        federationChild.registerReadWriteAttribute(ModelDefinition.FEDERATION_ALIAS.getKey(), null,
                FederationAliasHandler.INSTANCE, Storage.CONFIGURATION);
        return federationChild;
    }
    
    /**
     * Register operations and configurations for the IDP model.
     * 
     * @param federationChild
     * @return
     */
    private ManagementResourceRegistration registerIDPModel(ManagementResourceRegistration federationChild) {
        ManagementResourceRegistration identityProviderChild = federationChild.registerSubModel(
                PathElement.pathElement(ModelDefinition.IDENTITY_PROVIDER.getKey()), SubsystemProviders.IDENTITY_PROVIDER);
        
        identityProviderChild.registerOperationHandler(ModelDescriptionConstants.ADD, IdentityProviderAddHandler.INSTANCE,
                IdentityProviderAddHandler.INSTANCE);
        identityProviderChild.registerOperationHandler(ModelDescriptionConstants.REMOVE,
                IdentityProviderRemoveHandler.INSTANCE, IdentityProviderRemoveHandler.INSTANCE);
        
        identityProviderChild.registerReadWriteAttribute(ModelDefinition.IDENTITY_PROVIDER_ALIAS.getKey(), null,
                IdentityProviderAliasHandler.INSTANCE, Storage.CONFIGURATION);
        identityProviderChild.registerReadWriteAttribute(ModelDefinition.IDENTITY_PROVIDER_URL.getKey(), null,
                IdentityProviderURLHandler.INSTANCE, Storage.CONFIGURATION);
        identityProviderChild.registerReadWriteAttribute(ModelDefinition.IDENTITY_PROVIDER_SIGN_OUTGOING_MESSAGES.getKey(), null,
                IdentityProviderSignOutgoingMessagesHandler.INSTANCE, Storage.CONFIGURATION);
        identityProviderChild.registerReadWriteAttribute(ModelDefinition.IDENTITY_PROVIDER_IGNORE_INCOMING_SIGNATURES.getKey(), null,
                IdentityProviderIgnoreInSignMsgHandler.INSTANCE, Storage.CONFIGURATION);
        
        return identityProviderChild;
    }
    
    /**
     * Register operations and configurations for the IDP's trust domains model.
     * 
     * @param identityProviderChild
     */
    private void registerTrusDomainModel(ManagementResourceRegistration identityProviderChild) {
        ManagementResourceRegistration domainChild = identityProviderChild.registerSubModel(
                PathElement.pathElement(ModelDefinition.TRUST_DOMAIN.getKey()), SubsystemProviders.TRUST_DOMAIN);
        domainChild.registerOperationHandler(ModelDescriptionConstants.ADD, DomainAddHandler.INSTANCE,
                DomainAddHandler.INSTANCE);
        domainChild.registerOperationHandler(ModelDescriptionConstants.REMOVE, DomainRemoveHandler.INSTANCE,
                DomainRemoveHandler.INSTANCE);
        domainChild.registerReadWriteAttribute(ModelDefinition.TRUST_DOMAIN_NAME.getKey(), null, DomainNameHandler.INSTANCE,
                Storage.CONFIGURATION);
    }

}
