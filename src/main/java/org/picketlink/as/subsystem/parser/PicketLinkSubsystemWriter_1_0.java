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
import java.util.Map;

import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;

import org.jboss.as.controller.persistence.SubsystemMarshallingContext;
import org.jboss.dmr.ModelNode;
import org.jboss.staxmapper.XMLElementWriter;
import org.jboss.staxmapper.XMLExtendedStreamWriter;
import org.picketlink.as.subsystem.Namespace;
import org.picketlink.as.subsystem.model.ModelDefinition;

/**
 * @author pedroigor
 * 
 */
public class PicketLinkSubsystemWriter_1_0 implements XMLStreamConstants, XMLElementWriter<SubsystemMarshallingContext> {

    private static final Map<String, ModelWriter> writers;
    
    static {
        writers = new HashMap<String, ModelWriter>();
        
        writers.put(ModelDefinition.FEDERATION.getKey(), new FederationWriter(writers));
        writers.put(ModelDefinition.IDENTITY_PROVIDER.getKey(), new IdentityProviderWriter(writers));
        writers.put(ModelDefinition.SERVICE_PROVIDER.getKey(), new ServiceProviderWriter(writers));
        writers.put(ModelDefinition.TRUST_DOMAIN.getKey(), new TrustWriter(writers));
    }
    
    /** {@inheritDoc} */
    @Override
    public void writeContent(XMLExtendedStreamWriter writer, SubsystemMarshallingContext context) throws XMLStreamException {
        context.startSubsystemElement(Namespace.CURRENT.getUri(), false);

        ModelNode federation = context.getModelNode().get(ModelDefinition.FEDERATION.getKey());

        writers.get(ModelDefinition.FEDERATION.getKey()).write(writer, federation.asProperty());
        
        // End subsystem
        writer.writeEndElement();
    }

}
