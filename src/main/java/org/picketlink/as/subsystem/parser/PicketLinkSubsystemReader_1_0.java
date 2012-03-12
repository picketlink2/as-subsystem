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
import static org.jboss.as.controller.descriptions.ModelDescriptionConstants.SUBSYSTEM;

import java.util.Collections;
import java.util.List;

import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;

import org.jboss.as.controller.PathAddress;
import org.jboss.as.controller.PathElement;
import org.jboss.as.controller.descriptions.ModelDescriptionConstants;
import org.jboss.as.controller.operations.common.Util;
import org.jboss.as.controller.parsing.ParseUtils;
import org.jboss.dmr.ModelNode;
import org.jboss.staxmapper.XMLElementReader;
import org.jboss.staxmapper.XMLExtendedStreamReader;
import org.picketlink.as.subsystem.PicketLinkExtension;
import org.picketlink.as.subsystem.model.ModelDefinition;
import org.picketlink.as.subsystem.model.ModelUtil;

/**
 * @author pedroigor
 * 
 */
public class PicketLinkSubsystemReader_1_0 implements XMLStreamConstants, XMLElementReader<List<ModelNode>> {

    /* (non-Javadoc)
     * @see org.jboss.staxmapper.XMLElementReader#readElement(org.jboss.staxmapper.XMLExtendedStreamReader, java.lang.Object)
     */
    @Override
    public void readElement(XMLExtendedStreamReader reader, List<ModelNode> list) throws XMLStreamException {
        ParseUtils.requireNoAttributes(reader);

        ModelNode lastNode = ModelUtil.createAddOperation();

        list.add(lastNode);

        ModelNode federationNode = null;
        ModelNode identityNode = null;

        while (reader.hasNext() && reader.nextTag() != END_DOCUMENT) {
            if (reader.isStartElement()) {
                lastNode = readFederationType(reader, list, lastNode);

                if (lastNode != null) {
                    federationNode = lastNode;
                }

                lastNode = readIdentityProviderType(reader, list, federationNode);

                if (lastNode != null) {
                    identityNode = lastNode;
                }

                readServiceProviderType(reader, list, federationNode);

                readTrustType(reader, list, identityNode);

            }
        }
    }

    private ModelNode readFederationType(XMLExtendedStreamReader reader, List<ModelNode> list, ModelNode lastNode)
            throws XMLStreamException {
        if (!reader.getLocalName().equals(ModelDefinition.FEDERATION.getKey())) {
            return null;
        }

        ModelNode federation = Util.getEmptyOperation(ModelDescriptionConstants.ADD, null);

        String name = null;

        for (int i = 0; i < reader.getAttributeCount(); i++) {
            String attr = reader.getAttributeLocalName(i);

            if (attr.equals(ModelDefinition.FEDERATION_ALIAS.getKey())) {
                name = reader.getAttributeValue(i);
                ModelDefinition.FEDERATION_ALIAS.getDefinition().parseAndSetParameter(name, federation, reader);
            } else {
                throw ParseUtils.unexpectedAttribute(reader, i);
            }
        }

        if (name == null) {
            name = ModelDefinition.FEDERATION_ALIAS.getDefinition().getDefaultValue().asString();
        }

        PathAddress addr = PathAddress.pathAddress(PathElement.pathElement(SUBSYSTEM, PicketLinkExtension.SUBSYSTEM_NAME),
                PathElement.pathElement(ModelDefinition.FEDERATION.getKey(), name));

        federation.get(ModelDescriptionConstants.OP_ADDR).set(addr.toModelNode());

        list.add(federation);

        return federation;
    }

