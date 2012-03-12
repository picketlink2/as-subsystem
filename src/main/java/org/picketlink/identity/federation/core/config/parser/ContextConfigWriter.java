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
import java.util.HashMap;
import java.util.Map;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import org.picketlink.identity.federation.core.config.ProviderType;
import org.picketlink.identity.federation.core.exceptions.ProcessingException;
import org.picketlink.identity.federation.core.util.StaxUtil;

/**
 * @author pedroigor
 * @sice Mar 12, 2012
 */
public class ContextConfigWriter implements ConfigWriter {

    private static final String CLASS_NAME_ATTRIBUTE = "className";
    private static final String VALVE_ELEMENT = "Valve";
    private static final String CONTEXT_ELEMENT = "Context";

    private static final String IGNORE_INCOMING_SIGNATURES_ATTRIBUTE = "ignoreIncomingSignatures";
    private static final String SIGN_OUTGOING_MESSAGES_ATTRIBUTE = "signOutgoingMessages";

    private ProviderType configuration;

    public ContextConfigWriter(ProviderType idpTypeSubsystem) {
        this.configuration = idpTypeSubsystem;
    }
    
    public void write(OutputStream stream) {
        XMLStreamWriter writer = null;
        
        try {
            writer = StaxUtil.getXMLStreamWriter(stream);
            
            StaxUtil.writeStartElement(writer, "", CONTEXT_ELEMENT, "");
            
            if (this.configuration instanceof IDPTypeSubsystem) {
                IDPTypeSubsystem idpConfiguration = (IDPTypeSubsystem) this.configuration;

                writeValve(writer, "org.picketlink.identity.federation.bindings.tomcat.idp.IDPWebBrowserSSOValve", null);
                
                Map<String,String> attributes = new HashMap<String, String>();
                
                attributes.put(SIGN_OUTGOING_MESSAGES_ATTRIBUTE, String.valueOf(idpConfiguration.isSignOutgoingMessages()));
                attributes.put(IGNORE_INCOMING_SIGNATURES_ATTRIBUTE, String.valueOf(idpConfiguration.isIgnoreIncomingSignatures()));
                
                writeValve(writer, "org.picketlink.identity.federation.bindings.tomcat.idp.IDPWebBrowserSSOValve", attributes);
            } else if (this.configuration instanceof SPTypeSubsystem) {
                SPTypeSubsystem spConfiguration = (SPTypeSubsystem) this.configuration;
                
                writeValve(writer, "org.picketlink.identity.federation.bindings.tomcat.sp.SPRedirectFormAuthenticator", null);
            }
            
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
    
    public void writeValve(XMLStreamWriter writer, String className, Map<String,String> attributes) throws ProcessingException {
        StaxUtil.writeStartElement(writer, "", VALVE_ELEMENT, "");
        StaxUtil.writeAttribute(writer, CLASS_NAME_ATTRIBUTE, className);
        
        if (attributes != null) {
            for (String key : attributes.keySet()) {
                StaxUtil.writeAttribute(writer, key, attributes.get(key));
            }
        }
        
        StaxUtil.writeEndElement(writer);
    }
    
}
