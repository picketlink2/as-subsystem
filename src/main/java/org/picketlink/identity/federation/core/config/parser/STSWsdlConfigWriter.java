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
import java.util.Iterator;

import javax.xml.namespace.NamespaceContext;
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
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.picketlink.identity.federation.core.exceptions.ProcessingException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * <p>
 * Class responsible for the creation of the context.xml configuration file.
 * </p>
 * 
 * @author <a href="mailto:psilva@redhat.com">Pedro Silva</a>
 * @since Mar 12, 2012
 */
public class STSWsdlConfigWriter implements ConfigWriter {

    private STSTypeSubsystem configuration;

    public STSWsdlConfigWriter(STSTypeSubsystem configuration) {
        this.configuration = configuration;
    }

    /**
     * Get an {@code XMLStreamWriter}
     * 
     * @param outStream
     * @return
     * @throws ProcessingException
     */
    private XMLStreamWriter getXMLStreamWriter(final Result outStream) throws ProcessingException {
        XMLOutputFactory xmlOutputFactory = XMLOutputFactory.newInstance();
        try {
            return xmlOutputFactory.createXMLStreamWriter(outStream);
        } catch (XMLStreamException e) {
            throw new ProcessingException(e);
        }
    }

    public void write(File file) {
        Document wsdl = getWSDL(file);
        
        XPathFactory factory = XPathFactory.newInstance();
        XPath xpath = factory.newXPath();
        
        xpath.setNamespaceContext(new NamespaceContext() {
            
            @Override
            public Iterator getPrefixes(String namespaceURI) {
                return null;
            }
            
            @Override
            public String getPrefix(String namespaceURI) {
                // TODO Auto-generated method stub
                return null;
            }
            
            @Override
            public String getNamespaceURI(String prefix) {
                return prefix.equals("wsdl") ? "http://schemas.xmlsoap.org/wsdl/" : "";
            }
        });
        
        try {
            XPathExpression expr = xpath.compile("//*/address");
            
            Element result = (Element) expr.evaluate(wsdl.getFirstChild().getOwnerDocument(), XPathConstants.NODE);
            
            result.getAttributeNode("location").setTextContent(this.configuration.getEndpoint());
        } catch (XPathExpressionException e) {
            e.printStackTrace();
        }
        
        NodeList elements = ((Element) wsdl.getFirstChild()).getElementsByTagName("service");
        
        for (int i = 0; i < elements.getLength(); i++) {
            Node element = elements.item(i);
            
            element.getAttributes().getNamedItem("location").setTextContent(this.configuration.getEndpoint());
        }
        
        writeXmlFile(wsdl, file);
    }

    private Document getWSDL(File file) {
        DocumentBuilder newDocumentBuilder = null;
        Document existingDocument = null;

        try {
            newDocumentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            existingDocument = newDocumentBuilder.parse(new FileInputStream(file));
        } catch (Exception e) {
        }
        
        return existingDocument;
    }

    private static void writeXmlFile(Node doc, File file) {
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
