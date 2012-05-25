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

import java.net.MalformedURLException;
import java.net.URL;

import javax.xml.stream.XMLStreamWriter;

import org.picketlink.identity.federation.core.config.SPConfiguration;
import org.picketlink.identity.federation.core.exceptions.ProcessingException;
import org.picketlink.identity.federation.core.parsers.config.SAMLConfigParser;
import org.picketlink.identity.federation.core.util.StaxUtil;

/**
 * <p>
 * Class responsible for the creation of the picketlink-idfed.xml configuration file for Service Providers.
 * </p>
 * 
 * @author <a href="mailto:psilva@redhat.com">Pedro Silva</a>
 * @since Mar 12, 2012
 */
public class SPTypeConfigWriter extends AbstractProviderTypeConfigWriter<SPConfiguration> {


    public SPTypeConfigWriter(SPConfiguration spTypeSubsystem) {
        super(spTypeSubsystem);
    }
    
    /* (non-Javadoc)
     * @see org.picketlink.identity.federation.core.config.parser.AbstractProviderTypeConfigWriter#doWriteProviderElementAttributes(javax.xml.stream.XMLStreamWriter)
     */
    @Override
    protected void doWriteProviderElementAttributes(XMLStreamWriter writer) throws ProcessingException {
        String bindingType = "REDIRECT";
        
        if (this.getConfiguration().isPostBinding()) {
            bindingType = "POST";
        }
        
        StaxUtil.writeAttribute(writer, "BindingType", bindingType);
    }
    
    /* (non-Javadoc)
     * @see org.picketlink.identity.federation.core.config.parser.AbstractProviderTypeConfigWriter#doWrite(javax.xml.stream.XMLStreamWriter)
     */
    @Override
    protected void doWrite(XMLStreamWriter writer) throws ProcessingException {
        writeServiceURLConfig(writer);
    }
    
    @Override
    protected String getProviderElementName() {
        return SAMLConfigParser.SP;
    }
    
    /* (non-Javadoc)
     * @see org.picketlink.identity.federation.core.config.parser.AbstractProviderTypeConfigWriter#writeValidatingAliasConfig(javax.xml.stream.XMLStreamWriter)
     */
    @Override
    protected void writeValidatingAliasConfig(XMLStreamWriter writer) throws ProcessingException {
        String idpHost = getIDPHostAddress();
        
        StaxUtil.writeStartElement(writer, "", SAMLConfigParser.VALIDATING_ALIAS, "");
        StaxUtil.writeAttribute(writer, SAMLConfigParser.KEY, idpHost);
        StaxUtil.writeAttribute(writer, SAMLConfigParser.VALUE, idpHost);
        StaxUtil.writeEndElement(writer);
    }
    
    /**
     * <p>
     * Returns the IDP host address.
     * </p>
     * 
     * @return
     */
    private String getIDPHostAddress() {
        String idpHost = null;
        
        try {
            idpHost = new URL(getConfiguration().getIdentityURL()).getHost();
        } catch (MalformedURLException e) {
            throw new IllegalStateException("The Identity URL for the Service Provider " + getConfiguration().getServiceURL() + " is invalid.", e);
        }
        return idpHost;
    }

    /**
     * <p>
     * Writes the <ServiceURL/> element.
     * </p>
     * 
     * @param writer
     * @throws ProcessingException
     */
    private void writeServiceURLConfig(XMLStreamWriter writer) throws ProcessingException {
        StaxUtil.writeStartElement(writer, "", SAMLConfigParser.SERVICE_URL, "");
        StaxUtil.writeCharacters(writer, getConfiguration().getServiceURL());
        StaxUtil.writeEndElement(writer);
    }
    
}
