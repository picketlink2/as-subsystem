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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jboss.as.controller.ResourceDefinition;
import org.jboss.as.controller.SimpleAttributeDefinition;

/**
 * @author <a href="mailto:psilva@redhat.com">Pedro Silva</a>
 * @since Mar 18, 2012
 */
public class SubsystemDescriber {

    private static final Map<ModelElement, List<SimpleAttributeDefinition>> attributeDefinitions;
    private static final Map<ModelElement, List<ResourceDefinition>> childResourceDefinitions;
    
    static {
        attributeDefinitions = new HashMap<ModelElement, List<SimpleAttributeDefinition>>();
        childResourceDefinitions = new HashMap<ModelElement, List<ResourceDefinition>>();
    }
    
    public static void addAttributeDefinition(ModelElement resourceDefinitionKey, SimpleAttributeDefinition attribute) {
        List<SimpleAttributeDefinition> resourceAttributes = attributeDefinitions.get(resourceDefinitionKey);
        
        if (resourceAttributes == null) {
            resourceAttributes = new ArrayList<SimpleAttributeDefinition>();
            attributeDefinitions.put(resourceDefinitionKey, resourceAttributes);
        }
        
        if (!resourceAttributes.contains(attribute)) {
            resourceAttributes.add(attribute);    
        }
    }

    public static void addChildResourceDefinition(ModelElement resourceDefinitionKey, ResourceDefinition attribute) {
        List<ResourceDefinition> childResources = childResourceDefinitions.get(resourceDefinitionKey);
        
        if (childResources == null) {
            childResources = new ArrayList<ResourceDefinition>();
            childResourceDefinitions.put(resourceDefinitionKey, childResources);
        }

        if (!childResources.contains(attribute)) {
            childResources.add(attribute);    
        }
    }

    /**
     * @param modelElement
     * @return
     */
    public static List<SimpleAttributeDefinition> getAttributeDefinition(ModelElement modelElement) {
        return attributeDefinitions.get(modelElement);
    }
    
    /**
     * @param parentModelElement
     * @return
     */
    public static List<ResourceDefinition> getChildResourceDefinitions(ModelElement parentModelElement) {
        return childResourceDefinitions.get(parentModelElement);
    }

}
