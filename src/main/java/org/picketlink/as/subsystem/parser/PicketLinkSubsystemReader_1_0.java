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
import static org.picketlink.as.subsystem.model.ModelElement.IDENTITY_PROVIDER_SAML_METADATA;
import static org.picketlink.as.subsystem.model.ModelElement.IDENTITY_PROVIDER_SAML_METADATA_ORGANIZATION;
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
import org.picketlink.as.subsystem.model.federation.KeyStoreResourceDefinition;
import org.picketlink.as.subsystem.model.idp.IdentityProviderResourceDefinition;
import org.picketlink.as.subsystem.model.idp.TrustDomainResourceDefinition;
import org.picketlink.as.subsystem.model.idp.metadata.ContactResourceDefinition;
import org.picketlink.as.subsystem.model.idp.metadata.IDPSAMLMetadataResourceDefinition;
import org.picketlink.as.subsystem.model.idp.metadata.OrganizationResourceDefinition;
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
        ModelNode identityProviderNode = null;
        ModelNode idpSAMLMetadataProviderNode = null;

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
                    parseIdentityProviderKeyStoreConfig(reader, list, federationNode);
                    break;
                case IDENTITY_PROVIDER:
                    identityProviderNode = parseIdentityProviderConfig(reader, list, federationNode);
                    break;
                case IDENTITY_PROVIDER_SAML_METADATA:
                    idpSAMLMetadataProviderNode = parseIDPSAMLMetadataConfig(reader, list, identityProviderNode);
                    break;
                case CONTACT:
                    parseContactConfig(reader, list, idpSAMLMetadataProviderNode);
                    break;
                case IDENTITY_PROVIDER_SAML_METADATA_ORGANIZATION:
                    parseIDPSAMLMetadataOrganizationConfig(reader, list, idpSAMLMetadataProviderNode);
                    break;
                case TRUST_DOMAIN:
                    parseTrustDomainConfig(reader, list, identityProviderNode);
                    break;
                case SERVICE_PROVIDER:
                    parseServiceProviderConfig(reader, list, federationNode);
                    break;
                default:
                    unexpectedElement(reader);
            }
        }
    }

    private void parseIdentityProviderKeyStoreConfig(XMLExtendedStreamReader reader, List<ModelNode> list,
            ModelNode idpSAMLMetadataProviderNode) throws XMLStreamException {
        parseConfig(reader, ModelElement.KEY_STORE, KeyStoreResourceDefinition.SIGN_KEY_ALIAS.getName(), list,
                idpSAMLMetadataProviderNode, getIdentityProviderKeyStoreAttributes());
    }

    private void parseContactConfig(XMLExtendedStreamReader reader, List<ModelNode> list, ModelNode idpSAMLMetadataProviderNode)
            throws XMLStreamException {
        parseConfig(reader, ModelElement.CONTACT, null, list, idpSAMLMetadataProviderNode, getContactAttributes());
    }

    private ModelNode parseIDPSAMLMetadataOrganizationConfig(XMLExtendedStreamReader reader, List<ModelNode> list,
            ModelNode federationNode) throws XMLStreamException {
        return parseConfig(reader, IDENTITY_PROVIDER_SAML_METADATA_ORGANIZATION, null, list, federationNode,
                getIDPSAMLMetadataOrganizationAttributes());
    }

    private ModelNode parseIDPSAMLMetadataConfig(XMLExtendedStreamReader reader, List<ModelNode> list, ModelNode federationNode)
            throws XMLStreamException {
        return parseConfig(reader, IDENTITY_PROVIDER_SAML_METADATA, null, list, federationNode, getIDPSAMLMetadataAttributes());
    }

    /**
     * @param reader
     * @param list
     * @param federationNode
     * @throws XMLStreamException
     */
    private void parseServiceProviderConfig(XMLExtendedStreamReader reader, List<ModelNode> list, ModelNode federationNode)
            throws XMLStreamException {
        parseConfig(reader, SERVICE_PROVIDER, ServiceProviderResourceDefinition.ALIAS.getName(), list, federationNode,
                getServiceProviderAttributes());
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
                getTrustDomainAttributes());
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
                getIdentityProviderAttributes());
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
                getFederationAttributes());
    }

    /**
     * @return
     */
    private SimpleAttributeDefinition[] getServiceProviderAttributes() {
        return asArray(ServiceProviderResourceDefinition.ALIAS, ServiceProviderResourceDefinition.SECURITY_DOMAIN,
                ServiceProviderResourceDefinition.URL, ServiceProviderResourceDefinition.POST_BINDING);
    }

    /**
     * @return
     */
    private SimpleAttributeDefinition[] getTrustDomainAttributes() {
        return asArray(TrustDomainResourceDefinition.NAME);
    }

    /**
     * @return
     */
    private SimpleAttributeDefinition[] getIdentityProviderAttributes() {
        return asArray(IdentityProviderResourceDefinition.ALIAS, IdentityProviderResourceDefinition.URL,
                IdentityProviderResourceDefinition.SECURITY_DOMAIN,
                IdentityProviderResourceDefinition.EXTERNAL,
                IdentityProviderResourceDefinition.IGNORE_INCOMING_SIGNATURES,
                IdentityProviderResourceDefinition.SIGN_OUTGOING_MESSAGES);
    }

    private SimpleAttributeDefinition[] getIDPSAMLMetadataAttributes() {
        return asArray(IDPSAMLMetadataResourceDefinition.WANT_AUTHN_REQUESTS_SIGNED);
    }

    private SimpleAttributeDefinition[] getIDPSAMLMetadataOrganizationAttributes() {
        return asArray(OrganizationResourceDefinition.NAME, OrganizationResourceDefinition.URL);
    }

    private SimpleAttributeDefinition[] getContactAttributes() {
        return asArray(ContactResourceDefinition.NAME, ContactResourceDefinition.SUR_NAME, ContactResourceDefinition.COMPANY,
                ContactResourceDefinition.EMAIL, ContactResourceDefinition.PHONE, ContactResourceDefinition.TYPE);
    }

    private SimpleAttributeDefinition[] getIdentityProviderKeyStoreAttributes() {
        return asArray(KeyStoreResourceDefinition.URL, KeyStoreResourceDefinition.PASSWD,
                KeyStoreResourceDefinition.SIGN_KEY_ALIAS, KeyStoreResourceDefinition.SIGN_KEY_PASSWD);
    }

    /**
     * @return
     */
    private SimpleAttributeDefinition[] getFederationAttributes() {
        return asArray(FederationResourceDefinition.ALIAS);
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
            ModelNode lastNode, SimpleAttributeDefinition[] attributes) throws XMLStreamException {
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
                    lastNode.clone().get(OP_ADDR).add(xmlElement.getName(), modelNode.get("*")));
        }

        list.add(modelNode);

        return modelNode;
    }

    private SimpleAttributeDefinition[] asArray(SimpleAttributeDefinition... attributes) {
        return attributes;
    }
}
