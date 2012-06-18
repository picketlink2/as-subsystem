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
import static org.jboss.as.controller.parsing.ParseUtils.requireNoAttributes;
import static org.jboss.as.controller.parsing.ParseUtils.unexpectedElement;
import static org.picketlink.as.subsystem.model.ModelElement.FEDERATION;
import static org.picketlink.as.subsystem.model.ModelElement.IDENTITY_PROVIDER;
import static org.picketlink.as.subsystem.model.ModelElement.SERVICE_PROVIDER;
import static org.picketlink.as.subsystem.model.ModelElement.TRUST_DOMAIN;

import java.util.List;

import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;

import org.jboss.as.controller.SimpleAttributeDefinition;
import org.jboss.as.controller.descriptions.ModelDescriptionConstants;
import org.jboss.as.controller.operations.common.Util;
import org.jboss.dmr.ModelNode;
import org.jboss.staxmapper.XMLElementReader;
import org.jboss.staxmapper.XMLExtendedStreamReader;
import org.picketlink.as.subsystem.Namespace;
import org.picketlink.as.subsystem.PicketLinkExtension;
import org.picketlink.as.subsystem.model.ModelElement;
import org.picketlink.as.subsystem.model.XMLElement;
import org.picketlink.as.subsystem.model.federation.FederationResourceDefinition;
import org.picketlink.as.subsystem.model.federation.KeyProviderResourceDefinition;
import org.picketlink.as.subsystem.model.handlers.HandlerParameterResourceDefinition;
import org.picketlink.as.subsystem.model.handlers.HandlerResourceDefinition;
import org.picketlink.as.subsystem.model.idp.IdentityProviderResourceDefinition;
import org.picketlink.as.subsystem.model.idp.TrustDomainResourceDefinition;
import org.picketlink.as.subsystem.model.saml.SAMLResourceDefinition;
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
        requireNoAttributes(reader);

        Namespace nameSpace = Namespace.forUri(reader.getNamespaceURI());

        ModelNode subsystemNode = createSubsystemRoot();

        list.add(subsystemNode);

        switch (nameSpace) {
            case PICKETLINK_1_0:
                this.readElement_1_0(reader, list, subsystemNode);
                break;
            default:
                throw unexpectedElement(reader);
        }

    }

    /**
     * Parses the PicketLink subsystem configuration according to the XSD version 1.0.
     * 
     * @param reader
     * @param list
     * @throws XMLStreamException
     */
    private void readElement_1_0(XMLExtendedStreamReader reader, List<ModelNode> list, ModelNode parentNode)
            throws XMLStreamException {
        if (Namespace.PICKETLINK_1_0 != Namespace.forUri(reader.getNamespaceURI())) {
            throw unexpectedElement(reader);
        }

        ModelNode federationNode = null;
        ModelNode lastProviderNode = null;
        ModelNode lastHandlerNode = null;

        while (reader.hasNext() && reader.nextTag() != END_DOCUMENT) {
            if (!reader.isStartElement()) {
                continue;
            }

            // if the current element is supported but is not a model element
            if (XMLElement.forName(reader.getLocalName()) != null) {
                continue;
            }

            ModelElement modelKey = ModelElement.forName(reader.getLocalName());

            if (modelKey == null) {
                throw unexpectedElement(reader);
            }

            switch (modelKey) {
                case FEDERATION:
                    federationNode = parseFederationConfig(reader, list, parentNode);
                    break;
                case KEY_STORE:
                    parseKeyStoreConfig(reader, list, federationNode);
                    break;
                case IDENTITY_PROVIDER:
                    lastProviderNode = parseIdentityProviderConfig(reader, list, federationNode);
                    break;
                case TRUST_DOMAIN:
                    parseTrustDomainConfig(reader, list, lastProviderNode);
                    break;
                case HANDLER:
                    lastHandlerNode = parseHandlerConfig(reader, list, lastProviderNode);
                    break;
                case HANDLER_PARAMETER:
                    parseHandlerParameterConfig(reader, list, lastHandlerNode);
                    break;
                case SERVICE_PROVIDER:
                    lastProviderNode = parseServiceProviderConfig(reader, list, federationNode);
                    break;
                case SAML:
                    parseSAMLConfig(reader, list, federationNode);
                    break;
                default:
                    unexpectedElement(reader);
            }
        }
    }

    private void parseKeyStoreConfig(XMLExtendedStreamReader reader, List<ModelNode> list, ModelNode federationNode)
            throws XMLStreamException {
        parseConfig(reader, ModelElement.KEY_STORE, KeyProviderResourceDefinition.SIGN_KEY_ALIAS.getName(), list,
                federationNode, KeyProviderResourceDefinition.INSTANCE.getAttributes());
    }

    /**
     * @param reader
     * @param list
     * @param federationNode
     * @return 
     * @throws XMLStreamException
     */
    private ModelNode parseServiceProviderConfig(XMLExtendedStreamReader reader, List<ModelNode> list, ModelNode federationNode)
            throws XMLStreamException {
        return parseConfig(reader, SERVICE_PROVIDER, ServiceProviderResourceDefinition.ALIAS.getName(), list, federationNode,
                ServiceProviderResourceDefinition.INSTANCE.getAttributes());
    }

    private void parseSAMLConfig(XMLExtendedStreamReader reader, List<ModelNode> list, ModelNode federationNode)
            throws XMLStreamException {
        parseConfig(reader, ModelElement.SAML, null, list, federationNode, SAMLResourceDefinition.INSTANCE.getAttributes());
    }

    /**
     * @param reader
     * @param list
     * @param identityProviderNode
     * @throws XMLStreamException
     */
    private void parseTrustDomainConfig(XMLExtendedStreamReader reader, List<ModelNode> list, ModelNode identityProviderNode)
            throws XMLStreamException {
        parseConfig(reader, TRUST_DOMAIN, TrustDomainResourceDefinition.NAME.getName(), list, identityProviderNode,
                TrustDomainResourceDefinition.INSTANCE.getAttributes());
    }

    private ModelNode parseHandlerConfig(XMLExtendedStreamReader reader, List<ModelNode> list, ModelNode identityProviderNode)
            throws XMLStreamException {
        return parseConfig(reader, ModelElement.HANDLER, HandlerResourceDefinition.CLASS.getName(), list, identityProviderNode,
                HandlerResourceDefinition.INSTANCE.getAttributes());
    }

    private ModelNode parseHandlerParameterConfig(XMLExtendedStreamReader reader, List<ModelNode> list, ModelNode identityProviderNode)
            throws XMLStreamException {
        return parseConfig(reader, ModelElement.HANDLER_PARAMETER, HandlerParameterResourceDefinition.NAME.getName(), list, identityProviderNode,
                HandlerParameterResourceDefinition.INSTANCE.getAttributes());
    }

    /**
     * @param reader
     * @param list
     * @param federationNode
     * @return
     * @throws XMLStreamException
     */
    private ModelNode parseIdentityProviderConfig(XMLExtendedStreamReader reader, List<ModelNode> list, ModelNode federationNode)
            throws XMLStreamException {
        return parseConfig(reader, IDENTITY_PROVIDER, IdentityProviderResourceDefinition.ALIAS.getName(), list, federationNode,
                IdentityProviderResourceDefinition.INSTANCE.getAttributes());
    }

    /**
     * @param reader
     * @param list
     * @param parentNode
     * @return
     * @throws XMLStreamException
     */
    private ModelNode parseFederationConfig(XMLExtendedStreamReader reader, List<ModelNode> list, ModelNode parentNode)
            throws XMLStreamException {
        return parseConfig(reader, FEDERATION, FederationResourceDefinition.ALIAS.getName(), list, parentNode,
                FederationResourceDefinition.INSTANCE.getAttributes());
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
     * @param xmlElement Name of the Model Element to be parsed.
     * @param key Name of the attribute to be used to as the key for the model.
     * @param list List of operations.
     * @param lastNode Parent ModelNode instance.
     * @param attributes AttributeDefinition instances to be used to extract the attributes and populate the resulting model.
     * 
     * @return A ModelNode instance populated.
     * 
     * @throws XMLStreamException
     */
    private ModelNode parseConfig(XMLExtendedStreamReader reader, ModelElement xmlElement, String key, List<ModelNode> list,
            ModelNode lastNode, List<SimpleAttributeDefinition> attributes) throws XMLStreamException {
        if (!reader.getLocalName().equals(xmlElement.getName())) {
            return null;
        }

        ModelNode modelNode = Util.getEmptyOperation(ModelDescriptionConstants.ADD, null);

        for (SimpleAttributeDefinition simpleAttributeDefinition : attributes) {
            simpleAttributeDefinition.parseAndSetParameter(
                    reader.getAttributeValue("", simpleAttributeDefinition.getXmlName()), modelNode, reader);
        }

        if (key != null) {
            modelNode.get(ModelDescriptionConstants.OP_ADDR).set(
                    lastNode.clone().get(OP_ADDR).add(xmlElement.getName(), modelNode.get(key)));
        } else {
            modelNode.get(ModelDescriptionConstants.OP_ADDR).set(
                    lastNode.clone().get(OP_ADDR).add(xmlElement.getName(), xmlElement.getName()));
        }

        list.add(modelNode);

        return modelNode;
    }
}