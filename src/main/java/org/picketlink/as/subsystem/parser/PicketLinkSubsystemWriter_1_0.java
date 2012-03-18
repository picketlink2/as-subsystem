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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;

import org.jboss.as.controller.persistence.SubsystemMarshallingContext;
import org.jboss.dmr.ModelNode;
import org.jboss.staxmapper.XMLElementWriter;
import org.jboss.staxmapper.XMLExtendedStreamWriter;
import org.picketlink.as.subsystem.Namespace;
import org.picketlink.as.subsystem.model.ModelElement;
import org.picketlink.as.subsystem.model.XMLElement;

/**
 * <p>
 * XML Writer for the subsystem schema, version 1.0.
 * </p>
 * 
 * @author <a href="mailto:psilva@redhat.com">Pedro Silva</a>
 */
public class PicketLinkSubsystemWriter_1_0 implements XMLStreamConstants, XMLElementWriter<SubsystemMarshallingContext> {

    private static final Map<String, ModelWriter> writers;
    
    static {
        writers = new HashMap<String, ModelWriter>();
        
        writers.put(ModelElement.FEDERATION.getName(), new GenericModelElementWriter(ModelElement.FEDERATION, writers));
        writers.put(ModelElement.IDENTITY_PROVIDER.getName(), new GenericModelElementWriter(ModelElement.IDENTITY_PROVIDER, writers));
        writers.put(ModelElement.TRUST_DOMAIN.getName(), new GenericModelElementWriter(ModelElement.TRUST_DOMAIN, XMLElement.TRUST, writers));
        writers.put(ModelElement.SERVICE_PROVIDER.getName(), new GenericModelElementWriter(ModelElement.SERVICE_PROVIDER, XMLElement.SERVICE_PROVIDERS, writers));
    }
    
    /** {@inheritDoc} */
    @Override
    public void writeContent(XMLExtendedStreamWriter writer, SubsystemMarshallingContext context) throws XMLStreamException {
        context.startSubsystemElement(Namespace.CURRENT.getUri(), false);

        List<ModelNode> federation = context.getModelNode().asList();

        for (ModelNode modelNode : federation) {
            writers.get(ModelElement.FEDERATION.getName()).write(writer, modelNode);            
        }
        
        // End subsystem
        writer.writeEndElement();
    }

}
