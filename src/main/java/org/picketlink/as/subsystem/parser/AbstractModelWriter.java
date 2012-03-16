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

import java.util.Map;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import org.jboss.as.controller.SimpleAttributeDefinition;
import org.jboss.dmr.Property;

/**
 * @author <a href="mailto:psilva@redhat.com">Pedro Silva</a>
 * @since Mar 9, 2012
 */
public abstract class AbstractModelWriter implements ModelWriter {

    private Map<String, ModelWriter> register;
    
    public AbstractModelWriter(Map<String, ModelWriter> register) {
        this.register = register;
    }
    
    public ModelWriter get(String writerKey) {
        ModelWriter writer = this.register.get(writerKey);
        
        if (writer == null) {
            throw new IllegalStateException("No writer found in the register for key: " + writerKey);
        }
        
        return writer;
    }

    public void writeAttributes(XMLStreamWriter writer, Property property, SimpleAttributeDefinition... attributes) throws XMLStreamException {
        for (SimpleAttributeDefinition simpleAttributeDefinition : attributes) {
            if (property.getValue().hasDefined(simpleAttributeDefinition.getXmlName())) {
                simpleAttributeDefinition.marshallAsAttribute(property.getValue(), writer);
            }
        }
    }
}
