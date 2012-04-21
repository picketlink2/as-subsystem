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

package org.picketlink.as.subsystem.model;

import java.util.ArrayList;
import java.util.List;

import org.jboss.as.controller.OperationStepHandler;
import org.jboss.as.controller.PathElement;
import org.jboss.as.controller.ResourceDefinition;
import org.jboss.as.controller.SimpleAttributeDefinition;
import org.jboss.as.controller.SimpleResourceDefinition;
import org.jboss.as.controller.registry.ManagementResourceRegistration;
import org.picketlink.as.subsystem.PicketLinkExtension;
import org.picketlink.as.subsystem.model.idp.IDPWriteAttributeHandler;

/**
 * @author <a href="mailto:psilva@redhat.com">Pedro Silva</a>
 * @since Mar 18, 2012
 */
public abstract class AbstractResourceDefinition extends SimpleResourceDefinition {

    private ModelElement modelElement;
    private List<SimpleAttributeDefinition> attributes = new ArrayList<SimpleAttributeDefinition>();
    
    protected AbstractResourceDefinition(ModelElement modelElement, final OperationStepHandler addHandler, final OperationStepHandler removeHandler) {
        super(PathElement.pathElement(modelElement.getName()), PicketLinkExtension
                .getResourceDescriptionResolver(modelElement.getName()), addHandler,
                removeHandler);
        this.modelElement = modelElement;
    }
    
    @Override
    public void registerAttributes(ManagementResourceRegistration resourceRegistration) {
        for (SimpleAttributeDefinition attribute : getAttributes()) {
            addAttributeDefinition(attribute, null, doGetAttributeWriterHandler(), resourceRegistration);
        }
        
        registerResourceOperation(resourceRegistration);
    }

    protected void registerResourceOperation(ManagementResourceRegistration resourceRegistration) {
        
    }

    protected abstract OperationStepHandler doGetAttributeWriterHandler();

    public List<SimpleAttributeDefinition> getAttributes() {
        return this.attributes;
    }
    
    protected void addAttribute(SimpleAttributeDefinition attribute) {
        this.attributes.add(attribute);
    }

    protected void addAttributeDefinition(SimpleAttributeDefinition definition, OperationStepHandler readHandler,
            OperationStepHandler writeHandler, ManagementResourceRegistration resourceRegistration) {
        SubsystemDescriber.addAttributeDefinition(this.modelElement, definition);
        resourceRegistration.registerReadWriteAttribute(definition, readHandler, writeHandler);
    }

    protected void addChildResourceDefinition(ResourceDefinition definition, ManagementResourceRegistration resourceRegistration) {
        SubsystemDescriber.addChildResourceDefinition(this.modelElement, definition);
        resourceRegistration.registerSubModel(definition);
    }

}