    private ModelNode readIdentityProviderType(XMLExtendedStreamReader reader, List<ModelNode> list, ModelNode lastNode)
            throws XMLStreamException {
        if (!reader.getLocalName().equals(ModelDefinition.IDENTITY_PROVIDER.getKey())) {
            return null;
        }

        ModelNode identityProvider = Util.getEmptyOperation(ModelDescriptionConstants.ADD, null);

        String alias = null;
        String url = null;

        for (int i = 0; i < reader.getAttributeCount(); i++) {
            String attr = reader.getAttributeLocalName(i);

            if (attr.equals(ModelDefinition.IDENTITY_PROVIDER_ALIAS.getKey())) {
                alias = reader.getAttributeValue(i);
                ModelDefinition.IDENTITY_PROVIDER_ALIAS.getDefinition().parseAndSetParameter(alias, identityProvider, reader);
            } else if (attr.equals(ModelDefinition.COMMON_URL.getKey())) {
                url = reader.getAttributeValue(i);
                ModelDefinition.COMMON_URL.getDefinition().parseAndSetParameter(url, identityProvider, reader);
            } else if (attr.equals(ModelDefinition.IDENTITY_PROVIDER_SIGN_OUTGOING_MESSAGES.getKey())) {
                url = reader.getAttributeValue(i);
                ModelDefinition.IDENTITY_PROVIDER_SIGN_OUTGOING_MESSAGES.getDefinition().parseAndSetParameter(url,
                        identityProvider, reader);
            } else if (attr.equals(ModelDefinition.IDENTITY_PROVIDER_IGNORE_INCOMING_SIGNATURES.getKey())) {
                url = reader.getAttributeValue(i);
                ModelDefinition.IDENTITY_PROVIDER_IGNORE_INCOMING_SIGNATURES.getDefinition().parseAndSetParameter(url,
                        identityProvider, reader);
            } else {
                throw ParseUtils.unexpectedAttribute(reader, i);
            }
        }

        if (url == null) {
            throw ParseUtils.missingRequiredElement(reader, Collections.singleton("url"));
        }

        if (alias == null) {
            alias = ModelDefinition.IDENTITY_PROVIDER_ALIAS.getDefinition().getDefaultValue().asString();
        }

        identityProvider.get(ModelDescriptionConstants.OP_ADDR).set(
                lastNode.clone().get(OP_ADDR).add(ModelDefinition.IDENTITY_PROVIDER.getKey(), alias));

        list.add(identityProvider);

        return identityProvider;
    }

    private void readServiceProviderType(XMLExtendedStreamReader reader, List<ModelNode> list, ModelNode lastNode)
            throws XMLStreamException {
        if (!reader.getLocalName().equals(ModelDefinition.SERVICE_PROVIDER.getKey())) {
            return;
        }

        ModelNode serviceProvider = Util.getEmptyOperation(ModelDescriptionConstants.ADD, null);

        String alias = null;
        String url = null;

        for (int i = 0; i < reader.getAttributeCount(); i++) {
            String attr = reader.getAttributeLocalName(i);

            if (attr.equals(ModelDefinition.SERVICE_PROVIDER_ALIAS.getKey())) {
                alias = reader.getAttributeValue(i);
                ModelDefinition.SERVICE_PROVIDER_ALIAS.getDefinition().parseAndSetParameter(alias, serviceProvider, reader);
            } else if (attr.equals(ModelDefinition.COMMON_URL.getKey())) {
                url = reader.getAttributeValue(i);
                ModelDefinition.COMMON_URL.getDefinition().parseAndSetParameter(url, serviceProvider, reader);
            } else {
                throw ParseUtils.unexpectedAttribute(reader, i);
            }
        }

        if (url == null) {
            throw ParseUtils.missingRequiredElement(reader, Collections.singleton("url"));
        }

        if (alias == null) {
            alias = ModelDefinition.SERVICE_PROVIDER_ALIAS.getDefinition().getDefaultValue().asString();
        }

        serviceProvider.get(ModelDescriptionConstants.OP_ADDR).set(
                lastNode.clone().get(OP_ADDR).add(ModelDefinition.SERVICE_PROVIDER.getKey(), alias));

        list.add(serviceProvider);
    }

    private void readTrustType(XMLExtendedStreamReader reader, List<ModelNode> list, ModelNode lastNode)
            throws XMLStreamException {
        if (!reader.getLocalName().equals(ModelDefinition.TRUST_DOMAIN.getKey())) {
            return;
        }

        readDomainType(reader, list, lastNode);
    }

    private void readDomainType(XMLExtendedStreamReader reader, List<ModelNode> list, ModelNode lastNode)
            throws XMLStreamException {
        if (!reader.getLocalName().equals(ModelDefinition.TRUST_DOMAIN.getKey())) {
            return;
        }

        ModelNode domain = Util.getEmptyOperation(ModelDescriptionConstants.ADD, null);

        String name = null;

        for (int i = 0; i < reader.getAttributeCount(); i++) {
            String attr = reader.getAttributeLocalName(i);

            if (attr.equals(ModelDefinition.TRUST_DOMAIN_NAME.getKey())) {
                name = reader.getAttributeValue(i);
                ModelDefinition.TRUST_DOMAIN_NAME.getDefinition().parseAndSetParameter(name, domain, reader);
            } else {
                throw ParseUtils.unexpectedAttribute(reader, i);
            }
        }

        domain.get(ModelDescriptionConstants.OP_ADDR).set(
                lastNode.clone().get(OP_ADDR).add(ModelDefinition.TRUST_DOMAIN.getKey(), name));

        list.add(domain);
    }

}
