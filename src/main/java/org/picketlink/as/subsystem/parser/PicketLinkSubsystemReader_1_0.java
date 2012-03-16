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

import static org.jboss.as.controller.descriptions.ModelDescriptionConstants.OP_ADDR;

import java.util.List;

import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;

import org.jboss.as.controller.SimpleAttributeDefinition;
import org.jboss.as.controller.descriptions.ModelDescriptionConstants;
import org.jboss.as.controller.operations.common.Util;
import org.jboss.as.controller.parsing.ParseUtils;
import org.jboss.dmr.ModelNode;
import org.jboss.staxmapper.XMLElementReader;
import org.jboss.staxmapper.XMLExtendedStreamReader;
import org.picketlink.as.subsystem.PicketLinkExtension;
import org.picketlink.as.subsystem.model.ModelKeys;
import org.picketlink.as.subsystem.model.federation.FederationResourceDefinition;
import org.picketlink.as.subsystem.model.idp.IdentityProviderResourceDefinition;
import org.picketlink.as.subsystem.model.idp.TrustDomainResourceDefinition;
import org.picketlink.as.subsystem.model.sp.ServiceProviderResourceDefinition;

/**
 * <p>
 * XML Reader for the subsystem schema, version 1.0.
 * </p>
 * 
 * @author <a href="mailto:psilva@redhat.com">Pedro Silva</a>
 */
public class PicketLinkSubsystemReader_1_0 implements XMLStreamConstants, XMLElementReader<List<ModelNode>> {

    /*
     * (non-Javadoc)
     * 
     * @see org.jboss.staxmapper.XMLElementReader#readElement(org.jboss.staxmapper.XMLExtendedStreamReader, java.lang.Object)
     */
    @Override
    public void readElement(XMLExtendedStreamReader reader, List<ModelNode> list) throws XMLStreamException {
        ParseUtils.requireNoAttributes(reader);

        ModelNode lastNode = createSubsystemRoot();

        list.add(lastNode);

        ModelNode federationNode = null;
        ModelNode identityProviderNode = null;

        while (reader.hasNext() && reader.nextTag() != END_DOCUMENT) {
            if (reader.isStartElement()) {
                lastNode = readElement(reader, ModelKeys.FEDERATION, FederationResourceDefinition.ALIAS.getName(), list,
                        lastNode, FederationResourceDefinition.ALIAS);

                if (lastNode != null) {
                    federationNode = lastNode;
                }

                lastNode = readElement(reader, ModelKeys.IDENTITY_PROVIDER,
                        IdentityProviderResourceDefinition.IDENTITY_PROVIDER_ALIAS.getName(), list, federationNode,
                        IdentityProviderResourceDefinition.IDENTITY_PROVIDER_ALIAS,
                        IdentityProviderResourceDefinition.COMMON_URL,
                        IdentityProviderResourceDefinition.IDENTITY_PROVIDER_IGNORE_INCOMING_SIGNATURES,
                        IdentityProviderResourceDefinition.IDENTITY_PROVIDER_SIGN_OUTGOING_MESSAGES);

                if (lastNode != null) {
                    identityProviderNode = lastNode;
                }

                readElement(reader, ModelKeys.SERVICE_PROVIDER, ServiceProviderResourceDefinition.ALIAS.getName(), list,
                        federationNode, ServiceProviderResourceDefinition.ALIAS, ServiceProviderResourceDefinition.URL);

                readElement(reader, ModelKeys.TRUST_DOMAIN, TrustDomainResourceDefinition.TRUST_DOMAIN_NAME.getName(), list,
                        identityProviderNode, TrustDomainResourceDefinition.TRUST_DOMAIN_NAME);
            }
        }
    }

    /**
     * Creates the root subsystem's root address.
     * 
     * @return
     */
    private ModelNode createSubsystemRoot() {
        ModelNode subsystemAddress = new ModelNode();

        subsystemAddress.add(ModelDescriptionConstants.SUBSYSTEM, PicketLinkExtension.SUBSYSTEM_NAME);

        subsystemAddress.protect();

        return Util.getEmptyOperation(ModelDescriptionConstants.ADD, subsystemAddress);
    }

    /**
     * Reads a element from the stream considering the parameters.
     * 
     * @param reader XMLExtendedStreamReader instance from which the elements are read.
     * @param xmlElement Name of the XML Element to be read.
     * @param key Name of the attribute to be used to as the key for the model.
     * @param list List of operations.
     * @param lastNode Parent ModelNode instance.
     * @param attributes AttributeDefinition instances to be used to extract the attributes and populate the resulting model. 
     * 
     * @return A ModelNode instance populated.
     * 
     * @throws XMLStreamException
     */
    private ModelNode readElement(XMLExtendedStreamReader reader, String xmlElement, String key, List<ModelNode> list,
            ModelNode lastNode, SimpleAttributeDefinition... attributes) throws XMLStreamException {
        if (!reader.getLocalName().equals(xmlElement)) {
            return null;
        }

        ModelNode modelNode = Util.getEmptyOperation(ModelDescriptionConstants.ADD, null);

        for (SimpleAttributeDefinition simpleAttributeDefinition : attributes) {
            simpleAttributeDefinition.parseAndSetParameter(
                    reader.getAttributeValue("", simpleAttributeDefinition.getXmlName()), modelNode, reader);
        }

        modelNode.get(ModelDescriptionConstants.OP_ADDR).set(lastNode.clone().get(OP_ADDR).add(xmlElement, modelNode.get(key)));

        list.add(modelNode);

        return modelNode;
    }

}
