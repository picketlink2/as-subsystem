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

package org.picketlink.as.subsystem.parser;

import java.util.List;
import java.util.Map;

import javax.xml.stream.XMLStreamException;

import org.jboss.as.controller.ResourceDefinition;
import org.jboss.dmr.ModelNode;
import org.jboss.staxmapper.XMLExtendedStreamWriter;
import org.picketlink.as.subsystem.model.ModelElement;
import org.picketlink.as.subsystem.model.XMLElement;

/**
 * <p>
 * A generic XML Writer for all {@link ModelElement} definitions.
 * </p>
 * 
 * @author <a href="mailto:psilva@redhat.com">Pedro Silva</a>
 * @since Mar 9, 2012
 */
public class GenericModelElementWriter extends AbstractModelWriter {

    /**
     * @param register
     */
    public GenericModelElementWriter(ModelElement modelElement, Map<String, ModelWriter> register) {
        super(modelElement, register);
    }

    /**
     * @param trustDomain
     * @param trust
     * @param writers
     */
    public GenericModelElementWriter(ModelElement trustDomain, XMLElement trust, Map<String, ModelWriter> writers) {
        super(trustDomain, trust, writers);
    }

    /* (non-Javadoc)
     * @see org.picketlink.as.subsystem.parser.ModelWriter#write(org.jboss.staxmapper.XMLExtendedStreamWriter, org.jboss.dmr.Property)
     */
    @Override
    public void write(XMLExtendedStreamWriter writer, ModelNode property) throws XMLStreamException {
        if (property.asProperty().getName().equals(this.getModelElement().getName()) || property.asProperty().getValue().hasDefined(this.getModelElement().getName())) {
            
            if (this.getParentElement() != null) {
                writer.writeStartElement(this.getParentElement().getName());                
            }

            for (ModelNode modelNode : property.asProperty().getValue().asList()) {
                writer.writeStartElement(this.getModelElement().getName());
                
                writeAttributes(writer, modelNode.asProperty().getValue());
                
                for (ModelNode propertyIdentity: modelNode.asProperty().getValue().asList()) {
                    List<ResourceDefinition> children = this.getChildResourceDefinitions();
                    
                    if (children != null) {
                        for (ResourceDefinition child : children) {
                            get(child.getPathElement().getKey()).write(writer, propertyIdentity);
                        }
                    }
                }

                writer.writeEndElement();
            }
            
            if (this.getParentElement() != null) {
                writer.writeEndElement();                
            }
        }        
    }

}
