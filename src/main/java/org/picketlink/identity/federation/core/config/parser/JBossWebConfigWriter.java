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

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.picketlink.identity.federation.core.config.ProviderType;
import org.picketlink.identity.federation.core.exceptions.ProcessingException;
import org.picketlink.identity.federation.core.util.StaxUtil;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

/**
 * <p>
 * Class responsible for the creation of the context.xml configuration file.
 * </p>
 * 
 * @author <a href="mailto:psilva@redhat.com">Pedro Silva</a>
 * @since Mar 12, 2012
 */
public class JBossWebConfigWriter implements ConfigWriter {

    private static final String VALVE_PARAM_VALUE = "param-value";
    private static final String VALVE_PARAM_NAME = "param-name";
    private static final String VALVE_CLASS_NAME = "class-name";
    private static final String VALVE_ELEMENT = "valve";
    private static final String VALVE_PARAM = "param";
    
    private static final String IGNORE_INCOMING_SIGNATURES_ATTRIBUTE = "ignoreIncomingSignatures";
    private static final String SIGN_OUTGOING_MESSAGES_ATTRIBUTE = "signOutgoingMessages";

    private ProviderType configuration;

    public JBossWebConfigWriter(ProviderType idpTypeSubsystem) {
        this.configuration = idpTypeSubsystem;
    }

    /**
     * Get an {@code XMLStreamWriter}
     * 
     * @param outStream
     * @return
     * @throws ProcessingException
     */
    public static XMLStreamWriter getXMLStreamWriter(final Result outStream) throws ProcessingException {
        XMLOutputFactory xmlOutputFactory = XMLOutputFactory.newInstance();
        try {
            return xmlOutputFactory.createXMLStreamWriter(outStream);
        } catch (XMLStreamException e) {
            throw new ProcessingException(e);
        }
    }
    
    /**
     * Get an {@code XMLStreamWriter}
     * 
     * @param outStream
     * @return
     * @throws ProcessingException
     */
    public static XMLStreamWriter getXMLStreamWriter(final OutputStream outStream) throws ProcessingException {
        XMLOutputFactory xmlOutputFactory = XMLOutputFactory.newInstance();
        try {
            return xmlOutputFactory.createXMLStreamWriter(outStream);
        } catch (XMLStreamException e) {
            throw new ProcessingException(e);
        }
    }


    public void write(File file) {
        DocumentBuilder newDocumentBuilder = null;
        Document existingDocument = null;
        
        try {
            newDocumentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            existingDocument = newDocumentBuilder.parse(new FileInputStream(file));
        } catch (Exception e) {
        }

        Node node = getNodeToAppend(file);
        
        Node lastNode = existingDocument.getFirstChild().getChildNodes().item(existingDocument.getFirstChild().getChildNodes().getLength() - 1);
        
        Node importNode = existingDocument.importNode(node.getFirstChild(), true);
        
        lastNode.getParentNode().appendChild(importNode);
        
        writeXmlFile(existingDocument, file);
    }

    /**
     * @param file
     * @return
     */
    private Node getNodeToAppend(File file) {
        XMLStreamWriter writer = null;
        DocumentBuilder newDocumentBuilder = null;
        
        try {
            newDocumentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        } catch (Exception e) {
        }

        DOMResult result = null;

        try {
            result = new DOMResult();

            result.setNode(newDocumentBuilder.newDocument());
            
            writer = getXMLStreamWriter(result);
            
            if (this.configuration instanceof IDPTypeSubsystem) {
                IDPTypeSubsystem idpConfiguration = (IDPTypeSubsystem) this.configuration;

                Map<String, String> attributes = new HashMap<String, String>();

                attributes.put(SIGN_OUTGOING_MESSAGES_ATTRIBUTE, String.valueOf(idpConfiguration.isSignOutgoingMessages()));
                attributes.put(IGNORE_INCOMING_SIGNATURES_ATTRIBUTE,
                        String.valueOf(idpConfiguration.isIgnoreIncomingSignatures()));
                
                writeValve(writer, "org.picketlink.identity.federation.bindings.tomcat.idp.IDPWebBrowserSSOValve", attributes);
            } else if (this.configuration instanceof SPTypeSubsystem) {
                SPTypeSubsystem spConfiguration = (SPTypeSubsystem) this.configuration;

                if (spConfiguration.isPostBinding()) {
                    writeValve(writer, "org.picketlink.identity.federation.bindings.tomcat.sp.SPPostFormAuthenticator", null);    
                } else {
                    writeValve(writer, "org.picketlink.identity.federation.bindings.tomcat.sp.SPRedirectFormAuthenticator", null);
                }
                
            }
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
        
        return result.getNode();
    }

    public void writeValve(XMLStreamWriter writer, String className, Map<String, String> attributes) throws ProcessingException {
        StaxUtil.writeStartElement(writer, "", VALVE_ELEMENT, "");
        
        StaxUtil.writeStartElement(writer, "", VALVE_CLASS_NAME, "");
        StaxUtil.writeCharacters(writer, className);
        StaxUtil.writeEndElement(writer);
        
        if (attributes != null) {
            for (String key : attributes.keySet()) {
                StaxUtil.writeStartElement(writer, "", VALVE_PARAM, "");
                
                StaxUtil.writeStartElement(writer, "", VALVE_PARAM_NAME, "");
                StaxUtil.writeCharacters(writer, key);
                StaxUtil.writeEndElement(writer);
                
                StaxUtil.writeStartElement(writer, "", VALVE_PARAM_VALUE, "");
                StaxUtil.writeCharacters(writer, attributes.get(key));
                StaxUtil.writeEndElement(writer);
                
                StaxUtil.writeEndElement(writer);
            }
        }

        StaxUtil.writeEndElement(writer);
    }

    public static void writeXmlFile(Node doc, File file) {
        try {
            if (file.exists()) {
                file.delete();
            }
            
            file.createNewFile();
            
            // Prepare the DOM document for writing
            Source source = new DOMSource(doc);

            Result result = new StreamResult(file);

            // Write the DOM document to the file
            Transformer xformer = TransformerFactory.newInstance().newTransformer();
            xformer.transform(source, result);
        } catch (TransformerConfigurationException e) {
        } catch (TransformerException e) {
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
