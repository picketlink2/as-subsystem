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

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
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

import org.picketlink.identity.federation.core.config.STSConfiguration;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * <p>
 * Class responsible for the creation of the core-sts.xml/picketlink-sts.xml configuration file.
 * </p>
 * 
 * @author <a href="mailto:psilva@redhat.com">Pedro Silva</a>
 * @since Mar 12, 2012
 */
public class PicketLinkSTSConfigWriter implements ConfigWriter {

    private STSConfiguration configuration;

    public PicketLinkSTSConfigWriter(STSConfiguration configuration) {
        this.configuration = configuration;
    }

    /* (non-Javadoc)
     * @see org.picketlink.identity.federation.core.config.parser.ConfigWriter#write(java.io.File)
     */
    public void write(File file) {
        Document wsdl = getWSDL(file);
        
        writeEndpointURL(wsdl);
        
        writeXmlFile(wsdl, file);
    }

    /**
     * <p>
     * Writes the endpoint location.
     * </p>
     * 
     * @param wsdl
     */
    private void writeEndpointURL(Document wsdl) {
        XPathFactory factory = XPathFactory.newInstance();
        XPath xpath = factory.newXPath();
        
        try {
            XPathExpression expr = xpath.compile("//*/address");
            
            Element result = (Element) expr.evaluate(wsdl.getFirstChild().getOwnerDocument(), XPathConstants.NODE);
            
            result.getAttributeNode("location").setTextContent(this.configuration.getEndpoint());
        } catch (XPathExpressionException e) {
            e.printStackTrace();
            //TODO: Exception Handling
        }
    }

    /**
     * Returns an DOM representation fo the WSDL file. 
     * 
     * @param file
     * @return
     */
    private Document getWSDL(File file) {
        DocumentBuilder newDocumentBuilder = null;
        Document existingDocument = null;

        try {
            newDocumentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            existingDocument = newDocumentBuilder.parse(new FileInputStream(file));
        } catch (Exception e) {
            e.printStackTrace();
          //TODO: Exception Handling
        }
        
        return existingDocument;
    }

    /**
     * Write the changes to a new WSDL file.
     * 
     * @param doc
     * @param file
     */
    private static void writeXmlFile(Node doc, File file) {
        try {
            // always remove a file before writing changes, if it already exists
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
            e.printStackTrace();
            //TODO: Exception Handling
        } catch (TransformerException e) {
            e.printStackTrace();
          //TODO: Exception Handling
        } catch (IOException e) {
            e.printStackTrace();
          //TODO: Exception Handling
        }
    }

}
