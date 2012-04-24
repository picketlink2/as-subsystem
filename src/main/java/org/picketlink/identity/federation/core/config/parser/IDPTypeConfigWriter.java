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

package org.picketlink.identity.federation.core.config.parser;

import javax.xml.stream.XMLStreamWriter;

import org.picketlink.identity.federation.core.config.IDPConfiguration;
import org.picketlink.identity.federation.core.exceptions.ProcessingException;
import org.picketlink.identity.federation.core.parsers.config.SAMLConfigParser;
import org.picketlink.identity.federation.core.util.StaxUtil;

/**
 * <p>
 * Class responsible for the creation of the picketlink-idfed.xml configuration file for Identity Providers.
 * </p>
 * 
 * @author <a href="mailto:psilva@redhat.com">Pedro Silva</a>
 * @since Mar 12, 2012
 */
public class IDPTypeConfigWriter extends AbstractProviderTypeConfigWriter<IDPConfiguration> {

    public IDPTypeConfigWriter(IDPConfiguration idpTypeSubsystem) {
        super(idpTypeSubsystem);
    }

    /* (non-Javadoc)
     * @see org.picketlink.identity.federation.core.config.parser.AbstractProviderTypeConfigWriter#doWrite(javax.xml.stream.XMLStreamWriter)
     */
    @Override
    protected void doWrite(XMLStreamWriter writer) throws ProcessingException {
        writeTrustedDomainsConfig(writer);
    }
    
    @Override
    protected String getProviderElementName() {
        return SAMLConfigParser.IDP;
    }
    
    /* (non-Javadoc)
     * @see org.picketlink.identity.federation.core.config.parser.AbstractProviderTypeConfigWriter#writeValidatingAliasConfig(javax.xml.stream.XMLStreamWriter)
     */
    protected void writeValidatingAliasConfig(XMLStreamWriter writer) throws ProcessingException {
        if (getConfiguration().getTrust() != null && getConfiguration().getTrust().getDomains() != null) {
            String[] domains = getConfiguration().getTrust().getDomains().split(",");
            
            for (String domain : domains) {
                StaxUtil.writeStartElement(writer, "", SAMLConfigParser.VALIDATING_ALIAS, "");
                StaxUtil.writeAttribute(writer, SAMLConfigParser.KEY, domain);
                StaxUtil.writeAttribute(writer, SAMLConfigParser.VALUE, domain);
                StaxUtil.writeEndElement(writer);
            }
        }
    }

    /**
     * <p>
     * Writes the <Trust><Domains></Trust> elements.
     * </p>
     * 
     * @param writer
     * @throws ProcessingException
     */
    private void writeTrustedDomainsConfig(XMLStreamWriter writer) throws ProcessingException {
        StaxUtil.writeStartElement(writer, "", SAMLConfigParser.TRUST, "");
        StaxUtil.writeStartElement(writer, "", SAMLConfigParser.DOMAINS, "");
        StaxUtil.writeCharacters(writer, getConfiguration().getTrust().getDomains());
        StaxUtil.writeEndElement(writer);
        StaxUtil.writeEndElement(writer);
    }
   
}