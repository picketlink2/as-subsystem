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

import static org.jboss.as.controller.descriptions.ModelDescriptionConstants.DESCRIBE;

import org.jboss.as.controller.Extension;
import org.jboss.as.controller.ExtensionContext;
import org.jboss.as.controller.SubsystemRegistration;
import org.jboss.as.controller.descriptions.ResourceDescriptionResolver;
import org.jboss.as.controller.descriptions.StandardResourceDescriptionResolver;
import org.jboss.as.controller.operations.common.GenericSubsystemDescribeHandler;
import org.jboss.as.controller.parsing.ExtensionParsingContext;
import org.jboss.as.controller.registry.ManagementResourceRegistration;
import org.jboss.as.controller.registry.OperationEntry;
import org.picketlink.as.subsystem.model.federation.FederationResourceDefinition;

/**
 * <p>An extension to the JBoss Application Server to enable PicketLink configurations.</p>
 * <p>This class is the entry point for the initialization of the subsystem's configurations.</p>
 * 
 * @author <a href="mailto:psilva@redhat.com">Pedro Silva</a>
 * @since Mar 16, 2012
 */
public class PicketLinkExtension implements Extension {

    /**
     * PicketLink Subsystem name
     */
    public static final String SUBSYSTEM_NAME = "picketlink";
    
    /**
     * Resource bundle name/location used to load the model's description.
     */
    private static final String RESOURCE_NAME = PicketLinkExtension.class.getPackage().getName() + ".LocalDescriptions";

    /**
     * Returns a instance of <code>ResourceDescriptionResolver</code> to be used to load the model's description.
     * 
     * @param keyPrefix
     * @return
     */
    public static ResourceDescriptionResolver getResourceDescriptionResolver(final String keyPrefix) {
        return new StandardResourceDescriptionResolver(keyPrefix, RESOURCE_NAME, PicketLinkExtension.class.getClassLoader(), true, true);
    }

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

        ManagementResourceRegistration picketlink = subsystem.registerSubsystemModel(PicketLinkSubsystemRootResourceDefinition.INSTANCE);
        
        picketlink.registerOperationHandler(DESCRIBE, GenericSubsystemDescribeHandler.INSTANCE, GenericSubsystemDescribeHandler.INSTANCE, false, OperationEntry.EntryType.PRIVATE);

        picketlink.registerSubModel(FederationResourceDefinition.INSTANCE);
        
        subsystem.registerXMLElementWriter(Namespace.CURRENT.getXMLWriter());
    }

}
