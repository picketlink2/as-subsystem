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

import java.io.OutputStream;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import org.picketlink.identity.federation.core.exceptions.ProcessingException;
import org.picketlink.identity.federation.core.util.StaxUtil;

/**
 * @author pedroigor
 * @sice Mar 12, 2012
 */
public class HandlersConfigWriter implements ConfigWriter  {

    private static final String CLASS_NAME_ATTRIBUTE = "class";
    private static final String HANDLER_ELEMENT = "Handler";
    private static final String HANDLERS_ELEMENT = "Handlers";

    private IDPTypeSubsystem idpConfiguration;

    public HandlersConfigWriter(IDPTypeSubsystem idpTypeSubsystem) {
        this.idpConfiguration = idpTypeSubsystem;
    }
    
    public void write(OutputStream stream) {
        XMLStreamWriter writer = null;
        
        try {
            writer = StaxUtil.getXMLStreamWriter(stream);
            
            StaxUtil.writeStartElement(writer, "", HANDLERS_ELEMENT, "urn:picketlink:identity-federation:handler:config:1.0");
            
            StaxUtil.writeStartElement(writer, "", HANDLER_ELEMENT, "");
            StaxUtil.writeAttribute(writer, CLASS_NAME_ATTRIBUTE, "org.picketlink.identity.federation.web.handlers.saml2.SAML2IssuerTrustHandler");
            StaxUtil.writeEndElement(writer);
            
            StaxUtil.writeStartElement(writer, "", HANDLER_ELEMENT, "");
            StaxUtil.writeAttribute(writer, CLASS_NAME_ATTRIBUTE, "org.picketlink.identity.federation.web.handlers.saml2.SAML2LogOutHandler");
            StaxUtil.writeEndElement(writer);

            StaxUtil.writeStartElement(writer, "", HANDLER_ELEMENT, "");
            StaxUtil.writeAttribute(writer, CLASS_NAME_ATTRIBUTE, "org.picketlink.identity.federation.web.handlers.saml2.SAML2AuthenticationHandler");
            StaxUtil.writeEndElement(writer);

            StaxUtil.writeStartElement(writer, "", HANDLER_ELEMENT, "");
            StaxUtil.writeAttribute(writer, CLASS_NAME_ATTRIBUTE, "org.picketlink.identity.federation.web.handlers.saml2.RolesGenerationHandler");
            StaxUtil.writeEndElement(writer);

            StaxUtil.writeEndElement(writer);
        } catch (ProcessingException e) {
            e.printStackTrace();
        } finally {
            if (writer != null) {
                try {
                    writer.flush();
                    writer.close();
                } catch (XMLStreamException e) {
                    e.printStackTrace();
                }
            }
        }        
    }
    
}
